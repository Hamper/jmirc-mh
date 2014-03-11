package jmIrc;
/* Splash.java 13.05.2008 */
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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

public class Splash extends Canvas {
	private ProgressBar Bar;
	
	private int Width,Height,L,T,WH,BT;
	private int active,Count;
	private final static int num=6;
	private int max;
	
	public Splash(int max) {
		super();
		Width=getWidth();
		Height=getHeight();
		WH=Width/15;
		L=(Width-WH*(num*2-1))/2;
		T=(Height-WH)*2/3;
		BT=(Height-WH)/2;
		active=0;
		Count=0;
		this.max=max;
	}
	
	public void Show(){
		repaint();
		Bar = new ProgressBar();
		Bar.start();
	}
	
	public int Hide(){
		Bar.loading=false;
		return Count;
	}
	
	private class ProgressBar extends Thread{
		public boolean loading = true;
		public ProgressBar(){
		}
		public void run() {
			while(loading){
				Count++;active++;
				if(active>=num)active=0;
				repaint();
				try {sleep(250);}catch (InterruptedException e){}
			}

		}
	
	}
	
	public void paint(Graphics g){
		g.setColor(255,255,255);
		g.fillRect(0,0,Width,Height);
		if(max>0){
			g.setColor(0,0,0);
			g.fillRect(L-1,BT-1,WH*2*num+2,WH/2+2);
			g.setColor(212,208,200);
			g.fillRect(L,BT,WH*2*num,WH/2);
			g.setColor(10,36,106);
			if(Count-1>max)max=Count-1;
			int m=WH*2*num*(Count-1)/max;
			g.fillRect(L,BT,m,WH/2);
		}
		for(int i=0;i<num;i++){
			if(i==active)g.setColor(255,0,0);
			else g.setColor(0,255,0);
			g.fillRect(L+i*2*WH,T,WH,WH);
			g.setColor(0,0,0);
			g.drawRect(L+i*2*WH,T,WH,WH);
		}
		
//		if(isShown()) g.drawImage(ti, 0, 0, 0);
	}
}
