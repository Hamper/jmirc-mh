package jmIrc;
/* UIHandler.java 25.03.2008 */
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;
import javax.microedition.lcdui.Font;

public class UIHandler {
	public Window console;
	
	private boolean winlock;

	public Vector Channels;
    public Vector ChanNames;
    public Vector Privates;    
    public Vector PrivNames;    

	public static String Buffer="";//Внутренний буфер обмена
	public static boolean Clock=false;
	public static boolean Traf=false;
	
	public static String HighlightStr="";
	
	public Vector[] Menus;
	public String[] Depth;
	public boolean[] MenuChange;
	public boolean Changes=false;
	
	public Vector CLMenu;
	public String CLDepth;
	
	private int currentwin;
	public int NumsWind;

	public String nick;
	public boolean KeyLock;
	private jmIrc jmirc;
	
	public String Server,Pass;
	public int Port;
	
/*
	public int Left,Top,Width,Height;//обрасть окна
	public int hTop,hHeight;//Область заголовка
	public int wTop,wHeight;//Область текста
*/	
	public Font font;//шрифт
	public int FontHeight;//Высота шрифта
	public Image Screen;
	public Graphics Graph;	
	
	private static final String[] sMenus={"jmircfav","jmIrcSlaps","jmircign","MenuST","MenuCH","MenuNL","MenuPR"};
	
	private static final int iMENUFV=0;
	private static final int iMENUAC=1;
	private static final int iMENUIG=2;
	private static final int iMENUST=3;
	private static final int iMENUCH=4;
	private static final int iMENUNL=5;
	private static final int iMENUPR=6;

	public UIHandler(jmIrc jmirc) {
		this.jmirc=jmirc;
		nick = Database.Nick;
		KeyLock = false;

		winlock = false;
        Channels =new Vector();
        ChanNames=new Vector();
        Privates =new Vector();
        PrivNames=new Vector();
        
        Menus=new Vector[7];
        Depth=new String[7];
        MenuChange=new boolean[7];
        for(int i=0;i<7;i++){
        	if(i>=3)Depth[i]="";
        	Menus[i]=LoadList(sMenus[i],i);
        	MenuChange[i]=false;
        }	
		currentwin = 0;
		NumsWind=1;

	}

	public void Console(){
		HighlightStr=Utils.CodeToChars(jmIrc.language.get("HighlightStr"));
		SetFont();
		console=new Window(this,"Status",Window.TYPE_CONSOLE);
		setDisplay(console);
	}
	
	public void SetFont(){
		if (Database.FontSize==4) {
			font=null;
			FontHeight=jmIrc.bmFont.getHeight();
		}else{
			font=Font.getFont(Font.FACE_SYSTEM,Font.STYLE_PLAIN,Database.FontSize);
			FontHeight=font.getHeight();
		} 
	}
	
	public void OpenURL(String URL){
		jmirc.OpenURL(URL);
	}
	
	public void Exit(){
		if(jmIrc.isConnected())jmIrc.disconnect(null,jmIrc.QuitMessage());
    	jmIrc.Wait=true;
		jmIrc.Reconnect=0;
		Channels.removeAllElements();
		ChanNames.removeAllElements();
		Privates.removeAllElements();
		PrivNames.removeAllElements();
		NumsWind=1;
		currentwin=0;
		setDisplay(jmIrc.mainform);
	}
	
	public final Window GetChannel(String channel){
		return GetChannel(channel,false);
	}
	

    public final Window GetChannel(String channel,boolean Activate){
        channel=channel.trim();
        int Index=ChanNames.indexOf(channel.toUpperCase());
        Window win;
        if(Index==-1){
            Activate=true;
            win=new Window(this, channel, Window.TYPE_CHANNEL);
            Index=0;
            if(!Database.SortWind)Index=Channels.size();
            while(Index<=Channels.size()-1){
            	if(Utils.CompareString(((Window)Channels.elementAt(Index)).Name,channel)){//если название текущего окна строго больше, т.е. если окна равны, то новое встанет после текущего
            		Channels.insertElementAt(win,Index);
            		ChanNames.insertElementAt(channel.toUpperCase(),Index);
                	break;
            	}
            	Index++;
            }
            if(Index==Channels.size()){
            	Channels.addElement(win);
            	ChanNames.addElement(channel.toUpperCase());
            }
            NumsWind++;
        }
        else win=(Window)Channels.elementAt(Index);
		Index++;
        if(Activate)displayWindow(Index);
        return win;
     
    }	

    public final Window GetPrivate(String priv,boolean Activate){
        priv=priv.trim();
        int Index=PrivNames.indexOf(priv.toUpperCase());
        Window win;
        if(Index==-1){
            win=new Window(this, priv, Window.TYPE_PRIVATE);
          	Privates.addElement(win);
           	PrivNames.addElement(priv.toUpperCase());
            NumsWind++;
        }
        else win=(Window)Privates.elementAt(Index);
        if(Activate)displayWindow((Index==-1?-1:1+ChanNames.size()+Index));
        return win;

    }    

    public final Window GetChanList(){
        int Index=PrivNames.indexOf("@LIST");
        if(Index>=0)((Window)Privates.elementAt(Index)).close();
        Window win=new Window(this,"@List",Window.TYPE_CHANLIST);
       	Privates.addElement(win);
        PrivNames.addElement("@LIST");
        NumsWind++;
        displayWindow(-1);
        return win;

    }    
    
    
    public final char[] GetIndicators(){
    	char StateMas[]=new char[1/*console*/+Channels.size()+1+Privates.size()];
    	if(currentwin==0)StateMas[0]=Window.STATE_SELECTED;
    	else StateMas[0]=console.state;
    	int i=1;
        for(;i<Channels.size()+1;i++)
            if(i==currentwin)StateMas[i]=Window.STATE_SELECTED;
            else StateMas[i]=((Window)Channels.elementAt(i-1)).state;
        StateMas[i]=Window.STATE_NOTWIN;
        for(;i<Channels.size()+Privates.size()+1;i++)
            if(i==currentwin)StateMas[i+1]=Window.STATE_SELECTED;
            else StateMas[i+1]=((Window)Privates.elementAt(i-Channels.size()-1)).state;
        return StateMas;
    }

	public void displayNextWindow() {
		displayWindow(currentwin+1);
	}

	public void displayPreviousWindow() {
		displayWindow(currentwin-1);

	}
	
    public void displayWindow(int num){//??
    	
        if(winlock)return;// no window changing on winlock

        if(num>Channels.size()+Privates.size())num=0;
        else if(num<0)num=Channels.size()+Privates.size();

    	if(1<=currentwin&&currentwin<=Channels.size())((Window)Channels.elementAt(currentwin-1)).state=Window.STATE_NONE;
    	else
    	if(1+Channels.size()<=currentwin&&currentwin<=Channels.size()+Privates.size())((Window)Privates.elementAt(currentwin-1-Channels.size())).state=Window.STATE_NONE;
    	else console.state=Window.STATE_NONE;

    	if(1<=num&&num<=Channels.size())setDisplay(((Displayable)((Window)Channels.elementAt(num-1))));
    	else
    	if(1+Channels.size()<=num&&num<=Channels.size()+Privates.size())setDisplay(((Displayable)((Window)Privates.elementAt(num-1-Channels.size()))));
    	else setDisplay(((Displayable)(console)));
   	
        currentwin=num;
        System.gc();
    }

    public final void deleteWindow(Window win){
        if(win.getType()==Window.TYPE_CHANNEL){
        	if(ChanNames.indexOf(win.Name.toUpperCase())==currentwin-1)currentwin--;
        	Channels.removeElement(win);
        	ChanNames.removeElement(win.Name.toUpperCase());
        }
        else if(win.getType()==Window.TYPE_PRIVATE){
        	if(PrivNames.indexOf(win.Name.toUpperCase())==currentwin-1-Channels.size())currentwin--;
        	Privates.removeElement(win);
        	PrivNames.removeElement(win.Name.toUpperCase());
        }
        NumsWind--;
        displayWindow(currentwin);
    }
    
	public void setDisplay(Displayable disp) {
		Window.autoscroll=0;
		jmIrc.display.setCurrent(disp);
		System.gc();
	}

    public final Window GetActiveWindow(){
    	if(1<=currentwin&&currentwin<=Channels.size())return (Window)Channels.elementAt(currentwin-1);
    	else
    	if(1+Channels.size()<=currentwin&&currentwin<=Channels.size()+Privates.size())return (Window)Privates.elementAt(currentwin-1-Channels.size());
    	else return console;
    }
    
	public void setWinlock(boolean lock) {
		winlock = lock;
	}

	
	private Vector LoadList(String cfg,int ListType){
		Vector List=new Vector();
		String s;
		int i,j;
		try {
			RecordStore rs = RecordStore.openRecordStore(cfg,true);
			if (rs.getNumRecords() > 0) {
//				byte[] record = rs.enumerateRecords(null, null, false).nextRecord();
				DataInputStream dis = new DataInputStream(new ByteArrayInputStream(rs.getRecord(1)/*record*/));
				int count=dis.readInt();
				for(i=0;i<count;i++){
					s=dis.readUTF();
					if(ListType==iMENUIG&&s.charAt(0)=='¤')continue;//временный игнор
					if(ListType>=iMENUST&&ListType<=iMENUPR){
						j=0;
						while(j<255&&j<s.length()&&s.charAt(j)=='.')j++;
						Depth[ListType]+=(char)j;
						s=s.substring(j);
					}
					List.addElement(s);
				}
				dis.close();
			}
			rs.closeRecordStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(!List.isEmpty())return List;
		if((ListType>=iMENUST&&ListType<=iMENUPR)||(ListType==iMENUAC)){
			String name;
			switch (ListType) {
			case iMENUAC:name="actions";break;
			case iMENUST:name="status";break;
			case iMENUCH:name="channel";break;
			case iMENUNL:name="nicklist";break;
			default:name="private";
			}
			name+=".dat";
			ResourcesUTF8 lang=new ResourcesUTF8("/"+name);
			s=lang.get(""+(i=0),false);
			while(s!=null){
				j=0;
				if(ListType!=iMENUAC){
					while(j<255&&j<s.length()&&s.charAt(j)=='.')j++;
					Depth[ListType]+=(char)j;
				}
				List.addElement(s.substring(j));
				s=lang.get(""+(++i),false);
			}
			lang.unload();
			System.gc();
		}
		return List;
	}
	
	public void SaveMenus(){
		for(int i=0;i<7;i++)if(MenuChange[i])SaveList(Menus[i],sMenus[i],i);
	}

	public void ClearMenu(int Index){
		Depth[Index]="";
		SaveList(Menus[Index]=new Vector(),sMenus[Index],Index);
		Menus[Index]=LoadList(sMenus[Index],Index);
	}
	
	private void SaveList(Vector List,String cfg,int ListType){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		if(List==null)List=new Vector();
		try {
			int j=List.size();
			dos.writeInt(j);
			for(int i=0;i<j;i++)dos.writeUTF((Depth[ListType]!=null?Utils.Str('.',(int)Depth[ListType].charAt(i)):"")+(String)List.elementAt(i));
			dos.close();
			baos.close();
			RecordStore.deleteRecordStore(cfg);
			RecordStore rs = RecordStore.openRecordStore(cfg, true);
			byte[] bytes = baos.toByteArray();
			rs.addRecord(bytes, 0, bytes.length);
			rs.closeRecordStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void AddFav(String AddText){
		AddList(Menus[0],AddText,sMenus[0],iMENUFV);
		MenuChange[0]=true;
	}

	public void AddSlap(String AddText){
		AddList(Menus[1],AddText,sMenus[1],iMENUAC);
		MenuChange[1]=true;
	}

	public void AddIgnore(String AddText){
		AddList(Menus[2],AddText,sMenus[2],iMENUIG);
		MenuChange[2]=true;
		SaveList(Menus[2],sMenus[2],2);
	}
	
	private void AddList(Vector List,String AddText,String cfg,int ListType){
		int i=0,j=List.size();
		if(ListType==iMENUIG)i=j;
		for(;i<j;i++) {
			if(AddText.compareTo((String)List.elementAt(i))<0){
				List.insertElementAt(AddText,i);
				break;
			}
		}
		if(i==j)List.addElement(AddText);
	}

	public void RemoveFav(int Index){
		Menus[0].removeElementAt(Index);
		MenuChange[0]=true;
	}

	public void RemoveSlap(int Index){
		Menus[1].removeElementAt(Index);
		MenuChange[1]=true;
	}

	public void RemoveIgnore(int Index){
		Menus[2].removeElementAt(Index);
		MenuChange[2]=true;
	}
	
	public boolean isIgnore(String str){
		return Menus[2].indexOf(str)>=0;
	}

	public void AlertInfo(String resCaption,String Message){
		Alert a = new Alert(jmIrc.language.get(resCaption),Message,null,AlertType.INFO);
		a.setTimeout(Alert.FOREVER);
		setDisplay(a);
	}
	
}
