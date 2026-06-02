package ru.kessi.server.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ru.kessi.common.Request;

public class Serialize {
    public static Request tryDeserialize(byte[] bytes) throws Exception {
        try {
            ObjectInputStream obj = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return (Request) obj.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка при десериализации");
            throw e;
        }
    }

    public static byte[] serializeObject(Object obj) throws Exception {
        try {
            ByteArrayOutputStream byteObj = new ByteArrayOutputStream();
            ObjectOutputStream objFin = new ObjectOutputStream(byteObj);
            objFin.writeObject(obj);
            objFin.flush();
            return byteObj.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка при сериализации");
            throw e;
        }
    }
}
