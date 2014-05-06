// DatabaseConnector.java
// Provides easy connection and creation of UserContacts database.
package com.deitel.girlscoutcookies;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.StrictMode;
import android.util.Log;

public class DatabaseConnector 
{
   // database name
   private final String DATABASE_NAME = "COOKIES";
   private String loggedinas="trial";
   private SQLiteDatabase database; // database object
   private DatabaseOpenHelper databaseOpenHelper; // database helper
   JSONParser jsonParser = new JSONParser();
   private static String url_create_product = "http://phpnew-gscookiesales.rhcloud.com/create_product.php";
   private static final String url_product_detials = "http://phpnew-gscookiesales.rhcloud.com/get_product_details.php";
	// url to update product
   private static final String url_update_product = "http://phpnew-gscookiesales.rhcloud.com/update_product.php";	
   private static final String url_checkproduct = "http://phpnew-gscookiesales.rhcloud.com/checkproduct.php";	
	// url to delete product
   private static final String url_delete_product = "http://phpnew-gscookiesales.rhcloud.com/delete_product.php";	
   private static final String url_checkpassword="http://phpnew-gscookiesales.rhcloud.com/checkpassword.php";
   private static final String url_updatepassword = "http://phpnew-gscookiesales.rhcloud.com/updatepassword.php";	
   
   
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
//      System.out.println("Now in inserting");
//      List<NameValuePair> params = new ArrayList<NameValuePair>();
//      params.add(new BasicNameValuePair("sava", sava));
//      params.add(new BasicNameValuePair("tres", tres));
//      params.add(new BasicNameValuePair("dosi", dosi));
//      params.add(new BasicNameValuePair("sama", sama));
//      params.add(new BasicNameValuePair("tag", tag));
//      params.add(new BasicNameValuePair("mint", mint));
//      params.add(new BasicNameValuePair("cusname", cusname));
//      params.add(new BasicNameValuePair("paid", Integer.toString(paid)));
//      params.add(new BasicNameValuePair("pickedup", Integer.toString(pickedup)));
//      params.add(new BasicNameValuePair("cusnamedate", cusnamedate));
//      params.add(new BasicNameValuePair("updateDate", updateDate));
//      params.add(new BasicNameValuePair("reportDate", reportDate));
//      params.add(new BasicNameValuePair("gsno", "test"));
//      JSONObject json = jsonParser.makeHttpRequest(url_create_product, "POST", params);
//      Log.d("Create Response", json.toString());
//      System.out.println("inserted");
   } // end method insertContact
   public Cursor getUpdateAllContacts()
   {
	  return database.query("COOKIES", new String[] {"_id", "sava","tres","dosi","sama","tag","mint","cusname","paid","pickedup","cusnamedate","updateDate","reportDate"}, 
	    null, null, null, null, null);
   }
   
   public void testingdb()
   {
	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
   	   	System.out.println("Now in dbtesting");
   	   	System.out.println("entering function");
        Cursor result= getUpdateAllContacts(); 
        System.out.println("called dbconnector function");
//  	         pDialog = new ProgressDialog(Main.this);
//  			 pDialog.setMessage("Loading product details. Please wait...");
//  			 pDialog.setIndeterminate(false);
//  			 pDialog.setCancelable(true);
//
//  			 pDialog.show();

  	         if (result != null && result.moveToFirst()) {
  	             do {
  	         // get the column index for each data item
  	         int rowidIndex = result.getColumnIndex("_id");
  	         int savaIndex = result.getColumnIndex("sava");
  	         int tresIndex = result.getColumnIndex("tres");
  	         int dosiIndex = result.getColumnIndex("dosi");
  	         int samaIndex = result.getColumnIndex("sama");
  	         int tagIndex = result.getColumnIndex("tag");
  	         int mintIndex = result.getColumnIndex("mint");
  	         int cusnameIndex = result.getColumnIndex("cusname");
  	         int paidIndex = result.getColumnIndex("paid");
  	         int pickedupIndex = result.getColumnIndex("pickedup");
  	         int cusnamedateIndex = result.getColumnIndex("cusnamedate");
  	         int reportDateIndex = result.getColumnIndex("reportDate");
  	         int updateDateIndex = result.getColumnIndex("updateDate");
  	         String gsno=loggedinas+result.getString(rowidIndex);
  	         System.out.println("fethced data");


//  	         String gsno="Emma"+result.getString(rowidIndex);
  	         System.out.println("fethced this guy's data:"+gsno);
  	         List<NameValuePair> params = new ArrayList<NameValuePair>();
  	         params.add(new BasicNameValuePair("sava", result.getString(savaIndex)));
  	         params.add(new BasicNameValuePair("tres", result.getString(tresIndex)));
  	         params.add(new BasicNameValuePair("dosi", result.getString(dosiIndex)));
  	         params.add(new BasicNameValuePair("sama", result.getString(samaIndex)));
  	         params.add(new BasicNameValuePair("tag", result.getString(tagIndex)));
  	         params.add(new BasicNameValuePair("mint", result.getString(mintIndex)));
  	         params.add(new BasicNameValuePair("cusname", result.getString(cusnameIndex)));
  	         params.add(new BasicNameValuePair("paid", result.getString(paidIndex)));
  	         params.add(new BasicNameValuePair("pickedup", result.getString(pickedupIndex)));
  	         params.add(new BasicNameValuePair("cusnamedate", result.getString(cusnamedateIndex)));
  	         params.add(new BasicNameValuePair("updateDate", result.getString(updateDateIndex)));
  	         params.add(new BasicNameValuePair("reportDate", result.getString(reportDateIndex)));
  	         params.add(new BasicNameValuePair("gsno",gsno));
  	         
  	         boolean suc=false;
  	         System.out.println("finished fetching ");
  	         try
  	         {
//  	        	System.out.println("checking order on network ");
//  	        	List<NameValuePair> params2 = new ArrayList<NameValuePair>();
//  	        	params2.add(new BasicNameValuePair("gsno",gsno));
//  	        	JSONObject json = jsonParser.makeHttpRequest(url_checkproduct, "GET", params);
////	        		 System.out.println("updating failed, trying creating data to network ");
////  	    	         JSONObject json = jsonParser.makeHttpRequest(url_create_product, "POST", params);
////  	    	         Log.d("Create Response", json.toString()); 
//  	        	if(json.getInt("success")==1)
//  	        	{
  	        	//JSONObject json = jsonParser.makeHttpRequest(url_checkproduct, "GET", params);
	  	         System.out.println("updating data to network ");
	  	         JSONObject json = jsonParser.makeHttpRequest(url_create_product, "POST", params);
	  	         Log.d("Create Response", json.toString());
	  	         
	  	         if(json.getInt("success")==1)
	  	         {
	  	         suc=true;
	  	         System.out.println("updated");
	  	         }
  	         //}
	  	         } catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
	  	         }
	  	         
  	         finally	
  	         {
  	        	 if(suc==false)
  	        	 {
  	        		 System.out.println("updating failed, trying creating data to network ");
  	    	         JSONObject json = jsonParser.makeHttpRequest(url_create_product, "POST", params);
  	    	         Log.d("Create Response", json.toString()); 
  	    	      //   pDialog.dismiss();
  	        	 }
  	        	 else
  	        	 {
  	        		// pDialog.dismiss();
//  	    			 kDialog=new ProgressDialog(Main.this);
//  	    			 kDialog.setMessage("Sync Unssuccessful. Please try again later...");
//  	    			 kDialog.setIndeterminate(false);
//  	    			 kDialog.setCancelable(true);
//  	        		 kDialog.show();
  	        	 }
  	         }
  	         } while (result.moveToNext());
  	         result.close();
       } 
   }
   
   public int usercheck(String usname, String pass) throws JSONException
   {
   	List<NameValuePair> params=new ArrayList<NameValuePair>();
   	System.out.println("printing this user:"+usname);
   	System.out.println("printing this password:"+pass);
   	params.add(new BasicNameValuePair("scout_name",usname));
   	params.add(new BasicNameValuePair("password",pass));
   	JSONObject json = jsonParser.makeHttpRequest(url_checkpassword,"POST",params);
	Log.d("Single Product Details", json.toString());
	if(json.getInt("success")==1)
	{
		loggedinas=usname;
		
	}
	else
	{
		loggedinas="unknownscout";
	}
	return json.getInt("success");
   }
   
   public void changepass(String usname, String pass) throws JSONException
   {
	   	List<NameValuePair> params=new ArrayList<NameValuePair>();
	   	System.out.println("printing this user:"+usname);
	   	System.out.println("printing this password:"+pass);
	   	params.add(new BasicNameValuePair("scout_name",usname));
	   	params.add(new BasicNameValuePair("password",pass));
	   	JSONObject json = jsonParser.makeHttpRequest(url_updatepassword,"POST",params);
		Log.d("Single Product Details", json.toString());
   }
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
