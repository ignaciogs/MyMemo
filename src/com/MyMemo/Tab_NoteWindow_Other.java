/*
 * Copyright (C) 2011  Ignacio Gonzalez Sainz
 *
 * MyMemo: Programa para gestionar notas personales de texto, voz e imagenes en Android
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Ignacio Gonzalez Sainz
 * Madrid (Spain)
 * ignacio.glez.s@gmail.com
 *
 */

package com.MyMemo;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;

public class Tab_NoteWindow_Other extends Activity {
	
	static public Activity gActivityOther = null;
	private static final int ITEM_ID_MENU_MAIN_NOTEWINDOW_SAVENOTE = 1;
	private static final int ITEM_ID_MENU_MAIN_NOTEWINDOW_CANCELNOTE = 2;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_notewindow_other);
        
        gActivityOther = this;
        
        /* Asignamos los eventos a los botones de la pantalla */
        /* Check ActiveAlarm */
        CheckBox cbActiveAlarm = (CheckBox)findViewById(R.id.cbActivarAviso);
        cbActiveAlarm.setOnClickListener(OnClickActiveAlarm);
        
        if ( Main_NoteWindow.currentEntity.isInsert() ) {
        	TextView lblalarm = (TextView)findViewById(R.id.lblRemenberNote);
        	lblalarm.setText("");
        	lblalarm.setVisibility(View.GONE);
        	Main_NoteWindow.gPriorityNote = 1;
        	RefreshPriorityOff();
    		ImageView imageview = null;
    		imageview = (ImageView)findViewById(R.id.ivPriority1);
    		imageview.setImageResource(R.drawable.priority_low);    		
        } else {
        	/* Rellenamos la alarma de la nota si tiene */
            TextView lblalarm = (TextView)findViewById(R.id.lblRemenberNote);
            CheckBox cbAlarmNote = (CheckBox)findViewById(R.id.cbActivarAviso);
            if ( (Main_NoteWindow.currentEntity.isNull("alarm_day") == false) && (Main_NoteWindow.currentEntity.getInt("alarm_day") > 0) ) {
            	Calendar calendar = Calendar.getInstance();
            	calendar.set(Main_NoteWindow.currentEntity.getInt("alarm_year"), 
            			Main_NoteWindow.currentEntity.getInt("alarm_month"), 
            			Main_NoteWindow.currentEntity.getInt("alarm_day"), 
            			Main_NoteWindow.currentEntity.getInt("alarm_hour"), 
            			Main_NoteWindow.currentEntity.getInt("alarm_minute"));
            	lblalarm.setText(getString(R.string.remember_alarm) + " " + 
    							GeneralUtils.FormatDateTime(calendar.getTime(), "EEE, d MMM yyyy") + 
    							" " + getString(R.string.duration_alarm) + " " + 
    							GeneralUtils.FormatDateTime(calendar.getTime(), "HH:mm")
    			 				);
            	lblalarm.setVisibility(View.VISIBLE);
    			cbAlarmNote.setChecked(true);
            } else {
            	cbAlarmNote.setChecked(false);
            	lblalarm.setText("");
            	lblalarm.setVisibility(View.GONE);
            }
            if ( Main_NoteWindow.currentEntity.isNull("priority") == true ) {
				setPriorityTo1();
			} else {
				Main_NoteWindow.gPriorityNote = Main_NoteWindow.currentEntity.getInt("priority");
				switch (Main_NoteWindow.currentEntity.getInt("priority")) {
				case 1:
					setPriorityTo1();
					break;
				case 2:
					setPriorityTo2();
					break;
				case 3:
					setPriorityTo3();
					break;
				case 4:
					setPriorityTo4();
					break;
				case 5:
					setPriorityTo5();
					break;
				case 6:
					setPriorityTo6();
					break;
				}
			}
        }        
        ImageView imageview1 = (ImageView)findViewById(R.id.ivPriority1); 
        imageview1.setOnClickListener(ClickPriority1);
        ImageView imageview2 = (ImageView)findViewById(R.id.ivPriority2); 
        imageview2.setOnClickListener(ClickPriority2);
        ImageView imageview3 = (ImageView)findViewById(R.id.ivPriority3); 
        imageview3.setOnClickListener(ClickPriority3);
        ImageView imageview4 = (ImageView)findViewById(R.id.ivPriority4); 
        imageview4.setOnClickListener(ClickPriority4);
        ImageView imageview5 = (ImageView)findViewById(R.id.ivPriority5); 
        imageview5.setOnClickListener(ClickPriority5);
        ImageView imageview6 = (ImageView)findViewById(R.id.ivPriority6); 
        imageview6.setOnClickListener(ClickPriority6);
        
        CheckBox chkSecurity = (CheckBox)findViewById(R.id.chkSecurity);
        chkSecurity.setOnClickListener(OnClick_chkSecurity);
        chkSecurity.setChecked( Main_NoteWindow.currentEntity.getString("security").equals("S") );        
    }
	
	private void setPriorityTo1() {
		RefreshPriorityOff();
		ImageView imageview = null;
		imageview = (ImageView)findViewById(R.id.ivPriority1);
		imageview.setImageResource(R.drawable.priority_low);
		Main_NoteWindow.gPriorityNote = 1;
	}
	
	private OnClickListener ClickPriority1 = new OnClickListener()
    {	      	
		public void onClick(View v)
        {
			setPriorityTo1();
        }
    };
    
    private void setPriorityTo2() {
    	RefreshPriorityOff();
		ImageView imageview = null;
		imageview = (ImageView)findViewById(R.id.ivPriority1);
		imageview.setImageResource(R.drawable.priority_low);
		imageview = (ImageView)findViewById(R.id.ivPriority2);
		imageview.setImageResource(R.drawable.priority_low);
		Main_NoteWindow.gPriorityNote = 2;
    }
    
    private OnClickListener ClickPriority2 = new OnClickListener()
    {	      	
		public void onClick(View v)
        {
			setPriorityTo2();
        }
    };
    
    private void setPriorityTo3() {
    	RefreshPriorityOff();
		ImageView imageview = null;
		imageview = (ImageView)findViewById(R.id.ivPriority1);
		imageview.setImageResource(R.drawable.priority_low);
		imageview = (ImageView)findViewById(R.id.ivPriority2);
		imageview.setImageResource(R.drawable.priority_low);
		imageview = (ImageView)findViewById(R.id.ivPriority3);
		imageview.setImageResource(R.drawable.priority_medium);
		Main_NoteWindow.gPriorityNote = 3;
    }
    
    private OnClickListener ClickPriority3 = new OnClickListener()
    {	      	
		public void onClick(View v)
        {
			setPriorityTo3();			
        }
    };
    
    private void setPriorityTo4() {
    	RefreshPriorityOff();
		ImageView imageview = null;
		imageview = (ImageView)findViewById(R.id.ivPriority1);
		imageview.setImageResource(R.drawable.priority_low);
		imageview = (ImageView)findViewById(R.id.ivPriority2);
		imageview.setImageResource(R.drawable.priority_low);
		imageview = (ImageView)findViewById(R.id.ivPriority3);
		imageview.setImageResource(R.drawable.priority_medium);
		imageview = (ImageView)findViewById(R.id.ivPriority4);
		imageview.setImageResource(R.drawable.priority_medium);
		Main_NoteWindow.gPriorityNote = 4;
    }
    
    private OnClickListener ClickPriority4 = new OnClickListener()
    {	      	
		public void onClick(View v)
        {
			setPriorityTo4();			
        }
    };
    
    private void setPriorityTo5() {
    	RefreshPriorityOff();
		ImageView imageview = null;
		imageview = (ImageView)findViewById(R.id.ivPriority1);
		imageview.setImageResource(R.drawable.priority_low);
		imageview = (ImageView)findViewById(R.id.ivPriority2);
		imageview.setImageResource(R.drawable.priority_low);
		imageview = (ImageView)findViewById(R.id.ivPriority3);
		imageview.setImageResource(R.drawable.priority_medium);
		imageview = (ImageView)findViewById(R.id.ivPriority4);
		imageview.setImageResource(R.drawable.priority_medium);
		imageview = (ImageView)findViewById(R.id.ivPriority5);
		imageview.setImageResource(R.drawable.priority_hight);
		Main_NoteWindow.gPriorityNote = 5;
    }
    
    private OnClickListener ClickPriority5 = new OnClickListener()
    {	      	
		public void onClick(View v)
        {
			setPriorityTo5();			
        }
    };
    
    private void setPriorityTo6() {
    	RefreshPriorityOff();
		ImageView imageview = null;
		imageview = (ImageView)findViewById(R.id.ivPriority1);
		imageview.setImageResource(R.drawable.priority_low);
		imageview = (ImageView)findViewById(R.id.ivPriority2);
		imageview.setImageResource(R.drawable.priority_low);
		imageview = (ImageView)findViewById(R.id.ivPriority3);
		imageview.setImageResource(R.drawable.priority_medium);
		imageview = (ImageView)findViewById(R.id.ivPriority4);
		imageview.setImageResource(R.drawable.priority_medium);
		imageview = (ImageView)findViewById(R.id.ivPriority5);
		imageview.setImageResource(R.drawable.priority_hight);
		imageview = (ImageView)findViewById(R.id.ivPriority6);
		imageview.setImageResource(R.drawable.priority_hight);
		Main_NoteWindow.gPriorityNote = 6;
    }
    
    private OnClickListener ClickPriority6 = new OnClickListener()
    {	      	
		public void onClick(View v)
        {
			setPriorityTo6();			
        }
    };
    
    private void RefreshPriorityOff() {
    	ImageView imageview = null;
    	imageview = (ImageView)findViewById(R.id.ivPriority1);
    	imageview.setImageResource(R.drawable.priority_empty);
    	imageview = (ImageView)findViewById(R.id.ivPriority2);
    	imageview.setImageResource(R.drawable.priority_empty);
    	imageview = (ImageView)findViewById(R.id.ivPriority3);
    	imageview.setImageResource(R.drawable.priority_empty);
    	imageview = (ImageView)findViewById(R.id.ivPriority4);
    	imageview.setImageResource(R.drawable.priority_empty);
    	imageview = (ImageView)findViewById(R.id.ivPriority5);
    	imageview.setImageResource(R.drawable.priority_empty);
    	imageview = (ImageView)findViewById(R.id.ivPriority6);
    	imageview.setImageResource(R.drawable.priority_empty);
    }
    
    private OnClickListener OnClick_chkSecurity = new OnClickListener() {
		public void onClick(View v)
        {       	 	
   	 		CheckBox chkSecurity = (CheckBox)findViewById(R.id.chkSecurity);
   	 		if ( chkSecurity.isChecked() ) {
   	 			List<Entity> lListEntitys = DataFramework.getInstance().getEntityList("params");
   	 			if ( lListEntitys.size() > 0 ) {
   	 				Main_NoteWindow.gchkSecurity = "S";
   	 			} else {
   	 				chkSecurity.setChecked(false);
   	 				Main_NoteWindow.gchkSecurity = "N";
   	 				Toast.makeText(gActivityOther, R.string.msg_PasswordNoConfigure, Toast.LENGTH_LONG).show();
   	 			}
   	 		} else {
   	 			Main_NoteWindow.gchkSecurity = "N";
   	 		}       	 	
        }
    };
	
	private OnClickListener OnClickActiveAlarm = new OnClickListener() {
		public void onClick(View v)
        { 
			CheckBox cbAlarmNote = (CheckBox)findViewById(R.id.cbActivarAviso);
			if ( cbAlarmNote.isChecked() ) {
				AlertDialog.Builder alertDate = new AlertDialog.Builder(v.getContext());  
				alertDate.setTitle(getString(R.string.msg_dateAlarm));
				alertDate.setMessage(getString(R.string.msg_desdateAlarm));  
				final DatePicker datealarm = new DatePicker(gActivityOther);
		    	alertDate.setView(datealarm);
		    	alertDate.setPositiveButton(getString(R.string.text_Ok), new DialogInterface.OnClickListener() {  
		    		public void onClick(DialogInterface dialog, int whichButton) {
		    			Main_NoteWindow.gYearAlarm = datealarm.getYear();
		    			Main_NoteWindow.gMonthAlarm = datealarm.getMonth();
		    			Main_NoteWindow.gDayAlarm = datealarm.getDayOfMonth();
		    			
		    			AlertDialog.Builder alertTime = new AlertDialog.Builder(gActivityOther);  
		    			alertTime.setTitle(getString(R.string.msg_timeAlarm));
		    			alertTime.setMessage(getString(R.string.msg_destimeAlarm));  
		    	    	final TimePicker timealarm = new TimePicker(gActivityOther);
		    	    	timealarm.setIs24HourView(true);
		    	    	alertTime.setView(timealarm);
		    	    	alertTime.setPositiveButton(getString(R.string.text_Ok), new DialogInterface.OnClickListener() {  
		    	    		public void onClick(DialogInterface dialog, int whichButton) {
		    	    			Main_NoteWindow.gHourAlarm = timealarm.getCurrentHour();
		    	    			Main_NoteWindow.gMinutesAlarm = timealarm.getCurrentMinute();
		    	    			Calendar calendar = Calendar.getInstance();
		    	            	calendar.set(Main_NoteWindow.gYearAlarm, 
		    	            			Main_NoteWindow.gMonthAlarm, 
		    	            			Main_NoteWindow.gDayAlarm, 
		    	            			Main_NoteWindow.gHourAlarm, 
		    	            			Main_NoteWindow.gMinutesAlarm);
		    	            	TextView lblalarm = (TextView)findViewById(R.id.lblRemenberNote);
		    	            	lblalarm.setText(getString(R.string.remember_alarm) + " " + 
		    	            					GeneralUtils.FormatDateTime(calendar.getTime(), "EEE, d MMM yyyy") + 
		    	            			 		" " + getString(R.string.duration_alarm) + " " + 
		    	            			 		GeneralUtils.FormatDateTime(calendar.getTime(), "HH:mm")
		    	            			 		);
		    	            	lblalarm.setVisibility(View.VISIBLE);
		    	    		}
		    	    	});  
		    	    		alertTime.setNegativeButton(getString(R.string.text_Cancel), new DialogInterface.OnClickListener() {  
		    	    		public void onClick(DialogInterface dialog, int whichButton) {  
		    	    			// ha cancelado con lo que limpiamos todo  
		    	    			Main_NoteWindow.gYearAlarm = 0;
		    	    			Main_NoteWindow.gMonthAlarm = 0;
		    	    			Main_NoteWindow.gDayAlarm = 0;
		    	    			Main_NoteWindow.gHourAlarm = 0;
		    	    			Main_NoteWindow.gMonthAlarm = 0;
		    	    			CheckBox cbActiveAlarm = (CheckBox)findViewById(R.id.cbActivarAviso);
		    	    			cbActiveAlarm.setChecked(false);
		    	    			TextView lblalarm = (TextView)findViewById(R.id.lblRemenberNote);
		    	    			lblalarm.setVisibility(View.GONE);
		    	    		}  	
		    	    	});  
		    	    	alertTime.show();
		    		}

		    	});  
		    	   	alertDate.setNegativeButton(getString(R.string.text_Cancel), new DialogInterface.OnClickListener() {  
		    		public void onClick(DialogInterface dialog, int whichButton) {  
		    			// Cancelado no hacemos nada.
		    			CheckBox cbActiveAlarm = (CheckBox)findViewById(R.id.cbActivarAviso);
		    			cbActiveAlarm.setChecked(false);
		    			TextView lblalarm = (TextView)findViewById(R.id.lblRemenberNote);
    	    			lblalarm.setVisibility(View.GONE);
		    		}  	
		    	});  
		    	alertDate.show();
			} else {
				Main_NoteWindow.gYearAlarm = 0;
				Main_NoteWindow.gMonthAlarm = 0;
				Main_NoteWindow.gDayAlarm = 0;
				Main_NoteWindow.gHourAlarm = 0;
				Main_NoteWindow.gMonthAlarm = 0;
    			CheckBox cbActiveAlarm = (CheckBox)findViewById(R.id.cbActivarAviso);
    			cbActiveAlarm.setChecked(false);
    			TextView lblalarm = (TextView)findViewById(R.id.lblRemenberNote);
    			lblalarm.setVisibility(View.GONE);
			}
			
        }
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, ITEM_ID_MENU_MAIN_NOTEWINDOW_SAVENOTE, 0, R.string.text_Ok).setIcon(
				android.R.drawable.ic_menu_save).setAlphabeticShortcut('N');
		menu.add(0, ITEM_ID_MENU_MAIN_NOTEWINDOW_CANCELNOTE, 0,
				R.string.text_Cancel).setIcon(
				android.R.drawable.ic_menu_close_clear_cancel).setAlphabeticShortcut('N');
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case ITEM_ID_MENU_MAIN_NOTEWINDOW_SAVENOTE:
			Main_NoteWindow.SaveNote(false);
			return true;
		case ITEM_ID_MENU_MAIN_NOTEWINDOW_CANCELNOTE:
			Main_NoteWindow.CancelNote();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
