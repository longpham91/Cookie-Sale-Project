package com.deitel.girlscoutcookies;

import com.deitel.girlscoutcookies.R;
import com.deitel.girlscoutcookies.DatabaseConnector;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class GScoutActivity extends Activity {

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
        	rowID=extras.getLong("row_id");
        	savaEditText.setText(extras.getString("sava"));
        	treEditText.setText(extras.getString("tres"));
        	dosiEditText.setText(extras.getString("dosi"));
        	samaEditText.setText(extras.getString("sama"));
        	tagEditText.setText(extras.getString("tag"));
        	mintEditText.setText(extras.getString("mint"));
        	cusEditText.setText(extras.getString("cusname"));
        }

        Button confirmButton=(Button)findViewById(R.id.confirmcookie);
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
						protected void onPostExecut(Object result)
						{
							finish();
						}
    			};
    			saveCookieTask.execute((Object[])null);
    		}
    			else
    			{
    				AlertDialog.Builder builder=new AlertDialog.Builder(GScoutActivity.this);
    	            builder.setTitle(R.string.errorTitle); 
    	            builder.setMessage(R.string.errorMessage);
    	            builder.setPositiveButton(R.string.errorButton, null); 
    	            builder.show(); // display the Dialog
    			}
    		}

	
			
    	};

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gscout, menu);
        return true;
    }
    
}
