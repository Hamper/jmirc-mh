package jmIrc;
///#define sounds
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

import java.util.Enumeration;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import DrawControls.TPropFont;

public class jmIrc extends MIDlet implements CommandListener {
	public final static String VERSION = "0.35c";
//#if SELF
//# 	public final static String MODVER  = "Hamper Mod (for self)";
//#else
	public final static String MODVER  = "Hamper Mod";
//#endif
	public static int DarkThemes=1;
	public final static String DefQuitMessage="jmIrc-m v"+VERSION+" by Archangel ("+MODVER+")";

	private final static int FORM_MAIN = 0;
	private final static int FORM_PROFILES = 1;
	private final static int FORM_CONFIG = 2;
	private final static int FORM_CONFIG_EDIT = 3;
	private final static int FORM_ADVANCED = 4;
	private final static int FORM_COMBINATIONS=5;
	
	
	public static Display display;
	public static Displayable lastDisplay;
	public static boolean minimized = false;
	
	protected static Form mainform;
	private int currentform;
	private boolean running;

	// these are initialized on connection time
	private static Listener listener; // irc listener
	public static SocketIrc irc;
        public static long ConnectTime;
	
	private static UIHandler uihandler;
	public static TPropFont bmFont = null; 
	public static TPropFont bmBFont = null;
	public static TPropFont bmpFont = null;

	private Command cmd_connect, cmd_profiles, cmd_advanced,cmd_combinations, cmd_exit;
	private Command cmd_profile_add, cmd_profile_edit, cmd_profile_delete;
	private Command cmd_ok, cmd_cancel,cmd_Default;

	private TextField TF_ProfileName,TF_Nick,TF_AltNick,TF_Server,TF_Port,TF_Perform,TF_UserName,TF_RealName;
	private TextField tf_hilight, tf_passwd, tf_buflines,tf_ReconnectTime,tf_ReconnectTry,tf_SnowsNum,tf_Addressed,tf_TimeMask,tf_QuitMessage,tf_Notify;
	private TextField tf_combinations[];
	private TextField tf_SocketPollTime,tf_VibroDuration;
        
        //#ifdef sounds
//#         private TextField tf_SoundVol;
        //#endif

	//private TextField tf_R, tf_G, tf_B;
//#if NOKIA
//# private TextField tf_OnBrg,tf_OffBrg;
//#endif
	private ChoiceGroup cg_misc,cg_Vibro,cg_Beep, cg_encoding,cg_menu;
	private ChoiceGroup cg_font,cg_findurls,cg_Theme,cg_Header,cg_AdvComb;

	private List list_profile;
	
	public static String MIDP;
	public static boolean MOTD=false;//для пропуска первого motd при коннекте
    public static boolean whois=false;//для корректного отображения необработанных Raw от whois
    public static boolean names=false;//для корректного обновления списка ников
	public static boolean Lag=false;
	public static int Reconnect=0;//кол-во попыток
	public static boolean Wait;
	public static String ServerError;
	public static boolean Exit=false;

	public static ResourcesUTF8 language;
	
	private Splash splash;
        //#ifdef sounds
//#         public static Media med;
        //#endif
	
	public jmIrc() {
		display = Display.getDisplay(this);
		splash=new Splash(Database.LoadProgressBar());
		display.setCurrent(splash);
		splash.Show();
	} 

	public void startApp() {
		if(minimized){
			display.setCurrent(lastDisplay);
			minimized=false;
		}
		else if(!running){
			MIDP=System.getProperty("microedition.profiles");
			if(MIDP==null)MIDP="1";
			language = new ResourcesUTF8("/language.dat");
			Database.load();		
			uihandler = new UIHandler(this);
                        //#ifdef sounds
//#                         med = new Media();
                        //#endif
			if(Database.FontSize== 4) {
				bmFont = new TPropFont("/font.prs");
				bmFont.setImage("/font.png");
				bmBFont = bmFont;
			}else 
			bmBFont = bmFont = null;
			cmd_connect = new Command(language.get("Connect"), Command.OK, 1);
			cmd_profiles = new Command(language.get("Profiles"), Command.SCREEN, 2);
			cmd_advanced = new Command(language.get("Options"), Command.SCREEN, 3);
			cmd_combinations= new Command(language.get("Combinations"), Command.SCREEN, 4);
			cmd_exit = new Command(language.get("Exit"), Command.EXIT, 10);

			cmd_profile_add = new Command(language.get("Add"), Command.SCREEN, 2);
			cmd_profile_edit = new Command(language.get("Edit"), Command.SCREEN, 3);
			cmd_profile_delete = new Command(language.get("Del"), Command.SCREEN, 4);

			cmd_ok = new Command(language.get("Ok"), Command.OK, 1);
			cmd_Default= new Command(language.get("Default"), Command.SCREEN,5);
			cmd_cancel = new Command(language.get("Cancel"), Command.EXIT, 10);
			
			mainform = new Form((Database.ProfileName!=null&&Database.ProfileName!=""?"["+Database.ProfileName+"]":"jmIrc-m"));
			mainform.append("jmIrc-m v"+VERSION+"\n");
			mainform.append("By Archangel\n");
			mainform.append("IRCLine.Ru (Dal.Net.Ru)\n");
			mainform.append("Modified by Hamper\n");
			mainform.append("http://jmirc-m.net.ru/\n");
/*			
			mainform.append("Тестовые сборки\n");
			mainform.append("предназначены только\n");
			mainform.append("для тестирования\n");
			mainform.append("новых возможностей!!!\n");
	/**/		
	 		//mainform.append("Special for Daemon");
			mainform.addCommand(cmd_connect);
			mainform.addCommand(cmd_profiles);
			mainform.addCommand(cmd_advanced);
			mainform.addCommand(cmd_combinations);
			mainform.addCommand(cmd_exit);
			mainform.setCommandListener(this);
			Database.SaveProgressBar(splash.Hide());
			splash=null;

			running = false;
			Wait=true;
			irc=new SocketIrc(Database.UsePoll);
			listener = new Listener(uihandler);
			listener.start();
			
			display.setCurrent(mainform);
			currentform = FORM_MAIN;
			running = true;
		}
	}

	public void commandAction(Command cmd, Displayable disp) {
		if (cmd == cmd_connect) {
			if(Database.Profileidx==-1||Database.ProfileName==null){
				Alert a=new Alert(language.get(Window.Warning),language.get("NotProfiles",""),null,AlertType.WARNING);
				a.setTimeout(Alert.FOREVER);
				display.setCurrent(a);
				return;
			}	
			
			uihandler.Console();
			uihandler.Server=Database.Server;
			uihandler.Port=Database.Port;
			uihandler.Pass=Database.ServerPass;
			uihandler.nick=Database.Nick;
			//#if DEBUGER
//# 		Window.EnDebuger=Database.EnDeb;
			//#endif
			Wait=false;
			System.gc();
		}
		else if (cmd == cmd_exit) {
			Wait=false;
			Exit=true;
			splash=new Splash(0);
			display.setCurrent(splash);
			splash.Show();
			try {
				destroyApp(true);
			} catch (MIDletStateChangeException msce) { ; } // this never happens
			notifyDestroyed();
		}
		else if ((cmd == cmd_ok || cmd == cmd_cancel || cmd == cmd_Default || cmd == List.SELECT_COMMAND) && currentform != FORM_MAIN) {
			if (currentform == FORM_PROFILES) {
				currentform = FORM_MAIN;

				Database.Profileidx = list_profile.getSelectedIndex();
				Database.save_profile();
				list_profile = null;

				Database.setProfile(Database.Profileidx);
				display.setCurrent(mainform);
				mainform.setTitle("["+Database.ProfileName+"]");
				
			}
			else if (currentform == FORM_CONFIG || currentform == FORM_CONFIG_EDIT) {
				boolean editing = (currentform == FORM_CONFIG_EDIT);

				if (cmd == cmd_ok) {
					
    				String s="";
    				if(TF_ProfileName.getString().equals(""))s+=", "+language.get("ProfileName");
    				if(TF_Nick.getString().equals(""))s+=", "+language.get("Nick");
    				if(TF_Server.getString().equals(""))s+=", "+language.get("Server");
    				if(TF_Port.getString().equals(""))s+=", "+language.get("Port");
    				if(TF_UserName.getString().equals(""))s+=", "+language.get("UserName");
    				if(TF_RealName.getString().equals(""))s+=", "+language.get("RealName");
    				if(!s.equals("")){
    					s=s.substring(2);
    					Alert a=new Alert(language.get(Window.Warning),language.get("ProfAddErr",s),null,AlertType.WARNING);
    					a.setTimeout(Alert.FOREVER);
    					display.setCurrent(a);
    					return;
    				}                       
					
					currentform = FORM_PROFILES;
					Database.ProfileName=TF_ProfileName.getString();
					Database.Nick=TF_Nick.getString();
					Database.AltNick=TF_AltNick.getString();
					Database.Server=TF_Server.getString();
					Database.Port=Utils.parseInt(TF_Port.getString());
					Database.Perform=TF_Perform.getString();
					Database.UserName=TF_UserName.getString();
					Database.RealName=TF_RealName.getString();
					Database.Encoding=cg_encoding.getString(cg_encoding.getSelectedIndex());
					if(!tf_passwd.getString().equals(language.get("HiddenPass")))Database.ServerPass=tf_passwd.getString();

					if (editing)
						Database.editProfile(list_profile.getSelectedIndex());
					else
						Database.addProfile();
				}
				currentform = FORM_PROFILES;
				TF_ProfileName=null;
				TF_Nick=null;
				TF_AltNick=null;
				TF_Server=null;
				TF_Port=null;
				TF_Perform=null;
				TF_UserName=null;
				TF_RealName= null;
				cg_encoding=null;
				tf_passwd = null;

				if (cmd == cmd_ok)
					commandAction(cmd_profiles, null);
				else
					display.setCurrent(list_profile);
			}
			else if (currentform == FORM_ADVANCED) {
				if (cmd == cmd_Default){
					Database.LoadDef();
					this.commandAction(cmd_advanced,null);
					return;
				}

				currentform = FORM_MAIN;
				if (cmd == cmd_ok) {
					int i;
					Database.TimeStamp=cg_misc.isSelected(i=0);
					Database.UseColor=cg_misc.isSelected(++i);
					Database.UseMircCol=cg_misc.isSelected(++i);
					Database.UsePoll=cg_misc.isSelected(++i);
					Database.ShowInput=cg_misc.isSelected(++i);
					Database.ShowAddress=cg_misc.isSelected(++i);
					Database.JoinOnKick=cg_misc.isSelected(++i);
					Database.Reconnect=cg_misc.isSelected(++i);
					Database.JoinReconnect=cg_misc.isSelected(++i);
					Database.NotifyOn=cg_misc.isSelected(++i);
					Database.ShowJoinPart=cg_misc.isSelected(++i);
					Database.utf8detect = cg_misc.isSelected(++i);
					Database.ShowMotd = cg_misc.isSelected(++i);
					Database.SortWind = cg_misc.isSelected(++i);
					Database.SoftReverse = cg_misc.isSelected(++i);
					Database.Lagometr = cg_misc.isSelected(++i);
					Database.DoubleBuf = cg_misc.isSelected(++i);
					//#if DEBUGER
//# 				Database.EnDeb = cg_misc.isSelected(++i);
					//#endif
					if(Utils.MIDP2()){//MIDP-2.0
						Database.FullScreen=cg_misc.isSelected(++i);
						Database.ShowSnows=cg_misc.isSelected(++i);
						Database.AnimateSnows=cg_misc.isSelected(++i);
						Database.VibroHighLight=cg_Vibro.isSelected(i=0); 
						Database.VibroQuery=cg_Vibro.isSelected(++i);
						Database.VibroDisscon=cg_Vibro.isSelected(++i);
						Database.VibroWatchOnline=cg_Vibro.isSelected(++i);
						Database.VibroWatchOffline=cg_Vibro.isSelected(++i);
						Database.VibroPrivmsg=cg_Vibro.isSelected(++i);
						Database.VibroNotice=cg_Vibro.isSelected(++i);
						Database.VibroDuration=Utils.parseInt(tf_VibroDuration.getString());
						if(Database.VibroDuration<100)Database.VibroDuration=100;
					}
					Database.BeepHighLight=cg_Beep.isSelected(i=0); 
					Database.BeepQuery=cg_Beep.isSelected(++i);
					Database.BeepDisscon=cg_Beep.isSelected(++i);
					Database.BeepWatchOnline=cg_Beep.isSelected(++i);
					Database.BeepWatchOffline=cg_Beep.isSelected(++i);
					Database.BeepPrivmsg=cg_Beep.isSelected(++i);
					Database.BeepNotice=cg_Beep.isSelected(++i);
                                        
                                        //#ifdef sounds
//#                                         Database.SoundVol=Utils.parseInt(tf_SoundVol.getString());
//#                                         if(Database.SoundVol>100)Database.SoundVol=100;
                                        //#endif

					Database.SocketPollTime=Utils.parseInt(tf_SocketPollTime.getString());
					if(Database.SocketPollTime<20)Database.SocketPollTime=20;
					Database.ReconnectTime=Utils.parseInt(tf_ReconnectTime.getString());
					if(Database.ReconnectTime<5)Database.ReconnectTime=5;
					Database.ReconnectTry=Utils.parseInt(tf_ReconnectTry.getString());
					if(Database.ReconnectTry<1)Database.ReconnectTime=1;
					Database.SnowsNum=Utils.parseInt(tf_SnowsNum.getString());
					if(Database.SnowsNum<0)Database.SnowsNum=0;
					
					Database.BufLines=Utils.parseInt(tf_buflines.getString());
					if(Database.BufLines<5)Database.BufLines=5;

					Database.HeaderUp=(cg_Header.getSelectedIndex()==0);
					
					Database.Theme=cg_Theme.getSelectedIndex();
					
					if(cg_font.getSelectedIndex()== 2)Database.FontSize=Font.SIZE_LARGE;
					else if(cg_font.getSelectedIndex()==1)Database.FontSize=Font.SIZE_MEDIUM;
					else if(cg_font.getSelectedIndex()==3)Database.FontSize=4;
					else Database.FontSize=Font.SIZE_SMALL;
					if(Database.FontSize== 4) {
					bmFont = new TPropFont("/font.prs");bmFont.setImage("/font.png");bmBFont = bmFont;
					}else bmBFont = bmFont = null;
					
					if(Utils.MIDP2())Database.FindURLs=cg_findurls.getSelectedIndex();//MIDP-2.0
					
					Database.Notify=tf_Notify.getString();
					Database.HighLight=tf_hilight.getString();
					Database.Addressed=tf_Addressed.getString();
					Database.TimeMask=tf_TimeMask.getString();
					Database.QuitMessage=tf_QuitMessage.getString();
					
					for(i=0;i<4;i++)if(cg_menu.isSelected(i))uihandler.ClearMenu(i+3);
					//Database.cl_R=Utils.parseInt(tf_R.getString());
					//Database.cl_G=Utils.parseInt(tf_G.getString());
					//Database.cl_B=Utils.parseInt(tf_B.getString());
					//#if NOKIA
//# 				//private TextField tf_OnBrg,tf_OffBrg;
//# 				Database.OnBrg=Utils.parseInt(tf_OnBrg.getString());
//# 				if(Database.OnBrg>100)Database.OnBrg=100;
//# 				Database.OffBrg=Utils.parseInt(tf_OffBrg.getString());
//# 				if(Database.OffBrg>100)Database.OnBrg=100;
					//#endif
					Database.save_advanced();
				}
				cg_misc=null;
				cg_font=null;
				cg_findurls=null;
				cg_Theme=null;
				cg_Header=null;
				tf_buflines=null;
				tf_hilight=null;
				tf_ReconnectTime=null;
				tf_ReconnectTry=null;
				tf_SnowsNum=null;
				tf_Notify=null;
				tf_Addressed=null;
				tf_TimeMask=null;
				tf_QuitMessage=null;
				tf_SocketPollTime=null;
				tf_VibroDuration=null;
                                //#ifdef sounds
//#                                 tf_SoundVol=null;
                                //#endif
				//#if NOKIA
//# 			tf_OnBrg=null;
//# 			tf_OffBrg=null;
				//#endif
				//tf_R=tf_G=tf_B=null;
				cg_menu=null;
				display.setCurrent(mainform);
			}
			else if(currentform==FORM_COMBINATIONS){
				if (cmd == cmd_Default){
					Database.LoadDefKeys();
					this.commandAction(cmd_combinations,null);
					return;
				}

				currentform = FORM_MAIN;
				if (cmd == cmd_ok) {
					for(int i=0;i<40;i++){
						if(i==15||i==24||i==26)continue;//*+5
						Database.Combinations[i]=tf_combinations[i].getString();
					}
					Database.AdvComb=cg_AdvComb.isSelected(0);
					Database.save_advanced();
				}
				tf_combinations=null;
				cg_AdvComb=null;
				display.setCurrent(mainform);
			}
		}
		else {
			Form cfgform;

			if (cmd == cmd_profiles) {
				String[] profiles;

				profiles = Database.getProfiles();
				list_profile = new List(language.get("Profiles"), List.IMPLICIT);
				for (int i=0; i<profiles.length; i++) {
					list_profile.append(profiles[i], null);
				}
				if (Database.Profileidx >= 0)
					list_profile.setSelectedIndex(Database.Profileidx, true);

				list_profile.addCommand(cmd_profile_add);
				list_profile.addCommand(cmd_profile_edit);
				list_profile.addCommand(cmd_profile_delete);
				list_profile.addCommand(cmd_ok);

				list_profile.setCommandListener(this);

				display.setCurrent(list_profile);
				currentform = FORM_PROFILES;
				return;
			}
			else if (cmd == cmd_profile_add || cmd == cmd_profile_edit) {
				if (cmd == cmd_profile_edit)
					Database.setProfile(list_profile.getSelectedIndex());
				else
					Database.setProfile(-1);

				cfgform = new Form(language.get("Profiles"));
				TF_ProfileName=new TextField(language.get("ProfileName"),Database.ProfileName,20,TextField.ANY);
				cfgform.append(TF_ProfileName);
				TF_Nick=new TextField(language.get("Nick"),Database.Nick,30,TextField.ANY);
				cfgform.append(TF_Nick);
				TF_AltNick=new TextField(language.get("AltNick"),Database.AltNick,30,TextField.ANY);
				cfgform.append(TF_AltNick);
				TF_Server=new TextField(language.get("Server"),Database.Server,200,TextField.URL);
				cfgform.append(TF_Server);
				TF_Port=new TextField(language.get("Port"),new Integer(Database.Port).toString(),5,TextField.NUMERIC);
				cfgform.append(TF_Port);
				TF_Perform=new TextField(language.get("Perform"),Database.Perform,1000,TextField.ANY);
				cfgform.append(TF_Perform);
				TF_UserName=new TextField(language.get("UserName"),Database.UserName,10,TextField.URL);
				cfgform.append(TF_UserName);
				TF_RealName=new TextField(language.get("RealName"),Database.RealName,50,TextField.ANY);
				cfgform.append(TF_RealName);
				
				cg_encoding = new ChoiceGroup(language.get("Encoding"), ChoiceGroup.EXCLUSIVE);
				cg_encoding.append("Windows-1251", null);
				cg_encoding.append("KOI8-R", null);
				cg_encoding.append("UTF-8", null);
				cg_encoding.append("ISO-8859-1", null);
				cg_encoding.append("ISO-8859-2", null);
				if(Database.Encoding.equals("Windows-1251"))cg_encoding.setSelectedIndex(0,true);
				else if(Database.Encoding.equals("KOI8-R"))cg_encoding.setSelectedIndex(1,true);
				else if(Database.Encoding.equals("UTF-8"))cg_encoding.setSelectedIndex(2,true);
				else if(Database.Encoding.equals("ISO-8859-1"))cg_encoding.setSelectedIndex(3,true);
				else if(Database.Encoding.equals("ISO-8859-2"))cg_encoding.setSelectedIndex(4,true);
				else cg_encoding.setSelectedIndex(0,true);
				cfgform.append(cg_encoding);
				tf_passwd = new TextField(language.get("ServerPass"),(Database.ServerPass.equals("")?"":language.get("HiddenPass")), 100, TextField.ANY);
				
				cfgform.append(tf_passwd);

				if (cmd == cmd_profile_edit)
					currentform = FORM_CONFIG_EDIT;
				else
					currentform = FORM_CONFIG;
			}
			else if (cmd == cmd_profile_delete) {
				Database.deleteProfile(list_profile.getSelectedIndex());
				commandAction(cmd_profiles, null);
				return;
			}
			else if (cmd == cmd_advanced) {
				cfgform = new Form(language.get("Options"));
				int i;
				cg_misc = new ChoiceGroup(language.get("Options"), ChoiceGroup.MULTIPLE);
				cg_misc.append(language.get("ShowTime"),null);
				cg_misc.setSelectedIndex(i=0,Database.TimeStamp);
				cg_misc.append(language.get("UseColor"),null);
				cg_misc.setSelectedIndex(++i,Database.UseColor);
				cg_misc.append(language.get("UseMircColor"),null);
				cg_misc.setSelectedIndex(++i,Database.UseMircCol);
				cg_misc.append(language.get("CheckConnect"), null);
				cg_misc.setSelectedIndex(++i,Database.UsePoll);
				cg_misc.append(language.get("ShowAll"), null);
				cg_misc.setSelectedIndex(++i,Database.ShowInput);
				cg_misc.append(language.get("ShowAddress"),null);
				cg_misc.setSelectedIndex(++i,Database.ShowAddress);
				cg_misc.append(language.get("JoinOnKick"),null);
				cg_misc.setSelectedIndex(++i,Database.JoinOnKick);
				cg_misc.append(language.get("Reconnect"), null);
				cg_misc.setSelectedIndex(++i,Database.Reconnect);
				cg_misc.append(language.get("JoinReconnect"), null);
				cg_misc.setSelectedIndex(++i,Database.JoinReconnect);
				cg_misc.append(language.get("Notify"), null);//-
				cg_misc.setSelectedIndex(++i,Database.NotifyOn);
				cg_misc.append(language.get("ShowJoinPart"), null);
				cg_misc.setSelectedIndex(++i,Database.ShowJoinPart);
				cg_misc.append(language.get("UTF8Detect"), null);
				cg_misc.setSelectedIndex(++i,Database.utf8detect);
				cg_misc.append(language.get("ShowMotd"), null);
				cg_misc.setSelectedIndex(++i,Database.ShowMotd);
				cg_misc.append(language.get("SortWind"), null);//-
				cg_misc.setSelectedIndex(++i,Database.SortWind);
				cg_misc.append(language.get("SoftReverse"), null);
				cg_misc.setSelectedIndex(++i,Database.SoftReverse);
				cg_misc.append(language.get("Lagometr"), null);
				cg_misc.setSelectedIndex(++i,Database.Lagometr);
				cg_misc.append(language.get("DoubleBuf"), null);
				cg_misc.setSelectedIndex(++i,Database.DoubleBuf);
				
				if(Utils.MIDP2()){
					cg_misc.append(language.get("FullScreen"), null);//-
					cg_misc.setSelectedIndex(++i,Database.FullScreen);
					cg_misc.append(language.get("ShowSnows"), null);//-
					cg_misc.setSelectedIndex(++i,Database.ShowSnows);
					cg_misc.append(language.get("AnimateSnows"), null);//-
					cg_misc.setSelectedIndex(++i,Database.AnimateSnows);
				}
				//#if DEBUGER
//# 			cg_misc.append("Отладчик", null);
//# 			cg_misc.setSelectedIndex(++i,Database.EnDeb);
				//#endif
				cfgform.append(cg_misc);

				if(Utils.MIDP2()){
					cg_Vibro = new ChoiceGroup(language.get("Vibro"),ChoiceGroup.MULTIPLE);
					cg_Vibro.append(language.get("HightLights"),null);
					cg_Vibro.setSelectedIndex(i=0,Database.VibroHighLight);
					cg_Vibro.append(language.get("Querys"),null);
					cg_Vibro.setSelectedIndex(++i,Database.VibroQuery);
					cg_Vibro.append(language.get("Disscon"),null);
					cg_Vibro.setSelectedIndex(++i,Database.VibroDisscon);
					cg_Vibro.append(language.get("WatchOnline"),null);
					cg_Vibro.setSelectedIndex(++i,Database.VibroWatchOnline);
					cg_Vibro.append(language.get("WatchOffline"),null);
					cg_Vibro.setSelectedIndex(++i,Database.VibroWatchOffline);
					cg_Vibro.append(language.get("Privmsgs"),null);
					cg_Vibro.setSelectedIndex(++i,Database.VibroPrivmsg);
					cg_Vibro.append(language.get("Notices"),null);
					cg_Vibro.setSelectedIndex(++i,Database.VibroNotice);
					cfgform.append(cg_Vibro);
					tf_VibroDuration=new TextField(language.get("VibroDuration"), new Integer(Database.VibroDuration).toString(),4,TextField.NUMERIC);
					cfgform.append(tf_VibroDuration);
				}	

				cg_Beep = new ChoiceGroup(language.get("Beep"),ChoiceGroup.MULTIPLE);
				cg_Beep.append(language.get("HightLights"),null);
				cg_Beep.setSelectedIndex(i=0,Database.BeepHighLight);
				cg_Beep.append(language.get("Querys"),null);
				cg_Beep.setSelectedIndex(++i,Database.BeepQuery);
				cg_Beep.append(language.get("Disscon"),null);
				cg_Beep.setSelectedIndex(++i,Database.BeepDisscon);
				cg_Beep.append(language.get("WatchOnline"),null);
				cg_Beep.setSelectedIndex(++i,Database.BeepWatchOnline);
				cg_Beep.append(language.get("WatchOffline"),null);
				cg_Beep.setSelectedIndex(++i,Database.BeepWatchOffline);
				cg_Beep.append(language.get("Privmsgs"),null);
				cg_Beep.setSelectedIndex(++i,Database.BeepPrivmsg);
				cg_Beep.append(language.get("Notices"),null);
				cg_Beep.setSelectedIndex(++i,Database.BeepNotice);
				cfgform.append(cg_Beep);
				//#ifdef sounds
//#                                 tf_SoundVol=new TextField(language.get("SoundVol"), new Integer(Database.SoundVol).toString(),3,TextField.NUMERIC);
//# 				cfgform.append(tf_SoundVol);
				//#endif
				tf_SocketPollTime=new TextField(language.get("CheckConnectTime"), new Integer(Database.SocketPollTime).toString(),3,TextField.NUMERIC);
				cfgform.append(tf_SocketPollTime);
				tf_ReconnectTime=new TextField(language.get("ReconnectTime"), new Integer(Database.ReconnectTime).toString(),3,TextField.NUMERIC);
				cfgform.append(tf_ReconnectTime);
				tf_ReconnectTry=new TextField(language.get("ReconnectTry"), new Integer(Database.ReconnectTry).toString(),2,TextField.NUMERIC);
				cfgform.append(tf_ReconnectTry);
				tf_buflines=new TextField(language.get("BufferLen"), new Integer(Database.BufLines).toString(),3,TextField.NUMERIC);
				cfgform.append(tf_buflines);//-
				tf_SnowsNum=new TextField(language.get("SnowsNum"), new Integer(Database.SnowsNum).toString(),3,TextField.NUMERIC);
				cfgform.append(tf_SnowsNum);
				
				cg_Header=new ChoiceGroup(language.get("Header"),ChoiceGroup.EXCLUSIVE);
				cg_Header.append(language.get("HUp"),null);
				cg_Header.append(language.get("HDown"),null);
				cg_Header.setSelectedIndex((Database.HeaderUp?0:1),true);
				cfgform.append(cg_Header);//-
				
				cg_Theme=new ChoiceGroup(language.get("Theme"),ChoiceGroup.EXCLUSIVE);
				cg_Theme.append(language.get("WhiteTheme"),null);
				cg_Theme.append(language.get("DarkTheme"),null);
				cg_Theme.setSelectedIndex(Database.Theme,true);
				cfgform.append(cg_Theme);
				
				cg_font=new ChoiceGroup(language.get("FontSize"),ChoiceGroup.EXCLUSIVE);
				cg_font.append(language.get("SmallFont"),null);
				cg_font.append(language.get("MediumFont"),null);
				cg_font.append(language.get("LargeFont"),null);
				cg_font.append(language.get("GraphFont"), null);
				if(Database.FontSize==Font.SIZE_LARGE)cg_font.setSelectedIndex(2,true);
				else if(Database.FontSize==Font.SIZE_MEDIUM)cg_font.setSelectedIndex(1,true);
				else if(Database.FontSize==4)cg_font.setSelectedIndex(3,true);
				else cg_font.setSelectedIndex(0,true);//-
				
				cfgform.append(cg_font);
				
				if(Utils.MIDP2()){
					cg_findurls=new ChoiceGroup(language.get("FindURLs"),ChoiceGroup.EXCLUSIVE);
					cg_findurls.append(language.get("OffURLs"),null);
					cg_findurls.append(language.get("ActiveURLs"),null);
					cg_findurls.append(language.get("AllURLs"),null);
					cg_findurls.setSelectedIndex(Database.FindURLs,true);
					cfgform.append(cg_findurls);
				}
				
				tf_Notify = new TextField(language.get("NotifyList"), Database.Notify, 500, TextField.ANY);
				cfgform.append(tf_Notify);//-
				tf_hilight = new TextField(language.get("HighLight"), Database.HighLight, 500, TextField.ANY);
				cfgform.append(tf_hilight);
				tf_Addressed = new TextField(language.get("AddressedMask"), Database.Addressed, 100, TextField.ANY);
				cfgform.append(tf_Addressed);
				tf_TimeMask = new TextField(language.get("TimeMask"),Database.TimeMask,50,TextField.ANY);
				cfgform.append(tf_TimeMask);//-
				tf_QuitMessage = new TextField(language.get("QuitMessage"), Database.QuitMessage, 307, TextField.ANY);
				cfgform.append(tf_QuitMessage);
				
				cg_menu = new ChoiceGroup(language.get("DefaultMenu"), ChoiceGroup.MULTIPLE);
				cg_menu.append(language.get("DefMenuSt"),null);
				cg_menu.setSelectedIndex(0,false);
				cg_menu.append(language.get("DefMenuCh"),null);
				cg_menu.setSelectedIndex(1,false);
				cg_menu.append(language.get("DefMenuNL"),null);
				cg_menu.setSelectedIndex(2,false);
				cg_menu.append(language.get("DefMenuPr"), null);
				cg_menu.setSelectedIndex(3,false);
				cfgform.append(cg_menu);//-
				/*tf_R = new TextField("R", new Integer(Database.cl_R).toString(),3,TextField.NUMERIC);
				tf_G = new TextField("G", new Integer(Database.cl_G).toString(),3,TextField.NUMERIC);
				tf_B = new TextField("B", new Integer(Database.cl_B).toString(),3,TextField.NUMERIC);
				cfgform.append(tf_R);
				cfgform.append(tf_G);
				cfgform.append(tf_B);
				*/
				//#if NOKIA
//# 			tf_OnBrg = new TextField(language.get("OnBrg"),new Integer(Database.OnBrg).toString(), 3, TextField.NUMERIC);
//# 			cfgform.append(tf_OnBrg);
//# 			tf_OffBrg = new TextField(language.get("OffBrg"),new Integer(Database.OffBrg).toString(), 3, TextField.NUMERIC);
//# 			cfgform.append(tf_OffBrg);
				//#endif
				currentform = FORM_ADVANCED;
			}
			else if(cmd==cmd_combinations){
				cfgform = new Form(language.get("Combinations"));
				tf_combinations=new TextField[40];
				for(int i=0;i<40;i++){
					if(i==15||i==24||i==26)continue;//*+5, #+4, #+6
					if(i==30){//Настройка режима дополнительных клавиш
						cg_AdvComb = new ChoiceGroup(null,ChoiceGroup.MULTIPLE);
						cg_AdvComb.append(language.get("AdvComb"),null);
						cg_AdvComb.setSelectedIndex(0,Database.AdvComb);
						cfgform.append(cg_AdvComb);
					}
					tf_combinations[i]=new TextField((i>=10&&i<=19?"*+"+(i-10):(i>=20&&i<=29?"#+"+(i-20):(i>=30?"*+#+"+(i-30):""+i))),Database.Combinations[i],1000,TextField.ANY);
					cfgform.append(tf_combinations[i]);
				}
				currentform = FORM_COMBINATIONS;
			}
			else return;

			cfgform.addCommand(cmd_ok);
			if(currentform==FORM_ADVANCED||currentform==FORM_COMBINATIONS)cfgform.addCommand(cmd_Default);
			cfgform.addCommand(cmd_cancel);
			cfgform.setCommandListener(this);

			display.setCurrent(cfgform);
		}
	}
	
	/**
	 * Time to pause, free any space we don't need right now.
	 */
	public void pauseApp() {
	}

	/**
	 * Destroy must cleanup everything.
	 */
	public void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		if (irc != null && irc.isConnected()) {
			if (!unconditional)
				throw new MIDletStateChangeException("IRC is still connected");
			Wait=false;
			Exit=true;
			jmIrc.disconnect(null,QuitMessage());
		}
		if(uihandler!=null&&unconditional)uihandler.SaveMenus();
		if(splash!=null)splash.Hide();

		//if (uihandler != null) uihandler.cleanup();
	}

	public static String QuitMessage(){
		return (!Database.QuitMessage.equals("")?Utils.CodeToChars(Database.QuitMessage):DefQuitMessage+" (http://jmirc-m.net.ru/)");
	}

	public void OpenURL(String URL){
		try {platformRequest(URL);} 
		catch (Exception e) {e.printStackTrace();}
	}		
	
	public synchronized static void SendIRC(String Message) {
		if (irc.isConnected()){
		ServerError=irc.writeData(Message+"\r\n");
			//#if DEBUGER
//# 		if (Window.EnDebuger&&Message!=null&&!Message.trim().equals(""))
//# 		uihandler.GetChannel("!RAW").AddInfo("-> "+Message+"\r\n");
			//#endif
		}
        }
	public static String Connect(String Server,int Port,String Pass){
                ConnectTime = System.currentTimeMillis();
            
		String s="";
		if(!Utils.hasNoValue(Pass))s+="PASS "+Pass+"\r\n";
		s+=Window.NICK+" "+uihandler.nick+"\r\n";
		s+="USER "+Database.UserName+ " * * :"+Utils.CodeToChars(Database.RealName)+"\r\n";
		uihandler.console.AddInfo(jmIrc.language.get("TryConnect",Server+" "+Port));
		return irc.connect(Server,Port,s);
	}
	
	public static void disconnect(String Error,String Quit){
		if (irc!=null&&irc.isConnected()&&Quit!=null) {
			SendIRC("QUIT :"+Quit);
			irc.disconnect();
		}
		
		String s=jmIrc.language.get("Disconnected",uihandler.Server+(Error!=null?" "+Error:""));
		if(uihandler.console!=null)uihandler.console.AddInfo(s);
		Window Win;
		for(Enumeration en=uihandler.Channels.elements();en.hasMoreElements();){
			Win=((Window)en.nextElement());
			if(listener.Connected)Win.AddInfo(s);
			Win.ClearNames();
		}
		for(Enumeration en=uihandler.Privates.elements();en.hasMoreElements();){
			Win=((Window)en.nextElement());
			if(listener.Connected)Win.AddInfo(s);
		}

		listener.Connected=false;
		MOTD=false;
		names=false;
		whois=false;
		Lag=false;
	}

	public static boolean isConnected(){
		return irc.isConnected();
	}

	
	public static int getBytesIn() {
		return irc.getBytesIn();
	}

	public static int getBytesOut() {
		return irc.getBytesOut();
	}
	
	
}


/*
Настройка захода на канал изменена на автовыполнение (разделителем команд является
	символ ';',	пример: /ns id ник пароль;/join #канал1,#канал2)
В заголовке главной формы показывается имя загруженного профиля, если он существует
При попытке соедениться при отсутствии профилей выводится сообщение об ошибке
Окна каналов сортируются по названию. Окна приватов не сортируются.
Информация о whois обрабатывается более корректно
Корректно обновляется список ников канала по команде /names #канал
Ник установившего топик показывается с адресом, если он существует 
Кроме ника, установившего топик, отображается относительное время его установки
После кика окно канала не закрывается, его необходимо закрыть вручную
После закрытия канала, окно канала не закрывается до прихода подтверждения о выходе с канала
При разрыве связи окна каналов и приватов не закрываются, информация об отключении выводится во все окна
Счётчик трафика показывает мегабайты, килобайты и байты
Меню whois делает remote whois (whois ник ник), а не local whois (whois ник)
Добавлена возможность ввода произвольных команд
Добавлена возможность писать несколько сообщений сразу на нескольких строчках
Добавлена настройка показа адресов, quit сообщения, переподключения при разрыве связи, 
	списка уведомления, перезахода на открытые каналы после пересоединения 
Добавлена команда /perform для принудительного выполнения команд из автовыполнения, 
	/query для открытия окна привата
Изменена проверка важных параметров при добавлении и редактировании профиля
Добавлено меню для Kick и Kick+Ban с причиной
При выборе меню Kick+Ban бан ставится по хосту, а не по нику, хост берётся из ответа userhost
Добавлено меню Игноры в списке ников, используется серверная команда SILENCE,
	игноры сохраняются во внутреннем списке и выставляются каждый раз при входе в сеть,
	временные игноры (в списке помечены символом '¤') не выставляются, 
	любой игнор, поставленный вручную командой /silence является временным. 
Добавлены слапы
Добавлено меню CTCP, Обращение, Нотис в списке ников
(?)Добавлена настройка логина HTTP прокси
Добавлена обработка смены usermodes
Добавлен просмотр и редактирование топика и списка банов
Добавлена обработка символа с кодом 15 (ctrl+o), закрывающим все теги контрольных кодов
Реверс цвета и фона (ctrl+r) заменён на наклонный шрифт.
Во whois кроме времени молчания показывается относительное время подключения.
Добавлена поддержка контрольных кодов в меню нотис, кик и кик\бан, редактирование топика, в настройки Сообщение при выходе и Реальное имя
Добавлено выделение текста и его копирование во внутренний буфер обмена
Добавлена вставка текста из внутреннего буфера обмена при написании текста
Исправлен баг в отображении оранжевого цвета (отображался зелёный)
Исправлен баг в обработке цвета цифр (цветные цифры "съедались")
Добавлено цитирование текста
Добавлена настройка шаблона времени, используются идентификаторы s - секунды, m - минуты, h - часы, а так же идентификаторы цветов и стилей 
Добавлена команда /J
Команды в автовыполнении проходят через обработчик команд
Чтобы сказать текст, начинающийся с '/' необходимо поставить "//" в начале
Добавлено копирование текста с цветами
0.30
Добавлен идентификатор тега, закрывающий все теги контрольных кодов %o% (символ 15)
Чтобы идентификаторы контрольных кодов не заменялись на коды, необходимо перед ними поставить символ '\', например "\%с%"
Добавлено окно с выбором цветов
Исправлена ошибка при загрузке профилей при смене версии программы
Добавлено меню "Действие" при написании текста
Исправлена ошибка в списке уведомления 
Исправлена ошибка при скроллинге вниз
Добавлено автопролистывание текста при удержании клавиши вверх (2) и вниз (8)
Добавлена настройка для полноэкранного режима (midp2)
Добавлено меню "Сменить ник"
0.31
Добавлена поддержка контрольных кодов в избранное
Исправлена ошибка с закрытием тега цвета при копировании с цветом
Изменён алгоритм вставки скопированного текста в связи с некорректной работой на некоторых телефонах, текст всегда вставляется в конец независимо от положения курсора в тексте
Увеличина скорость автопролистывания до 5-ти строк в секунду
Добавлена настройка для отключения показа входов\выходов пользователей на канале
В окно смены ника добавлено меню "Отмена"
Добавлено меню "Очистить"
Добавлено меню "Добавить к скопированному" и "Добавить с цветом"
Изменено окно выбора цвета. Добавлено меню выбора стилей: подчёркивание, курсив, жирный текст
Исправлена ошибка при загрузке профилей при смене версии программы (профили ранних версий не поддерживаются)
Добавлена поддержка выбора цвета стилусом в окне выбора цвета
Добавлено редактирование слапов
Исправлена ошибка при добавлении слапов
Исправлена недоработка при посылке пустого текста
0.31a
Исправлена ошибка при загрузке и сохранении настроек
Исправлена ошибка при обработке кодов курсива и подчёркивания
0.31b
Исправлена ошибка при сохранении настроек
Исправлена ошибка в работе опции "Показывать входы\выходы"

0.32
Переделано Избранное, добавлено редактирование, добавлено избранное при написании текста
Переделана навигация меню
Исправлена работа оповещений (HighLight), идентификатор "%me%" возвращает текущий ник
Исправлены маленькие недоработки при смене ника
Исправлена недоработка при прорисовке фона активного текста
Исправлены недоработки в обработке идентификаторов %chan%,%nick%,%ident%,%host%,%address%
Исправлена недоработка в автовыполнении (пробелы до и после команды теперь вырезаются)
Исправлена ошибка обновления индикаторов окон
Исправлена ошибка в обработке ответа /USERHOST
Исправлена ошибка в работе опции "Показывать все сообщения сервера"
Исправлена ошибка при сохранении профилей
Исправлена ошибка при скроллинге вниз
Исправлена ошибка при отображении и копировании слов, превышающих ширину экрана  
Исправлена ошибка в копировании текста, содержащего идентификаторы кодов
Исправлена ошибка при открытии привата
Исправлена ошибка подсчёта трафика при переподключении (счётчики теперь не сбрасываются)
Исправлена недоработка настройки "Перезаходить на каналы после пересоединения"
Добавлена поддержка автоопределения UTF-8 (из jmIrc v0.95)
Добавлена поддержка текстовых ресурсов из файла (из InetTools). 
Добавлена работа в фоновом режиме (из InetTools)
Добавлена возможность форматировать сообщения, используя шаблоны из текстовых ресурсов
Добавлены идентификаторы "%n%"(переход на следующую строку) и "%s%"(пробел)
Добавлена обработка контрольных кодов в автовыполнение
Добавлено меню "Добавить бан"
Добавлены маски банов "nick!*@*", "*!user@*", "*!user@*.host", "*!*@*.host"
Добавлен индикатор занимаемой памяти
Добавлена настройка выбора размера шрифта
Добавлено меню Информация о бане
Добавлена настройка пропуска /motd при коннекте
Добавлена возможность переключать окна и прокручивать\пролистывать текст стилусом; добавлены метки для удобства пролистывания и переключения (включаются при первом использовании); переключать окна можно тыкая на индикаторе окна
Добавлены настройки вибрации при: оповещениях (HighLight), открытии привата, уведомлениях, сообщениях в привате, нотисах, разрыве связи
Добавлена команда /q, аналогичная /query
Добавлена команда /CTCP, удалены команды /PING, /VERSION 
Добавлен псевдоалиас %zebra(текст,n1,n2)% (чисто для развлечения для слапов), возвращающий текст, в котором цвета текста и фона n1 и n2 меняются местами, 0<=n1,n2<=15 - номер цвета текста/фона; (P.S. пользователи версий <0.32 добавьте слап: "покрасил %zebra(%nick%,00,01)% из баллончика под зебру") 
Доработана система индикаторов окон, индикаторы приватов отодвинуты от индикаторов каналов для наглядности, размер индикаторов теперь пропорционален размеру шрифта
Доработаны настройки "Использовать цвета" и "Использовать mIRC цвета"
Команды в автовыполнении теперь разделяются символом ';', старый разделитель "%n%" автоматически преобразуется в новый
При цитировании текста позиция активного текста принудительно перемещается вниз
0.33
Исправлены ошибки при закрытии избранного
Исправлена прорисовка линий для стилуса, добавлена возможность вызывать список ников и закрывать окно
Исправлена ошибка при выборе цвета стилусом, выбор осуществляется двойным нажатием
Исправлена ошибка при выборе первого бана из списка
Исправлена ошибка при использовании меню "Дополнительно"
Исправлена ошибка прокрутки текста при блокировке клавиатуры
Исправлена ошибка при копировании текста после очистки окна и при копировании длинных слов
Исправлена ошибка при редактирования избранного и слапов
Исправлена ошибка в вибрации при уведомлениях
Исправлена ошибка при обработке пустого сообщения от пользователей
Исправлена ошибка алиаса %zebra% при обработке текста с цифрами
Исправлена ошибка использования альтернативного ника при занятом нике
Исправлена ошибка залипания автопрокрутки при смене активного окна
Исправлена ошибка обработки кодов цвета и стилей при комбинации с буквами идентификаторов (например %b%c%b%ool - <b>c</b>ool)
Меню "Слапы" переименовано в "Действия"
При выборе меню "Смена ника" в окно вода текста вставляется текущий ник
Функция блокировки клавиатуры изменена с комбинации "5+#" на "*+5", функция вызова текста перенесена на кнопку "FIRE" и "5"
Переделана цветовая палитра, внесены изменения в окно выбора цвета, изменены цвета индикаторов окон и цвет заголовка
При смене своего ника информация выводится в окно статуса
Доработана функция вставки текста, если телефон возвращает позицию курсора, то текст будет вставляться в текущую позицию, если нет или позиция равна 0, то в конец
Доработана система оповещений (HighLight), проверка теперь не учитывает ник того, кто пишет текст
Добавлена возможность использования многострочного избранного и многострочных действий
Добавлена поддержка произвольных префиксов ников (информация берётся из raw 005, по умолчанию "(ohv)@%+"), меню Op/Deop использует эту информацию (режимы в меню можно заменить названиями в текстовых ресурсах)
Добавлены настройки сортировки окон каналов по названию, выбора темы, количества попыток пересоединения, положения заголовка (вверху или внизу)
Добавлены меню "Игноры", "CTCP" и "Действия" в окно привата
Добавлено меню "Вставить ник" при написании текста
Добавлены меню "Скопировать цитату", "Переподключиться"
Добавлено редактирование списка исключений (Excepts)
Добавлено меню управления режимами(модами) канала (информация берётся из raw 005, по умолчанию "k,l,psmnti") (режимы в меню можно заменить названиями в текстовых ресурсах)
Добавлены комбинации клавиш "*+1" - копирование текста, "*+2" - добавить к скопированому, "*+3" - фоновый режим, "*+7" - вызов списка ников, "*+0" - вызов избранного
Добавлена команда /SERVER имя_сервера порт пароль (или /S), для ручного переподключения к произвольному серверу с сохранением остальных настроек (включая автовыполнение), 
	порт и пароль не обязательны. Ввод команды без указания сервера (а так же порта и пароля) подключит к серверу из текущего профиля с параметрами профиля.
Добавлена команды /HOP для перезахода на канал без закрытия окна канала, /CLEAR для очистки экрана командой
Добавлено меню "Перезайти" в меню команд канала
Алиас %zebra% добавлен в избранное, начальный цвет теперь определяется длиной ника (слова) 
Добавлена функция подсветки и открытия ссылок во внешнем браузере (MIDP2), добавлены настройки подсветки ссылок (Не подсвечивать, подсвечивать активные, подсвечивать все)
0.34
Исправлена недоработка при ожидании переподключения<br>
Исправлена недоработка при использовании команды <b>/CTCP Ник ping</b><br>
Исправлена недоработка расчёта высоты окон<br>
Исправлена недоработка в команде <b>/MSGБ</b><br>
Исправлена недоработка закрытия тега цвета<br>
Исправлена ошибка перевода символа подчёркивания в идентификатор <i>%u%</i> при редактировании топика<br>
Исправлена ошибка в команде <b>/SERVER</b> (показывалось сообщение об отключении от сервера, указанного в команде, вместо текущего сервера)<br>
Исправлена ошибка с подключением после окночания числа попыток переподключиться<br>
Исправлена ошибка использования клавиш для комбинаций после использования комбинаций клавиш<br>
Исправлена ошибка обработки снятия режимов канала с параметрами.<br>
Исправлена ошибка при обновлении установленного режима с параметром.<br>
Исправлена ошибка в настройке <i>"Показывать все сообщения сервера"</i> для не номерных не обрабатываемых сообщений<br>
Исправлена ошибка при смене настройки полноэкранного режима<br>
Исправлена ошибка парсинга сообщений некоторых серверов, приводящая к некорректной работе приложения<br>
Исправлена ошибка обработки сообщений сервера, в адресе пользователей которого содержится символ ':' (IPv6)<br>
Исправлена ошибка закрытия меню вставки ника при добавлени и редактировании Действий, банов, исключений<br>
Удалена предобработка команды <b>/TOPIC</b><br>
Удалена настройка <i>"Всегда писать в UTF8"</i><br>
При копировании через комбинации клавиш сообщение о том, что текст скопирован выводиться не будет.<br>
Переделана система ведения списка игноров, теперь список всегда запрашивается с сервера.<br>
Меню <i>"CTCP"</i> и <i>"Информация о нике"</i> в привате перемещены в меню <i>"Команды"</i>.<br>
Начальный список действий теперь можно вписывать в текстовые ресурсы. При удалении всех действий, список вновь считается из текстовых ресурсов после корректного перезапуска программы.<br>
При добавлении к скопированному, текст добавляется на следующую строку (<i>%n%</i> - идентификатор перехода на следующую строку), а не дописывается в конец предыдущей.<br>
Информация о смене ника и выходе из сети будет выводиться в окно привата с ником, если оно открыто.<br>
Все изменения в Избранном и Действиях сохраняются только при удачном выходе из программы (меню <i>"Выход"</i> в главном меню)<br> 
Нотисы и CTCP, посылаемые на канал, будут отображаться в окне канала, а не в статусе<br>
Настройка выбора Кодировки перенесена из общих настроек в профили<br>
Доработана система оповещений (HighLight), в начале строки будет символ '¤' красного цвета (символ и цвета устанавливаются в текстовых ресурсах language.dat)<br>
Доработана система автоопределения UTF8, теперь только текст в активной строке будет переводиться в UTF8, а не весь входящий.<br>
Добавлено меню <i>"Команды"</i> в список ников. Меню <i>"CTCP"</i>, <i>"Информация о нике"</i>, <i>"Нотис"</i> и <i>"Открыть приват"</i> в списке ников перемещены в меню <i>"Команды"</i>.<br>
Добавлено меню <i>"Дополнительно"</i> (<i>"Трафик"</i>, <i>"Фоновый режим"</i>, <i>"Ссылки"</i>) в меню привата.<br>
Добавлены маски игноров <i>"*!user@*", "*!user@*.host", "*!*@*.host"</i>.<br>
Добавлен идентификатор <i>%me%</i> в Действия<br>
Добавлена поддержка отображения WALLOPS и INVITE сообщений<br>
Добавлена возможность прокручивать текст стилусом по скролбару<br>
Добавлены команды <b>/COPY, /FAVOURITES, /ACTIONS, /TRAF, /BACKGROUND, /OPENURL, /SETTOPIC, /SETMODES, /BANLIST, /EXCEPTLIST, /BAN, /KBAN, /IGNORE, /TEMPIGNORE, /IGNORESLIST, /OPDEOP, /MESSAGE, /ADDRESSED, /DESCRIBE, /NAMESLIST, /CLOSE, /EXIT, /CLOCK</b>. Для получения детальной информации читайте <a href="commands.html">тут</a>.<br>
Добавлены настройки системного сигнала при: оповещениях (HighLight), открытии привата, уведомлениях, сообщениях в привате, нотисах, разрыве связи<br>
Добавлена возможность добавлять/изменять/удалять меню и подменю в меню в статуса, канала, списка ников и привата. Для получения детальной информации читайте <a href="menu.html">тут</a>. Любые изменения в меню сохраняются только при удачном выходе из программы (меню <i>"Выход"</i> в главном меню)<br>
Добавлен индикатор загрузки при старте программы<br>
Добавлена настройка инвертирования софт клавиш<br>
Добавлено меню сброса настроек к значениям по умолчанию.<br>
Добавлен индикатор скопированного текста (конвертик), показываемый в шапке при наличии в буфере скопированного текста, а так же второй конвертик, показывающийся сразу после копирования текста<br>
Добавлена настройка сброса редактируемого меню статуса, канала, списка ников, привата в первоначальное состояние (описанное в language.dat). После включения настройки необходимо корректно выйти из программы через меню <i>"Выход"</i> и повторно запустить программу.<br>
Добавлено меню выбора стилей при написании текста. В меню перенесены стили <i>%b%</i> - жирный, <i>%u%</i> - подчёркивание, <i>%i%</i> - курсив, а так же добавлены идентификаторы <i>%o%</i> - закрытие всех тегов стилей и цвета, <i>%n%</i> - перенос строки<br>
Добавлены комбинации: <i>#+1</i> - копирование с цветом, <i>#+2</i> - добавить с цветом, <i>#+3</i> - цитирование, <i>#+9</i> включение\отключение показа часов, <i>*+6</i> - очистка экрана<br>

0.35
Исправлена ошибка использования настройки "Время опроса сокета"
Исправлена недоработка установки\снятия режимов канала при наличии установленных режимов с параметрами.
Исправлена ошибка закрытия избранного и меню стилей при вводе текста в %text%
Исправлена ошибка вставки избранного в окно ввода текста %text%
Исправлена недоработка в команде /DESCRIBE
Исправлена ошибка прорисовки привата, открытого командой /QUERY ник
Исправлена ошибка в редактируемых меню при использовании в идентификаторе %nick% ника "|";
Исправлена ошибка при просмотре списка банов и исключений для серверов, которые не выдают информацию о дате установки бана/исключения и адресе установившего
Исправлена ошибка при перемещении меню в подменю вверх/вниз.
Исправлена ошибка с тегом %n% в избранном, действиях и копировании текста
Исправлены ошибки показа причин сбоев при подключении и разрывах связи.
Исправлена ошибка перемещения меню с подменю, имеющим вложеные подменю.
Исправлена ошибка отображения текста пользователей, чей ник заканчивается на символ '\'
Исправлена ошибка установки временного игнора на ник
Исправлена ошибка отображения комбинации цветов текста (не белый+белый) в белой теме
Исправлена недоработка команды /MESSAGE при использовании в параметре тэга %n%

Изменено меню редактирования/удаления/просмотра информации о банах и исключениях. Информация о бане или исключении будет показываться только во всплывающем окне.
При вводе пароля сервера в настройках профиля он не будет закрываться звёздочками, при редактировании профиля вместо пароля бует показываться текст "***". Для смены пароля необходимо будет стереть звёздочки и вписать новый пароль.
Изменена форма индикаторов окон.
Изменена/доработана система переключения окон стилусом. Теперь заголовок окна разделен на сегменты, соответствующие кол-ву окон, переключение происходит по тапу на соответствующем сегменте.
При перемещении редактируемого меню вверх\вниз, индекс выбранного меню будет так же перемещаться. 
При переходе назад на меню высшего уровня (в редактируемых меню), курсор будет установлен на выбранном ранее подменю.
Теперь при сбросе меню из настроек перезапускать программу не нужно.

Из общего файла ресурсов выделены ресурсы Действий (actions.dat), и ресурсы меню (status.dat, channel.dat, nicklist.dat, private.dat). Разделение ресурсов ускорит загрузку приложения.
Переделана структура меню списка ников и привата (учитаны пожелания по местоположению некоторых пунктов меню). Необходимо сбросить старое меню в настройках.
При добавлении/удалении/редактировании избранного/действий/меню будет выдаваться подсказка (один раз) о том, что необходимо корректно выйти из программы для сохранения изменений
Список игноров при добавлении или удалении игноров теперь будет сохраняться сразу, а не при корректном выходе из программы
Для телефонов, имеющих кнопку "Назад", она будет работать как "Закрыть" и "Отмена" во всех меню и в окне ввода текста.
Если сервер не предоставляет информацию о времени и пользователе, установившем бан\исключение, то меню Информация будет недоступно, а меню Копировать будет копировать только маску бана\исключения
При положении заголовка "Внизу" индикаторы памяти и лагометра будут находиться внизу заголовка

Доработана система оповещений (Highlight), теперь при проверке не будут учитываться цвета и стили текста.
Доработана установка\снятия режимов канала. (Информация о максимальном количестве режимов (в т.ч. банов и исключений), которые можно изменить за один раз берётся из raw 005, по умолчанию 4)
Доработана команда /HOP, теперь можно указывать причину, которая будет показываться при выходе с канала (пожалуйста, не спамьте причинамы выхода с канала)
Доработана команда /OPENURL, теперь можно открывать ссылки, введённые вручную, указав её в качестве параметра команды (в качестве ссылки будет выбран весь текст после команды, в т.ч пробелы и др. символы, не использующиеся в подсветке ссылок)
Доработана команда /CLOSE. Теперь можно указывать текст, который будет показываться при выходе с канала (для окон канала) (пожалуйста, не спамьте причинамы выхода с канала)
Доработана команда /COPY. Добавлен параметр 'b', позволяющий копировать текст во временный буфер.

Добавлены настройки, позволяющие редактировать команды, вызываемые при комбинациях клавиш 0 - 9, *+0 - *+9, #+0 - #+9 (за исключением *+5, #+4 и #+6). Поддерживается использование алиасов и комбинаций команд (как в редактируемых меню).
Добавлена настройка расширенного режима, позволяющая использовать 10 дополнительных комбинаций (*+#+n). При использовании режима, таймаут для нажатий кнопок * и # не наступает, необходимо повторно нажать их. Нажатие кнопки * или # в этом режиме будет сопровождаться соответствующим значком под индикаторами окон.
Добавлены команды /ScreenUp, /LineUp, /PageUp, /PrevWindow, /NextWindow, /ScreenDown, /LineDown, /PageDown (исключительно для назначаемых комбинаций)
Добавлена функция Лагометр, позволяющая следить за изменением задержки сигнала к серверу и обратно (лаг). Показатель лагометра отображается синей полоской вверху экрана по закону: первая четверть экрана 0-10сек., вторая четверть 10-30сек, третья - 30-60сек и четвёртая 60-200сек (красная полоса). Частота проверки лага определяется настройкой частоты проверки сокета.
Добавлена настройка включения\выключения лагометра. Настройка игнорируется при выключенной функии "Проверка сокета"
Добавлена команда /LAG для получения текущего состояния лага.
В редактируемое меню добавлен алиас %address(ник[,n])%, возвращающий адрес указанного ника по маске "n". Если маска не указана, то она равна маске по умолчанию; При вызове алиаса парсер текста будет остановлен до получения ответа от сервера об адресе пользователя. Если адрес получен не будет, то вызов меню так же будет не закончен.
В редактируемое меню добавлен алиас %letters(Текст,n1[,n2])%, возвращающий часть текста с символа n1 до символа n2 (включительно). Если n2 не указан, то текст возвращается с n1 до конца, для отрицательных значений n1 и n2 отсчёт ведётся справа на лево. В случае ошибки возвращается пустота. 
Примеры использования %letters%: %letters(123456789,2,5)% = 2345; %letters(123456789,1,7)% = 1234567; %letters(123456789,5)% = 56789; %letters(123456789,-3)% = 1234567; %letters(123456789,-5)% = 12345; %letters(123456789,-5,-3)% = 567. Более углублённые примеры практического использования алиаса будут рассмотрены отдельно.
Добавлен рекурсивный вызов алиасов, т.е. алиас может быть вызван из другого алиаса, за исключением %text% и %address% в связи с особенностью получения информации из вне.
В распознавание ссылок добавлены символы '~', '#', '+'
Добавлена возможность изменять сервер и порт по умолчанию при создании нового профиля из текстовых ресурсов.
Добавлено меню копирования информации о бане или исключении.
Добавлено меню удаления <b>выбранных</b> банов и исключений. 
Добавлено меню "Вставить канал" в окне ввода текста
В редактируемое меню добавлено меню <b>"Копировать" меню</b>
Добавлена возможность использования двойной буферизации при прорисовке текста (убирает мерцание перерисовки, присутствующее на некоторых телефонах)
Добавлена настройка включения\выключения двойной буферизации
Доработана система автопрокрутки текста
Доработана команда /NAMESLIST. Если указать параметром ник, то, если ник присутствует в списке ников, курсор будет установлен на этом нике.
Доработана команда /COPY при копировании текста, определённого как utf8 при автоматическом определении utf8
Доработана команда /KBAN
Добавлен процентный относительный индикатор загрузки. Индикатор запоминает время предыдущей загрузки и по нему показывает процент текущей загрузки. 
Добавлена поддержка произвольных префиксов каналов (информация берётся из raw 005, по умолчанию "#")
Добавлен алиас %buffer%, возвращающий текст, скопированный командой /COPY
Добавлен алиас %tempbuf%, возвращающий текст, скопированный во временный буфер (команда /COPY с параметром 'b'). Временный буфер автоматически очищается по окончании выполнения команд.
Добавлена система просмотра списка каналов (/LIST). Список запоминается как алфавитное меню, где в подменю каждой буквы находятся каналы, начинающиеся с этой буквы. Имеется возможность зайти на выбранный канал, посмотреть его тему или информацию о кол-ве человек и режимах.
Пользователям предыдущих версий необходимо сбросить меню канала и статуса в настройках или самостоятельно добавить новое подменю "Список каналов" и в него новые меню:
Список каналов
.>100 человек:/list >100
.>50 человек:/list >50
.>20 человек:/list >20
.>10 человек:/list >10
.Все каналы (трафик!):/list
0.35a
Исправлена ошибка вызова меню при заблокированной клавиатуре
Исправлена ошибка при использовании в шаблонах %sN-M% диапазонов слов M>=10
Исправлена ошибка при прорисовке цветной запятой
Исправлена ошибка при обработке текста пользователя, состоящего из одного или двух символов с кодом 1
Исправлена ошибка показа меню канала, открытого из списка каналов
Исправлена ошибка просмотра списка каналов после закрытия любого окна при открытом окне списка каналов (зависание окна списка каналов) 
0.35b
Исправлена ошибка сохранения меню после некорректного добавления\редактирования
Исправлена случайно добавленная ошибка отображения курсива
Исправлена недоработка ввода служебных символов
Доработана команда /HOP (не посылается PART, в случае отсутствия на канале)
Доработано использование расширенного режима комбинаций клавиш. После нажатия комбинации клавиш, флаги нажатых '*' и '#' сбрасываются.
При возвращении из меню списка ников в список ников, курсор устанавливается на выбранный ранее ник.
Во второй строке ответа на запрос версии (в ресурсах) можно указывать теги цвета и стилей
В поле "Пароль сервера" вместо трёх звёздочек "***" пароль будет закрыт словом "<пароль скрыт>". Для ввода нового пароля, слово необходимо будет полностью стереть.
Внесены изменения в language.dat, касающиеся действий пользователя (например "меняет ник" заменено на "сменил ник")
0.35с
Праздничный подарок пользователям jmirc-m: новогодние снежинки, разбросанные по экрану телефона.
Добавлена настройка включения\выключения показа снежинок 
Добавлена настройка включения\выключения анимации снежинок
Добавлена настройка кол-ва снежинок, показываемых на экране. При установке значения 0, кол-во будет автоматически расчитано. Максимальное кол-во отображаемых снежинок равно высоте экрана в пикселях.
Доработано редактируемое меню: название подменю теперь будет отображаться с ':' в конце для наглядности. При добавлении или редактировании названия подменю, символ ':' вводить не надо.
Доработан просмотр списка каналов: если список каналов по какому-либо запрошенному критерию пуст, то пустое меню списка каналов показано не будет. 
Исправлена недоработка списка каналов. При отключении от сети в список каналов не будет добавляться меню "Выход"
//---------------------------------------------
Hamper mod (требует MIDP-2.0)
//---------------------------------------------

0.29h
Добавлена поддержка альтернативного мелкого шрифта
При блокировке клавиатуры отключается подсветка (сообщение о блокировке не выводится)

0.29bh2
Добавлено автоопределение UTF-8
Исправлены ошибки отображения битмапного шрифта
Исправлено отображения названия канала в заголовке
На кнопку 0 добавлен вывод меню "Избранное"
Профиль не совместим с предыдущими версиями

0.29bh3
Добавлена возможность использовать битмапный шрифт в заголовке
Добавлена возможность включить вибрацию при уведомлениях
Разделитель ников для уведомлений исправлен обратно с %n% на ;

0.30h1
Изменения основной версии
Добавлена возможность включить вибрацию при приходе приватного сообщения при заблокированной клавиатуре

0.31h1
Изменения основной версии
Исправлена подсветка обращений
Добавлена возможность включить вибрацию при обращениях
Добавлена возможность установки времени до отключения подсветки (пока нестабильно)

0.31bh1, 0.31ch1
Изменения основной версии

0.32h1
Изменения основной версии
Вибрация в привате работает только при ЗАБЛОКИРОВАННОЙ клавиатуре
Изменения в коде обработки шрифтов
Изменения в коде обработки графического шрифта
Отключены хайлайты в окне статуса

0.32h2
Исправления вывода окна выбора цвета
Исправление мелких недочетов
Оптимизация кода

0.33h
Изменения основной версии
Графический шрифт выгружается из памяти если не используется
Выключение вибрации на лету

0.34h
Изменения основной версии
Изменения работы с подсветкой для NOKIA

0.35ch
Изменения основной версии
Добавление возможности использования звуков вместо системного сигнала
Добавлена команда /QUOTE как синоним команды /RAW
*/
