package ru.kessi.client.manegers;

import java.util.ArrayList;

public class ComHistory {
    private static ArrayList<ComExec> history = new ArrayList<>();

    private static class ComExec {
        private String name;
        private String res;

        public ComExec(String name, String res) {
            this.name = name;
            this.res = res;
        }
        public String getName() {
            return name;
        }
        public String getRes() {
            return res;
        }
    }

    public static void addCom(String name, String res) {
        history.add(new ComExec(name, res));
    }
    public static int getSize() {
        return history.size();
    }
    public static ComExec getCom(int num) {
        return history.get(num);
    }
    public static void printHistory() {
        if (history.isEmpty()) {
            System.out.println("История команд пуста");
        } else {
            for (ComExec com : history) {
                System.out.println("Команда: " + com.getName());
                System.out.println("Параметры: " + com.getRes());
                System.out.println("-----------------");
            }
        }
    }
}
