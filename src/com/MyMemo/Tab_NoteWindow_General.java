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
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class Tab_NoteWindow_General extends Activity {
	
	private static final int ITEM_ID_MENU_MAIN_NOTEWINDOW_SAVENOTE = 1;
	private static final int ITEM_ID_MENU_MAIN_NOTEWINDOW_CANCELNOTE = 2;	
	
	static public Activity gActivityGeneral = null;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        setContentView(R.layout.tab_notewindow_general);
        gActivityGeneral = this;
        
        EditText edittext;
        /* Consultamos las preferencias para las notas */
        edittext = (EditText)findViewById(R.id.txtTitulo);
        if ( main.preferences.getBoolean("checkbox_capitalize_title", true) == true ) {        	
        	if ( edittext != null ) {
        		edittext.setInputType(0x00004001);
        	}
        } else {
        	edittext.setInputType(0x00000000);
        }
		
        edittext = (EditText)findViewById(R.id.txtText);
        if ( main.preferences.getBoolean("checkbox_capitalize_text", true) == true ) {        	
        	if ( edittext != null ) {
        		edittext.setInputType(0x00004001|0x00020001);
        	}
        } else {
        	edittext.setInputType(0x00000000);
        }
        /* Fin de la carga de preferencias */
        
        EditText txttitle = (EditText)findViewById(R.id.txtTitulo);
        txttitle.setOnFocusChangeListener(OnFocusChangeListenerTxtTitulo);
        txttitle.setOnKeyListener(OnKeyListenertxtTitle);
        
        EditText txttext = (EditText)findViewById(R.id.txtText);
        txttext.setOnFocusChangeListener(OnFocusChangeListenerTxtText);
        txttext.setOnKeyListener(OnKeyListenertxtText);        
        
        //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(edittext.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		//imm.hideSoftInputFromInputMethod(edittext.getWindowToken(), 0); 
		//hideSoftInputFromWindow(edittext.getWindowToken(), 0);
        //edittext.f
        
        EditText pepe = (EditText)findViewById(R.id.txtTitulo);
        //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(pepe.getWindowToken(), 0);
        //pepe.setFocusable(true);
        
    }
	
	private OnFocusChangeListener OnFocusChangeListenerTxtTitulo = new OnFocusChangeListener()
    {	  
		public void onFocusChange(View v, boolean hasFocus) {
			EditText txttitle = (EditText)findViewById(R.id.txtTitulo);
			String ltexttitle = txttitle.getText().toString();
			if ( ltexttitle == null ) {
				ltexttitle = getString(R.string.automaticTitle);
				txttitle.setText(ltexttitle);
			}
			Main_NoteWindow.gTitleNote = ltexttitle;
			
			EditText edittext = (EditText)findViewById(R.id.txtTitulo);
	        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.hideSoftInputFromInputMethod(edittext.getWindowToken(), 0); 
		}
    };
	
	private OnKeyListener OnKeyListenertxtTitle = new OnKeyListener()
    {	  
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			EditText txttitle = (EditText)findViewById(R.id.txtTitulo);
			String ltexttitle = txttitle.getText().toString();
			if ( ltexttitle == null ) {
				ltexttitle = getString(R.string.automaticTitle);
				txttitle.setText(ltexttitle);
			}
			Main_NoteWindow.gTitleNote = ltexttitle;
			return false;
		}
    };
    
    private OnFocusChangeListener OnFocusChangeListenerTxtText = new OnFocusChangeListener()
    {	  
		public void onFocusChange(View v, boolean hasFocus) {
			EditText txttext = (EditText)findViewById(R.id.txtText);
			Main_NoteWindow.gTextNote = txttext.getText().toString();
		}
    };
	
	private OnKeyListener OnKeyListenertxtText = new OnKeyListener()
    {	  
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			EditText txttext = (EditText)findViewById(R.id.txtText);
			Main_NoteWindow.gTextNote = txttext.getText().toString();
			return false;
		}
    };
	
	/*private OnFocusChangeListener OnFocusChangeListenerTxtTitulo = new OnFocusChangeListener()
    {	  
		public void onFocusChange(View v, boolean hasFocus)
        {	
			if (!hasFocus) {
				EditText txttitle = (EditText)findViewById(R.id.txtTitulo);
				String ltexttitle = txttitle.getText().toString();
				if ( ltexttitle == null ) {
					ltexttitle = getString(R.string.automaticTitle);
					txttitle.setText(ltexttitle);
				}
				Main_NoteWindow.gTitleNote = ltexttitle;
			}
        }
    };*/
    
    /*private OnFocusChangeListener OnFocusChangeListenerTxtText = new OnFocusChangeListener()
    {	  
		public void onFocusChange(View v, boolean hasFocus)
        {	
			if (!hasFocus) {
				EditText txttext = (EditText)findViewById(R.id.txtText);
				Main_NoteWindow.gTextNote = txttext.getText().toString();
			}
        }
    };*/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
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
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
