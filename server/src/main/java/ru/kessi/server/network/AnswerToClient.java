package ru.kessi.server.network;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.tinylog.Logger;

public class AnswerToClient {
    private Selector selector;
    private SelectionKey key;
    private SocketChannel clientChannel;
    private String req;
    
    public AnswerToClient(Selector selector, SelectionKey key, SocketChannel clientChannel, String req) {
        this.selector = selector;
        this.key = key;
        this.clientChannel = clientChannel;
        this.req = req;
    }
    public void answerServer() {
        try {
            byte[] serializedObject = Serialize.serializeObject(req);
            ByteBuffer buffer = ByteBuffer.allocate(4 + serializedObject.length);
            buffer.putInt(serializedObject.length);
            buffer.put(serializedObject);
            buffer.flip();
            while (buffer.hasRemaining()) {
                clientChannel.write(buffer);
            }
            Logger.info("Команда обработана");
            key.interestOps(key.interestOps() | SelectionKey.OP_READ);
            // Будим селектор, чтобы он мгновенно увидел, что ключ снова готов к чтению
            selector.wakeup();
        } catch (Exception e) {
            Logger.error(e, "Ошибка при отправке ответа клиенту");
        }
    }
}
