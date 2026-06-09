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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.tinylog.Logger;

import ru.kessi.common.Request;
import ru.kessi.server.database.DatabaseManager;
import ru.kessi.server.managers.CollectionManager;

public class Server {
    private final String host;
    private final int port;
    private ServerSocketChannel serverSocketChannel;
    public volatile static boolean work = false;
    private volatile boolean wait = false;
    private CollectionManager collectionManager;
    private Selector selector;
    private Map<SocketChannel, ByteArrayOutputStream> arrByteMapForClients = new HashMap<>(); //Данные для каждого клиента
    private boolean isPaused = false; 
    private Thread serverThread;
    private Thread dotsThread;
    private static Lock lock = new ReentrantLock();

    //private final Queue<Runnable> queueForFixedPool = new ConcurrentLinkedQueue<>();
    private static ExecutorService fixedReadPool = Executors.newFixedThreadPool(10);
    private static ExecutorService cachedHandlePool = Executors.newCachedThreadPool(); 

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
        work = false;
        if (dotsThread != null) {
            dotsThread.interrupt();
        }
        try {
            closeServer();
        } catch (Exception e) {
            Logger.error(e, "Ошибка при закрытии сервера");
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
            DatabaseManager.initDatabase();

            collectionManager.loadCollection(DatabaseManager.loadCollectionToDB());
            
            Logger.info("Коллекция успешно загружена из базы данных в память.");

            Logger.info("Сервер host={} port={} создн и ожидает подключения", host, port);


        } catch (Exception e) {
            Logger.error(e, "Ошибка при попытке создать сервер");
            stop();
        }
    }

    public void closeServer(){
        try {
            if (selector != null) selector.close();
            if (serverSocketChannel != null) serverSocketChannel.close();
            Logger.info("Сервер закрыт");
        } catch (Exception e) {
            Logger.error(e, "Ошибка при попытке закрыть сервер");
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
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break; 
                    }
                }
            });
            dotsThread.setDaemon(true);
            dotsThread.start();
            //Logger.info("Ожидаем подключения хотя бы одного клиента ...");
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
                            SocketChannel clientChannel = serverSocketChannel.accept();
                            if (clientChannel == null) {
                                Logger.warn("null клиент при подключении");
                                return;
                            }
                            clientChannel.configureBlocking(false); 
                            clientChannel.register(selector, SelectionKey.OP_READ);
                            arrByteMapForClients.put(clientChannel, new ByteArrayOutputStream());
                            Logger.info("Клиент подключён, адрес={}", clientChannel.getRemoteAddress());
                        } catch (Exception e) {
                            Logger.error(e, "Ошибка при подключении клиента");
                        }
                    } else if (key.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);

                        //String requestId = UUID.randomUUID().toString().substring(0, 8);
                        //ThreadContext.put("requestId", requestId);

                        fixedReadPool.submit(() -> {
                            //try {
                                readCommand(selector, key, clientChannel, collectionManager, cachedHandlePool);
                            //}
                            /*  catch (EOFException e) {
                                if (key != null) {
                                    key.cancel();
                                    clientChannel.close();
                                    arrByteMapForClients.remove(clientChannel);
                                    Logger.info("Клиент отключился");
                                }
                            } 
                           catch (Exception e) {
                                //key.cancel();
                            }
                            finally {
                                if (key.isValid()) {
                                    key.interestOps(key.interestOps() | SelectionKey.OP_READ);
                                }
                                selector.wakeup(); 
                                ThreadContext.clear();
                            } */
                        });
                    }
                }
            }
        } catch (Exception e) {
            Logger.error(e, "Ошибка в главном цикле сервера");
        } finally {
            if(!work) stop();
        }
    }

    public void readCommand(Selector selector, SelectionKey key, SocketChannel clientChannel, CollectionManager collectionManager, ExecutorService nextPool) {
        if (!key.isValid()) return;
        try {
        //(ByteArrayOutputStream byteMapForClients = new ByteArrayOutputStream();){
            ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
            while (lengthBuffer.hasRemaining()) {
                int c = clientChannel.read(lengthBuffer);
                if (c == -1) {
                    clientChannel.close();
                    key.cancel();
                    arrByteMapForClients.remove(clientChannel);
                    throw new EOFException("Канал клиента закрыт");
                } else if (c == 0) {
                    Logger.debug("Ждём данных");
                    Thread.sleep(100); 
                    continue;
                }   
            }
            lengthBuffer.flip();
            int size = lengthBuffer.getInt();
            if(size <= 0) {
                Logger.warn("Получен некорректный размер сообщения");
                return;
            }

            ByteBuffer buf = ByteBuffer.allocate(size);

            while (buf.hasRemaining()) {
                int c = clientChannel.read(buf);
                if (c == -1) {
                    clientChannel.close();
                    key.cancel();
                    arrByteMapForClients.remove(clientChannel);
                    throw new EOFException("Канал закрыт");
                } else if (c == 0) {
                    Logger.debug("Читаем данные");
                    Thread.sleep(100); 
                    continue;
                }   
            }
            
            buf.flip();
            byte[] bytes = new byte[buf.remaining()];
            buf.get(bytes);
            
            Request req = Serialize.tryDeserialize(bytes);

            Logger.info("Команда прочитана");

            HandlerClients handlerClients = new HandlerClients(selector, key, clientChannel, collectionManager, req);
            cachedHandlePool.submit(() -> {handlerClients.handle();});
            
        } catch (Exception e) {
            Logger.error(e, "Ошибка при попытке получить запрос от клиента");
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