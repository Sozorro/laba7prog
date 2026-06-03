package ru.kessi.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.tinylog.Logger;

public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/collection";
    private static final String USER = "kessi";
    private static final String PASSWORD = "markiz";

    public static Connection getConnectionDB() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            Logger.error("Ошибка при попытке подключения к БД", e);
            throw e;
        }
    }

    /*public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                Logger.info("Подключение к базе данных закрыто");
            }
        } catch (SQLException e) {
            Logger.error("Ошибка при закрытии подключения к БД", e);
        }
    }*/

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
                name VARCHAR(255) NOT NULL,
                coordinates_x DOUBLE PRECISION NOT NULL,
                coordinates_y DOUBLE PRECISION NOT NULL,
                creation_date TIMESTAMP NOT NULL,
                minimal_point INTEGER NOT NULL CHECK (minimal_point > 0),
                personal_qualities_minimum INTEGER NOT NULL CHECK (personal_qualities_minimum > 0),
                description VARCHAR(3271) NOT NULL,
                difficulty VARCHAR(50),
                login_author VARCHAR(50) NOT NULL REFERENCES users(login),
                
                -- Person (author):
                author_name VARCHAR(255) NOT NULL,
                author_height DOUBLE PRECISION NOT NULL CHECK (author_height > 0),
                author_weight BIGINT NOT NULL CHECK (author_weight > 0),
                author_passport_id VARCHAR(255),
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
            Logger.error("ошибка при подключении к таблице или при инициализации данных", e);
        }
    }
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