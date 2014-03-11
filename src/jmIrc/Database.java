package jmIrc;
///#define sounds
/* Database.java 07.04.2008 */
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

import javax.microedition.lcdui.Font;
import javax.microedition.rms.RecordStore;

public class Database {
	// these are set in setprofile that is always called
	public static String ProfileName;
	public static String Nick;
	public static String AltNick;
	public static String Server;
	public static int Port;
	public static String UserName;
	public static String RealName;
	public static String Perform;
	public static String Encoding;
	public static String ServerPass;
    
	private static int[] idxarray;

	// other options
	static int Profileidx = -1;
	
	public static boolean TimeStamp;	
	public static boolean UseColor;	
	public static boolean UseMircCol;	
	public static boolean UsePoll;		
	public static boolean ShowInput;	
	public static boolean ShowAddress;	
	public static boolean JoinOnKick;	
	public static boolean JoinReconnect; 
	public static boolean Reconnect;	//переподключиться при дисконнекте
	public static boolean NotifyOn; 	
	public static boolean ShowJoinPart;  //Показывать входы\выходы\смену ника на каналах
	public static boolean utf8detect;	//cfg 2
//	public static boolean utf8output; //cfg 2
	public static boolean ShowMotd; //cfg 4
	public static boolean SortWind; //cfg7
	public static boolean SoftReverse; //cfg12
	public static boolean Lagometr; //cfg14
	public static boolean DoubleBuf; //cfg16
	public static boolean ShowSnows;//cfg18
	public static boolean AnimateSnows;//cfg18
	
	
	public static boolean HeaderUp;//cfg9 Положение шапки true - вверху, false - внизу

	public static boolean FullScreen;	//	midp2!!!

	public static boolean VibroHighLight;  // midp2 cfg5
	public static boolean VibroQuery;  // midp2 cfg5
	public static boolean VibroDisscon;  // midp2 cfg6
	public static boolean VibroWatchOnline;  // midp2 cfg5
	public static boolean VibroWatchOffline;  // midp2 cfg5
	public static boolean VibroPrivmsg;  // midp2 cfg5
	public static boolean VibroNotice;  // midp2 cfg5
	public static int VibroDuration; // midp2 cfg5
	public static boolean BeepHighLight;  // cfg11
	public static boolean BeepQuery;  // cfg11
	public static boolean BeepDisscon;  // cfg11
	public static boolean BeepWatchOnline;  // cfg11
	public static boolean BeepWatchOffline;  // cfg11
	public static boolean BeepPrivmsg;  // cfg11
	public static boolean BeepNotice;  // cfg11
        //#ifdef sounds
//# 	public static int SoundVol;  // cfg11
        //#endif
	//#if DEBUGER
//#	public static boolean EnDeb;
	//#endif
	public static int SocketPollTime;		
	public static int ReconnectTime;		//	время ожидания перед переподключением
	public static int ReconnectTry;//cfg8 //количество попыток пересоедениться
	public static int SnowsNum;//cfg20 //кол-во снежинок
	
	public static int BufLines;
	public static int Theme;//cfg7
	public static int FontSize;//cfg 3
	public static int FindURLs;//cfg 10
//	public static int cl_R=150;
//	public static int cl_G=170;
//	public static int cl_B=255;
	//#if NOKIA
//#	public static int OnBrg=100;
//#	public static int OffBrg=0;  
	//#endif  
	public static String Notify;					
	public static String HighLight;
	public static String Addressed;
	public static String TimeMask;				
	public static String QuitMessage;

	public static String[] Combinations;//cfg 14
	public static boolean AdvComb;//cfg 14 Альтернативный режим комбинаций с залипанием клавиш * и #
	

	private static int cfg;//версия настроек
	private static int prf;//версия профилей (не исп)
	private static int prfv;//версия считанного профиля

	private static final char cv='#';
	private static final char c=':';	

//  Резервные слоты для профиля
    private static boolean bPReserv[]={false/*1*/,false/*2*/,false/*3*/};
    private static int iPReserv[]={0/*1*/,0/*2*/};
    private static String sPReserv[]={""/*1*/,""/*2*/};    
//  Резервные слоты для настроек
    private static int iAReserv[]={0/*1*/,0/*2*/,0/*3*/,0/*4*/,0/*5*/};
    private static boolean bAReserv[]={false/*1*/,false/*2*/,false/*3*/,false/*4*/,false/*5*/,
		  							   false/*6*/,false/*7*/,false/*8*/,false/*9*/,false/*10*/};
    private static String sAReserv[]={""/*1*/,""/*2*/,""/*3*/};    
	
	
	private final static String STORE_CONFIG = "jmir-m.cfg";
	private final static int cnfg=20;//кол-во изменений в параметрах
	private final static String STORE_PROFILE = "jmir-m.prof";
	private final static int prof=1;//кол-во изменений в параметрах FIXME
	private final static String BAR_CONFIG="jmirc-m.bar";
	
	public Database() {

	}

	public static void LoadDef(){
		TimeStamp=true;	
		UseColor=true;	
		UseMircCol=true;	
		UsePoll=true;		
		ShowInput=true;	
		ShowAddress=true;	
		JoinOnKick=false;	
		JoinReconnect=true; 
		Reconnect=true;	//переподключиться при дисконнекте
		NotifyOn=false; 	
		ShowJoinPart=true;  //Показывать входы\выходы\смену ника на каналах
		utf8detect=true;	//cfg 2
//		utf8output=false; //cfg 2
		ShowMotd=true; //cfg 4
		SortWind=false; //cfg7
		SoftReverse=false; //cfg12
		Lagometr=true;//cfg14
		DoubleBuf=false;//cfg16
		ShowSnows=Utils.HewYear();
		AnimateSnows=true;
		HeaderUp=true;//cfg9 Положение шапки true - вверху, false - внизу
		FullScreen=false;	//	midp2!!!
		VibroHighLight=true;  // midp2 cfg5
		VibroQuery=true;  // midp2 cfg5
		VibroDisscon=true;  // midp2 cfg6
		VibroWatchOnline=true;  // midp2 cfg5
		VibroWatchOffline=false;  // midp2 cfg5
		VibroPrivmsg=false;  // midp2 cfg5
		VibroNotice=false;  // midp2 cfg5
		VibroDuration=1000; // midp2 cfg5
		BeepHighLight=true;  // cfg11
		BeepQuery=true;  // cfg11
		BeepDisscon=true;  // cfg11
		BeepWatchOnline=true;  // cfg11
		BeepWatchOffline=false;  // cfg11
		BeepPrivmsg=false;  // cfg11
		BeepNotice=false;  // cfg11
                //#ifdef sounds
//# 		SoundVol=100;  // cfg11
                //#endif
		SocketPollTime=60;		
		ReconnectTime=60;		//	время ожидания перед переподключением
		ReconnectTry=5;//cfg8 //количество попыток пересоедениться
		SnowsNum=0;
		BufLines = 100;
		Theme=0;//cfg7
		FontSize=Font.SIZE_SMALL;//cfg 3
		FindURLs=1;//cfg 10
		Notify="";					
		HighLight = "%me%";
		Addressed="%b%%nick%%b%: ";
		TimeMask="[h:m:s]";				
		QuitMessage="";
		//#if DEBUGER
//#		EnDeb=true;
		//#endif
	}
	
	public static void LoadDefKeys(){
		AdvComb=false;
		Combinations=new String[40];
		for(int i=0;i<40;i++)Combinations[i]="";
		Combinations[1]="/ScreenUp";//1
		Combinations[2]="/LineUp";//2
		Combinations[3]="/PageUp";//3
		Combinations[4]="/PrevWindow";//4
		Combinations[5]="/message";//5
		Combinations[6]="/NextWindow";//6
		Combinations[7]="/ScreenDown";//7		
		Combinations[8]="/LineDown";//8
		Combinations[9]="/PageDown";//9
        Combinations[10]="/favourites";
		Combinations[11]="/copy -";
		Combinations[12]="/copy -a";
		Combinations[13]="/background";
//        Combinations[15]="Блокировка клавиш";//mmm
        Combinations[16]="/clear";
        Combinations[17]="/nameslist";
        Combinations[21]="/copy -c";
        Combinations[22]="/copy -ac";
        Combinations[23]="/copy -cz";
        Combinations[29]="/clock";
	}
	
	public static int LoadProgressBar(){
		DataInputStream din;
		int i=0;
		try {
			RecordStore rs=RecordStore.openRecordStore(BAR_CONFIG,true);
			din = new DataInputStream(new ByteArrayInputStream(rs.getRecord(1)));
			i=din.readInt();
			din.close();
			rs.closeRecordStore();			
		}catch(Exception e){
			try {
				RecordStore rs=RecordStore.openRecordStore(BAR_CONFIG,true);
				byte[] temp;
				temp = new byte[0];
				rs.addRecord(temp, 0, temp.length);
			}catch(Exception ee){};
		};
		return i;
	}
	
	public static void SaveProgressBar(int i){
		if(i==0)return;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			byte[] byteout;
			dos.writeInt(i);
			byteout = baos.toByteArray();
			dos.close();
			baos.close();
			RecordStore rs = RecordStore.openRecordStore(BAR_CONFIG,true);
			rs.setRecord(1,byteout,0,byteout.length);
			rs.closeRecordStore();
		}catch(Exception e){};
	}
	
	public static void load() {
		int i;
		LoadDef();
		LoadDefKeys();
		DataInputStream din;
		RecordStore rs=null;
		try {
			rs = RecordStore.openRecordStore(STORE_CONFIG, true);

			String version;
			
			try {
				byte[] temp = rs.getRecord(1);
				version = (new DataInputStream(new ByteArrayInputStream(temp))).readUTF();
			} catch (Exception e) {
				version = "";
			}
			cfg=version.indexOf(STORE_CONFIG+cv);
			if(cfg<0||version.indexOf(c)<=cfg)cfg=0;
			else cfg=Utils.parseInt(version.substring(version.indexOf(cv)+1,version.indexOf(c)));
			if(cfg>0){
				version=version.substring(version.indexOf(c)+1);
				prf=version.indexOf(STORE_PROFILE+cv);
				if(prf<0)prf=0;
				else prf=Utils.parseInt(version.substring(version.indexOf(cv)+1));
			}
			if(cfg>0&&prf>0){
				din = new DataInputStream(new ByteArrayInputStream(rs.getRecord(2)));
				Profileidx = din.readInt();
				
				din = new DataInputStream(new ByteArrayInputStream(rs.getRecord(3)));
				
				TimeStamp=din.readBoolean();
				UseColor=din.readBoolean();
				UseMircCol=din.readBoolean();
				UsePoll=din.readBoolean();
				ShowInput=din.readBoolean();
				ShowAddress=din.readBoolean();
				JoinOnKick=din.readBoolean();
				Reconnect=din.readBoolean();
				JoinReconnect=din.readBoolean();
				NotifyOn=din.readBoolean();
				if(cfg>=2){
					utf8detect=din.readBoolean();
					din.readBoolean();
				}
				if(cfg>=4)ShowMotd=din.readBoolean();
				if(cfg>=7)SortWind=din.readBoolean();
				if(cfg>=12)SoftReverse=din.readBoolean();
				if(cfg>=14)Lagometr=din.readBoolean();
				if(cfg>=17)DoubleBuf=din.readBoolean();
				if(cfg>=19)ShowSnows=din.readBoolean();
				if(cfg>=19)AnimateSnows=din.readBoolean();
				
				if(cfg>=9)HeaderUp=din.readBoolean();
				FullScreen=din.readBoolean();
				if(cfg>=5){
					VibroHighLight=din.readBoolean();
				    VibroQuery=din.readBoolean();
				    if(cfg>=6)VibroDisscon=din.readBoolean();
				    VibroWatchOnline=din.readBoolean();
				    VibroWatchOffline=din.readBoolean();
				    VibroPrivmsg=din.readBoolean();
				    VibroNotice=din.readBoolean();
				    VibroDuration=din.readInt();
				}
				if(cfg>=11){
					BeepHighLight=din.readBoolean();
					BeepQuery=din.readBoolean();
				    BeepDisscon=din.readBoolean();
					BeepWatchOnline=din.readBoolean();
					BeepWatchOffline=din.readBoolean();
					BeepPrivmsg=din.readBoolean();
					BeepNotice=din.readBoolean();
				}
				
				SocketPollTime=din.readInt();
				ReconnectTime=din.readInt();
				if(cfg>=8)ReconnectTry=din.readInt();
				if(cfg>=20)SnowsNum=din.readInt();
				BufLines=din.readInt();
				if(cfg>=7)Theme=din.readInt();
				if(cfg>=3)FontSize=din.readInt();
				if(cfg>=10)FindURLs=din.readInt();
				if(!Utils.MIDP2())Database.FindURLs=0;
				din.readUTF();//encoding
				Notify=din.readUTF();
				HighLight=din.readUTF();
				Addressed=din.readUTF();
				QuitMessage=din.readUTF();
				TimeMask=din.readUTF();
				ShowJoinPart=din.readBoolean();
				if(cfg>=15){
					for(i=0;i<40;i++)Combinations[i]=din.readUTF();//cfg 14
					AdvComb=din.readBoolean();
				}	
//Резервные слоты				
				for(i=0;i<10;i++)bAReserv[i]=din.readBoolean();
				//#if DEBUGER
//#				EnDeb=bAReserv[9];
				//#endif
				for(i=0;i<5;i++)iAReserv[i]=din.readInt();
//				cl_R=iAReserv[4];
//				cl_G=iAReserv[3];
//				cl_B=iAReserv[2];
				//#if NOKIA
//#				OnBrg=iAReserv[4];
//#				OffBrg=iAReserv[3];
				//#endif
                //#ifdef sounds
//#                 SoundVol = iAReserv[2];       
                //#endif
                                
				for(i=0;i<3;i++)sAReserv[i]=din.readUTF();
				
				din.close();
			}
			else {
				rs.closeRecordStore();
				try {
					RecordStore.deleteRecordStore(STORE_CONFIG);
					RecordStore.deleteRecordStore(STORE_PROFILE);
				} catch (Exception e) {}
				
				rs = RecordStore.openRecordStore(STORE_CONFIG, true);

				byte[] temp;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(baos);
				dos.writeUTF(STORE_CONFIG+cv+(cfg=cnfg)+":"+STORE_PROFILE+cv+(prf=prof));
				temp = baos.toByteArray();
				dos.close();
				
				rs.addRecord(temp, 0, temp.length);
				temp = new byte[0];
				rs.addRecord(temp, 0, temp.length);
				rs.addRecord(temp, 0, temp.length);
				
				save_profile();
				save_advanced();
			}
			rs.closeRecordStore();
			
		}
		catch(java.io.EOFException e) {
			LoadDef();
			save_advanced();
		}
		catch(Exception e){
			e.printStackTrace();
			
		}
	
		getProfiles();
		setProfile(Profileidx);
	}
	
	public static String[] getProfiles() {
		String[] ret = null;

		try {
			//byte[] temp;
			DataInputStream din;
			RecordStore rs = RecordStore.openRecordStore(STORE_PROFILE, true);

			if (rs.getNumRecords() == 0) {
				rs.addRecord(new byte[4], 0, 4);
				idxarray = new int[0];
				ret = new String[0];
			}
			else {
				din = new DataInputStream(new ByteArrayInputStream(rs.getRecord(1)));
				rs.closeRecordStore();

				int profiles = din.readInt();
				ret = new String[profiles];
				idxarray = new int[profiles];

				for (int i=0; i<ret.length; i++) {
					ret[i] = din.readUTF();
					idxarray[i] = din.readInt();
				}
				din.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	public static void setProfile(int index) {
		ProfileName = "";
		Nick = "";
		AltNick = "";
		Server = jmIrc.language.get("ServerDef");
		Port = Utils.parseInt(jmIrc.language.get("PortDef"));
		Perform = "";
		UserName = "";
		RealName = "";
		Encoding = "Windows-1251";
		ServerPass = "";
		if(index>=0&&index<idxarray.length){
			try {
				int rsidx = idxarray[index];
				RecordStore rs = RecordStore.openRecordStore(STORE_PROFILE, false);
				if (rsidx > 0) {
					DataInputStream dis = new DataInputStream(new ByteArrayInputStream(rs.getRecord(rsidx)));
					prfv=0;
					ProfileName = dis.readUTF();
					if(!ProfileName.equals("")&&ProfileName.length()>2&&ProfileName.charAt(0)=='\001'){
						prfv=(int)ProfileName.charAt(1);
						ProfileName=ProfileName.substring(2);
					}
					Nick = dis.readUTF();
					AltNick = dis.readUTF();
					Server = dis.readUTF();
					Port = dis.readInt();
					Perform = Utils.Replace(dis.readUTF(),"%n%",";");
					UserName = dis.readUTF();
					RealName = dis.readUTF();
					ServerPass = dis.readUTF();
					if(prfv>=1)Encoding=dis.readUTF();
//					:\
//Резервные слоты					
					for(int i=0;i<3;i++)bPReserv[i]=dis.readBoolean();
					for(int i=0;i<2;i++)iPReserv[i]=dis.readInt();
					for(int i=0;i<2;i++)sPReserv[i]=dis.readUTF();


				}
				rs.closeRecordStore();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void addProfile() {
		// this should handle the adding also just fine
		editProfile(idxarray.length);
	}

	public static void editProfile(int index) {
		try {
			ByteArrayOutputStream baos;
			DataOutputStream dos;

			RecordStore rs = RecordStore.openRecordStore(STORE_PROFILE, false);
			editProfileName(rs, index,ProfileName);

			baos = new ByteArrayOutputStream();
			dos = new DataOutputStream(baos);
			dos.writeUTF((prof>0?"\001"+(char)prof:"")+ProfileName);
			dos.writeUTF(Nick);
			dos.writeUTF(AltNick);
			dos.writeUTF(Server);
			dos.writeInt(Port);
			dos.writeUTF(Perform);
			dos.writeUTF(UserName);
			dos.writeUTF(RealName);
			dos.writeUTF(ServerPass);
			dos.writeUTF(Encoding);
//			if(prf>=2)...
//Резервные слоты
			for(int i=0;i<3;i++)dos.writeBoolean(bPReserv[i]);
			for(int i=0;i<2;i++)dos.writeInt(iPReserv[i]);
			for(int i=0;i<2;i++)dos.writeUTF(sPReserv[i]);
			
			
			byte[] temp = baos.toByteArray();
			dos.close();

			rs.setRecord(idxarray[index], temp, 0, temp.length);
			rs.closeRecordStore();
			if(prf<prof)SaveVersion();//версия загруженных настроек меньше, пересохраняем номер версии
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void SaveVersion(){//RecordStore должны быть закрыты
		try{
			RecordStore rs=RecordStore.openRecordStore(STORE_CONFIG,true);
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			DataOutputStream dos=new DataOutputStream(baos);
			dos.writeUTF(STORE_CONFIG+cv+cnfg+c+STORE_PROFILE+cv+prof);
			byte[] temp=baos.toByteArray();
			rs.setRecord(1,temp,0,temp.length);
			dos.close();
			baos.close();
			rs.closeRecordStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void deleteProfile(int index) {
		try {
			RecordStore rs = RecordStore.openRecordStore(STORE_PROFILE, false);

			rs.deleteRecord(idxarray[index]);
			editProfileName(rs, index, null); // delete profile name

			rs.closeRecordStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void editProfileName(RecordStore rs, int idx, String newname) throws Exception {
		int i, profiles;
		DataInputStream dis;
		DataOutputStream dos;
		ByteArrayOutputStream baos;
		boolean createnew = false;

		byte[] temp = rs.getRecord(1);
		dis = new DataInputStream(new ByteArrayInputStream(temp));
		baos = new ByteArrayOutputStream();
		dos = new DataOutputStream(baos);

		profiles = dis.readInt();
		if (newname == null && idx >= 0 && idx < profiles) {
			// if we delete a profile then decrease the idxarray size
			profiles--;
			idxarray = new int[profiles];
		}
		else if (idx < 0 || idx >= profiles) {
			// create a new profile
			profiles++;
			idxarray = new int[profiles];
			createnew = true;
		}
		else if (newname == null) {
			// can't delete a profile that doesn't exist
			return;
		}
		dos.writeInt(profiles);

		for (i=0; i<profiles; i++) {
			if (i == profiles-1 && createnew) break;

			if (i == idx) {
				if (newname != null) {
					dis.readUTF();
					dos.writeUTF(newname);
					idxarray[i] = dis.readInt();
					dos.writeInt(idxarray[i]);
					continue;
				}
				else {
					dis.readUTF();
					dis.readInt();
				}
			}
			dos.writeUTF(dis.readUTF());
			idxarray[i] = dis.readInt();
			dos.writeInt(idxarray[i]);
		}
		if (createnew) {
			// add a new profile to the end
			dos.writeUTF(newname);
			idxarray[i] = rs.getNextRecordID();
			dos.writeInt(idxarray[i]);

			// also add the empty record for it
			rs.addRecord(new byte[0], 0, 0);
		}

		temp = baos.toByteArray();
		dis.close();
		dos.close();
		rs.setRecord(1, temp, 0, temp.length);

		if (Profileidx >= idxarray.length)
			Profileidx = idxarray.length-1;
	}

	public static void save_profile() {
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			byte[] byteout;

			dos.writeInt(Profileidx);
			byteout = baos.toByteArray();
			dos.close();
			baos.close();

			RecordStore rs = RecordStore.openRecordStore(STORE_CONFIG, true);
			rs.setRecord(2, byteout, 0, byteout.length);
			rs.closeRecordStore();
		}
		catch(Exception e) {
			e.printStackTrace();
			// error in saving settings, should handle this
		}
	}

	public static void save_advanced() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			byte[] byteout;

			dos.writeBoolean(TimeStamp);
			dos.writeBoolean(UseColor);
			dos.writeBoolean(UseMircCol);
			dos.writeBoolean(UsePoll);
			dos.writeBoolean(ShowInput);
			dos.writeBoolean(ShowAddress);
			dos.writeBoolean(JoinOnKick);
			dos.writeBoolean(Reconnect);
			dos.writeBoolean(JoinReconnect);
			dos.writeBoolean(NotifyOn);
			dos.writeBoolean(utf8detect);
			dos.writeBoolean(false);//utf8output
			dos.writeBoolean(ShowMotd);
			dos.writeBoolean(SortWind);
			dos.writeBoolean(SoftReverse);
			dos.writeBoolean(Lagometr);
			dos.writeBoolean(DoubleBuf);
			dos.writeBoolean(ShowSnows);
			dos.writeBoolean(AnimateSnows);
			dos.writeBoolean(HeaderUp);
			dos.writeBoolean(FullScreen);
			dos.writeBoolean(VibroHighLight);
			dos.writeBoolean(VibroQuery);
			dos.writeBoolean(VibroDisscon);
			dos.writeBoolean(VibroWatchOnline);
			dos.writeBoolean(VibroWatchOffline);
 		    dos.writeBoolean(VibroPrivmsg);
			dos.writeBoolean(VibroNotice);													
			dos.writeInt(VibroDuration);
			dos.writeBoolean(BeepHighLight);
			dos.writeBoolean(BeepQuery);
			dos.writeBoolean(BeepDisscon);
			dos.writeBoolean(BeepWatchOnline);
			dos.writeBoolean(BeepWatchOffline);
			dos.writeBoolean(BeepPrivmsg);
			dos.writeBoolean(BeepNotice);
			dos.writeInt(SocketPollTime);
			dos.writeInt(ReconnectTime);
			dos.writeInt(ReconnectTry);
			dos.writeInt(SnowsNum);
			dos.writeInt(BufLines);
			dos.writeInt(Theme);
			dos.writeInt(FontSize);
			if(!Utils.MIDP2())Database.FindURLs=0;
			dos.writeInt(FindURLs);
			dos.writeUTF("");//encoding
			dos.writeUTF(Notify);
			dos.writeUTF(HighLight);
			dos.writeUTF(Addressed);
			dos.writeUTF(QuitMessage);
			dos.writeUTF(TimeMask);
			dos.writeBoolean(ShowJoinPart);
			
			for(int i=0;i<40;i++)dos.writeUTF(Combinations[i]);
			dos.writeBoolean(AdvComb);			
			//#if DEBUGER
//#			bAReserv[9]=EnDeb;
			//#endif
			for(int i=0;i<10;i++)dos.writeBoolean(bAReserv[i]);
//			iAReserv[4]=cl_R;
//			iAReserv[3]=cl_G;
//			iAReserv[2]=cl_B;
			//#if NOKIA
//#			iAReserv[4]=OnBrg;
//#			iAReserv[3]=OffBrg;
			//#endif
			//#ifdef sounds
//# 		iAReserv[2]=SoundVol;       
			//#endif
			for(int i=0;i<5;i++)dos.writeInt(iAReserv[i]);
			for(int i=0;i<3;i++)dos.writeUTF(sAReserv[i]);
			byteout = baos.toByteArray();
			dos.close();
			baos.close();

			RecordStore rs = RecordStore.openRecordStore(STORE_CONFIG, true);
			rs.setRecord(3, byteout, 0, byteout.length);
			rs.closeRecordStore();
			if(cfg<cnfg)SaveVersion();//версия загруженных настроек меньше, пересохраняем номер версии
		}
		catch(Exception e) {
			e.printStackTrace();
			// error in saving settings, should handle this
		}
	}
}
