package ru.kessi.server.network;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.tinylog.Logger;
import org.tinylog.ThreadContext;

import ru.kessi.common.Request;
import ru.kessi.server.commandServer.SaveCommand;
import ru.kessi.server.commandServer.ExecuteScriptCommand;
import ru.kessi.server.managers.CollectionManager;
import ru.kessi.server.managers.ComParser;

public class Server {
    private final String host;
    private final int port;
    private ServerSocketChannel serverSocketChannel;
    private boolean work = false;
    private volatile boolean wait = false;
    private CollectionManager collectionManager;
    private Selector selector;
    private Map<SocketChannel, ByteArrayOutputStream> arrByteMapForClients = new HashMap<>(); //Данные для каждого клиента
    private boolean isPaused = false; 
    private Thread serverThread;
    private Thread dotsThread;

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        if (work) {
            Logger.warn("Сервер уже запущен");
            return;
        }
        work = true;
        serverThread = new Thread(() -> run());
        serverThread.start();
        Logger.info("Сервер host={} port={} запущен", host, port);
    }
    public void stop() {
        SaveCommand saveCommand = new SaveCommand();
        saveCommand.execute(collectionManager, null);
        work = false;
        if (dotsThread != null) {
            dotsThread.interrupt();
        }
        try {
            closeServer();
        } catch (Exception e) {
            Logger.error("Ошибка при закрытии сервера", e);
        }
    }
    
    public void openServer(){
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(host, port));
            serverSocketChannel.configureBlocking(false);
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            collectionManager = new CollectionManager();
            ExecuteScriptCommand executeScriptCommand = new ExecuteScriptCommand();
            executeScriptCommand.execute(collectionManager, null);

            Logger.info("Сервер host={} port={} создн и ожидает подключения", host, port);
        } catch (Exception e) {
            Logger.error("Ошибка при попытке создать сервер", e);
            stop();
        }
    }

    public void closeServer(){
        try {
            if (selector != null) selector.close();
            if (serverSocketChannel != null) serverSocketChannel.close();
            Logger.info("Сервер закрыт");
        } catch (Exception e) {
            Logger.error("Ошибка при попытке закрыть сервер", e);
        }
    }

    public void run() {
        try {
            openServer();
            dotsThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    if (wait) {
                        Logger.info("Ожидаем подключения хотя бы одного клиента...");
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break; 
                    }
                }
            });
            dotsThread.setDaemon(true);
            dotsThread.start();
            //System.out.println("Ожидаем подключения хотя бы одного клиента ...");
            while (work) {
                if (arrByteMapForClients.size() == 0) wait = true;
                else wait = false;
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        try {
                            //serverSocketChannel = (ServerSocketChannel) key.channel();
                            SocketChannel clientChannel = serverSocketChannel.accept();
                                if (clientChannel == null) {
                                    Logger.warn("null клиент при подключении");
                                    return;
                                }
                            clientChannel.configureBlocking(false);
                            clientChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                            arrByteMapForClients.put(clientChannel, new ByteArrayOutputStream());
                            Logger.info("Клиент подключён, адрес={}", clientChannel.getRemoteAddress());
                        } catch (Exception e) {
                            Logger.error("Ошибка при подключении клиента", e);
                        }
                    } else if (key.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        String requestId = UUID.randomUUID().toString().substring(0, 8);
                        ThreadContext.put("requestId", requestId);
                        try {
                            Logger.debug("Есть данные для чтения от: {}", clientChannel);
                            clientRequest(clientChannel);
                            Logger.info("Команда обработана");
                        } catch (EOFException e) {
                            if (key != null) {
                                key.cancel();
                                clientChannel.close();
                                arrByteMapForClients.remove(clientChannel);
                                Logger.info("Клиент отключился");
                            }
                        }  finally {
                            ThreadContext.clear();
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.error("Ошибка в главном цикле сервера", e);
        } finally {
            if(!work) stop();
        }
    }

    public void clientRequest(SocketChannel clientChannel) throws EOFException {
        try {
            ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
            while (lengthBuffer.hasRemaining()) {
                int c = clientChannel.read(lengthBuffer);
                if (c == -1) {
                    clientChannel.close();
                    arrByteMapForClients.remove(clientChannel);
                    //System.out.println("Клиент отключился");
                    throw new EOFException("Канал закрыт");
                } else if (c == 0) {
                    Logger.debug("Нет данных, ждём следующего события");
                    return;
                }   
            }
            lengthBuffer.flip();
            int size = lengthBuffer.getInt();
            //System.out.println("][" + size);
            //lengthBuffer.clear();

            if(size <= 0) {
                Logger.warn("Получен некорректный размер сообщения");
                return;
            }

            ByteBuffer buf = ByteBuffer.allocate(size);
            ByteArrayOutputStream arrByte = arrByteMapForClients.get(clientChannel);

            while (buf.hasRemaining()) {
                int c = clientChannel.read(buf);
                if (c == -1) {
                    clientChannel.close();
                    arrByteMapForClients.remove(clientChannel);
                    //System.out.println("Клиент отключился");
                    throw new EOFException("Канал закрыт");
                }
            }
            
            buf.flip();
            byte[] bytes = new byte[buf.remaining()];
            buf.get(bytes);
            //buf.clear();
            //arrByte.write(bytes);
            Request req = Serialize.tryDeserialize(bytes);
            //Logger.debug("запрос: {} ---- {}", req, req.getCommand().getName());
            
            if (req == null) {
                Logger.debug("Получен null-запрос");
                answerServer(clientChannel, "получен нулевой запрос или произошла ошибка при его получении");
                return;
            }
            Logger.debug("Получен объект: {}", req);
            if (req.getCommand().getName().equals("exit")) {
                Logger.info("Клиент запросил разрыв соединения (команда 'exit')");
                work = false;
                return;
                //clientChannel.close();
                //throw new EOFException("Канал закрыт по команде клиента");
            }
            if (req.getCommand().getName().equals("stop")) {
                Logger.info("Клиент запросил завершение сервера (команда 'stop')");
                work = false;
                return;
            }

            buf.clear();

            ComParser comParser = new ComParser(collectionManager);
            String res;
            if(req.getArgs() != null) {
                res = comParser.interpret(req.getCommand(), req.getArgs());
            } else if(req.getLabWork() != null) {
                res = comParser.interpret(req.getCommand(), req.getLabWork());
            } else {
                res = comParser.interpret(req.getCommand(), null);
            }
            answerServer(clientChannel, res);
            Logger.debug("Ответ отправлен клиенту: {}", res);

        } catch (EOFException e) {
            throw e;
        } catch (Exception e) {
            Logger.error("Ошибка при попытке получить или обработать запрос от клиента", e);
        }
    }
    public void answerServer(SocketChannel clientChannel, String res) {
        try {
            byte[] serializedObject = Serialize.serializeObject(res);
            ByteBuffer buffer = ByteBuffer.allocate(4 + serializedObject.length);
            buffer.putInt(serializedObject.length);
            buffer.put(serializedObject);
            buffer.flip();
            while (buffer.hasRemaining()) {
                clientChannel.write(buffer);
            }
        } catch (Exception e) {
            Logger.error("Ошибка при отправке ответа клиенту", e);
        }
    }
}



/*
 * Необходимо выполнить следующие требования:

 * Объекты между клиентом и сервером должны передаваться в сериализованном виде.
 * Обмен данными между клиентом и сервером должен осуществляться по протоколу TCP
 * Для обмена данными на сервере необходимо использовать сетевой канал
 * Сетевые каналы должны использоваться в неблокирующем режиме.
 * 
 * Серверное приложение должно состоять из следующих модулей (реализованных в
 * виде одного или нескольких классов):
 * Модуль приёма подключений.
 * Модуль чтения запроса.
 * Модуль обработки полученных команд.
 * Модуль отправки ответов клиенту.
 * Сервер должен работать в однопоточном режиме.
 * 
 * Обязанности серверного приложения:
 * 
 * Работа с файлом, хранящим коллекцию.
 * Управление коллекцией объектов.
 * Назначение автоматически генерируемых полей объектов в коллекции.
 * Ожидание подключений и запросов от клиента.
 * Обработка полученных запросов (команд).
 * Сохранение коллекции в файл при завершении работы приложения.
 * Сохранение коллекции в файл при исполнении специальной команды, доступной
 * только серверу (клиент такую команду отправить не может).
 * 
 * Объекты в коллекции, передаваемой клиенту, должны быть отсортированы по
 * названию
 * 
 */