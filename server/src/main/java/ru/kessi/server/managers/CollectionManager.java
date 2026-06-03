package ru.kessi.server.managers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.stream.Collectors;

import ru.kessi.common.entites.LabWork;
import ru.kessi.common.entites.Person;
import ru.kessi.common.exceptions.WrongParam;
import ru.kessi.server.database.DatabaseManager;

public class CollectionManager {
    private TreeSet<LabWork> labwork = new TreeSet<>(new idComparator());
    private java.util.Date creationDate = new java.util.Date();
    private long idCounter = 1;
    
    public void loadCollection(TreeSet<LabWork> labwork) {
        this.labwork = labwork;
    }
    public String addLab(LabWork labWork) {
        long id = DatabaseManager.addLabworkToDB(labWork);
        if (id != -1) {
            labWork.setId(id);
        }
        this.labwork.add(labWork);
        return "Объект добавлен в коллекцию и сохранен в БД";
    }
    public String addLabs(ArrayList<LabWork> labWorks) {
        String s = "";
        for(var laba : labWorks) {
            long id = DatabaseManager.addLabworkToDB(laba);
            if (id != -1) {
                laba.setId(id);
            }
            this.labwork.add(laba);
            s += ("Объект " + laba.toString() + " добавлен в коллекцию и сохранен в БД \n");
        }
        return s;
    }

    public String delLab(long id) {
        LabWork delLaba = findElem(id);
        if(delLaba == null) {
            throw new WrongParam("Несуществующий элемент");
        }
        boolean delete = DatabaseManager.delLabworkForDB(delLaba.getId());
        if (delete == true) {
            labwork.remove(delLaba);
            return ("Объект с id " + id + " удалён из коллекции и базы данных");
        } else {
            return "Возникла ошибка при попытке удаления объекта";
        }
    }
    public String clearCollection() {
        long countElems = DatabaseManager.clearDatabase();
        if (countElems != -1) {
            labwork.clear();
            return "Коллекция очищена из базы данных удалено " + countElems + " объектов";
        } else {
            return "Возникла ошибка при попытке удаления объектов";
        }
    }

    public String updateLab(long id, LabWork updLaba) {
        LabWork delLaba = findElem(id);
        if(delLaba == null) {
            throw new WrongParam("Несуществующий элемент");
        }
        updLaba.setId(id);
        boolean updateElem = DatabaseManager.updateLabworkToDB(updLaba);
        if (updateElem != false) {
            labwork.remove(delLaba);
            this.labwork.add(updLaba);
            return ("Объект с id " + id + " обновлён");
        } else {
            return "Возникла ошибка при попытке изменения объекта";
        }
    }

    public LabWork findElem(long id) {
        return labwork.stream()
            .filter(laba -> laba.getId() == id)
            .findFirst()
            .orElse(null);
    }
    public ArrayList<LabWork> findElemsHeavierPerson(Person author) {
        return labwork.stream()
            .filter(laba -> laba.getAuthor().getWeight() > author.getWeight())
            .collect(Collectors.toCollection(ArrayList::new));
    }
    public ArrayList<LabWork> findElemsSubstring(String prefDescription) {
        return labwork.stream()
            .filter(laba -> laba.getDescription().startsWith(prefDescription))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public TreeSet<LabWork> getElems() {
        return labwork;
    }

    class idComparator implements Comparator<LabWork> {
        @Override
        public int compare(LabWork a, LabWork b) {
            return (int) (Long.valueOf(a.getId()) - Long.valueOf(b.getId()));
        }
    }

    public long getIdCounter() {
        return idCounter;
    }

    public String toString() {
        return "Объект Collection:\nType: " + labwork.getClass() + "\n" +
            "creationDate: " + creationDate + "\n" +
            "Size: " + labwork.size();
    }
}
