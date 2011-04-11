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

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.Overlay;

public class NoteOnMapWindow extends MapActivity  {
	
	List<Overlay> mapOverlays;
	Drawable drawable;

	@Override
    protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		FrameLayout frame = new FrameLayout(this);
		MapLocationViewer mMapView = new MapLocationViewer(this, "XXXXXXXXXXXXXXXXXXXXXXXXXXXX"); //Put here you KEY MAP
		frame.addView(mMapView);
		setContentView(frame);
		
		MapController mMapController = mMapView.getController();
		mMapView.setTraffic(true);
		mMapView.setSatellite(false);
		mMapController.setZoom(14);
		
		View zoomControls = mMapView.getZoomControls();
		
		FrameLayout.LayoutParams p =
		new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
		LayoutParams.WRAP_CONTENT,
		Gravity.BOTTOM + Gravity.CENTER_HORIZONTAL);
		frame.addView(zoomControls, p);
		
		Bundle extras = getIntent().getExtras();
		List<Entity> entityList;
		Entity entity;
		GeoPoint point = null;
		if ( extras.getLong(DataFramework.KEY_ID) != -1 &&
        	extras.getLong(DataFramework.KEY_ID) != 0	) { /* Quiere ver solo una nota en el mapa*/
			entity = new Entity("notes", extras.getLong(DataFramework.KEY_ID));
			if ( entity.getFloat("latitude") > 0 ) { //La nota tiene localizacion
				String ltitle = entity.getString("title");
				if ( main.preferences.getBoolean("checkbox_show_provider_map", true) == true ) {
					ltitle = entity.getString("title") + " -- provider: " + 
							 entity.getString("provider");
				}
				if ( main.preferences.getBoolean("checkbox_send_mail_location", true) == true ) {
					
				}
				point = new GeoPoint((int)(entity.getFloat("latitude")*1E6), (int)(entity.getFloat("longitude")*1E6));
				mMapView.getManager().addMapLocation(new MapLocation(mMapView,
													ltitle,
													Utils.Geopoint2Location(point), 
													MapLocation.TYPE_BUBBLE));
			}
		} else {
			entityList = DataFramework.getInstance().getEntityList("notes", "latitude > 0"); /*Todas las notas */
			for ( int i = 0; i < entityList.size(); i++ ) {
				entity = entityList.get(i);
				if ( entity.getFloat("latitude") > 0 ) { //La nota tiene localizacion
					String ltitle = entity.getString("title");
					if ( main.preferences.getBoolean("checkbox_show_provider_map", true) == true ) {
						ltitle = entity.getString("title") + " -- provider: " + 
								 entity.getString("provider");
					}
					point = new GeoPoint((int)(entity.getFloat("latitude")*1E6), (int)(entity.getFloat("longitude")*1E6));
					mMapView.getManager().addMapLocation(new MapLocation(mMapView,
														ltitle,
														Utils.Geopoint2Location(point), 
														MapLocation.TYPE_BUBBLE));
				}	
			}
		}
		if ( point != null) {
			mMapController.setCenter(point);
		}	
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
