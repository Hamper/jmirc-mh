package jmIrc;

/* ResourcesUTF8.java 23.01.2008 */
/************************************************************************
 *   jmIrc-m
 *   Copyright (C) 2007 Archangel, HelpTeam Leader Dal.Net.Ru
 *	 http://jmirc-m.net.ru/
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2, or (at your option)
 *   any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *   02111-1307 USA
 *   
 *   Это свободная программа; вы можете повторно распространять ее и/или
 * 	 модифицировать ее в соответствии с Универсальной Общественной
 *   Лицензией GNU, опубликованной Фондом Свободного ПО; либо версии 2,
 *   либо (по вашему выбору) любой более поздней версии.
 *
 *   Эта программа распространяется в надежде, что она будет полезной,
 *   но БЕЗ КАКИХ-ЛИБО ГАРАНТИЙ; даже без подразумеваемых гарантий
 *   КОММЕРЧЕСКОЙ ЦЕННОСТИ или ПРИГОДНОСТИ ДЛЯ КОНКРЕТНОЙ ЦЕЛИ.  Для
 *   получения подробных сведений смотрите Универсальную Общественную
 *   Лицензию GNU.
 * 
 *   Вы должны были получить копию Универсальной Общественной Лицензии
 *   GNU вместе с этой программой; если нет, напишите по адресу: Free
 *   Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *   02111-1307 USA
 */

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;
import java.util.Hashtable;


public class ResourcesUTF8{
  public static final char DELIMITER = '=';

  private Hashtable hashtable;
  private StringBuffer buffer = new StringBuffer();
  private boolean endFile = false;

  private String fileName;
  private boolean load  = false;
  private boolean error  = false;

 
  public ResourcesUTF8 (String fileName){
	    this.load = false;
	    this.fileName = fileName;
  }
  
  public String get(String name){
	  return get(name,null,true);
  }

  public String get(String name,String Formate){
	  return get(name,Formate,true);
  }
  
  public String get(String name,boolean Error){
	  return get(name,null,Error);
  }
  
  public String get(String name,String Formate,boolean Error){
	  if(!load&&!load())return (Error?"Error read "+fileName:null);
	  String str=(String)hashtable.get(name);
	  if(str==null)return (Error?"Error "+fileName+" '"+name+"' not found!":null);
	  else return (Formate!=null?Utils.RepS(str,Formate):str);
  }

  public void remove(String name){
	  hashtable.remove((String)name);  
  }
  
  public void unload(){
	  hashtable = null;
	  load = false;
	  endFile = true;
	  buffer = null;
  }

  public boolean load(){
	  if (error)return false;
	  DataInputStream in = null;
	  try{
		  InputStream inputStream = getInputStream();
		  if (inputStream == null)	return false;
		  in = new DataInputStream (inputStream);
		  hashtable = new Hashtable();
		  endFile = false;
		  buffer = new StringBuffer();
		  String str = readKey (in);
 		  while (str != null){
			  String key = str.trim();
			  if (key.length() > 0)	{
				  String value = readValue (in);
				  if (value != null)hashtable.put (key, value.trim());
			  }
			  str = readKey (in);
		  }
		  in.close();
		  load = true;
		  return true;
	  }
	  catch (Exception io){
		  error = true;
		  unload();
		  return false;
	  }
	  finally{
		  if (in != null)try {in.close();} catch (IOException ex){}
	  }
  }

  private InputStream getInputStream(){
	  if (error) return null;
	  else return this.getClass().getResourceAsStream (fileName);
  }

  private String readKey (DataInputStream in)throws IOException{
	  if (endFile)return null;
	  buffer.setLength (0);
	  int r = -1;
	  while ((r = in.read()) > 0 && r != 0x0A && r != DELIMITER){
		  char c = (char)r;
		  buffer.append(c);
	  }
	  if (r == -1){
		  endFile = true;
		  return null;
	  }
	  return buffer.toString();
  }

  private String readValue (InputStream in)	throws IOException{
	  if (endFile)return null;

	  buffer.setLength (0);
	  int c, char2, char3;
	  int r = 0;
	  while ((r = in.read()) > 0 && r != 0x0A){
		  c =  r & 0xff;
		  switch (c >> 4){
		  case 0:
		  case 1:
		  case 2:
		  case 3:
		  case 4:
		  case 5:
		  case 6:
		  case 7:
			  buffer.append ((char)c);
			  break;

		  case 12:
		  case 13:
			  char2 = getByte (in);
			  if ((char2 & 0xC0) != 0x80)throw new UTFDataFormatException();
			  buffer.append ((char)(((c & 0x1F) << 6) | (char2 & 0x3F)));
			  break;

		  case 14:
			  char2 = getByte (in);
			  char3 = getByte (in);
			  if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))	throw new UTFDataFormatException();

			  buffer.append ((char)(((c & 0x0F) << 12) |
					  ((char2 & 0x3F) << 6) |
					  ((char3 & 0x3F) << 0)));
			  break;

		  default:throw new UTFDataFormatException();
		  }
	  }
	  return buffer.toString();
  }

  private int getByte (InputStream in)throws IOException, UTFDataFormatException{
	  int r = in.read();
	  if (r < 0 || r == 0x0A){
		  endFile = true;
		  throw new UTFDataFormatException();
	  }
	  return r;
  }
}
