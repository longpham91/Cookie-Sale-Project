// AddressBook.java
// Main activity for the Address Book app.
package com.deitel.girlscoutcookies;

import com.deitel.girlscoutcookies.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Main extends ListActivity 
{
 
   public static final String ROW_ID = "row_id"; // Intent extra key
   private ListView contactListView; // the ListActivity's ListView
   private CursorAdapter contactAdapter; 
   public static int addEdit;
   
   // called when the activity is first created
   @SuppressWarnings("deprecation")
@Override
   public void onCreate(Bundle savedInstanceState) 
   {
   
      super.onCreate(savedInstanceState); // call super's onCreate         
     
      contactListView = getListView(); // get the built-in ListView
      contactListView.setOnItemClickListener(viewContactListener);      
      

      // map each contact's name to a TextView in the ListView layout
      String[] from = new String[] {"cusnamedate"};
      
     
      int[] to = new int[] { R.id.cookieTextView };
    
      
     
      contactAdapter = new SimpleCursorAdapter(
         Main.this, R.layout.viewtransaction, null, from, to);
      setListAdapter(contactAdapter);
      
    /*  contactAdapter2 = new SimpleCursorAdapter(
    	         Main.this, R.layout.viewtransaction, null, from2, to2);
    	      setListAdapter(contactAdapter2);// set contactView's adapter */
   } // end method onCreate

   @Override
   protected void onResume() 
   {
      super.onResume(); // call super's onResume method
      
       // create new GetContactsTask and execute it 
       new GetContactsTask().execute((Object[]) null);
    } // end method onResume

   @SuppressWarnings("deprecation")
@Override
   protected void onStop() 
   {
      Cursor cursor = contactAdapter.getCursor(); // get current Cursor
      
      if (cursor != null) 
         cursor.deactivate(); // deactivate it
      
      contactAdapter.changeCursor(null); // adapted now has no Cursor
      super.onStop();
   } // end method onStop

   // performs database query outside GUI thread
   private class GetContactsTask extends AsyncTask<Object, Object, Cursor> 
   {
      DatabaseConnector databaseConnector = 
         new DatabaseConnector(Main.this);

      // perform the database access
      @Override
      protected Cursor doInBackground(Object... params)
      {
         databaseConnector.open();
         
         // get a cursor containing call contacts
         return databaseConnector.getAllContacts(); 
      } // end method doInBackground

      // use the Cursor returned from the doInBackground method
      @Override
      protected void onPostExecute(Cursor result)
      {
         contactAdapter.changeCursor(result); // set the adapter's Cursor
         databaseConnector.close();
      } // end method onPostExecute
   } // end class GetContactsTask
      
   // create the Activity's menu from a menu resource XML file
   @Override
   public boolean onCreateOptionsMenu(Menu menu) 
   {
      super.onCreateOptionsMenu(menu);
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.gscout, menu);
      return true;
   } // end method onCreateOptionsMenu
   
   // handle choice from options menu
   
   public boolean isConnected(Context context) {
    
     ConnectivityManager connectivityManager = (ConnectivityManager)
         context.getSystemService(Context.CONNECTIVITY_SERVICE);
     NetworkInfo networkInfo = null;
     if (connectivityManager != null) {
         networkInfo =
             connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
     }
     return networkInfo == null ? false : networkInfo.isConnected();
 }
   
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item) 
   {
    if (item.getItemId() == R.id.action_settings)
    {
      
    addEdit = 1;
      Intent addNewContact = 
         new Intent(Main.this, AddEdit.class);
      startActivity(addNewContact);
    }
    
    if (item.getItemId() == R.id.action_sync)
    {
      if(isConnected(getBaseContext())== true){
    //addEdit = 1;
      Intent Login = 
         new Intent(Main.this, Login.class);
      startActivity(Login);
      }
      else
      {
       AlertDialog.Builder builder=new AlertDialog.Builder(Main.this);
          builder.setTitle("WIFI"); 
          builder.setMessage("You are not currently connected to wifi, connect and try again.");
          builder.setPositiveButton("Ok, thanks based developer!", null); 
          builder.show(); // display the Dialog
      }
    }
    
    if (item.getItemId() == R.id.delete)
    {

        DatabaseConnector databaseConnector = 
           new DatabaseConnector(Main.this);
       
        
        databaseConnector.delete();
        finish();
        Intent Restart = 
                new Intent(Main.this, Main.class);
             startActivity(Restart);
    
     
    }
    
      return super.onOptionsItemSelected(item); // call super's method
   } // end method onOptionsItemSelected

   // event listener that responds to the user touching a contact's name
   // in the ListView
   OnItemClickListener viewContactListener = new OnItemClickListener() 
   {
      @Override
      public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
         long arg3) 
      {
       addEdit = 2;
         // create an Intent to launch the ViewContact Activity
         Intent viewContact = 
            new Intent(Main.this, AddEdit.class);
         
         // pass the selected contact's row ID as an extra with the Intent
         viewContact.putExtra(ROW_ID, arg3);
         startActivity(viewContact); // start the ViewContact Activity
      }
      
      
      
      // end method onItemClick
   }; // end viewContactListener
} // end class AddressBook


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
