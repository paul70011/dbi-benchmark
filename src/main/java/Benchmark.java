import java.sql.*;

public class Benchmark {
    static final String DB_URL = "jdbc:mariadb://localhost:3306/benchmark";
    static final String USER = "root";
    static final String PASS = "root";

    public static void clearTables(Connection conn) throws SQLException{
        System.out.println("Clearing tables...");
        Statement stmt = conn.createStatement();
        stmt.executeQuery("DELETE FROM branches");
        stmt.close();
        System.out.println("Cleared!");
    }

    public static void fillBranches(Connection conn, int n) throws SQLException{
        System.out.println("Filling branches table...");
        String branchName = "00000000000000000000";
        String address = "000000000000000000000000000000000000000000000000000000000000000000000000";
        PreparedStatement prep = conn.prepareStatement("INSERT INTO branches (branchid, branchname, balance, address) VALUES(?, ?, 0, ?)");
        prep.setString(2, branchName);
        prep.setString(3, address);

        for (int i = 1; i <= n*10000; i++) {
            prep.setInt(1, i);
            prep.execute();
        }
        prep.close();
        System.out.println("Filled!");
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("one parameter required");
            System.exit(0);
        }
        int n = Integer.parseInt(args[0]);
        System.out.println("n: " + n);

        System.out.println("Connecting to database...");
        try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);) {
            System.out.println("Connected!");
            clearTables(conn);
            long start = System.currentTimeMillis();
            fillBranches(conn, n);
            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;
            System.out.println("Time: " + timeElapsed/1000.0d + "s");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
