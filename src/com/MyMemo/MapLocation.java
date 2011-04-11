package com.MyMemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Path.Direction;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

/** Class to hold our location information */
public class MapLocation {

	public static final int TYPE_CURRENTPOSITION = 0;
	public static final int TYPE_BUBBLE = 1;
	
	public static final int PADDING_X = 10;
	public static final int PADDING_Y = 8;
	public static final int RADIUS_BUBBLES = 5;
	public static final int DISTANCE_BUBBLE = 15;
	public static final int SIZE_SELECTOR_BUBBLE = 10;
	
	private Location mLocation;
	private String mName;
	private MapLocationViewer mMapLocationView;
	private int mType = -1;
	private boolean mVisibility = true;
	
	private Bitmap mDrawIcon, mShadowIcon;

    /**
     * Constructor - Una marca en el mapa
     * 
     * @param mapView MapLocationViewer
     * @param name Nombre de la marca
     * @param loc Localizacion de la marca en el mapa
     * @param type Tipo de marca
     */
	
	public MapLocation(MapLocationViewer mapView, String name, Location loc, int type) {
		this.mName = name;
		mMapLocationView = mapView;
		this.mLocation = loc;
		setType(type);
	}
	
    /**
     * Mostrarlo en el mapa
     */
	
	public void show() {
		mVisibility = true;
	}
	
    /**
     * Mostrarlo en el mapa
     */
	
	public void hide() {
		mVisibility = false;
	}
	
	public boolean isVisible() {
		return mVisibility;
	}
	
    /**
     * Establece el tipo de la marca
     * 
     * @param type Tipo de marca
     */
	
	private void setType(int type) {
		this.mType = type;	
		switch (mType) {
		case TYPE_CURRENTPOSITION:
			mDrawIcon = BitmapFactory.decodeResource(mMapLocationView.getResources(),R.drawable.current_position);
			mShadowIcon = null;
			break;
		case TYPE_BUBBLE:
			mDrawIcon = BitmapFactory.decodeResource(mMapLocationView.getResources(),R.drawable.bubble);
			mShadowIcon = BitmapFactory.decodeResource(mMapLocationView.getResources(),R.drawable.bubble_shadow);
			break;
		}
		
	}
	
    /**
     * Devuelve el tipo de dibujo
     * 
     * @return Bitmap Dibujo
     */
	
	public Bitmap getDrawIcon() {
		return mDrawIcon;
	}
	
    /**
     * Devuelve la sombra del dibujo
     * 
     * @return Bitmap Sombra
     */
	
	public Bitmap getShadowIcon() {
		return mShadowIcon;
	}
	
    /**
     * Devuelve el tipo
     * 
     * @return int Tipo
     */
	
	public int getType() {
		return mType;
	}
	
    /**
     * Devuelve localizacion
     * 
     * @return Location Localizacion
     */
	
	public void setLocation(Location loc) {
		mLocation = loc;
	}
	
    /**
     * Devuelve localizacion
     * 
     * @return Location Localizacion
     */
	
	public Location getLocation() {
		return mLocation;
	}
	
    /**
     * Devuelve localizacion
     * 
     * @return GeoPoint Objeto GeoPoint
     */
	
	public GeoPoint getGeoPoint() {
		return new GeoPoint((int)(mLocation.getLatitude()*1E6), (int)(mLocation.getLongitude()*1E6));
	}
			
    /**
     * Devuelve el nombre
     * 
     * @return String Nombre
     */

	public String getName() {
		return mName;
	}
	
    /**
     * Devuelve el ancho del dibujo
     * 
     * @return int Ancho
     */
	
	public int getWidthIcon() {
		return mDrawIcon.getWidth();
	}
	
    /**
     * Devuelve el alto del dibujo
     * 
     * @return int Alto
     */
	
	public int getHeightIcon() {
		return mDrawIcon.getHeight();
	}
	
	public int getWidthText() {
		return (int)MapLocationsManager.textPaint.measureText(this.getName());
	}
	
	public int getHeightText() {
		return (int)MapLocationsManager.textPaint.descent()-(int)MapLocationsManager.textPaint.ascent();
	}
		
    /**
     * Devuelve el objeto RectF asociado al icono en la posicion (0,0)
     * 
     * @return Objeto RectF
     */
	
	public RectF getHRectFIcon() {
		return getHRectFIcon(0, 0);
	}
	
    /**
     * Devuelve el objeto RectF asociado al icono en la posicion 
     * enviada por parametro
     * 
     * @param offsetx Desplazamiento en X
     * @param offsety Desplazamiento en Y
     * @return Objeto RectF
     */
	
	public RectF getHRectFIcon(int offsetx, int offsety) {
		RectF rectf = new RectF();
		rectf.set(-mDrawIcon.getWidth()/2,-mDrawIcon.getHeight(),mDrawIcon.getWidth()/2,0);
		rectf.offset(offsetx, offsety);
		return rectf;
	}
	
    /**
     * Devuelve si ha sido pulsado el icono en el mapa
     * 
     * @param offsetx Desplazamiento en X
     * @param offsety Desplazamiento en Y
     * @param event_x Posicion X
     * @param event_y Posicion Y
     * @return Booleando
     */
	
	public boolean getHit(int offsetx, int offsety, float event_x, float event_y) {
	    if ( getHRectFIcon(offsetx, offsety).contains(event_x,event_y) ) {
	        return true;
	    }
	    return false;
	}
	
    /**
     * Dibuja la locacalizacion en el mapa
     * 
     * @param canvas Canvas sobre el que se dibuja
     * @param mapView Mapa
     * @param shadow Si es la sombra
     */
	
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (mVisibility && (getLocation()!=null)) {
			Point p = new Point();
			mapView.getProjection().toPixels(this.getGeoPoint(), p);
			
	    	if (shadow) {
	    		if (getShadowIcon()!=null) canvas.drawBitmap(this.getShadowIcon(), p.x, p.y - this.getShadowIcon().getHeight(),null);
	    	} else {
				canvas.drawBitmap(this.getDrawIcon(), p.x -this.getDrawIcon().getWidth()/2, p.y -this.getDrawIcon().getHeight(),null);
	    	}
		}
	}
	
	public void drawBubble(Canvas canvas, MapView mapView, boolean shadow) {
	    Point p = new Point();
	    mapView.getProjection().toPixels(Utils.Location2Geopoint(mLocation), p);
	    
	    int wBox = getWidthText()  + (PADDING_X*2);
	    int hBox = getHeightText() + (PADDING_Y*2); 
	    
	    RectF boxRect = new RectF(0, 0, wBox, hBox);
	    int offsetX = p.x - wBox/2;
	    int offsetY = p.y - hBox - this.getHeightIcon() - DISTANCE_BUBBLE;
	    boxRect.offset(offsetX, offsetY);
	    
	    Path pathBubble = new Path();
	    pathBubble.addRoundRect(boxRect, RADIUS_BUBBLES, RADIUS_BUBBLES, Direction.CCW);
	    pathBubble.moveTo(offsetX+(wBox/2)-(SIZE_SELECTOR_BUBBLE/2), offsetY+hBox);
	    pathBubble.lineTo(offsetX+(wBox/2), offsetY+hBox+SIZE_SELECTOR_BUBBLE);
	    pathBubble.lineTo(offsetX+(wBox/2)+(SIZE_SELECTOR_BUBBLE/2), offsetY+hBox);
	    
	    canvas.drawPath(pathBubble, MapLocationsManager.borderPaint);
	    canvas.drawPath(pathBubble, MapLocationsManager.innerPaint);
	
	    canvas.drawText(this.getName(), p.x-(getWidthText()/2),
	    		p.y-MapLocationsManager.textPaint.ascent()-this.getHeightIcon()-hBox+PADDING_Y - DISTANCE_BUBBLE, MapLocationsManager.textPaint);
	}

}
