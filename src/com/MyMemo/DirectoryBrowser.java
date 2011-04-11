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
import java.io.FilenameFilter;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;

public class DirectoryBrowser extends ListActivity{
	
	private String[] filenames = null;  
	private static final int ITEM_ID_CONTEXT_MENU_BROWSER_DELETE = 1;
	private Activity gActivity = null;
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.directorybrowser);
        setTitle(getString(R.string.caption_browser));
        
        gActivity = this;
        /* Indicamos que la lista tiene ContextMenu */
        registerForContextMenu(getListView());
        
        File file = new File(GlobalVar.gNoteBackupPath);
        filenames = file.list(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
             return filename.toLowerCase().endsWith("." + GlobalVar.gExtensionBackup);
            }
        });
        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.file_list_row, filenames);
        setListAdapter(fileList);
    }
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		final int lposition = position;
		final View lv = v; 
		AlertDialog.Builder alert = new AlertDialog.Builder(this);  
		  alert.setTitle(getString(R.string.msg_RestoreNotes));   
		  alert.setMessage(getString(R.string.msg_Des_RestoreNotes));  
		  alert.setPositiveButton(R.string.text_Ok, new DialogInterface.OnClickListener() {  
			  public void onClick(DialogInterface dialog, int whichButton) {		    			
				  File filecopia = new File(GlobalVar.gNoteBackupPath + filenames[lposition]);
				  if ( filecopia.exists() ) {
					  /* Eliminamos los datos actuales de la BD */
					  List<Entity> entityList = DataFramework.getInstance().getEntityList("notes");
					  Entity entity;
					  for ( int i = 0; i < entityList.size(); i++ ) {
						  entity = entityList.get(i);
						  
						  /*
						   * Eliminamos la posible alarma que tuviera
						   * programada
						   */
						  int lFlag = Integer.parseInt(Long.toString(entity
								  .getId()));
						  Intent intent = new Intent(lv.getContext(),
								  OnAlarmReceiver.class);
						  PendingIntent pendingIntentDelete = PendingIntent
								.getBroadcast(lv.getContext(),
											100, intent, lFlag);
						  AlarmManager alarmManagerDelete = (AlarmManager) getSystemService(ALARM_SERVICE);
						  alarmManagerDelete.cancel(pendingIntentDelete);

						  entity.delete();
					  }
					  /* Recuperamos los registros de la base de datos */
					  try {
						  DataFramework.getInstance().restore(GlobalVar.gNoteBackupPath + filenames[lposition]);
					  } catch (Exception e) {
						  Toast.makeText(lv.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();   
					  }
					  /*try { 
						  FileInputStream fis = new FileInputStream(GlobalVar.gNoteBackupPath + filenames[lposition]);
						  BufferedInputStream bis = new BufferedInputStream(fis);
						  DataInputStream dis = new DataInputStream(bis);
						  String line = "";
						  while (dis.available() != 0) {
							  line = dis.readLine();
							  Entity newEntity = new Entity("notes");
							  newEntity.setValue("title", line.split(";")[0]);
							  newEntity.setValue("text", line.split(";")[1]);
							  newEntity.setValue("date", line.split(";")[2]);
							  newEntity.setValue("latitude", line.split(";")[3]);
							  newEntity.setValue("longitude", line.split(";")[4]);
							  newEntity.setValue("provider", line.split(";")[5]);
							  newEntity.setValue("alarm_day", line.split(";")[6]);
							  newEntity.setValue("alarm_month", line.split(";")[7]);
							  newEntity.setValue("alarm_year", line.split(";")[8]);
							  newEntity.setValue("alarm_hour", line.split(";")[9]);
							  newEntity.setValue("alarm_minute", line.split(";")[10]);
							  newEntity.save();
						  }
						  fis.close();
						  bis.close();
						  dis.close();
					  } catch (IOException e) {
					      e.printStackTrace();
					  }*/
					  /* Volvemos a programar las notas de aviso */
					  List<Entity> entityListAlarms = DataFramework.getInstance().getEntityList("notes", "alarm_year > 0");
						 Entity entityAlarm;
						 Calendar newdatealarm = Calendar.getInstance();
						 Calendar datenow = Calendar.getInstance();
						 for ( int i = 0; i < entityListAlarms.size(); i++ ) {
							 entityAlarm = entityListAlarms.get(i);
							 newdatealarm.set(entityAlarm.getInt("alarm_year"), 
									 		entityAlarm.getInt("alarm_month"), 
									 		entityAlarm.getInt("alarm_day"),
									 		entityAlarm.getInt("alarm_hour"), 
									 		entityAlarm.getInt("alarm_minute"),
									  		  0);
						     if ( newdatealarm.getTimeInMillis() > datenow.getTimeInMillis() ) {
						    	 int lFlag = Integer.parseInt(Long.toString(entityAlarm.getId()));
						    	    Intent intentNewAlarm = new Intent(lv.getContext(), OnAlarmReceiver.class);
						    	    intentNewAlarm.putExtra("NOTE_ID", Long.toString(entityAlarm.getId()));
						    	    intentNewAlarm.putExtra("NOTE_TITLE", entityAlarm.getString("title"));
						    	    intentNewAlarm.putExtra("NOTE_TEXT", entityAlarm.getString("text"));
							    	PendingIntent pendingIntent = PendingIntent.getBroadcast(lv.getContext(), 100, intentNewAlarm, lFlag);
							    	AlarmManager alarmManager = (AlarmManager) lv.getContext().getSystemService(Context.ALARM_SERVICE);
							    	alarmManager.set(AlarmManager.RTC_WAKEUP, 
							    					 newdatealarm.getTimeInMillis(),
							    					 pendingIntent);
						     } 
						 }
					  /* Borramos los datos de las notas en la sdcard */
					  GeneralUtils.DeleteDirectory(new File(GlobalVar.gNoteDataPath));
					  /* Descomprimimos el fichero de backup de datos multimedia */
					  String filezip = GlobalVar.gNoteBackupPath + GeneralUtils.getFileNameWithoutExtension(filecopia.getAbsolutePath()) + ".zip";
					  try {
						  GeneralUtils.unzip(new File(filezip), new File("/"));
					  } catch (Exception e) {
					    	e.printStackTrace();
					  }
					  Toast.makeText(lv.getContext(), getString(R.string.msg_FinishImport), Toast.LENGTH_LONG).show();
				  } else {
					  Toast.makeText(lv.getContext(), getString(R.string.msg_FileNotFound), Toast.LENGTH_LONG).show();
				  }
			  }  
		  });  
		  alert.setNegativeButton(R.string.text_Cancel, new DialogInterface.OnClickListener() {  
			  public void onClick(DialogInterface dialog, int whichButton) {
				  /* No hacemos nada */
			  }  	
		  });  
		  alert.show();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(R.string.caption_contextmenu_browser);
		menu.add(0, ITEM_ID_CONTEXT_MENU_BROWSER_DELETE, 0,
				R.string.item_contextmenu_deletenote);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
			.getMenuInfo();
		switch (item.getItemId()) {
		case ITEM_ID_CONTEXT_MENU_BROWSER_DELETE:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle(getString(R.string.msg_DeleteBackup));
			alert.setMessage(getString(R.string.msg_DesDeleteBackup));
			alert.setPositiveButton(getString(R.string.text_Ok),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String filename = filenames[info.position];
							filename = GeneralUtils.getFileNameWithoutExtension(filename);
							new File(GlobalVar.gNoteBackupPath + filename + "." + GlobalVar.gExtensionBackup).delete();
							new File(GlobalVar.gNoteBackupPath + filename + ".zip").delete();
							Toast.makeText(gActivity, getString(R.string.msg_Backup_Deleted), Toast.LENGTH_LONG).show();
							RefreshListBackups();
						}
					});
			alert.setNegativeButton(getString(R.string.text_Cancel),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// Cancelado no hacemos nada
						}
					});
			alert.show();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	private void RefreshListBackups() {
		File file = new File(GlobalVar.gNoteBackupPath);
        filenames = file.list(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
             return filename.toLowerCase().endsWith("." + GlobalVar.gExtensionBackup);
            }
        });
        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.file_list_row, filenames);
        setListAdapter(fileList);
	}
}
