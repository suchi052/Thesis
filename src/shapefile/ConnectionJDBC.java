package shapefile;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Suchi
 */
import java.awt.List;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionJDBC {

    String url = "jdbc:oracle:thin:@localhost:1521:sysdba";
    Statement stmt;
    Connection con;

    public void openConnection() {
        try { // invoke the oracle thin driver; register it with DriverManager
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
        } // this step 'loads' the drivers for Oracle that are 'found'
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            System.out.println("MR.UnitSitQueries.constructor.Exception: " + e);
        }
        try {
            con = DriverManager.getConnection(url, "scott", "P0l0club"); // establish
            // con = DriverManager.getConnection(url,aa[0],aa[1]); // establish
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            con.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void getColumnDataTye(String query) {
        query = "SELECT * FROM states";
        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(query);// create result object
            ResultSetMetaData md = rs.getMetaData();
            // rs.getColumnType(1);
            //rs.getColumnLabel(1);
            //rs.getColumnDisplaySize(1);
        } catch (SQLException e) {
            System.out.println("SQL statement is not executed!");
        }
    }

    public List getAttributes() {
        List columnNames = new List();
        String query = "SELECT * FROM states";
        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(query);// create result object
            ResultSetMetaData md = rs.getMetaData();
            int col = md.getColumnCount();
            System.out.println("Number of Column : " + col);
            System.out.println("Columns Name: ");
            for (int i = 1; i <= col; i++) {
                String col_name = md.getColumnName(i);
                System.out.println(col_name);
                columnNames.add(col_name);
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed!");
        }
        return columnNames;
    }

    public void testDatabase() {
        String jdbcUrl;
        jdbcUrl = "jdbc:oracle:thin:@localhost:1521:sysdba";
        Connection jdbcCon;
        Statement jdbcStmt;
        //String query = "select ENAME from emp";
        String query = "select STATE_NAME,AREA from states";
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            System.out.println("MR.UnitSitQueries.constructor.Exception: " + e);
        }
        try {
            jdbcCon = DriverManager.getConnection(jdbcUrl, "scott", "P0l0club");
            jdbcStmt = jdbcCon.createStatement();
            ResultSet rs = jdbcStmt.executeQuery(query);
            // Vector rowData = new Vector();
            while (rs.next()) {
                // rowData.addElement(rs.getString(""));
                // System.out.println(rowData.elementAt(0));
                String s1 = rs.getString("STATE_NAME");
                String s2 = rs.getString("AREA");
                System.out.print(s1 + "  ");
                System.out.println(s2);
            }
            jdbcCon.close();
        } catch (SQLException e) {
            System.err.println("OOPS " + e.getMessage());
        }
    }

    public void createShapefile(String query) {
        //obtain the query to parse it and form a proper sql string
        //connectDatabase(finalQueryString);
        String finalQuery = "CREATE TABLE TABLE_NAME AS (" + query + ")";
        openConnection();
        droptable("TABLE_NAME");
        createTable(finalQuery);
        closeConnection();
        generateShapeFile("TABLE_NAME");
    }

    public void connectDatabase(String query) {
//        String jdbcUrl;
//        jdbcUrl = "jdbc:oracle:thin:@localhost:1521:sysdba";
//        Connection jdbcCon;
//        Statement jdbcStmt;
//        try {
//            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
//            System.out.println("MR.UnitSitQueries.constructor.Exception: " + e);
//        }
//        try {
//            jdbcCon = DriverManager.getConnection(jdbcUrl, "scott", "P0l0club");
//            jdbcStmt = jdbcCon.createStatement();
//            jdbcStmt.executeQuery("DROP TABLE TABLE_NAME");
//            ResultSet rs = jdbcStmt.executeQuery(query);
//            jdbcCon.close();
//        } catch (SQLException e) {
//            System.err.println("OOPS " + e.getMessage());
//        }

    }

    public void createTable(String query) {
        try {
            Statement statement = con.createStatement();
            statement.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void droptable(String tableName) {
        //need to add code to make suer to drop table only if it exists from before

        try {
            String query = "DROP TABLE " + tableName;
            Statement statement = con.createStatement();
            statement.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //code to create a table in SQL using command line
    public void generateShapeFile(String tableName) {
        String pathname = "C:\\Users\\Anil\\Documents\\test";
        String user = "scott";
        String pwd = "P0l0club";
//        String tablename = "State_C";
//        tablename = "TABLE_NAME";
        String cmd = "java -jar C:\\Users\\Anil\\Downloads\\sdo2shpcmd.jar " + user + "/" + pwd + " -t " + tableName + " -db sysdba -f " + pathname;
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {
            Logger.getLogger(ConnectionJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
