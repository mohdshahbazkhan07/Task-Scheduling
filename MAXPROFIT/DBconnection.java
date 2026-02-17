import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/maxprofit";

    private static final String USER = "postgres";
    private static final String PASSWORD = "Shahbaz@123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}