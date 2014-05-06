package com.deitel.girlscoutcookies;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class LoginDatebase {
	   private final String DATABASE_NAME = "LOGIN";
	   private SQLiteDatabase database2; // database object
	   private DatabaseOpenHelper2 databaseOpenHelper2; // database helper
	   JSONParser jsonParser = new JSONParser();
	   
	   public LoginDatebase(Context context2)
	   {
	      // create a new DatabaseOpenHelper
	      databaseOpenHelper2 = new DatabaseOpenHelper2(context2, DATABASE_NAME, null, 1);
	   } // end DatabaseConnector constructor
	   
	   public void open() throws SQLException 
	   {
	      // create or open a database for reading/writing
	      database2 = databaseOpenHelper2.getWritableDatabase();
	   } // end method open
	   
	   public void close() 
	   {
	      if (database2 != null)
	         database2.close(); // close the database connection
	   } // end method close
	   
	   public void insertUser(String username, String password) 
	   {
		   ContentValues newUser= new ContentValues(); 
		   newUser.put("username",username);
		   newUser.put("password",password);
		   open(); // open the database
		   database2.insert("COOKIES", null, newUser);
		   close(); // close the database
	   }
	   
	   public void updateUser(Long id, String username, String password) 
	   {
		   ContentValues editUser= new ContentValues(); 
		   editUser.put("username",username);
		   editUser.put("password",password);
		   open(); // open the database
		   database2.update("LOGIN", editUser, "_id=" + id, null);
		   close(); // close the database
	   }
	   
	   
	   
	   public class DatabaseOpenHelper2 extends SQLiteOpenHelper 
	   {
	      // public constructor
	      public DatabaseOpenHelper2(Context context, String username,
	         CursorFactory factory, int version) 
	      {
	         super(context, username, factory, version);
	      } // end DatabaseOpenHelper constructor

	      // creates the contacts table when the database is created
	      @Override
	      public void onCreate(SQLiteDatabase db) 
	      {
	         // query to create a new table named contacts
	         String createQuery = "CREATE TABLE LOGIN" +
	            "(_id integer primary key autoincrement," +
	            "username TEXT, password TEXT);";  
	         db.execSQL(createQuery); // execute the query
	         //System.out.print("table created");
	      } // end method onCreate

	      public void onUpgrade(SQLiteDatabase db, int oldVersion, 
	          int newVersion) 
	      {
	      } // end method onUpgrade
	   } // end class DatabaseOpenHelper

}

