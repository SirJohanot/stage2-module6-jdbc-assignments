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

    private Connection connection;
    private PreparedStatement ps = null;
    private Statement st = null;

    {
        try {
            connection = CustomDataSource.getInstance()
                    .getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String CREATE_USER_SQL = "INSERT INTO ?(firstname, lastname, age) VALUES(?, ?, ?) RETURNING id;";
    private static final String UPDATE_USER_SQL = "UPDATE ? SET firstname=?, lastname=?, age=? WHERE id=?;";
    private static final String DELETE_USER = "DELETE FROM ? WHERE id=?;";
    private static final String FIND_USER_BY_ID_SQL = "SELECT * FROM ? WHERE id=?;";
    private static final String FIND_USER_BY_NAME_SQL = "SELECT * FROM ? WHERE firstname=?;";
    private static final String FIND_ALL_USER_SQL = "SELECT * FROM ?;";

    public Long createUser(User user) {
        try (PreparedStatement preparedStatement = buildPreparedStatement(CREATE_USER_SQL, "user", user.getFirstName(), user.getLastName(), user.getAge());
             ResultSet resultSet = preparedStatement.executeQuery()) {
            return resultSet.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findUserById(Long userId) {
        try (PreparedStatement preparedStatement = buildPreparedStatement(FIND_USER_BY_ID_SQL, "user", userId);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            Long id = resultSet.getLong(1);
            String firstName = resultSet.getString(2);
            String lastName = resultSet.getString(3);
            int age = resultSet.getInt(4);

            return new User(id, firstName, lastName, age);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findUserByName(String userName) {
        try (PreparedStatement preparedStatement = buildPreparedStatement(FIND_USER_BY_NAME_SQL, "user", userName);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            Long id = resultSet.getLong(1);
            String firstName = resultSet.getString(2);
            String lastName = resultSet.getString(3);
            int age = resultSet.getInt(4);

            return new User(id, firstName, lastName, age);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> findAllUser() {
        try (PreparedStatement preparedStatement = buildPreparedStatement(FIND_ALL_USER_SQL, "user");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<User> results = new ArrayList<>();
            do {
                Long id = resultSet.getLong(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                int age = resultSet.getInt(4);
                results.add(new User(id, firstName, lastName, age));
            } while (resultSet.next());

            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User updateUser(User user) {
        try (PreparedStatement preparedStatement = buildPreparedStatement(UPDATE_USER_SQL, "user", user.getFirstName(), user.getLastName(), user.getAge());
             ResultSet resultSet = preparedStatement.executeQuery()) {

            Long id = resultSet.getLong(1);
            String firstName = resultSet.getString(2);
            String lastName = resultSet.getString(3);
            int age = resultSet.getInt(4);

            return new User(id, firstName, lastName, age);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteUser(Long userId) {
        try (PreparedStatement preparedStatement = buildPreparedStatement(DELETE_USER, "user", userId)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
