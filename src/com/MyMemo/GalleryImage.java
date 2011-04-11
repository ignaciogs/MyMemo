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

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class GalleryImage extends Activity {
	
	private Context mContext;
    private Cursor cursor;
    private int column_index;
    private GridView g;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);      
        setContentView(R.layout.galleryimage);
        String [] proj={MediaStore.Images.Thumbnails._ID};
        cursor = managedQuery( MediaStore.Images.Thumbnails.INTERNAL_CONTENT_URI,  //EXTERNAL_CONTENT_URI,        
        //cursor = managedQuery( MediaStore.Images.Thumbnails.getContentUri(GlobalVar.gNoteDataPath + "id_1/Picture/"),
                proj,
                null,    
                null,      
                null);  
       
         
        column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
       
        g = (GridView) findViewById(R.id.grid);
        g.setAdapter(new ImageAdapter(this));      
       
        g.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
               String [] proj={MediaStore.Images.Media.DATA};
                    cursor = managedQuery( MediaStore.Images.Media.INTERNAL_CONTENT_URI, //EXTERNAL_CONTENT_URI,
               		//cursor = managedQuery( Uri.fromFile(new File(GlobalVar.gNoteDataPath + "id_1/Picture/")),
                          proj,
                          null,      
                          null,      
                          null);
                 
                  column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                 
                  cursor.moveToPosition((int) g.getSelectedItemId());
                 
                  String filename = cursor.getString(column_index);
                  Toast.makeText(GalleryImage.this, filename, Toast.LENGTH_SHORT).show();
            }
        });        
    }

    public class ImageAdapter extends BaseAdapter {

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
          return cursor.getCount();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
          ImageView i;
          if (convertView == null) {
            i = new ImageView(mContext);
               cursor.moveToPosition(position);
                    int id = cursor.getInt(column_index);
                    i.setImageURI(Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, ""+id));
                    i.setScaleType(ImageView.ScaleType.FIT_XY);
                    i.setLayoutParams(new GridView.LayoutParams(92, 92));
          }
          else
          {
            i=(ImageView)convertView;
          }
               return i;
        }
      } 
}
