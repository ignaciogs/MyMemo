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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class GeneralUtils{
	
	public static Boolean gValidPas;
	
	public static String Encrypt(String pPass) {
		String claveEncriptada = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(pPass.getBytes());
			claveEncriptada = new String(md.digest());
		} catch (Exception e) {
			//
		}
		return claveEncriptada;
	}
		
	public static String getFileNameWithoutExtension(String fileName) {
		File tmpFile = new File(fileName);
		tmpFile.getName();
		int whereDot = tmpFile.getName().lastIndexOf('.');
		if (0 < whereDot && whereDot <= tmpFile.getName().length() - 2 ) {
			return tmpFile.getName().substring(0, whereDot);
		}
		return "";
	}
	
	private static void copy(InputStream in, File file) throws IOException {
	    OutputStream out = new FileOutputStream(file);
	    try {
	      copy(in, out);
	    } finally {
	      out.close();
	    }
	  }
	private static void copy(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    while (true) {
	      int readCount = in.read(buffer);
	      if (readCount < 0) {
	        break;
	      }
	      out.write(buffer, 0, readCount);
	    }
	  }

	public static void unzip(File zipfile, File directory) throws IOException {
	    ZipFile zfile = new ZipFile(zipfile);
	    Enumeration<? extends ZipEntry> entries = zfile.entries();
	    while (entries.hasMoreElements()) {
	      ZipEntry entry = entries.nextElement();
	      File file = new File(directory, entry.getName());
	      if (entry.isDirectory()) {
	        file.mkdirs();
	      } else {
	        file.getParentFile().mkdirs();
	        InputStream in = zfile.getInputStream(entry);
	        try {
	        	copy(in, file);
	        } finally {
	          in.close();
	        }
	      }
	    }
	  }
	
	
	public static void zipDir(String zipFileName, String dir, Boolean pStructureDir) throws Exception {
        File dirObj = new File(dir);
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        addDir(dirObj, out, pStructureDir);
        out.close();
      }
      static void addDir(File dirObj, ZipOutputStream out, Boolean pStructureDir) throws IOException {
        File[] files = dirObj.listFiles();
         byte[] tmpBuf = new byte[1024];
         for (int i = 0; i < files.length; i++) {
           if (files[i].isDirectory()) {
             addDir(files[i], out, pStructureDir);
             continue;
           }
           FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
           if ( pStructureDir ) {
        	   out.putNextEntry(new ZipEntry(files[i].getAbsolutePath()));
           } else {
        	   out.putNextEntry(new ZipEntry(files[i].getName()));
           }
           int len;
           while ((len = in.read(tmpBuf)) > 0) {
             out.write(tmpBuf, 0, len);
           }
           out.closeEntry();
           in.close();
         }
       }
	
	public static Boolean renamefile (String pOriFile, String pDesFile) {		
		File file = new File(pOriFile);
		if ( file.exists() ) {
			return file.renameTo(new File(pDesFile));
		} else {
			return false;
		}
		
	}
	
	public static Date getdate () {
		Calendar date = Calendar.getInstance();        
        return date.getTime();
	}
	
	public static String getformatdate () {
		Calendar date = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
        return sdf.format(date.getTime());
	}
	
	public static String FormatDateTime (Date pDate, String pFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(pFormat);
        return sdf.format(pDate);
	}
	
	public static boolean DeleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = DeleteDirectory(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }    
        return dir.delete();
    }

}
