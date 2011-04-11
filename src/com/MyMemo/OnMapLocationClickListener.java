package com.MyMemo;

import android.location.Location;

public interface OnMapLocationClickListener {
	public abstract void OnMapLocationClick(MapLocation mapLocation, boolean wasSelected);
	public abstract void OnMapChanged(Location topLeft, Location bottomRight);
}
