package dbi;

import java.sql.*;
import java.util.Random;

public class Benchmark {
    static final String DB_URL = "jdbc:mariadb://localhost:3306/benchmark?rewriteBatchedStatements=true";
    static final String USER = "root";
    static final String PASS = "root";

    static final int BATCH_SIZE = 3000;

    static final String str20 = "00000000000000000000";
    static final String str68 = "00000000000000000000000000000000000000000000000000000000000000000000";
    static final String str72 = "000000000000000000000000000000000000000000000000000000000000000000000000";

    private static int getRandomNumberInRange(int min, int max) {
        if (min == max) return min;
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static void clearTables(Connection conn) throws SQLException{
        System.out.println("Clearing tables...");
        Statement stmt = conn.createStatement();
        stmt.executeQuery("DELETE FROM tellers");
        stmt.executeQuery("DELETE FROM accounts");
        stmt.executeQuery("DELETE FROM branches");
        stmt.close();
        System.out.println("Cleared!");
    }

    public static void fillBranches(Connection conn, int n) throws SQLException{
        System.out.println("Filling branches table...");
        PreparedStatement prep = conn.prepareStatement("INSERT INTO branches (branchid, branchname, balance, address) VALUES(?, ?, 0, ?)");

        for (int i = 1; i <= n; i++) {
            prep.setString(2, str20);
            prep.setString(3, str72);
            prep.setInt(1, i);
            prep.addBatch();
            if ((i - 1) % BATCH_SIZE == 0) {
                prep.executeBatch();
            }
        }
        prep.executeBatch();
        prep.close();
        System.out.println("Branches Filled!");
    }

    public static void fillTellers(Connection conn, int n) throws SQLException{
        System.out.println("Filling tellers table...");
        PreparedStatement prep = conn.prepareStatement("INSERT INTO tellers (tellerid, tellername, balance, branchid, address) VALUES(?, ?, 0, ?, ?)");

        for (int i = 1; i <= n*10; i++) {
            prep.setString(2, str20);
            prep.setString(4, str68);
            prep.setInt(1, i);
            prep.setInt(3,  getRandomNumberInRange(1, n));
            prep.addBatch();
            if ((i - 1) % BATCH_SIZE == 0) {
                prep.executeBatch();
            }
        }
        prep.executeBatch();
        prep.close();
        System.out.println("tellers Filled!");
    }

    public static void fillAccounts(Connection conn, int n) throws SQLException {
        System.out.println("Filling accounts table...");
        PreparedStatement prep = conn.prepareStatement("INSERT INTO accounts (accid, name, balance, branchid, address) VALUES(?, ?, 0, ?, ?)");
        for (int i = 1; i <= n * 100000; i++) {
            prep.setString(2, str20); // name
            prep.setString(4, str68); // address
            prep.setInt(1, i); // accid
            prep.setInt(3, getRandomNumberInRange(1, n)); // branchid
            prep.addBatch();
            if ((i - 1) % BATCH_SIZE == 0) {
                prep.executeBatch();
            }
        }
        prep.executeBatch();
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
            fillTellers(conn, n);
            fillAccounts(conn, n);

            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;
            System.out.println("Time: " + timeElapsed/1000.0d + "s");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
