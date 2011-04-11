package com.MyMemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class MapLocationViewer extends MapView {
	
	private BubblesOverlay overlay;
	private MapActivity mMapActivity;
	private boolean move = false;
	private GeoPoint gpoint00;
		
    /**
     * Devuelve el MapLocationsManager
     * @return MapLocationsManager
     */
	
    public MapLocationsManager getManager() {
    	return overlay.getManager();
    }
    
    /**
     * Constructor - Mapa que contiene las rutas
     * 
     * @param context Context
     * @param apiKey Clave de la API
     */
    
    public MapLocationViewer(Context context, String apiKey) {
		super(context, apiKey);
		mMapActivity = (MapActivity) context;
		init();
	}
	
    /**
     * Constructor - Mapa que contiene las rutas
     * 
     * @param context Context
     * @param attrs AttributeSet
     */
    
	public MapLocationViewer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
    /**
     * Inicializa el mapa
     */
	
	private void init() {		
		overlay = new BubblesOverlay(this);
    	getOverlays().add(overlay);
    	
    	setClickable(true);

	}
	
    /**
     * Refresca el mapa
     */
	
	public void refresh() {
		this.invalidate();
	}
	
    /**
     * Devuelve el MapActivity
     * 
     * @return MapActivity
     * 
     */
	
	public MapActivity getMapActivity() {
		return mMapActivity;
	}
		
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			if (!move) {
	    		GeoPoint aux = getProjection().fromPixels(0, 0);
				if (!aux.equals(gpoint00)) {
					move = true;
				}
			}
		}
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			gpoint00 = getProjection().fromPixels(0, 0);
			move = false;
		}
		if (ev.getAction() == MotionEvent.ACTION_UP) {
			if (move) this.getManager().setTodoRefresh(true);
		}
		return super.onTouchEvent(ev);
	}
	
}