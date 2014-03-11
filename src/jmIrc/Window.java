package jmIrc;
///#define sounds
/* Window.java 15.12.2008 */
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
 *   Это свободная программа; вы можете повторно распространять её и/или
 * 	 модифицировать её в соответствии с Универсальной Общественной
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
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

public class Window extends Canvas implements CommandListener {
	public static final char TYPE_CONSOLE = 0;
	public static final char TYPE_CHANNEL = 1;
	public static final char TYPE_PRIVATE = 2;
	public static final char TYPE_NICKLIST= 3;
	public static final char TYPE_CHANLIST= 4;

	public static final char STATE_NONE=0;
	public static final char STATE_INFO=1;
	public static final char STATE_MSG=2;
	public static final char STATE_HILIGHT=3;
	public static final char STATE_SELECTED=4;
	public static final char STATE_NOTWIN=5;


	private static final int MENU_MAIN=0;
	private static final int MENU_MESSAGE=1;

	private static final int MENU_NICK=4;
	private static final int MENU_FAVOURITES=7;
	private static final int MENU_FAVOURITES_ADD=8;
	private static final int MENU_FAVOURITES_EDIT=9;
	private static final int MENU_TOPIC=10;
	private static final int MENU_ACTIONS=11;
	private static final int MENU_ACTION_ADD=12;
	private static final int MENU_ACTION_EDIT=13;
	private static final int MENU_BANLIST=14;
	private static final int MENU_BANLIST_ADD=15;
	private static final int MENU_BANLIST_EDIT=16;
	private static final int MENU_DEL_BANS=17;
	private static final int MENU_EXCEPTLIST=18;
	private static final int MENU_EXCEPTLIST_ADD=19;
	private static final int MENU_EXCEPTLIST_EDIT=20;
	private static final int MENU_DEL_EXCEPTS=21;
	
	private static final int MENU_CIT=27;
	private static final int SET_TEXT_BOX=32;
	private static final int ADD_MENU=33;
	private static final int ADD_SUB_MENU=34;
	private static final int EDIT_MENU=35;
	private static final int EDIT_SUB_MENU=36;
	
	private static int menu=MENU_MAIN;
	
	public static final String PRIVMSG="PRIVMSG";
	public static final String NOTICE="NOTICE";
	public static final String MODE="MODE";
	public static final String JOIN="JOIN";
	public static final String PART="PART";
	public static final String NICK="NICK";
	public static final String KICK="KICK";
	public static final String QUIT="QUIT";
	public static final String USERHOST="USERHOST";
	public static final String SILENCE="SILENCE";
	public static final String TOPIC="TOPIC";
	public static final String VERSION="VERSION";
	public static final String PING="PING";
	public static final String WHOIS="WHOIS";
	
	public static final String NEXT="Next";
	public static final String PREV="Prev";
	public static final String ADD="Add";
	public static final String DEL="Del";
	public static final String EDIT="Edit";
	public static final String COPY="Copy";	
	public static final String Warning="Warning";
	public static final String Info="Info";
		
	
	

	public String Modes_CB;
	public String Modes_D;

	private Command cmd_Message;
	private Command cmd_Menu;

	public static boolean EnVibro = true;
	public static boolean EnBeep = true;
	public static boolean EnLgtChg = true;
	//#if DEBUGER
//# public static boolean EnDebuger;
	//#endif         
	private Command cmd_favourites;
	
	private Command cmd_ok;
	private Command cmd_cancel;
	private Command cmd_Action;
	private Command cmd_paste;
	private Command cmd_paste_nick;	
	private Command cmd_paste_chan;	
	
	private Command cmd_colors;
	private Command cmd_styles;

	private Command cmd_Add;
	private Command cmd_Edit;
	private Command cmd_Del;
	
	private Command cmd_Info;
	private Command cmd_Mark;
	private Command cmd_Copy;	
//	private Command cmd_AddFav;
//	private Command cmd_EditFav;

//	private Command cmd_AddSlap;
//	private Command cmd_EditSlap;
//	private Command cmd_DelSlap;
	private Command cmd_DelIgnores;
//	private Command cmd_DelFav;

//	private Command cmd_AddBan,cmd_AddExcept;

	private Command cmd_AddMenu;
	private Command cmd_AddSubMenu;
	private Command cmd_EditMenu;
	private Command cmd_DelMenu;
	private Command cmd_UpMenu;
	private Command cmd_DownMenu;
	private Command cmd_CopyMenu;
	
	
	private Command cmd_Join;
	private Command cmd_Topic;
	private Command cmd_Stop;
	private Command cmd_Back;
	
	private char type, menutype;
	public char state;
	private UIHandler uihandler;

	private TextBox textbox;
	private Form WinForm;
	private TextField TF_Modes[];
	private ChoiceGroup CG_Modes_BC[];
	private ChoiceGroup CG_Modes_D;
	
	private ChoiceGroup CG_List;
	private List SlapsList;
	private List FavList;
	private List ChanList;
	
	private Command cmd_CloseList;//Favs, Slaps, Ignores, Copy
	private Command cmd_apply;

	private String chansize;
	public String Name;

	private static long StarPressedTime,PoundPressedTime,FlagsTime=0;
	private static boolean CopyFlag=false,AddCopy=false,StarFlag=false,PoundFlag=false;
	
	private TextArea textarea;

	private List OpDeopList;
	private List URLList;
	private List Menus;
	private List Styles;
	private List StateList;

	private static Vector CommandsMenu;
	private static Vector MenuCMDS;
	private static String Depth;
	
	
	private static int StartCMD=-1,DepthMenu=0;//индекс начала подменю, глубина текущего подменю
	private static String MenuCMD[];
	private static int AliasPos,TextPos,IndexCMD;
	private static String Buffer="";
	
	private static String Entry="";//Внутренняя переменная для сохранения текста для textbox
	private int MaxList;
	private int person_position = 0;
	private Vector Names;
	private List NamesList;
	
	public Vector Bans,Excepts;
	public String Topic;
	
	private static Vector IgnoresList;
	
	private List BansList,ExceptsList;
	private int bans_position=0,excepts_position=0;
	
	public boolean GetBanList=false;
	public boolean GetExceptList=false;

	private boolean NLCMD=false;//NickList Commands
	private boolean LIST=false;//List channels window
	private boolean LISTSTOP=false;
	private int LISTNum;

	public static boolean Stylus=false;
	
	public int hTop,hHeight;//Область заголовка
	public int Left,Top,Width,Height;//обрасть окна
	public int wTop,wHeight;//Область текста
	
	public boolean rejoin=false;
	public static int autoscroll=0;//
	public static long LagTime=0,LastLagTime=0;
	
	private static int[][] SnowPos;
	private static Image[] rgbImage;
	private static int SnowsNum=0;
	private static int SnowsHeight=0;
	
	public Window(UIHandler uihandler, String name, char type){
		super();
	try{	
		MaxList=Utils.parseInt(jmIrc.language.get("MaxList"));
		if(MaxList<5)MaxList=5;
		this.uihandler=uihandler;
		this.Name=name;
		this.type=type;
		Modes_CB=Modes_D="";
		chansize="";
		state=STATE_NONE;
		SetWinSize();		
		textarea=new TextArea(uihandler,Width,wHeight,wTop,hTop,Left,true);
		if(type==Window.TYPE_CONSOLE&&Database.DoubleBuf){
			uihandler.Screen=Image.createImage(Width,Height);
			uihandler.Graph=uihandler.Screen.getGraphics();
		}
		SetFullScreen();
                
		if(type==Window.TYPE_CONSOLE&&Database.ShowSnows)CreateSnowArray();
                
		if(type<=TYPE_PRIVATE){
			if(!Database.FullScreen){
				cmd_Message=new Command(jmIrc.language.get("Message"),(Database.SoftReverse?Command.OK:Command.CANCEL),(Database.SoftReverse?10:20));
				this.addCommand(cmd_Message);
			}
			cmd_Add       =new Command(jmIrc.language.get(ADD) ,Command.SCREEN,20);
			cmd_Edit      =new Command(jmIrc.language.get(EDIT),Command.SCREEN,25);
			cmd_Del       =new Command(jmIrc.language.get(DEL) ,Command.SCREEN,30);

			cmd_Copy      =new Command(jmIrc.language.get(COPY),Command.SCREEN,40);
			cmd_Mark      =new Command(jmIrc.language.get("Mark"),Command.SCREEN,45);
			
			cmd_AddMenu	  =new Command(jmIrc.language.get(ADD),Command.SCREEN,10);	
			cmd_AddSubMenu=new Command(jmIrc.language.get("SubMenu"),Command.SCREEN,20);
			cmd_EditMenu  =new Command(jmIrc.language.get(EDIT),Command.SCREEN,30);
			cmd_DelMenu   =new Command(jmIrc.language.get(DEL),Command.SCREEN,40);
			cmd_UpMenu    =new Command(jmIrc.language.get("Up"),Command.SCREEN,50);
			cmd_DownMenu  =new Command(jmIrc.language.get("Down"),Command.SCREEN,60);
			cmd_CopyMenu  =new Command(jmIrc.language.get(COPY),Command.SCREEN,70);
			
			cmd_Action    =new Command(jmIrc.language.get("Action"),Command.SCREEN,11);
			cmd_colors    =new Command(jmIrc.language.get("Colors"),Command.SCREEN,12);
			cmd_styles    =new Command(jmIrc.language.get("Styles"),Command.SCREEN,13);
			cmd_paste     =new Command(jmIrc.language.get("Paste"),Command.SCREEN,17);
			cmd_paste_nick=new Command(jmIrc.language.get("PasteNick"),Command.SCREEN,19);
			cmd_paste_chan=new Command(jmIrc.language.get("PasteChan"),Command.SCREEN,18);
			
			cmd_DelIgnores=new Command(jmIrc.language.get(DEL),Command.SCREEN, 10);
			cmd_favourites=new Command(jmIrc.language.get("Favourites"),Command.SCREEN,90);
			cmd_ok          =new Command(jmIrc.language.get("Ok"),Command.OK,10);
			cmd_cancel      =new Command(jmIrc.language.get("Cancel"),Command.BACK,20);
		}
		if(type==TYPE_CHANNEL){
			Topic="";
			Names=new Vector();
			Bans=new Vector();
			Excepts=new Vector();

			cmd_apply =new Command(jmIrc.language.get("Apply"),Command.SCREEN,10);
		}
		cmd_CloseList =new Command(jmIrc.language.get("Close"),Command.BACK, 5);

		cmd_Info      =new Command(jmIrc.language.get(Info),Command.SCREEN,35);
		if(!Database.FullScreen){
			cmd_Menu   =new Command(jmIrc.language.get("Menu"),(!Database.SoftReverse?Command.OK:Command.CANCEL),(!Database.SoftReverse?10:20));
			this.addCommand(cmd_Menu);
		}

		if(type==TYPE_CHANLIST){
			this.type=TYPE_PRIVATE;
			LIST=true;
			LISTNum=0;
			cmd_Stop=new Command(jmIrc.language.get("Stop"),Command.BACK,10);
			cmd_Join=new Command(jmIrc.language.get("Join"),Command.SCREEN,20);
			cmd_Topic=new Command(jmIrc.language.get("Topic"),Command.SCREEN,30);
			cmd_Back=new Command(jmIrc.language.get("Back"),Command.BACK,10);
			AddInfo(jmIrc.language.get("GetChanList",""));
			uihandler.CLMenu=new Vector();
			uihandler.CLDepth="";
			if(!Database.FullScreen)this.addCommand(cmd_Stop);
		}
		this.setCommandListener(this); 
	}catch(Exception e){
		uihandler.console.AddInfo(jmIrc.language.get("SystemError","Create Window "+Name));
		close();
   	}

	}
	
    protected void sizeChanged(int w,int h) {
        super.sizeChanged(w,h);
        SetWinSize();
        InitSnowArray();
        textarea.setSize(Width,wHeight,wTop);
		if(type==Window.TYPE_CONSOLE&&Database.DoubleBuf){
			uihandler.Screen=Image.createImage(Width,Height);
			uihandler.Graph=uihandler.Screen.getGraphics();
		}        
    }

    public void SetFullScreen(){
    	if(Utils.MIDP2())setFullScreenMode(Database.FullScreen);
    }
    
    public void SetWinSize(){
		Left=0;
		Top=0;
		Width=getWidth();
		Height=getHeight();
		wHeight=(int)((Height-uihandler.FontHeight)/uihandler.FontHeight)*uihandler.FontHeight;
		hHeight=uihandler.FontHeight;
		hTop=(Database.HeaderUp?0:Height-hHeight);
		wTop=(Database.HeaderUp?hHeight:hTop-wHeight);
    }

	public char getType() {
		return type;
	}

	private void updateHeader() {
		chansize=" [" + (!LIST?Names.size():LISTNum) + "]";
	}

	/* show and close methods */
	public void show() {
		uihandler.setWinlock(false);
		uihandler.setDisplay(this);
	}

	public void ClearNames() {
		Names.removeAllElements();
		Bans.removeAllElements();
		Excepts.removeAllElements();
		System.gc();
	}

	public void close(){
		if(LIST){
			uihandler.CLMenu=null;
			uihandler.CLDepth=null;
		}
		CommandsMenu=null;
		uihandler.setWinlock(false);
		uihandler.deleteWindow(this);
		uihandler.GetActiveWindow().repaint();
	}
	
	public void AddChannel(String Chan,String Info){
		LISTNum++;
		updateHeader();
		String str;
		char c0=Chan.charAt(0);//#c...
		char c1=(Chan.toLowerCase()).charAt(1);
		int i;
		for(i=0;i<uihandler.CLMenu.size();i++){
			if(uihandler.CLDepth.charAt(i)==0){
				str=((String)uihandler.CLMenu.elementAt(i)).toLowerCase();
				if(str.charAt(0)==c0&&str.charAt(1)==c1){
					uihandler.CLMenu.insertElementAt(Chan+":"+Info,++i);
					uihandler.CLDepth=uihandler.CLDepth.substring(0,i)+(char)(1)+uihandler.CLDepth.substring(i);
					return;
				}
				if(str.charAt(0)>c0||(str.charAt(0)==c0&&str.charAt(1)>c1))break;
			}
		}
//		if(i<uihandler.CLMenu.size())i++;
		uihandler.CLMenu.insertElementAt(Chan.toLowerCase().substring(0,2)+"...",i);
		uihandler.CLDepth=uihandler.CLDepth.substring(0,i)+(char)(0)+uihandler.CLDepth.substring(i);
		uihandler.CLMenu.insertElementAt(Chan+":"+Info,++i);
		uihandler.CLDepth=uihandler.CLDepth.substring(0,i)+(char)(1)+uihandler.CLDepth.substring(i);
		repaint();
	}
	
	public void StopChanList(){
		LISTSTOP=true;
		AddInfo(jmIrc.language.get("EndChanList",""+LISTNum));
		show();
		if(LISTNum>0)SetMenu(-1);
	}
	
	public synchronized void handleMsg(String str){
		handleMsg(str,false);	
	}
	
	private String RepNick(String Text,String Nick,String Formate){
		int pos,newpos;
      	String out="";
      	if(Database.utf8detect){
      		try{
      			String s=Utils.decodeUTF8(Utils.stringToByteArray(Nick,Database.Encoding),false);
      			if(s.length()<Nick.length())Nick=TextArea.cbUTF+Nick+TextArea.ceUTF;
      		} catch (UTFDataFormatException udfe) {}
      	}
		Nick=Nick+TextArea.cRem;
    	newpos=Text.indexOf("%nick%",pos=0);
    	while(newpos!=-1){
       		out+=Text.substring(pos,newpos)+Nick;
    		newpos=Text.indexOf("%nick%",pos=newpos+6);
    	}
		return Utils.RepS(out+Text.substring(pos),Formate);
	}

	public synchronized void handleMsg(String str,boolean perform) {
		String tmp="";
		str=Utils.CodeToChars(Utils.Remove(str,new String[] {"\r"}));/*FIXME переводить при отправке*/
		str=str.replace(TextArea.cbURL,TextArea.cCHAR);
		str=str.replace(TextArea.ceURL,TextArea.cCHAR);  
		str=str.replace(TextArea.cbUTF,TextArea.cCHAR);  
		str=str.replace(TextArea.ceUTF,TextArea.cCHAR);  
		str=str.replace(TextArea.cRem,TextArea.cCHAR);
		if(str!=null&&str.length()>0){
			if(str.charAt(0)=='/'&&str.length()>1){
				String[] s=Utils.splitString(str," ");
				String command=s[0].toUpperCase().substring(1);//команда без '/'
               	if(str.charAt(1)=='/'&&!perform){//если "//текст"
               		jmIrc.SendIRC(PRIVMSG+" "+Name+" :"+str.substring(1));
               		AddMessage(RepNick(jmIrc.language.get("TextMessage"),uihandler.nick,str.substring(1)),false);
               	}
               	else if(command.equals("ME")){
               		if(perform||s.length==1)return;
               		tmp=str.substring(s[0].length()+1);
               		jmIrc.SendIRC(PRIVMSG+" "+Name+" :\001ACTION "+tmp+"\001");
               		AddMessage(RepNick(jmIrc.language.get("ACTION"),uihandler.nick,tmp),false);				
               	}
               	else if(command.equals("HOP")&&type==TYPE_CHANNEL){
               		if(perform)return;
               		rejoin=true;
               		if(s.length>1)tmp=str.substring(s[0].length()+1);
               		if(!Names.isEmpty())jmIrc.SendIRC(PART+" "+Name+(s.length>1?" :"+tmp:""));
               		jmIrc.SendIRC(JOIN+" "+Name);
               	}
               	else if(command.equals("CLEAR")){
               		if(perform)return;
               		textarea.clear();
               	}
               	else if(command.equals("EXIT")){
               		if(perform)return;
                   	uihandler.Exit();
                   	return;
               	}
               	else if(command.equals("SCREENUP"))textarea.Home();
               	else if(command.equals("LINEUP"))LineUp(true);
               	else if(command.equals("PAGEUP"))textarea.PageUp();
               	else if(command.equals("PREVWINDOW")){uihandler.displayPreviousWindow();return;}
               	else if(command.equals("NEXTWINDOW")){uihandler.displayNextWindow();return;}
               	else if(command.equals("SCREENDOWN"))textarea.End();
               	else if(command.equals("LINEDOWN"))LineDown(true);
               	else if(command.equals("PAGEDOWN"))textarea.PageDown();               	
              	else if(command.equals("CLOCK")){
               		if(perform)return;
                   	UIHandler.Clock=!UIHandler.Clock;
               	}
              	else if(command.equals("LAG")){
              		if(!Database.Lagometr||!Database.UsePoll||!jmIrc.isConnected())return;
              		str=""+((jmIrc.Lag?System.currentTimeMillis():LastLagTime)-LagTime);
              		if(str.length()>3)str=Utils.parseInt(str.substring(0,str.length()-3))+" "+str.substring(str.length()-3);              			
              		else str="0 "+str;
              		AddInfo(jmIrc.language.get("LAG",str));
              	}
               	else if(command.equals("MESSAGE")||command.equals("ADDRESSED")){
               		if(perform||(command.equals("ADDRESSED")&&s.length==1)||LIST)return;
               		menu=MENU_MESSAGE;
    				AddTextCommand(jmIrc.language.get("WriteMSG",(Entry=Name)),null);
    				if(type!=TYPE_CONSOLE)textbox.addCommand(cmd_Action);
    				if(command.equals("MESSAGE")&&s.length>1)tmp=Utils.nToChar(str.substring(s[0].length()+1));
    				if(command.equals("ADDRESSED")){
    					tmp=Utils.CodeToChars(Utils.Replace(Database.Addressed,"%nick%",s[1]));
    					if(s.length>2)tmp=tmp+str.substring(s[0].length()+1+s[1].length());
    				}
    				if(!tmp.equals(""))textbox.setString(Utils.ColorToCode(tmp));
					uihandler.setWinlock(true);
    				uihandler.setDisplay(textbox);
    				return; 				
               	}
               	else if(command.equals("COPY")){
               		if(perform)return;
               		if(s.length==1)UIHandler.Buffer="";
               		else if(s[1].charAt(0)=='-'&&(s[1].indexOf("b")>0||s[1].indexOf("c")>0||s[1].indexOf("a")>0||s[1].indexOf("n")>0||s[1].indexOf("z")>0||s[1].length()==1)){
           				if(s[1].indexOf("z")>0&&s[1].indexOf("c")>0){
               				menu=MENU_CIT;
               				AddTextCommand(jmIrc.language.get("WriteMSG",(Entry=Name)),null);
               				if(type!=TYPE_CONSOLE)textbox.addCommand(cmd_Action);
               				textbox.setString(jmIrc.language.get("MaskCit",textarea.GetSelectedText(false,false,false)));
               				uihandler.setWinlock(true);
               				uihandler.setDisplay(textbox);
               				return;
              				}
               			tmp=textarea.GetSelectedText(s[1].indexOf("c")>0,false,s[1].indexOf("n")>0);
               			if(s.length>=3)tmp=Utils.RepS(str.substring(s[0].length()+s[1].length()+2),tmp);
               			String st=(s[1].indexOf("b")>0?Buffer:UIHandler.Buffer);
           				if(s[1].indexOf("b")==-1&&!UIHandler.Buffer.equals("")){
           					CopyFlag=true;
               				FlagsTime=System.currentTimeMillis();
               				AddCopy=s[1].indexOf("a")>0;
           				}
           				st=(s[1].indexOf("a")>0?(st.equals("")?"":st+"%n%"):"")+(s[1].indexOf("z")>0?jmIrc.language.get("MaskCit",textarea.GetSelectedText(false,false,false)):tmp);
           				if(s[1].indexOf("b")>0)Buffer=st;
           				else UIHandler.Buffer=st; 
               		}
               	}
            	else if(command.equals("OPDEOP")){
               		if(!(!perform&&type==TYPE_CHANNEL&&menutype==TYPE_NICKLIST))return;
    				OpDeopList=new List(jmIrc.language.get("OP/DEOPList",Entry),List.IMPLICIT);//без статуса
    				OpDeopList.append(jmIrc.language.get(PREV),null);
    				String S;
    				for(int j=0;j<Listener.PREFIXMODES.length();j++){
    					S=jmIrc.language.get("+"+Listener.PREFIXMODES.charAt(j));
    					if(S==null)S="+"+Listener.PREFIXMODES.charAt(j);
    					OpDeopList.append(S,null);
    					S=jmIrc.language.get("-"+Listener.PREFIXMODES.charAt(j));
    					if(S==null)S="-"+Listener.PREFIXMODES.charAt(j);
    					OpDeopList.append(S,null);
    				}
    				OpDeopList.addCommand(cmd_CloseList);
    				OpDeopList.setCommandListener(this);
    				uihandler.setWinlock(true);
    				uihandler.setDisplay(OpDeopList);
    				textbox=null;
    				return;
            	}
            	else if(command.equals("FAVOURITES")){
               		if(perform)return;
               		SetFavsList();
               		textbox=null;
        			return;
            	}
            	else if(command.equals("ACTIONS")){
               		if(!(!perform&&((type==TYPE_CHANNEL&&menutype==TYPE_NICKLIST)||type==TYPE_PRIVATE)))return;
    				SlapsList=new List(jmIrc.language.get("SlapsList",Entry),List.IMPLICIT);//без статуса
    				SetSlapsList();
    				SlapsList.addCommand(cmd_Add);
    				SlapsList.addCommand(cmd_Edit);
    				SlapsList.addCommand(cmd_Del);
    				SlapsList.addCommand(cmd_CloseList);
    				SlapsList.setCommandListener(this);
    				menu=MENU_ACTIONS;
    				uihandler.setWinlock(true);
    				uihandler.setDisplay(SlapsList);
    				textbox=null;
    				return;
            	}
            	else if(command.equals("IGNORESLIST")){
               		if(perform)return;
    				jmIrc.SendIRC(SILENCE);
    				AddInfo(jmIrc.language.get("GetIgnoresList",""));               		
            	}
				else if(command.equals("TRAF")){
					if(perform)return;
					AddInfo("*** Входящий: "+ParseTraf(jmIrc.getBytesIn()));
					AddInfo("*** Исходящий: "+ParseTraf(jmIrc.getBytesOut()));
					AddInfo("*** Всего: "+ParseTraf(jmIrc.getBytesOut()+jmIrc.getBytesIn()));
				}
				else if(command.equals("BACKGROUND")){
					if(!perform)SetBackgroundMode();
					return;
				}
				else if(command.equals("NAMESLIST")){
					if(!(!perform&&type==TYPE_CHANNEL))return;
					menutype=TYPE_NICKLIST;
					listnames(s.length>1?s[1]:"");
					textbox=null;
					return;
				}
				else if(command.equals("OPENURL")){
					if(perform)return;
					if(s.length>1){
						uihandler.OpenURL(str.substring(s[0].length()+1));
						textbox=null;
						show();
						return;
					}
					
					if(Database.FindURLs==0)return;
					URLList=new List(jmIrc.language.get("OpenURL"),List.IMPLICIT);
					Vector URLs=new Vector();
					Utils.GetURLs(textarea.GetSelectedText(false,true,false),URLs);
					for(int i=0;i<URLs.size();i++)URLList.append((String)URLs.elementAt(i),null);
					URLList.addCommand(cmd_CloseList);
					URLList.setCommandListener(this);
					uihandler.setWinlock(true);
					uihandler.setDisplay(URLList);	
					textbox=null;
					return;
				}
				else if(command.equals("SETTOPIC")){
					if(!(!perform&&type==TYPE_CHANNEL))return;
					menu=MENU_TOPIC;
					AddTextCommand(jmIrc.language.get("WriteTopic",Name),Utils.ColorToCode(Topic));
//		Топик # ?			
					uihandler.setWinlock(true);
					uihandler.setDisplay(textbox);
					return;
				}
				else if(command.equals("SETMODES")){
					if(!perform&&type==TYPE_CHANNEL)SetModes();
					textbox=null;
					return;
				}
				else if((command.equals("BAN")||command.equals("KBAN"))){
					if(!(!perform&&type==TYPE_CHANNEL&&s.length>1))return;
					int N=Utils.parseInt(s[1]);
					tmp="";
// kban [n] nick reason					
					String Nick;
					if(command.equals("KBAN"))tmp=str.substring(s[0].length()+s[1].length()+2+(/*(N==0&&s.length>2)||(N>0&&s.length>3)*/s.length>2+(N>0?1:0)?s[2].length()+1:0));
					if(N==0||s.length==1){
						N=Utils.parseInt(jmIrc.language.get("DefBanMask"));//Не указана маска бана
						Nick=s[1];
					}
					else Nick=s[2];
					if(N<1)N=1;
					if(N>5)N=5;
					if(N==1){//по нику
						jmIrc.SendIRC(MODE+" "+Name+" +b "+Nick);
						if(command.equals("KBAN"))jmIrc.SendIRC(KICK+" "+Name+" "+Nick+" :"+tmp);
					}
					else{
						Listener.KickBan=(char)(N+(command.equals("KBAN")?10:0))+Nick+Name+":"+tmp;
						jmIrc.SendIRC(USERHOST+" "+Nick);
					}
				}
				else if((command.equals("IGNORE")||command.equals("TEMPIGNORE"))){
					if(!(!perform&&s.length>1))return;
					int N=Utils.parseInt(s[1]);
					String Nick;
					if(N==0||s.length==1){
						N=Utils.parseInt(jmIrc.language.get("DefBanMask"));//Не указана маска бана
						Nick=s[1];
					}
					else Nick=s[2];					
					if(N<1)N=1;
					if(N>5)N=5;
					if(N==1){//по нику
	    				String mask=Nick+"!*@*";
	    				jmIrc.SendIRC(SILENCE+" +"+mask);
	    				if(command.equals("IGNORE"))uihandler.AddIgnore(mask);
	    				uihandler.console.AddInfo(jmIrc.language.get(command.equals("IGNORE")?"AddIgnore":"TempIgnore",mask));
	    			}
					else{
	    				Listener.Ignore=(char)(N+(command.equals("IGNORE")?0:10))+Nick;
	    				jmIrc.SendIRC(USERHOST+" "+Nick);
					}
				}
				else if(command.equals("BANLIST")){
					if(!(!perform&&type==TYPE_CHANNEL))return;
					if(Bans.isEmpty()){
						GetBanList=true;
						AddInfo(jmIrc.language.get("GetBanList",""));
						jmIrc.SendIRC(MODE+" "+Name+" b");
						show();
					}
					else ListBans();
					textbox=null;
					return;
				}
				else if(command.equals("EXCEPTLIST")){
					if(!(!perform&&type==TYPE_CHANNEL))return;
					if(Excepts.isEmpty()){
						GetExceptList=true;
						AddInfo(jmIrc.language.get("GetExceptList",""));
						jmIrc.SendIRC(MODE+" "+Name+" e");
						show();
					}
					else ListExcepts();
					textbox=null;
					return;
				}
				else if(command.equals("CLOSE")){
					if(perform)return;
					if(type==TYPE_CHANNEL&&!Names.isEmpty()){
						jmIrc.SendIRC(PART+" "+Name+(s.length>1?" :"+str.substring(s[0].length()+1):""));
						
						show();
					}
					else close();
					textbox=null;
					return;
				}
/*  FIXME             	
				else if(command.equals("FINDTEXT")){
					if(perform)return;
					
				}
*/				
				else if(command.equals("PERFORM")){//Ну нельзя перформ запускать из перформа :)
					if(perform)return;
					Perform();
               	}
               	else if(command.equals("SERVER")||command.equals("S")){
					if(perform)return;
					if(jmIrc.isConnected())jmIrc.disconnect((s.length==1?jmIrc.language.get("Reconnected"):null),jmIrc.QuitMessage());
					uihandler.Server=(s.length==1?Database.Server:s[1]);
               		int i;
               		if(s.length>2&&(i=Utils.parseInt(s[2]))>0)uihandler.Port=i;
               		else uihandler.Port=Database.Port;
               		if(s.length>3)uihandler.Pass=s[3];
               		else uihandler.Pass=Database.ServerPass;
               		jmIrc.Wait=false;
               		jmIrc.Reconnect=0;
              		
//     /server [-mp] server [port] [pass] [-j #chan pass] [-i nick anick username realname] 
//     -p редотвращает автовыполнение					
               	}
               	else if(command.equals("MSG")){
               		if(s.length<=2)return;
					Window win=null;
					tmp=str.substring(s[0].length()+1+s[1].length()+1);
					jmIrc.SendIRC(PRIVMSG+" "+s[1]+" :"+tmp);
					if(uihandler.ChanNames.indexOf(s[1].toUpperCase())!=-1)win=uihandler.GetChannel(s[1]);
					else if(uihandler.PrivNames.indexOf(s[1].toUpperCase())!=-1)win=uihandler.GetPrivate(s[1],false);
					if(win==null)write("-> *"+s[1]+"* "+tmp,(char)(0xf7),false,false);
					else win.AddMessage(RepNick(jmIrc.language.get("TextMessage"),uihandler.nick,tmp),false);
				}
               	else if(command.equals(KICK)&&s.length>2){
               		tmp=str.substring(s[0].length()+1+s[1].length()+1+s[2].length()+1);
					jmIrc.SendIRC(KICK+" "+s[1]+" "+s[2]+" :"+tmp);
				}
               	else if(command.equals(TOPIC)&&s.length>1){
               		if(s.length>2)tmp=str.substring(s[0].length()+1+s[1].length()+1);
					jmIrc.SendIRC(TOPIC+" "+s[1]+(s.length>2?" :"+tmp:""));
				}
               	else if(command.equals("DESCRIBE")){
               		if(s.length<=2)return;
					Window win=null;
					tmp=str.substring(s[0].length()+1+s[1].length()+1);
					jmIrc.SendIRC(PRIVMSG+" "+s[1]+" :\001ACTION "+tmp+"\001");
					if(uihandler.ChanNames.indexOf(s[1].toUpperCase())!=-1)win=uihandler.GetChannel(s[1]);
					else if(uihandler.PrivNames.indexOf(s[1].toUpperCase())!=-1)win=uihandler.GetPrivate(s[1],false);
/*FIXME*/			if(win==null)write("-> *"+s[1]+"* "+tmp,(char)(0xf7),false,false);
					else win.AddMessage(RepNick(jmIrc.language.get("ACTION"),uihandler.nick,tmp),false);	
               	}               	
            	else if(command.equals(NOTICE)){
               		if(s.length<=2)return;
            		tmp=str.substring(s[0].length()+1+s[1].length());					
            		jmIrc.SendIRC(NOTICE+" "+s[1]+" :"+tmp);
            		AddMessage(RepNick(jmIrc.language.get("MeNotice"),uihandler.nick,s[1]+" "+tmp),false);
				}
				else if((command.equals("QUERY")||command.equals("Q"))){
					if(s.length==1)return;
					if(!Listener.isChannel(s[1])&&!s[1].trim().equals("")){
						uihandler.setWinlock(false);
						Window win=uihandler.GetPrivate(s[1],true);
						if(s.length>2){
							tmp=str.substring(s[0].length()+1+s[1].length()+1);
							jmIrc.SendIRC(PRIVMSG+" "+s[1]+" :"+tmp);
							win.AddMessage(RepNick(jmIrc.language.get("TextMessage"),uihandler.nick,tmp),false);
						}
						win.show();
						win.repaint();
						textbox=null;
						return;
					}
				}
				else if(command.equals("CTCP")){
               		if(s.length<=2)return;
					if(s.length==3)tmp="";
					else tmp=str.substring(s[0].length()+1+s[1].length()+1+s[2].length()+1);
					if(s[2].toUpperCase().equals(PING)&&s.length==3)jmIrc.SendIRC(PRIVMSG+" "+s[1]+" :\001"+PING+" "+System.currentTimeMillis()+"\001");
					else jmIrc.SendIRC(PRIVMSG+" "+s[1]+" :\001"+s[2].toUpperCase()+(!tmp.equals("")?" "+tmp:"")+"\001");
					AddMessage(RepNick(jmIrc.language.get("MeCTCP"),uihandler.nick,s[1]+" "+s[2].toUpperCase()+(!tmp.equals("")?" "+tmp:"")),false);
				}
				else if(command.equals("RAW")||command.equals("QUOTE")){
               		if(s.length==1)return;
					jmIrc.SendIRC(str.substring(s[0].length()+1));
					write("-> Server: " + str.substring(s[0].length()+1),(char)(0xf7),false,false);
				}
				else if (command.equals("J")){
               		if(s.length==1)return;
               		tmp=str.substring(s[0].length()+1);
               		if(!Listener.isChannel(tmp))tmp="#"+tmp;
					jmIrc.SendIRC(JOIN+" "+tmp);
				}
				else if(command.equals(QUIT)){
					jmIrc.Wait=true;
					jmIrc.disconnect(null,(s.length>1?str.substring(s[0].length()+1):jmIrc.QuitMessage()));
				}
				//Mod
				else if(command.equals("CHGSTATE")){
					//EnVibro=!EnVibro; 
					listState(0);
					textbox=null;
					return;
				}
				else jmIrc.SendIRC(str.substring(1));//Другие команды /команда
			}
			else if(type!=TYPE_CONSOLE){
				jmIrc.SendIRC(PRIVMSG+" "+Name+" :"+str);
				AddMessage(RepNick(jmIrc.language.get("TextMessage"),uihandler.nick,str),false);
			}
			else jmIrc.SendIRC(str);
		}
		textbox=null;
		show();
		repaint();
	}

	public boolean HighLight(String Text){
		if(!Database.HighLight.equals("")){
			String[] HighLight=Utils.splitString(Utils.Replace(Database.HighLight,"%me%",uihandler.nick).toLowerCase(),";");
			String tmp=Utils.Strip(Text.toLowerCase());
			for(int i=0;i<HighLight.length;i++){
				if(tmp.indexOf(HighLight[i])>-1){
					if(state<STATE_HILIGHT)state=STATE_HILIGHT;
					if(Database.VibroHighLight&&Utils.MIDP2())Media.Vibro();
					if(Database.BeepHighLight)
                                    //#ifdef sounds
//#                                     Media.playSoundNotification(Media.SOUND_TYPE_HIGHLIGHT);
                                    //#else
                                    Media.Beep();
                                    //#endif
					return true;
				}
			}
		}
		return false;
	}
	
	public synchronized void AddMessage(String Text,boolean HighLight){
		write(Text,DefColor(Text),HighLight,(state<STATE_MSG)||HighLight);
		if(state<STATE_MSG)state=STATE_MSG;
	}
	
	public synchronized void AddInfo(String str) {
		String[] tmp=Utils.splitString(str,"\n");
		for(int i=0;i<tmp.length;i++)write(tmp[i],DefColor(tmp[i]),false,state<STATE_INFO);
		if(state<STATE_INFO)state=STATE_INFO;
	}
	
	public void write(String str,char color,boolean HightLight,boolean newstate) {
		String[] rets;
		boolean end = textarea.isAtEndpos();
		String time="";
		if (Database.TimeStamp)time=Utils.TimeStamp()+" ";
		str=(HightLight?UIHandler.HighlightStr:"")+time+str;
//		В начало каждой строки вставляем символ, например #10, для определения начала сообщения после разбиения
		str=TextArea.cBegin+(Database.FindURLs==0?str:Utils.FindURL(str));
		
		rets=parseMircColours(color,str);
		textarea.addText(rets);
		if (end) textarea.End();
		if(this.equals(uihandler.GetActiveWindow()))repaint();
		else if(newstate)uihandler.GetActiveWindow().repaint(); //перересовываем индикаторы активного окна, если они изменились
	}
	
	private char DefColor(String Message){
		int text,fon;
		if(Database.Theme==jmIrc.DarkThemes){
			text=0;
			fon=1;
		}
		else{
			text=1;
			fon=0;
		}
		if(Database.UseColor&&Message.length()>3){
			if(Message.charAt(0)=='\003'&&Character.isDigit(Message.charAt(1))&&Character.isDigit(Message.charAt(2))){
				text=Integer.parseInt(Message.substring(1,3))&0x0f;
			}
		}
		return (char)((text&0x0f)|((fon<<4)&0xf0));
	}
	
	public void RemoveMode(char iMode){
		String S1=Modes_CB.substring(0,Modes_CB.indexOf(" "));
		String S2[]=Utils.splitString(Modes_CB.substring(Modes_CB.indexOf(" ")+1)," ");
		int j=S1.indexOf(iMode);
		if(j>=0){
			Modes_CB=Utils.Remove(S1,new String[]{""+iMode});;
			for(int k=0;k<S2.length;k++)if(k!=j)Modes_CB+=" "+S2[k];
		}	
	}
	
	/* Nicklist editing functions start from here */
	public void addNick(char mode, String nick) {
		int i, nsize = Names.size();
		String upnick = nick.toUpperCase();

		for (i=0; i<nsize; i++) {
			String str = (String) Names.elementAt(i);

			if (str.charAt(0) > mode) continue;
			if (upnick.compareTo(str.substring(1).toUpperCase()) < 1 ||
			    str.charAt(0) < mode) {
				Names.insertElementAt(mode + nick, i);
				break;
			}
		}
		if (i == nsize) Names.addElement(mode + nick);
		updateHeader();
	}

	public boolean hasNick(String nick) {
		return (getNickIndex(nick)>=0);
	}

	public void changeNick(String oldnick, String newnick) {
		int idx;

		if ((idx = getNickIndex(oldnick)) >= 0) {
			char mode = ((String) Names.elementAt(idx)).charAt(0);
			deleteNick(oldnick);
			addNick(mode, newnick);
		}
	}

	public void changeMode(char mode, String nick, boolean action) {
		int idx;

		if ((idx = getNickIndex(nick)) >= 0) {
			char oldmode = ((String) Names.elementAt(idx)).charAt(0);
			deleteNick(nick);
			if (action)
				addNick((char) (oldmode | mode), nick);
			else
				addNick((char) (oldmode & ~mode), nick);
		}
	}

	public void deleteNick(String nick) {
		int idx;

		if ((idx = getNickIndex(nick)) >= 0) {
			Names.removeElementAt(idx);
			updateHeader();
		}
	}

	public void printNicks() {
		if(Names.size()>MaxList){
			AddInfo(jmIrc.language.get("NamesLargeChan",""+Names.size()));
			return;
		}
		String temp,str="";
		Enumeration e=Names.elements();
		while(e.hasMoreElements()) {
			char mode=' ';
			temp=((String)e.nextElement());
			
			if(temp.charAt(0)>0){
				int i=Listener.PREFIX.length()-1;
				while(((temp.charAt(0)>>i)&1)!=1&&i>=0)i--;
				if(i>=0)mode=Listener.PREFIX.charAt(Listener.PREFIX.length()-1-i);
			}
			str+=mode+temp.substring(1);
			if(e.hasMoreElements())str+=", ";
		}
		AddInfo(jmIrc.language.get("NamesChan",str));
	}
	
	private int getNickIndex(String nick) {
		int nsize=Names.size();
		for(int i=0;i<nsize;i++){
			String n=(String)Names.elementAt(i);
			if(n.substring(1).equals(nick))return i;
		}
		return -1;
	}

	private void listnames(String ActiveNick) {
		uihandler.setWinlock(true);
		NamesList=new List(jmIrc.language.get("Names"),List.IMPLICIT);
		int n=getNickIndex(ActiveNick);
		if(n>=0)person_position=(int)(n/MaxList);
		int mp=(person_position+1)*MaxList;
		if(person_position>0)NamesList.append(jmIrc.language.get(PREV),null);
		if(Names.size()>mp)NamesList.append(jmIrc.language.get(NEXT),null);					
		n=Names.size();
		String Nick;
		for(int i=(person_position*MaxList); i<mp && i <n; i++) {
			 Nick = (String) Names.elementAt(i);
			char mode=' ';
			
			if(Nick.charAt(0)>0){
				int j=Listener.PREFIX.length()-1;
				while(((Nick.charAt(0)>>j)&1)!=1&&j>=0)j--;
				if(j>=0)mode=Listener.PREFIX.charAt(Listener.PREFIX.length()-1-j);
			}			
			Nick=Nick.substring(1);
			NamesList.append(mode+Nick,null);
			if(ActiveNick.equals(Nick))NamesList.setSelectedIndex(NamesList.size()-1,true);
		}
		NamesList.addCommand(cmd_CloseList);
		NamesList.setCommandListener(this);
		uihandler.setDisplay(NamesList);
	}
	//Mod
        private void listState(int Index) {
                uihandler.setWinlock(true);
                StateList=new List(jmIrc.language.get("StateList"),List.IMPLICIT);
                if (EnVibro) 
                  StateList.append(jmIrc.language.get("OffVibro"),null);
                else
                  StateList.append(jmIrc.language.get("OnVibro"),null);  
                if (EnBeep) 
                  StateList.append(jmIrc.language.get("OffBeep"),null);
                else
                  StateList.append(jmIrc.language.get("OnBeep"),null);  
                if (EnLgtChg) 
                  StateList.append(jmIrc.language.get("OffLgtChg"),null);
                else
                  StateList.append(jmIrc.language.get("OnLgtChg"),null); 
                ////#if SELF
        // нестабильно        StateList.append(jmIrc.language.get("xTheme"+jmIrc.DarkThemes),null);
                ////#endif
                //#if DEBUGER
//#                 if (EnDebuger) 
//#                   StateList.append("Закрыть отладчик",null);
//#                 else
//#                   StateList.append("Открыть отладчик",null); 
                //#endif
                StateList.setSelectedIndex(Index,true);
		StateList.addCommand(cmd_CloseList);
		StateList.setCommandListener(this);
		uihandler.setDisplay(StateList);                
	}
	
	public void AddBan(String Ban,String info) {
		int i,j=Bans.size();
		for (i=0;i<j;i++){
			String mask=(String)Bans.elementAt(i);
			mask=mask.substring(mask.indexOf(" ")+1);
			if(Ban.equals(mask))return;
			if(Ban.compareTo(mask)<0){
				Bans.insertElementAt(Ban+" "+info,i);
				break;
			}
		}
		if(i==Bans.size())Bans.addElement(Ban+" "+info);
	}

	public void AddExcept(String Ban,String info) {
		int i,j=Excepts.size();
		for (i=0;i<j;i++){
			String mask=(String)Excepts.elementAt(i);
			mask=mask.substring(mask.indexOf(" ")+1);
			if(Ban.equals(mask))return;
			if(Ban.compareTo(mask)<0){
				Excepts.insertElementAt(Ban+" "+info,i);
				break;
			}
		}
		if(i==Excepts.size())Excepts.addElement(Ban+" "+info);
	}
	
	
	public void ListBans(){
		uihandler.setWinlock(true);
		BansList=new List(jmIrc.language.get("BansList"),List.IMPLICIT);
		int mp=(bans_position+1)*MaxList;
		if(bans_position>0)BansList.append(jmIrc.language.get(PREV),null);
		if(Bans.size()>mp)BansList.append(jmIrc.language.get(NEXT),null);					
		int n=Bans.size();
		boolean b=false,inf=false;
		for(int i=(bans_position*MaxList);i<mp&&i<n;i++){
			int j=((String)Bans.elementAt(i)).indexOf(' ');
			if(!b&&!inf){
				String s[]=Utils.splitString((String)Bans.elementAt(i)," ");
				if(s.length>=3)inf=true;
				b=true;
			}
			BansList.append(((String)Bans.elementAt(i)).substring(0,j),null);
		}
		menu=MENU_BANLIST;
		BansList.addCommand(cmd_Add);
		BansList.addCommand(cmd_Edit);
		BansList.addCommand(cmd_Del);
		if(inf)BansList.addCommand(cmd_Info);	
		BansList.addCommand(cmd_Copy);
		BansList.addCommand(cmd_Mark);
		BansList.addCommand(cmd_CloseList);
		BansList.setCommandListener(this);
		uihandler.setDisplay(BansList);
	}
	
	public void ListExcepts(){
		uihandler.setWinlock(true);
		ExceptsList=new List(jmIrc.language.get("ExceptsList"),List.IMPLICIT);
		int mp=(excepts_position+1)*MaxList;
		if(excepts_position>0)ExceptsList.append(jmIrc.language.get(PREV),null);
		if(Excepts.size()>mp)ExceptsList.append(jmIrc.language.get(NEXT),null);					
		int n=Excepts.size();
		boolean b=false,inf=false;
		for(int i=(excepts_position*MaxList);i<mp&&i<n;i++){
			int j=((String)Excepts.elementAt(i)).indexOf(' ');
			if(!b&&!inf){
				String s[]=Utils.splitString((String)Excepts.elementAt(i)," ");
				if(s.length>=3)inf=true;
				b=true;
			}
			ExceptsList.append(((String)Excepts.elementAt(i)).substring(0,j),null);
		}
		menu=MENU_EXCEPTLIST;
		ExceptsList.addCommand(cmd_Add);
		ExceptsList.addCommand(cmd_Edit);
		ExceptsList.addCommand(cmd_Del);
		if(inf)ExceptsList.addCommand(cmd_Info);
		ExceptsList.addCommand(cmd_Copy);		
		ExceptsList.addCommand(cmd_Mark);
		ExceptsList.addCommand(cmd_CloseList);
		ExceptsList.setCommandListener(this);
		uihandler.setDisplay(ExceptsList);
	}

	private void PaintLagometr(Graphics g){
		if(!Database.Lagometr||!jmIrc.isConnected())return;
		int lag=(int)((jmIrc.Lag?System.currentTimeMillis():LastLagTime)-LagTime)/1000;
		g.setColor(0,0,255);
		int w4=Width/4;
		int top=Database.HeaderUp?hTop+1:hTop+hHeight-2;
		
		if(lag<10)g.fillRect(Left,top,(int)(w4*lag/10),1);
		else if(lag<30){
			g.fillRect(Left,top,w4,1);
			g.fillRect(w4,top,(int)(w4*lag/30),1);
		}
		else if(lag<60){
			g.fillRect(Left,top,w4*2,1);
			g.fillRect(w4*2,top,(int)(w4*lag/60),1);
		}
		else {
			g.fillRect(Left,top,w4*3,1);
			g.setColor(255,0,0);
			g.fillRect(w4*3,top,(int)(w4*lag/200),1);
		}
	}
	
	private void PaintHeader(Graphics g){
		int i;
		g.setColor(140,170,255);//Фон заголовка
		g.fillRect(Left,hTop,Width,hHeight);
	
//mem indicator
		long total=Runtime.getRuntime().totalMemory();
		long used=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		if(used*100/total>=90)g.setColor(255,0,0);
		else g.setColor(0,255,0);
		g.fillRect(Left,Database.HeaderUp?hTop:hTop+hHeight-1,(int)(Width*used/total),1);
		PaintLagometr(g);
		
		char[] wins = uihandler.GetIndicators();
		int ih=6;
		if (Database.FontSize!=4) ih=uihandler.FontHeight/3+2;
		
		int k,ik=0;
		int j=Width/uihandler.NumsWind;
		for (i=0; i<wins.length; i++) {
			switch (wins[i]) {
			case STATE_NONE:
				g.setColor(255,255,255);
				break;
			case STATE_INFO:
				g.setColor(0,255,0);						
				break;
			case STATE_MSG:
				g.setColor(0,0,255); //0,0,125
				break;
			case STATE_HILIGHT:
				g.setColor(255,0,0);
				break;
			case STATE_SELECTED:
				g.setColor(0,0,0);
				break;
			case STATE_NOTWIN:
				ik++;
				continue;
			}
//			g.fillRect(3+(i*ih),hTop+3,ih-2,ih-2);
			if(Stylus){
				if(j>uihandler.FontHeight-6)k=uihandler.FontHeight-6;
				else k=j;
				g.fillRoundRect((i-ik)*j+j/2-k/2,hTop+3,k,k,k,k);
			}
			else g.fillRoundRect(3+(i*ih),hTop+3,ih-2,ih-2,ih-2,ih-2);
		}
		if(Stylus)return;
		if (Database.FontSize!=4)g.setFont(uihandler.font);
		g.setColor(0x000000);
		//-------------------------
		int tmpwidth;
		if (Database.FontSize==4) {
			tmpwidth = jmIrc.bmBFont.getStringWidth(Name + chansize)+1;
		} else {
			tmpwidth = uihandler.font.stringWidth(Name+chansize);
		}
		//-------------------------
		if (tmpwidth<Width-ih-i*ih){
				if (Database.FontSize==4) {
					jmIrc.bmBFont.drawString(g,Width-2-jmIrc.bmBFont.getStringWidth(Name+chansize),hTop,Name+chansize,1);
				} else {
			 		g.drawString(Name + chansize,Width-2,hTop, Graphics.RIGHT/*LEFT*/ | Graphics.TOP);
				}
		} else {
			int textwidth=Width-ih-i*ih;
			if (Database.FontSize==4) {
				textwidth -= jmIrc.bmBFont.getStringWidth(chansize+".." + Name.substring(Name.length()-2));
				for (i=Name.length()-3; i>=0 && jmIrc.bmBFont.getStringWidth(Name.substring(0, i))>textwidth; i--);
			} else {
				textwidth -= uihandler.font.stringWidth(chansize+".." + Name.substring(Name.length()-2));
				for (i=Name.length()-3; i>=0 && uihandler.font.substringWidth(Name, 0, i)>textwidth; i--);
			}
				if (Database.FontSize==4) {
					jmIrc.bmBFont.drawString(g, Width-2-jmIrc.bmBFont.getStringWidth(Name.substring(0, i) + ".." + Name.substring(Name.length()-2) + chansize), hTop, Name.substring(0, i) + ".." + Name.substring(Name.length()-2) + chansize, 1);
				} else {
                			g.drawString(Name.substring(0, i) + ".." + Name.substring(Name.length()-2) + chansize,Width-2,hTop, Graphics.RIGHT | Graphics.TOP);
				}
                }
	}
	
	private void CreateSnowArray(){
		int[] Snows;
		Snows=new int[182];
		rgbImage=new Image[6];
		DataInputStream in = null;
		try{
			InputStream inputStream=this.getClass().getResourceAsStream("/snow.dat");
			in=new DataInputStream(inputStream);
			int i,j;
			for(j=0;j<6;j++){ 
				for(i=0;i<182;i++){
					Snows[i]=(in.read()&0xff)|((in.read()&0xff)<<8)|((in.read()&0xff)<<16)|((in.read()&0xff)<<24);
					if(Database.Theme==jmIrc.DarkThemes)Snows[i]^=0x00ffffff;
				}
				rgbImage[j]=Image.createRGBImage(Snows,13,14,true);
			}
		}catch (Exception io){
		}
		finally{
			if (in != null)try {in.close();} catch (IOException ex){}
		}
		InitSnowArray();
	}
	
	private void InitSnowArray(){
		if(SnowsHeight==Height&&SnowsNum==Database.SnowsNum)return;
		SnowsNum=Database.SnowsNum;
		if(SnowsNum==0)SnowsNum=(SnowsHeight=Height)/7+2;
		if(SnowsNum>Height)SnowsNum=Height+14;
		SnowPos=new int[3][SnowsNum];
		CreateSnowPos(true);
	}
	
	public void CreateSnowPos(boolean start){
		int i=0,y=Height;
		for(;i<SnowsNum;i++){
			if(!Database.AnimateSnows||start){
				SnowPos[0][i]=Utils.Random(Width+8)-15;
				SnowPos[1][i]=(y+14)*i/SnowsNum-14;
				SnowPos[2][i]=Utils.Random(6);
			}
			else {
				SnowPos[1][i]++;
				if(SnowPos[1][i]>y){
					SnowPos[0][i]=Utils.Random(Width+8)-15;
					SnowPos[1][i]=-14;
					SnowPos[2][i]=Utils.Random(6);
				}
				else SnowPos[0][i]+=Utils.Random(3)-1; 
			}
		}
	}
	
	private void PaintSnow(Graphics g){
		for(int x=0;x<SnowsNum;x++)g.drawImage(rgbImage[SnowPos[2][x]],SnowPos[0][x],SnowPos[1][x],Graphics.LEFT | Graphics.TOP);
	}

	
	private int PaintCopy(Graphics g,int x,int y,boolean plus){
		if(plus){
			g.drawLine(x,y+2,x+2,y+2);
			g.drawLine(x+1,y+1,x+1,y+3);			
			x+=4;
		}
		g.drawLine(x,y,x+6,y);
		g.drawLine(x,y+5,x+6,y+5);
		g.drawLine(x,y,x,y+5);
		g.drawLine(x+6,y,x+6,y+5);
		g.drawLine(x,y,x+6,y+5);
		g.drawLine(x+6,y,x,y+5);
		x+=8;
		return x;
	}
	
	private int PaintStar(Graphics g,int x,int y){
		g.drawLine(x+1,y,x+1,y);
		g.drawLine(x+3,y,x+3,y);
		g.drawLine(x,y+2,x+4,y+2);		//**+**
		g.drawLine(x+2,y+1,x+2,y+3);	//**+**
		g.drawLine(x+1,y+4,x+1,y+4);	//+++++
		g.drawLine(x+3,y+4,x+3,y+4);	//**+**
		x+=6;							//*+*+*
		return x;						
	}

	private int PaintPound(Graphics g,int x,int y){
		g.drawLine(x,y+1,x+5,y+1);	//*+**+*
		g.drawLine(x,y+3,x+5,y+3);	//++++++
		g.drawLine(x+1,y,x+1,y+4);	//*+**+*
		g.drawLine(x+4,y,x+4,y+4);  //++++++
		x+=7;						//*+**+*
		return x;					
	}
	
	
	private void PaintFlags(Graphics g){
		int x=3,y=hTop+uihandler.FontHeight/2+1;
		g.setColor(0, 0, 0);
		if(uihandler.KeyLock){
			g.drawLine(x,y+1,x,y+3);
			g.drawLine(x+2,y+1,x+2,y+3);
			g.drawLine(x+4,y+1,x+4,y+2);
			g.drawLine(x+6,y+1,x+6,y+2);
			g.drawLine(x,y+1,x+3,y+1);
			g.drawLine(x+5,y,x+5,y);
			g.drawLine(x+5,y+3,x+5,y+3);
			x+=8;
		}
		if(!UIHandler.Buffer.equals(""))x=PaintCopy(g,x,y,false);
		
		if(Database.AdvComb){
			if(StarFlag)x=PaintStar(g,x,y);
			if(PoundFlag)x=PaintPound(g,x,y);
		}
		if(System.currentTimeMillis()-FlagsTime<5000){
			if(CopyFlag)x=PaintCopy(g,x,y,AddCopy);
		}
		else {
			CopyFlag=false;
			AddCopy=false;
		}
	
		if(UIHandler.Clock){
			g.drawLine(x+3,y+3,x+6,y+3);
			g.drawLine(x+3,y+3,x+3,y);			
			g.drawRoundRect(x,y,6,6,6,6);
			PaintClock(g);
		}
	}
	
	private void PaintClock(Graphics g){
		int L,T,W,H;
		Calendar cal = Calendar.getInstance();
		String time;
		char c=(((int)(cal.get(Calendar.SECOND)/2))*2==cal.get(Calendar.SECOND)?':':' ');
		time=(cal.get(Calendar.HOUR_OF_DAY)<10?"0":"")+cal.get(Calendar.HOUR_OF_DAY)+
		c+
		(cal.get(Calendar.MINUTE)<10?"0":"")+cal.get(Calendar.MINUTE)+
		c+
		(cal.get(Calendar.SECOND)<10?"0":"")+cal.get(Calendar.SECOND);
		if (Database.FontSize!=4)
		  W=uihandler.font.charWidth('D');
		else
			W=jmIrc.bmFont.getStringWidth("D");
		L=Width/2-W*4;
		H=uihandler.FontHeight;
		T=wHeight/2-H/2;
		g.setFont(uihandler.font);
		g.setColor(Database.Theme==jmIrc.DarkThemes?0x000000:0xffffff);//fon
		g.fillRect(L-1,T-1,W*8+2,H+2);
		g.setColor(255,0,0);
		g.drawRect(L-1,T-1,W*8+2,H+2);
		g.setColor(Utils.getColor(Database.Theme==jmIrc.DarkThemes?0:1));//text
		for(int i=0;i<8;i++)
		if (Database.FontSize!=4)
		  g.drawChar(time.charAt(i),1+L+W*i,1+T,Graphics.LEFT|Graphics.TOP);
		else
		  jmIrc.bmFont.drawString(g, 1+L+W*i, 1+T, time.charAt(i)+"", Database.Theme==jmIrc.DarkThemes?0:1);
	}

	private void PaintScreen(Graphics g){
		g.setColor(Database.Theme==jmIrc.DarkThemes?0x000000:0xffffff);//fon
		g.fillRect(Left,Top,Width,Height);
		PaintHeader(g);
		textarea.draw(g);
		PaintFlags(g);
		if(Database.ShowSnows)PaintSnow(g);
	}
	
	
	public void paint(Graphics g){
		PaintScreen(Database.DoubleBuf?uihandler.Graph:g);
		if(Database.DoubleBuf)g.drawImage(uihandler.Screen,0,0,Graphics.LEFT|Graphics.TOP);

/*FIXME*/
		if(!uihandler.Changes&&(uihandler.MenuChange[0]||uihandler.MenuChange[1]||uihandler.MenuChange[3]||uihandler.MenuChange[4]||uihandler.MenuChange[5]||uihandler.MenuChange[6])){
			uihandler.Changes=true;
			uihandler.AlertInfo(Info,jmIrc.language.get("WarnSaveMenu"));
		}
	}

	public void LineUp(boolean flag){
		if(flag)autoscroll=1;
		if(textarea.LineUp())repaint();
	}

	public void LineDown(boolean flag){
		if(flag)autoscroll=-1;
		if(textarea.LineDown())repaint();
	}
	
	/* triggers and callbacks start from here */
	protected void keyPressed(int keyCode) {
		if(keyCode==KEY_NUM5&&(System.currentTimeMillis()-StarPressedTime<2000&&!Database.AdvComb||(Database.AdvComb&&StarFlag))){
			String s;
			uihandler.setWinlock(uihandler.KeyLock=!uihandler.KeyLock);
			if(uihandler.KeyLock){
				s="KeyLocked";
				if(Database.AdvComb){StarFlag=false;repaint();}
				Media.BackLight(0);
			}
			else{
				s="KeyUnlocked";
				Media.BackLight(1);
			}
			if (!(EnLgtChg)) {
			Alert a = new Alert(jmIrc.language.get("KeyLock"),jmIrc.language.get(s), null, AlertType.INFO);
			a.setTimeout(1000);
			uihandler.setDisplay(a);
			}
			StarPressedTime=0;
			repaint();
			return;
		}

		if(uihandler.KeyLock){
			if(keyCode==KEY_STAR){
				if(Database.AdvComb){StarFlag=!StarFlag;repaint();}
				else {
					StarPressedTime=System.currentTimeMillis();
					PoundPressedTime=0;
				}
			}
			return;
		}
		
		if(Database.AdvComb&&StarFlag&&PoundFlag&&keyCode>=KEY_NUM0&&keyCode<=KEY_NUM9){
			if(!Database.Combinations[keyCode+30-KEY_NUM0].equals("")&&Database.Combinations[keyCode+30-KEY_NUM0].charAt(0)!=';'){
				ParseCMD(Database.Combinations[keyCode+30-KEY_NUM0]);
				StarFlag=PoundFlag=false;
				return;
			}
		}
		if(keyCode>=KEY_NUM0&&keyCode<=KEY_NUM9&&(System.currentTimeMillis()-StarPressedTime<2000&&!Database.AdvComb||(Database.AdvComb&&StarFlag))){
			if(!Database.Combinations[keyCode+10-KEY_NUM0].equals("")&&Database.Combinations[keyCode+10-KEY_NUM0].charAt(0)!=';'){
				ParseCMD(Database.Combinations[keyCode+10-KEY_NUM0]);
				StarPressedTime=0;
				StarFlag=false;
				return;
			}	
		}
		if(keyCode>=KEY_NUM0&&keyCode<=KEY_NUM9&&(System.currentTimeMillis()-PoundPressedTime<2000&&!Database.AdvComb||(Database.AdvComb&&PoundFlag))){
			if(!Database.Combinations[keyCode+20-KEY_NUM0].equals("")&&Database.Combinations[keyCode+20-KEY_NUM0].charAt(0)!=';'){
				ParseCMD(Database.Combinations[keyCode+20-KEY_NUM0]);
				PoundPressedTime=0;
				PoundFlag=false;
				return;
			}	
		}	
		if(keyCode>=KEY_NUM0&&keyCode<=KEY_NUM9){
			if(!Database.Combinations[keyCode-KEY_NUM0].equals("")&&Database.Combinations[keyCode-KEY_NUM0].charAt(0)!=';'){
				ParseCMD(Database.Combinations[keyCode-KEY_NUM0]);
				return;
			}	
		}	
		
		if(keyCode==KEY_POUND){
			if(Database.AdvComb){PoundFlag=!PoundFlag;repaint();}
			else StarPressedTime=0;
			PoundPressedTime=System.currentTimeMillis();
			return;
		}		
		
		if(keyCode==KEY_STAR){
			if(Database.AdvComb){StarFlag=!StarFlag;repaint();}
			else PoundPressedTime=0;
			StarPressedTime=System.currentTimeMillis();
			return;
		}
		else if ((keyCode >= 97 && keyCode <= 122) || (keyCode >= 65 && keyCode <= 90)) { // BlackBerry
			handleMsg("/MESSAGE");
			textbox.insert("" + (char) keyCode, 0);
			uihandler.setDisplay(textbox);
		}
		else if (keyCode == 137) { // another BlackBerry
			handleMsg("/MESSAGE");
		}
		else if(getGameAction(keyCode)==UP)LineUp(true);
		else if(getGameAction(keyCode)==DOWN)LineDown(true);
		else if(getGameAction(keyCode)==LEFT)uihandler.displayPreviousWindow();
		else if(getGameAction(keyCode)==RIGHT)uihandler.displayNextWindow();
		
//L		-6	-202	-21	21	-1	
//R		-7	-203	-22	22	-4		
		else if(keyCode==-202||keyCode==-21||keyCode==-11||keyCode==-6|
                        keyCode==-1||keyCode==21||keyCode==105||keyCode==57345/*pda*/){
			if(!Database.SoftReverse)handleMsg("/MESSAGE");
			else SetMenu(-1);
		}
		
		else if((keyCode==-203||keyCode==-22||keyCode==-7||
                            keyCode==-4|| /*только Siemens S-Gold*/
                            keyCode==22||keyCode==106||keyCode==57346/*pda*/)){
			if(!Database.SoftReverse)SetMenu(-1);
			else handleMsg("/MESSAGE");

		}
		else if(keyCode==-23/*Motorola*/)SetMenu(-1);
		else if(keyCode==KEY_NUM5||keyCode==-5||getGameAction(keyCode)==FIRE)handleMsg("/MESSAGE");
		
	}
	
	protected void keyRepeated(int keyCode){
		keyPressed(keyCode);
	}
	
	private boolean InSqare(int x,int y,int x1,int x2,int y1,int y2){
		return (x>=x1&&x<=x2&&y>=y1&&y<=y2);
	}
	
	protected void pointerPressed(int x, int y){
		if(uihandler.KeyLock)return;
		int Line =uihandler.FontHeight;
		if(Line<Width/15)Line=Width/15;

		int n=Width/uihandler.NumsWind;
		if(Stylus)for (int i=0;i<uihandler.NumsWind;i++){
//			if(x>=3+(k*5)&&x<=3+(k*(uihandler.FontHeight/3+2))+3&&y>=hTop+1&&y<=hTop+uihandler.FontHeight/3){
			if(InSqare(x,y,i*n,(i+1)*n,hTop,hTop+uihandler.FontHeight)){
				uihandler.displayWindow(i);
				return;
			}
		}
		if(InSqare(x,y,Width-Line*2,Width-Line,wTop+wHeight-Line,wTop+wHeight))SetMenu(-1);//handleMsg("/NAMESLIST");
		else if(InSqare(x,y,0,Line,wTop+Line*2,wTop+wHeight-Line*2))uihandler.displayPreviousWindow();
		else if(InSqare(x,y,Width-Line*2,Width-Line,wTop+Line*2,wTop+wHeight-Line*2))uihandler.displayNextWindow();
		else if(InSqare(x,y,Width-Line,Width,wTop,wTop+wHeight))textarea.SetPos(y-wTop);
		else if(InSqare(x,y,Line*2,Width-Line*3,wTop,wTop+Line))textarea.LineUp();
		else if(InSqare(x,y,Line*2,Width-Line*3,wTop+wHeight-Line,wTop+wHeight))textarea.LineDown();
		else if(InSqare(x,y,0,Line*2,wTop+wHeight-Line*2,wTop+wHeight))textarea.End();	
		else if(InSqare(x,y,0,Line*2,wTop,wTop+Line*2))textarea.Home();	
		else if(InSqare(x,y,Width-Line*3,Width-Line,wTop,wTop+Line*2))textarea.PageUp();
		else if(InSqare(x,y,Width-Line*3,Width-Line,wTop+wHeight-Line*2,wTop+wHeight))textarea.PageDown();
		else Stylus=!Stylus;
		repaint();
	}			
	
	private void TextBoxInsert(String str){
		if(textbox.getCaretPosition()>0)textbox.insert(str,textbox.getCaretPosition());
		else textbox.setString(textbox.getString()+str);
	}
	
//++++++++++++++++++++++++++++++++++++++++++
//Окно с выбором цвета текста
	private class Colors extends Canvas{
		private int width;
		private int height;
		private int Active=0,Old=0;//0-15 активный кубик цвета; 16 - активна кнопка закрыть 
		private boolean TextFon=true;//true - Text, false - Fon
		private int TextColor;
		
		private int r=1;//ширина рамки

		private int hw;//размер грани квадрата
		private int ww;//ширина окна
		private int wh;//высота окна
		private int Left=(width-ww)/2;//левая позиция окна
		private int Top=(height-wh)/2;//верхняя позиция окна
		
		private int cl;//левая позиция кнопки
		private int ct;//верхняя позиция кнопки
		private int ch;//размер кнопки
		
		public Colors(int width, int height) {
			this.width = width;
			this.height = height;
			TextColor=(Database.Theme==jmIrc.DarkThemes?0:1);
			if (Database.FontSize==4) {
				if(jmIrc.bmFont.getStringWidth("00")>jmIrc.bmFont.getHeight())hw=jmIrc.bmFont.getStringWidth("00");//ширина 2-x символов
			  	else hw=jmIrc.bmFont.getHeight();//высота шрифта
			} else {
				Font f=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Database.FontSize);
				if(f.stringWidth("00")>f.getHeight())hw=f.stringWidth("00");//ширина 2-x символов
				else hw=f.getHeight();//высота шрифта
			}
			this.height-=hw;
//ширина окна
//			ww=r*2/*внешняя рамка*/+r*8/*рамки 4-х квадратов 2*4*/+hw*4/*ширина 2-х букв 4квадрата*/;
			if(width>height)ww=wh=this.height;
			else ww=wh=width;
//высота окна			
//			wh=r*8/*внешняя рамка и заголовок 2*2*/+hw/*заголовок*/+r*8/*рамки 4-х квадратов 2*4*/+hw*4/*высота буквы 4квадрата*/;
			Left=(width-ww)/2;
			Top=(this.height-wh)/2;
			
			cl=Left+ww-r/*рамка окна*/-(hw+r);//левая позиция кнопки
			ct=Top+r/*рамка окна*/+2*r+r;//верхняя позиция кнопки
			ch=hw-2;//размер кнопки
			if(Database.FullScreen&&Utils.MIDP2())setFullScreenMode(true);
		}
	
		private void Frame(Graphics g,int L,int T,int R,int B){
			g.setColor(212,208,200);//верхняя
            g.drawLine(L,T,R,T);
			g.setColor(128,128,128);//правая
            g.drawLine(R,T,R,B);
			g.setColor(212,208,200);//левая
            g.drawLine(L,T,L,B);
			g.setColor(128,128,128);//нижняя
            g.drawLine(L,B,R,B);
		}
		
		public void paint(Graphics g){
			if (Database.FontSize!=4)g.setFont(uihandler.font);
//рисуем рамку окна
			Frame(g,Left,Top,Left+ww,Top+wh);
//Рисуем заголовок окна
			g.setColor(11,37,107);//цвет заголовка
			g.fillRect(Left+r,Top+r,ww-2*r,hw+4*r/*размер заголовка*/);
			g.setColor(255,255,255);//белый
			if (Database.FontSize==4) {
				jmIrc.bmFont.drawString(g,Left+r+2*r/*отступ*/,Top+r+2*r,(TextFon?"Text":"Fon"),0);
			} else {
				g.drawString((TextFon?"Text":"Fon"),Left+r+2*r/*отступ*/,Top+r+2*r,Graphics.LEFT|Graphics.TOP);
			}
//рисуем кнопку закрытия (крестик)
			g.setColor(212,208,200);//цвет кнопки
			g.fillRect(cl,ct,ch,ch);
//рисуем крестик
			g.setColor(Active==16?255:0,0,0);
			g.drawLine(cl+2,ct+2,cl+ch-2,ct+ch-2);
            g.drawLine(cl+ch-2,ct+2,cl+2,ct+ch-2);
//рисуем квадраты с цветами
            int h=(ww-2*r)/4;
            for(int i=0;i<4;i++){
            	for(int j=0;j<4;j++){
            		int cubeL=Left+2*r+h*i;
            		int cubeT=ct+ch+3*r+h*j;
            		g.setColor(Utils.getColor(i+j*4));
            		g.fillRect(cubeL,cubeT,h,h);            		
            		if(i+j*4==Active){
            			g.setColor(Utils.getColor(Active==TextColor?(Active==1?0:1):TextColor));
            			g.fillRoundRect(cubeL+h/4,cubeT+h/4,h/2,h/2,h/2,h/2);
            		}
            	}	
            }
		}
		
		private void SetColor(){
			if(Active==16){
        		Active=Old=0;
        		TextFon=true;
        		uihandler.setDisplay(textbox);
        		return;
        	}
    		TextBoxInsert((TextFon?"%c%":",")+(Active<10?"0":"")+Active);
        	if((TextFon=!TextFon))uihandler.setDisplay(textbox);
        	else{
        		TextColor=Active;
        		Active=Old=16;
        		repaint();
        	}
		}
		
	    protected void keyPressed(int keyCode){
	    	Old=Active;
	    	if(keyCode==KEY_NUM2||getGameAction(keyCode)==UP)Active-=4;
			else if(keyCode==KEY_NUM8||getGameAction(keyCode)==DOWN)Active+=4;
			else if(keyCode==KEY_NUM4||getGameAction(keyCode)==LEFT)Active--;
			else if(keyCode==KEY_NUM6||getGameAction(keyCode)==RIGHT)Active++;
			else if(keyCode==KEY_NUM5||getGameAction(keyCode)==FIRE)SetColor();
	    	if(Active<0)Active=16;
	    	if(Active>16)Active=0;
    		repaint();
	    }
	    
	    protected void pointerPressed(int x, int y){
	    	int ret=-1;
            int h=(ww-2*r)/4;
	        if(x>=cl&&x<=cl+ch&&y>=ct&&y<=ct+ch)ret=16;//кнопка "закрыть"
	        else
            for(int i=0;i<4;i++){
            	for(int j=0;j<4;j++){
            		int cubeL=Left+2*r+h*i;
            		int cubeT=ct+ch+3*r+h*j;
            		if(x>=cubeL&&x<=cubeL+h&&y>=cubeT&&y<=cubeT+h){
            			ret=i+j*4;
            			i=j=4;
            		}
            	}	
            }
            if(ret>=0){
            	Old=Active;
            	Active=ret;
            	if(Old==Active)SetColor();
            	else repaint();
            }
	    }
	}
	
//------------------------------------------	
	
	
	
	
	protected void keyReleased(int keyCode) {
		if (keyCode == KEY_NUM2 || getGameAction(keyCode) == UP||keyCode == KEY_NUM8 || getGameAction(keyCode) == DOWN) autoscroll=0;
	}

	private void AddTextCommand(String Title,String Text){
		textbox = new TextBox(Title, Text, 2000,TextField.ANY);
		textbox.addCommand(cmd_ok);
		textbox.addCommand(cmd_colors);
		textbox.addCommand(cmd_styles);
		if(menu!=MENU_FAVOURITES_ADD&&menu!=MENU_FAVOURITES_EDIT)textbox.addCommand(cmd_favourites);
		if(!UIHandler.Buffer.equals(""))textbox.addCommand(cmd_paste);
		textbox.addCommand(cmd_paste_nick);
		textbox.addCommand(cmd_paste_chan);
		textbox.addCommand(cmd_cancel);
		textbox.setCommandListener(this);
		uihandler.setWinlock(true);
	}
	
	public void nickChangeAction(){
		menu=MENU_NICK;
		textbox=new TextBox(jmIrc.language.get("AltNick"),"",32,TextField.ANY);
		textbox.addCommand(cmd_ok);
		textbox.addCommand(cmd_cancel);
		textbox.setString(uihandler.nick);
		textbox.setCommandListener(this);
		uihandler.setWinlock(true);
		uihandler.setDisplay(textbox);
	}
	
	public void CheckEntry(String str1,String str2){
		if(menu==MENU_MESSAGE&&Entry.equals(str1)&&textbox!=null){
			textbox.setTitle(jmIrc.language.get((menu==MENU_MESSAGE?"WriteMSG":"WriteNotice"),str2));
		}
//		if(NamesList!=null)listnames();
//		if(NamesCMDList!=null&&Entry.equals(str1))NamesCMDList.setTitle(str2);
		if(Entry.equals(str1))Entry=str2;
//FIXME :\		
	}
	
	private void SetMenu(int Index){
		if(uihandler.KeyLock)return;
		uihandler.setWinlock(true);
		Menus=new List(jmIrc.language.get("Menu"),List.IMPLICIT);
		if(LIST&&LISTSTOP){
			if(DepthMenu==1){
				Menus.addCommand(cmd_Join);
				Menus.addCommand(cmd_Topic);
				Menus.addCommand(cmd_Info);
			}
			else {
				Menus.removeCommand(cmd_Join);
				Menus.removeCommand(cmd_Topic);
				Menus.removeCommand(cmd_Info);
			}
			
			Menus.addCommand(cmd_Back);
			this.removeCommand(cmd_Stop);
		}
		if(LIST&&!LISTSTOP)Menus.addCommand(cmd_Stop);
		else if(!LIST){
		Menus.addCommand(cmd_AddMenu);
		Menus.addCommand(cmd_AddSubMenu);
		Menus.addCommand(cmd_EditMenu);
		Menus.addCommand(cmd_DelMenu);
		Menus.addCommand(cmd_UpMenu);
		Menus.addCommand(cmd_DownMenu);
		Menus.addCommand(cmd_CopyMenu);
		}
		
		Menus.setCommandListener(this);
		
		if(LIST&&!LISTSTOP){
			uihandler.setDisplay(Menus);
			return;
		}
		
		Menus.addCommand(cmd_CloseList);		
		
		String S;
		int i,j;
		if(LIST){
			CommandsMenu=uihandler.CLMenu;
			Depth=uihandler.CLDepth;
		}
		else if(type==TYPE_CONSOLE){
			CommandsMenu=uihandler.Menus[3];
			Depth=uihandler.Depth[3];
		}
		else if(type==TYPE_CHANNEL&&!NLCMD){
			CommandsMenu=uihandler.Menus[4];
			Depth=uihandler.Depth[4];
		}
		else if(type==TYPE_CHANNEL&&NLCMD){
			CommandsMenu=uihandler.Menus[5];
			Depth=uihandler.Depth[5];
		}
		else {
			CommandsMenu=uihandler.Menus[6];
			Depth=uihandler.Depth[6];
		}
	
		MenuCMDS=new Vector();
		if(StartCMD>=0||NLCMD){
			Menus.append(jmIrc.language.get(PREV),null);
			MenuCMDS.addElement("");
		}
		for(i=StartCMD+1;i<CommandsMenu.size()&&(int)Depth.charAt(i)>=DepthMenu;i++){
			if((int)Depth.charAt(i)>DepthMenu)continue;
			S=(String)CommandsMenu.elementAt(i);
			Menus.append((j=S.indexOf(":"))>=0?S.substring(0,j):S+":",null);//название меню
			if(i==Index)Menus.setSelectedIndex(Menus.size()-1,true);
			MenuCMDS.addElement(j>=0?S.substring(j+1):"");
		}
		if(!jmIrc.isConnected()&&DepthMenu==0&&!NLCMD&&!LIST){//Нельзя удалить меню "Выход"
			Menus.append(jmIrc.language.get("Exit"),null);
			MenuCMDS.addElement("/EXIT");
		}
		if(StartCMD>=0)Menus.setTitle((String)CommandsMenu.elementAt(StartCMD));
		else if(NLCMD)Menus.setTitle(Entry); 
		uihandler.setDisplay(Menus);		
		System.gc();
	}
	
	public void commandAction(Command c, Displayable s){
		if(c==cmd_Message)handleMsg("/MESSAGE");
		else 
		if(c==cmd_Menu)SetMenu(-1);
		else
		if(c==cmd_Stop)handleMsg("/LIST STOP");
		else
		if(c==cmd_ok){
			String str=textbox.getString();
			String tmp="";
			int i=0;
			if(menu!=ADD_MENU&&menu!=ADD_SUB_MENU&&menu!=EDIT_MENU&&menu!=EDIT_SUB_MENU)	
			textbox=null;
			switch (menu){
			case SET_TEXT_BOX:
				AliasText(str);
				menu=MENU_MAIN;
				while(IndexCMD<MenuCMD.length){
					TextPos=0;
					if(ParseAliases(MenuCMD[IndexCMD].length()))return;
					if(MenuCMD[IndexCMD].length()>0)handleMsg((MenuCMD[IndexCMD].charAt(0)!='/'?"/":"")+MenuCMD[IndexCMD]);
					IndexCMD++;
				}	
				CloseCommands();
		        uihandler.setWinlock(false);
				return;
			case ADD_MENU:
			case ADD_SUB_MENU:
			case EDIT_MENU:
			case EDIT_SUB_MENU:	
				if(!str.equals("")){
					for(i=0;i<str.length()&&str.charAt(i)=='.';i++);
					if(i>0)str=str.substring(i);//удаляем символы '.' из начала строки, если они есть
					if((str.indexOf(":")<=0&&(menu==ADD_MENU||menu==EDIT_MENU))||(str.indexOf(":")>=0&&(menu==ADD_SUB_MENU||menu==EDIT_SUB_MENU))){
						uihandler.AlertInfo(Warning,jmIrc.language.get((menu==ADD_MENU||menu==EDIT_MENU?"ErrorMenu":"ErrorSubMenu")));
						return;
					}
					int k=StartCMD+1;
					if(menu==ADD_MENU||menu==ADD_SUB_MENU){
						for(i=0;k<CommandsMenu.size()&&(int)Depth.charAt(k)>=DepthMenu;k++);
						CommandsMenu.insertElementAt(str,k);
						Depth=Depth.substring(0,k)+(char)(DepthMenu)+Depth.substring(k);
						//Добавляем команду не пересобирая список
						Menus.append((i=str.indexOf(":"))>0?str.substring(0,i):str+":",null);
						Menus.setSelectedIndex(Menus.size()-1,true);
						MenuCMDS.addElement(i>0?str.substring(i+1):"");
					}
					else {
						int Index=Menus.getSelectedIndex()-(StartCMD>=0||NLCMD?1:0);
						for(i=0;i<=Index&&k<CommandsMenu.size();k++){//с учётом подменю
							if((int)Depth.charAt(k)==DepthMenu)i++;//конец подменю
						}
						if(i>Index)k--;//не конец всех меню
						CommandsMenu.setElementAt(str,k);
						//Редактируем команду не пересобирая список
						Menus.set(Index=Menus.getSelectedIndex(),(i=str.indexOf(":"))>0?str.substring(0,i):str+":",null);
						MenuCMDS.setElementAt(i>0?str.substring(i+1):"",Index);
					}
					SaveCommands();
					uihandler.setDisplay(Menus);
				}
				else SetMenu(-1);
				textbox=null;
				return;
			case MENU_MESSAGE:
			case MENU_CIT:
				if(!str.equals("")){
					String[] ss=Utils.splitString(Utils.nToChar(str),"\n");//	            	позволяет писать многострочный текст с командами
					for(i=0;i<ss.length;i++)handleMsg(ss[i]);
				}
				else show();
				if(menu==MENU_CIT)textarea.End();
            	return;
			case MENU_NICK:
				if(!str.equals(uihandler.nick))jmIrc.SendIRC(NICK+" "+str);
				break;
			case MENU_TOPIC:
				if(!str.equals(Topic))jmIrc.SendIRC(TOPIC+" "+Name+" :"+Utils.RepZebra(str));
				break;
			case MENU_FAVOURITES_ADD:
			case MENU_FAVOURITES_EDIT:
				if(menu==MENU_FAVOURITES_EDIT)tmp=FavList.getString(i=FavList.getSelectedIndex());
				if(!str.equals(tmp)&&!str.equals("")){
					if(menu==MENU_FAVOURITES_EDIT)uihandler.RemoveFav(i);
					uihandler.AddFav(str);
					SetFavsList();
				}	
				menu=MENU_FAVOURITES;
				uihandler.setDisplay(FavList);
				return;
			case MENU_ACTION_ADD:
			case MENU_ACTION_EDIT:
				if(menu==MENU_ACTION_EDIT)tmp=SlapsList.getString(i=SlapsList.getSelectedIndex());
				if(!str.equals(tmp)&&!str.equals("")){
					if(menu==MENU_ACTION_EDIT)uihandler.RemoveSlap(i-(type==TYPE_CHANNEL?1:0)/*0 - CMD_BACK*/);
					uihandler.AddSlap(str);
					SetSlapsList();
				}	
				menu=MENU_ACTIONS;
				uihandler.setDisplay(SlapsList);
				return;
				
			case MENU_BANLIST_ADD:
				jmIrc.SendIRC(MODE+" "+Name+" +b "+str);
				BansList=null;
				break;
			case MENU_EXCEPTLIST_ADD:
				jmIrc.SendIRC(MODE+" "+Name+" +e "+str);
				ExceptsList=null;
				break;
			case MENU_BANLIST_EDIT:
               	if(!Entry.equals(str))jmIrc.SendIRC(MODE+" "+Name+" -b+b "+Entry+" "+str);
               	else{
               		menu=MENU_BANLIST;
               		uihandler.setDisplay(BansList);
               		return;
               	}
				BansList=null;
               	break;
			case MENU_EXCEPTLIST_EDIT:
               	if(!Entry.equals(str))jmIrc.SendIRC(MODE+" "+Name+" -e+e "+Entry+" "+str);
               	else{
               		menu=MENU_EXCEPTLIST;
               		uihandler.setDisplay(ExceptsList);
               		return;
               	}
               	ExceptsList=null;
               	break;
			}
			show();
		}
		else if(c==cmd_Action){ //!
			String str=textbox.getString();
			textbox=null;
			if(str.equals("")){
				show();
				return;
			}
        	String[] ss=Utils.splitString(Utils.nToChar(str),"\n");
        	for(int i=0;i<ss.length;i++)handleMsg("/ME "+ss[i]);
		}
		else if(c==cmd_colors){
			if(Database.UseMircCol)uihandler.setDisplay(new Colors(Width,Height));
			else uihandler.AlertInfo(Warning,jmIrc.language.get("ColorOff"));
		}
		else if(c==cmd_styles){
			Styles=new List(jmIrc.language.get("Styles"),List.IMPLICIT);
			Styles.append(jmIrc.language.get("Italic"),null);
			Styles.append(jmIrc.language.get("Underline"),null);
			Styles.append(jmIrc.language.get("Bold"),null);
			Styles.append(jmIrc.language.get("Closing"),null);
			if(menu!=SET_TEXT_BOX)Styles.append(jmIrc.language.get("Carry"),null);
			Styles.addCommand(cmd_CloseList);
			Styles.setCommandListener(this);
			uihandler.setDisplay(Styles);
		}
		else if(Styles!=null&&Styles.isShown()&&c==List.SELECT_COMMAND){
			if(Styles.getSelectedIndex()<=3&&!Database.UseMircCol){
				uihandler.AlertInfo(Warning,jmIrc.language.get("ColorOff"));
				return;
			}
			switch (Styles.getSelectedIndex()){
			case 0:TextBoxInsert("%i%");break;
			case 1:TextBoxInsert("%u%");break;
			case 2:TextBoxInsert("%b%");break;
			case 3:TextBoxInsert("%o%");break;
			case 4:TextBoxInsert("%n%");break;
			}	
			Styles=null;
			uihandler.setDisplay(textbox);
		}
		else if(c==cmd_paste_nick){
			if(textbox==null)return;
			switch(type){
			case TYPE_CHANNEL:
				listnames("");
				return;
			case TYPE_CONSOLE:
				//#if SELF
//# 			TextBoxInsert(" "+uihandler.nick);
				//#else
				TextBoxInsert(uihandler.nick);
				//#endif
				break;
			case TYPE_PRIVATE:
				//#if SELF
//# 			TextBoxInsert(" "+Name);
				//#else
				TextBoxInsert(Name);
				//#endif
				break;
			}
			uihandler.setDisplay(textbox);
		}
		else if(c==cmd_paste_chan){
			ChanList=new List(jmIrc.language.get("PasteChan"),List.IMPLICIT);
			String tmp;
			for(int i=0;i<uihandler.Channels.size();i++){
				ChanList.append(tmp=((Window)uihandler.Channels.elementAt(i)).Name,null);
				if(tmp.equals(Name))ChanList.setSelectedIndex(i,true);
			}
			ChanList.addCommand(cmd_CloseList);
			ChanList.setCommandListener(this);
			uihandler.setDisplay(ChanList);
		}
		else if(ChanList!=null&&ChanList.isShown()&&c==List.SELECT_COMMAND){
				//#if SELF
//# 						TextBoxInsert(" "+ChanList.getString(ChanList.getSelectedIndex()));
				//#else
			TextBoxInsert(ChanList.getString(ChanList.getSelectedIndex()));
				//#endif
			ChanList=null;
			uihandler.setDisplay(textbox);
		}
		else if(c==cmd_paste)TextBoxInsert(UIHandler.Buffer);
		else if(c==cmd_cancel){
			textbox=null;
			switch(menu){
			case SET_TEXT_BOX:
				CloseCommands();
				show();
				break;
			case ADD_MENU:
			case ADD_SUB_MENU:
			case EDIT_MENU:
			case EDIT_SUB_MENU:	
				uihandler.setDisplay(Menus);
				break;
			case MENU_FAVOURITES_ADD:
			case MENU_FAVOURITES_EDIT:
				menu=MENU_FAVOURITES;
				uihandler.setDisplay(FavList);
				break;
			case MENU_ACTION_ADD:
			case MENU_ACTION_EDIT:
				menu=MENU_ACTIONS;
				uihandler.setDisplay(SlapsList);
				break;
			case MENU_BANLIST_ADD:	
			case MENU_BANLIST_EDIT:
				menu=MENU_BANLIST;
				uihandler.setDisplay(BansList);
				break;
			case MENU_EXCEPTLIST_ADD:	
			case MENU_EXCEPTLIST_EDIT:
				menu=MENU_EXCEPTLIST;
				uihandler.setDisplay(ExceptsList);
				break;
			default:
//case MENU_MESSAGE: case MENU_WHOIS: case MENU_JOIN: case MENU_QUERY:  case MENU_NICK: case: MENU_TOPIC: case MENU_NOTICE: и тд
				show();
				break;
			}
		}
		else if(c==cmd_AddMenu||c==cmd_AddSubMenu){
			menu=(c==cmd_AddMenu?ADD_MENU:ADD_SUB_MENU);
			AddTextCommand(jmIrc.language.get((c==cmd_AddMenu?ADD:"SubMenu")),null);
			uihandler.setDisplay(textbox);
		}
		else if(c==cmd_EditMenu&&(Menus.getSelectedIndex()>0/*<<Назад*/||(DepthMenu==0&&!NLCMD)/*В начальном меню нет <<Назад*/)){
			int Index=Menus.getSelectedIndex();
			String str=Menus.getString(Index);
			if(!jmIrc.isConnected()&&DepthMenu==0&&!NLCMD&&Index==Menus.size()-1)return;//ВЫход
			if(MenuCMDS.elementAt(Index).equals("")){
				menu=EDIT_SUB_MENU;
				str=str.substring(0,str.length()-1);//убираем двоеточие
			}
			else {
				menu=EDIT_MENU;
				str=str+':'+MenuCMDS.elementAt(Index);
			}
			AddTextCommand(jmIrc.language.get(EDIT),str);
			uihandler.setDisplay(textbox);
		}
		else if(c==cmd_DelMenu&&(Menus.getSelectedIndex()>0||(DepthMenu==0&&!NLCMD))){
			if(!jmIrc.isConnected()&&DepthMenu==0&&!NLCMD&&Menus.getSelectedIndex()==Menus.size()-1)return;//Выход
			int i,k=StartCMD+1,Index=Menus.getSelectedIndex()-(StartCMD>=0||NLCMD?1:0)/*<<Назад*/;
			for(i=0;i<=Index&&k<CommandsMenu.size();k++){//с учётом подменю
				if((int)Depth.charAt(k)==DepthMenu)i++;//конец подменю
			}
			if(i>Index)k--;//не конец всех меню
			
			if(MenuCMDS.elementAt(Menus.getSelectedIndex()).equals("")&&k<CommandsMenu.size()-1&&Depth.charAt(k+1)>DepthMenu){
				uihandler.AlertInfo(Warning,jmIrc.language.get("ErrorDelSub"));
				return;
			}
			CommandsMenu.removeElementAt(k);
			Depth=Depth.substring(0,k)+Depth.substring(k+1);
//Удаляем команды не пересобирая список
			Menus.delete(i=Menus.getSelectedIndex());
			MenuCMDS.removeElementAt(i);
			SaveCommands();
		}
		else if(c==cmd_CopyMenu){
			int i=Menus.getSelectedIndex();
			String tmp=(String)MenuCMDS.elementAt(i);
			String str=Menus.getString(i);
			UIHandler.Buffer=(tmp.equals("")?str.substring(0,str.length()-1):str+":"+tmp);
		}
		else if(c==cmd_UpMenu||c==cmd_DownMenu){
			String str;
			int i,Index=Menus.getSelectedIndex();
			if(!jmIrc.isConnected()&&DepthMenu==0&&!NLCMD&&Menus.getSelectedIndex()-(c==cmd_UpMenu?0:1)==Menus.size()-1)return;//Выход
			boolean up=(Index<=0+(DepthMenu==0&&!NLCMD?0:1));//Меню "Назад" (в главном меню есть только в списке ников) или следующее меню
			boolean down=(Index<0+(DepthMenu==0&&!NLCMD?0:1))||(Index==Menus.size()-1);//Меню "Назад" (кроме первого меню НЕ списка ников) или последнее меню
			
			
			if(c==cmd_UpMenu&&up)return;//уже вверху
			if(c==cmd_DownMenu&&down)return;//уже внизу
			if(c==cmd_DownMenu)Index++;//перемещение вниз=перемещение вверх следующего элемента списка ;)
			Index-=(StartCMD>=0||NLCMD?1:0);/*<<Назад*/
			int k=StartCMD+1,j=k;
			
			for(i=0;i<=Index-1&&j<CommandsMenu.size();j++){//с учётом подменю
				if((int)Depth.charAt(j)==DepthMenu)i++;//конец подменю
			}
			for(i=0;i<=Index&&k<CommandsMenu.size();k++){//с учётом подменю
				if((int)Depth.charAt(k)==DepthMenu)i++;//конец подменю
			}
			if(j==k)return;
			if(i>Index)k--;j--;
			str=(String)CommandsMenu.elementAt(k);
			char b=Depth.charAt(k);
			Depth=Depth.substring(0,k)+Depth.substring(k+1);
			Depth=Depth.substring(0,j)+b+Depth.substring(j);
			
			CommandsMenu.removeElementAt(k++);
			CommandsMenu.insertElementAt(str,j++);
			if(str.indexOf(":")==-1){//перемещаем с подменю
				while(k<CommandsMenu.size()&&((int)Depth.charAt(k)>=DepthMenu+1)){
					str=(String)CommandsMenu.elementAt(k);
					b=Depth.charAt(k);
					Depth=Depth.substring(0,k)+Depth.substring(k+1);
					Depth=Depth.substring(0,j)+b+Depth.substring(j);
					CommandsMenu.removeElementAt(k++);
					CommandsMenu.insertElementAt(str,j++);
				}
			}
//меняем местами команды в списке не пересобирая список
			str=Menus.getString(i=Menus.getSelectedIndex());
			Menus.set(i,Menus.getString(j=i+(c==cmd_UpMenu?-1:1)),null);
			Menus.set(j,str,null);
			Menus.setSelectedIndex(j,true);
			str=(String)MenuCMDS.elementAt(i);
			MenuCMDS.setElementAt((String)MenuCMDS.elementAt(j),i);
			MenuCMDS.setElementAt((String)str,j);
			SaveCommands();
		}
		else if(c==cmd_favourites)SetFavsList(); 
		else if(NamesList!=null&&NamesList.isShown()&&c==List.SELECT_COMMAND){//должен быть выше BanList и ExceptList чтобы можно было использоватьменю "Вставить ник", т.к. списки не обнуляются
			int Index=NamesList.getSelectedIndex();
			String nick=NamesList.getString(Index);
			NamesList=null;
			int mp=(person_position+1)*MaxList;
			if(person_position>0&&Index==0){//Prev
				person_position--;
				listnames("");
			}
			else if((mp<Names.size()&&person_position>0&&Index==1)||(person_position==0&&Index==0&&Names.size()>MaxList)){//Next
				person_position++;
				listnames("");
			}
			else {
				if(textbox!=null){//cmd_paste_nick
					//#ifndef SELF
					TextBoxInsert(nick.substring(1));
					//#else
//# 				TextBoxInsert(" "+nick.substring(1));
					//#endif
					uihandler.setDisplay(textbox);
					return;
				}
				Entry=nick.substring(1);
				NLCMD=true;
				SetMenu(-1);
			}
		}
		else if(c==cmd_Back){
			CloseCommands();
			show();
		}
		else if(c==cmd_Join&&Menus.getSelectedIndex()>0){
			handleMsg("/JOIN "+Menus.getString(Menus.getSelectedIndex()));
			StartCMD=-1;DepthMenu=0;
		}
    	else if(c==cmd_Topic&&Menus.getSelectedIndex()>0){
    		String str=Menus.getString(Menus.getSelectedIndex());
    		AddInfo(jmIrc.language.get("TopicList",str));
    		handleMsg("/TOPIC "+str);
    	}
		else if(Menus!=null&&Menus.isShown()&&c==List.SELECT_COMMAND){
			int j,Index=Menus.getSelectedIndex();
			if(StartCMD==-1&&Index==0&&NLCMD)handleMsg("/NAMESLIST "+Entry);//Назад
			else
			if(StartCMD>=0&&Index==0){//<<Назад
				DepthMenu--;
				j=StartCMD;
				if(DepthMenu==0)StartCMD=-1;
				else for(;StartCMD>=0&&(int)Depth.charAt(StartCMD)>=DepthMenu;StartCMD--);
				SetMenu(j);
			}
			else
			if(MenuCMDS.elementAt(Index).equals("")){//выбор подменю
				Index=Index-(StartCMD>=0||NLCMD?1/*<<Назад*/:0);
				for(j=0;j<=Index&&StartCMD+1<CommandsMenu.size();StartCMD++)if((int)Depth.charAt(StartCMD+1)==DepthMenu)j++;//конец подменю
				DepthMenu++;
				SetMenu(-1);
			}
			else if(LIST)this.commandAction(cmd_Info,null);
			else 
				ParseCMD((String)MenuCMDS.elementAt(Index));
		}
		else if(c==cmd_apply){
			int t,j=Modes_CB.indexOf(" ");
			String Modes=(j>0?Modes_CB.substring(0,j):"");
			String AllModes=Listener.MODES_CB[0]+Listener.MODES_CB[1];
			String Params[]=Utils.splitString((j>0?Modes_CB.substring(j+1):"")," ");
			int k=Listener.MODES_CB[0].length()+Listener.MODES_CB[1].length();
			String M1="",M2="",P1="",P2="";
			int m=0;
			for(j=0;j<k;j++){
				t=Modes.indexOf(AllModes.charAt(j));
				if(CG_Modes_BC[j].isSelected(0)&&!TF_Modes[j].getString().equals("")&&(t<0||t>=0&&!TF_Modes[j].getString().equals(Params[t]))){
					M1+=AllModes.charAt(j);
					P1+=" "+TF_Modes[j].getString();
					m++;
				}
				else if(!CG_Modes_BC[j].isSelected(0)&&t>=0){
					M2+=AllModes.charAt(j);
					P2+=" "+Params[t];
					m++;
				}
				if(m>=Listener.MAXMODES){
					jmIrc.SendIRC("MODE "+Name+" +"+M1+"-"+M2+P1+P2);//M1 или M2 всегда есть
					M1="";M2="";P1="";P2="";m=0;
				}
			}
			k=Listener.MODES_D.length();
			for(j=0;j<k;j++){
				if(CG_Modes_D.isSelected(j)&&Modes_D.indexOf(Listener.MODES_D.charAt(j))==-1){M1+=Listener.MODES_D.charAt(j);m++;}
				else if(!CG_Modes_D.isSelected(j)&&Modes_D.indexOf(Listener.MODES_D.charAt(j))>=0){M2+=Listener.MODES_D.charAt(j);m++;}
				if(m>=Listener.MAXMODES){
					jmIrc.SendIRC("MODE "+Name+" +"+M1+"-"+M2+P1+P2);//M1 или M2 всегда есть
					M1="";M2="";P1="";P2="";m=0;
				}
			}
			if(m>0&&(!M1.equals("")||!M2.equals("")))jmIrc.SendIRC("MODE "+Name+" +"+M1+"-"+M2+P1+P2);
			this.commandAction(cmd_CloseList,null);	
		}
		else if(c==cmd_Add){
			menu++;
			//MENU_FAVOURITES -> MENU_FAVOURITES_ADD
			//MENU_ACTIONS    -> MENU_ACTION_ADD
			//MENU_BANLIST    -> MENU_BANLIST_ADD
			//MENU_EXCEPTLIST -> MENU_EXCEPT_ADD
			AddTextCommand(jmIrc.language.get(ADD),null);
			uihandler.setDisplay(textbox);
		}
		else if(c==cmd_Edit){
			switch(menu){
			case MENU_FAVOURITES:
				menu=MENU_FAVOURITES_EDIT;
				AddTextCommand(jmIrc.language.get(EDIT),FavList.getString(FavList.getSelectedIndex()));
				break;
			case MENU_ACTIONS:
				if(SlapsList.getSelectedIndex()==(type==TYPE_CHANNEL?0:-1))return;
				menu=MENU_ACTION_EDIT;
				AddTextCommand(jmIrc.language.get(EDIT),SlapsList.getString(SlapsList.getSelectedIndex()));
				break;
			case MENU_BANLIST:
				menu=MENU_BANLIST_EDIT;
				if(!IsBan())return;
				AddTextCommand(jmIrc.language.get(EDIT),(Entry=BansList.getString(BansList.getSelectedIndex())));//запоминаем маску
				break;
			case MENU_EXCEPTLIST:
				menu=MENU_EXCEPTLIST_EDIT;
				if(!IsExcept())return;
				AddTextCommand(jmIrc.language.get(EDIT),(Entry=ExceptsList.getString(ExceptsList.getSelectedIndex())));//запоминаем маску
			}	
			uihandler.setDisplay(textbox);
		}
		else if(c==cmd_Del){
			switch(menu){
			case MENU_FAVOURITES:
				uihandler.RemoveFav(FavList.getSelectedIndex());
				FavList.delete(FavList.getSelectedIndex());
				return;
			case MENU_ACTIONS:
				if(SlapsList.getSelectedIndex()==(type==TYPE_CHANNEL?0:-1)/*0 - CMD_BACK*/)return;
				uihandler.RemoveSlap(SlapsList.getSelectedIndex()-(type==TYPE_CHANNEL?1:0));
				SlapsList.delete(SlapsList.getSelectedIndex());
				return;
			case MENU_BANLIST:
				if(!IsBan())return;
				jmIrc.SendIRC(MODE+" "+Name+" -b "+BansList.getString(BansList.getSelectedIndex()));
				break;
			case MENU_EXCEPTLIST:
				if(!IsExcept())return;
				jmIrc.SendIRC(MODE+" "+Name+" -e "+ExceptsList.getString(ExceptsList.getSelectedIndex()));
				break;
			case MENU_DEL_BANS:
			case MENU_DEL_EXCEPTS:
				boolean[] bools=new boolean[CG_List.size()];
				CG_List.getSelectedFlags(bools);
				int Modes=0;
				String Params="";
				for(int i=0;i<bools.length;i++){
					if(bools[i]){
						Modes++;
						Params+=" "+CG_List.getString(i);
						if(Modes>=Listener.MAXMODES){
							jmIrc.SendIRC(MODE+" "+Name+" -"+Utils.Str((menu==MENU_DEL_BANS?'b':'e'),Modes)+Params);
							Params="";
							Modes=0;
						}
					}
				}
				if(Modes>0)jmIrc.SendIRC(MODE+" "+Name+" -"+Utils.Str((menu==MENU_DEL_BANS?'b':'e'),Modes)+Params);
				CG_List=null;
				break;
			}	
			show();
		}	
		else if(c==cmd_Info&&LIST){
			int Index=Menus.getSelectedIndex();
			if(Index>0)uihandler.AlertInfo(Info,jmIrc.language.get("ChanListInfo",Menus.getString(Index)+" "+(String)MenuCMDS.elementAt(Index)));
		}			

		else if(c==cmd_Info||c==cmd_Copy){
			int i,j;
			String mask;
			switch(menu){
			case MENU_BANLIST:
				if(!IsBan())return;
				j=Bans.size();
				mask=BansList.getString(BansList.getSelectedIndex());
				for(i=0;i<j;i++){
					String tmp=(String)Bans.elementAt(i);
					tmp=tmp.substring(0,tmp.indexOf(' '));
					if(tmp.equals(mask)){
						tmp=(String)Bans.elementAt(i);
						i=Utils.LastIndexOf(tmp," ");
						if(i>=0)tmp=jmIrc.language.get("BanInfo",tmp.substring(0,i)+' '+Utils.parseTime((Utils.GetTimeStamp()-Long.parseLong(tmp.substring(i+1)))));
						else tmp=tmp.substring(0,i);
						if(c==cmd_Copy)UIHandler.Buffer=tmp;
						else uihandler.AlertInfo(Info,Utils.Strip(tmp));
						break;
					}
				}
				break;
			case MENU_EXCEPTLIST:
				j=Excepts.size();
				mask=ExceptsList.getString(ExceptsList.getSelectedIndex());
				for(i=0;i<j;i++){
					String tmp=(String)Excepts.elementAt(i);
					tmp=tmp.substring(0,tmp.indexOf(' '));
					if(tmp.equals(mask)){
						tmp=(String)Excepts.elementAt(i);
						i=Utils.LastIndexOf(tmp," ");
						if(i>=0)tmp=jmIrc.language.get("ExceptInfo",tmp.substring(0,i)+' '+Utils.parseTime((Utils.GetTimeStamp()-Long.parseLong(tmp.substring(i+1)))));
						else tmp=tmp.substring(0,i);
						if(c==cmd_Copy)UIHandler.Buffer=tmp;
						else uihandler.AlertInfo(Info,Utils.Strip(tmp));
						break;					
					}
				}
				break;
				
			}
		}
		else if(c==cmd_Mark){
			CG_List=new ChoiceGroup(null, Choice.MULTIPLE);
			if(menu==MENU_BANLIST)SetDeleteBans();
			if(menu==MENU_EXCEPTLIST)SetDeleteExcepts();
			WinForm.append(CG_List);
			WinForm.addCommand(cmd_Del);  
			WinForm.addCommand(cmd_CloseList);   
			WinForm.setCommandListener(this);
			uihandler.setDisplay(WinForm);
		}
		else if(FavList!=null&&FavList.isShown()&&c==List.SELECT_COMMAND){
			String msg=FavList.getString(FavList.getSelectedIndex());
			FavList=null;
			if(textbox!=null){//если текстбокс есть, то вставляем в текстбокс, иначе посылаем как текст\команда
				TextBoxInsert(msg);
				uihandler.setDisplay(textbox);
			}
			else {
				String[] ss=Utils.splitString(Utils.RepZebra(msg),"\n");//позволяет писать многострочный текст с командами
				for(int i=0;i<ss.length;i++)handleMsg(ss[i]);
				show();
			}
		}
		else if(SlapsList!=null&&SlapsList.isShown()&&c==List.SELECT_COMMAND){
			int i=SlapsList.getSelectedIndex();
			String msg=SlapsList.getString(i);
			SlapsList=null;
			if(i==0&&type==TYPE_CHANNEL)listnames(Entry);
			else {
				msg=Utils.Replace(msg,"%nick%",(type==TYPE_CHANNEL?Entry:Name));
				msg=Utils.Replace(msg,"%chan%",Name);
				msg=Utils.Replace(msg,"%me%",uihandler.nick);
				String[] ss=Utils.splitString(Utils.RepZebra(msg),"\n");//позволяет писать многострочный текст с командами
				for(i=0;i<ss.length;i++)handleMsg("/ME "+ss[i]);
				show();
			}
		}
		else if(c==cmd_DelIgnores){ //!
			boolean[] bools=new boolean[CG_List.size()];
			CG_List.getSelectedFlags(bools);
			int j=0;
			for(int i=0;i<bools.length;i++,j++)
				if(bools[i]){
					uihandler.RemoveIgnore(j--);//внутренний список уменьшился
					jmIrc.SendIRC(SILENCE+" -"+(CG_List.getString(i).charAt(0)=='¤'?CG_List.getString(i).substring(1):CG_List.getString(i)));
				}
			Vector list=uihandler.Menus[2];
			CG_List.deleteAll();
			for(int i=0;i<list.size();i++)CG_List.append((String)list.elementAt(i),null);
		}
		else if(c==cmd_CloseList){//Bans и Excepts не обнулять!
			bans_position=0;
			excepts_position=0;

			NamesList=null;
			OpDeopList=null;
			WinForm=null;
			TF_Modes=null;
			CG_Modes_BC=null;
			CG_Modes_D=null;
			CG_List=null;
			URLList=null;
			IgnoresList=null;
			Styles=null;
			ChanList=null;
			
			if(menu!=MENU_ACTION_ADD&&menu!=MENU_ACTION_EDIT)SlapsList=null;

			
			if(menu!=MENU_BANLIST_ADD&&menu!=MENU_BANLIST_EDIT)BansList=null;
			if(menu!=MENU_EXCEPTLIST_ADD&&menu!=MENU_EXCEPTLIST_EDIT)ExceptsList=null;
			if(menu!=MENU_FAVOURITES_ADD&&menu!=MENU_FAVOURITES_EDIT)FavList=null;
			if(textbox!=null)uihandler.setDisplay(textbox);
			else {
				CloseCommands();
				show();
			}
			if(LIST)close();

			
		}
		else if(BansList!=null&&BansList.isShown()&&c==List.SELECT_COMMAND){
			int Index=BansList.getSelectedIndex();
			int mp=(bans_position+1)*MaxList;
			if(bans_position>0&&Index==0)bans_position--;//Prev
			else if((mp<Bans.size()&&bans_position>0&&Index==1)||(bans_position==0&&Index==0&&Bans.size()>MaxList))bans_position++;//Next
			else return;
			ListBans();
		}
		else if(ExceptsList!=null&&ExceptsList.isShown()&&c==List.SELECT_COMMAND){
			int Index=ExceptsList.getSelectedIndex();
			int mp=(excepts_position+1)*MaxList;
			if(excepts_position>0&&Index==0)excepts_position--;//Prev
			else if((mp<Excepts.size()&&excepts_position>0&&Index==1)||(excepts_position==0&&Index==0&&Excepts.size()>MaxList))excepts_position++;//Next
			else return;
			ListExcepts();
		}
		else if(URLList!=null&&URLList.isShown()&&c==List.SELECT_COMMAND){
			String URL=URLList.getString(URLList.getSelectedIndex());
			URLList=null;
			uihandler.OpenURL(URL);
			show();
		}
                //Mod
                else if(StateList!=null&&StateList.isShown()&&c==List.SELECT_COMMAND){
			int Index=StateList.getSelectedIndex();
                        StateList=null;
                        switch (Index) {
                          case 0: EnVibro=!EnVibro;break;
                          case 1: EnBeep=!EnBeep;break;
                          case 2: EnLgtChg=!EnLgtChg;break;
                          ////#if SELF
                      /*    case 3: jmIrc.DarkThemes=(jmIrc.DarkThemes==0)?1:0;
                                  repaint();break; // нестабильно
                       */   ////#endif
                          //#if DEBUGER
//#                           case 4: 
//#                             EnDebuger=!EnDebuger;
//#                             if (!EnDebuger)uihandler.deleteWindow(uihandler.GetChannel("!RAW"));
                          //#endif
                        }
			listState(Index);
		}
		else if(OpDeopList!=null&&OpDeopList.isShown()&&c==List.SELECT_COMMAND){
			int Index=OpDeopList.getSelectedIndex();
			OpDeopList=null;
			if(Index==0){
				listnames(Entry);
				return;
			}
			Index--;
			String mode="-";
			if((Index&1)==0)mode="+";
			mode+=Listener.PREFIXMODES.charAt(Index>>1);
			jmIrc.SendIRC(MODE+" "+Name+" "+mode+" "+Entry);
			show();
		}
	}	
	
	private boolean IsBan(){
		int Index=BansList.getSelectedIndex();
		int mp=(bans_position+1)*MaxList;
		if(bans_position>0&&Index==0)return false;//Prev
		if((mp<Bans.size()&&bans_position>0&&Index==1)||(bans_position==0&&Index==0&&Bans.size()>MaxList))return false;//Next
		return true;
	}

	private boolean IsExcept(){
		int Index=ExceptsList.getSelectedIndex();
		int mp=(excepts_position+1)*MaxList;
		if(excepts_position>0&&Index==0)return false;
		if((mp<Excepts.size()&&excepts_position>0&&Index==1)||(excepts_position==0&&Index==0&&Excepts.size()>MaxList))return false;
		return true;
	}
	
	
	public void Perform(){
		if(!Database.Perform.equals("")){
   			String [] Perform=Utils.splitString(Utils.CodeToChars(Database.Perform),";");
   			for(int i=0;i<Perform.length;i++)handleMsg((Perform[i].trim().charAt(0)!='/'?"/":"")+Perform[i].trim(),true);
   		}
   		AddInfo(jmIrc.language.get("RunPerform",""));
	}
	
	private void SetModes(){
		uihandler.setWinlock(true);
		WinForm=new Form(jmIrc.language.get("Modes"));
		WinForm.append(Name);
		int j=Modes_CB.indexOf(" ");
		String Modes=(j>0?Modes_CB.substring(0,j):"");
		String AllModes=Listener.MODES_CB[0]+Listener.MODES_CB[1];
		String Params[]=Utils.splitString((j>0?Modes_CB.substring(j+1):"")," ");
		int k=Listener.MODES_CB[0].length()+Listener.MODES_CB[1].length();
		TF_Modes=new TextField[k];
		CG_Modes_BC=new ChoiceGroup[k];
		for(j=0;j<k;j++){
			CG_Modes_BC[j]=new ChoiceGroup(null,Choice.MULTIPLE);
			String S=jmIrc.language.get("+"+AllModes.charAt(j),false);
			if(S==null)S="+"+AllModes.charAt(j);
			CG_Modes_BC[j].append(S,null);
			CG_Modes_BC[j].setSelectedIndex(0,(Modes.indexOf(AllModes.charAt(j))>=0));
			TF_Modes[j]=new TextField(null,((Modes.indexOf(AllModes.charAt(j))>=0)?Params[Modes.indexOf(AllModes.charAt(j))]:""),50,TextField.ANY);
			WinForm.append(CG_Modes_BC[j]);
			WinForm.append(TF_Modes[j]);
		}
		k=Listener.MODES_D.length();
		CG_Modes_D=new ChoiceGroup(null,Choice.MULTIPLE);
		for(j=0;j<k;j++){
			String S=jmIrc.language.get("+"+Listener.MODES_D.charAt(j),false);
			if(S==null)S="+"+Listener.MODES_D.charAt(j);
			CG_Modes_D.append(S,null);
			CG_Modes_D.setSelectedIndex(j,(Modes_D.indexOf(Listener.MODES_D.charAt(j))>=0));
		}
		WinForm.append(CG_Modes_D);
		WinForm.addCommand(cmd_apply);  
		WinForm.addCommand(cmd_CloseList);   
		WinForm.setCommandListener(this);
		uihandler.setDisplay(WinForm);
	}
	
	public void AddToIgnoreList(String mask){
		if(IgnoresList==null){
			IgnoresList=new Vector();
			uihandler.setWinlock(true);
		}
		IgnoresList.addElement((uihandler.isIgnore(mask)?"":"¤")+mask);
	}
	
	public void SetIgnoresList(){
		if(IgnoresList==null)IgnoresList=new Vector();
		uihandler.setWinlock(true);//если список пуст
		Entry=uihandler.GetActiveWindow().Name;
		WinForm=new Form(jmIrc.language.get("IgnoresList",null));
		CG_List=new ChoiceGroup(null, Choice.MULTIPLE);
		uihandler.Menus[2].removeAllElements();
		for(int i=0;i<IgnoresList.size();i++){
			CG_List.append((String)IgnoresList.elementAt(i),null);
			uihandler.Menus[2].addElement((String)IgnoresList.elementAt(i));
		}
		WinForm.append(CG_List);
		WinForm.addCommand(cmd_DelIgnores);  
		WinForm.addCommand(cmd_CloseList);   
		WinForm.setCommandListener(this);
		uihandler.setDisplay(WinForm);
	}
	
	private void SaveCommands(){
		if(type==TYPE_CONSOLE){
			uihandler.MenuChange[3]=true;
			uihandler.Depth[3]=Depth;
		}
		else if(type==TYPE_CHANNEL&&!NLCMD){
			uihandler.MenuChange[4]=true;
			uihandler.Depth[4]=Depth;
		}
		else if(type==TYPE_CHANNEL&&NLCMD){
			uihandler.MenuChange[5]=true;
			uihandler.Depth[5]=Depth;
		}
		else {
			uihandler.MenuChange[6]=true;
			uihandler.Depth[6]=Depth;
		}
	}
	
	private void CloseCommands(){
		DepthMenu=0;
		StartCMD=-1;
		MenuCMDS=null;
		CommandsMenu=null;
		Depth=null;
		Menus=null;
		NLCMD=false;
		MenuCMD=null;
		Buffer="";
	}
	
	private void ParseCMD(String cmd){
		String s;
		if(type==TYPE_PRIVATE)s=Name;
		else if(type==TYPE_CHANNEL&&NLCMD)s=Entry;
		else s="";
		cmd=Utils.Replace(cmd," | ","\n");
		if(!s.equals(""))cmd=Utils.Replace(cmd,"%nick%",s);
		if(type==TYPE_CHANNEL)s=Name;
		else s="";
		if(!s.equals(""))cmd=Utils.Replace(cmd,"%chan%",s);
		cmd=Utils.Replace(cmd,"%me%",uihandler.nick);
		MenuCMD=Utils.splitString(cmd,"\n");
		IndexCMD=0;
		AliasPos=0;
		while(IndexCMD<MenuCMD.length){
			TextPos=0;
			if(ParseAliases(MenuCMD[IndexCMD].length()))return;
			if(MenuCMD[IndexCMD].length()>0)handleMsg((MenuCMD[IndexCMD].charAt(0)!='/'?"/":"")+MenuCMD[IndexCMD]);
			IndexCMD++;
		}	
        uihandler.setWinlock(false);
		CloseCommands();
	}
	
	public void SetAdddress(String mask){
		AliasText(mask);		
		while(IndexCMD<MenuCMD.length){
			TextPos=0;
			if(ParseAliases(MenuCMD[IndexCMD].length()))return;
			if(MenuCMD[IndexCMD].length()>0)handleMsg((MenuCMD[IndexCMD].charAt(0)!='/'?"/":"")+MenuCMD[IndexCMD]);
			IndexCMD++;
		}	
		CloseCommands();
        uihandler.setWinlock(false);
	}
	
	private boolean ParseAliases(int last){
		 while((AliasPos=MenuCMD[IndexCMD].indexOf("%",TextPos=AliasPos))>=0&&AliasPos<last){
			 int i=MenuCMD[IndexCMD].length()-AliasPos;
			 if(i>6&&MenuCMD[IndexCMD].substring(AliasPos,AliasPos+6).equals("%text(")){
				 if(GetText())return true;
			 }
			 else if(i>9&&MenuCMD[IndexCMD].substring(AliasPos,AliasPos+9).equals("%address(")){
				 if(GetAddress())return true;
			 }
			 else if(i>9&&MenuCMD[IndexCMD].substring(AliasPos,AliasPos+9).equals("%letters("))GetLetters();
			 else if(i>=8&&MenuCMD[IndexCMD].substring(AliasPos,AliasPos+8).equals("%buffer%"))GetBuffer(false);
			 else if(i>=9&&MenuCMD[IndexCMD].substring(AliasPos,AliasPos+9).equals("%tempbuf%"))GetBuffer(true);
			 else AliasPos++;
		 }	 
         return false;
	}
	
	private void AliasText(String Text){
		MenuCMD[IndexCMD]=MenuCMD[IndexCMD].substring(0,TextPos)+Text+MenuCMD[IndexCMD].substring(AliasPos+2);
		AliasPos=TextPos+Text.length();
	}
	
	private boolean GetText(){
		TextPos=AliasPos;
		AliasPos+=6;
		int i=MenuCMD[IndexCMD].indexOf("%",AliasPos);
		int j=MenuCMD[IndexCMD].indexOf(")%",AliasPos);
		int aPos=AliasPos,tPos=TextPos;//запоминаем позиции
		if(i<j){if(ParseAliases(j))return true;}		
		String alias="",Title="",Text="";
		TextPos=tPos;
		if((j=MenuCMD[IndexCMD].indexOf(")%",AliasPos=aPos))<0)return false;
		alias=MenuCMD[IndexCMD].substring(AliasPos,j);//тело алиаса
		AliasPos=j;
		if((i=alias.indexOf(','))>=0){
			Title=alias.substring(0,i);
			Text=alias.substring(i+1);
		}
		else Title=alias;
		menu=SET_TEXT_BOX;
		AddTextCommand(Title,Text);
		uihandler.setDisplay(textbox);
		return true;
	}
	
	private void GetLetters(){//%letters(text,n1[,n2])% - возвращает часть текста с позиции n1 до n2, если n2 не указано, то возвращает текст с позиции n1 до конца. В случае ошибки возвращается пустая строка
		TextPos=AliasPos;
		AliasPos+=9;
		int i=MenuCMD[IndexCMD].indexOf("%",AliasPos);
		int j=MenuCMD[IndexCMD].indexOf(")%",AliasPos);
		int aPos=AliasPos,tPos=TextPos;//запоминаем позиции
		if(i<j)ParseAliases(j);		
		String alias="",Text="",N1="",ret="";
		TextPos=tPos;
		if((j=MenuCMD[IndexCMD].indexOf(")%",AliasPos=aPos))<0)return;
		alias=MenuCMD[IndexCMD].substring(AliasPos,j);//тело алиаса
		AliasPos=j;
		if((i=alias.indexOf(','))>0){
			Text=alias.substring(0,i);
			N1=alias.substring(i+1);
			if((i=N1.indexOf(','))>0){//Text,N1,N2
				j=Utils.parseInt(N1.substring(i+1));
				i=Utils.parseInt(N1.substring(0,i))-1;
				if(i>=0&&j>0&&j<=Text.length()&&i<j)ret=Text.substring(i,j);					
				else if(i>0&&j<0&&-j<=Text.length()&&i<Text.length()+j+1)ret=Text.substring(i,Text.length()+j+1);					
				else if(i<0&&j<0&&-i<=Text.length()&&i<j)ret=Text.substring(Text.length()+i+1,Text.length()+j+1);					
			}
			else if((i=Utils.parseInt(N1))>0&&i<=Text.length())ret=Text.substring(i-1);					
			else if(i<0&&-i<=Text.length())ret=Text.substring(0,Text.length()+i);					
		}
		AliasText(ret);
	}
	
	private void GetBuffer(boolean temp){
		TextPos=AliasPos;
		AliasPos+=6+(temp?1:0);//8-2 (tmp +1)
		AliasText(temp?Buffer:UIHandler.Buffer);
	}
	
	private boolean GetAddress(){
		TextPos=AliasPos;
		AliasPos+=9;
		int i=MenuCMD[IndexCMD].indexOf("%",AliasPos);
		int j=MenuCMD[IndexCMD].indexOf(")%",AliasPos);
		int aPos=AliasPos,tPos=TextPos;//запоминаем позиции
		if(i<j){if(ParseAliases(j))return true;}		
		String alias="";
		TextPos=tPos;
		if((j=MenuCMD[IndexCMD].indexOf(")%",AliasPos=aPos))<0)return false;
		alias=MenuCMD[IndexCMD].substring(AliasPos,j);//тело алиаса
		AliasPos=j;
		int m=Utils.parseInt(jmIrc.language.get("DefBanMask"));
		String Nick;
		if((i=alias.indexOf(','))>=0){
			if((m=Utils.parseInt(alias.substring(i+1)))<1)m=1;
			if(m>5)m=5;
			Nick=alias.substring(0,i);
		}
		else Nick=alias;
		if(m==1){
			AliasText(Nick+"!*@*");			
			return false;
		}
		Listener.MenuAddress=(char)m+Nick+":"+(char)type+Name.toUpperCase();
		jmIrc.SendIRC("USERHOST "+Nick);
		show();
		return true;
	}
	
	private void SetDeleteBans(){
		menu=MENU_DEL_BANS;
		WinForm=new Form(jmIrc.language.get("BansList",null));
		int n=Bans.size();
		for(int i=0;i<n;i++){
			int j=((String)Bans.elementAt(i)).indexOf(' ');
			CG_List.append(((String)Bans.elementAt(i)).substring(0,j),null);
		}
	}

	private void SetDeleteExcepts(){
		menu=MENU_DEL_EXCEPTS;
		WinForm=new Form(jmIrc.language.get("ExceptsList",null));
		int n=Excepts.size();
		for(int i=0;i<n;i++){
			int j=((String)Excepts.elementAt(i)).indexOf(' ');
			CG_List.append(((String)Excepts.elementAt(i)).substring(0,j),null);
		}
	}
	
	
	private void SetFavsList(){
		uihandler.setWinlock(true);
		Vector list=uihandler.Menus[0];
		FavList=new List(jmIrc.language.get("Favourites"),List.IMPLICIT);
		for(int i=0;i<list.size();i++)FavList.append((String)list.elementAt(i),null);
		if(textbox==null){
			menu=MENU_FAVOURITES;
			FavList.addCommand(cmd_Add);
			FavList.addCommand(cmd_Edit);
			FavList.addCommand(cmd_Del);
		}
		FavList.addCommand(cmd_CloseList);
		FavList.setCommandListener(this);
		uihandler.setDisplay(FavList);
	}
	
	private void SetSlapsList(){
		SlapsList.deleteAll();
		if(type==TYPE_CHANNEL)SlapsList.append(jmIrc.language.get(PREV),null);
		Vector list=uihandler.Menus[1];
		for(int i=0;i<list.size();i++)SlapsList.append((String)list.elementAt(i),null);
	}
	private void SetBackgroundMode(){
		show();
		jmIrc.lastDisplay=this;
		jmIrc.minimized=true;
		uihandler.setDisplay(null);
	}
	
	private String ParseTraf(int b){
		int mb,kb;
		mb=b/1048576;
		b-=mb*1048576;
		kb=b/1024;
		b-=kb*1024;
		return (mb>0?mb+"мб ":"")+(kb>0?kb+"кб ":"")+b+"б";
	}
	
	private String[] parseMircColours(int startcol, String str) {
		String[] strs = null;
		Vector rets, retc;
		int colour = startcol;
		boolean iscolor=false;//открыт ли тег цвета

		rets = new Vector();
		retc = new Vector();

		boolean bURL=false;
		boolean eURL=false;
		boolean bUTF8=false;
		boolean eUTF8=false;
		
		for (int i=0; i<str.length(); i++) {
			char ch = str.charAt(i);
			if (ch == 2 || ch == 3 || ch == 22 || ch == 31 || ch == 15 ||ch==TextArea.cbURL||ch==TextArea.ceURL||ch==TextArea.cbUTF||ch==TextArea.ceUTF){
				if (!str.substring(0, i).equals("")) {
					rets.addElement((bURL?""+TextArea.cbURL:"")+(eURL?""+TextArea.ceURL:"")+(bUTF8?""+TextArea.cbUTF:"")+(eUTF8?""+TextArea.ceUTF:"")+str.substring(0, i));
					retc.addElement(new Character((char) colour));
					bURL=false;
					eURL=false;
					bUTF8=false;
					eUTF8=false;					
				}
				str = str.substring(i+1);
				i = -1;

				switch (ch) {
				case TextArea.cbURL:bURL=true;break;
				case TextArea.ceURL:eURL=true;break;
				case TextArea.cbUTF:bUTF8=true;break;
				case TextArea.ceUTF:eUTF8=true;break;
				case 2:  // BOLD
						colour ^= (Font.STYLE_BOLD << 8);
						break;
					case 31: //UNDERLINE
						colour ^= (Font.STYLE_UNDERLINED << 8);
						break;
					case 22: // REVERSE -> ITALIC
						colour ^= (Font.STYLE_ITALIC << 8);
//						int oldcol = colour;
//						colour &= ~0xff;
//						colour |= (oldcol&0x0f) << 4;
//						colour |= (oldcol&0xf0) >> 4;
						break;
						
					case 15:
						colour=startcol;
						iscolor=false;//тег цвета закрыт
						break;
					case 3:  // mIRC COLOUR
						String[] cols = new String[2];
//+++						
						int j=0;
						char c='D';//защита от пустой строки str
						c=(str.length()>j?str.charAt(j):'D');
						cols[0]=cols[1]="";
						if(Character.isDigit(c)){//Если первый символ - цифра
							cols[0]+=c;//запоминаем цифру как цвет текста
							c=(str.length()>++j?str.charAt(j):'D');//следующий символ
							if(Character.isDigit(c)){//второй символ - цифра
								cols[0]+=c;//дописываем к цвету текста
								c=(str.length()>++j?str.charAt(j):'D');//следующий символ
							}
							if(c==','){//если есть фон
								c=(str.length()>++j?str.charAt(j):'D');//следующий символ
								if(Character.isDigit(c)){//если цифра, то есть фон
									cols[1]+=c;//запоминаем цифру фона
									c=(str.length()>++j?str.charAt(j):'D');//следующий символ
									if(Character.isDigit(c)){cols[1]+=c;j++;}//если цифра - дописываем к цвету фона
								}
								else j--;
							}
						}						
						str=(str.length()>j?str.substring(j):"");
//---
						if (cols[0] == /*null*/"") {
							colour &= ~0xff;
							colour |= startcol&0xff;
							if(iscolor)iscolor=false;
						}
						else {
							if (cols[0] != /*null*/"") {
								colour &= ~0x0f;
								colour |= Integer.parseInt(cols[0])&0x0f;
							}
							if (cols[1] != /*null*/"") {
								colour &= ~0xf0;
								colour |= (Integer.parseInt(cols[1])&0x0f) << 4;
							}
						}
					break;
				}
			}
		}
		if (!str.equals("")) {
			if(!iscolor)colour ^= (8<<8);//помечаем закрытие битом

			rets.addElement((bURL?""+TextArea.cbURL:"")+(eURL?""+TextArea.ceURL:"")+(bUTF8?""+TextArea.cbUTF:"")+(eUTF8?""+TextArea.ceUTF:"")+str);
			retc.addElement(new Character((char) colour));
		}

		strs = new String[rets.size()];
		for (int i=0; i<strs.length; i++)
			strs[i] = ((Character) retc.elementAt(i)).charValue() + (String) rets.elementAt(i);

		return strs;
	}
	
}