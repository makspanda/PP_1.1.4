package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {

    private Connection conn = null;

    public UserDaoJDBCImpl() {
        //this.conn = Util.getConnection();
    }

    public void createUsersTable() {
        String createTable = """
                    CREATE TABLE IF NOT EXISTS users (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(45) NOT NULL,
                    lastName VARCHAR(45) NOT NULL,
                    age INT NOT NULL
                )""";

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTable);
            System.out.println("Таблица users успешно создана");
        } catch (SQLException e) {
            System.err.println("Ошибка при попытке создания таблицы: " + e.getMessage());
        }
    }

    public void dropUsersTable() {
        String dropTable = "DROP TABLE IF EXISTS users";

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(dropTable);
            System.out.println("Таблица users успешно удалена");
        } catch (SQLException e) {
            System.err.println("Ошибка при попытке удаления таблицы: " + e.getMessage());
        }
    }

    public void saveUser(String name, String lastName, byte age) {
        String saveUser = "INSERT INTO users (name, lastName, age) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(saveUser)) {
            pstmt.setString(1, name);
            pstmt.setString(2, lastName);
            pstmt.setInt(3, age);
            pstmt.executeUpdate();
            System.out.println("User с именем " + name + " добавлен в базу данных");
        } catch (SQLException e) {
            System.err.println("Ошибка при попытке добавления User: " + e.getMessage());
        }
    }

    public void removeUserById(long id) {
        String removeUser = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(removeUser)) {
            pstmt.setLong(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println(String.format(
                        "Пользователь с ID %d успешно удален из базы данных", id));
            } else {
                System.out.println(String.format(
                        "Пользователь с ID %d не найден в базе данных", id));
            }
        } catch (SQLException e) {
            System.err.println(String.format(
                    "Ошибка удаления пользователя с ID %d: %s", id, e.getMessage()));
        }

    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<User>();
        String selectUser = "SELECT * FROM users";

        try (PreparedStatement pstmt = conn.prepareStatement(selectUser);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setLastName(rs.getString("lastName"));
                user.setAge(rs.getByte("age"));
                users.add(user);
            }
            if (users.isEmpty()) {
                System.out.println("В базе данных не найдено ни одного пользователя");
            } else {
                System.out.printf("Успешно загружено %d пользователей из базы данных\n", users.size());
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при выводе таблицы users: " + e.getMessage());
        }
        return users;
    }

    public void cleanUsersTable() {
        String cleanUsersTable = "TRUNCATE TABLE users";

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(cleanUsersTable);
            System.out.println("Таблица users успешно очищена");
        } catch (SQLException e) {
            System.err.println("Ошибка при очистке таблицы users: " + e.getMessage());
        }
    }
}
