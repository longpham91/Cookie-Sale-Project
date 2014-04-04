package com.deitel.girlscoutcookies;


import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class Login extends Activity 
{
 
   
   // EditTexts for contact information
   private EditText usernameET;
   private EditText passwordET;
   
   
  
   
   
   // called when the Activity is first started
   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
	   
      super.onCreate(savedInstanceState); 
      setContentView(R.layout.activity_gscout); 
      
      usernameET = (EditText) findViewById(R.id.usernameET);
      passwordET = (EditText) findViewById(R.id.passwordET);
      
      Button submitcredentialsbutton = 
    	         (Button) findViewById(R.id.submitcredentialsbutton);
      
     
      submitcredentialsbutton.setOnClickListener(new OnClickListener() {
      
         @Override
         public void onClick(View v) 
         {
        	 
            if ((usernameET.getText().toString().equals("z")) && (passwordET.getText().toString().equals("z")))
            {
            	
            	Intent Main =
                         new Intent(getBaseContext(),CookieBook.class);
                      
                      
                      
                      
                      startActivity(Main); 
                      
                  
               // save the contact to the database using a separate thread
              
            } // end if
             // end else
         }
      // end method onClick
      });
   }
}
      
      
      
      
   

     