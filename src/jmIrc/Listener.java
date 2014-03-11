package jmIrc;
///#define sounds
/* Listener.java 15.12.2008 */
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
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Vector;

public class Listener extends Thread {
	private UIHandler uihandler;

    public static String KickBan="";//Typeник#канал:причина (Type=02 - +b *!*@host; 03 - +b *!user@*; 04 - +b *!user@*.host; 05 - +b *!*@*.host)
    //														(Type=12 - +kb *!*@host;13 - +kb *!user@*;14 - +kb *!user@*.host;15 - +kb *!*@*.host)
    public static String Ignore="";//TypeНик (Type - см KickBan (0x - permanent, 1x - temp))
    public static String MenuAddress="";//тип_маскиНик:тип_окнаНазвание_окна
    
    
//005 CHANMODES=A,B,C,D моды по умолчанию(исключая 'r' и A)
    public static String PREFIX="@%+";//005
    public static String PREFIXMODES="ohv";//005
    public static String[] MODES_CB={"l","k"};
    public static String MODES_D="psmnti";
    public static String CHANTYPES="#";
    public static char MAXMODES=4;//Mmax modes
    
	public boolean Connected=false;
	
	private long floodtime=0;
	private byte floodcount=0;
	private final static byte floodreply=6;//6 ответов
	private final static byte floodsecs=60;//в 60 секунд
	
	public static boolean isChannel(String chan) {
		if(chan.length()==0)return false;
		return (CHANTYPES.indexOf(chan.charAt(0))>=0);
	}
	
	public Listener(UIHandler uihandler) {
		this.uihandler = uihandler;
		jmIrc.ServerError=null;
	}
	
	public void run() {
		String ErrorMSG;
		while(!jmIrc.Exit){
			while(jmIrc.Wait)try {Thread.sleep(200);} catch(InterruptedException ie){}
			if(jmIrc.Exit)break;
			ErrorMSG=jmIrc.Connect(uihandler.Server,uihandler.Port,uihandler.Pass);
			if(jmIrc.Exit)break;
			if(jmIrc.Wait)continue;
			if(ErrorMSG==null)ErrorMSG=Listen();
			if(ErrorMSG!=null){
				jmIrc.disconnect(ErrorMSG,null);
				ErrorMSG=null;
				jmIrc.ServerError=null;
				if(Database.VibroDisscon&&Utils.MIDP2())Media.Vibro();
				if(Database.BeepDisscon&&Connected)
                                    //#ifdef sounds
//#                                     Media.playSoundNotification(Media.SOUND_TYPE_DISCON);
                                    //#else
                                    Media.Beep();
                                    //#endif
				jmIrc.Reconnect++;
				if(!Database.Reconnect||jmIrc.Reconnect>Database.ReconnectTry)uihandler.Exit();
				else {
					uihandler.console.AddInfo(jmIrc.language.get("TryReconnect",Database.ReconnectTime+" "+jmIrc.Reconnect));
					int i=0;
					while(!jmIrc.Wait&&!jmIrc.Exit&&i<Database.ReconnectTime*5&&jmIrc.Reconnect>0){
						try{Thread.sleep(200);}catch(InterruptedException _ex) { }
						i++;
					}
				}
			}	
		}
	}	

	private String Listen() {
		String data, Err = null;
		int counter,Snow=0;
		jmIrc.Reconnect=0;
		counter = 0;
		while(jmIrc.irc.isConnected()&&!jmIrc.Wait){
			try {Thread.sleep(200);} catch(InterruptedException ie){}
			counter++;Snow++;
			// check poll time here
			if(counter>=Database.SocketPollTime*5&&Database.UsePoll){
				jmIrc.SendIRC((!jmIrc.Lag&&Database.Lagometr?"PING :"+uihandler.nick:""));
				if(jmIrc.Lag)uihandler.GetActiveWindow().repaint();
				else Window.LagTime=System.currentTimeMillis();
				jmIrc.Lag=true;
				counter=0;
			}
			if(Database.ShowSnows&&Snow>(Database.AnimateSnows?2:300)){uihandler.console.CreateSnowPos(!Database.AnimateSnows);Snow=0;uihandler.GetActiveWindow().repaint();}
			if(Window.autoscroll> 0)Window.autoscroll++;
			if(Window.autoscroll< 0)Window.autoscroll--;
			if(Window.autoscroll> 2)uihandler.GetActiveWindow().LineUp(false);
			if(Window.autoscroll<-2)uihandler.GetActiveWindow().LineDown(false);
			if((UIHandler.Clock)&&counter%5==0)uihandler.GetActiveWindow().repaint();
				
			if (Err == null) {
				while(jmIrc.irc.isConnected()&&jmIrc.irc.hasDataInBuffer()){
					data=jmIrc.irc.readLine();
					if(data!=null&&!data.trim().equals(""))checkMessage(data);
				}
			}

			if(Err==null)Err=jmIrc.ServerError;
   			if(Err!=null)break;
		}
		return Err;
	}
	
	private String[] parseLine(String input) {
		int i,j;
		String[] ret = new String[3];
		
		if(input.charAt(0)==':') {
			ret[0]=input.substring(1, input.indexOf(' '));
			i=input.indexOf(" ")+1;
		}
		else {
			ret[0]=null;
			i=0;
		}
		
		j=input.indexOf(" :",i);
		if (j != -1) {
			ret[1] = input.substring(i,j++);
			ret[2] = input.substring(j+1);
		}
		else if ((j = input.lastIndexOf(' ')) != -1) {
			ret[1] = input.substring(i,j);
			ret[2] = input.substring(j+1);
		}
		else {
			ret[1] = input.substring(i);
			ret[2] = null;
		}

		return ret;
	}
	
    private synchronized void checkMessage(String RawMess){
//    	System.out.println("Listener L172 "+RawMess);
        //#if DEBUGER
//#        if (Window.EnDebuger)
//#        uihandler.GetChannel("!RAW").AddInfo("<- "+RawMess);
        //#endif
        int i=0,k=RawMess.length()-1;
        while(RawMess.charAt(k-i)==' ')i++;
        if(i>0)RawMess=RawMess.substring(0,k-i+1);
        String[] cmdline,command;
        RawMess=RawMess.replace(TextArea.cbURL,TextArea.cCHAR);
        RawMess=RawMess.replace(TextArea.ceURL,TextArea.cCHAR);  
        RawMess=RawMess.replace(TextArea.cbUTF,TextArea.cCHAR);  
        RawMess=RawMess.replace(TextArea.ceUTF,TextArea.cCHAR);  
        RawMess=RawMess.replace(TextArea.cRem,TextArea.cCHAR);

        cmdline=parseLine(RawMess);
//        System.out.println("Listener L176 s[0]='"+cmdline[0]+"' s[1]='"+cmdline[1]+"' s[2]='"+cmdline[2]+"'");
        if(cmdline[0]==null)cmdline[0]=cmdline[1];
		if(cmdline[1]==null)return;
		command=Utils.splitString(cmdline[1]," ");
        
/*        
        String ss[]=Utils.splitString(RawMess," ");
        long jj=System.currentTimeMillis();
        for(int ii=0;ii<1000;ii++)ss=Utils.splitString(RawMess," ");
        System.out.println("Listener L220 s[]="+(System.currentTimeMillis()-jj));
        jj=System.currentTimeMillis();
        for(int ii=0;ii<1000;ii++)ss=Utils.splitString2(RawMess," ");
        System.out.println("Listener L223 sn[]="+(System.currentTimeMillis()-jj)+" "+ss[0]);
/**/        
        if(command[0].equals(Window.PING)){
        	jmIrc.SendIRC("PONG"+RawMess.substring(4));
            return;
        }
        if(cmdline[0].equals("ERROR")){
        	jmIrc.ServerError=cmdline[2];
        	return;
        }

        String Nick,Ident,Host;
        if((i=cmdline[0].indexOf('!'))>0&&(k=cmdline[0].indexOf('@'))>i){//nick!user@host
        	Nick=cmdline[0].substring(0,i);
        	Ident=cmdline[0].substring(i+1,k);
        	Host=cmdline[0].substring(k+1);
        }
        else {
        	Nick=cmdline[0];
        	Ident=Host="";
        }

        AliasValue[0]=(command.length>1?command[1]:cmdline[2]);
        AliasValue[1]=Nick;
		AliasValue[2]=Ident;
		AliasValue[3]=Host;
		AliasValue[4]=(Database.ShowAddress&&!Ident.equals("")&&!Host.equals("")?RepAddr(jmIrc.language.get("Address"),Ident+" "+Host):"");

        int RawNum=0;
        try{RawNum=Integer.parseInt(command[0]);}
        catch(NumberFormatException _ex){}
        Window Win;
        String Text="",CTCP;

        try{
        
        boolean flag=true;
        if(RawNum==0){
        	if(command[0].equals("PONG")||cmdline[0].equals("PONG")){
               	Window.LastLagTime=System.currentTimeMillis();
                jmIrc.Lag=false;
                return;
        	}        		
        	else if(command[0].equals(Window.MODE)){
        		String Words[]=Utils.splitString(RawMess," ");
            	String ModeNick="",Params="",Modes=Words[3];
            	int NumParams=0;
            	if(Modes.charAt(0)==':')Modes=Modes.substring(1);
            	if(Words.length>4){//собираем параметры модов
            		NumParams=4;
                	while(NumParams<=Words.length-1){
                		Params+=" "+Words[NumParams];
                		NumParams++;
                	}
                	Params=Params.substring(1);
                	NumParams-=4;
                } 
            	if(Listener.isChannel(Words[2])){//смена модов канала и есть сами моды хотябы
                	Win=uihandler.GetChannel(Words[2]);
                    i=k=0;
                    Text=Utils.Remove(Modes,new String[]{"+","-"});
                    while(i<Modes.length()){
                    	switch(Modes.charAt(i)){
                    		case '+':flag=true;k--;break;
                    		case '-':flag=false;k--;break;
                    		case 'b':{
                    			if(Win.Bans.isEmpty())break;//если список не был запрошен
                    			if(4+Text.length()-(k+1)<=Words.length-1)ModeNick=Words[4+k-(Text.length()-NumParams)];
                    			else break;//в принципе невозможно, но бывают глюки :\
                    			if(flag)Win.AddBan(ModeNick,Nick+' '+Utils.GetTimeStamp());
                    			else  
                    			for(int ii=0;ii<Win.Bans.size();ii++){
                    				String mask=(String)Win.Bans.elementAt(ii);
                    				mask=mask.substring(0,mask.indexOf(" "));
                    				if(mask.equals(ModeNick)){
                    					Win.Bans.removeElementAt(ii);
                    					break;
                    				}	
                    			}
                    			break;
                    		}
                    		case 'e':{
                    			if(Win.Excepts.isEmpty())break;//если список не был запрошен
                    			if(4+Text.length()-(k+1)<=Words.length-1)ModeNick=Words[4+k-(Text.length()-NumParams)];
                    			else break;//в принципе невозможно, но бывают глюки :\
                    			if(flag)Win.AddExcept(ModeNick,Nick+' '+Utils.GetTimeStamp());
                    			else  
                    			for(int ii=0;ii<Win.Excepts.size();ii++){
                    				String mask=(String)Win.Excepts.elementAt(ii);
                    				mask=mask.substring(0,mask.indexOf(" "));
                    				if(mask.equals(ModeNick)){
                    					Win.Excepts.removeElementAt(ii);
                    					break;
                    				}	
                    			}
                    			break;
                    		}
                    		default:
                    			int j=PREFIXMODES.indexOf(Modes.charAt(i));
                    			if(j>=0){
                        			if(4+Text.length()-(k+1)<=Words.length-1)ModeNick=Words[4+k-(Text.length()-NumParams)];
                        			else break;//в принципе невозможно, но бывают глюки :\
                        			j=PREFIXMODES.length()-PREFIXMODES.indexOf(Modes.charAt(i))-1;
                        			Win.changeMode((char)(1<<j),ModeNick,flag);
                    			}
                    			
                    			if(MODES_D.indexOf(Modes.charAt(i))>=0){
                    				if(flag)Win.Modes_D+=Modes.charAt(i);
                    				else Win.Modes_D=Utils.Remove(Win.Modes_D,new String[]{""+Modes.charAt(i)});
                    			}
                    			if(MODES_CB[0].indexOf(Modes.charAt(i))>=0||MODES_CB[1].indexOf(Modes.charAt(i))>=0){
                        			if(MODES_CB[0].indexOf(Modes.charAt(i))==-1||flag){//При снятии режимов, режимы 'C' снимаются без параметров, режимы 'B' с параметрами!
                        				if(4+Text.length()-(k+1)<=Words.length-1)ModeNick=Words[4+k-(Text.length()-NumParams)];
                        				else break;//в принципе невозможно, но бывают глюки :\
                        			}	
                        			String AllModes=MODES_CB[0]+MODES_CB[1];
                        			if(AllModes.indexOf(Modes.charAt(i))>=0){
                    					if(flag){
                    						if(Win.Modes_CB.indexOf(Modes.charAt(i))>=0)Win.RemoveMode(Modes.charAt(i));//при обновлении модов с параметром без снятия
                    						if(Win.Modes_CB.indexOf(" ")==-1)Win.Modes_CB=Modes.charAt(i)+" "+ModeNick;
                    						else Win.Modes_CB=Win.Modes_CB.substring(0,Win.Modes_CB.indexOf(" "))+Modes.charAt(i)+Win.Modes_CB.substring(Win.Modes_CB.indexOf(" "))+" "+ModeNick;
                    					}
                    					else if(!Win.Modes_CB.equals(""))Win.RemoveMode(Modes.charAt(i));
                    				}
                        			
                    			}
                    			break;
                    	}//switch
                    	i++;k++;
                    }//while
                    Win.AddInfo(RepAddr(jmIrc.language.get("CHMODE"),Modes+" "+Params));
                } 
                else {
                	uihandler.console.AddInfo(RepAddr(jmIrc.language.get("UMODE"),Modes+" "+Params));
//обработка смены своих                	
                }
            	return;
            }
        	else
           	if(command[0].equals(Window.PRIVMSG)){
           		//if(command[1].equals("#help")&&!uihandler.GetActiveWindow().Name.equals("#help"))Media.Vibro();
           		if(cmdline[2].length()>2&&cmdline[2].charAt(0)=='\001'&&cmdline[2].charAt(cmdline[2].length()-1)=='\001'){
           			i=cmdline[2].indexOf(' ');
           			if(i>1){
           				CTCP=cmdline[2].substring(1,i);//!!!
           				Text=cmdline[2].substring(i+1,cmdline[2].length()-1);
           			}	
           			else {
           				CTCP=cmdline[2].substring(1,cmdline[2].length()-1);
           				Text="";
           			}	

           			if(CTCP.equals("ACTION")){
            			if(Listener.isChannel(command[1]))Win=uihandler.GetChannel(command[1]);
            			else Win=uihandler.GetPrivate(Nick,false);
            			Win.AddMessage(RepAddr(jmIrc.language.get("ACTION"),Text),Win.HighLight(Text)); 
           			} 
           			else {	
    					if(Listener.isChannel(command[1]))Win=uihandler.GetChannel(command[1]);
    					else Win=uihandler.console;
           				Win.AddMessage(RepAddr(jmIrc.language.get("CTCP"),CTCP+" "+Text),Win.HighLight(CTCP+" "+Text));
           				String s="";
           				if(CTCP.equals(Window.PING))s=Window.PING+" "+Text;
               			if(CTCP.equals("TIME")){
               				Calendar calendar=Calendar.getInstance();
               				s="TIME ["+calendar.get(11)+":"+(calendar.get(12)>=10?"":"0")+calendar.get(12)+"]";
               			}
               			if(CTCP.equals(Window.VERSION)){
                			s=System.getProperty("microedition.platform");
                			if(s==null)s="J2ME device";
                			//Пожалуйста не убирайте и не изменяйте ответ версии, уважайте разработчика и его труд, дописывайте свой текст в конец.
                			s=Window.VERSION+" "+jmIrc.DefQuitMessage+" on "+s;
                   			String s2=jmIrc.language.get(Window.VERSION,false);
                   			if(s2!=null&&s2.length()>0)s+="\001\r\n"+Window.NOTICE+" "+Nick+" :\001VERSION "+Utils.CodeToChars(s2);
               			}
               			if(!s.equals("")){//защита от флуда запросами PING, VERSION, TIME, имеющими ответы
               				long current=System.currentTimeMillis()/1000;
               				if(current-floodtime>floodsecs/floodreply){
               					if(current-floodtime>floodsecs)floodcount=0;
               					else floodcount-=(current-floodtime)*floodreply/floodsecs;//уменьшаем счётчик
               					floodtime=current;
               				}
               				if(floodcount<floodreply){
               					jmIrc.SendIRC(Window.NOTICE+" "+Nick+" :\001"+s+"\001");//6 запросов в минуту
               					floodcount++;
               				}
               			}
           			} 
                }
           		else if(Listener.isChannel(command[1])){
           			k=uihandler.ChanNames.indexOf(command[1].toUpperCase());
           			if(k!=-1)Win=uihandler.GetChannel(command[1]);
					else Win=uihandler.console;
           			Win.AddMessage((k==-1?command[1]+":":"")+RepAddr(jmIrc.language.get("TextMessage"),cmdline[2]),Win.HighLight(cmdline[2]));
				}
    			else {
    				if(uihandler.PrivNames.indexOf(Nick.toUpperCase())==-1){
    					if(Database.VibroQuery&&Utils.MIDP2())Media.Vibro();
    					if(Database.BeepQuery)
                                    //#ifdef sounds
//#                                     Media.playSoundNotification(Media.SOUND_TYPE_QUERY);
                                    //#else
                                    Media.Beep();
                                    //#endif
    				}
    				else {
    					if(uihandler.KeyLock&&Database.VibroPrivmsg&&Utils.MIDP2())Media.Vibro();
    					if(uihandler.KeyLock&&Database.BeepPrivmsg)
                                    //#ifdef sounds
//#                                     Media.playSoundNotification(Media.SOUND_TYPE_PRIVMSG);
                                    //#else
                                    Media.Beep();
                                    //#endif
    				}
    				Win=uihandler.GetPrivate(Nick,false);
    				Win.AddMessage(RepAddr(jmIrc.language.get("TextMessage"),cmdline[2]),Win.HighLight(cmdline[2]));
    			}
           		return;
            } 
           	else	
            if(command[0].equals(Window.NOTICE)){
           		if(!cmdline[2].equals("")&&cmdline[2].charAt(0)=='\001'&&cmdline[2].charAt(cmdline[2].length()-1)=='\001'){
           			i=cmdline[2].indexOf(' ');
           			if(i>1){
           				CTCP=cmdline[2].substring(1,i);//!!!
           				Text=cmdline[2].substring(i+1,cmdline[2].length()-1);
           			}	
           			else {
           				CTCP=cmdline[2].substring(1,cmdline[2].length()-1);
           				Text="";
           			}	
           			CTCP=CTCP.toUpperCase();
           			if(CTCP.equals(Window.PING)){
           				try {
           					long j=Long.parseLong(Text);
           					j=((System.currentTimeMillis()-j)/1000L);
           					Text=""+j;
           					Text=Text.substring(Text.length());//?
           					Text=+j+" "+(Utils.parseInt(Text)==1?"секунда":(Utils.parseInt(Text)>=2&&Utils.parseInt(Text)<=4?"секунды":"секунд"));
           				}	
           				catch(NumberFormatException _ex){
           			    }           					
           			} 
					uihandler.console.AddMessage(RepAddr(jmIrc.language.get("CTCPReply"),CTCP+" "+Text),uihandler.console.HighLight(CTCP+" "+Text));
           		} 
           		else {
					if(Listener.isChannel(command[1]))Win=uihandler.GetChannel(command[1]);
					else Win=uihandler.console;
           			Win.AddMessage(RepAddr(jmIrc.language.get("TextNotice"),cmdline[2]),uihandler.console.HighLight(cmdline[2]));
					if(Database.VibroNotice&&Utils.MIDP2())Media.Vibro();
					if(Database.BeepNotice)
                                    //#ifdef sounds
//#                                     Media.playSoundNotification(Media.SOUND_TYPE_NOTICE);
                                    //#else
                                    Media.Beep();
                                    //#endif
           		}
           		return;
            } 
            else if(command[0].equals(Window.NICK)){
            	Text=RepAddr(jmIrc.language.get(command[0]),cmdline[2]);
            	for(i=0;i<uihandler.Channels.size();i++){
            		Win=(Window)uihandler.Channels.elementAt(i);
            		if(Win.hasNick(Nick)){
            			if(Database.ShowJoinPart)Win.AddInfo(Text);
            			Win.changeNick(Nick,cmdline[2]);
                    }
            	}
            	if(uihandler.nick.equals(Nick))uihandler.console.AddInfo(RepAddr(jmIrc.language.get("MeNickChange"),uihandler.nick=cmdline[2])); 
            	i=uihandler.PrivNames.indexOf(Nick.toUpperCase());//если есть приват
            	if(i>=0&&Database.ShowJoinPart)((Window)uihandler.Privates.elementAt(i)).AddInfo(Text);
        		if(uihandler.PrivNames.indexOf(cmdline[2].toUpperCase())>-1)i=-1;//если приват с новым ником уже есть
            	if(i>-1){
            		uihandler.PrivNames.setElementAt(cmdline[2].toUpperCase(),i);
            		Win=(Window)uihandler.Privates.elementAt(i);
            		Win.Name=cmdline[2];
            		Win.CheckEntry(Nick,cmdline[2]);//Если активен textbox с ником или никлист
            		Win.repaint();
                }
            	return;
            } 
            else 
            if(command[0].equals(Window.QUIT)){
            	Text=RepAddr(jmIrc.language.get(command[0]),cmdline[2]);
            	for(i=0;i<uihandler.Channels.size();i++){
            		Win=(Window)uihandler.Channels.elementAt(i);
            		if(Win.hasNick(Nick)){
            			if(Database.ShowJoinPart)Win.AddInfo(Text);
            			Win.deleteNick(Nick);
                    }
            	}
            	i=uihandler.PrivNames.indexOf(Nick.toUpperCase());//если есть приват
    			if(i>=0&&Database.ShowJoinPart)((Window)uihandler.Privates.elementAt(i)).AddInfo(Text);
    			return;
            } 
            else 
            if(command[0].equals(Window.JOIN)){
            	Win=uihandler.GetChannel(cmdline[2],Nick.equals(uihandler.nick));
            	if(Database.ShowJoinPart)Win.AddInfo(RepAddr(jmIrc.language.get(command[0]),cmdline[2]));
                if(!Nick.equals(uihandler.nick)&&!Win.hasNick(Nick))Win.addNick('\000',Nick);
                if(Nick.equals(uihandler.nick)){
                	jmIrc.SendIRC("MODE "+cmdline[2]);
                	Win.rejoin=false;
                }
                return;
            } 
            else 
            if(command[0].equals(Window.PART)){
            	Win=uihandler.GetChannel(command.length>1?command[1]:cmdline[2]);
            	if(command.length==1)cmdline[2]="";//no part message            	
           		if(!Nick.equals(uihandler.nick))Win.deleteNick(Nick);
           		else if(!Win.rejoin)Win.close();
       			if(Database.ShowJoinPart)Win.AddInfo(RepAddr(jmIrc.language.get(command[0]),(command.length==1||cmdline[2].equals("")?"":"("+cmdline[2]+"\003)")));
       		 	return;
            } 
            else 
            if(command[0].equals(Window.KICK)){
            	Win=uihandler.GetChannel(command[1]);
           		if(command[2].equals(uihandler.nick)){
       				Win.ClearNames();
       				Win.AddInfo(RepAddr(jmIrc.language.get("KickYou"),cmdline[2]));
           			if(Database.JoinOnKick)jmIrc.SendIRC(Window.JOIN+" "+command[1]);
           		} else{
           			Win.AddInfo(RepAddr(jmIrc.language.get(command[0]),command[2]+" "+cmdline[2]));
           			Win.deleteNick(command[2]);
           		}
           		return;
            }  	
            else 
            if(command[0].equals(Window.TOPIC)){
           		Win=uihandler.GetChannel(command[1]);
           		Win.AddInfo(RepAddr(jmIrc.language.get(command[0]),Win.Topic=cmdline[2]));
           		return;
            }
        }            			
   		
        if(command.length>2&&uihandler.ChanNames.indexOf(command[2].toUpperCase())>=0)Win=uihandler.GetChannel(command[2]);
		else if(command.length>2&&uihandler.PrivNames.indexOf(command[2].toUpperCase())>=0)Win=uihandler.GetPrivate(command[2],false);
   		else Win=uihandler.console;	      		
/*!
        
        if(Words.length>3){
        	if(Utils.isChannel(Words[3]))AliasValue[0]=Words[3];
        	else AliasValue[1]=Words[3];
        } 
        else AliasValue[1]="";
*/      		
        switch(RawNum){
        case 375:
        case 372:
        case 376:
        	boolean b=false;
        	if(Database.ShowMotd||jmIrc.MOTD)b=true;
        	if(RawNum==376)jmIrc.MOTD=true;
        	if(b)break;
        	return;
        case 1: // '\001'
        	Connected=true;
            uihandler.nick=command[1];//получаем свой ник при коннекте
        	if(Database.UsePoll&&Database.Lagometr)jmIrc.SendIRC("PING :"+uihandler.nick);        		
        	Window.LagTime=Window.LastLagTime=System.currentTimeMillis();
			jmIrc.Lag=true;
           	uihandler.console.AddInfo(jmIrc.language.get("Connected",uihandler.Server+" "+uihandler.Port));
           	uihandler.console.Perform();

            Vector list=uihandler.Menus[2];//Выставляем игноры
			for(i=0;i<list.size();i++)jmIrc.SendIRC(Window.SILENCE+" +"+(String)list.elementAt(i));
 			String[] Notify;
			Notify=Utils.splitString(Database.Notify,";");
            if(Database.NotifyOn&&Notify!=null&&Notify.length>0){//Уведомления
            	Text="";
            	for(i=0;i<Notify.length;i++){
            		Notify[i]=Notify[i].trim();
            		if(!Utils.hasNoValue(Notify[i]))Text+=" +"+Notify[i];
            	}
            	jmIrc.SendIRC("WATCH C"+Text);
            }	
            if(Database.JoinReconnect){//Перезаход после реконнекта на открытые каналы
            	String Channels="";
   				for(Enumeration en=uihandler.Channels.elements();en.hasMoreElements();)
   					Channels+=","+(((Window)en.nextElement()).Name);
   				if(Channels.length()>2)jmIrc.SendIRC(Window.JOIN+" "+Channels.substring(1));
   				
            }
            PREFIX="@%+";//005
            CHANTYPES="#";
            PREFIXMODES="ohv";//005
            MODES_CB=new String[]{"l","k"};
            MODES_D="psmnti";
            MAXMODES=4;
            break;
//        case 4:    
        case 5:
//        	:Irc.Paradise.ru 005 A SAFELIST HCN MAXCHANNELS=10 CHANLIMIT=#:10 MAXLIST=b:60,e:60,I:60 NICKLEN=30 CHANNELLEN=32 TOPICLEN=307 KICKLEN=307 AWAYLEN=307 MAXTARGETS=20 WALLCHOPS WATCH=128 :are supported by this server
//        	:Irc.Paradise.ru 005 A SILENCE=15 MODES=12 CHANTYPES=# PREFIX=(ohv)@%+ CHANMODES=beIqa,kfL,lj,psmntirRcOAQKVGCuzNSMTGDP NETWORK=Dal.Net.Ru CASEMAPPING=ascii EXTBAN=~,cqnr ELIST=MNUCT STATUSMSG=@%+ EXCEPTS INVEX CMDS=KNOCK,MAP,DCCALLOW,USERIP :are supported by this server
//          :Irc.Paradise.ru 005 A CALLERID :are supported by this server
        	String S="";
        	if((i=RawMess.indexOf("CHANMODES"))>=0){
        		Text=RawMess.substring(i);
//        		CHANMODES=beIqa,kfL,lj,psmntirRcOAQKVGCuzNSMTGDP
        		if((i=Text.indexOf(" "))>=0)Text=Text.substring(0,i);
        		if((i=Text.indexOf(","))>=0)S=Text.substring(i+1);//CHANMODES=A,B,C,D исключая A
           		if(!S.equals("")){
           			MODES_CB[1]=S.substring(0,i=S.indexOf(","));
           			S=S.substring(i+1);
           			MODES_CB[0]=S.substring(0,i=S.indexOf(","));
           			MODES_D=Utils.Remove(S.substring(i+1),new String[]{"r"});
           		}
        	}
        	if((i=RawMess.indexOf("PREFIX"))>=0){
        		Text=RawMess.substring(i);
        		if((i=Text.indexOf(" "))>=0)Text=Text.substring(0,i);
//        		PREFIX=(ohv)@%+
        		if((i=Text.indexOf("("))>=0){
        			Text=Text.substring(i+1);//ohv)@%+
        			if((i=Text.indexOf(")"))>=0){
        				String s1,s2;
						s1=Text.substring(0,i);//ohv
        				s2=Text.substring(i+1);//@%+
        				if(s1.length()==s2.length()){
        					PREFIXMODES=s1;
        					PREFIX=s2;
        				}
        			}
        		}
        	}
        	if((i=RawMess.indexOf("MODES"))>=0){
        		S=RawMess.substring(i);
        		if((i=S.indexOf(" "))>0)S=S.substring(0,i);
        		if((i=S.indexOf("="))>0)MAXMODES=(char)Utils.parseInt(S.substring(i+1));
        	}
        	if((i=RawMess.indexOf("CHANTYPES"))>=0){
        		S=RawMess.substring(i);
    			if((i=S.indexOf(" "))>0)S=S.substring(0,i);
    			if((i=S.indexOf("="))>0)CHANTYPES=S.substring(i+1);
        	}
        	break;
        case 600:case 601:case 604:case 605:
//:Irc.Paradise.ru 604 test test ~a 127-0-0-1.setka.dal.net.ru 1208520160 :is online
//s[0]='Irc.Paradise.ru' s[1]='604 test test ~a 127-0-0-1.setka.dal.net.ru 1208520160 ' s[2]='is online'
        	
            AliasValue[1]=command[2];
    		AliasValue[2]=command[3];
    		AliasValue[3]=command[4];
    		AliasValue[4]=(Database.ShowAddress&&!command[3].equals("")&&!command[4].equals("")?RepAddr(jmIrc.language.get("Address"),command[3]+" "+command[4]):"");
           	uihandler.console.AddInfo(RepAddr(jmIrc.language.get(""+RawNum),RawMess));
			if(((Database.VibroWatchOnline&&(RawNum==600||RawNum==604))||(Database.VibroWatchOffline&&(RawNum==601||RawNum==605)))&&Utils.MIDP2())Media.Vibro();
			if(Database.BeepWatchOnline&&(RawNum==600||RawNum==604))
                                     //#ifdef sounds
//#                                     Media.playSoundNotification(Media.SOUND_TYPE_ONLINE);
                                    //#else
                                    Media.Beep();
                                    //#endif                           
                        if(Database.BeepWatchOffline&&(RawNum==601||RawNum==605))
                                    //#ifdef sounds
//#                                     Media.playSoundNotification(Media.SOUND_TYPE_OFFLINE);
                                    //#else
                                    Media.Beep();
                                    //#endif
           	return;
        case 302:
			if(cmdline[2].indexOf("=")==-1)break;
        	String uNick=cmdline[2].substring(0,cmdline[2].indexOf("="));
			String uIdent=cmdline[2].substring(cmdline[2].indexOf("=")+2,cmdline[2].indexOf('@'));
			String uHost=cmdline[2].substring(cmdline[2].indexOf('@')+1).trim();
			if(uNick.charAt(uNick.length()-1)=='*')uNick=uNick.substring(0,uNick.length()-1);
			if(!KickBan.equals("")){
				String kNick=KickBan.substring(1,KickBan.indexOf("#"));
				if(uNick.equals(kNick)){
					String kChan=KickBan.substring(KickBan.indexOf("#"),KickBan.indexOf(":"));
					String reason=KickBan.substring(KickBan.indexOf(":")+1);
					int bb=KickBan.charAt(0)%10;
//Typeник#канал:причина (Type=01 - +b *!*@host; 03 - +b *!user@*; 04 - +b *!user@*.host; 05 - +b *!*@*.host)
//						(Type=11 - +kb *!*@host;13 - +kb *!user@*;14 - +kb *!user@*.host;15 - +kb *!*@*.host)
					jmIrc.SendIRC(Window.MODE+" "+kChan+" +b *!"+(bb==3||bb==4?uIdent:"*")+"@"+(bb==2?uHost:(bb==3?"*":CreateMask(uHost))));
					if((int)(KickBan.charAt(0)/10)==1)jmIrc.SendIRC(Window.KICK+" "+kChan+" "+kNick+" :"+reason);
				}
				KickBan="";
				return;
       		}
			else
			if(!Ignore.equals("")){
				if(uNick.equals(Ignore.substring(1))){
					String mask;
					int kk=Ignore.charAt(0)/10;//0 - permanent,1 - temp
					int bb=Ignore.charAt(0)%10;//mask type
					mask="*!"+(bb==3||bb==4?uIdent:"*")+"@"+(bb==2?uHost:(bb==3?"*":CreateMask(uHost)));
        			if(!uihandler.isIgnore(mask)){
        				uihandler.console.AddInfo(jmIrc.language.get(kk==1?"TempIgnore":"AddIgnore",mask));
        				if(kk!=1)uihandler.AddIgnore(mask);
        				jmIrc.SendIRC(Window.SILENCE+" +"+mask);
        			}	
				}
				Ignore="";
				return;
       		}
			else if(!MenuAddress.equals("")){
				int m=MenuAddress.charAt(0);
				MenuAddress=MenuAddress.substring(1);
				int n=MenuAddress.indexOf(":");
				if(uNick.equals(MenuAddress.substring(0,n))){
					MenuAddress=MenuAddress.substring(n+1);
					String mask="*!"+(m==3||m==4?uIdent:"*")+"@"+(m==2?uHost:(m==3?"*":CreateMask(uHost)));
					n=(int)MenuAddress.charAt(0);
					Text=MenuAddress.substring(1);
					MenuAddress="";
					switch(n){
					case 0:uihandler.console.SetAdddress(mask);break;
					case 1:
						if(uihandler.ChanNames.indexOf(Text)>=0)uihandler.GetChannel(Text).SetAdddress(mask);
						break;
					case 2:	
						if(uihandler.PrivNames.indexOf(Text)>=0)uihandler.GetPrivate(Text,false).SetAdddress(mask);
						break;
					}
				}
				return;
			}
				//тип_маскиНик:тип_окнаНазвание_окна
       	break;
        case 311:jmIrc.whois=true;break;
        case 317: 
           	uihandler.console.AddInfo(jmIrc.language.get("317i",Utils.parseTime(Long.parseLong(command[3]))));
           	uihandler.console.AddInfo(jmIrc.language.get("317c",Utils.parseTime((Utils.GetTimeStamp()-Long.parseLong(command[4])))));
            return;
        case 324: 
       		Text=RawMess;
       		for(i=0;i<=3&&Text.indexOf(" ")>=0;i++)Text=Text.substring(Text.indexOf(" ")+1);
       		if(Win.getType()==Window.TYPE_CONSOLE)break;
       		String Modes=Text;
       		if((i=Text.indexOf(" "))>0){
       			Modes=Text.substring(0,i);
       			Text=Text.substring(i+1);
       		}
       		else Text="";
   			Modes=Utils.Remove(Modes,new String[]{"r"});
       		Win.Modes_CB="";
       		Win.Modes_D="";
       		String AllModes=MODES_CB[0]+MODES_CB[1];
       		while(Modes.length()>1){
       			char c=Modes.charAt(Modes.length()-1);
       			if(AllModes.indexOf(c)>=0){
       				String Param;
       				if((i=Utils.LastIndexOf(Text," "))>0)Param=Text.substring(i+1);
       				else {
       					Param=Text;
       					Text="";
       				}
       				if((i=Win.Modes_CB.indexOf(" "))>=0)Win.Modes_CB=c+Win.Modes_CB.substring(0,i)+" "+Param+" "+Win.Modes_CB.substring(i+1);
       				else Win.Modes_CB=c+" "+Param;
       			}
       			if((i=Utils.LastIndexOf(Text," "))>0)Text=Text.substring(0,i);
       			if(MODES_D.indexOf(c)>=0)Win.Modes_D=c+Win.Modes_D;
       			Modes=Modes.substring(0,Modes.length()-1);
       		}
       		break;
        case 329:return;
        case 321:
        	uihandler.GetChanList();
        	return;//<- :mobile.dal.net.ru 321 Archangel[mob|aw] Channel :Users  Name
        case 322:
        	Win=uihandler.GetPrivate("@List",false);
        	if((i=cmdline[2].indexOf(" "))>0)cmdline[2]=cmdline[2].substring(0,i);
        	if(command[2].length()>=2&&command[3]!=null&&!command[3].equals("")&&cmdline[2]!=null&&!cmdline[2].equals(""))
        		Win.AddChannel(command[2],command[3]+" "+cmdline[2]);
        	return;
        case 323:
        	Win=uihandler.GetPrivate("@List",false);
        	Win.StopChanList();
        	return;
        case 332:
        	
        	if(Win.getType()==Window.TYPE_CHANNEL)Win.Topic=cmdline[2];
        	else if(uihandler.PrivNames.indexOf("@LIST")>=0)Win=uihandler.GetPrivate("@List",true);
        	break; 
        case 333: 
//        	:Irc.Paradise.ru 333 test #test test!~a@127-0-0-1.setka.dal.net.ru 1208520509
//        	s[0]='Irc.Paradise.ru' s[1]='333 test #test test!~a@127-0-0-1.setka.dal.net.ru' s[2]='1208520509'
        	if(Win.getType()==Window.TYPE_CHANNEL)Win.AddInfo(jmIrc.language.get("333",command[3]+" "+Utils.parseTime((Utils.GetTimeStamp()-Long.parseLong(cmdline[2])))));
        	else if(uihandler.PrivNames.indexOf("@LIST")>=0)Win=uihandler.GetPrivate("@List",true);
            return;
        case 353:
//:Irc.Paradise.ru 353 test = #test :@test 
        	
           	if(uihandler.ChanNames.indexOf(command[3].toUpperCase())==-1)break;
           	Win=uihandler.GetChannel(command[3]);
        	if(!jmIrc.names){Win.ClearNames();jmIrc.names=true;}//для корректного обновления списка ников
        	char c;
        	String Nicks[]=Utils.splitString(cmdline[2].trim()," ");
            for(i=0;i<Nicks.length;i++){
             	c=0;
             	Nick=Nicks[i];
             	while(PREFIX.indexOf(Nicks[i].charAt(0))>=0){//протоколом не предусмотрена, но возможно будет введена многопрефиксность перед ником +%@Ник вместо наибольшего статуса
             		c|=(1<<PREFIX.length()-1-PREFIX.indexOf(Nicks[i].charAt(0)));
             		Nicks[i]=Nicks[i].substring(1);
             	}
//для избежания двойных ников                    
                if(Win.hasNick(Nicks[i]))Win.deleteNick(Nicks[i]);
                Win.addNick(c,Nicks[i]);
            }
            return;

        case 366: 
        	if(Win.getType()==Window.TYPE_CONSOLE)break;
           	Win.printNicks();
           	jmIrc.names=false;
            return;
            
//            Listener L154 :Paradise.dal.net.ru 271 A A 1!*@*
//            Listener L154 :Paradise.dal.net.ru 272 A :End of Silence List
        case 271:    
            uihandler.GetActiveWindow().AddToIgnoreList(cmdline[2]);
            return;
        case 272:
        	uihandler.GetActiveWindow().SetIgnoresList();
        	return;
//            Listener L112 :Irc.Paradise.ru 367 ARC #1 a!*@* ARC 1185520363
//            Listener L112 :Irc.Paradise.ru 368 ARC #1 :End of Channel Ban List
        case 348:
        	if(Win.getType()!=Window.TYPE_CONSOLE&&Win.GetExceptList)Win.AddExcept(command.length>3?command[3]:cmdline[2],command.length>3?command[4]+' '+cmdline[2]:" ");
            else break;
        	return;
        case 349:
        	if(Win.GetExceptList){
        		Win.GetExceptList=false;
        		Win.ListExcepts();
        		return;
        	}
        	break;
        
        case 367:
        	if(Win.getType()!=Window.TYPE_CONSOLE&&Win.GetBanList)Win.AddBan(command.length>3?command[3]:cmdline[2],command.length>3?command[4]+' '+cmdline[2]:" ");
            else break;
        	return;
        case 368:
        	if(Win.GetBanList){
        		Win.GetBanList=false;
        		Win.ListBans();
        		return;
        	}
        	break;
//        case 431: 
        case 432:	
        case 433: 
//            	:irc.uzaomsk.ru 433 Archangel Moderator :Nickname is already in use.            	
            if(!Connected&&!Database.AltNick.trim().equals("")&&!command[2].equals(Database.AltNick)){
            	uihandler.console.AddInfo(jmIrc.language.get("TryAltNick",command[2]+' '+Database.AltNick));
            	jmIrc.SendIRC(Window.NICK+" "+Database.AltNick);
            }
            else if(!Connected)uihandler.console.nickChangeAction();
            else break;
            return;
       	}
        
        if(jmIrc.whois)Win=uihandler.console;
        if(RawNum==318)jmIrc.whois=false;

        /*
		Text="";i=1;
		boolean b=true;
		while(i<Words.length){
			if(b&&!Words[i].equals("")&&Words[i].charAt(0)==':'){Words[i]=Words[i].substring(1);b=false;}
			Text+=" "+Words[i++];
		}
*/		
		Text=cmdline[1]+" "+cmdline[2];
		
   		if(jmIrc.language.get(RawNum==0?command[0]:""+RawNum,null,false)!=null)Win.AddInfo(RepAddr(jmIrc.language.get(RawNum==0?command[0]:""+RawNum),Text));
   		else if(jmIrc.language.get(RawNum==0?"RAW":"000",null,false)!=null&&(Database.ShowInput||jmIrc.whois))uihandler.console.AddInfo(jmIrc.language.get(RawNum==0?"RAW":"000",Text));
		for(i=0;i<AliasValue.length;i++)AliasValue[i]="";
    }
   	catch(Exception e){
   		e.printStackTrace();
		uihandler.console.AddInfo(jmIrc.language.get("SystemError",(UIHandler.Buffer=RawMess+"\r\n"+e.toString())));
   	}

}	
	
   
	private String[] Aliases=new String[]{"%chan%","%nick%","%ident%","%host%","%address%"};
	private String[] AliasValue=new String[]{"","","","",""};;

	private String RepAddr(String Text,String Formate){
      	int i=0;
      	int aliasPos;
      	String out;

      	if(Database.utf8detect){
      		try{
      			String s=Utils.decodeUTF8(Utils.stringToByteArray(AliasValue[0],Database.Encoding),false);
      			if(s.length()<AliasValue[0].length())AliasValue[0]=TextArea.cbUTF+AliasValue[0]+TextArea.ceUTF;
      		} catch (UTFDataFormatException udfe) {}
      		try{
      			String s=Utils.decodeUTF8(Utils.stringToByteArray(AliasValue[1],Database.Encoding),false);
      			if(s.length()<AliasValue[1].length())AliasValue[1]=TextArea.cbUTF+AliasValue[1]+TextArea.ceUTF;
      		} catch (UTFDataFormatException udfe) {}
      	}      	
      	while(i<Aliases.length){
      		if(AliasValue[i]==null){i++;continue;}
      		out="";
      		aliasPos=Text.indexOf(Aliases[i]);
          	while(aliasPos>=0){
          		out+=Text.substring(0,aliasPos)+AliasValue[i]+TextArea.cRem;
          		Text=(aliasPos+Aliases[i].length()<Text.length()?Text.substring(aliasPos+Aliases[i].length()):"");
          		aliasPos=Text.indexOf(Aliases[i]);
          	}
          	out+=Text;
          	Text=out;
          	i++;
      	}	
		return Utils.RepS(Text,Formate);
	}


	private String CreateMask(String host){
		boolean h=false;
		int i=host.length();
		int t=0;
		String[] s={"","","",""};
		for(int j=0;j<i;j++){
			if(host.charAt(j)=='.')t++;
			else if(Character.isDigit(host.charAt(j))&&t<=3)s[t]+=host.charAt(j);
			else {
				h=true;
				break;
			}
		}
		if(!h&&(s[0].equals("")||s[1].equals("")||s[2].equals("")||s[3].equals("")))h=true;
		if(!h)return s[0]+"."+s[1]+"."+s[2]+".*";//ип адрес
		else {
			if((i=host.indexOf("."))>=0){
				String tmp=host.substring(i);
				if((t=tmp.indexOf("."))>=0)return "*"+tmp;
			}
			return host;
		}
	}

}


