package jdbc;

import lombok.Getter;
import lombok.Setter;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

@Getter
@Setter
public class CustomDataSource implements DataSource {

    private static final String CONNECTION_PROPERTIES_FILE_NAME = "app.properties";
    private static final String DRIVER_CLASS = "postgres.driver";
    private static final String CONNECTION_URL = "postgres.url";
    private static final String USERNAME = "postgres.name";
    private static final String DB_PASSWORD = "postgres.password";

    private static volatile CustomDataSource instance;
    private final String driver;
    private final String url;
    private final String name;
    private final String password;

    private final CustomConnector connector = new CustomConnector();

    private CustomDataSource(String driver, String url, String password, String name) {
        this.driver = driver;
        this.url = url;
        this.name = name;
        this.password = password;
    }

    public static CustomDataSource getInstance() {
        if (instance == null) {
            try {
                instance = createSourceFromFile();
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    private static CustomDataSource createSourceFromFile() throws ClassNotFoundException, IOException {
        Properties properties = new Properties();
        InputStream inputStream = CustomDataSource.class.getClassLoader().getResourceAsStream(CONNECTION_PROPERTIES_FILE_NAME);
        properties.load(inputStream);

        String driver = properties.getProperty(DRIVER_CLASS);
        String url = properties.getProperty(CONNECTION_URL);
        String password = properties.getProperty(DB_PASSWORD);
        String name = properties.getProperty(USERNAME);

        Class.forName(driver);

        return new CustomDataSource(driver, url, password, name);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connector.getConnection(url, name, password);
    }

    @Override
    public Connection getConnection(String s, String s1) throws SQLException {
        return connector.getConnection(url, s, s1);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter printWriter) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLoginTimeout(int i) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return false;
    }
}
