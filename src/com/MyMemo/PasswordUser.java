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

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;

public class PasswordUser extends Activity {
	
	Entity gEntity;
	Activity gActivity;
	List<Entity> gListEntitys;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passworduser);
        
        gActivity = this;
        
        
        gListEntitys = DataFramework.getInstance().getEntityList("params");
        TextView lTextView = (TextView)findViewById(R.id.lblPassOld);
        EditText lEditText = (EditText)findViewById(R.id.txtOldPass);
        if ( gListEntitys.size() > 0 ) { //ya existe algun password con lo que lo va a modificar
        	lTextView.setVisibility(View.VISIBLE);
        	lEditText.setVisibility(View.VISIBLE);
        	gEntity = new Entity("params", gListEntitys.get(0).getId());
        } else { //no se ha configurado aun ningun password
        	lTextView.setVisibility(View.GONE);
        	lEditText.setVisibility(View.GONE);
        	gEntity = new Entity("params");
        }
                
        Button lButton = (Button)findViewById(R.id.btnChangePass);
        lButton.setOnClickListener(OnClick_btnChangePass);
	}	
	
	private OnClickListener OnClick_btnChangePass = new OnClickListener()
    {	      	
		public void onClick(View v)
        {	
			Boolean lChangePass = false;
			if ( gListEntitys.size() > 0 ) { //Comprobamos que la clave actual sea correcta para permitir cambiarla
				EditText lTextOldPass = (EditText)findViewById(R.id.txtOldPass);
				String loldpasuser = GeneralUtils.Encrypt(lTextOldPass.getText().toString());
				String loldpasdatabase = gEntity.getString("password");
				if ( loldpasuser.equals(loldpasdatabase) ) {
					lChangePass = true;
				} else {
					lChangePass = false;
					Toast.makeText(gActivity, R.string.msg_IncorectPass, Toast.LENGTH_LONG).show();
				}
			} else {
				lChangePass = true;
			}
			
			if ( lChangePass ) { //Cambiamos la password
				EditText lTextNewPass = (EditText)findViewById(R.id.txtNewPass);
				EditText lTextRepPass = (EditText)findViewById(R.id.txtRepPass);
				String lpas1 = lTextNewPass.getText().toString();
				String lpas2 = lTextRepPass.getText().toString();
				if ( lpas1.equals(lpas2) ) {					
					gEntity.setValue("password", GeneralUtils.Encrypt(lpas1));					
					gEntity.save();
					gActivity.finish();
				} else {
					//la claves no son correctas
					Toast.makeText(gActivity, R.string.msg_PassNoIgual, Toast.LENGTH_LONG).show();
				}
			}
			
        }
    };
}
