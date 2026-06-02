package ru.kessi.common.commandManager;

import java.io.Serializable;

public abstract class Command implements Serializable {
    protected String name;
    protected String description;

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
}
/*
Нужные команды:
help : вывести справку по доступным командам
info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)
show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении
add {element} : добавить новый элемент в коллекцию
update id {element} : обновить значение элемента коллекции, id которого равен заданному
remove_by_id id : удалить элемент из коллекции по его id
clear : очистить коллекцию
save : сохранить коллекцию в файл
execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.
exit : завершить программу (без сохранения в файл)
add_if_max {element} : добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции
remove_greater {element} : удалить из коллекции все элементы, превышающие заданный
history : вывести последние 7 команд (без их аргументов)
group_counting_by_coordinates : сгруппировать элементы коллекции по значению поля coordinates, вывести количество элементов в каждой группе
count_greater_than_author author : вывести количество элементов, значение поля author которых больше заданного
filter_starts_with_description description : вывести элементы, значение поля description которых начинается с заданной подстроки
*/
