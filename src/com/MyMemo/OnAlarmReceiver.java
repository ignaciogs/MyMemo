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
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;

public class OnAlarmReceiver extends BroadcastReceiver {
	
	private static final int NOTIFICATION_ID = 500;
	public static Ringtone rt = null;
	
	@Override
	 public void onReceive(Context context, Intent intent) {
		 //String note_id = intent.getStringExtra("NOTE_ID");
		 String note_title = intent.getStringExtra("NOTE_TITLE");
		 String note_text = intent.getStringExtra("NOTE_TEXT");
		 
		 /* Consultamos el ringtone configurado por el usuario*/
		 PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
		 SharedPreferences lpreferences = PreferenceManager.getDefaultSharedPreferences(context);
		 String lringtone = lpreferences.getString("ringtoneAlarms", "");
		 //Ringtone rt = null;
		 if ( lringtone != "" ) {
			 rt = RingtoneManager.getRingtone(context, Uri.parse(lringtone)); 			 	   		 		     
		 } else {
			 rt = RingtoneManager.getRingtone(context, RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM)); 
		 }
		 /* Tocamos el sonido */
		 if (!rt.isPlaying()) rt.play();
		 
		 // Hacemos que el telefono vibre
		 Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		 v.vibrate(1500);
		 		 
		 /* Mostramos un notificador en la barra de tareas */
		 NotificationManager manger = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		 Notification notification = new Notification(R.drawable.icon, "MyMemo (" + context.getString(R.string.notification_text) + ")", System.currentTimeMillis());
		 Intent intentNoteWindow = new Intent(context, main.class);
		 PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intentNoteWindow, 0);
		 notification.setLatestEventInfo(context, note_title, note_text, contentIntent);
		 notification.flags = Notification.FLAG_INSISTENT;
		 notification.sound = (Uri) intent.getParcelableExtra("Ringtone");
		 notification.vibrate = (long[]) intent.getExtras().get("vibrationPatern");
		 manger.notify(NOTIFICATION_ID, notification);
	 }
}
