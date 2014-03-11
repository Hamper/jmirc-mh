package jmIrc;
/* TextArea.java 07.04.2008 */
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

import java.io.UTFDataFormatException;
import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

public class TextArea {
	private int mls; // max lines in screen
	private int ActiveUp=-1;//верхняя граница выделяемого текста (>=position&&<=mls)
	private int ActiveDown=-1;//нижняя граница выделяемого текста (>=ActiveUp&&<=mls)
	
	private int position, emptylines;
	private boolean scrollbar;

	private String[][] scrollbuffer;
	private int bufindex;//индекс последнего элемента в буфере

	private UIHandler uihandler;
	
	private final int MAX_LINES;
	
	private int wHeight,wWidth,wTop,wLeft,hTop;
	
	public static final int wScrollBar=3;//ширина скролбара
	
	public static final char cBegin='\r';//символ начала строки

	public static final char cRem=(char)(27);//символ для удаления
	public static final char cbUTF=(char)(23);//символ начала строки автоопределения UTF8
	public static final char ceUTF=(char)(24);//символ конца строки автоопределения UTF8
	public static final char cbURL=(char)(25);//символ начала URL
	public static final char ceURL=(char)(26);//символ конца URL
	public static final char cCHAR=(char)(30);//символ заменитель
		
//	private boolean repaint=true;
	
	public TextArea(UIHandler uihandler,int wWidth,int wHeight,int wTop,int hTop,int wLeft,boolean scrollbar) {
		this.scrollbar = scrollbar;
		this.uihandler=uihandler;
		this.wHeight=wHeight;
		this.wWidth=wWidth;
		this.wTop=wTop;
		this.hTop=hTop;
		this.wLeft=wLeft;
		mls=wHeight/uihandler.FontHeight; // max lines in screen
		MAX_LINES=Database.BufLines;
		clear();
	}

	public void setSize(int newwidth,int newheight,int newtop) {
		boolean end=isAtEndpos();
		wHeight=newheight;
		wWidth=newwidth;
		wTop=newtop;
		mls=wHeight/uihandler.FontHeight; // max lines in screen
		if(end)End();
	}		
	
	public boolean LineDown(){
/*
 * Перемещение на одну линию вниз, пока выделенная строка не окажется внизу,
 * перемещается выделенная строка, как только оказалась, перемещается указатель	
 * 
 */		
		if(ActiveDown<MAX_LINES-emptylines-1||position+mls<MAX_LINES-emptylines){//если не конец экрана
			if(ActiveDown+1>position+mls){position++;return true;}//если следующие строки для выделения ниже экрана, то просто смещаем экран
			ActiveDown++;
			ActiveUp=ActiveDown;//перемещаем вернюю строку на следующую после предыдущей нижней
//			пока не достигли конца экрана и следующая строка не помечена как начальная, перемещаем нижнюю вниз
			ActiveDown++;
			while(ActiveDown<MAX_LINES-emptylines&&scrollbuffer[(ActiveDown+bufindex+emptylines)%MAX_LINES][0].charAt(1)!=cBegin)ActiveDown++;
			if(ActiveDown<=MAX_LINES-emptylines)ActiveDown--;//если не последняя строка экрана, то перескачили
			if(ActiveUp+1>position+mls)position++;//если следующие строки для выделения ниже экрана, то просто смещаем экран
			return true;
		}
		return false;
	}
	
	public boolean LineUp(){ 
/*
 * Перемещение на одну линию вверх, пока выделенная строка не окажется наверху, 
 * перемещается выделенная строка, как только оказалась, перемещается указатель 
 * на верхнюю строку
 */
		if(ActiveUp>0||position>0){//если не начало экрана
			if(ActiveUp<position){position--;return true;}//если следующие строки для выделения выше экрана, то просто смещаем экран
			ActiveUp--;
			ActiveDown=ActiveUp;//перемещаем вернюю строку на следующую после предыдущей верхней
//			пока не достигли начала экрана и следующая строка не помечена как начальная, перемещаем нижнюю вниз
			while(ActiveUp>0&&scrollbuffer[(ActiveUp+bufindex+emptylines)%MAX_LINES][0].charAt(1)!=cBegin)ActiveUp--;
			if(ActiveDown<position)position--;//если следующие строки для выделения ниже экрана, то просто смещаем экран
			return true;
		}
		return false;
	}
	
	public boolean PageDown(){
		if(position+mls>=MAX_LINES-emptylines)return End();//если строк меньше, чем экран
		if(ActiveUp<=position+mls-1&&ActiveDown>=position+mls-1)position+=mls;//если активная линия уже внизу
		if(position+mls>=MAX_LINES-emptylines)return End();
		ActiveUp=ActiveDown=position+mls-1;
		while(ActiveUp>0&&scrollbuffer[(ActiveUp+bufindex+emptylines)%MAX_LINES][0].charAt(1)!=cBegin)ActiveUp--;
		ActiveDown=ActiveUp+1;
		while(ActiveDown<MAX_LINES-emptylines&&scrollbuffer[(ActiveDown+bufindex+emptylines)%MAX_LINES][0].charAt(1)!=cBegin)ActiveDown++;
		if(ActiveDown<MAX_LINES-emptylines)ActiveDown--;//если не последняя строка экрана, то перескачили
		return true;
	}
	
	public boolean PageUp(){
		if(position==0)return Home();
		if(ActiveUp<=position&&ActiveDown>=position)position-=mls;//если активная линия уже вверху
		if(position<0)return Home();
		ActiveUp=ActiveDown=position;
		while(ActiveUp>0&&scrollbuffer[(ActiveUp+bufindex+emptylines)%MAX_LINES][0].charAt(1)!=cBegin)ActiveUp--;
		ActiveDown=ActiveUp+1;
		while(ActiveDown<MAX_LINES-emptylines&&scrollbuffer[(ActiveDown+bufindex+emptylines)%MAX_LINES][0].charAt(1)!=cBegin)ActiveDown++;
		if(ActiveDown<MAX_LINES-emptylines)ActiveDown--;//если не последняя строка экрана, то перескачили
		return true;
	}
	
	public boolean End(){
//		if(ActiveDown==MAX_LINES-emptylines-1)return false;
		position=MAX_LINES-emptylines-mls;
		if(position<0)position=0;
		ActiveUp=ActiveDown=MAX_LINES-emptylines-1;
		while(ActiveUp>0&&scrollbuffer[(ActiveUp+bufindex+emptylines)%MAX_LINES][0].charAt(1)!=cBegin)ActiveUp--;
		return true;
	}
	
	public boolean Home(){ 
		if(position==0&&ActiveUp==0)return false;
		position=ActiveUp=ActiveDown=0;
		ActiveDown++;
		while(ActiveDown<MAX_LINES-emptylines&&scrollbuffer[(ActiveDown+bufindex+emptylines)%MAX_LINES][0].charAt(1)!=cBegin)ActiveDown++;
		if(ActiveDown<MAX_LINES-emptylines)ActiveDown--;//если не последняя строка экрана, то перескачили
		return true;
	}

	public String GetSelectedText(boolean color,boolean url,boolean n){
		String s="",str;
		char ccol=0;
		int icol=0,ifon=0,lastcol=-1,lastfon=-1;
		boolean bold=false,italic=false,underline=false,closecolor=false;
		if(ActiveUp==-1||ActiveDown==-1)return "";//окно очищено
		for(int i=ActiveUp;i<=ActiveDown;i++){
			String ss[]=scrollbuffer[(emptylines==0?(i+bufindex)%MAX_LINES:i)];
			if(i>ActiveUp&&n){s+="%n%";lastcol=-1;lastfon=-1;}
			for(int j=0;j<ss.length;j++){
				ccol=ss[j].charAt(0);
				str=Utils.ColorToCode(ss[j].substring(1));/*FIXME ColorToCode ?*/
				if(str.charAt(0)==cBegin)str=str.substring(1);
				if(!url)str=Utils.Remove(str,new String[]{""+cbURL,""+ceURL,""+cbUTF,""+ceUTF});
				if(color){
					if((ccol&(Font.STYLE_BOLD<<8))!=0){
						if(!bold){
							s+="%b%";bold=true;
						}
					}
					else if(bold){
						s+="%b%";bold=false;
					}
					
					if((ccol&(Font.STYLE_ITALIC<<8))!=0){
						if(!italic){
							s+="%i%";italic=true;
						}
					}
					else if(italic){
						s+="%i%";italic=false;
					}
					if((ccol&(Font.STYLE_UNDERLINED<<8))!=0){
						if(!underline){
							s+="%u%";underline=true;
						}
					}
					else if(underline){
						s+="%u%";underline=false;
					}
					icol=ccol&0x0f;
					ifon=(ccol>>4)&0x0f;
					if(/*(j!=0)&&*/(lastcol!=icol||lastfon!=ifon)){//пропускаем первый цвет?, цвет текста или фона поменялся
						closecolor=(ccol&~(8<<8))==~(8<<8);
						s+="%c%"+(!(closecolor)?(icol<10?"0"+icol:""+icol)+(lastfon!=ifon&&j>0/*пропускаем первый фон*/?","+(ifon<10?"0"+ifon:""+ifon):""):"%c%");
					}
					lastcol=icol;
					lastfon=ifon;
				}
				s+=str;
			}
		}
		return s+(color&&!closecolor?"%c%":"")/*закрываем тег цвета*/;
	}
	
	public boolean isAtEndpos() {
		return ActiveDown==MAX_LINES-emptylines-1;
	}

	private void addLine(String[] strings) {
		scrollbuffer[bufindex] = strings;
		bufindex = (bufindex+1)%MAX_LINES;

		if (emptylines > 0) emptylines--;
		else if(ActiveDown>0){
			position--;
			if(position<0)position=0;
			ActiveUp--;
			if(ActiveUp<0)ActiveUp=0;
			ActiveDown--;
		}
		
	}

	/* First character or String is the colour with following rules:
	     lowest 4 bits indicate the foreground colour
	     next 4 bits indicate the background colour
	     next 4 bits indicate the font style as in Font.STYLE_* (only 3 in use) */
	/* я заюзал 4-й бит :) */
	public void addText(String[] strings) {
		String tmpline;
		Font font;
		Vector rets, retc;
		int new_width;
		int tmp_width;
		boolean endspace;
		rets = new Vector();
		retc = new Vector();
		if (Database.FontSize!=4){
			font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN,Database.FontSize);
		} else font = null;
		new_width = 0;
		endspace = false;

		for (int i=0; i<strings.length; i++) {
			String line;
			String[] s;
			char currentcol;

			line = null;
			tmpline = null;
			currentcol = strings[i].charAt(0);
			strings[i] = strings[i].substring(1);
			if((currentcol&(8<<8))==8<<8)currentcol^=(8<<8);//убираем бит, обозначающий закрытие тега цвета
			if (Database.FontSize==4) {
				if ((((currentcol >> 8)&0x0f)&Font.STYLE_BOLD) != 0) jmIrc.bmpFont = jmIrc.bmBFont; 
				else jmIrc.bmpFont = jmIrc.bmFont;
				font = null;
			} else font = Font.getFont(Font.FACE_SYSTEM, (currentcol >> 8)&0x0f,Database.FontSize);
			s = Utils.splitString(strings[i], " ");
			for (int j=0; j<s.length; j++) {
				// Notice that width includes now scrollbar so it's decreased here in 3 places
				// using 2 pixels for scrollbar so we decrease 5 (1 pixel left, 1 right)
				//------- шрифт ----------------
				if (Database.FontSize==4) {
					tmp_width = jmIrc.bmpFont.getStringWidth(" " + s[j]);
				}else {
					tmp_width = font.stringWidth(" " + s[j]);
				}
				//-------------------------------------
				if (tmpline == null) {
					// colour just changed, we need special handling
					tmpline = s[j];

					// also special linechange handling
					if (Database.FontSize==4) {
						tmp_width = jmIrc.bmpFont.getStringWidth(tmpline);
					}else {
						tmp_width = font.stringWidth(tmpline);
					}
					//-------------------------------------
					if (new_width + tmp_width>wWidth-5) {
						if (endspace) {
							line = "";
							tmpline = " " + tmpline;
						}
						else {
							int k;
							if (Database.FontSize==4) {
								for(k=1; new_width + jmIrc.bmpFont.getStringWidth(tmpline.substring(0, k))<wWidth-5; k++);
							}else{
								for(k=1; new_width + font.stringWidth(tmpline.substring(0, k))<wWidth-5; k++);
							}
							//проверяем на разделение UTF байта (1-3 символа)
							for(int ki=0;ki<3;ki++){
								try{
									if(k>0)Utils.decodeUTF8(Utils.stringToByteArray(tmpline.substring(0,k),Database.Encoding),false);
								}catch (UTFDataFormatException udfe) {
									k--;	
									continue;
								}
								break;
							}
							if(k<=0)k=1;
							line = tmpline.substring(0,k-1);
							tmpline = /*" " +*/ tmpline.substring(k-1);
						}
						new_width = 0;
					}
					if (Database.FontSize==4) {
						new_width += jmIrc.bmpFont.getStringWidth(tmpline);
					}else {
						new_width += font.stringWidth(tmpline);
					}
				}
				// linechange handling
				else if (new_width + tmp_width > wWidth-5) {
					line = tmpline;
					tmpline = " " + s[j];
					if (Database.FontSize==4) {
						new_width = jmIrc.bmpFont.getStringWidth(tmpline);
					}else {
						new_width = font.stringWidth(tmpline);
					}
				}
				// normal adding
				else {
					tmpline += " " + s[j];
					new_width += tmp_width;
				}

				while (line != null) {
					// we don't want to add an empty line
					if (!line.equals("")) {
						rets.addElement(line);
						retc.addElement(new Character(currentcol));
					}

					String[] sarray = new String[rets.size()];
					for (int k=0; k<sarray.length; k++)
						sarray[k] = ((Character) retc.elementAt(k)).charValue() + 
						            (String) rets.elementAt(k);
					addLine(sarray);

					rets.removeAllElements();
					retc.removeAllElements();

					if (Database.FontSize==4) {
						tmp_width = jmIrc.bmpFont.getStringWidth(tmpline);
					} else {
						tmp_width = font.stringWidth(tmpline);
					}
					if (tmp_width > wWidth-5) {
						int k;
						if (Database.FontSize==4) {
							for(k=1; jmIrc.bmpFont.getStringWidth(tmpline.substring(0, k))<wWidth-5; k++);
						} else {
							for(k=1; font.stringWidth(tmpline.substring(0, k))<wWidth-5; k++);
						}
						//проверяем на разделение UTF байта (1-3 символа)
						for(int ki=0;ki<3;ki++){
							try{
								if(k>0)Utils.decodeUTF8(Utils.stringToByteArray(tmpline.substring(0,k-1),Database.Encoding),false);
							}catch (UTFDataFormatException udfe) {
								k--;
								continue;
							}
							break;
						}
						if(k<=0)k=1;
						line = tmpline.substring(0, k-1);
						tmpline =/* " " +/**/ tmpline.substring(k-1);
						if (Database.FontSize==4) {
							new_width = jmIrc.bmpFont.getStringWidth(tmpline);
						}else {
							new_width = font.stringWidth(tmpline);
						}
					}
					else line = null;
				}
			}
			rets.addElement(tmpline);
			retc.addElement(new Character(currentcol));

			if (tmpline.length() > 0 && tmpline.charAt(tmpline.length()-1) == ' ') endspace = true;
			else endspace = false;
		}

		if (rets.size() > 0 && retc.size() > 0) {
			String[] sarray = new String[rets.size()];
			for (int i=0; i<sarray.length; i++) {
				sarray[i] = ((Character) retc.elementAt(i)).charValue() + 
				            (String) rets.elementAt(i);
			}
			addLine(sarray);

			rets.removeAllElements();
			retc.removeAllElements();
		}
	}
	
	private void DrawStylusLines(Graphics g){
		int Line = uihandler.FontHeight;
		if(Line<wWidth/15)Line=wWidth/15;
		g.setColor(180,180,255);
		g.drawLine(Line*2,wTop+Line,wWidth-Line*3,wTop+Line);//Top
		g.drawLine(0,wTop,wWidth,wTop);//Top 2

		
		g.drawLine(0,wTop+Line*2,Line*2,wTop+Line*2);//Top Left
		g.drawLine(Line*2,wTop,Line*2,wTop+Line*2);//Top Left
		g.drawLine(wWidth-Line*3,wTop+Line*2,wWidth-Line,wTop+Line*2);//Top Right
		g.drawLine(wWidth-Line*3,wTop,wWidth-Line*3,wTop+Line*2);//Top Right
		g.drawLine(Line*2,wTop+wHeight-Line,wWidth-Line*3,wTop+wHeight-Line);//Bottom
		g.drawLine(0,wTop+wHeight,wWidth,wTop+wHeight);//Bottom 2
		g.drawLine(0,wTop+wHeight-Line*2,Line*2,wTop+wHeight-Line*2);//Bottom Left
		g.drawLine(Line*2,wTop+wHeight-Line*2,Line*2,wTop+wHeight);//Bottom Left
		g.drawLine(wWidth-Line*3,wTop+wHeight-Line*2,wWidth-Line,wTop+wHeight-Line*2);//Bottom Right
		g.drawLine(wWidth-Line*3,wTop+wHeight-Line*2,wWidth-Line*3,wTop+wHeight);//Bottom Right
		g.drawLine(Line,wTop+Line*2,Line,wTop+wHeight-Line*2);//Left
		g.drawLine(wWidth-Line*2,wTop+Line*2,wWidth-Line*2,wTop+wHeight-Line*2);//Right
		g.drawLine(wWidth-Line,wTop,wWidth-Line,wTop+wHeight);//Right Scroll
		
//run names list
		g.drawLine(wWidth-Line*2,wTop+wHeight-Line,wWidth-Line,wTop+wHeight-Line);
		g.drawLine(wWidth-Line*2,wTop+wHeight-Line,wWidth-Line*2,wTop+wHeight);
//		g.drawLine(wWidth-Line,wTop+wHeight-Line,wWidth,wTop+wHeight);
//run Message		
/*		g.drawLine(0,wTop+wHeight-Line,Line,wTop+wHeight-Line);//Bottom Left
		g.drawLine(Line,wTop+wHeight-Line,Line,wTop+wHeight);//Bottom Left
		g.drawLine(Line,wTop+wHeight-Line,0,wTop+wHeight);//Bottom Left
*/		
//close window
/*
		g.drawLine(wWidth-Line,wTop+Line,wWidth,wTop+Line);//Top Right
		g.drawLine(wWidth-Line,wTop,wWidth-Line,wTop+Line);//Top Right	
		g.drawLine(wWidth-Line,wTop+Line,wWidth,wTop);
		g.drawLine(wWidth-Line,wTop,wWidth,wTop+Line);	
*/		
//		g.setColor(255,0,0);
		int j=wWidth/(uihandler.NumsWind);
		for(int i=1;i<uihandler.NumsWind;i++)g.drawLine(i*j,hTop,i*j,hTop+uihandler.FontHeight-1);
	}
	
	public boolean SetPos(int y){
		y=y*(MAX_LINES-emptylines-mls)/(wHeight-uihandler.FontHeight);
		if(y==position)return false;
		if(y>position)ActiveUp=ActiveDown=(position=y)+mls-1;
		if(y<position)ActiveUp=ActiveDown=position=y;
		while(ActiveUp>0&&scrollbuffer[(ActiveUp+bufindex+emptylines)%MAX_LINES][0].charAt(1)!=cBegin)ActiveUp--;
		ActiveDown=ActiveUp+1;
		while(ActiveDown<MAX_LINES-emptylines&&scrollbuffer[(ActiveDown+bufindex+emptylines)%MAX_LINES][0].charAt(1)!=cBegin)ActiveDown++;
		if(ActiveDown<MAX_LINES-emptylines)ActiveDown--;//если не последняя строка экрана, то перескачили
		if(position+mls>=MAX_LINES-emptylines)return End();//если строк меньше, чем экран
		return true;
	}
/* FIXME	
	public void SetPos(String Text,boolean Up){
		int i,j;
		String s="";
		if(Up){
			i=position+mls;
			if(i>MAX_LINES-emptylines)i=MAX_LINES-emptylines;
			while(i>0&&scrollbuffer[(i+bufindex+emptylines)%MAX_LINES][0].charAt(1)!=cBegin)i--;
			while(i>0){
				while(scrollbuffer[(i+bufindex+emptylines)%MAX_LINES][0].charAt(1)!=cBegin){
					String ss[]=scrollbuffer[(emptylines==0?(i+bufindex)%MAX_LINES:i)];
					for(j=0;j<ss.length;j++)s+=ss[j].substring(1);
				}
				if(s.indexOf(Text)>=0){SetPos(i);break;}
				i--;
			}
		}
		else {
			i=position-mls;
			if(i<0)i=0;
			while(i>0&&scrollbuffer[(i+bufindex+emptylines)%MAX_LINES][0].charAt(1)!=cBegin)i--;
			while(i<MAX_LINES-emptylines){
				while(scrollbuffer[(i+bufindex+emptylines)%MAX_LINES][0].charAt(1)!=cBegin){
					String ss[]=scrollbuffer[(emptylines==0?(i+bufindex)%MAX_LINES:i)];
					for(j=0;j<ss.length;j++)s+=ss[j].substring(1);
				}
				if(s.indexOf(Text)>=0){SetPos(i);break;}
				i--;
			}
		}
	}
*/	
	
	public void draw(Graphics g) {
//		if(!repaint)return;
		char lastcolour;
		g.setFont(uihandler.font);
		g.setColor(Utils.getColor(Database.Theme==jmIrc.DarkThemes?0:1));//text

		// loops through every line on screen
		
		int bURL,eURL;
		bURL=0;
		eURL=0;
		int bUTF8,eUTF8;
		bUTF8=0;
		eUTF8=0;
		char currentcol;
		char c;
		boolean b;
		for(int i=0; i<mls; i++) {
			int leftpixels = 1 +wLeft;
			int idx = (bufindex+emptylines+position+i)%scrollbuffer.length;//упростить idx++ !
			String[] strings = scrollbuffer[idx];

			lastcolour = 255;
			if (strings == null) break; // we get null and stop iterating
			if (strings[0].charAt(1) == ' ')
				if (Database.FontSize==4) {
					leftpixels += jmIrc.bmFont.getStringWidth(" ");
				}else{
					leftpixels += g.getFont().stringWidth(" ");
				}

			b=(i>=(ActiveUp-position)&&i<=(ActiveDown-position));
			if(b){
				if(Database.Theme==jmIrc.DarkThemes)g.setColor(255-225,255-225,255-225);
				else g.setColor(225,225,225); 
				g.fillRect(wLeft,wTop+i*uihandler.FontHeight,wWidth-1,uihandler.FontHeight);
				if(Database.Theme==jmIrc.DarkThemes)g.setColor(255-200,255-200,255-200);
				else g.setColor(200,200,200);
				if(i==(ActiveUp  -position))g.drawLine(wLeft,wTop+i*uihandler.FontHeight,wWidth-1,wTop+i*uihandler.FontHeight);
				if(i==(ActiveDown-position))g.drawLine(wLeft,wTop+(i+1)*uihandler.FontHeight-1,wWidth-1,wTop+(i+1)*uihandler.FontHeight-1);
			}
			for (int j=0; j<strings.length; j++) {
				currentcol = strings[j].charAt(0);
				if((currentcol&(8<<8))==8<<8)currentcol^=(8<<8);//убираем бит, обозначающий закрытие тега цвета

				String currentstr = strings[j].substring(1);
				c=currentstr.charAt(0);
				if(c==' '&&currentstr.length()>1)c=currentstr.charAt(1);
				if(c==cBegin||c==cbURL||c==ceURL||c==cbUTF||c==ceUTF){
					if(currentstr.charAt(0)==' ')currentstr=' '+currentstr.substring(2);
					else currentstr=currentstr.substring(1);//символ начала строки
				}
				if(c==cBegin)bURL=eURL=bUTF8=eUTF8=0;
				if(c==cbURL)bURL++;
				if(c==ceURL)eURL++;
				if(eURL>bURL)eURL--;//если начало ссылки за экраном
				if(c==cbUTF)bUTF8++;
				if(c==ceUTF)eUTF8++;
				if(eUTF8>bUTF8)eUTF8--;//если начало текста за экраном
				
				if(currentstr.equals(""))continue;
				if (currentstr.charAt(0) == ' ' && j==0) currentstr = currentstr.substring(1);
				if (currentcol!=lastcolour||b||Database.FindURLs==2) {
					// set new style
					c=currentcol;
					if(bURL>eURL&&(b||Database.FindURLs==2))c|=(Font.STYLE_UNDERLINED<<8);
				if (Database.FontSize==4) {
					if ((((c>>8)&0x0f)&Font.STYLE_BOLD) != 0) jmIrc.bmpFont = jmIrc.bmBFont; else jmIrc.bmpFont = jmIrc.bmFont;
				} else {
					g.setFont(Font.getFont(Font.FACE_SYSTEM,(c>>8)&0x0f,Database.FontSize));
				}
					// set background colour and paint it to screen
//					фон выделенного фрагмента
					if(!b){
						g.setColor(Utils.getColor((currentcol >> 4)&0x0f));
						if (Database.FontSize==4) {
							g.fillRect(leftpixels, wTop+i*uihandler.FontHeight, jmIrc.bmpFont.getStringWidth(currentstr), uihandler.FontHeight);
						} else	{
							g.fillRect(leftpixels,wTop+i*uihandler.FontHeight, g.getFont().stringWidth(currentstr),uihandler.FontHeight);
						}
					}
					// set font colour and update lastcolour
					if(bURL>eURL&&(b||Database.FindURLs==2))g.setColor(Utils.getColor(12));
					else g.setColor(Utils.getColor(currentcol&0x0f));
					
					lastcolour = currentcol;
				}
				try {
					if(bUTF8>eUTF8&&b)currentstr=Utils.decodeUTF8(Utils.stringToByteArray(currentstr,Database.Encoding),true);
				} catch (UTFDataFormatException udfe){}
				if (Database.FontSize==4) {
					if ((((currentcol >> 8)&0x0f)&Font.STYLE_BOLD) != 0) jmIrc.bmpFont = jmIrc.bmBFont; else jmIrc.bmpFont = jmIrc.bmFont;
					jmIrc.bmpFont.drawString(g, leftpixels, wTop+i*uihandler.FontHeight, currentstr, currentcol&0x0f);
					leftpixels += jmIrc.bmpFont.getStringWidth(currentstr);
				} else {
					g.drawString(currentstr, leftpixels,wTop+i*uihandler.FontHeight, Graphics.LEFT | Graphics.TOP);
					leftpixels += g.getFont().stringWidth(currentstr);
				}
			}
		}
		if(Window.Stylus)DrawStylusLines(g);

		if (scrollbar && MAX_LINES - emptylines > mls) {
			int startpos = ((position*(wHeight-uihandler.FontHeight)) / (MAX_LINES-emptylines-mls));
			if(Database.Theme==jmIrc.DarkThemes)g.setColor(255-200,255-200,255-200);
			else g.setColor(200,200,200); 
			g.fillRect(wWidth-wScrollBar,wTop, wScrollBar,wHeight);
			if(Database.Theme==jmIrc.DarkThemes)g.setColor(255-0,255-0,255-0);
			else g.setColor(0,0,0);
			g.fillRect(wWidth-wScrollBar,wTop+startpos, wScrollBar,uihandler.FontHeight);
		}
	}

	public void clear() {
		scrollbuffer = new String[MAX_LINES][];
		emptylines = MAX_LINES;
		ActiveUp=-1;
		ActiveDown=-1;
		bufindex=0;
		position=0;
		System.gc();
	}
}
