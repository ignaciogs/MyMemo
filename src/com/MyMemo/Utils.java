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
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

public class Utils {

    /**
     * Convierte de Location a Geopoint
     * 
     */
    
    static public GeoPoint Location2Geopoint(Location loc) {
    	return new GeoPoint((int)(loc.getLatitude()*1E6), (int)(loc.getLongitude()*1E6));
    }
    
    /**
     * Convierte de Geopoint a Location 
     * 
     */
    
    static public Location Geopoint2Location(GeoPoint geo) {
    	Location loc = new Location(LocationManager.GPS_PROVIDER);
    	loc.setLatitude((double)geo.getLatitudeE6()/1E6);
		loc.setLongitude((double)geo.getLongitudeE6()/1E6);
    	return loc;
    }
    
    /**
     * Comprueba si el GPS esta activido en el dispositivo
     * 
     * @return savedInstanceState
     */
    
    static public boolean getGPSStatus(Activity act)
    {
    	String allowedLocationProviders =
    		Settings.System.getString(act.getContentResolver(),
    		Settings.System.LOCATION_PROVIDERS_ALLOWED);
     
    	if (allowedLocationProviders == null) {
    		allowedLocationProviders = "";
    	}
     
    	return allowedLocationProviders.contains(LocationManager.GPS_PROVIDER) || allowedLocationProviders.contains(LocationManager.NETWORK_PROVIDER);
    }	
    
    /**
     * Muestra un mensaje
     * 
     * @param msg Mensaje
     * 
     */
    
    public static void showMessage(Context context, String msg) {
	    Toast.makeText(context, 
	    		msg, 
	            Toast.LENGTH_LONG).show();
    }
    
    /**
     * Muestra un mensaje
     * 
     * @param msg Mensaje
     * 
     */
    
    public static void showShortMessage(Context context, String msg) {
	    Toast.makeText(context, 
	    		msg, 
	            Toast.LENGTH_SHORT).show();
    }
	
}
