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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Tab_NoteWindow_Multimedia extends Activity {
	
	private static final int ITEM_ID_MENU_MAIN_NOTEWINDOW_SAVENOTE = 1;
	private static final int ITEM_ID_MENU_MAIN_NOTEWINDOW_CANCELNOTE = 2;
	private static final int ACTIVITY_RECORD_SOUND = 3;
	private static final int ACTIVITY_CAPTURE_PICTURE = 4;
	private static final int ITEM_ID_MENU_MAIN_NOTEWINDOW_ADDPICTURE = 5;
	public String gimages[];
	public int gindeximage = 0;
	
	private InputStream is = null;
	private BufferedInputStream bis = null;
	private Bitmap bm = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_notewindow_multimedia);
               
        fillComboBoxAudio(); /* rellenamos el combo de audios */
        fillImageGallery(); /* rellenamos la lista de imagenes */
        fillComboBoxVideo(); /* rellenamos el combo de videos */     
        
        /* Asignamos los eventos a los botones de la pantalla */
        /* Boton de opciones de ficheros de sonido */
        ImageButton btnOptionSound = (ImageButton)findViewById(R.id.btnOptionSoundNote);
        btnOptionSound.setOnClickListener(OnClickOptionSoundNote);
        /* Boton de opciones de ficheros de imagenes*/
        //ImageButton btnOptionPhotoNote = (ImageButton)findViewById(R.id.btnOptionPhotoNote);
        //btnOptionPhotoNote.setOnClickListener(OnClickOptionPhotoNote);
        /* Boton de opciones de ficheros de video*/
        Button btnOptionVideoNote = (Button)findViewById(R.id.btnOptionVideoNote);
        btnOptionVideoNote.setOnClickListener(OnClickOptionVideoNote);

        ImageView image = (ImageView) findViewById(R.id.ImageViewGallery);
        image.setOnClickListener(OnClickOptionPhotoNote);        
                
        ImageButton btnprior = (ImageButton)findViewById(R.id.btn_prior_image);
		btnprior.setImageResource(android.R.drawable.ic_media_previous);
		btnprior.setOnClickListener(OnClickPriorImage);		
		
		ImageButton btnnext = (ImageButton)findViewById(R.id.btn_next_image);
		btnnext.setImageResource(android.R.drawable.ic_media_next);
		btnnext.setOnClickListener(OnClickNextImage); 
    }
	
	public void ShowImage(String[] pimages, int pindex) {		
        try {    
        	if ( pindex > 0 ) {
        		is = null;
            	bis = null;
            	bm = null;
            	is = new FileInputStream(new File(GlobalVar.gNoteDataPath + "id_" + 
            								Main_NoteWindow.currentEntity.getId()  + "/Picture/" + gimages[pindex - 1]));
            	bis = new BufferedInputStream(is);
            	
            	BitmapFactory.Options options=new BitmapFactory.Options();
            	options.inSampleSize = 10;
            	//Bitmap preview_bitmap=BitmapFactory.decodeStream(is,null,options);
            	bm = BitmapFactory.decodeStream(bis, null, options);
            	//bis.reset();
            	bis.close();
            	is.close();
            	ImageView image = (ImageView) findViewById(R.id.ImageViewGallery);
            	image.destroyDrawingCache();
            	image.setImageBitmap(bm);
            	//image.setScaleType(ScaleType.CENTER_INSIDE);        	
            	TextView textview = (TextView)findViewById(R.id.lblPictue_Parcial);
        		textview.setText("  " + String.valueOf(gindeximage));
        	}
        } catch (IOException e) {
        	Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
       }
	}
	
	public void fillComboBoxAudio () {
    	String lPath = GlobalVar.gNoteDataPath + "id_" + Main_NoteWindow.currentEntity.getId() + "/Audio/FileAudio_";
    	File directory = new File(lPath).getParentFile();
    	
    	if ( directory.list() != null ) {
    		ArrayAdapter<String> Adapter = new ArrayAdapter<String> (this,   
					android.R.layout.simple_spinner_item, 
					directory.list()
					); 
    		Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		Spinner cmbAudios = (Spinner) this.findViewById(R.id.cmbAudios);
    		cmbAudios.setAdapter(Adapter);
    	}    	    	
    }
	
	public void fillImageGallery() {
		gimages = null;
		gindeximage = 0;
		String lPath = GlobalVar.gNoteDataPath + "id_" + Main_NoteWindow.currentEntity.getId()  + "/Picture";
    	File directory = new File(lPath);
    	if ( directory.list() != null ) {
    		gimages = directory.list();
    		if (gimages.length > 0) {
    			gindeximage = 1;
        		ShowImage(gimages, gindeximage);
        		
        		TextView textview = (TextView)findViewById(R.id.lblPictue_Parcial);
        		textview.setText("  1");
        		TextView textviewtotal = (TextView)findViewById(R.id.lblPictue_Total);
        		textviewtotal.setText(String.valueOf(gimages.length));
    		} else {
    			setWhitePicture();
    		}
    		
    	} else {
    		setWhitePicture();
    	}
    }
	
	public void setWhitePicture () {
		TextView textview = (TextView)findViewById(R.id.lblPictue_Parcial);
		textview.setText(" 0");
		TextView textviewtotal = (TextView)findViewById(R.id.lblPictue_Total);
		textviewtotal.setText("0");
		ImageView imageview = (ImageView)findViewById(R.id.ImageViewGallery);
		imageview.setImageResource(R.drawable.nopicture);
	}
    
    public void fillComboBoxVideo() {
    	String lPath = GlobalVar.gNoteDataPath + "id_" + Main_NoteWindow.currentEntity.getId() + "/Video/FileVideo_";
    	File directory = new File(lPath).getParentFile();
    	
    	if ( directory.list() != null ) {
    		ArrayAdapter<String> Adapter = new ArrayAdapter<String> (this,   
					android.R.layout.simple_spinner_item, 
					directory.list()
					); 
    		Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		Spinner cmbVideo = (Spinner) this.findViewById(R.id.cmbVideo);
    		cmbVideo.setAdapter(Adapter);
    	}    	    	
    }
    
    private OnClickListener OnClickOptionSoundNote = new OnClickListener()
    {	      	
		public void onClick(View v)
        { 			
			new AlertDialog.Builder(v.getContext())
            .setTitle(R.string.caption_menu_sound_notewindow)
            .setItems(R.array.menu_audio_notewindow, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which>=0) {
                        if (which==0) {
                       	 PlayAudio(); 
                        } else if (which==1) {
                       	 AddNewAudio();
                    	 } else if (which==2) {
                    		DeleteAudio();
                    	 } else if (which==3) {
                    		RenameFileAudio();
                   	 }
                    }
                }
            }).show();			
        }
    };
    
    private void RenameFileAudio () {
    	Spinner spinner = (Spinner)findViewById(R.id.cmbAudios);
    	if ( spinner.getSelectedItem() != null ) {
    		AlertDialog.Builder alert = new AlertDialog.Builder(this);  
        	alert.setTitle(R.string.msg_renamefile);  
        	alert.setMessage(getString(R.string.msg_namenewfile));  
        	final EditText input = new EditText(this);
        	input.setText(spinner.getSelectedItem().toString());
        	alert.setView(input);  
        	alert.setPositiveButton(getString(R.string.text_Ok), new DialogInterface.OnClickListener() {  
        		public void onClick(DialogInterface dialog, int whichButton) {  
        			Spinner spinner = (Spinner)findViewById(R.id.cmbAudios);
        			String lPathOri = GlobalVar.gNoteDataPath + "id_" + Main_NoteWindow.currentEntity.getId() + 
    				   "/Audio/" + spinner.getSelectedItem().toString();
        			String lPathDes = GlobalVar.gNoteDataPath + "id_" + Main_NoteWindow.currentEntity.getId() + 
    				   "/Audio/" + input.getText();
        			if (GeneralUtils.renamefile(lPathOri, lPathDes) ) {
        				fillComboBoxAudio();
        			}
        		}  
        	});  
        	alert.setNegativeButton(getString(R.string.text_Cancel), new DialogInterface.OnClickListener() {  
        		public void onClick(DialogInterface dialog, int whichButton) {  
        			// Cancelado no hacemos nada.  
        		}  	
        	});  
        	alert.show();
    	} else {
    		Toast.makeText(this, R.string.msg_NoAudioSelected, Toast.LENGTH_LONG).show();
    	}
    }
    
    private void DeleteAudio() {
    	Spinner spinner = (Spinner)findViewById(R.id.cmbAudios);
    	if ( spinner.getSelectedItem() != null ) {
    		String lPath = GlobalVar.gNoteDataPath + "id_" + Main_NoteWindow.currentEntity.getId() + 
    					   "/Audio/" + spinner.getSelectedItem().toString();
    		File file = new File(lPath);    		
    		if ( file.delete() == true) {
    			Toast.makeText(this, R.string.msg_AudioNoteDeleted, Toast.LENGTH_LONG).show();
    			fillComboBoxAudio(); /* rellenamos el combo de audios */
    		} else {
    			Toast.makeText(this, R.string.msg_err_AudioNoteDeleted, Toast.LENGTH_LONG).show();
    		}
    	} else {
    		Toast.makeText(this, R.string.msg_NoAudioSelected, Toast.LENGTH_LONG).show();
    	}
    }
    
    private void PlayAudio () {
    	Spinner spinner = (Spinner)findViewById(R.id.cmbAudios);
    	if ( spinner.getSelectedItem() != null ) {
    		String lPath = GlobalVar.gNoteDataPath + "id_" + Main_NoteWindow.currentEntity.getId() +
    					   "/Audio/" + spinner.getSelectedItem().toString();
    		MediaPlayer mp = new MediaPlayer();
    		try {
    			mp.setDataSource(lPath);
    			mp.prepare();
    			mp.start();
    		} catch (IOException e) {
    			Toast.makeText(this, 
    					e.toString(), 
    					Toast.LENGTH_LONG).show();   
    		}
    	} else {
    		Toast.makeText(this, 
    				R.string.msg_NoAudioSelected, Toast.LENGTH_LONG).show();
    	}
    }
    
    private void AddNewAudio() {
    	/* Antes de grabar el audio salvamos la nota si es nueva */
    	if ( Main_NoteWindow.currentEntity.isInsert() ) {
    		Main_NoteWindow.SaveWhiteNote();
    	}
    	Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
		startActivityForResult(intent, ACTIVITY_RECORD_SOUND);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	switch (requestCode) {
    	case ACTIVITY_RECORD_SOUND:
    		if ( resultCode == -1 ) {
    			if ( data.getData() != null ) {
        			Uri mRecordingUri = data.getData();
            		String mRecordingFilename = null;
            		mRecordingFilename = getFilenameFromUri(mRecordingUri);
            		try {
            			SaveFileInDirectoryAplication(mRecordingFilename);
            			fillComboBoxAudio();
            		} catch (IOException e) {
            			Toast.makeText(this, 
                	    		e.getMessage(), 
                	            Toast.LENGTH_LONG).show();   
            		}
        		}
    		}    		
    		break;
    	case ACTIVITY_CAPTURE_PICTURE:
    		Uri photoUri = null;
    		if (resultCode == RESULT_OK) {
    			photoUri = data.getData();
                if (photoUri != null) {
                	/* Comprobamos que esista el directoio de imagenes para la nota */
                	String lPath = GlobalVar.gNoteDataPath + "id_" + Main_NoteWindow.currentEntity.getId() + "/Picture/";
                	File directory = new File(lPath);
                	if (!directory.exists() && !directory.mkdirs()) {	
                		Toast.makeText(this, "Path to file could not be created.", Toast.LENGTH_LONG).show();
               	    }
                	
                	String destino = GlobalVar.gNoteDataPath + "id_" + String.valueOf(Main_NoteWindow.currentEntity.getId()) + 
                	                 "/Picture/" + new File(getFilenameFromUri(photoUri)).getName();
                	try {
                		CopyFile(getFilenameFromUri(photoUri), destino);
                	} catch (IOException e) {
            			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();   
            		}
                	;
                }
           } 
    		fillImageGallery();
    		break;
    	}
    }
    
    private String getFilenameFromUri(Uri uri) {
        Cursor c = managedQuery(uri, null, "", null, null);
        if (c.getCount() == 0) {
            return null;
        }
        c.moveToFirst();
        int dataIndex = c.getColumnIndexOrThrow(
            MediaStore.Audio.Media.DATA);
        return c.getString(dataIndex);
    }
    
    private void SaveFileInDirectoryAplication(String pfile) throws IOException {
    	/* Nos aseguramos que exsita nuestro directorio de destion */
    	String lPath = GlobalVar.gNoteDataPath + "id_" + Main_NoteWindow.currentEntity.getId() + "/Audio/";
    	File directory = new File(lPath);
    	if (!directory.exists() && !directory.mkdirs()) {	
    	      throw new IOException("Path to file could not be created.");
   	    }    	
    	try {    		
    		String lFileName = GetFileNameFree("FileAudio_", GlobalVar.gNoteDataPath + "id_" + Main_NoteWindow.currentEntity.getId() + "/Audio/", "3gpp" );
			CopyFile(pfile, lFileName); /* Copiamos el fichero a nuestro directorio */
			DeleteFile(pfile); /* Eliminamos la que se crea en el directorio raiz de la sd */
		} catch (IOException e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();   
		}
    }
    
    public String GetFileNameFree(String pPatron, String pDirectory, String pExtension) {
		String lcad = "";
		int lnum = 1;
		lcad = pPatron + lnum;
		while ( new File(pDirectory + lcad + "." + pExtension).exists() ) {
			lnum ++;
			lcad = pPatron + lnum;
		}
		return pDirectory + lcad + "." + pExtension;
	}
    
    public void CopyFile(String pOriFile, String pDesFile) throws IOException {
    	File OriFile = new File(pOriFile);
    	File DesFile = new File(pDesFile); 
    	
    	InputStream in = new FileInputStream(OriFile);
    	OutputStream out = new FileOutputStream(DesFile);
    	
    	byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0){
          out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
    
    public void DeleteFile(String pDeleteFile) throws IOException {
    	File DeleteFile = new File(pDeleteFile);
    	DeleteFile.delete();
    }
    
    private OnClickListener OnClickOptionPhotoNote = new OnClickListener()
    {	  
		public void onClick(View v)
        {	
			if (gimages != null) {
				if ( gimages.length > 0 ) {
					new AlertDialog.Builder(v.getContext())
		            .setTitle(R.string.caption_menu_picture_notewindow)            
		            .setItems(R.array.menu_picture_notewindow, new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {
		                    if (which>=0) {
		                        if (which==0) {
		                        	PlayPicture(); 
		                        } else if (which==1) {
		                        	DeletePicture();
		                        }
		                    }
		                }
		            }).show();
				}
			}			
        }
    };
    
    private void DeletePicture() {
    	String lPath = GlobalVar.gNoteDataPath + "id_" + Main_NoteWindow.currentEntity.getId() + 
    					"/Picture/" + gimages[gindeximage - 1];
    	File file = new File(lPath);
    	if ( file.delete() == true) {
    		Toast.makeText(this, R.string.msg_PictureNoteDeleted, Toast.LENGTH_LONG).show();
    		fillImageGallery(); /* rellenamos el combo de imagenes */
    	} else {
    		Toast.makeText(this, R.string.msg_err_PictureNoteDeleted, Toast.LENGTH_LONG).show();
    	}
    }
    
    private void PlayPicture() {
    	String lPath = GlobalVar.gNoteDataPath + "id_" + Main_NoteWindow.currentEntity.getId() + 
		   "/Picture/" + gimages[gindeximage - 1];
    	Intent intent = new Intent();  
    	intent.setAction(android.content.Intent.ACTION_VIEW);  
    	File file = new File(lPath);  
    	intent.setDataAndType(Uri.fromFile(file), "image/*");  
    	startActivity(intent);
    }
        
    private void AddNewPicture() {
    	/* Antes de grabar la imagen salvamos la nota si es nueva */
    	if ( Main_NoteWindow.currentEntity.isInsert() ) {
    		Main_NoteWindow.SaveWhiteNote();
    	}
    	Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, ACTIVITY_CAPTURE_PICTURE);
    }
    
    private OnClickListener OnClickOptionVideoNote = new OnClickListener()
    {	  
		public void onClick(View v)
        {	
			new AlertDialog.Builder(v.getContext())
            .setTitle(R.string.caption_menu_video_notewindow)
            .setItems(R.array.menu_video_notewindow, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which>=0) {
                        if (which==0) {
                        	PlayVideo(); 
                        } else if (which==1) {
                        	AddNewVideo();
                    	 } else if (which==2) {
                    		DeleteVideo();
                    	 } else if (which==3) {
                    		 RenameFileVideo();
                    	 } 
                    }
                }
            }).show();
        }
    };
    
    private void PlayVideo() {
    	Toast.makeText(this, "Pendiente de hacer", Toast.LENGTH_LONG).show();	
    }
    
    private void AddNewVideo() {
    	/* Antes de grabar el video salvamos la nota si es nueva */
    	if ( Main_NoteWindow.currentEntity.isInsert() ) {
    		Main_NoteWindow.SaveWhiteNote();
    	}
    	Intent intendCapture = new Intent("android.media.action.VIDEO_CAMERA");
    	startActivityForResult(intendCapture, 88);    
    }
    
    private void DeleteVideo() {    	
    	Spinner spinner = (Spinner)findViewById(R.id.cmbVideo);
    	if ( spinner.getSelectedItem() != null ) {
    		String lPath = GlobalVar.gNoteDataPath + "id_" + Main_NoteWindow.currentEntity.getId() + 
    					   "/Video/" + spinner.getSelectedItem().toString();
    		File file = new File(lPath);
    		if ( file.delete() == true) {
    			Toast.makeText(this, R.string.msg_VideoNoteDeleted, Toast.LENGTH_LONG).show();
    			fillComboBoxVideo(); /* rellenamos el combo de videos */
    		} else {
    			Toast.makeText(this, R.string.msg_err_VideoNoteDeleted, Toast.LENGTH_LONG).show();
    		}
    	} else {
    		Toast.makeText(this, R.string.msg_NoVideoSelected, Toast.LENGTH_LONG).show();
    	}	
    }
    
    private void RenameFileVideo () {
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);  
    	alert.setTitle(getString(R.string.msg_renamefile));
    	alert.setMessage(getString(R.string.msg_namenewfile));  
    	final EditText input = new EditText(this);
    	Spinner spinner = (Spinner)findViewById(R.id.cmbVideo);
    	input.setText(spinner.getSelectedItem().toString());
    	alert.setView(input);  
    	alert.setPositiveButton(getString(R.string.text_Ok), new DialogInterface.OnClickListener() {  
    		public void onClick(DialogInterface dialog, int whichButton) {  
    			Spinner spinner = (Spinner)findViewById(R.id.cmbVideo);
    			String lPathOri = GlobalVar.gNoteDataPath + "id_" + Main_NoteWindow.currentEntity.getId() + 
				   "/Video/" + spinner.getSelectedItem().toString();
    			String lPathDes = GlobalVar.gNoteDataPath + "id_" + Main_NoteWindow.currentEntity.getId() + 
				   "/Video/" + input.getText();
    			if (GeneralUtils.renamefile(lPathOri, lPathDes) ) {
    				fillComboBoxVideo();
    			}
    		}  
    	});  
    	alert.setNegativeButton(getString(R.string.text_Cancel), new DialogInterface.OnClickListener() {  
    		public void onClick(DialogInterface dialog, int whichButton) {  
    			// Cancelado no hacemos nada.  
    		}  	
    	});  
    	alert.show();
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, ITEM_ID_MENU_MAIN_NOTEWINDOW_ADDPICTURE, 0, R.string.btn_menu_Add_Picture).setIcon(
				android.R.drawable.ic_menu_add).setAlphabeticShortcut('N');
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
		case ITEM_ID_MENU_MAIN_NOTEWINDOW_ADDPICTURE:
			AddNewPicture();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	private OnClickListener OnClickNextImage = new OnClickListener()
    {	  
		public void onClick(View v)
        {
			if ( gimages != null ){
				gindeximage += 1;
				if ( gindeximage > (gimages.length) ) {
						gindeximage = gimages.length;
				} else {
					ShowImage(gimages, gindeximage);
				}
			}
        }
    };
    
    private OnClickListener OnClickPriorImage = new OnClickListener()
    {	  
		public void onClick(View v)
        { 
			gindeximage -= 1;
			if ( gindeximage < 1 ) {
				gindeximage = 1;
			} else {
			  ShowImage(gimages, gindeximage);
			} 
        }
    };
}
