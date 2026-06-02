package ru.kessi.server.managers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.stream.Collectors;

import ru.kessi.common.entites.LabWork;
import ru.kessi.common.entites.Person;
import ru.kessi.common.exceptions.WrongParam;

public class CollectionManager {
    private TreeSet<LabWork> labwork = new TreeSet<>(new idComparator());
    private java.util.Date creationDate = new java.util.Date();
    private long idCounter = 1;
    
    public String addLab(LabWork labWork) {
        labWork.setId(Long.valueOf(idCounter));
        idCounter++;
        this.labwork.add(labWork);
        return "Объект добавлен";
    }
    public void addLabs(ArrayList<LabWork> labWorks) {
        for(var laba : labWorks) {
            laba.setId(Long.valueOf(idCounter));
            idCounter++;
            this.labwork.add(laba);
        }
    }

    public String delLab(long id) {
        LabWork delLaba = findElem(id);
        if(delLaba == null) {
            throw new WrongParam("Несуществующий элемент");
        }
        Iterator<LabWork> iterator = labwork.tailSet(delLaba, false).iterator();
        labwork.remove(delLaba);
        while (iterator.hasNext()) {
            LabWork laba = iterator.next();
            labwork.remove(laba);
            laba.setId(laba.getId() - 1);
            labwork.add(laba);
        }
        idCounter--;
        return ("Объект с id " + id + " удалён");
    }
    public long delLabs() {
        long i = idCounter - 1;
        labwork.clear();
        idCounter = 1;
        return i;
    }

    public String updateLab(long id, LabWork updLaba) {
        LabWork delLaba = findElem(id);
        if(delLaba == null) {
            throw new WrongParam("Несуществующий элемент");
        }
        labwork.remove(delLaba);
        updLaba.setId(id);
        this.labwork.add(updLaba);
        return ("Объект с id " + id + " обновлён");
    }

    //Изменен с использованием Stream API
    public LabWork findElem(long id) {
        return labwork.stream()
            .filter(laba -> laba.getId() == id)
            .findFirst()
            .orElse(null);
    }
    //Изменен с использованием Stream API
    public ArrayList<LabWork> findElemsHeavierPerson(Person author) {
        return labwork.stream()
            .filter(laba -> laba.getAuthor().getWeight() > author.getWeight())
            .collect(Collectors.toCollection(ArrayList::new));
    }
    //Изменен с использованием Stream API
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
    /*
    Должно быть:
    сортировка по умолчанию
    Коллекция типа java.util.TreeSet
    При запуске приложения коллекция должна автоматически заполняться значениями из файла.
    */
}
