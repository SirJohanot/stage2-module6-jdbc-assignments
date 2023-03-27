package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = "INSERT INTO user(firstname, lastname, age) VALUES(?, ?, ?) RETURNING id;";
    private static final String updateUserSQL = "UPDATE user SET firstname=?, lastname=?, age=? WHERE id=?;";
    private static final String deleteUser = "DELETE FROM user WHERE id=?;";
    private static final String findUserByIdSQL = "SELECT * FROM user WHERE id=?;";
    private static final String findUserByNameSQL = "SELECT * FROM user WHERE firstname=?;";
    private static final String findAllUserSQL = "SELECT * FROM user;";

    public Long createUser() throws SQLException {
        try (PreparedStatement preparedStatement = buildPreparedStatement(createUserSQL, "first name", "last name", 3);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            return resultSet.getLong(1);
        }
    }

    public User findUserById(Long userId) throws SQLException {
        try (PreparedStatement preparedStatement = buildPreparedStatement(findUserByIdSQL, userId);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            Long id = resultSet.getLong(1);
            String firstName = resultSet.getString(2);
            String lastName = resultSet.getString(3);
            int age = resultSet.getInt(4);

            return new User(id, firstName, lastName, age);
        }
    }

    public User findUserByName(String userName) throws SQLException {
        try (PreparedStatement preparedStatement = buildPreparedStatement(findUserByNameSQL, userName);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            Long id = resultSet.getLong(1);
            String firstName = resultSet.getString(2);
            String lastName = resultSet.getString(3);
            int age = resultSet.getInt(4);

            return new User(id, firstName, lastName, age);
        }
    }

    public List<User> findAllUser() throws SQLException {
        try (ResultSet resultSet = connection.createStatement().executeQuery(findAllUserSQL)) {

            List<User> results = new ArrayList<>();
            do {
                Long id = resultSet.getLong(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                int age = resultSet.getInt(4);
                results.add(new User(id, firstName, lastName, age));
            } while (resultSet.next());

            return results;
        }
    }

    public User updateUser() {
        return null;
    }

    private void deleteUser(Long userId) throws SQLException {
        try (PreparedStatement preparedStatement = buildPreparedStatement(deleteUser, userId)) {
            preparedStatement.executeUpdate();
        }
    }

    private PreparedStatement buildPreparedStatement(String query, Object... parameters) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        for (int i = 1; i <= parameters.length; i++) {
            preparedStatement.setObject(i, parameters[i - 1]);
        }
        return preparedStatement;
    }
}
