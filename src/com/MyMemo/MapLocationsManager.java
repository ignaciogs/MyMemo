package com.MyMemo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.location.Location;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class MapLocationsManager {
	
	private List<MapLocation> mapLocations;
	
	public OnMapLocationClickListener OnMapLocationClickListener = null;
	
	public static Paint innerPaint, borderPaint, textPaint;
		
	private MapLocation selectedLocation = null;
	
	private MapLocation mCurrentLocation = null;
	
	private MapLocationViewer mMapView;
	
	private GeoPoint gpoint00 = null;
	
	private boolean todoRefresh = true;
	
	private int zoom = -1;
	
    public MapLocationsManager(MapLocationViewer mlv) {
    	mMapView = mlv;
    	mapLocations = new ArrayList<MapLocation>();
    	
		mCurrentLocation = new MapLocation(mMapView, "Posici�n actual", null, MapLocation.TYPE_CURRENTPOSITION);
		mCurrentLocation.hide();
		
		addMapLocation(mCurrentLocation);
		
		innerPaint = new Paint();
		innerPaint.setARGB(255, 255, 255, 255);
		innerPaint.setAntiAlias(true);
		
		borderPaint = new Paint();
		borderPaint.setARGB(255, 0, 0, 0);
		borderPaint.setAntiAlias(true);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(4);
		
		textPaint = new Paint();
		textPaint.setARGB(255, 0, 0, 0);
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(12);
		
		todoRefresh = true;
    	
	}
    
	
	public void setOnMapLocationClickListener(OnMapLocationClickListener OnMapLocationClickListener) {
		this.OnMapLocationClickListener = OnMapLocationClickListener;
	}
	
	public void addMapLocation(MapLocation ml) {
		mapLocations.add(ml);
	}
	
	public List<MapLocation> getMapLocations() {
		return mapLocations;
	}
	
	public MapLocation getSelectedMapLocation() {
		return selectedLocation;
	}
	
	public void clear() {
		mapLocations.clear();
		addMapLocation(mCurrentLocation);
	}
	
    public boolean verifyHitMapLocation(MapView mapView, MotionEvent event) {
    	if (event.getAction()==MotionEvent.ACTION_DOWN) {
	    	Iterator<MapLocation> iterator = getMapLocations().iterator();
	    	while(iterator.hasNext()) {
	    		MapLocation testLocation = iterator.next();
	    		
	    		if (testLocation.getLocation()!=null && testLocation.isVisible()) {
	    		
		    		Point p = new Point();
		    		
		    		mapView.getProjection().toPixels(testLocation.getGeoPoint(), p);
		    		
		    		if (testLocation.getHit(p.x, p.y, event.getX(),event.getY())) {
		    			if (selectedLocation == testLocation) {
		    				if (OnMapLocationClickListener!=null) {
		    					OnMapLocationClickListener.OnMapLocationClick(selectedLocation, true);
		    				}
		    			} else {
		    				selectedLocation = testLocation;
		    				if (OnMapLocationClickListener!=null) {
		    					OnMapLocationClickListener.OnMapLocationClick(selectedLocation, false);
		    				}
		    			}
		    			return false;
		    	    }
		    		
	    		}
	    	    
	    	}
    	}
    	//selectedLocation = null;
    	return false; 
    }
    
    private void changeMap() {   	
		if (OnMapLocationClickListener!=null) {
			OnMapLocationClickListener.OnMapChanged(
					Utils.Geopoint2Location(mMapView.getProjection().fromPixels(0, 0)), 
					Utils.Geopoint2Location(mMapView.getProjection().fromPixels(mMapView.getWidth(), mMapView.getHeight())));
		}
    }
    
    public Location getLocationTopLeft() {
		return Utils.Geopoint2Location(mMapView.getProjection().fromPixels(0, 0));
    }
    
    public Location getLocationBottomRight() {
		return Utils.Geopoint2Location(mMapView.getProjection().fromPixels(mMapView.getWidth(), mMapView.getHeight()));
    }
    
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
    	
    	if (shadow) {
	    	if (gpoint00==null) {
	    		gpoint00 = mMapView.getProjection().fromPixels(0, 0);
	    	} 
	    		
    		if (mMapView.getZoomLevel() != zoom) {
    			todoRefresh = true;
    			zoom = mMapView.getZoomLevel();
    		}
    		
    		if (todoRefresh) {
	    		GeoPoint aux = mMapView.getProjection().fromPixels(0, 0);
	
				if (aux.equals(gpoint00)) {
					changeMap();
					todoRefresh = false;
				}
    		}
			
    	}
    	
		Iterator<MapLocation> iterator = getMapLocations().iterator();

		while(iterator.hasNext()) {	   
    		MapLocation location = iterator.next();
    		location.draw(canvas, mapView, shadow);
    	}
		
		// dibujar bocadillo
    	if ( selectedLocation != null) {
    		selectedLocation.drawBubble(canvas, mapView, shadow);
    	}
		
		gpoint00 = mMapView.getProjection().fromPixels(0, 0);
		    	
    }
    
	
	public void showCurrentLocation() {
		mCurrentLocation.show();
	}
	
	
	public void hideCurrentLocation() {
		mCurrentLocation.hide();
	}
	
	public void setCurrentLocation(Location loc) {
		mCurrentLocation.setLocation(loc);
	}


	public void setTodoRefresh(boolean todoRefresh) {
		this.todoRefresh = todoRefresh;
	}

    
}
