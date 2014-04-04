/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Long Pham
 */

/** Class to interact with database. 
 * 
 */




import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.Statement;
import java.util.Vector;


public class SQLiteJDBC
{
    private Connection conn;
    private Statement stmt;
    
    public SQLiteJDBC() throws Exception {
//      conn = null; 
//      try {
            Class.forName("org.sqlite.JDBC");
//            conn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Rudy Duarte\\Documents\\College\\Senior Year (2013-2014)\\CSC 271\\Final DBMS Project: Girl_Scouts_DB");
           
//        }
//        catch (Exception e){
//            System.err.println( e.getClass().getName() + ":" + e.getMessage());
//            System.exit(0);
//            
//        }
//      System.out.println("Database is Connected");
    }
    
    public void openConnection() throws Exception {
       conn = DriverManager.getConnection("jdbc:sqlite:/users/longpham/Dropbox/test.db");
       stmt = conn.createStatement();
   }

   public void closeConnection() throws Exception {
       stmt.close();
       conn.close();
   }
   
   public void insertScout(String scout_name, String scout_address, int age) throws Exception{
       this.openConnection();
       stmt.executeUpdate("insert into scout(scout_name, scout_address, age) values('" + scout_name + "','" + scout_address + "'," + age + ");");
       this.closeConnection();
   }
   
   public void editScout(int scout_id, String scout_name, String scout_address, int age) throws Exception {
       this.openConnection();
       stmt.executeUpdate("update scout set scout_name='" + scout_name + "',scout_address='" + scout_address + "',age=" + age + " where scout_id=" + scout_id + ";");
       this.closeConnection();
   }
   
   public void insertCustomer(String cust_name, String address, int phone, String email) throws Exception{
       this.openConnection();
       stmt.executeUpdate("insert into customer values (null, '" + cust_name + "','" + address + "'," + phone + ",'" + email + "');");
       this.closeConnection();
   }
   
   public void editCustomer(int cust_id, String cust_name, String address, int phone, String email) throws Exception {
       this.openConnection();
       stmt.executeUpdate("update customer set cust_name='" + cust_name + "',address='" + address + "',phone=" + phone + ",email='" + email + "' where scout_id=" + cust_id + ";");
       this.closeConnection();
   }
   
   public void insertOrder(String cust_name, String scout_name, String type, int amount, boolean paid, boolean picked_up) throws Exception {
       this.openConnection();
       int paid_int = paid ? 1 : 0;
       int pickedup_int = picked_up ? 1 : 0;
       String string = "insert into c_order values (null," + cust_name + ",'" + scout_name + "','" + type + "'," + amount + "," + paid_int + "," + pickedup_int + ");";
       stmt.executeUpdate("insert into c_order values (null,'" + cust_name + "','" + scout_name + "','" + type + "'," + amount + "," + paid_int + "," + pickedup_int + ");");
       this.closeConnection();
   }
   
   public void editOrder(int order_id, int cust_id, String type, int amount, boolean paid, boolean deliver) throws Exception {
       this.openConnection();
       int paid_int = paid ? 1 : 0;
       int deliver_int = deliver ? 1 : 0;
       stmt.executeUpdate("update c_order set cust_id=" + cust_id + ",type='" + type + "',amount=" + amount + ",paid=" + paid_int + ",deliver=" + deliver_int + " where order_id=" + order_id + ";");
       this.closeConnection();
   }
   
   public void insertCookie(String type, float price) throws Exception {
       this.openConnection();
       stmt.executeUpdate("insert into cookie values ('" + type + "'," + price + ");");
       this.closeConnection();
   }
   
   public JTable findOrder(String searchText) throws Exception {
       DefaultTableModel tbl = new DefaultTableModel();
       JTable output = new JTable(tbl);
       this.openConnection();
       
       ResultSet rs = stmt.executeQuery("select * from customer natural join c_order where cust_name like '%" + searchText + "%' or scout_name like '%" + searchText + "%';");
       
       if (rs != null) {
           ResultSetMetaData metaData = rs.getMetaData();
           int numberOfColumns = metaData.getColumnCount();
           
           for (int i = 1; i <= numberOfColumns; i++) {
               tbl.addColumn(metaData.getColumnName(i));
           }
           
           while(rs.next()) {
                Object[] obj = new Object[numberOfColumns];
                for(int i = 0; i < numberOfColumns;i++){
                    obj[i] = rs.getObject(i);
                }
                tbl.addRow(obj);
           }
       }
       tbl.fireTableDataChanged();
       this.closeConnection();
       
       return output;
       //return null;
   }
   
   public Vector<String> findScout(String searchText) throws Exception {
       //DefaultTableModel tbl = new DefaultTableModel();
       //JTable output = new JTable(tbl);
       Vector<String> output = new Vector<String>();
       this.openConnection();
       
       ResultSet rs = stmt.executeQuery("select distinct cust_name, scout_name, type, amount from c_order where scout_name like '%" + searchText + "%' or cust_name like '%" + searchText + "%' or type like '%" + searchText + "%';");
       
       if (rs != null) {
           ResultSetMetaData metaData = rs.getMetaData();
           int numberOfColumns = metaData.getColumnCount();
//           for (int i = 1; i <= numberOfColumns; i++) {
//               tbl.addColumn(metaData.getColumnName(i));
//           }
           
           while(rs.next()) {
                for (int i = 1; i <= numberOfColumns; i = i + 4) {
                    String string = "";
                    for (int j = i; j < i + 4; j++) {
                        string = string + rs.getObject(j).toString() + " | ";
                    }
                        
                    output.add(string);
                }
                    
//                Object[] obj = new Object[numberOfColumns];
//                for(int i = 0; i < numberOfColumns;i++){
//                    obj[i] = rs.getObject(i);
//                }
                //tbl.addRow(obj);
           }
       }
       //tbl.fireTableDataChanged();
       this.closeConnection();
       
       return output;
   }
}