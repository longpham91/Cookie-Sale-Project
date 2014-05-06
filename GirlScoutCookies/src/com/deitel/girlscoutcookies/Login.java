package com.deitel.girlscoutcookies;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;


public class Login extends Activity 
{
	    
   // EditTexts for contact information
   private ProgressDialog pDialog,kDialog;
   private EditText usernameET;
   private EditText passwordET;
   public Intent Main;
   private int succ;
   private static final String url_checkpassword="http://phpnew-gscookiesales.rhcloud.com/checkpassword.php";
   private static final String url_update_product = "http://phpnew-gscookiesales.rhcloud.com/update_product.php";	
   private static String url_create_product = "http://phpnew-gscookiesales.rhcloud.com/create_product.php";
   private JSONParser jsonParser = new JSONParser();
   private String password,username;
   private CursorAdapter contactAdapter; 
   
   
   // called when the Activity is first started
   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
	   if (Main == null){
		    Main =
		           new Intent(Login.this, Main.class);
		    }
	   
      super.onCreate(savedInstanceState); 
      setContentView(R.layout.activity_gscout); 
      
      usernameET = (EditText) findViewById(R.id.usename);
      passwordET = (EditText) findViewById(R.id.newpassconfirm);
      Button changepassbutton=(Button) findViewById(R.id.changpassbutton);
      changepassbutton.setOnClickListener(new OnClickListener() {
        
          @Override
          public void onClick(View v) 
          {
       	  AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);

        	  alert.setTitle("Change Password");
        	  alert.setMessage("Please input your new password");

        	  // Set an EditText view to get user input 
        	  final EditText input = new EditText(Login.this);
        	  alert.setView(input);

        	  alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        	  public void onClick(DialogInterface dialog, int whichButton) {
        	    String value = input.getText().toString();
        	    DatabaseConnector databaseConnector = new DatabaseConnector(Login.this);
                databaseConnector.open();
           	 	succ=0;
	          	 try {
	  				succ=databaseConnector.usercheck(usernameET.getText().toString(),passwordET.getText().toString());
	  			} catch (Exception e) {
	  				// TODO Auto-generated catch block
	  				e.printStackTrace();
	  			}
	          	 System.out.println("button clicked"+String.valueOf(succ));
	          	 if(succ==1)
	          	 {
	          		 try {
							databaseConnector.changepass(usernameET.getText().toString(),value);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	          	 }
	          	 else{
	                 AlertDialog.Builder builder=new AlertDialog.Builder(Login.this);
	                 builder.setTitle("Wrong Password"); 
	                 builder.setMessage("You have input a wrong password, try again.");
	                 builder.setPositiveButton("Input Again!", null); 
	                 builder.show(); // display the Dialog
	          	 }
        	    }
        	  });

        	  alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int whichButton) {
        	      // Canceled.
        	    }
        	  });

        	  alert.show();
          }
          });
 	  Button quit=(Button)findViewById(R.id.cancel);
      quit.setOnClickListener(new OnClickListener() {
    	  public void onClick(View v)
    	  {
    	      Intent Main = 
    	    	         new Intent(Login.this, Main.class);
    	    	      startActivity(Main);
    	  }
      });
          Button submitcredentialsbutton = 
     	         (Button) findViewById(R.id.changpass);  
      submitcredentialsbutton.setOnClickListener(new OnClickListener() {
      
   
         @Override
         public void onClick(View v) 
         {
             DatabaseConnector databaseConnector = new DatabaseConnector(Login.this);
             databaseConnector.open();
        	 succ=0;
        	 try {
				succ=databaseConnector.usercheck(usernameET.getText().toString(),passwordET.getText().toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        			 
            if (succ==1)
            {
           	      username=usernameET.getText().toString();

                  databaseConnector.testingdb();
                  System.out.println("button clicked");
              	  databaseConnector.close();
	              startActivity(Main); 
                      
                  
               // save the contact to the database using a separate thread
              
            } // end if
            else
            {
              AlertDialog.Builder builder=new AlertDialog.Builder(Login.this);
              builder.setTitle("Wrong Password"); 
              builder.setMessage("You have input a wrong password, try again.");
              builder.setPositiveButton("Input Again!", null); 
              builder.show(); // display the Dialog
         
            }
             // end else
         }
         
      // end method onClick
      });
   }

}
      
      
      
      
   

     