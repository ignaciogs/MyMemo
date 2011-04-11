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
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.dataframework.Entity;

public class RowAdapter extends BaseAdapter {

    private Context mContext;
    private List<Entity> elements;
    private Entity item;
	
	/**
     * Constructor - Adaptador que crea la vista de cada una de las
     * filas de la lista de rutas
     * 
     * @param mContext Context
     * @param elements Lista de elementos
     */
    
    public RowAdapter(Context mContext, List<Entity> elements)
    {
        this.mContext = mContext;
        this.elements = elements;        
    }
	
	public int getCount() {
		return elements.size();
	}

	public Object getItem(int position) {
		return elements.get(position);
	}

	public long getItemId(int position) {
		Entity ent = elements.get(position);
		return ent.getId();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		//Entity item = elements.get(position);
		item = elements.get(position);
		
		View v = View.inflate(mContext, R.layout.rownotes, null);
		TextView datetext = (TextView)v.findViewById(R.id.DateNote);   
		Calendar datenote = Calendar.getInstance();
	    datenote.setTimeInMillis(item.getLong("date"));
		datetext.setText(GeneralUtils.FormatDateTime(datenote.getTime(), "EEE, d MMM yyyy HH:mm"));
		
		/* Imagen de la prioridad de la nota */
		ImageView imagePriority = (ImageView)v.findViewById(R.id.Image_Priority);
		if ( item.isNull("priority") == true ) {
			imagePriority.setImageResource(R.drawable.priority_all_1);
		} else {
			switch (item.getInt("priority")) {
			case 1:
				imagePriority.setImageResource(R.drawable.priority_all_1);
				break;
			case 2:
				imagePriority.setImageResource(R.drawable.priority_all_2);
				break;
			case 3:
				imagePriority.setImageResource(R.drawable.priority_all_3);
				break;
			case 4:
				imagePriority.setImageResource(R.drawable.priority_all_4);
				break;
			case 5:
				imagePriority.setImageResource(R.drawable.priority_all_5);
				break;
			case 6:
				imagePriority.setImageResource(R.drawable.priority_all_6);
				break;
			}
		}
        
        TextView text = (TextView)v.findViewById(R.id.TeleTituloNota);       
        text.setText(item.getValue("title").toString());
        
        ImageView imageText = (ImageView)v.findViewById(R.id.Image_Text);        
        if ( ( item.isNull("text") == true ) || ( item.getValue("text").toString().length() == 0 )  ) {
        	/* Ocultamos la imagen del texto ya que la nota no tiene */
        	imageText.setVisibility(View.GONE);
        }
        
        /* Comprobamos si la nota tiene alguign audio asociado */
        String lPath = GlobalVar.gNoteDataPath + "id_" + item.getId() + "/Audio/";
    	File directoryAudio = new File(lPath);//.getParentFile();
    	ImageView imageAudio = (ImageView)v.findViewById(R.id.Image_Sound);
    	if ( directoryAudio.isDirectory() ) { 
    		if ( directoryAudio.list().length < 1 ) {    		
    			imageAudio.setVisibility(View.GONE);
    		} else {
    			imageAudio.setVisibility(View.VISIBLE);
    		}
    	} else {
    		imageAudio.setVisibility(View.GONE);
    	}
    	directoryAudio = null;
    	
    	/* Comprobamos si la nota tiene alguna imagen asociada */
    	String lPath2 = GlobalVar.gNoteDataPath + "id_" + item.getId() + "/Picture/";
    	File directoryPicture = new File(lPath2);
    	ImageView imagePicture = (ImageView)v.findViewById(R.id.Image_Photo);
    	if ( directoryPicture.isDirectory() ) {
    		if ( directoryPicture.list().length < 1 ) {    		
    			imagePicture.setVisibility(View.GONE);
    		} else {
    			imagePicture.setVisibility(View.VISIBLE);
    		}
    	} else {
    		imagePicture.setVisibility(View.GONE);
    	}
    	
    	/* Comprobamos si la nota tiene algun video asociado */
    	String lPath3 = GlobalVar.gNoteDataPath + "id_" + item.getId() + "/Video/";
    	File directoryVideo = new File(lPath3);
    	ImageView imageVideo = (ImageView)v.findViewById(R.id.Image_Video);
    	if ( directoryVideo.isDirectory() ) {
    		if ( directoryVideo.list().length < 1 ) {    		
    			imageVideo.setVisibility(View.GONE);
    		} else {
    			imageVideo.setVisibility(View.VISIBLE);
    		}
    	} else {
    		imageVideo.setVisibility(View.GONE);
    	}
    	/* Comprobamos si la nota tiene una alarma pendiente para mostrar el icono del reloj */
    	Calendar newdatealarm = Calendar.getInstance();
    	Calendar datenow = Calendar.getInstance();
    	newdatealarm.set(item.getInt("alarm_year"), 
		  		  		item.getInt("alarm_month"), 
		  		  		item.getInt("alarm_day"),
		  		  		item.getInt("alarm_hour"), 
		  		  		item.getInt("alarm_minute"),
		  		  		0);
    	ImageView imageAlarm = (ImageView)v.findViewById(R.id.Image_Alarm);
    	if ( newdatealarm.getTimeInMillis() > datenow.getTimeInMillis() ) {
    		imageAlarm.setVisibility(View.VISIBLE);
    	} else {
    		imageAlarm.setVisibility(View.GONE);
    	}
    	
    	/* Comprobamos si tiene mano alzada utlizada*/
    	String lfilebitmap = GlobalVar.gNoteDataPath + "id_" + item.getId() + "/FreeHand/FreeHand.jpg";
        File file = new File(lfilebitmap);
        ImageView imageFreeHand = (ImageView)v.findViewById(R.id.Image_FreeHand);
        if ( file.exists() ) {
        	imageFreeHand.setVisibility(View.VISIBLE);
    	} else {
    		imageFreeHand.setVisibility(View.GONE);
    	}

		return v;
	}

}
