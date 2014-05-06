// DatabaseConnector.java
// Provides easy connection and creation of UserContacts database.
package com.deitel.girlscoutcookies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseConnector 
{
   // database name
   private final String DATABASE_NAME = "COOKIES";
   private SQLiteDatabase database; // database object
   private DatabaseOpenHelper databaseOpenHelper; // database helper

   // public constructor for DatabaseConnector
   public DatabaseConnector(Context context)
   {
      // create a new DatabaseOpenHelper
      databaseOpenHelper = 
         new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
   } // end DatabaseConnector constructor

   // open the database connection
   public void open() throws SQLException 
   {
      // create or open a database for reading/writing
      database = databaseOpenHelper.getWritableDatabase();
   } // end method open

   // close the database connection
   public void close() 
   {
      if (database != null)
         database.close(); // close the database connection
   } // end method close

   // inserts a new transaction in the database
   public void insertCookie(String sava, String tres, String dosi, 
      String sama, String tag, String mint, String cusname, int paid, int pickedup, String cusnamedate, String updateDate, String reportDate) 
   {
      ContentValues newCookie= new ContentValues();
      newCookie.put("sava", sava);
      newCookie.put("tres", tres);
      newCookie.put("dosi", dosi);
      newCookie.put("sama", sama);
      newCookie.put("tag", tag);
      newCookie.put("mint", mint);
      newCookie.put("cusname", cusname);
      newCookie.put("paid", paid);
      newCookie.put("pickedup", pickedup);
      newCookie.put("cusnamedate", cusnamedate);
      newCookie.put("updateDate", updateDate);
      newCookie.put("reportDate", reportDate);
      

      open(); // open the database
      database.insert("COOKIES", null, newCookie);
      close(); // close the database
   } // end method insertContact

   // inserts a new contact in the database
   public void updateCookie(long id, String sava, String tres, String dosi, 
		      String sama, String tag, String mint, String cusname, int paid, int pickedup, String cusnamedate, String updateDate, String reportDate) 
   {
      ContentValues editCookie = new ContentValues();
      editCookie.put("sava", sava);
      editCookie.put("tres", tres);
      editCookie.put("dosi", dosi);
      editCookie.put("sama", sama);
      editCookie.put("tag", tag);
      editCookie.put("mint", mint);
      editCookie.put("cusname", cusname);
      editCookie.put("paid", paid);
      editCookie.put("pickedup", pickedup);
      editCookie.put("cusnamedate", cusnamedate);
      editCookie.put("updateDate", updateDate);
      editCookie.put("reportDate", reportDate);
      

      open(); // open the database
      database.update("COOKIES", editCookie, "_id=" + id, null);
      close(); // close the database
   } // end method updateContact

   // return a Cursor with all contact information in the database
   public Cursor getAllContacts() 
   {
	  
      return database.query("COOKIES", new String[] {"_id", "cusnamedate"}, 
         null, null, null, null, "cusnamedate");
   } 

   // get a Cursor containing all information about the contact specified
   // by the given id
   public Cursor getOneContact(long id) 
   {
      return database.query(
         "COOKIES", null, "_id=" + id, null, null, null, null);
   } // end method getOnContact

   // delete the contact specified by the given String name
   public void deleteContact(long id) 
   {
      open(); // open the database
      database.delete("COOKIES", "_id=" + id, null);
      close(); // close the database
   } // end method deleteContact
   
   public void delete() {
 	  //int id = 0;

 	  
 	  
 	  
	   	
	   
	   open(); 
    
     String query = "DELETE FROM COOKIES;";
 	 	
 	  	  database.execSQL(query);
 	  	  
 	  	 
 	   close();
 	 	
 	 }
   
   
   
   private class DatabaseOpenHelper extends SQLiteOpenHelper 
   {
      // public constructor
      public DatabaseOpenHelper(Context context, String cusnamedate,
         CursorFactory factory, int version) 
      {
         super(context, cusnamedate, factory, version);
      } // end DatabaseOpenHelper constructor

      // creates the contacts table when the database is created
      @Override
      public void onCreate(SQLiteDatabase db) 
      {
         // query to create a new table named contacts
         String createQuery = "CREATE TABLE COOKIES" +
            "(_id integer primary key autoincrement," +
            "cusname TEXT, sava TEXT, tres TEXT, dosi TEXT, mint TEXT, " +
            "sama TEXT, tag TEXT, paid TEXT, pickedup TEXT, cusnamedate TEXT, updateDate TEXT, reportDate TEXT);";
         
                  
         db.execSQL(createQuery); // execute the query
         //System.out.print("table created");
      } // end method onCreate
      
       
      
      

      
      
      

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, 
          int newVersion) 
      {
      } // end method onUpgrade
   } // end class DatabaseOpenHelper





	

} // end class DatabaseConnector


/**************************************************************************
 * (C) Copyright 1992-2012 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 **************************************************************************/
