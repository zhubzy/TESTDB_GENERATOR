import java.io.FileInputStream;
import java.sql.*;
import java.util.Scanner;

/**
 * Usage:
 * call TestDB.initalize_test_db before making your influence table &&
 * call TestDB.validate_db(connection) to verify influence table
 * modify SQL_generator.py as needed for new test cases
 */
public class TestDB {
    private static void cleanTable(Connection conn) throws SQLException {
        try {
            System.out.println("---------------Cleaning table---------------");

            conn.createStatement().executeUpdate("DELETE FROM influence");
        } catch (Exception e) {
            System.out.println("influence tables does not exist, skipping");
        }
        conn.createStatement().executeUpdate("DELETE FROM influence_ans");
        conn.createStatement().executeUpdate("DELETE FROM transfer");
        conn.createStatement().executeUpdate("DELETE FROM depositor");
        conn.createStatement().executeUpdate("DELETE FROM account");
        conn.createStatement().executeUpdate("DELETE FROM customer");
        System.out.println("Done!");

    }

    /**
     * Tests if influence table matches with influence_test completely
     * Make sure to fill your influence table first!
     * 
     * @param conn the postgres connection object
     * @return returns true if test passes
     */
    public static boolean validate_db(Connection conn) {
        System.out.println("---------------Validating table---------------");

        try (ResultSet res = conn.createStatement()
                .executeQuery("select * from influence_ans ia EXCEPT select * from influence i")) {
            while (res.next()) {
                String c1 = res.getString("who");
                String c2 = res.getString("whom");
                System.out.println(
                        "WRONG OUTPUT!!!!!!!!! (" + c1 + "," + c2 + ") is not in the influence table as expected");
                return false;
            }
            ResultSet res2 = conn.createStatement()
                    .executeQuery("select * from influence ia EXCEPT select * from influence_ans i");
            while (res2.next()) {
                String c1 = res.getString("who");
                String c2 = res.getString("whom");
                System.out.println("WRONG OUTPUT!!!!!! (" + c1 + "," + c2 + ") should not be in the influence table");
                return false;

            }
        } catch (SQLException e) {
            System.out.println(
                    "Cannot test db, either you didn't call initalize_testDB or you didn't make influence table");
            e.printStackTrace();
        }
        System.out.println("!!!!!CORRECT!!!!!");

        return true;

    }

    /**
     * Initalizes the db with data specified in SQL_generator.py
     * Puts the correct transitive closure into a table called influence_test table
     * Make sure to run SQL_generator.py first!
     * 
     * @param conn the postgres connection object
     */
    public static void initalize_test_db(Connection conn) {
        try {
            create_relations(conn);
        } catch (SQLException e) {
            System.out.println("Tables already exists, skipping");
        }

        try {
            cleanTable(conn);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        FileInputStream fis;
        try {
            fis = new FileInputStream("queries.txt");
            Scanner sc = new Scanner(fis); // file to be scanned
            // returns true if there is another line to read
            System.out.println("---------------Inserting Test Databse---------------");

            while (sc.hasNextLine()) {
                String query = sc.nextLine();
                conn.createStatement().executeUpdate(query);
                System.out.println("Excuted Query-" + query + " succesfully");

            }
            sc.close(); // closes the scanner
        } catch (Exception e) {
            System.out.println("Fail to create test database from file, make sure to run SQL_generator.py first!!!!");
        }

        System.out.println("---------------Done init db---------------");

    }

    private static void create_relations(Connection conn) throws SQLException {
        // ResultSet rs = conn.createStatement().executeQuery("DROP TABLE customer");
        System.out.println("---------------Verifying table---------------");

        try {
            conn.createStatement().executeUpdate("""
                    CREATE TABLE customer(
                        name varchar(30) not null PRIMARY KEY,
                        credit integer
                    )
                    """);
            conn.createStatement().executeUpdate("""
                    CREATE TABLE account(
                        no varchar(30) not null PRIMARY KEY,
                        balance float
                    )
                    """);
            conn.createStatement().executeUpdate("""
                    CREATE TABLE depositor(
                        cname varchar(30) REFERENCES customer(name),
                        ano varchar(30) references account(no)
                    )
                    """);
            conn.createStatement().executeUpdate("""
                       CREATE TABLE transfer (
                           src varchar(30) references account(no),
                           tgt varchar(30) references account(no),
                           timestamp date,
                           amount float
                       );
                    """);
        } catch (Exception e) {
            System.out.println("customer, account, deposit, transfer tables already exist");
        }

        conn.createStatement().executeUpdate("""
                CREATE TABLE influence_ans(
                    who varchar(30) references customer(name),
                    whom varchar(30) references customer(name)
                )
                """);

        System.out.println("---------------Done---------------");
    }
}
