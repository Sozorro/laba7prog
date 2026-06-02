package ru.kessi.client;

import java.util.Scanner;
import org.tinylog.Logger;

import ru.kessi.client.network.Client;

public class Main {
    public static Scanner scannerNow;
    public static void main(String[] args) {
        Logger.info("Запуск создания клиента");

        scannerNow = new Scanner(System.in);
        while (true) {
            try {
                System.out.println("----------------------");
                System.out.println("Вы сейчас находитесь в меню создания клиента. Чтобы его создать и подключить к серверу введите: new_client(new). Если хотите выйти введите: exit(e)");
                String s = scannerNow.nextLine().trim();
                if (s.equals("new_client") || s.equals("new")) {
                    Client client = new Client();
                    client.run("localhost", 1234);
                } else if (s.equals("exit") || s.equals("e")) {
                    System.exit(0);
                }
            } catch(Exception e) {
                System.exit(0);
            }
        }
    }
}
