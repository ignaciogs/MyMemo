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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;

/**
 * @author ignacio
 *
 */
public class Main_NoteWindow extends TabActivity {
	
	static private TabHost mTabHost;
	private Resources mResources;
	private static final int ALARM_REQUEST_CODE = 100;
	static public Entity currentEntity;
	
	static public int gYearAlarm = 0;
	static public int gMonthAlarm = 0;
	static public int gDayAlarm = 0;
	static public int gHourAlarm = 0;
	static public int gMinutesAlarm = 0;
	static public String gTitleNote = "";
	static public String gTextNote = "";
	static public int gPriorityNote = 1;
	static public Bitmap mBitmap = null;
	static public boolean gActiveFreeHand = false;
	static public String gchkSecurity = "N";	
	 
	static private Activity gActivity = null;
	static private boolean gCancelNote = false;
	
	@Override
	public void finish() {
		if ( (main.preferences.getBoolean("checkbox_autosave", true) == true) && (gCancelNote == false) ) {
			SaveNote(true);
		}
		super.finish();
	}
		
		@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_notewindow);
         
        mTabHost = getTabHost();       
        mResources = getResources();
        gActivity = this;
         
        addTabGeneral();
        addTabMultimedia();
        addTabFreehand();
        addTabOther();        
        
        mTabHost.setCurrentTab(0);   
        gchkSecurity = "N";
        
        Bundle extras = getIntent().getExtras();
        if ( extras.getLong(DataFramework.KEY_ID) != -1 && extras.getLong(DataFramework.KEY_ID) != 0	) { /* Esta en edicion */
        	currentEntity = new Entity("notes", extras.getLong(DataFramework.KEY_ID));        	
        	/* Cargamos en los objetos de la pantalla los valores de la nota */
            /* Titulo de la nota */
            TextView txtTitleNote = (TextView) Tab_NoteWindow_General.gActivityGeneral.findViewById(R.id.txtTitulo);        
            txtTitleNote.setText(currentEntity.getValue("title").toString());
            gTitleNote = currentEntity.getValue("title").toString();
            /* Texto de la nota */
            if ( currentEntity.isNull("text") == false ) {
            	TextView txtTextNote = (TextView) Tab_NoteWindow_General.gActivityGeneral.findViewById(R.id.txtText);        
                txtTextNote.setText(currentEntity.getValue("text").toString());
                gTextNote = currentEntity.getValue("text").toString();
            }                        
            if ( (Main_NoteWindow.currentEntity.isNull("alarm_day") == false) && (Main_NoteWindow.currentEntity.getInt("alarm_day") > 0) ) {
            	gYearAlarm = currentEntity.getInt("alarm_year");
            	gMonthAlarm = currentEntity.getInt("alarm_month");
            	gDayAlarm = currentEntity.getInt("alarm_day");
            	gHourAlarm = currentEntity.getInt("alarm_hour");
            	gMinutesAlarm = currentEntity.getInt("alarm_minute");
            }
            
            if ( currentEntity.isNull("priority") == true ) {
            	gPriorityNote = 1;
            } else {
            	gPriorityNote = currentEntity.getInt("priority");
            }
            
            gchkSecurity = "N";
            if ( ! currentEntity.isNull("security") ) {
              if ( currentEntity.getString("security").equals("S") ) {
            	  gchkSecurity = "S";
              }
            }
        } else {
        	/* Quiere dar de alta una nueva con lo que creamos una nueva */        	
        	currentEntity = new Entity("notes");
        	gPriorityNote = 1;
        	gTitleNote = "";
        	gTextNote = "";
        	gActiveFreeHand = false;        	
        }         
    }	
	
	static public void SaveNote(boolean pFromAutoSave) {
		final boolean lFromAutoSave = pFromAutoSave;
		gCancelNote = false;
		
		if ( currentEntity.isInsert() ) {
			SaveWhiteNote();
		}
		if ( gTitleNote != "" ) {
			currentEntity.setValue("title", gTitleNote);
		} else {
			currentEntity.setValue("title", gActivity.getString(R.string.automaticTitle));
		}
		currentEntity.setValue("text", gTextNote);
		Calendar datenote = Calendar.getInstance();
		currentEntity.setValue("date", datenote.getTimeInMillis());
		currentEntity.setValue("priority", gPriorityNote);
		
		/* Guardamos la posible nota de mano alzada */
		if ( gActiveFreeHand ) {
			if ( mBitmap != null ) {
				try{
					String lPath = GlobalVar.gNoteDataPath + "id_" + currentEntity.getId() + "/FreeHand";
					String lfile = GlobalVar.gNoteDataPath + "id_" + currentEntity.getId() + "/FreeHand/FreeHand.jpg";				
					File directory = new File(lPath);
	            	if (!directory.exists() && !directory.mkdirs()) {	
	            		Toast.makeText(gActivity, "Path to file could not be created.", Toast.LENGTH_LONG).show();
	           	    }
					OutputStream out = gActivity.getContentResolver().openOutputStream(Uri.fromFile(new File(lfile)));
					mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
				}catch(IOException e){
				}
			}
		} else { //Eliminamos el fichero
			String lfilename = GlobalVar.gNoteDataPath + "id_" + currentEntity.getId() + "/FreeHand/FreeHand.jpg";
			File lfilehand = new File(lfilename);
			if ( lfilehand.exists() ) {
				lfilehand.delete();
			}
		}
		

		/* Consultamos la posicion geografica desde donde se escribe la nota */
		if ( main.preferences.getBoolean("checkbox_save_location", true) == true ) {
			if ( currentEntity.isUpdate() ) { /* esta actualizando con lo que le preguntamos si desea actualizar la posicion de la nota*/
				if ( currentEntity.getFloat("latitude") > 0 && pFromAutoSave == false ) { /* Ya tiene una latitud */
					AlertDialog.Builder alert = new AlertDialog.Builder(gActivity);  
			    	alert.setTitle(gActivity.getString(R.string.msg_updateLocalization));  
			    	alert.setMessage(gActivity.getString(R.string.msg_desupdateLocalization));  
			    	alert.setPositiveButton(gActivity.getString(R.string.text_Ok), new DialogInterface.OnClickListener() {  
			    		public void onClick(DialogInterface dialog, int whichButton) {  
			    			SaveLocationinNote();
							SaveOtherFieldsNote(lFromAutoSave);
			    		}  
			    	});  
			    	alert.setNegativeButton(gActivity.getString(R.string.text_Cancel), new DialogInterface.OnClickListener() {  
			    		public void onClick(DialogInterface dialog, int whichButton) {  
			    			// Cancelado no hacemos nada con la posicion.
			    			SaveOtherFieldsNote(lFromAutoSave);
			    		}  	
			    	});  
			    	alert.show();
				} else {
					SaveLocationinNote();
					SaveOtherFieldsNote(pFromAutoSave);
				}					
			} else {
				SaveLocationinNote();
				SaveOtherFieldsNote(pFromAutoSave);
			}
		} else {
			currentEntity.setValue("latitude", 0);
			currentEntity.setValue("longitude", 0);
			SaveOtherFieldsNote(pFromAutoSave);
		}
	}
	
	static public void SaveOtherFieldsNote(boolean pFromAutoSave) {
		if ( gDayAlarm > 0 ) {
			currentEntity.setValue("alarm_day", gDayAlarm);
			currentEntity.setValue("alarm_month", gMonthAlarm);
			currentEntity.setValue("alarm_year", gYearAlarm);
			currentEntity.setValue("alarm_hour", gHourAlarm);
			currentEntity.setValue("alarm_minute", gMinutesAlarm);
			
			Calendar date = Calendar.getInstance();
	    	date.set(gYearAlarm, 
	    			gMonthAlarm, 
	    			gDayAlarm,
	    			gHourAlarm, 
	    			gMinutesAlarm,
	    			0);
	    	
	    	Calendar datenow = Calendar.getInstance();
	    	if ( date.getTimeInMillis() > datenow.getTimeInMillis() ) {
	    		/*Programamos el aviso */
				int lFlag = Integer.parseInt(Long.toString(currentEntity.getId()));
				Intent intent = new Intent(gActivity, OnAlarmReceiver.class);
		    	intent.putExtra("NOTE_ID", Long.toString(currentEntity.getId()));
		    	intent.putExtra("NOTE_TITLE", currentEntity.getString("title"));
		    	intent.putExtra("NOTE_TEXT", currentEntity.getString("text"));
		    	PendingIntent pendingIntent = PendingIntent.getBroadcast(gActivity, ALARM_REQUEST_CODE, intent, lFlag);
		    	AlarmManager alarmManager = (AlarmManager) gActivity.getSystemService(ALARM_SERVICE);
		    		    	
		    	alarmManager.set(AlarmManager.RTC_WAKEUP, 
		    					//System.currentTimeMillis() + (5 * 1000),
		    					date.getTimeInMillis(),
		    					pendingIntent);
	    	}
		} else {
			currentEntity.setValue("alarm_day", 0);
			currentEntity.setValue("alarm_month", 0);
			currentEntity.setValue("alarm_year", 0);
			currentEntity.setValue("alarm_hour", 0);
			currentEntity.setValue("alarm_minute", 0);
			/* Desactivamos la posible alamrma por si estaba activada */
			int lFlag = Integer.parseInt(Long.toString(currentEntity.getId()));
			Intent intent = new Intent(gActivity, OnAlarmReceiver.class);
	    	PendingIntent pendingIntentDelete = PendingIntent.getBroadcast(gActivity, ALARM_REQUEST_CODE, intent, lFlag);
	    	AlarmManager alarmManagerDelete = (AlarmManager) gActivity.getSystemService(ALARM_SERVICE);
	    	alarmManagerDelete.cancel(pendingIntentDelete); 
		}
		
		currentEntity.setValue("security", gchkSecurity);
		
		if ( currentEntity.isInsert() ) {
			/* Eliminamos la posible carpeta que haya podido quedar despues de desistalar e intalar la aplicación */
			currentEntity.save();
			GeneralUtils.DeleteDirectory(new File(GlobalVar.gNoteDataPath + "id_" + currentEntity.getId()));
		} else {
			currentEntity.save();
		}
		
    	/* Creamos el directorio fisico para guardar los ficheros multimedia de la nota */
    	String lPath = GlobalVar.gNoteDataPath + "id_" + currentEntity.getId() + "/";
    	File directory = new File(lPath);        	
    	if (!directory.exists() && !directory.mkdirs()) {
    		try {
    			throw new IOException("Path to file could not be created.");
    		} catch (IOException e) {
    			Toast.makeText(gActivity, e.toString(), Toast.LENGTH_LONG).show();   
    		}
    	}    	
    	
    	gActivity.setResult(RESULT_OK);
    	if ( pFromAutoSave == false ) {
    		Toast.makeText(gActivity, R.string.msg_NoteSaved, Toast.LENGTH_LONG).show();
    		gActivity.finish();
    	}
    }
	
	static public void SaveLocationinNote() {
    	LocationManager locationmanager = (LocationManager)gActivity.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria(); 
		criteria.setAccuracy(Criteria.ACCURACY_FINE); 
		criteria.setAltitudeRequired(false); 
		criteria.setBearingRequired(false); 
		criteria.setCostAllowed(true); 
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = locationmanager.getBestProvider(criteria,true);
		Location location = null;
		if (provider != null) { 
			location = locationmanager.getLastKnownLocation(provider);
			if ( location != null ) {
				currentEntity.setValue("latitude", location.getLatitude());
				currentEntity.setValue("longitude", location.getLongitude());
				currentEntity.setValue("provider", provider);
			}
		} else {
			currentEntity.setValue("latitude", 0);
			currentEntity.setValue("longitude", 0);
			Toast.makeText(gActivity, gActivity.getString(R.string.Disabled_Location), Toast.LENGTH_LONG).show();
		}
    }

	static public void CancelNote() {
		gActivity.setResult(RESULT_CANCELED);
		gCancelNote = true;
    	gActivity.finish();
	}
	
	private void addTabGeneral() {
    	Intent intent = new Intent(this, Tab_NoteWindow_General.class);
        TabSpec spec = mTabHost.newTabSpec("Texto Tab1");
        spec.setIndicator(mResources.getString(R.string.Caption_Tab_General), mResources
                .getDrawable(android.R.drawable.ic_menu_agenda));
        spec.setContent(intent);
        mTabHost.addTab(spec);
  
    }
	private void addTabMultimedia() {
    	Intent intent = new Intent(this, Tab_NoteWindow_Multimedia.class);
        TabSpec spec = mTabHost.newTabSpec("Texto Tab2");
        spec.setIndicator(mResources.getString(R.string.Caption_Tab_Multimedia), mResources
                .getDrawable(android.R.drawable.ic_menu_gallery));
        spec.setContent(intent);
        mTabHost.addTab(spec);
  
    }
	private void addTabOther() {
    	Intent intent = new Intent(this, Tab_NoteWindow_Other.class);
        TabSpec spec = mTabHost.newTabSpec("Texto Tab3");
        spec.setIndicator(mResources.getString(R.string.Caption_Tab_Other), mResources
                .getDrawable(android.R.drawable.ic_menu_view));
        spec.setContent(intent);
        mTabHost.addTab(spec);
  
    }
	private void addTabFreehand() {
    	Intent intent = new Intent(this, Tab_NoteWindow_Freehand.class);
        TabSpec spec = mTabHost.newTabSpec("Texto Tab4");
        spec.setIndicator(mResources.getString(R.string.Caption_Tab_FreeHand), mResources
                .getDrawable(android.R.drawable.ic_menu_edit));
        spec.setContent(intent);
        mTabHost.addTab(spec);
  
    }
	static public void SaveWhiteNote() {
		if ( gTitleNote != "" ) {
			currentEntity.setValue("title", gTitleNote);
		} else {
			currentEntity.setValue("title", gActivity.getString(R.string.automaticTitle));
		}
   		currentEntity.setValue("text", gTextNote);
   		currentEntity.setValue("priority", gPriorityNote);
    	Calendar datenote = Calendar.getInstance();
		currentEntity.setValue("date", datenote.getTimeInMillis());
		currentEntity.save();
		/* Eliminamos la posible carpeta que halla podido quedar después de desistalar e intalar la aplicación */
		GeneralUtils.DeleteDirectory(new File(GlobalVar.gNoteDataPath + "id_" + currentEntity.getId()));
    }

}
