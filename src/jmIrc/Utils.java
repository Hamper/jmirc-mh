package jmIrc;

/* Utils.java 13.10.2008 */
/************************************************************************
 *   jmIrc-m
 *   Copyright (C) 2007 Archangel, HelpTeam Leader Dal.Net.Ru
 *   Copyright (C) 2007 Hamper
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

import java.io.ByteArrayOutputStream;
import java.io.UTFDataFormatException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
//import java.util.TimeZone;
import java.util.Hashtable;
import java.util.Vector;

public class Utils {
	
	// We save about 1 kB when inputting these as strings instead of arrays
	protected static char[] koi8rmap = "\u2500\u2502\u250C\u2510\u2514\u2518\u251C\u2524\u252C\u2534\u253C\u2580\u2584\u2588\u258C\u2590\u2591\u2592\u2593\u2320\u25A0\u2219\u221A\u2248\u2264\u2265\u00A0\u2321\u00B0\u00B2\u00B7\u00F7\u2550\u2551\u2552\u0451\u2553\u2554\u2555\u2556\u2557\u2558\u2559\u255A\u255B\u255C\u255D\u255E\u255F\u2560\u2561\u0401\u2562\u2563\u2564\u2565\u2566\u2567\u2568\u2569\u256A\u256B\u256C\u00A9\u044E\u0430\u0431\u0446\u0434\u0435\u0444\u0433\u0445\u0438\u0439\u043A\u043B\u043C\u043D\u043E\u043F\u044F\u0440\u0441\u0442\u0443\u0436\u0432\u044C\u044B\u0437\u0448\u044D\u0449\u0447\u044A\u042E\u0410\u0411\u0426\u0414\u0415\u0424\u0413\u0425\u0418\u0419\u041A\u041B\u041C\u041D\u041E\u041F\u042F\u0420\u0421\u0422\u0423\u0416\u0412\u042C\u042B\u0417\u0428\u042D\u0429\u0427\u042A".toCharArray();
	// notes: #154 is a non-breaking space
	protected static char[] cp1251map = "\u0402\u0403\u201A\u0453\u201E\u2026\u2020\u2021\u20AC\u2030\u0409\u2039\u040A\u040C\u040B\u040F\u0452\u2018\u2019\u201C\u201D\u2022\u2013\u2014\uFFFD\u2122\u0459\u203A\u045A\u045C\u045B\u045F\u00A0\u040E\u045E\u0408\u00A4\u0490\u00A6\u00A7\u0401\u00A9\u0404\u00AB\u00AC\u00AD\u00AE\u0407\u00B0\u00B1\u0406\u0456\u0491\u00B5\u00B6\u00B7\u0451\u2116\u0454\u00BB\u0458\u0405\u0455\u0457\u0410\u0411\u0412\u0413\u0414\u0415\u0416\u0417\u0418\u0419\u041A\u041B\u041C\u041D\u041E\u041F\u0420\u0421\u0422\u0423\u0424\u0425\u0426\u0427\u0428\u0429\u042A\u042B\u042C\u042D\u042E\u042F\u0430\u0431\u0432\u0433\u0434\u0435\u0436\u0437\u0438\u0439\u043A\u043B\u043C\u043D\u043E\u043F\u0440\u0441\u0442\u0443\u0444\u0445\u0446\u0447\u0448\u0449\u044A\u044B\u044C\u044D\u044E\u044F".toCharArray();
	// notes: #152 not used in cp1252 so we encode it to '?'
	//        #160 is a non-breaking space, #173 is a soft hyphen
	protected static Hashtable hashmap = null;

	protected static String byteArrayToString(byte[] bytes, String encoding) {
		char[] map = null;
		String ret;
/*
		if (utf8fallback) {
			// we need to have some 1-byte fallback, let's use latin1
			if (encoding.equals("UTF-8"))
				encoding = "Windows-1251";//Database?

			try {
				ret=Utils.decodeUTF8(bytes, false);
				return ret;
			} catch (UTFDataFormatException udfe) {}
		}
*/
		if (encoding.equals("KOI8-R"))
			map = koi8rmap;
		else if (encoding.equals("Windows-1251"))
			map = cp1251map;

		if (map != null) {
			char[] chars = new char[bytes.length];
			for (int i=0; i<bytes.length; i++) {
				byte b = bytes[i];
				chars[i] = (b >= 0) ? (char) b : map[b+128];
			}
			ret = new String(chars);
		}
		else if (encoding.equals("UTF-8")) {
			try {
				ret = Utils.decodeUTF8(bytes, true);
			} catch (UTFDataFormatException udfe) {
				// this should never happen when gracious decoding is true
				ret = new String(bytes);
			}
		}
		else {
			try {
				ret = new String(bytes, encoding);
			} catch (UnsupportedEncodingException uee) {
				ret = new String(bytes);
			}
		}

		return ret;
	}

	protected static byte[] stringToByteArray(String string, String encoding) {
		byte[] ret;

		if (encoding.equals("KOI8-R") || encoding.equals("Windows-1251")) {
			if (hashmap == null || !encoding.equals((String) hashmap.get("encoding"))) {
				if (encoding.equals("KOI8-R"))
					hashmap = generateHashmap(koi8rmap);
				else if (encoding.equals("Windows-1251"))
					hashmap = generateHashmap(cp1251map);
				hashmap.put("encoding", encoding);
			}

			char[] chars = string.toCharArray();
			byte[] bytes = new byte[chars.length];

			for (int i=0; i<chars.length; i++) {
				if (chars[i]<0x80)
					bytes[i] = (byte) chars[i];
				else {
					Byte b = (Byte) hashmap.get(new Character(chars[i]));
					bytes[i] = (b == null) ? (byte) 0x3f : b.byteValue(); // 0x3f is '?'
				}
			}

			ret = bytes;
		}
		else if (encoding.equals("UTF-8")) {
			ret = encodeUTF8(string);
		}
		else {
			try {
				ret = string.getBytes(encoding);
			} catch (UnsupportedEncodingException uee) {
				ret = string.getBytes();
			}
		}

		return ret;
	}

	private static Hashtable generateHashmap(char[] encmap) {
		Hashtable ret = new Hashtable();

		for (int i=0; i<encmap.length; i++)
			ret.put(new Character(encmap[i]), new Byte((byte) (0x80+i)));

		return ret;
	}	
	
	private static byte[] encodeUTF8(String text) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] ret;
		
		for (int i=0; i<text.length(); i++) {
			char c = text.charAt(i);
			if (c != '\u0000' && c < '\u0080')
				baos.write(c);
			else if (c == '\u0000' || (c >= '\u0080' && c < '\u0800')) {
				baos.write((byte)(0xc0 | (0x1f & (c >> 6))));
				baos.write((byte)(0x80 | (0x3f & c)));
			}
			else {
				baos.write((byte)(0xe0 | (0x0f & (c >> 12))));
				baos.write((byte)(0x80 | (0x3f & (c >>  6))));
				baos.write((byte)(0x80 | (0x3f & c)));
			}
		}
		ret = baos.toByteArray();
		
		return ret;
	}
/*	
	public static byte[] readLine(InputStream is) throws IOException {
		byte[] ret, buf;
		int i;

		buf = new byte[512];
		for(i=0; i<512; i++) {
			int readbyte = is.read();
			if (readbyte == -1) throw new EOFException();

			buf[i] = (byte) readbyte;
			if (buf[i] == '\n')
				break;
		}

		if (i==512) return null;
		if (i>0 && buf[i-1] == '\r') i--;
		ret = new byte[i];
		System.arraycopy(buf, 0, ret, 0, i);
		return ret;
	}
/*
	public static Vector splitStringV(String str, String delims) {
		if (str == null || delims == null || delims.length() == 0)
		         return null;


	  	Vector v = new Vector();
		int pos, newpos;
		newpos = str.indexOf(delims, pos=0);
		while(newpos !=-1) {
			v.addElement(str.substring(pos, newpos));
			pos = newpos + delims.length();
			newpos = str.indexOf(delims, pos);
		}
		v.addElement(str.substring(pos));

		return v;
	}
*/	
	public static String[] splitString(String str, String delims) {
		if (str == null || delims == null || delims.length() == 0)
		         return null;

		String[] s;
	  	Vector v = new Vector();
		int pos, newpos;

		pos = 0;
		newpos = str.indexOf(delims, pos);

		while(newpos !=-1) {
			v.addElement(str.substring(pos,newpos));
			newpos=str.indexOf(delims,pos=newpos+delims.length());
		}
		v.addElement(str.substring(pos));
		
		s = new String[v.size()];
		for(int i=0; i<s.length; i++) {
			s[i] = (String) v.elementAt(i);
		}
		return s;
	}	

	
	public static boolean hasNoValue(String s) {
		return (s == null || s.equals("") || s.getBytes().length ==0);
	}
/*
	public static String URLEncode(byte[] input) {
		StringBuffer ret;
		int i, temp;

		if (input==null) return null;

		ret = new StringBuffer();
		for (i=0; i<input.length; i++) {
			temp = input[i] & 0xff; // [-128,127] to [0,255]
			if ((temp >= 0x30 && temp <= 0x39) || // 0-9
			    (temp >= 0x41 && temp <= 0x5a) || // A-Z
			    (temp >= 0x61 && temp <= 0x7a) || // a-z
			     temp == 0x2e || temp == 0x2d ||  // . and -
			     temp == 0x2a || temp == 0x5f) {  // * and _
				ret.append((char) temp);
			}
			else if (temp == 0x20) {
				ret.append('+');
			}
			else {
				ret.append('%');
				if (temp < 16) ret.append('0');
				ret.append(Integer.toHexString(temp));
			}
		}

		return ret.toString();
	}
*/

	public static String[] mergeStringArray(String[] inp1, String[] inp2) {
		String[] ret = new String[inp1.length + inp2.length];

		System.arraycopy(inp1, 0, ret, 0, inp1.length);
		System.arraycopy(inp2, 0, ret, inp1.length, inp2.length);
		return ret;
	}

    public static boolean CompareString(String s1,String s2){
    	int Len1,Len2,i=0;
    	Len1=s1.length();
    	Len2=s2.length();
    	while(i<Len1&&i<Len2){
    		if(s1.charAt(i)>s2.charAt(i))return true;
    		else
    		if(s1.charAt(i)<s2.charAt(i))return false;
    		else i++;
    	}
    	return false;
    }
    
    public static String Remove(String Text,String[] strs){
    	int i=0;
    	if(Text==""||Text==null||strs.length<=0)return Text;
    	while(i<strs.length){
    		try {
    			while(Text.indexOf(strs[i])>=0&&strs[i].length()>0)Text=(Text.indexOf(strs[i])>0?Text.substring(0,Text.indexOf(strs[i])):"")+(Text.indexOf(strs[i])+strs[i].length()<Text.length()?Text.substring(Text.indexOf(strs[i])+strs[i].length()):"");
    		}catch(Exception e){}	
    		i++;
    	}	
    	return Text;
    	
    }
/*
    public static String Replace(String s, String s1, String s2){
    	String out=" ";
    	s=s+" ";
    	int i=s.indexOf(s1);
        while(i>-1){
        	out+=s.substring(0,i)+s2;
        	s=s.substring(i+s1.length());
        	i=s.indexOf(s1);
        }
        out+=s;
        return out.substring(1,out.length()-1);
    }
*/
    
    public static String Replace(String s, String s1, String s2){
    	String out="";
		int pos,newpos,L=s1.length();
		pos=0;
		newpos=s.indexOf(s1,0);
		while(newpos!=-1&&L>0){
			out+=s.substring(pos,newpos)+s2;
			newpos=s.indexOf(s1,pos=newpos+L);
		}
		return out+s.substring(pos);
    }
 /*   
    public static String Code2Char(String s){
    	if(Utils.hasNoValue(s))return s;
    	
//    	return s;// :\
    	
    	String tmp="",chr="";
    	s=" "+s;
    	int i=s.indexOf("\\");
    	while(i>-1){
    		tmp+=s.substring(0,i);
    		if(i<=s.length()-4){
    			chr=s.substring(i+1,i+4);
    			s=(i<s.length()-4?s.substring(i+4):"");
    		}
    		else break;
    		if(s.charAt(i-1)!='\\')i=parseInt(chr);
    		else i=0;
    		if(i>0&&i!=10&&i!=13&&i<=255)tmp+=""+(char)(i);
    		else tmp+="\\"+chr;
    		i=s.indexOf("\\");
    	}
    	return (tmp.length()>1?tmp.substring(1)+s:s.substring(1));
    }
 */

    
/*
    public static String CodeToChars(String str){
    	if(Utils.hasNoValue(str))return str;
    	String[] code ={"%c%" ,"%b%" ,"%u%" ,"%i%" ,"%o%" ,"%n%","%s%"};
    	String[] color={"\003","\002","\037","\026","\017","\n" ," "};
    	for(int i=0;i<code.length;i++){
    		String tmp="";
    		int j=str.indexOf(code[i]);
        	while(j>-1){
        		tmp+=str.substring(0,j);///!!!
        		if(j==0)tmp+=color[i];
        		else if(str.charAt(j-1)!='\\')tmp+=color[i];
        		else tmp=tmp.substring(0,tmp.length()-1)+code[i];
    			str=(j+code[i].length()<str.length()?str.substring(j+code[i].length()):"");
        		j=str.indexOf(code[i]);
        	}
        	tmp+=str;
        	str=tmp;
    	}
    	return str;
    }
    
    
    public static String CodeToChars(String str){
    	if(Utils.hasNoValue(str))return str;
    	String code="%c%%b%%u%%i%%o%%n%%s%";
    	String color="\003\002\037\026\017\n\20";
    	String out="",c;
    	boolean b;
		int pos=0,newpos=str.indexOf("%",pos),k,l=str.length();
		while(newpos!=-1){
			if(newpos+3>=l)break;
			b=(newpos>0&&str.charAt(newpos-1)=='\\');
        	out+=str.substring(pos,newpos);
    		c=str.substring(newpos,newpos+3);
    		for(k=0;k<color.length();k++)
    			if(c.equals(code.substring(k*3,(k+1)*3))){
    				out+=(b?c:""+color.charAt(k));
    				newpos+=3;
    				break;
    			}
    		if(k==color.length()){
    			out+="%";
				newpos++;
    		}
			newpos=str.indexOf("%",pos=newpos);
		}
    	return out+str.substring(pos);
    }
    
    
*/
    public static String ReplaceTok(String str,String s1,String s2,int n1,int n2,boolean slash){
    	if(Utils.hasNoValue(str))return str;
    	String out="",tok;
    	int len=s1.length()/n1;
    	boolean b;
		int pos=0,newpos=str.indexOf("%",pos),k,l=str.length();
		while(newpos!=-1){
			if(newpos+n1>l)break;
			b=(slash&&newpos>0&&str.charAt(newpos-1)=='\\');
        	out+=str.substring(pos,newpos+(b?-1:0));
        	tok=str.substring(newpos,newpos+n1);
    		for(k=0;k<len;k++)
    			if(tok.equals(s1.substring(k*n1,(k+1)*n1))){
    				out+=(b?tok:s2.substring(k*n2,(k+1)*n2));
    				newpos+=n1;
    				break;
    			}
    		if(k==len){
    			out+=(b?"\\":"")+"%";
				newpos++;
    		}
			newpos=str.indexOf("%",pos=newpos);
		}
    	return out+str.substring(pos);
    }

    public static String nToChar(String str){
    	return ReplaceTok(str,"%n%","\n",3,1,true);
    }
    
    public static String CodeToChars(String str){
    	return Utils.Remove(ReplaceTok(str,"%c%%b%%u%%i%%o%%s%","\003\002\037\026\017\40",3,1,true),new String[]{""+TextArea.cRem});
    	
    }
    
    
//	String[] code ={"%c%" ,"%b%" ,"%u%" ,"%i%" ,"%o%" ,"%n%","%s%"};
//	String[] color={"\003","\002","\037","\026","\017","\n" ," "};    
    
    public static String ColorToCode(String str){
		String s1="\003\002\037\026\017\n";
		String s2="%c%%b%%u%%i%%o%%n%";
		str=ReplaceTok(str,"%c%%b%%u%%i%%o%%n%","\\%c%\\%b%\\%u%\\%i%\\%o%\\%n%",3,4,false);
    	for(int i=0;i<=4;i++)str=Replace(str,""+s1.charAt(i),s2.substring(i*3,(i+1)*3));
		return str;
    }
    
	public static String Zebra(String Text,String c1,String c2){
		String out="";int i=0;
		boolean b=((int)(Text.length()/2)*2==Text.length());
		while(i<Text.length())out+="\003"+(b?c1:c2)+","+((b=!b)?c1:c2)+Text.charAt(i++);
		return out+"\003";
	}

    
	public static int parseInt(String input) {
		int ret = 0;

		try {
			ret = Integer.parseInt(input);
		} catch (NumberFormatException nfe) {}

		return ret;
	}
 /*   
	public static String formatDateMillis(long millis) {
		String weekdays = "SunMonTueWedThuFriSat";
		String months = "JanFebMarAprMayJunJulAugSepOctNovDec";
		Calendar cal = Calendar.getInstance();

		cal.setTime(new Date(millis));
		return weekdays.substring((cal.get(Calendar.DAY_OF_WEEK)-1)*3).substring(0, 3) + " " + 
		       months.substring(cal.get(Calendar.MONTH)*3).substring(0, 3) + " " + 
		       cal.get(Calendar.DATE) + " " +
		       cal.get(Calendar.HOUR_OF_DAY) + ":" + 
		       (cal.get(Calendar.MINUTE)<10?"0":"") + cal.get(Calendar.MINUTE) + " " +
		       cal.get(Calendar.YEAR);
	}
	*/
	 public static long GetTimeStamp(){
        Calendar calendar=Calendar.getInstance();
        Date date=calendar.getTime();
        return date.getTime()/1000;
        
    }
	 
	 public static String TimeStamp(){
		 String time;
//		 TimeZone tz=TimeZone.getDefault();
	 
//		 Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+4"));
		 Calendar cal = Calendar.getInstance();
		 time=Utils.Replace(Database.TimeMask,"h",(cal.get(Calendar.HOUR_OF_DAY)<10?"0":"")+cal.get(Calendar.HOUR_OF_DAY));
		 time=Utils.Replace(time,"m",(cal.get(Calendar.MINUTE)<10?"0":"")+cal.get(Calendar.MINUTE));
		 time=Utils.Replace(time,"s",(cal.get(Calendar.SECOND)<10?"0":"")+cal.get(Calendar.SECOND));
		 return time=Utils.CodeToChars(time);
	 }
	 
	 public static boolean HewYear(){
		 Calendar cal = Calendar.getInstance();
		 return (cal.get(Calendar.MONTH)==11&&cal.get(Calendar.DATE)>20)||(cal.get(Calendar.MONTH)==0&&cal.get(Calendar.DATE)<10);
	 }
/*	 
	 public static int LastIndexOf(String Text,String str){
		 int i=-1,j;
		 j=Text.indexOf(str);
		 while(j!=-1){
			 j+=str.length();
			 i+=j;
			 Text=Text.substring(j);
			 j=Text.indexOf(str);
		 }
		 return i;
	 }
*/
	 public static int LastIndexOf(String Text,String str){
		 int i=0,j=Text.indexOf(str),L=str.length();
		 while(j!=-1&&L>0)j=Text.indexOf(str,(i=(j+=L)));
		 return i-1;
	 }

	 public static String Strip(String Text){
		 Text=Remove(Text,new String[]{"\002","\026","\037"});//"\017"
		 Text="\017"+Text+"\017";//%o%
		 int j,i=Text.indexOf("\003");
		 j=i+1;
		 while(i!=-1){
			 if(Character.isDigit(Text.charAt(j))){
				 j++;
				 if(Character.isDigit(Text.charAt(j)))j++;
			 }
			 if(Text.charAt(j)==','){
				 j++;
				 if(Character.isDigit(Text.charAt(j))){
					 j++;
					 if(Character.isDigit(Text.charAt(j)))j++;
				 }
			 }
			 Text=Text.substring(0,i)+Text.substring(j);
			 i=Text.indexOf("\003");
			 j=i+1;
		 }
		 return Remove(Text,new String[]{"\017"});
	 }
	 
	 public static String RepZebra(String Text){
		 int i=0;
		 int aliasPos,endPos;
		 String out="",alias;
		 /*
		  		int pos, newpos;

		pos = 0;
		newpos = str.indexOf(delims, pos);

		while(newpos !=-1) {
			v.addElement(str.substring(pos,newpos));
			newpos=str.indexOf(delims,pos=newpos+delims.length());
		}
		v.addElement(str.substring(pos));
		  */
		 
		 aliasPos=Text.indexOf("%zebra(");//FIXME
		 while(aliasPos>=0){
			 out+=Text.substring(0,aliasPos);//текст до первого алиаса
			 Text=Text.substring(aliasPos+7);//текст без первого %asctime(
			 endPos=Text.indexOf(")%");//конец алиаса
			 if(endPos>0)alias=Text.substring(0,endPos);//тело алиаса
			 else {//если тела нет, то ищем дальше
				 out+="%zebra(";//дополняем вырезанное началo тела
				 aliasPos=Text.indexOf("%zebra(");
				 continue;
			 }
			 i=alias.indexOf(',');
			 String st=alias.substring(i+1);//парам1,парам2
			 alias=alias.substring(0,i);//ник
			 i=st.indexOf(',');
			 String sf=st.substring(i+1);//номер1
			 st=st.substring(0,i);//номер2
			 if(!alias.equals("")&&!st.equals("")&&!sf.equals("")){
				 int t,f;
				 try{
					 t=Integer.parseInt(st);
					 f=Integer.parseInt(sf);
					 if(t>=0&&t<=15&&f>=0&&f<=15)out+=Utils.Zebra(alias,(t<10?"0"+t:""+t),(f<10?"0"+f:""+f));
					 else Integer.parseInt("D");
				 }	
				 catch(Exception e) { //ошибка преобразования
					 out+="%zebra("+alias+","+st+","+sf+")%";//дополняем вырезанное началo тела
					 Text=Text.substring(endPos);
				 }
			 }	
			 else {
				 out+="%zebra("+alias+","+st+","+sf+")%";//дополняем вырезанное началo тела
				 Text=Text.substring(endPos);
			 }
			 Text=Text.substring(endPos+2);//убираем тело алиаса
			 aliasPos=Text.indexOf("%zebra(");
		 }
		 out+=Text;
		 return nToChar(Utils.CodeToChars(out));
	 }
		
	 public static String RepS(String Text,String Formate){
		 int i,aliasPos,endPos,StartN,EndN;
		 boolean UTF8=false;
		 if(Database.utf8detect){
			 try{
				 String s=decodeUTF8(Utils.stringToByteArray(Formate,Database.Encoding),false);
				 if(s.length()<Formate.length())UTF8=true;
			 } catch (UTFDataFormatException udfe) {}
		 }
		 
		 String Tokens[] = splitString(Formate," ");
		 String out="",alias=""/*,tmp*/;
		 Text=nToChar(CodeToChars(Text));
		 i=0;
		 if(!Database.UseColor)Text=Strip(Text);
		 aliasPos=Text.indexOf("%s");
		 while(aliasPos>=0){
			 out+=Text.substring(0,aliasPos);//текст до первого алиаса
			 Text=Text.substring(aliasPos+2);//текст без первого %s
			 endPos=Text.indexOf("%");//конец алиаса
			 if(endPos>0)alias=Text.substring(0,endPos);//тело алиаса
			 else {//если тела нет, то ищем дальше
				 out+="%s";//дополняем вырезанное началo тела
				 aliasPos=Text.indexOf("%s");
				 continue;
			 }
			 i=alias.indexOf('-');
			 if(i>=0){//если есть перечисление
				 if(alias.charAt(0)=='-'){//если перечисление с 0 до N
					 try {
						 StartN=0;
						 EndN=Integer.parseInt(alias.substring(1));//пытаемся получить число
						 String tmp="";
						 while(StartN<=EndN&&EndN<Tokens.length)tmp+=" "+Tokens[StartN++];
						 out+=(UTF8?TextArea.cbUTF+"":"")+(Database.UseMircCol?tmp:Utils.Strip(tmp))+(UTF8?TextArea.ceUTF+"":"");
					 }
					 catch(Exception e) { //ошибка преобразования
						 out+="%s"+alias+"%";//не алиас, восстанавливаем
					 }
					 Text=Text.substring(endPos+1);//убираем тело алиаса
					 aliasPos=Text.indexOf("%s");//выставляем новый счётчик и ищем новый алиас
					 continue;
				 }
				 else if(alias.charAt(alias.length()-1)=='-'){//если перечисление с N до конца
					 try {
						 EndN=Tokens.length-1;
						 StartN=Integer.parseInt(alias.substring(0,alias.length()-1));//пытаемся получить число
						 String tmp="";
						 while(StartN<=EndN)tmp+=Tokens[StartN]+(StartN++!=EndN?" ":"");
						 out+=(UTF8?TextArea.cbUTF+"":"")+(Database.UseMircCol?tmp:Utils.Strip(tmp))+(UTF8?TextArea.ceUTF+"":"");
					 }	
					 catch(Exception e) { //ошибка преобразования
						 out+="%s"+alias+"%";//не алиас, восстанавливаем
					 }
					 Text=Text.substring(endPos+1);//убираем тело алиаса
					 aliasPos=Text.indexOf("%s");//выставляем новый счётчик и ищем новый алиас
					 continue;
				 }
				 else{//если 
					 try {
						 StartN=Integer.parseInt(alias.substring(0,i));//пытаемся получить первое число диапазона
						 EndN=Integer.parseInt(alias.substring(i+1));//пытаемся получить последнее число диапазона
						 String tmp="";
						 if(EndN>=StartN){//если слова в прямом порядке
							 while(StartN<=EndN&&EndN<Tokens.length)tmp+=Tokens[StartN]+(StartN++!=EndN?" ":"");
						 }	
						 else {//если слова в обратном порядке
							 while(StartN>=EndN&&StartN<Tokens.length)tmp+=Tokens[StartN]+(StartN--!=EndN?" ":"");
						 }
						 out+=(UTF8?TextArea.cbUTF+"":"")+(Database.UseMircCol?tmp:Utils.Strip(tmp))+(UTF8?TextArea.ceUTF+"":"");
					 }
					 catch(Exception e) { //ошибка преобразования
						 out+="%s"+alias+"%";//не алиас, восстанавливаем
					 }	
					 Text=Text.substring(endPos+1);//убираем тело алиаса
					 aliasPos=Text.indexOf("%s");//выставляем новый счётчик и ищем новый алиас
					 continue;
				 }
			 }
			 else {
				 try {
					 EndN=Integer.parseInt(alias);//пытаемся получить число
					 out+=(UTF8?TextArea.cbUTF+"":"")+(Database.UseMircCol?Tokens[EndN]:Utils.Strip(Tokens[EndN]))+(UTF8?TextArea.ceUTF+"":"");
				 }
				 catch(Exception e) { //ошибка преобразования
					 out+="%s"+alias+"%";//не алиас, восстанавливаем
				 }
				 Text=Text.substring(endPos+1);//убираем тело алиаса
				 aliasPos=Text.indexOf("%s");//выставляем новый счётчик и ищем новый алиас
				 continue;
			 }
		 }
		 Text=out+Text;
		 System.gc();
		 return Text;
	 }
	 
	 public static String parseTime(long seconds) {
		 long s,m,h,d;
		 boolean minus=seconds<0;
		 s=(seconds>=0?seconds:-seconds);
		 d=s/86400;
		 s-=d*86400;
		 h=s/3600;
		 s-=h*3600;
		 m=s/60;
		 s-=m*60;
		 return  new String((minus?"-":"")
				 +(d>0?d+" "+(d==1?"день":(d>=2&&d<=4?"дня":"дней"))+" ":"")
				 +(h>0?h+" "+(h==1?"час":(h>=2&&h<=4?"часа":"часов"))+" ":"")
				 +(m>0?m+" "+(m==1?"минуту":(m>=2&&m<=4?"минуты":"минут"))+" ":"")
				 +s+" "+(s==1?"секунду":(s>=2&&s<=4?"секунды":"секунд")));
	 }	

		public static boolean MIDP2(){
			return (jmIrc.MIDP.indexOf("2.")>-1);
		}

		public static String FindURL(String Text){
			String out="";
			if(Text.length()==0||Database.FindURLs==0)return Text;
			int URLIndex=IndexURL(Text.toLowerCase());
			while(URLIndex>-1){
				out+=Text.substring(0,URLIndex)+TextArea.cbURL;
				Text=Text.substring(URLIndex);
				int splitIndex;
				for(splitIndex=0; splitIndex<Text.length(); splitIndex++)if(CharIsURL(Text.charAt(splitIndex)))break;
				out+=Text.substring(0,splitIndex)+TextArea.ceURL;
				if(splitIndex<Text.length())Text=Text.substring(splitIndex);
				else {
					Text="";
					break;
				}
				URLIndex=IndexURL(Text.toLowerCase());
			}	
			return out+Text;
		}
		
		private static int IndexURL(String Text){
			int Index=Text.indexOf("http://");
			if(Index==-1)Index=Text.indexOf("https://");
			if(Index==-1)Index=Text.indexOf("ftp://");
//			if(Index==-1)Index=Text.indexOf("www.");
			if(Index==-1)Index=Text.indexOf("mailto:");
			return Index;
		}
		
		private static boolean CharIsURL(char ch){
			return (!((ch >= 0x2d && ch <= 0x3a) || //'-' or '.' or '/' or 0-9
					(ch >= 0x40 && ch <= 0x5a)   || //'@' or A-Z
					(ch >= 0x61 && ch <= 0x7a)   || //a-z
					 ch==0x7e||ch==0x23||	//'~' or '#'
					 ch == 0x3f || ch == 0x3d    || //'?' or '='
					 ch == 0x5f || ch == 0x2b    ||	//'_' or '+'
					 ch == 0x26 || ch == 0x25));     //'&' or '%'
		}
		
		public static void GetURLs(String Text,Vector URLs){
			if(Text.length()>0){
				int Index=IndexURL(Text.toLowerCase());
				if(Index>-1){
					String Temp=Text;
					if(Index>1){
						Temp=Temp.substring(0,Index-1);
						GetURLs(Temp,URLs);
					}
					Temp=Text.substring(Index);
					int splitIndex;
					for(splitIndex=0; splitIndex<Temp.length(); splitIndex++)if(CharIsURL(Temp.charAt(splitIndex)))break;
					if(splitIndex>-1){
						URLs.addElement(Temp.substring(0, splitIndex));
						Temp=Temp.substring(splitIndex);
						GetURLs(Temp,URLs);
					}
					else URLs.addElement(Temp);
				}
			}
		}
		
     public static int getColor(int numb) {
 		numb &= 0x0f;
 		switch (numb) {
			case 0:  return 0x00ffffff;
			case 1:  return 0x00000000;
			case 2:  return 0x0000007f;
			case 3:  return 0x00009300;
			case 4:  return 0x00ff0000;
			case 5:  return 0x007f0000;
			case 6:  return 0x009c009c;			
			case 7:  return 0x00fc7f00;
			case 8:  return 0x00ffff00;
			case 9:  return 0x0000fc00;
			case 10: return 0x00009393;			
			case 11: return 0x0000ffff;
			case 12: return 0x000000fc; //7f
			case 13: return 0x00ff00ff;
			case 14: return 0x007f7f7f;
			case 15: return 0x00d4d0c8;			
 		}
 		return 0x00FFFFFF;
 	}

   
     public static String Str(char c,int num){
    	 String s="";
    	 for(int i=0;i<num;i++)s+=c;
    	 return s;
     }
 	public static String decodeUTF8(byte[] data, boolean gracious) throws UTFDataFormatException {
		byte a, b, c;
		StringBuffer ret = new StringBuffer();
		try {
			for (int i=0; i<data.length; i++) {
				a = data[i];
				if ((a&0x80) == 0)
					ret.append((char) a);
				else if ((a&0xe0) == 0xc0) {
					b = data[++i];
					if ((b&0xc0) == 0x80)
						ret.append((char)(((a& 0x1F) << 6) | (b & 0x3F)));
					else {
						if (gracious) {
							ret.append("?");
							i -= 1;
						}
						else
							throw new UTFDataFormatException("Illegal 2-byte group");
					}
				}
				else if ((a&0xf0) == 0xe0) {
					b = data[++i];
					c = data[++i];
					if (((b&0xc0) == 0x80) && ((c&0xc0) == 0x80)) {
						ret.append((char)(((a & 0x0F) << 12) | ((b & 0x3F) << 6) | (c & 0x3F)));
					}
					else {
						if (gracious) {
							ret.append("?");
							i -= 2;
						}
						else
							throw new UTFDataFormatException("Illegal 3-byte group");
					}
				}
				else if (((a&0xf0) == 0xf0) || ((a&0xc0) == 0x80)) {
					if (gracious)
						ret.append("?");
					else
						throw new UTFDataFormatException("Illegal first byte of a group");
				}
			}
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			if (gracious)
				ret.append("?");
			else
				throw new UTFDataFormatException("Unexpected EOF");
		}
		
		return ret.toString();
	}
 	
 	private static long seed = System.currentTimeMillis();

    synchronized private static int nextRandom(int bits) {
            seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
            return (int)(seed >>> (48 - bits));
    }

    public static int Random(int n) {
//            if (n<=0)
//                    throw new IllegalArgumentException("n must be positive");
            if ((n & -n) == n)  // i.e., n is a power of 2
                    return (int)((n * (long)nextRandom(31)) >> 31);
            int bits, val;
            do {
                    bits = nextRandom(31);
                    val = bits % n;
            } while(bits - val + (n-1) < 0);
            return val;
    }
}