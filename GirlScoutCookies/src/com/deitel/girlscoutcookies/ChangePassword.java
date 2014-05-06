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
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;


public class ChangePassword extends Activity 
{
	    
   // EditTexts for contact information
   private ProgressDialog pDialog,kDialog;
   private EditText usenameET;
   private EditText usernameET;
   private EditText passwordET;
   private EditText newpasswordET;
   private EditText oldpasswordET;
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
	  System.out.println("in printing now");
      super.onCreate(savedInstanceState); 
      setContentView(R.layout.changepassword); 
      
      usernameET = (EditText) findViewById(R.id.usename);
      passwordET = (EditText) findViewById(R.id.newpass);
      newpasswordET = (EditText) findViewById(R.id.newpassconfirm);
      oldpasswordET = (EditText) findViewById(R.id.oldpassword);
      Button changepassbutton=(Button) findViewById(R.id.changpass);
      changepassbutton.setOnClickListener(new OnClickListener() {
         
          @Override
          public void onClick(View v) 
          {
        	  if(newpasswordET.getText().toString()!=passwordET.getText().toString())
        	  {
                  AlertDialog.Builder builder=new AlertDialog.Builder(ChangePassword.this);
                  builder.setTitle("New Password Doesn't Match"); 
                  builder.setMessage("Please try again.");
                  builder.setPositiveButton("Input Again!", null); 
                  builder.show(); // display the Dialog
        	  }
        	  else
        	  {
                  DatabaseConnector databaseConnector = new DatabaseConnector(ChangePassword.this);
                  databaseConnector.open();
             	 succ=0;
            	 try {
    				succ=databaseConnector.usercheck(usernameET.getText().toString(),oldpasswordET.getText().toString());
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            	 if(succ==1)
            	 {
            		 try {
						databaseConnector.changepass(usernameET.getText().toString(),newpasswordET.getText().toString());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	 }
        	  }
          }
          });
          
          Button cancelbutton = 
     	         (Button) findViewById(R.id.cancel);  
          cancelbutton.setOnClickListener(new OnClickListener() {
      
         @Override
         public void onClick(View v) 
         {
            
     	      Intent Main = 
 	    	         new Intent(ChangePassword.this, Main.class);
 	    	      startActivity(Main);
         }
         
      // end method onClick
      });
   }

}
      
      
      
      
   

     