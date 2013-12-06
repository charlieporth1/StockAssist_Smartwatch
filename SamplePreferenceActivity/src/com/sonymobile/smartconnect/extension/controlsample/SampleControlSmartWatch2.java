/*
Copyright (c) 2011, Sony Ericsson Mobile Communications AB
Copyright (c) 2011-2013, Sony Mobile Communications AB

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of the Sony Ericsson Mobile Communications AB / Sony Mobile
 Communications AB nor the names of its contributors may be used to endorse or promote
 products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.sonymobile.smartconnect.extension.controlsample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.view.View.OnClickListener;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlListItem;
import com.sonyericsson.extras.liveware.extension.util.control.ControlObjectClickEvent;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;
import com.sonyericsson.extras.liveware.extension.util.control.ControlView;
import com.sonyericsson.extras.liveware.extension.util.control.ControlViewGroup;

/**
 * The sample control for SmartWatch handles the control on the accessory. This
 * class exists in one instance for every supported host application that we
 * have registered to
 */
class SampleControlSmartWatch2 extends ControlExtension {

	private static final int MENU_ITEM_0 =0;
	private static final int MENU_ITEM_1 =1;
	private static final int MENU_ITEM_2 =2;
	private static final int MENU_ITEM_3 =3;
	private static final int MENU_ITEM_4 =4;
	private static final int MENU_ITEM_5=5;

	String stockSymbol="";
	String stags;
	int screenAt=0;
	String stc;
	int position=0;
	private Handler mHandler;
	Context context;
	private ControlViewGroup mLayout = null;

	private boolean mTextMenu = false;
	Bundle[] mMenuItemsText = new Bundle[3];
	Bundle[] mMenuItemsIcons = new Bundle[3];
	String inputGold ="",inputOil="",inputSensex="",inputSensexDiff="";
	int[] screenControl={R.layout.smart_watchlayout2,R.layout.gold_layout,R.layout.oil_layout,R.layout.nifty_layout};
	SharedPreferences.Editor editor;

	String companySensex[];
	/**
	 * Create sample control.
	 *
	 * @param hostAppPackageName Package name of host application.
	 * @param context The context.
	 * @param handler The handler to use
	 */
	SampleControlSmartWatch2(final String hostAppPackageName, Context context,Handler handler) 
	{
		super(context, hostAppPackageName);
		this.context=context;
		
		new GetGoldValue().execute();
		new GetOil().execute();
		new GetSensex().execute();
		if (handler == null) 
		{
			throw new IllegalArgumentException("handler == null");
		}
		editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putString("GoldTime", System.currentTimeMillis()+"");
		editor.commit();
		Log.d("cming","################coinstructor");
		mHandler = handler;
		setupClickables(context);
		initializeMenus();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String companyList=prefs.getString("CompanyList","");
		String[] company=companyList.split(",");
		
		//stc=company[0];
		companySensex=new String[10];
		companySensex[0]="";companySensex[1]="";companySensex[2]="";companySensex[3]="";
		stc="WNC";
		for(int i=0;i<4;i++)
		{
			System.out.println(company[i]);
			companySensex[i]=company[i].substring((company[i].indexOf("("))+1, (company[i].indexOf(")")));
			Log.d("meme",companySensex[i]);
			stockSymbol=companySensex[i]+"+"+stockSymbol;
			
		}
		stockSymbol=stockSymbol.substring(0, stockSymbol.lastIndexOf("+")-1);
		
		new getFinance().execute();
	}


	private void initializeMenus() {
		mMenuItemsText[0] = new Bundle();
		mMenuItemsText[0].putInt(Control.Intents.EXTRA_MENU_ITEM_ID, MENU_ITEM_0);
		mMenuItemsText[0].putString(Control.Intents.EXTRA_MENU_ITEM_TEXT, "Item 1");
		mMenuItemsText[1] = new Bundle();
		mMenuItemsText[1].putInt(Control.Intents.EXTRA_MENU_ITEM_ID, MENU_ITEM_1);
		mMenuItemsText[1].putString(Control.Intents.EXTRA_MENU_ITEM_TEXT, "Item 2");
		mMenuItemsText[2] = new Bundle();
		mMenuItemsText[2].putInt(Control.Intents.EXTRA_MENU_ITEM_ID, MENU_ITEM_2);
		mMenuItemsText[2].putString(Control.Intents.EXTRA_MENU_ITEM_TEXT, "Item 3");

		mMenuItemsIcons[0] = new Bundle();
		mMenuItemsIcons[0].putInt(Control.Intents.EXTRA_MENU_ITEM_ID, MENU_ITEM_3);
		mMenuItemsIcons[0].putString(Control.Intents.EXTRA_MENU_ITEM_ICON,
				ExtensionUtils.getUriString(mContext, R.drawable.actions_call));
		mMenuItemsIcons[1] = new Bundle();
		mMenuItemsIcons[1].putInt(Control.Intents.EXTRA_MENU_ITEM_ID, MENU_ITEM_4);
		mMenuItemsIcons[1].putString(Control.Intents.EXTRA_MENU_ITEM_ICON,
				ExtensionUtils.getUriString(mContext, R.drawable.actions_reply));
		mMenuItemsIcons[2] = new Bundle();
		mMenuItemsIcons[2].putInt(Control.Intents.EXTRA_MENU_ITEM_ID, MENU_ITEM_5);
		mMenuItemsIcons[2].putString(Control.Intents.EXTRA_MENU_ITEM_ICON,
				ExtensionUtils.getUriString(mContext, R.drawable.actions_view_in_phone));
	}

	/**
	 * Get supported control width.
	 *
	 * @param context The context.
	 * @return the width.
	 */
	public static int getSupportedControlWidth(Context context) {
		return context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_width);
	}

	private void showActiveScreen()
	{
		Log.d("myscreen", screenAt+"");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String time=prefs.getString("GoldTime","");
		Long curTime=System.currentTimeMillis();

		Log.d("answer",curTime+"");
		long timeGap = TimeUnit.MILLISECONDS.toMinutes(curTime-Long.parseLong(time));
		Log.d("answer-minutes",timeGap+"");

		switch(screenAt)
		{
		case 0: 
		//	new getFinance().execute();
			
			break;
		case 1: 
			if(timeGap > 5)
			{
				new GetGoldValue().execute();
				editor.putString("GoldTime", System.currentTimeMillis()+"");
				editor.commit();
			}
			break;
		case 2: 
			if(timeGap > 5)
			{
				new GetOil().execute();
				editor.putString("GoldTime", System.currentTimeMillis()+"");
				editor.commit();
			}
			break;
		case 3:
			//new GetSensex().execute();
			break;
		}

		showLayout(screenControl[screenAt],null);	

		switch(screenAt)
		{
		case 0: 
			if(companySensex[position]!="")
				sendText(R.id.textView1, companySensex[position].split(",")[1]+"\n"+companySensex[position].split(",")[0]);
			break;
		case 1: 
			Log.d("string value in text set","");
			sendText(R.id.textView2, inputGold.split(",")[0]);
			break;
		case 2: 
			sendText(R.id.textView3, inputOil.split(",")[0]);
			break;
		case 3:
			sendText(R.id.textView4, inputSensex);
			//sendText(R.id.textView5, inputSensexDiff);
			
			break;
		}
	}
	@Override
	public void onSwipe(int direction) 
	{
		// TODO Auto-generated method stub
		super.onSwipe(direction);
		if(direction==Control.Intents.SWIPE_DIRECTION_LEFT)
		{
			screenAt=(screenAt+1)%4;
			//sendText(R.id.textView1, screenAt+"");
		}
		if(direction==Control.Intents.SWIPE_DIRECTION_RIGHT)
		{
			screenAt=(screenAt>0)?--screenAt:3;
			//sendText(R.id.textView1, "Right");
		}
		if(screenAt==0)
		{
			if(direction==Control.Intents.SWIPE_DIRECTION_DOWN)
			{
				if(position==0)
				{
					position=4;
				}
				else
				{
					position--;
				}
			}
			if(direction==Control.Intents.SWIPE_DIRECTION_UP)
			{
				position=(position+1)%4;
			}
		}
		showActiveScreen();
	}
	/**
	 * Get supported control height.
	 *
	 * @param context The context.
	 * @return the height.
	 */
	public static int getSupportedControlHeight(Context context) {
		return context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_height);
	}

	@Override
	public void onDestroy() {
		Log.d(SampleExtensionService.LOG_TAG, "SampleControlSmartWatch onDestroy");
		mHandler = null;
	};

	@Override
	public void onStart() {
		Log.d("mymy", "onstart");
		// Nothing to do. Animation is handled in onResume.
	}

	@Override
	public void onStop() {
		// Nothing to do. Animation is handled in onPause.
	}

	@Override
	public void onResume() {
		Log.d("mymy", "onresume");
		showLayout(R.layout.smart_watchlayout2, null);
		//showActiveScreen();
	}

	@Override
	public void onPause() {}

	@Override
	public void onTouch(final ControlTouchEvent event) {
		Log.d(SampleExtensionService.LOG_TAG, "onTouch() " + event.getAction());
		if (event.getAction() == Control.Intents.TOUCH_ACTION_RELEASE) {
			Log.d(SampleExtensionService.LOG_TAG, "Toggling animation");
		}
	}

	@Override
	public void onObjectClick(final ControlObjectClickEvent event) {
		Log.d(SampleExtensionService.LOG_TAG, "onObjectClick() meme" + event.getClickType());
		if (event.getLayoutReference() != -1) {
			Log.d("My message",event.getLayoutReference()+"");
			if(event.getLayoutReference()==R.id.textView1)
			{
				sendText(R.id.textView1, inputGold);
			}
			if(event.getLayoutReference()==R.id.textView2)
			{
				sendText(R.id.textView2, "Changing text two");
			}
		}

	}

	@Override
	public void onKey(final int action, final int keyCode, final long timeStamp) {
		Log.d(SampleExtensionService.LOG_TAG, "onKey()");
		if (action == Control.Intents.KEY_ACTION_RELEASE
				&& keyCode == Control.KeyCodes.KEYCODE_OPTIONS) {
			toggleMenu();
		}
		else if (action == Control.Intents.KEY_ACTION_RELEASE
				&& keyCode == Control.KeyCodes.KEYCODE_BACK) {
			Log.d(SampleExtensionService.LOG_TAG, "onKey() - back button intercepted.");
		}
	}

	@Override
	public void onMenuItemSelected(final int menuItem) {
		Log.d(SampleExtensionService.LOG_TAG, "onMenuItemSelected() - menu item " + menuItem);
		if (menuItem == MENU_ITEM_0) {
			clearDisplay();
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					onResume();
				}
			}, 1000);
		}
	}

	private void toggleMenu() {
		if (mTextMenu) {
			showMenu(mMenuItemsIcons);
		}
		else
		{
			showMenu(mMenuItemsText);
		}
		mTextMenu = !mTextMenu;
	}

	private void setupClickables(Context context) 
	{
		
	}

	public class GetGoldValue extends AsyncTask<Void,Void,Void>
	{
		String val;
		@Override
		protected Void doInBackground(Void... params) 
		{
			try{
				URL FUrl = new URL("http://finance.yahoo.com/d/quotes.csv?s=GLD&f=l1d1t1c0");
				BufferedReader in = new BufferedReader(new InputStreamReader(FUrl.openStream()));
				val=in.readLine();
				Log.d("vaule",val);
				if (val == null)
				{
					val="no data";
					//format: Rate,Last update date,Last update time, %Change
				}
				else
				{
					inputGold=val;
					cancel(true);
				}
				Log.d("hi","ret");
				Log.d("status",""+getStatus());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}
		protected void onPostExecute(Void feed)
		{
			inputGold=val;
			Log.d("hey","comeshere2");
			Log.d("status",""+getStatus());

		}
	};

	public class GetOil extends AsyncTask<Void,Void,Void>
	{

		String val;
		@Override
		protected Void doInBackground(Void... params) 
		{
			try{
				URL FUrl = new URL("http://finance.yahoo.com/d/quotes.csv?s=OIL&f=l1d1t1c0");
				BufferedReader in = new BufferedReader(new InputStreamReader(FUrl.openStream()));
				val=in.readLine();
				Log.d("vaule",val);
				if (val == null)
				{
					val="no data";
					//format: Rate,Last update date,Last update time, %Change
				}
				else
				{
					inputOil=val;
				}
				Log.d("hi","ret");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}
		protected void onPostExecute(Void feed)
		{
			inputOil=val;
			Log.d("hey","comeshere2");

		}
	};

	public class GetSensex extends AsyncTask<Void,Void,Void>
	{
		@Override
		protected Void doInBackground(Void... params) 
		{
			try
			{
				String j = null;
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date();
				long predate=date.getTime()- 3 * 24 * 60 * 60 * 1000;
				String s1 = dateFormat.format(predate), s2 = dateFormat.format(date);
				URL FUrl = new URL("http://www.quandl.com/api/v1/datasets/YAHOO/INDEX_BSESN.csv?&trim_start="+s1+"&trim_end="+s2+"&sort_order=desc&rows=5&exclude_headers=true");
				BufferedReader in = new BufferedReader(	new InputStreamReader(FUrl.openStream()));
				String inputLine;
				Double i=0.00;
				while ((inputLine = in.readLine()) != null)
				{	
					String []ini=inputLine.split(",");
					if(i==0.00)
					{
						i+=Double.parseDouble(ini[1]);
						j=Double.parseDouble(ini[1])+"";
					}
					else
						i-=Double.parseDouble(ini[4]);

					System.out.println(Math.round(i*100)/100.0d);

				}
				Double iii=Math.round(i*100)/100.0d;
				inputSensex=j;
				inputSensexDiff=iii.toString();

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}
	};


	public class getFinance extends AsyncTask<Void,Void,Void>
	{
		String inputLine;
		@Override
		protected Void doInBackground(Void... params) 
		{
			try{
				int cnt=0;
				//stockSymbol = "XOM+BBDb.TO+JNJ+MSFT" ; 
				stags ="o0n" ;
				//String stc= "WNS";
				URL FUrl = new URL("http://finance.yahoo.com/d/quotes.csv?s=" + stockSymbol +"&f="+stags);
				BufferedReader in = new BufferedReader(new InputStreamReader(FUrl.openStream()));
				while ((inputLine = in.readLine()) != null)
				{
					Log.d("loglog", inputLine);
					companySensex[cnt]=inputLine;
					cnt++;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}

	};

}
