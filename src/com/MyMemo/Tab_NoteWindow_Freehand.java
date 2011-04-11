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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

public class Tab_NoteWindow_Freehand extends GraphicsActivity
        implements ColorPickerDialog.OnColorChangedListener {
	
	GraphicsActivity gActivity;
	private View gView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MyView(this));
        
        gActivity = this;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        //mPaint.setColor(0xFFFF0000); Rojo
        mPaint.setColor(0xFFFFFFFF);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(6);
        mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 },
                                       0.4f, 6, 3.5f);        
    }
    
    private Paint       mPaint;
    private MaskFilter  mEmboss;    
    private Canvas      mCanvas;
    private Path        mPath;
    private Paint       mBitmapPaint;    
    
    public void colorChanged(int color) {
        mPaint.setColor(color);
    }

    public class MyView extends View {
        
        public MyView(Context c) {
            super(c);
            
            String lfilebitmap = GlobalVar.gNoteDataPath + "id_" + Main_NoteWindow.currentEntity.getId() + "/FreeHand/FreeHand.jpg";
            File file = new File(lfilebitmap);
            if ( file.exists() &&  Main_NoteWindow.currentEntity.getId() >= 0 ) {
            	Main_NoteWindow.mBitmap = Bitmap.createBitmap(320, 480, Bitmap.Config.ARGB_8888);
        		mCanvas = new Canvas(Main_NoteWindow.mBitmap);
        		Bitmap lbm = BitmapFactory.decodeFile(lfilebitmap);
        		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        		mCanvas.drawBitmap(lbm, 0f, 0f, mBitmapPaint);
        		mPath = new Path();
        		Main_NoteWindow.gActiveFreeHand = true;
            } else {
            	Main_NoteWindow.mBitmap = Bitmap.createBitmap(320, 480, Bitmap.Config.ARGB_8888);            	
            	mCanvas = new Canvas(Main_NoteWindow.mBitmap);
            	mCanvas.drawARGB(255, 156, 130, 132);
            	mPath = new Path();
                mBitmapPaint = new Paint(Paint.DITHER_FLAG);
                Main_NoteWindow.gActiveFreeHand = false;
            }
            
            gView = this;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
        }
        
        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(0xFFAAAAAA);
            canvas.drawBitmap(Main_NoteWindow.mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
        }
        
        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;
        
        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }
        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
            }
        }
        private void touch_up() {
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }
        
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            
            Main_NoteWindow.gActiveFreeHand = true;
            
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }
    
    private static final int COLOR_MENU_ID = Menu.FIRST;
    private static final int EMBOSS_MENU_ID = Menu.FIRST + 1;
    private static final int ERASE_MENU_ID = Menu.FIRST + 3;
    private static final int SAVENOTE_MENU_ID = Menu.FIRST +5;
    private static final int CANCELNOTE_MENU_ID = Menu.FIRST +6;
    private static final int CLEAR_MENU_ID = Menu.FIRST +7;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        menu.add(0, SAVENOTE_MENU_ID, 0, R.string.text_Ok).setIcon(
				android.R.drawable.ic_menu_save).setAlphabeticShortcut('N');
        menu.add(0, COLOR_MENU_ID, 0, R.string.FreeHand_Color).setIcon(
				android.R.drawable.ic_menu_edit).setAlphabeticShortcut('N');
        menu.add(0, EMBOSS_MENU_ID, 0, R.string.FreeHand_Emboss).setIcon(
				android.R.drawable.ic_menu_set_as).setAlphabeticShortcut('N');
        menu.add(0, ERASE_MENU_ID, 0, R.string.FreeHand_Eraser).setIcon(
				android.R.drawable.ic_menu_revert).setAlphabeticShortcut('N');
        menu.add(0, CLEAR_MENU_ID, 0, R.string.FreeHand_Clear).setIcon(
				android.R.drawable.ic_menu_delete).setAlphabeticShortcut('N');
        menu.add(0, CANCELNOTE_MENU_ID, 0,
				R.string.text_Cancel).setIcon(
				android.R.drawable.ic_menu_close_clear_cancel).setAlphabeticShortcut('N');
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xFF);

        switch (item.getItemId()) {
            case COLOR_MENU_ID:
                new ColorPickerDialog(this, this, mPaint.getColor()).show();
                return true;
            case EMBOSS_MENU_ID:
                if (mPaint.getMaskFilter() != mEmboss) {
                    mPaint.setMaskFilter(mEmboss);
                } else {
                    mPaint.setMaskFilter(null);
                }
                return true;
            case ERASE_MENU_ID:
                //mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            	mPaint.setColor(0xFF9c8284);
                return true;
            case SAVENOTE_MENU_ID:
            	Main_NoteWindow.SaveNote(false);
            	return true;
            case CANCELNOTE_MENU_ID:
            	Main_NoteWindow.CancelNote();
            	return true;
            case CLEAR_MENU_ID:
            	mCanvas.drawARGB(255, 156, 130, 132);
            	mPaint.setColor(0xFFFFFFFF);
            	gView.invalidate();
            	Main_NoteWindow.gActiveFreeHand = false;
            	return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
