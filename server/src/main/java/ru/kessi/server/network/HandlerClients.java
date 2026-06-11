package ru.kessi.server.network;

import java.io.EOFException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.tinylog.Logger;

import ru.kessi.common.Request;
import ru.kessi.server.database.DatabaseManager;
import ru.kessi.server.managers.CollectionManager;
import ru.kessi.server.managers.ComParser;

public class HandlerClients {
    private Selector selector;
    private SelectionKey key;
    private SocketChannel clientChannel;
    private CollectionManager collectionManager;
    private Request req;
    
    public HandlerClients(Selector selector, SelectionKey key, SocketChannel clientChannel, CollectionManager collectionManager, Request req) {
        this.selector = selector;
        this.clientChannel = clientChannel;
        this.collectionManager = collectionManager;
        this.req = req;
        this.key = key;
    }

    public void handle() {
        String reqForClient;
        try {
            
            if (req == null) {
                Logger.debug("Получен null-запрос");
                reqForClient = "получен нулевой запрос или произошла ошибка при его получении";
                AnswerToClient answerToClient = new AnswerToClient(selector, key, clientChannel, reqForClient);
                new Thread(() -> answerToClient.answerServer()).start();
                return;
            }
            
            //Logger.debug("Получен объект: {}", req);
            if (req.getCommand().getName().equals("exit")) {
                Logger.info("Клиент запросил разрыв соединения (команда 'exit')");
                reqForClient = "команда закрытия сервера";
                Server.work = false;
                return;
            }
            if (req.getCommand().getName().equals("authentication")) {
                Logger.info("Получен запрос на вход от пользователя: login='{}', password='{}'", req.getLogin(), req.getPassword());
                boolean isSuccess = DatabaseManager.authenticateUser(req.getLogin(), req.getPassword());
                if (isSuccess) {
                    reqForClient = "auth correct";
                } else {
                    reqForClient = "auth fail";
                }
                AnswerToClient answerToClient = new AnswerToClient(selector, key, clientChannel, reqForClient);
                new Thread(() -> answerToClient.answerServer()).start();
                return;
            }
            if (req.getCommand().getName().equals("registrationNewUser")) {
                Logger.info("Получен запрос на регистрацию нового пользователя: login='{}', password='{}'", req.getLogin(), req.getPassword());
                boolean isSuccess = DatabaseManager.registerUser(req.getLogin(), req.getPassword());
                if (isSuccess) {
                    reqForClient = "reg correct";
                } else {
                    reqForClient = "reg fail";
                }
                AnswerToClient answerToClient = new AnswerToClient(selector, key, clientChannel, reqForClient);
                new Thread(() -> answerToClient.answerServer()).start();
                return;
            }

            ComParser comParser = new ComParser(collectionManager);
            if(req.getArgs() != null) {
                reqForClient = comParser.interpret(req.getLogin(), req.getCommand(), req.getArgs());
            } else if(req.getLabWork() != null) {
                reqForClient = comParser.interpret(req.getLogin(), req.getCommand(), req.getLabWork());
            } else {
                reqForClient = comParser.interpret(req.getLogin(), req.getCommand(), null);
            }
            AnswerToClient answerToClient = new AnswerToClient(selector, key, clientChannel, reqForClient);
            new Thread(() -> answerToClient.answerServer()).start();
            Logger.info("Команда выполнена");
        } catch (Exception e) {
            Logger.error(e, "Ошибка при попытке обработать запрос от клиента");
        }
    }
    
    
    /* 
    public void readCommand() {
        try(ByteArrayOutputStream byteMapForClients = new ByteArrayOutputStream();){
            ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
            while (lengthBuffer.hasRemaining()) {
                int c = clientChannel.read(lengthBuffer);
                if (c == -1) {
                    clientChannel.close();
                    throw new EOFException("Канал закрыт");
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
            
            handle(req);

        } catch (Exception e) {
            Logger.error(e, "Ошибка при попытке получить запрос от клиента");
        }
    }
         */
}
