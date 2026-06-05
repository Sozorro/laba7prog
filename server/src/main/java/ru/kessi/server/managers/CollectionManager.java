package ru.kessi.server.managers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.stream.Collectors;

import ru.kessi.common.entites.LabWork;
import ru.kessi.common.entites.Person;
import ru.kessi.common.exceptions.NotEnoughRights;
import ru.kessi.common.exceptions.WrongParam;
import ru.kessi.server.database.DatabaseManager;

public class CollectionManager {
    private TreeSet<LabWork> collection = new TreeSet<>(new idComparator());
    private java.util.Date creationDate = new java.util.Date();
    
    public void loadCollection(TreeSet<LabWork> collection) {
        this.collection = collection;
    }
    public String addLab(String login, LabWork labWork) {
        long id = DatabaseManager.addLabworkToDB(login, labWork);
        if (id != -1) {
            labWork.setId(id);
            this.collection.add(labWork);
            return "Объект добавлен в коллекцию и сохранен в БД";
        }
        return null;
    }
    public String addLabs(String login, ArrayList<LabWork> labWorks) {
        String s = "";
        for(var laba : labWorks) {
            long id = DatabaseManager.addLabworkToDB(login, laba);
            if (id != -1) {
                laba.setId(id);
                this.collection.add(laba);
                s += ("Объект " + laba.toString() + " добавлен в коллекцию и сохранен в БД \n");
            }
        }
        return s;
    }

    public String delLab(String login, long id) {
        try {
            LabWork delLaba = findElem(id);
            if(delLaba == null) {
                throw new WrongParam("Несуществующий элемент");
            }
            boolean delete = DatabaseManager.delLabworkForDB(login, delLaba.getId());
            if (delete == true) {
                collection.remove(delLaba);
                return "Объект с id " + id + " удалён из коллекции и базы данных";
            } else {
                return "Возникла ошибка при попытке удаления объекта";
            }
        } catch (NotEnoughRights e) {
            return e.getMessage();
        }
        
    }
    public String clearCollection() {
        long countElems = DatabaseManager.clearDatabase();
        if (countElems != -1) {
            collection.clear();
            return "Коллекция очищена из базы данных удалено " + countElems + " объектов";
        } else {
            return "Возникла ошибка при попытке удаления объектов";
        }
    }

    public String updateLab(String login, long id, LabWork updLaba) {
        try {
            LabWork delLaba = findElem(id);
            if(delLaba == null) {
                throw new WrongParam("Несуществующий элемент");
            }
            updLaba.setId(id);
            boolean updateElem = DatabaseManager.updateLabworkToDB(login, updLaba);
            if (updateElem != false) {
                collection.remove(delLaba);
                this.collection.add(updLaba);
                return ("Объект с id " + id + " обновлён");
            } else {
                return "Возникла ошибка при попытке изменения объекта";
            }
        } catch (NotEnoughRights e) {
            return e.getMessage();
        }
    }

    public LabWork findElem(long id) {
        return collection.stream()
            .filter(laba -> laba.getId() == id)
            .findFirst()
            .orElse(null);
    }
    public ArrayList<LabWork> findElemsHeavierPerson(Person author) {
        return collection.stream()
            .filter(laba -> laba.getAuthor().getWeight() > author.getWeight())
            .collect(Collectors.toCollection(ArrayList::new));
    }
    public ArrayList<LabWork> findElemsSubstring(String prefDescription) {
        return collection.stream()
            .filter(laba -> laba.getDescription().startsWith(prefDescription))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public TreeSet<LabWork> getElems() {
        return collection;
    }

    class idComparator implements Comparator<LabWork> {
        @Override
        public int compare(LabWork a, LabWork b) {
            return (int) (Long.valueOf(a.getId()) - Long.valueOf(b.getId()));
        }
    }

    public long getClollectionSize() {
        return collection.size();
    }

    public String toString() {
        return "Объект Collection:\nType: " + collection.getClass() + "\n" +
            "creationDate: " + creationDate + "\n" +
            "Size: " + collection.size();
    }
}
