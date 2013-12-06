package com.sonymobile.smartconnect.extension.controlsample;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CheckboxListAdapter adapter = new CheckboxListAdapter(
				getLayoutInflater());
        final ListView l1=getListView();
       
        
        getListView().setAdapter(adapter);
        Button b1=(Button)findViewById(R.id.buttonmain);
        b1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String companies="";
				for(int i=0;i<l1.getCount();i++)
				{
				SampleData da=(SampleData)l1.getItemAtPosition(i);
				if(da.isSelected())
				{
					companies+=da.getName()+",";
				}
				}
				
				SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
		        editor.putString("CompanyList", companies);
		        editor.commit();
			}
		});
        
        
        
    }


   
    
}
