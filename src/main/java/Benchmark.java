import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Benchmark {
    static final String DB_URL = "jdbc:mariadb://localhost:3306/benchmark";
    static final String USER = "root";
    static final String PASS = "root";

    static public void main(String[] args) {
        if (args.length != 1) {
            System.out.println("one parameter required");
            System.exit(0);
        }
        System.out.println(args[0]);

        System.out.println("Connecting to database...");
        try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);) {
            System.out.println("Connected!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
