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

    private static final String ID_COLUMN = "id";
    private static final String FIRST_NAME_COLUMN = "firstname";
    private static final String LAST_NAME_COLUMN = "lastname";
    private static final String AGE_COLUMN = "age";

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

    private static final String CREATE_USER_SQL = "INSERT INTO myusers(firstname, lastname, age) VALUES(?, ?, ?);";
    private static final String UPDATE_USER_SQL = "UPDATE myusers SET firstname=?, lastname=?, age=? WHERE myusers.id=?;";
    private static final String DELETE_USER = "DELETE FROM myusers WHERE myusers.id=?;";
    private static final String FIND_USER_BY_ID_SQL = "SELECT * FROM myusers WHERE myusers.id=?;";
    private static final String FIND_USER_BY_NAME_SQL = "SELECT * FROM myusers WHERE myusers.firstname=?;";
    private static final String FIND_ALL_USER_SQL = "SELECT * FROM myusers;";

    public Long createUser(User user) {
        try (PreparedStatement preparedStatement = buildPreparedStatement(CREATE_USER_SQL, user.getFirstName(), user.getLastName(), user.getAge())) {
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                resultSet.next();
                return resultSet.getLong(ID_COLUMN);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findUserById(Long userId) {
        try (PreparedStatement preparedStatement = buildPreparedStatement(FIND_USER_BY_ID_SQL, userId);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (!resultSet.next()) {
                return null;
            }

            Long id = resultSet.getLong(ID_COLUMN);
            String firstName = resultSet.getString(FIRST_NAME_COLUMN);
            String lastName = resultSet.getString(LAST_NAME_COLUMN);
            int age = resultSet.getInt(AGE_COLUMN);

            return new User(id, firstName, lastName, age);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findUserByName(String userName) {
        try (PreparedStatement preparedStatement = buildPreparedStatement(FIND_USER_BY_NAME_SQL, userName);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (!resultSet.next()) {
                return null;
            }

            Long id = resultSet.getLong(ID_COLUMN);
            String firstName = resultSet.getString(FIRST_NAME_COLUMN);
            String lastName = resultSet.getString(LAST_NAME_COLUMN);
            int age = resultSet.getInt(AGE_COLUMN);

            return new User(id, firstName, lastName, age);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> findAllUser() {
        try (ResultSet resultSet = connection.createStatement().executeQuery(FIND_ALL_USER_SQL)) {
            List<User> results = new ArrayList<>();
            while (resultSet.next()) {
                Long id = resultSet.getLong(ID_COLUMN);
                String firstName = resultSet.getString(FIRST_NAME_COLUMN);
                String lastName = resultSet.getString(LAST_NAME_COLUMN);
                int age = resultSet.getInt(AGE_COLUMN);
                results.add(new User(id, firstName, lastName, age));
            }

            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User updateUser(User user) {
        try (PreparedStatement preparedStatement = buildPreparedStatement(UPDATE_USER_SQL, user.getFirstName(), user.getLastName(), user.getAge())) {
            preparedStatement.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteUser(Long userId) {
        try (PreparedStatement preparedStatement = buildPreparedStatement(DELETE_USER, userId)) {
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
