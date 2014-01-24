package shapefile;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Anil
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
    /*
     public HashMap<String, STRUCT> getShapes(String clause) {
     HashMap<String, STRUCT> shapes = new HashMap<>();

     try {
     String query = clause;			
     // connection to DBMS or database
     stmt = con.createStatement(); // creates object from which SQL commands
     // can be sent to the DBMS
     ResultSet rs = stmt.executeQuery(query); // create result object to hold
     // information returned by the DBMS
     while (rs.next()) {
     shapes.put(rs.getString(1), (STRUCT) rs.getObject(2));
     System.out.println(rs.getString(1));
     }

     stmt.close();
     } catch (SQLException e) {
     System.out.println("OOPS" + e.getMessage());
     }

     return shapes;
     }

     public HashMap<String, String> getArea() {
     HashMap<String, String> areas = new HashMap<>();

     String query = "SELECT name, SDO_GEOM.SDO_AREA(shape, 0.005) FROM midnight_run";
     try {
     stmt = con.createStatement();
     ResultSet rs = stmt.executeQuery(query); // create result object 
			
     while (rs.next()) {
     areas.put(rs.getString(1), rs.getString(2));
     System.out.println(rs.getString(1) + "			 : " + rs.getString(2));
     }
     } catch (SQLException e) {
     e.printStackTrace();
     }
     return areas;
     }
	
     public HashMap<String, String> getLength() {
     HashMap<String, String> lengths = new HashMap<>();

     String query = "SELECT c.name, SDO_GEOM.SDO_LENGTH(c.shape, m.diminfo) " +
     "FROM midnight_run c, user_sdo_geom_metadata m " +
     "WHERE m.table_name = 'MIDNIGHT_RUN' AND m.column_name = 'SHAPE'";
		
     try {
     stmt = con.createStatement();
     ResultSet rs = stmt.executeQuery(query); // create result object to
			
     while (rs.next()) {
     lengths.put(rs.getString(1), rs.getString(2));
     System.out.println(rs.getString(1) + "			 : " + rs.getString(2));
     }
     } catch (SQLException e) {
     e.printStackTrace();
     }
     return lengths;
     }

	
	
     public ResultSet runQuery(String query) {
     try {
     stmt = con.createStatement();
     return stmt.executeQuery(query); // create result object 
     } catch (SQLException e) {
     e.printStackTrace();
     }
     return null;
     }

     public List getParts(String query) {
     List parts = new List();

     try {
     stmt = con.createStatement();
     ResultSet rs = stmt.executeQuery(query); // create result object 
			
     while (rs.next()) {
     parts.add(rs.getString(1));
     }
     } catch (SQLException e) {
     e.printStackTrace();
     }
     return parts;
     }
     */

    public void getColumnDataTye(String query) {
        //String query = "SELECT * FROM states";
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
        String baseQuery = query;
        String finalQuery = "";
        String[] individualItems = baseQuery.split("[ ]+");

        for (int i = 0; i < individualItems.length; i++) {
            System.out.println(individualItems[i]);
        }

        for (int i = 0; i < individualItems.length; i += 4) {
            String clause = "";
            String criteriaName = individualItems[i];
            String criteriaCondition = individualItems[i + 1];
            String criteriaValue = individualItems[i + 2];
            clause = "(" + criteriaName + criteriaCondition + criteriaValue + ")";
            String conjuction = "";
            if (individualItems.length > i + 3) {
                conjuction = individualItems[i + 3];
            }
            finalQuery += clause + " " + conjuction + " ";
        }

        String baseTableQuery = "CREATE TABLE NEW_TABLE_NAME AS (select * from states where ";
        String finalQueryString = baseTableQuery + finalQuery + ")";
        connectDatabase(finalQueryString);
        
        //code to create a table in SQL
        String pathname = "C:\\Users\\Anil\\Documents\\test";
        String user = "scott";
        String pwd = "P0l0club";
        String tablename = "State_C";
        tablename = "NEW_TABLE_NAME";

        String cmd = "java -jar C:\\Users\\Anil\\Downloads\\sdo2shpcmd.jar " + user + "/" + pwd + " -t " + tablename + " -db sysdba -f " + pathname;
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {
            Logger.getLogger(ConnectionJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void connectDatabase(String query) {
        String jdbcUrl;
        jdbcUrl = "jdbc:oracle:thin:@localhost:1521:sysdba";
        Connection jdbcCon;
        Statement jdbcStmt;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            System.out.println("MR.UnitSitQueries.constructor.Exception: " + e);
        }
        try {
            jdbcCon = DriverManager.getConnection(jdbcUrl, "scott", "P0l0club");
            jdbcStmt = jdbcCon.createStatement();
            ResultSet rs = jdbcStmt.executeQuery(query);
            jdbcCon.close();
        } catch (SQLException e) {
            System.err.println("OOPS " + e.getMessage());
        }
    }

}
