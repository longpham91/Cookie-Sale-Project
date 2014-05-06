package com.deitel.girlscoutcookies;

import java.util.Date;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.deitel.girlscoutcookies.R;
import com.deitel.girlscoutcookies.DatabaseConnector;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class AddEdit extends Activity {

	private long rowID;
	private EditText savaEditText;
	private EditText treEditText;
	private EditText dosiEditText;
	private EditText samaEditText;
	private EditText tagEditText;
	private EditText mintEditText;
	private EditText cusEditText;
	
	public int resuming;

	
	
	
	public int sama;
	public int sava;
	public int dosi;
	public int tag;
	public int mint;
	public int tre;
	public String cusnamedate;
	public String reportDate;
	public String updateDate;
	public Format df;
	public Date today;
	public String reportedDate;
	
	
	private int pickedup;
	private int paid;
	private CheckBox paybox, pickbox;
	private TextView totalTextView;
	public int oldCookieVal;
	public static int totalCookies;
	public int newCookieVal;
	public TextView datetime;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inputcookie); 
        totalCookies =0;
        Button deleteButton=(Button)findViewById(R.id.deletebutton);
     
        deleteButton.setOnClickListener(deletecookieClicked);
        datetime=(TextView)findViewById(R.id.datetime);
        if (Main.addEdit == 1)
        {
        	deleteButton.setVisibility(View.GONE);
        	datetime.setVisibility(View.GONE);
        
        }
        
        savaEditText=(EditText)findViewById(R.id.savaquant);
        treEditText=(EditText)findViewById(R.id.trequant);
        dosiEditText=(EditText)findViewById(R.id.dosiquant);
        samaEditText=(EditText)findViewById(R.id.samquant);
        tagEditText=(EditText)findViewById(R.id.tagquant);
        mintEditText=(EditText)findViewById(R.id.mintquant);
        cusEditText=(EditText)findViewById(R.id.cusname);
        paybox=(CheckBox)findViewById(R.id.checkBox1);
        pickbox=(CheckBox)findViewById(R.id.checkBox2);
        totalTextView=(TextView)findViewById(R.id.totalp);
        
        savaEditText.addTextChangedListener(new CustomTextWatcher(savaEditText));
        treEditText.addTextChangedListener(new CustomTextWatcher(treEditText));
        dosiEditText.addTextChangedListener(new CustomTextWatcher(dosiEditText));
        samaEditText.addTextChangedListener(new CustomTextWatcher(samaEditText));
        tagEditText.addTextChangedListener(new CustomTextWatcher(tagEditText));
        mintEditText.addTextChangedListener(new CustomTextWatcher(mintEditText)); 
        

        newCookieVal = 0;
        Bundle extras = getIntent().getExtras();
        
        if(extras!=null)
        {
        	//System.out.println(extras.toString());
        	rowID=extras.getLong("row_id");
        	
        	
        	
        	
        	
        }

        Button confirmButton=(Button)findViewById(R.id.confirmbutton);
        confirmButton.setOnClickListener(confirmcookieClicked);
        
        
    }
    	OnClickListener confirmcookieClicked = new OnClickListener()
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
    	
    	
    	OnClickListener deletecookieClicked = new OnClickListener()
    	{
    		public void onClick(View v)
    		
    		{
    			if(Main.addEdit == 2)
    			{
    			deleteCookie();
    			finish();
    			}
    		}

	
			
    	};
    	
    	@Override
    	   protected void onResume()
    	   {
    		 super.onResume();
    		if(Main.addEdit == 2){
    	     
    			resuming = 0;
    	      
    	      
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
    	         int paidIndex = result.getColumnIndex("paid");
    	         int pickedupIndex = result.getColumnIndex("pickedup");
    	         int reportDateIndex = result.getColumnIndex("reportDate");
    	         int updateDateIndex = result.getColumnIndex("updateDate");
    	   
    	         // fill TextViews with the retrieved data
    	      
    	         savaEditText.setText(result.getString(savaIndex));
    	         treEditText.setText(result.getString(tresIndex));
    	         dosiEditText.setText(result.getString(dosiIndex));
    	         samaEditText.setText(result.getString(samaIndex));
    	         tagEditText.setText(result.getString(tagIndex));
    	         mintEditText.setText(result.getString(mintIndex));
    	         cusEditText.setText(result.getString(cusnameIndex));
    	         reportedDate = result.getString(reportDateIndex);
    	         String temp = result.getString(updateDateIndex);
    	         datetime.setText("Last updated " + temp.substring(5,8)+temp.substring(8,10)+"/"+temp.substring(0,4)+"." );
    	         
    	          	         
    	         if((result.getInt(paidIndex) == 1))
    	        		 {
    	        	 paybox.setChecked(true);
    	        		 }
    	         
    	         if((result.getInt(paidIndex) == 0))
        		 {
        	 paybox.setChecked(false);
        		 }
    	         
    	         if((result.getInt(pickedupIndex) == 1))
        		 {
        	 pickbox.setChecked(true);
        		 }
         
         if((result.getInt(pickedupIndex) == 0))
		 {
	 pickbox.setChecked(false);
		 }
         
         if (
      	       savaEditText.getText().toString().length() > 0)
      	       {
      	    	   sava = Integer.parseInt(savaEditText.getText().toString());
      	       }
      	       
      	       if (
      	    	       dosiEditText.getText().toString().length() > 0)
      	    	       {
      	    	    	   dosi = Integer.parseInt(dosiEditText.getText().toString());
      	    	       }
      	       
      	       if (
      	    	       samaEditText.getText().toString().length() > 0)
      	    	       {
      	    	    	   sama = Integer.parseInt(samaEditText.getText().toString());
      	    	       }
      	       
      	       if (
      	    	       tagEditText.getText().toString().length() > 0)
      	    	       {
      	    	    	   tag = Integer.parseInt(tagEditText.getText().toString());
      	    	       }
      	       
      	       if (
      	    	       treEditText.getText().toString().length() > 0)
      	    	       {
      	    	    	   tre = Integer.parseInt(treEditText.getText().toString());
      	    	       }
      	       if (
      	    	        mintEditText.getText().toString().length() > 0)
      	    	       {
      	    	    	   mint = Integer.parseInt(mintEditText.getText().toString());
      	    	    	   
      	    	       }
      	       totalCookies = mint + sava+ sama + tre + tag + dosi;
      	       totalTextView.setText("$"+ Integer.toString(totalCookies*4));
    	        
    	         
    	        // }
    	         
    	         
    	         
    	      
    	   
    	         result.close(); // close the result cursor
    	         databaseConnector.close(); // close database connection
    	      } // end method onPostExecute
    	   } // end class LoadMovieTask

    private void saveCookie()
    {
        // get DatabaseConnector to interact with the SQLite database
        DatabaseConnector databaseConnector = new DatabaseConnector(this);
        df = new SimpleDateFormat("yyyy/MM/dd");
    	today =  Calendar.getInstance().getTime();
    	
    	if(Main.addEdit == 1)
    	{
    		reportDate = df.format(today);
    		System.out.println("reportDate");
    		updateDate = reportDate;
    	}
    	
    	else {
    		updateDate = df.format(today);
    		reportDate = reportedDate;
    	}
    	
    	
    	
    	
    	
    	cusnamedate =  cusEditText.getText().toString() + " (" + reportDate +")";
        
	        if (getIntent().getExtras() == null)
        {
           
        	
        	if(pickbox.isChecked()){
        		pickedup = 1;
   		 } 
        	else pickedup = 0;
        	
        	
        	if(paybox.isChecked()){
        		paid = 1;
   		 } 
        	else paid = 0;
        	
        	
           databaseConnector.insertCookie(
              savaEditText.getText().toString(),
              treEditText.getText().toString(), 
              dosiEditText.getText().toString(), 
              samaEditText.getText().toString(),
              tagEditText.getText().toString(),
           	  mintEditText.getText().toString(),
           	  cusEditText.getText().toString(),paid,pickedup, cusnamedate, updateDate, reportDate);
              
           
           
           
           
        } // end if
        else
        {
        	
        	if(pickbox.isChecked()){
        		pickedup = 1;
   		 } 
        	else pickedup = 0;
        	
        	
        	if(paybox.isChecked()){
        		paid = 1;
   		 } 
        	else paid = 0;
        	
           databaseConnector.updateCookie(rowID,
        		   savaEditText.getText().toString(),
                   treEditText.getText().toString(), 
                   dosiEditText.getText().toString(), 
                   samaEditText.getText().toString(),
                   tagEditText.getText().toString(),
                   mintEditText.getText().toString(),
                   cusEditText.getText().toString(),
                   paid,
                   pickedup, cusnamedate, updateDate, reportDate);
        
           
        } // end else
     }
 // end class saveCookie
    private void deleteCookie()
    {
    	DatabaseConnector databaseConnector = new DatabaseConnector(this);
    	databaseConnector.deleteContact(rowID);
    	
    	
    }
    private class CustomTextWatcher implements TextWatcher {
        

        public CustomTextWatcher(EditText e) { 
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        	//System.out.println(Integer.toString(resuming));
        	if (resuming >= 11 || Main.addEdit == 1)
        			{
        	if (s.toString().length()== 0){
        	
        		oldCookieVal = 0;
        		
        	}
        	else
        	{
        		if ((Integer.parseInt(s.toString())) != newCookieVal && (s.toString().length() > 0))
        				{
        				newCookieVal = Integer.parseInt(s.toString());
        		 
        				}
        		oldCookieVal = Integer.parseInt(s.toString());
        		
        	}
        	
        	
        			}
        	resuming +=1;
        	
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        	
        	
        	
        }

		@Override
		public void afterTextChanged(Editable s) {
			if (resuming >= 12 || Main.addEdit == 1)
			{
				
			if (s.toString().length()!= 0){
			newCookieVal = Integer.parseInt(s.toString());
			
			totalCookies += (newCookieVal - oldCookieVal); 
			totalTextView.setText("$"+ Integer.toString(totalCookies*4));
			}
			else
			{
				totalCookies -= newCookieVal;
				totalTextView.setText("$"+ Integer.toString(totalCookies*4));
			}
			
			}
			resuming +=1;

		}

		
		
    }  
    
}
