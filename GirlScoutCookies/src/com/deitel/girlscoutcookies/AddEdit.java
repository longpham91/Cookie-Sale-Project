package com.deitel.girlscoutcookies;

import com.deitel.girlscoutcookies.R;
import com.deitel.girlscoutcookies.DatabaseConnector;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class AddEdit extends Activity {

	private long rowID;
	
    
              
              
              
              
          
	
	private EditText savaEditText;
	private EditText treEditText;
	private EditText dosiEditText;
	private EditText samaEditText;
	private EditText tagEditText;
	private EditText mintEditText;
	private EditText cusEditText;
	private String pickup;
	private String paid;
	private CheckBox paybox, pickbox;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inputcookie); 
        savaEditText=(EditText)findViewById(R.id.savaquant);
        treEditText=(EditText)findViewById(R.id.trequant);
        dosiEditText=(EditText)findViewById(R.id.dosiquant);
        samaEditText=(EditText)findViewById(R.id.samquant);
        tagEditText=(EditText)findViewById(R.id.tagquant);
        mintEditText=(EditText)findViewById(R.id.mintquant);
        cusEditText=(EditText)findViewById(R.id.cusname);

        Bundle extras = getIntent().getExtras();
        
        if(extras!=null)
        {
        	System.out.println(extras.toString());
        	rowID=extras.getLong("row_id");
        	
        	
        	
        	
        	
        }

        Button confirmButton=(Button)findViewById(R.id.confirmbutton);
        confirmButton.setOnClickListener(confirmcookieCliked);
    }
    	OnClickListener confirmcookieCliked = new OnClickListener()
    	{
    		public void onClick(View v)
    		
    		
    		{
    			
    			if(cusEditText.getText().length()!=0)
    			{
    				AsyncTask<Object,Object,Object> saveCookieTask=
    						new AsyncTask<Object,Object,Object>() 
    				{
						protected Object doInBackground(Object...params)
						{
							saveCookie();
							return null; 	
						}
						protected void onPostExecute(Object result)
						{
							finish();
							
							
						}
    			};
    			saveCookieTask.execute((Object[])null);
    		}
    			else
    			{
    				AlertDialog.Builder builder=new AlertDialog.Builder(AddEdit.this);
    	            builder.setTitle(R.string.errorTitle); 
    	            builder.setMessage(R.string.errorMessage);
    	            builder.setPositiveButton(R.string.errorButton, null); 
    	            builder.show(); // display the Dialog
    			}
    		}

	
			
    	};
    	
    	@Override
    	   protected void onResume()
    	   {
    		 super.onResume();
    		if(Main.addEdit == 2){
    	     
    	      
    	      // create new LoadMovieTask and execute it 
    	      new LoadCookieTask().execute(rowID);
    		}
    	   }
    	
    	private class LoadCookieTask extends AsyncTask<Long, Object, Cursor> 
    	   {
    	     DatabaseConnector databaseConnector = 
    	         new DatabaseConnector(AddEdit.this);

    	      // perform the database access
    	      @Override
    	      protected Cursor doInBackground(Long... params)
    	      {
    	         databaseConnector.open();
    	         
    	         // get a cursor containing all data on given entry
    	         return databaseConnector.getOneContact(params[0]);
    	      } // end method doInBackground

    	      // use the Cursor returned from the doInBackground method
    	      @Override
    	      protected void onPostExecute(Cursor result)
    	      {
    	         super.onPostExecute(result);
    	   
    	         result.moveToFirst(); // move to the first item 
    	   
    	         // get the column index for each data item
    	         int savaIndex = result.getColumnIndex("sava");
    	         int tresIndex = result.getColumnIndex("tres");
    	         int dosiIndex = result.getColumnIndex("dosi");
    	         int samaIndex = result.getColumnIndex("sama");
    	         int tagIndex = result.getColumnIndex("tag");
    	         int mintIndex = result.getColumnIndex("mint");
    	         int cusnameIndex = result.getColumnIndex("cusname");
    	        
    	   
    	         // fill TextViews with the retrieved data
    	         if (result.getString(savaIndex) != null)
    	         {
    	         savaEditText.setText(result.getString(savaIndex));
    	         treEditText.setText(result.getString(tresIndex));
    	         dosiEditText.setText(result.getString(dosiIndex));
    	         samaEditText.setText(result.getString(samaIndex));
    	         tagEditText.setText(result.getString(tagIndex));
    	         mintEditText.setText(result.getString(mintIndex));
    	         cusEditText.setText(result.getString(cusnameIndex));
    	         }
    	         
    	         
    	         
    	      
    	   
    	         result.close(); // close the result cursor
    	         databaseConnector.close(); // close database connection
    	      } // end method onPostExecute
    	   } // end class LoadMovieTask

    private void saveCookie()
    {
        // get DatabaseConnector to interact with the SQLite database
        DatabaseConnector databaseConnector = new DatabaseConnector(this);

        if (getIntent().getExtras() == null)
        {
           
           databaseConnector.insertCookie(
              savaEditText.getText().toString(),
              treEditText.getText().toString(), 
              dosiEditText.getText().toString(), 
              samaEditText.getText().toString(),
              tagEditText.getText().toString(),
           	  mintEditText.getText().toString(),
           	  cusEditText.getText().toString());
           
           
           
           
        } // end if
        else
        {
           databaseConnector.updateCookie(rowID,
        		   savaEditText.getText().toString(),
                   treEditText.getText().toString(), 
                   dosiEditText.getText().toString(), 
                   samaEditText.getText().toString(),
                   tagEditText.getText().toString(),
                   mintEditText.getText().toString(),
                   cusEditText.getText().toString());
        
           
        } // end else
     } // end class saveCookie
   
    
}
