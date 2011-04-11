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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;

public class OnBootReceiver extends BroadcastReceiver {
	
	private static final int ALARM_REQUEST_CODE = 100;
	
	@Override
	 public void onReceive(Context context, Intent intent) {			 
		 try {
			 DataFramework.getInstance().open(context, "com.MyMemo");
	     } catch (Exception e) {
	       	e.printStackTrace();
	     }
		 List<Entity> entityList = DataFramework.getInstance().getEntityList("notes", "alarm_year > 0");
		 Entity entity;
		 Calendar newdatealarm = Calendar.getInstance();
		 Calendar datenow = Calendar.getInstance();
		 for ( int i = 0; i < entityList.size(); i++ ) {
			 entity = entityList.get(i);
			 newdatealarm.set(entity.getInt("alarm_year"), 
					  		  entity.getInt("alarm_month"), 
					  		  entity.getInt("alarm_day"),
					  		  entity.getInt("alarm_hour"), 
					  		  entity.getInt("alarm_minute"),
					  		  0);
		     if ( newdatealarm.getTimeInMillis() > datenow.getTimeInMillis() ) {
		    	 int lFlag = Integer.parseInt(Long.toString(entity.getId()));
		    	    Intent intentNewAlarm = new Intent(context, OnAlarmReceiver.class);
		    	    intentNewAlarm.putExtra("NOTE_ID", Long.toString(entity.getId()));
		    	    intentNewAlarm.putExtra("NOTE_TITLE", entity.getString("title"));
		    	    intentNewAlarm.putExtra("NOTE_TEXT", entity.getString("text"));
			    	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intentNewAlarm, lFlag);
			    	AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			    	alarmManager.set(AlarmManager.RTC_WAKEUP, 
			    					 newdatealarm.getTimeInMillis(),
			    					 pendingIntent);
		     } 
		 }
	 }
}
