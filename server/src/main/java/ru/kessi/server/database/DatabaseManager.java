package ru.kessi.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.TreeSet;

import org.tinylog.Logger;

import ru.kessi.common.entites.Coordinates;
import ru.kessi.common.entites.LabWork;
import ru.kessi.common.entites.Person;
import ru.kessi.common.entites.enums.Color;
import ru.kessi.common.entites.enums.Difficulty;
import ru.kessi.common.exceptions.NotEnoughRights;

public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/collection";
    private static final String USER = "kessi";
    private static final String PASSWORD = "markiz";

    public static Connection getConnectionDB() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            Logger.error(e, "Ошибка при попытке подключения к БД");
            throw e;
        }
    }

    public static void initDatabase() {
        String textForUsersTableSQL = """
            CREATE TABLE IF NOT EXISTS users (
                login VARCHAR(25) PRIMARY KEY,
                password VARCHAR(255) NOT NULL
            );
            """; 
        String textForLabWorksTableSQL = """
            CREATE TABLE IF NOT EXISTS lab_works (
                id BIGSERIAL PRIMARY KEY,
                creation_date TIMESTAMP NOT NULL,
                name VARCHAR(255) NOT NULL,
                coordinates_x DOUBLE PRECISION NOT NULL,
                coordinates_y DOUBLE PRECISION NOT NULL,
                minimal_point INTEGER NOT NULL CHECK (minimal_point > 0),
                personal_qualities_minimum INTEGER NOT NULL CHECK (personal_qualities_minimum > 0),
                description VARCHAR(3271) NOT NULL,
                difficulty VARCHAR(50) NOT NULL,
                
                -- Person (author):
                login_author VARCHAR(25) NOT NULL REFERENCES users(login),
                author_name VARCHAR(255) NOT NULL,
                author_height DOUBLE PRECISION NOT NULL CHECK (author_height > 0),
                author_weight BIGINT NOT NULL CHECK (author_weight > 0),
                author_passport_id VARCHAR(255) NOT NULL,
                author_hair_color VARCHAR(50) NOT NULL
            );
            """;

        // Используем try-with-resources, чтобы Statement и ResultSet закрылись автоматически
        try (Connection conn = getConnectionDB();
                Statement stmt = conn.createStatement()) {
            
            Logger.info("Подключение к базе данных прошло успешно");
            Logger.info("Начинаем инициализацию данных...");

            stmt.executeUpdate(textForUsersTableSQL);
            Logger.info("Таблица 'users' успешно создана или уже существует.");
           
            stmt.executeUpdate(textForLabWorksTableSQL);
            Logger.info("Таблица 'lab_works' успешно создана или уже существует.");

            // information_schema.tables - это системная таблица PostgreSQL, где хранится метаинформация
            String checkTablesSQL = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'";
            ResultSet rs = stmt.executeQuery(checkTablesSQL);
            
            Logger.info("--- Список таблиц в базе данных ---");
            while (rs.next()) {
                // rs.getString("table_name") достает значение из колонки "table_name" текущей строки
                Logger.info("Найдена таблица: {}", rs.getString("table_name"));
            }
            Logger.info("-----------------------------------");

        } catch (SQLException e) {
            Logger.error(e, "ошибка при подключении к таблице или при инициализации данных");
        }

    }

    public static boolean registerUser(String login, String password) {
        String insertSql = "INSERT INTO users (login, password) VALUES (?, ?)";
        try (Connection conn = getConnectionDB();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setString(1, login);
            stmt.setString(2, PasswordManager.getHash(password));
            stmt.executeUpdate();
            Logger.info("Пользователь '{}' успешно зарегистрирован в БД", login);
            return true;
        } catch (SQLException e) {
            Logger.warn("Не удалось зарегистрировать пользователя '{}'. Возможно, логин занят.", login);
            return false;
        }
    }

    public static boolean authenticateUser(String login, String password) {
        String sql = "SELECT password FROM users WHERE login = ?";
        try (Connection conn = getConnectionDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String passHash = rs.getString("password");
                    String chackHash = PasswordManager.getHash(password);
                    return passHash.equals(chackHash);
                }
                return false; // Пользователь не найден
            }
        } catch (SQLException e) {
            Logger.error(e, "Ошибка при аутентификации пользователя");
            return false;
        }
    }
    
    public static TreeSet<LabWork> loadCollectionToDB() {

        class idComparator implements Comparator<LabWork> {
            @Override
            public int compare(LabWork a, LabWork b) {
                return (int) (Long.valueOf(a.getId()) - Long.valueOf(b.getId()));
            }
        }
        TreeSet<LabWork> collection = new TreeSet<>(new idComparator());

        String loadLabs = """
            SELECT * FROM lab_works
        """;

        try (Connection conn = getConnectionDB();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(loadLabs);) {

            while (rs.next()) {
                long id = rs.getLong("id");
                java.util.Date creationDate = new java.util.Date(rs.getTimestamp("creation_date").getTime());
                String name = rs.getString("name");
                float x = rs.getFloat("coordinates_x");
                float y = rs.getFloat("coordinates_y");

                int minimalPoint = rs.getInt("minimal_point");
                int personalQualitiesMinimum = rs.getInt("personal_qualities_minimum");
                String description = rs.getString("description");
                Difficulty difficulty = Difficulty.valueOf(rs.getString("difficulty"));
                
                //Person (author):
                String authorName = rs.getString("author_name");
                double authorHeight = rs.getDouble("author_height");
                long authorWeight = rs.getLong("author_weight");
                String passportID = rs.getString("author_passport_id");
                Color hairColor = Color.valueOf(rs.getString("author_hair_color"));

                LabWork laba = new LabWork(id, creationDate, name, new Coordinates(x, y), minimalPoint, personalQualitiesMinimum, description, difficulty, new Person(authorName, authorHeight, authorWeight, passportID, hairColor));
                
                collection.add(laba);
            }
            Logger.info("Из базы данных успешно загружено {} объектов", collection.size());
            
        } catch (SQLException e) {
            Logger.error(e, "ошибка при извлечении данных из БД");
        }
        return collection;
    }

    public static long addLabworkToDB(String login, LabWork labWork) {
        String addLab = """
            INSERT INTO lab_works (
                creation_date, name, coordinates_x, coordinates_y, minimal_point, 
                personal_qualities_minimum, description, difficulty, 
                login_author, author_name, author_height, author_weight, author_passport_id, author_hair_color
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnectionDB();
                PreparedStatement stmt = conn.prepareStatement(addLab, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setTimestamp(1, new Timestamp(labWork.getCreationDate().getTime()));
            stmt.setString(2, labWork.getName());
            stmt.setFloat(3, labWork.getCoordinates().getX());
            stmt.setFloat(4, labWork.getCoordinates().getY());
            
            stmt.setInt(5, labWork.getMinimalPoint());
            stmt.setInt(6, labWork.getPersonalQualitiesMinimum());
            stmt.setString(7, labWork.getDescription());
            stmt.setString(8, labWork.getDifficulty().name());
            
            // Person (author):
            stmt.setString(9, login);
            stmt.setString(10, labWork.getAuthor().getName());
            stmt.setDouble(11, labWork.getAuthor().getHeight());
            stmt.setLong(12, labWork.getAuthor().getWeight());
            stmt.setString(13, labWork.getAuthor().getPassportID());
            stmt.setString(14, labWork.getAuthor().getHairColor().name());
           
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long newId = generatedKeys.getLong(1);
                    Logger.info("Объект '{}' успешно сохранен в БД. Его id={}", labWork.getName(), newId);
                    return newId;
                }
            }
        } catch (SQLException e) {
            Logger.error(e, "ошибка при попытке добавить объект в базу данных");
        }
        return -1;

    }

    public static boolean delLabworkForDB(String login, long id) throws NotEnoughRights {
        String delSql = "DELETE FROM lab_works WHERE id = ? AND login_author = ?";
        
        try (Connection conn = getConnectionDB();
            PreparedStatement stmt = conn.prepareStatement(delSql)) {
            
            stmt.setLong(1, id);
            stmt.setString(2, login);
            
            long updateStr = stmt.executeUpdate();
            
            if (updateStr > 0) {
                Logger.info("Объект с id={} успешно удален из БД пользователем {}", id, login);
                return true;
            } else {
                String checkElem = "SELECT * FROM lab_works WHERE id = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkElem)) {
                    checkStmt.setLong(1, id);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next()) {
                            throw new NotEnoughRights("Недостаточно прав для удаления объекта с id = " + id);
                        }
                    }
                }
                Logger.warn("Объект с id={} не найден в БД", id);
                return false;
            }
            
        } catch (SQLException e) {
            Logger.error(e, "Ошибка при удалении объекта из БД");
            return false;
        }
    }

    public static boolean updateLabworkToDB(String login, LabWork labWork) {
        String updateSql = """
            UPDATE lab_works SET
                creation_date = ?,
                name = ?,
                coordinates_x = ?,
                coordinates_y = ?,
                minimal_point = ?,
                personal_qualities_minimum = ?,
                description = ?,
                difficulty = ?,
                author_name = ?,
                author_height = ?,
                author_weight = ?,
                author_passport_id = ?,
                author_hair_color = ?
            WHERE id = ? AND login_author = ?
            """;
        
        try (Connection conn = getConnectionDB();
            PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            
            stmt.setTimestamp(1, new Timestamp(labWork.getCreationDate().getTime()));
            stmt.setString(2, labWork.getName());
            stmt.setFloat(3, labWork.getCoordinates().getX());
            stmt.setFloat(4, labWork.getCoordinates().getY());
            stmt.setInt(5, labWork.getMinimalPoint());
            stmt.setInt(6, labWork.getPersonalQualitiesMinimum());
            stmt.setString(7, labWork.getDescription());
            stmt.setString(8, labWork.getDifficulty().name());
            
            // Person (author):
            stmt.setString(9, labWork.getAuthor().getName());
            stmt.setDouble(10, labWork.getAuthor().getHeight());
            stmt.setLong(11, labWork.getAuthor().getWeight());
            stmt.setString(12, labWork.getAuthor().getPassportID());
            stmt.setString(13, labWork.getAuthor().getHairColor().name());

            stmt.setLong(14, labWork.getId());
            stmt.setString(15, login);
            
            long updateStr = stmt.executeUpdate();
            
            if (updateStr > 0) {
                Logger.info("Объект с id={} успешно обновлен в БД", labWork.getId());
                return true;
            } else {
                String checkElem = "SELECT * FROM lab_works WHERE id = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkElem)) {
                    checkStmt.setLong(1, labWork.getId());
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next()) {
                            throw new NotEnoughRights("Недостаточно прав для удаления объекта с id = " + labWork.getId());
                        }
                    }
                }
                Logger.warn("Объект с id={} не найден в БД", labWork.getId());
                return false;
            }
            
        } catch (SQLException e) {
            Logger.error(e, "Ошибка при обновлении объекта в БД");
            return false;
        }
    }

    public static long clearDatabase() {
        String clearSql = "DELETE FROM lab_works";
        
        try (Connection conn = getConnectionDB();
            Statement stmt = conn.createStatement()) {
            
            long updateStr = stmt.executeUpdate(clearSql);
            Logger.info("Из БД удалено {} объектов", updateStr);
            return updateStr;
            
        } catch (SQLException e) {
            Logger.error(e, "Ошибка при очистке БД");
            return -1;
        }
    }

    /* 

    public String execute(String login, CollectionManager collectionManager, Object args){
        try {
            String str = "dop_doc/collection.csv";
            
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(str), "UTF-8")) {
                TreeSet<LabWork> collection = collectionManager.getElems();
                //CsvSaver.saveCollection(collection, writer);
                String head = "name; id; date; name; coordinatesX; coordinatesY; minimalPoint; personalQualitiesMinimum; description; difficulty;name; height; weight; passportID; hairColor;intparam;stringparam";
                writer.write(head + "\n");
                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                for (LabWork laba : collection) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("add").append(";");
                    sb.append(laba.getId()).append(";");
                    sb.append(formatter.format(laba.getCreationDate())).append(";"); 
                    sb.append(laba.getName()).append(";");
                    sb.append(laba.getCoordinates().getX()).append(";");
                    sb.append(laba.getCoordinates().getY()).append(";");
                    sb.append(laba.getMinimalPoint()).append(";");
                    sb.append(laba.getPersonalQualitiesMinimum()).append(";");
                    sb.append(laba.getDescription()).append(";");
                    sb.append(laba.getDifficulty().toString()).append(";");
                    sb.append(laba.getAuthor().getName()).append(";"); 
                    sb.append(laba.getAuthor().getHeight()).append(";");
                    sb.append(laba.getAuthor().getWeight()).append(";");
                    sb.append(laba.getAuthor().getPassportID()).append(";");
                    sb.append(laba.getAuthor().getHairColor()).append(";");
                    sb.append(";");
                    sb.append("\n");
                    writer.write(sb.toString());

                }
                writer.flush();
            } catch (IOException e) {
                throw new WrongParam("Ошибка ввода");
            }
            return ("Коллекция сохранена в файл");
        } catch (Exception e) {
            Logger.info("Произошла непредвиденная ошибка. Создание элемента было остановлено и он не был добавлен в коллекцию");
            throw e;
        }
    
    }
     */


}

//

/*

docker run --name db_for_collection_laba7proga -e POSTGRES_USER=kessi -e POSTGRES_PASSWORD=markiz -e POSTGRES_DB=collection -p 5432:5432 -d postgres:16
То есть db_for_collection_laba7proga - имя контейнера и я могу создать в нём несколько разных пространств, одно из которых - collection?



Организовать хранение коллекции в реляционной СУБД (PostgresQL). Убрать хранение коллекции в файле.
Для генерации поля id использовать средства базы данных (sequence).
Организовать возможность регистрации и авторизации пользователей.
При хранении объектов сохранять информацию о пользователе, который создал этот объект.
Пользователи должны иметь возможность просмотра всех объектов коллекции, но модифицировать могут только принадлежащие им.

В качестве базы данных использовать PostgreSQL.
Для подключения к БД на кафедральном сервере использовать хост pg, имя базы данных - studs, имя пользователя/пароль совпадают с таковыми для подключения к серверу.
 
*/