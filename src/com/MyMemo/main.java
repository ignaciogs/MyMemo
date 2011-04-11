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
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;

public class main extends ListActivity {

	/** Variables Definition **/
	private static final int ITEM_ID_MENU_MAIN_NEW = 1;
	private static final int ITEM_ID_MENU_MAIN_VISIONMAPA = 2;
	private static final int ITEM_ID_MENU_MAIN_PREFERENCES = 3;
	private static final int ITEM_ID_CONTEXT_MENU_MAIN_SHARE = 4;
	private static final int ITEM_ID_CONTEXT_MENU_MAIN_DELETE = 5;
	private static final int ITEM_ID_CONTEXT_MENU_AUDIO_FILE = 6;
	private static final int ITEM_ID_CONTEXT_MENU_AUDIO_FILES = 7;
	private static final int ITEM_ID_CONTEXT_MENU_PICTURE_FILE = 8;
	private static final int ITEM_ID_CONTEXT_MENU_PICTURE_FILES = 9;
	// private static final int ITEM_ID_CONTEXT_MENU_VIDEO_FILE = 10;
	// private static final int ITEM_ID_CONTEXT_MENU_VIDEO_FILES = 11;
	private static final int ITEM_ID_CONTEXT_MENU_MAIN_VIEWONMAP = 12;
	private static final int ITEM_ID_MENU_MAIN_ORDER_LIST = 15;
	private static final int ITEM_ID_MENU_MAIN_EXPORT_EXPORT = 16;
	private static final int ITEM_ID_CONTEXT_MENU_MAIN_REFRESHLOCATION = 17;

	private RowAdapter rowAdapter;
	private static Activity gActivity;
	private static AdapterContextMenuInfo infoDelete;

	public static SharedPreferences preferences;
	private static final int ALARM_REQUEST_CODE = 100;
	private String gOrderNoteList = "";
	
	//public final static Ringtone rt = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		gActivity = this;

		try {
			//DataFramework.getInstance().open(this, R.xml.tables, R.xml.initialvalues);
			DataFramework.getInstance().open(this, "com.MyMemo");
		} catch (Exception e) {
			Toast.makeText(this, "Debug: 1 -->" + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		VerifyApplicationDirectory();

		/* Quitamos nuestro posible icono del area de notificacion */
		try {
			NotificationManager manger = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
			manger.cancelAll();
		} catch (Exception e) {
			//No hacemos nada
		}

		
		/* Cargamos las preferencias de la aplicacion */
		try {
			PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
			preferences = PreferenceManager.getDefaultSharedPreferences(this);
		} catch (Exception e) {
			Toast.makeText(this, "Debug: 2 -->" + e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
		try {
			/* Consultamos la ordenación del usuario por defecto y mostramos la lista */
			String lordertype = preferences.getString("listOrderType", "0");
			if (Integer.valueOf(lordertype) == 0) { /* Sin order*/
	        	OrderMainList("");
	        } else if (Integer.valueOf(lordertype) == 1) { /* por prioridad desc*/
	        	OrderMainList(" priority desc ");
	        } else if ( Integer.valueOf(lordertype) == 2) { /* por prioridad asc*/
	        	OrderMainList(" priority asc ");
	        } else if (Integer.valueOf(lordertype) == 3) { /* por fecha desc*/
	        	OrderMainList(" date desc ");
	   	 	} else if (Integer.valueOf(lordertype) == 4) { /* por fecha asc*/
	   	 		OrderMainList(" date asc ");            		
	    	} else if (Integer.valueOf(lordertype) == 5) { /* por titulo desc*/
	        	OrderMainList(" title desc ");
	   	 	} else if (Integer.valueOf(lordertype) == 6) { /* por titulo asc*/
	   	 		OrderMainList(" title asc ");            		
	    	} 
		} catch (Exception e) {
			Toast.makeText(this, "Debug: 3 -->" + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	    		
		registerForContextMenu(getListView());
	}

	public void RefreshTitle(List<Entity> elements, Context context) {
		String desnote = "";
		if (elements.size() == 1) {
			desnote = context.getString(R.string.note_sing);
		} else {
			desnote = context.getString(R.string.note_plur);
		}
		this.setTitle(context.getString(R.string.app_name) + " - "
				+ context.getString(R.string.note_list) + " ("
				+ String.valueOf(elements.size()) + " " + desnote + ")");
	}

	public void VerifyApplicationDirectory() {
		/*
		 * Comprobamos que existe la arquitectura de directorios necesaria para
		 * la aplicacion
		 */
		File directory = new File(GlobalVar.gApplicationPath);
		if (!directory.exists() && !directory.mkdirs()) {
			try {
				throw new IOException("Path to file could not be created.");
			} catch (IOException e) {
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
		File directory2 = new File(GlobalVar.gNoteDataPath);
		if (!directory2.exists() && !directory2.mkdirs()) {
			try {
				throw new IOException("Path to file could not be created.");
			} catch (IOException e) {
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
		File directory3 = new File(GlobalVar.gNoteBackupPath);
		if (!directory3.exists() && !directory3.mkdirs()) {
			try {
				throw new IOException("Path to file could not be created.");
			} catch (IOException e) {
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
		File directory4 = new File(GlobalVar.gMailPath);
    	if (!directory4.exists() && !directory4.mkdirs()) {
    		try {
    			throw new IOException("Path to file could not be created.");
    		} catch (IOException e) {
    			Toast.makeText(this, 
    					e.toString(), 
    					Toast.LENGTH_LONG).show();   
    		}
    	}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, ITEM_ID_MENU_MAIN_NEW, 0, R.string.item_menu_new).setIcon(
				android.R.drawable.ic_menu_add).setAlphabeticShortcut('N');
		menu.add(0, ITEM_ID_MENU_MAIN_ORDER_LIST, 0, R.string.item_menu_order_list).setIcon(
				android.R.drawable.ic_menu_sort_by_size).setAlphabeticShortcut('N');
		menu.add(0, ITEM_ID_MENU_MAIN_VISIONMAPA, 0,
				R.string.item_menu_visionmapa).setIcon(
				android.R.drawable.ic_menu_mapmode).setAlphabeticShortcut('N');
		menu.add(0, ITEM_ID_MENU_MAIN_EXPORT_EXPORT, 0, R.string.item_menu_import_export)
		.setIcon(android.R.drawable.ic_menu_manage)
		.setAlphabeticShortcut('N');
		menu.add(0, ITEM_ID_MENU_MAIN_PREFERENCES, 0,
				R.string.item_menu_preferences).setIcon(
				android.R.drawable.ic_menu_preferences).setAlphabeticShortcut(
				'N');
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case ITEM_ID_MENU_MAIN_NEW:
			ShowWindowNote(-1);
			return true;
		case ITEM_ID_MENU_MAIN_VISIONMAPA:
			Intent i = new Intent(this, NoteOnMapWindow.class);
			i.putExtra(DataFramework.KEY_ID, -1);
			startActivityForResult(i, 99);
			return true;
		case ITEM_ID_MENU_MAIN_PREFERENCES:
			Intent prefe = new Intent(this, Preferences.class);
			startActivity(prefe);
			return true;
		case ITEM_ID_MENU_MAIN_EXPORT_EXPORT:
			backup_restore_notes();
			return true;
		case ITEM_ID_MENU_MAIN_ORDER_LIST:
			ChangeOrderList();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	private void ChangeOrderList () {
		new AlertDialog.Builder(this)
        .setTitle(R.string.caption_menu_ordenr_main_list)
        .setItems(R.array.menu_order_list, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which>=0) {
                    if (which==0) { /* Sin order*/
                    	OrderMainList("");
                    } else if (which==1) { /* por prioridad desc*/
                    	OrderMainList(" priority desc ");
                    } else if (which==2) { /* por prioridad asc*/
                    	OrderMainList(" priority asc ");
                    } else if (which==3) { /* por fecha desc*/
                    	OrderMainList(" date desc ");
               	 	} else if (which==4) { /* por fecha asc*/
               	 		OrderMainList(" date asc ");            		
                	} else if (which==5) { /* por titulo desc*/
                    	OrderMainList(" title desc ");
               	 	} else if (which==6) { /* por titulo asc*/
               	 		OrderMainList(" title asc ");            		
                	}
                }
            }
        }).show();				
	}
	
	private void OrderMainList(String pOrder) {
		RefreshListNotes(pOrder);
		gOrderNoteList = pOrder;
	}
	
	private void backup_restore_notes() {
		new AlertDialog.Builder(this)
        .setTitle(R.string.item_menu_import_export)
        .setItems(R.array.menu_export_import_notes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which>=0) {
                    if (which==0) { 
                    	ExportNotes();
                    } else if (which==1) {
                    	ImportNotes();
                	}
                }
            }
        }).show();
	}

	private void ExportNotes() {
		Calendar now = Calendar.getInstance();				
		String date = GeneralUtils.FormatDateTime(now.getTime(), "yyyyMMdd");
		String hour = GeneralUtils.FormatDateTime(now.getTime(), "HHmmss");
		/* Creamos el fichero de texto con las notas */
		String filename = GlobalVar.gNoteBackupPath + "Backup_MyMemo_" + date + "_" + hour; 
		String filenamedb = filename + "." + GlobalVar.gExtensionBackup;
		String filenamezip = filename + ".zip";
		try {
			DataFramework.getInstance().backup(filenamedb, false);
			//DataFramework.getInstance().backup(filenamedb);
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();   
		}
		/* Generamos el fichero comprimido con los ficheros multimedia */
		try {
			GeneralUtils.zipDir(filenamezip, GlobalVar.gNoteDataPath, true);
		} catch (Exception e) {
  			Toast.makeText(gActivity, 
      	    		e.getMessage(), 
      	            Toast.LENGTH_LONG).show();   
		}		
		Toast.makeText(this, R.string.msg_finish_export, Toast.LENGTH_LONG).show();
	}

	private void ImportNotes() {
		Intent i = new Intent(this, DirectoryBrowser.class);		
		startActivityForResult(i, 999);
	}

	private void ShowWindowNote(long pIdNote) {
		if (pIdNote > -1) {
			Entity lEntityNewNote = new Entity("notes", pIdNote);
			final long pIdNote_final = pIdNote;
			// Comprobamos si tiene la validacion de clave activada			
	    	if ( ! lEntityNewNote.isNull("security")) { 
	        	if ( lEntityNewNote.getValue("security").toString().equals("S") ) {
	        		AlertDialog.Builder alert = new AlertDialog.Builder(gActivity);  
	        		alert.setTitle(R.string.text_passwordrequired);  
	        		alert.setMessage(R.string.text_mes_passwordrequired);  
	        		final EditText input = new EditText(gActivity);
	        		alert.setView(input);  
	        		alert.setPositiveButton(R.string.text_Ok, new DialogInterface.OnClickListener() {  
	                     public void onClick(DialogInterface dialog, int whichButton) {
	                        //Ha pulsado aceptar  
	                    	 List<Entity> lListEntitys = DataFramework.getInstance().getEntityList("params");
	                    	 if ( lListEntitys.size() > 0 ) { 
	                         	Entity lEntity = new Entity("params", lListEntitys.get(0).getId());
	                         	String loldpasuser = GeneralUtils.Encrypt(input.getText().toString());
	            				String loldpasdatabase = lEntity.getString("password");
	            				if ( loldpasuser.equals(loldpasdatabase) ) {
	            					//Clave correcta
	            					ShowWindowNoteExt(pIdNote_final);
	            				} else {
	            					//Incorrecta no hacemos nada 
	            				}
	                    	 }
	                     }  
	               	});  
	        		alert.setNegativeButton(R.string.text_Cancel, new DialogInterface.OnClickListener() {  
	        			public void onClick(DialogInterface dialog, int whichButton) {  
	        				//nada  
	        			}
	        		});  
	        		alert.setCancelable(false);
	        		alert.show();
	        	} else {
	        		ShowWindowNoteExt(pIdNote);
	        	}
	    	} else {
	    		ShowWindowNoteExt(pIdNote);
	    	}
		} else {
			ShowWindowNoteExt(pIdNote);
		}
	}
	private void ShowWindowNoteExt(long pIdNote) {
		Intent i = new Intent(this, Main_NoteWindow.class);
		i.putExtra(DataFramework.KEY_ID, pIdNote);
		startActivityForResult(i, 0);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		ShowWindowNote(rowAdapter.getItemId(position));
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(R.string.caption_contextmenu);

		/*
		 * Comprobamos que menus deben salir dependiendo de los ficheros
		 * multimedia que tenga la nota
		 */
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		Long lId = rowAdapter.getItemId(info.position);
		/* Comprobamos si la nota tiene alg�n audio asociado */
		String lPath = GlobalVar.gNoteDataPath + "id_" + lId + "/Audio/";
		File directoryAudio = new File(lPath);
		if (directoryAudio.isDirectory()) {
			switch (directoryAudio.list().length) {
			case 0:
			case 1:
				menu.add(info.position, ITEM_ID_CONTEXT_MENU_AUDIO_FILE, 0,
						R.string.item_contextmenu_audiofile);
				break;
			default:
				Menu lmenu = menu.addSubMenu(info.position, 0, 0,
						R.string.item_contextmenu_audiofiles);
				for (int i = 0; i < directoryAudio.listFiles().length; i++) {
					lmenu.add(info.position, ITEM_ID_CONTEXT_MENU_AUDIO_FILES,
							0, directoryAudio.listFiles()[i].getName());
				}
			}
		}
		/* Comprobamos si la nota tiene alguna imagen asociada */
		lPath = GlobalVar.gNoteDataPath + "id_" + lId + "/Picture/";
		File directoryPicture = new File(lPath);
		if (directoryPicture.isDirectory()) {
			switch (directoryPicture.list().length) {
			case 0:
			case 1:
				menu.add(info.position, ITEM_ID_CONTEXT_MENU_PICTURE_FILE, 0,
						R.string.item_contextmenu_picturefile);
				break;
			default:
				Menu lmenu = menu.addSubMenu(info.position, 0, 0,
						R.string.item_contextmenu_picturefiles);
				for (int i = 0; i < directoryPicture.listFiles().length; i++) {
					lmenu.add(info.position,
							ITEM_ID_CONTEXT_MENU_PICTURE_FILES, 0,
							directoryPicture.listFiles()[i].getName());
				}
			}
		}
		/* Comprobamos si la nota tiene alguna imagen asociada */
		/*
		 * lPath = GlobalVar.gNoteDataPath + "id_" + lId + "/Video/"; File
		 * directoryVideo = new File(lPath); if ( directoryVideo.isDirectory() )
		 * { switch( directoryVideo.list().length ) { case 0:
		 * 
		 * case 1: menu.add(0, ITEM_ID_CONTEXT_MENU_VIDEO_FILES, 0,
		 * R.string.item_contextmenu_videofiles); default: } }
		 */
		menu.add(0, ITEM_ID_CONTEXT_MENU_MAIN_VIEWONMAP, 0,
				R.string.menu_note_map);
		menu.add(0, ITEM_ID_CONTEXT_MENU_MAIN_SHARE, 0,
				R.string.item_contextmenu_sharenote);
		menu.add(0, ITEM_ID_CONTEXT_MENU_MAIN_DELETE, 0,
				R.string.item_contextmenu_deletenote);
		menu.add(0, ITEM_ID_CONTEXT_MENU_MAIN_REFRESHLOCATION, 0,
				R.string.item_contextmenu_refreshlocation);		
	}

	public void SendNoteMail(String pSubject, String pEmailText, String pnoteid, String platitude, String plongitude) {
    	final String paSubject = pSubject;
    	final String panoteid = pnoteid;
    	
    	if ( main.preferences.getBoolean("checkbox_send_mail_location", true) == true ) {
    		pEmailText = pEmailText + "\n \n" +
    					  getString(R.string.text_mail_send_location) + " " +
    					  "http://maps.google.com/maps?q=" + platitude + 
    					  "," + plongitude + "&iwloc=A&hl=es";
    	}
    	final String paEmailText = pEmailText;
    	
    	Boolean lHaveData = false;
    	File dirPictures = new File(GlobalVar.gNoteDataPath + "id_" + panoteid + "/Picture" );    	
    	if ( dirPictures.isDirectory() ) {
    		lHaveData = dirPictures.listFiles().length > 0;
    	}
    	if ( !lHaveData ) {
    		File dirSounds = new File(GlobalVar.gNoteDataPath + "id_" + panoteid + "/Audio" );    	
        	if ( dirSounds.isDirectory() ) {
        		lHaveData = dirSounds.listFiles().length > 0;
        	}
    	}
    	if ( !lHaveData ) {
    		File dirFreeHand = new File(GlobalVar.gNoteDataPath + "id_" + panoteid + "/FreeHand" );    	
        	if ( dirFreeHand.isDirectory() ) {
        		lHaveData = dirFreeHand.listFiles().length > 0;
        	}
    	}
    	
    	if ( lHaveData ) {
    		AlertDialog.Builder alert = new AlertDialog.Builder(this);  
  		  alert.setTitle(getString(R.string.msg_AttachFile));   
  		  alert.setMessage(getString(R.string.msg_question_AttachFile));  
  		  alert.setPositiveButton(R.string.msg_affirmation, new DialogInterface.OnClickListener() {  
  			  public void onClick(DialogInterface dialog, int whichButton) {		    			
  				  /* Generamos el zip con todos los ficheros */
  				  String filename = GlobalVar.gMailPath + "MyMemo.zip";
  				  /* Eliminamos el fichero si existe*/
  				  File file = new File(filename);
  				  if ( file.exists() ) {
  					  file.delete();
  				  }
  				  /* Hacemos el zip */
  				  try {
  					  GeneralUtils.zipDir(filename, GlobalVar.gNoteDataPath + "id_" + panoteid + "/", false);
  				  } catch (Exception e) {
  		    			Toast.makeText(gActivity, 
  		        	    		e.getMessage(), 
  		        	            Toast.LENGTH_LONG).show();   
  				  }
  				  /* Mandamos el mail */
  				  final Intent emailIntent = new Intent(Intent.ACTION_SEND); 			  
  				  emailIntent.putExtra(Intent.EXTRA_SUBJECT, paSubject); 
  				  emailIntent.putExtra(Intent.EXTRA_TEXT, paEmailText);
  				  emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filename)));
  				  emailIntent.setType("application/zip");
  				  startActivity(Intent.createChooser(emailIntent, "MyMemo"));
  			  }  
  		  });  
  		  alert.setNegativeButton(R.string.msg_Denial, new DialogInterface.OnClickListener() {  
  			  public void onClick(DialogInterface dialog, int whichButton) {
  				  /* Mail sin ficheros adjuntos */
  				  final Intent emailIntent = new Intent(Intent.ACTION_SEND); 			  
  				  emailIntent.putExtra(Intent.EXTRA_SUBJECT, paSubject); 
  				  emailIntent.putExtra(Intent.EXTRA_TEXT, paEmailText);
  				  emailIntent.setType("text/plain");
  				  startActivity(Intent.createChooser(emailIntent, "MyMemo"));
  			  }  	
  		  });  
  		  alert.show();
    	} else { //No tiene ficheros
    		 /* Mail sin ficheros adjuntos */
			  final Intent emailIntent = new Intent(Intent.ACTION_SEND); 			  
			  emailIntent.putExtra(Intent.EXTRA_SUBJECT, paSubject); 
			  emailIntent.putExtra(Intent.EXTRA_TEXT, paEmailText);
			  emailIntent.setType("text/plain");
			  startActivity(Intent.createChooser(emailIntent, "MyMemo"));
    	}    	
    }
	
	public void Show_MENU_MAIN_SHARE(Entity lEntityShare, AdapterContextMenuInfo info) {		
		SendNoteMail(getString(R.string.send_mail_Subject) + " "
				+ lEntityShare.getValue("title").toString(), lEntityShare
				.getValue("text").toString(),
				String.valueOf(rowAdapter.getItemId(info.position)),
						lEntityShare.getValue("latitude").toString(),
						lEntityShare.getValue("longitude").toString()
				);	
	}	
	
	public void Show_MENU_MAIN_DELETE_NOTE(MenuItem item) {
		/* Borramos la nota de la base de datos */
		infoDelete = (AdapterContextMenuInfo) item.getMenuInfo();
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(getString(R.string.msg_DeleteNote));
		alert.setMessage(getString(R.string.msg_DesDeleteNote));
		alert.setPositiveButton(getString(R.string.text_Ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						Entity lEntity = new Entity("notes", rowAdapter
								.getItemId(infoDelete.position));
						/*
						 * Eliminamos la posible alarma que tuviera
						 * programada
						 */
						int lFlag = Integer.parseInt(Long.toString(lEntity
								.getId()));
						Intent intent = new Intent(gActivity,
								OnAlarmReceiver.class);
						PendingIntent pendingIntentDelete = PendingIntent
								.getBroadcast(gActivity,
										ALARM_REQUEST_CODE, intent, lFlag);
						AlarmManager alarmManagerDelete = (AlarmManager) getSystemService(ALARM_SERVICE);
						alarmManagerDelete.cancel(pendingIntentDelete);

						lEntity.delete();
						/* Borramos la carpeta de la nota de la sdcard */
						GeneralUtils
								.DeleteDirectory(new File(
										GlobalVar.gNoteDataPath
												+ "id_"
												+ rowAdapter
														.getItemId(infoDelete.position)));
						Toast.makeText(gActivity, R.string.msg_NoteDeleted,
								Toast.LENGTH_SHORT).show();
						RefreshListNotes(gOrderNoteList);
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
	}
	
	public void Show_MENU_PICTURE_FILE (MenuItem item) {
		String lPath = GlobalVar.gNoteDataPath + "id_"
		+ rowAdapter.getItemId(item.getGroupId()) + "/Picture/";
		lPath = lPath + new File(lPath).listFiles()[0].getName();
		Intent intent = new Intent();  
		intent.setAction(android.content.Intent.ACTION_VIEW);  
		File file = new File(lPath);  
		intent.setDataAndType(Uri.fromFile(file), "image/*");  
		startActivity(intent);
	}
	
	public void Show_MENU_PICTURE_FILES (MenuItem item) {
		String lPath2 = GlobalVar.gNoteDataPath + "id_" + rowAdapter.getItemId(item.getGroupId()) + "/Picture/"
						+ item.toString();
		Intent intentImages = new Intent();  
		intentImages.setAction(android.content.Intent.ACTION_VIEW);  
		File fileImages = new File(lPath2);  
		intentImages.setDataAndType(Uri.fromFile(fileImages), "image/*");  
		startActivity(intentImages);
	}
	
	public void Show_MENU_AUDIO_FILE (MenuItem item) {
		String lPathAudio = GlobalVar.gNoteDataPath + "id_" + rowAdapter.getItemId(item.getGroupId()) + "/Audio/";
		lPathAudio = lPathAudio + new File(lPathAudio).listFiles()[0].getName();
		MediaPlayer mp = new MediaPlayer();
		try {
			mp.setDataSource(lPathAudio);
			mp.prepare();
			mp.start();
		} catch (IOException e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		}
	}
	
	public void Show_MENU_AUDIO_FILES (MenuItem item) {
		String lPathVideos = GlobalVar.gNoteDataPath + "id_" + rowAdapter.getItemId(item.getGroupId()) + "/Audio/"
							+ item.toString();
		MediaPlayer mpVideos = new MediaPlayer();
		try {
			mpVideos.setDataSource(lPathVideos);
			mpVideos.prepare();
			mpVideos.start();
		} catch (IOException e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		}
	}
	
	public void Show_MENU_VIEWONMAP (AdapterContextMenuInfo info) {
		Intent mapa = new Intent(this, NoteOnMapWindow.class);
		mapa.putExtra(DataFramework.KEY_ID, rowAdapter.getItemId(info.position));
		startActivityForResult(mapa, 99);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		final Entity lEntityShare;
		//final Entity lEntityShare = new Entity("notes", rowAdapter.getItemId(info.position));
		final MenuItem item2 = item;
		switch (item.getItemId()) {
		case ITEM_ID_CONTEXT_MENU_MAIN_SHARE:
			lEntityShare = new Entity("notes", rowAdapter.getItemId(info.position));
			// Comprobamos si tiene la validacion de clave activada			
        	if ( ! lEntityShare.isNull("security")) { 
	        	if ( lEntityShare.getValue("security").toString().equals("S") ) {
	        		AlertDialog.Builder alert = new AlertDialog.Builder(gActivity);  
	        		alert.setTitle(R.string.text_passwordrequired);  
	        		alert.setMessage(R.string.text_mes_passwordrequired);  
	        		final EditText input = new EditText(gActivity);
	        		alert.setView(input);  
	        		alert.setPositiveButton(R.string.text_Ok, new DialogInterface.OnClickListener() {  
	                     public void onClick(DialogInterface dialog, int whichButton) {
	                        //Ha pulsado aceptar  
	                    	 List<Entity> lListEntitys = DataFramework.getInstance().getEntityList("params");
	                    	 if ( lListEntitys.size() > 0 ) { 
	                         	Entity lEntity = new Entity("params", lListEntitys.get(0).getId());
	                         	String loldpasuser = GeneralUtils.Encrypt(input.getText().toString());
	            				String loldpasdatabase = lEntity.getString("password");
	            				if ( loldpasuser.equals(loldpasdatabase) ) {
	            					//Clave correcta
	            					Show_MENU_MAIN_SHARE(lEntityShare, info);
	            				} else {
	            					//Incorrecta no hacemos nada 
	            				}
	                    	 }
	                     }  
	               	});  
	        		alert.setNegativeButton(R.string.text_Cancel, new DialogInterface.OnClickListener() {  
	        			public void onClick(DialogInterface dialog, int whichButton) {  
	        				//nada  
	        			}
	        		});  
	        		alert.setCancelable(false);
	        		alert.show();
	        	} else {
	        		Show_MENU_MAIN_SHARE(lEntityShare, info);
	        	}
        	} else {
        		Show_MENU_MAIN_SHARE(lEntityShare, info);
        	}
			return true;
		case ITEM_ID_CONTEXT_MENU_MAIN_REFRESHLOCATION:
			lEntityShare = new Entity("notes", rowAdapter.getItemId(info.position));
			// Comprobamos si tiene la validacion de clave activada			
        	if ( ! lEntityShare.isNull("security")) { 
	        	if ( lEntityShare.getValue("security").toString().equals("S") ) {
	        		AlertDialog.Builder alert = new AlertDialog.Builder(gActivity);  
	        		alert.setTitle(R.string.text_passwordrequired);  
	        		alert.setMessage(R.string.text_mes_passwordrequired);  
	        		final EditText input = new EditText(gActivity);
	        		alert.setView(input);  
	        		alert.setPositiveButton(R.string.text_Ok, new DialogInterface.OnClickListener() {  
	                     public void onClick(DialogInterface dialog, int whichButton) {
	                        //Ha pulsado aceptar  
	                    	 List<Entity> lListEntitys = DataFramework.getInstance().getEntityList("params");
	                    	 if ( lListEntitys.size() > 0 ) { 
	                         	Entity lEntity = new Entity("params", lListEntitys.get(0).getId());
	                         	String loldpasuser = GeneralUtils.Encrypt(input.getText().toString());
	            				String loldpasdatabase = lEntity.getString("password");
	            				if ( loldpasuser.equals(loldpasdatabase) ) {
	            					//Clave correcta
	            					RefreshLocationNote(rowAdapter.getItemId(info.position));
	            				} else {
	            					//Incorrecta no hacemos nada 
	            				}
	                    	 }
	                     }  
	               	});  
	        		alert.setNegativeButton(R.string.text_Cancel, new DialogInterface.OnClickListener() {  
	        			public void onClick(DialogInterface dialog, int whichButton) {  
	        				//nada  
	        			}
	        		});  
	        		alert.setCancelable(false);
	        		alert.show();
	        	} else {
	        		RefreshLocationNote(rowAdapter.getItemId(info.position));
	        	}
        	} else {
        		RefreshLocationNote(rowAdapter.getItemId(info.position));
        	}			
			return true;
		case ITEM_ID_CONTEXT_MENU_MAIN_DELETE:
			lEntityShare = new Entity("notes", rowAdapter.getItemId(info.position));
			// Comprobamos si tiene la validacion de clave activada			
        	if ( ! lEntityShare.isNull("security")) { 
	        	if ( lEntityShare.getValue("security").toString().equals("S") ) {
	        		AlertDialog.Builder alert = new AlertDialog.Builder(gActivity);  
	        		alert.setTitle(R.string.text_passwordrequired);  
	        		alert.setMessage(R.string.text_mes_passwordrequired);  
	        		final EditText input = new EditText(gActivity);
	        		alert.setView(input);  
	        		alert.setPositiveButton(R.string.text_Ok, new DialogInterface.OnClickListener() {  
	                     public void onClick(DialogInterface dialog, int whichButton) {
	                        //Ha pulsado aceptar  
	                    	 List<Entity> lListEntitys = DataFramework.getInstance().getEntityList("params");
	                    	 if ( lListEntitys.size() > 0 ) { 
	                         	Entity lEntity = new Entity("params", lListEntitys.get(0).getId());
	                         	String loldpasuser = GeneralUtils.Encrypt(input.getText().toString());
	            				String loldpasdatabase = lEntity.getString("password");
	            				if ( loldpasuser.equals(loldpasdatabase) ) {
	            					//Clave correcta
	            					Show_MENU_MAIN_DELETE_NOTE(item2);
	            				} else {
	            					//Incorrecta no hacemos nada 
	            				}
	                    	 }
	                     }  
	               	});  
	        		alert.setNegativeButton(R.string.text_Cancel, new DialogInterface.OnClickListener() {  
	        			public void onClick(DialogInterface dialog, int whichButton) {  
	        				//nada  
	        			}
	        		});  
	        		alert.setCancelable(false);
	        		alert.show();
	        	} else {
	        		Show_MENU_MAIN_DELETE_NOTE(item);
	        	}
        	} else {
        		Show_MENU_MAIN_DELETE_NOTE(item);
        	}
			return true;
		case ITEM_ID_CONTEXT_MENU_PICTURE_FILE:
			lEntityShare = new Entity("notes", rowAdapter.getItemId(info.position));
			// Comprobamos si tiene la validacion de clave activada			
        	if ( ! lEntityShare.isNull("security")) { 
	        	if ( lEntityShare.getValue("security").toString().equals("S") ) {
	        		AlertDialog.Builder alert = new AlertDialog.Builder(gActivity);  
	        		alert.setTitle(R.string.text_passwordrequired);  
	        		alert.setMessage(R.string.text_mes_passwordrequired);  
	        		final EditText input = new EditText(gActivity);
	        		alert.setView(input);  
	        		alert.setPositiveButton(R.string.text_Ok, new DialogInterface.OnClickListener() {  
	                     public void onClick(DialogInterface dialog, int whichButton) {
	                        //Ha pulsado aceptar  
	                    	 List<Entity> lListEntitys = DataFramework.getInstance().getEntityList("params");
	                    	 if ( lListEntitys.size() > 0 ) { 
	                         	Entity lEntity = new Entity("params", lListEntitys.get(0).getId());
	                         	String loldpasuser = GeneralUtils.Encrypt(input.getText().toString());
	            				String loldpasdatabase = lEntity.getString("password");
	            				if ( loldpasuser.equals(loldpasdatabase) ) {
	            					//Clave correcta
	            					Show_MENU_PICTURE_FILE(item2);
	            				} else {
	            					//Incorrecta no hacemos nada 
	            				}
	                    	 }
	                     }  
	               	});  
	        		alert.setNegativeButton(R.string.text_Cancel, new DialogInterface.OnClickListener() {  
	        			public void onClick(DialogInterface dialog, int whichButton) {  
	        				//nada  
	        			}
	        		});  
	        		alert.setCancelable(false);
	        		alert.show();
	        	} else {
	        		Show_MENU_PICTURE_FILE(item2);
	        	}
        	} else {
        		Show_MENU_PICTURE_FILE(item2);
        	}			
			return true;
		case ITEM_ID_CONTEXT_MENU_PICTURE_FILES:
			lEntityShare = new Entity("notes", rowAdapter.getItemId(item.getGroupId()));
			// Comprobamos si tiene la validacion de clave activada			
        	if ( ! lEntityShare.isNull("security")) { 
	        	if ( lEntityShare.getValue("security").toString().equals("S") ) {
	        		AlertDialog.Builder alert = new AlertDialog.Builder(gActivity);  
	        		alert.setTitle(R.string.text_passwordrequired);  
	        		alert.setMessage(R.string.text_mes_passwordrequired);  
	        		final EditText input = new EditText(gActivity);
	        		alert.setView(input);  
	        		alert.setPositiveButton(R.string.text_Ok, new DialogInterface.OnClickListener() {  
	                     public void onClick(DialogInterface dialog, int whichButton) {
	                        //Ha pulsado aceptar  
	                    	 List<Entity> lListEntitys = DataFramework.getInstance().getEntityList("params");
	                    	 if ( lListEntitys.size() > 0 ) { 
	                         	Entity lEntity = new Entity("params", lListEntitys.get(0).getId());
	                         	String loldpasuser = GeneralUtils.Encrypt(input.getText().toString());
	            				String loldpasdatabase = lEntity.getString("password");
	            				if ( loldpasuser.equals(loldpasdatabase) ) {
	            					//Clave correcta
	            					Show_MENU_PICTURE_FILES(item2);
	            				} else {
	            					//Incorrecta no hacemos nada 
	            				}
	                    	 }
	                     }  
	               	});  
	        		alert.setNegativeButton(R.string.text_Cancel, new DialogInterface.OnClickListener() {  
	        			public void onClick(DialogInterface dialog, int whichButton) {  
	        				//nada  
	        			}
	        		});  
	        		alert.setCancelable(false);
	        		alert.show();
	        	} else {
	        		Show_MENU_PICTURE_FILES(item2);
	        	}
        	} else {
        		Show_MENU_PICTURE_FILES(item2);
        	}
			return true;
		case ITEM_ID_CONTEXT_MENU_AUDIO_FILE:
			lEntityShare = new Entity("notes", rowAdapter.getItemId(info.position));
			// Comprobamos si tiene la validacion de clave activada			
        	if ( ! lEntityShare.isNull("security")) { 
	        	if ( lEntityShare.getValue("security").toString().equals("S") ) {
	        		AlertDialog.Builder alert = new AlertDialog.Builder(gActivity);  
	        		alert.setTitle(R.string.text_passwordrequired);  
	        		alert.setMessage(R.string.text_mes_passwordrequired);  
	        		final EditText input = new EditText(gActivity);
	        		alert.setView(input);  
	        		alert.setPositiveButton(R.string.text_Ok, new DialogInterface.OnClickListener() {  
	                     public void onClick(DialogInterface dialog, int whichButton) {
	                        //Ha pulsado aceptar  
	                    	 List<Entity> lListEntitys = DataFramework.getInstance().getEntityList("params");
	                    	 if ( lListEntitys.size() > 0 ) { 
	                         	Entity lEntity = new Entity("params", lListEntitys.get(0).getId());
	                         	String loldpasuser = GeneralUtils.Encrypt(input.getText().toString());
	            				String loldpasdatabase = lEntity.getString("password");
	            				if ( loldpasuser.equals(loldpasdatabase) ) {
	            					//Clave correcta
	            					Show_MENU_AUDIO_FILE(item2);
	            				} else {
	            					//Incorrecta no hacemos nada 
	            				}
	                    	 }
	                     }  
	               	});  
	        		alert.setNegativeButton(R.string.text_Cancel, new DialogInterface.OnClickListener() {  
	        			public void onClick(DialogInterface dialog, int whichButton) {  
	        				//nada  
	        			}
	        		});  
	        		alert.setCancelable(false);
	        		alert.show();
	        	} else {
	        		Show_MENU_AUDIO_FILE(item2);
	        	}
        	} else {
        		Show_MENU_AUDIO_FILE(item2);
        	}
			return true;
		case ITEM_ID_CONTEXT_MENU_AUDIO_FILES:
			lEntityShare = new Entity("notes", rowAdapter.getItemId(item.getGroupId()));
			// Comprobamos si tiene la validacion de clave activada			
        	if ( ! lEntityShare.isNull("security")) { 
	        	if ( lEntityShare.getValue("security").toString().equals("S") ) {
	        		AlertDialog.Builder alert = new AlertDialog.Builder(gActivity);  
	        		alert.setTitle(R.string.text_passwordrequired);  
	        		alert.setMessage(R.string.text_mes_passwordrequired);  
	        		final EditText input = new EditText(gActivity);
	        		alert.setView(input);  
	        		alert.setPositiveButton(R.string.text_Ok, new DialogInterface.OnClickListener() {  
	                     public void onClick(DialogInterface dialog, int whichButton) {
	                        //Ha pulsado aceptar  
	                    	 List<Entity> lListEntitys = DataFramework.getInstance().getEntityList("params");
	                    	 if ( lListEntitys.size() > 0 ) { 
	                         	Entity lEntity = new Entity("params", lListEntitys.get(0).getId());
	                         	String loldpasuser = GeneralUtils.Encrypt(input.getText().toString());
	            				String loldpasdatabase = lEntity.getString("password");
	            				if ( loldpasuser.equals(loldpasdatabase) ) {
	            					//Clave correcta
	            					Show_MENU_AUDIO_FILES(item2);
	            				} else {
	            					//Incorrecta no hacemos nada 
	            				}
	                    	 }
	                     }  
	               	});  
	        		alert.setNegativeButton(R.string.text_Cancel, new DialogInterface.OnClickListener() {  
	        			public void onClick(DialogInterface dialog, int whichButton) {  
	        				//nada  
	        			}
	        		});  
	        		alert.setCancelable(false);
	        		alert.show();
	        	} else {
	        		Show_MENU_AUDIO_FILES(item2);
	        	}
        	} else {
        		Show_MENU_AUDIO_FILES(item2);
        	}
			return true;
		case ITEM_ID_CONTEXT_MENU_MAIN_VIEWONMAP:
			lEntityShare = new Entity("notes", rowAdapter.getItemId(info.position));
			// Comprobamos si tiene la validacion de clave activada			
        	if ( ! lEntityShare.isNull("security")) { 
	        	if ( lEntityShare.getValue("security").toString().equals("S") ) {
	        		AlertDialog.Builder alert = new AlertDialog.Builder(gActivity);  
	        		alert.setTitle(R.string.text_passwordrequired);  
	        		alert.setMessage(R.string.text_mes_passwordrequired);  
	        		final EditText input = new EditText(gActivity);
	        		alert.setView(input);  
	        		alert.setPositiveButton(R.string.text_Ok, new DialogInterface.OnClickListener() {  
	                     public void onClick(DialogInterface dialog, int whichButton) {
	                        //Ha pulsado aceptar  
	                    	 List<Entity> lListEntitys = DataFramework.getInstance().getEntityList("params");
	                    	 if ( lListEntitys.size() > 0 ) { 
	                         	Entity lEntity = new Entity("params", lListEntitys.get(0).getId());
	                         	String loldpasuser = GeneralUtils.Encrypt(input.getText().toString());
	            				String loldpasdatabase = lEntity.getString("password");
	            				if ( loldpasuser.equals(loldpasdatabase) ) {
	            					//Clave correcta
	            					Show_MENU_VIEWONMAP(info);
	            				} else {
	            					//Incorrecta no hacemos nada 
	            				}
	                    	 }
	                     }  
	               	});  
	        		alert.setNegativeButton(R.string.text_Cancel, new DialogInterface.OnClickListener() {  
	        			public void onClick(DialogInterface dialog, int whichButton) {  
	        				//nada  
	        			}
	        		});
	        		alert.setCancelable(false);
	        		alert.show();
	        	} else {
	        		Show_MENU_VIEWONMAP(info);
	        	}
        	} else {
        		Show_MENU_VIEWONMAP(info);
        	}			
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	private void RefreshLocationNote(long pid) {
		Entity lEntity = new Entity("notes", pid);
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
				lEntity.setValue("latitude", location.getLatitude());
				lEntity.setValue("longitude", location.getLongitude());
				lEntity.setValue("provider", provider);
				lEntity.save();
			}
		} else {
			lEntity.setValue("latitude", 0);
			lEntity.setValue("longitude", 0);
			lEntity.save();
			Toast.makeText(gActivity, gActivity.getString(R.string.Disabled_Location), Toast.LENGTH_LONG).show();
		}
	}

	public void RefreshListNotes(String pOrder) {
		/* Refrescamos la lista general de notas */
		List<Entity> ListEntitys = null;
		try {
			ListEntitys = DataFramework.getInstance().getEntityList(
					"notes", "", pOrder);
		} catch (Exception e) {
			Toast.makeText(this, "Debug: 4 -->" + e.getMessage(), Toast.LENGTH_LONG).show();
		}
		try {
			rowAdapter = new RowAdapter(this, ListEntitys);
			setListAdapter(rowAdapter);
			RefreshTitle(ListEntitys, this);
		} catch (Exception e) {
			Toast.makeText(this, "Debug: 5 -->" + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			RefreshListNotes(gOrderNoteList); /*
								 * Refrescamos la lista de notas despues de los
								 * cambios
								 */
		default:
			RefreshListNotes(gOrderNoteList); /*
								 * refrescamos también hasta que mejoremos el
								 * código
								 */
			/*
			 * No hacemos nada ya que no necesitamos refrescar ya que ha
			 * cancelado los cambios
			 */
			break;
		}
	}
}