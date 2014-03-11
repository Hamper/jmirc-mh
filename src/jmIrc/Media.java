package jmIrc;
///#define sounds
/* Media.java 31.01.2008 */
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
///#define SIEMENS2
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.AlertType;
//#ifdef sounds
//# import java.util.Vector;
//# import javax.microedition.media.MediaException;
//# import javax.microedition.media.Player;
//# import javax.microedition.media.PlayerListener;
//# import javax.microedition.media.Manager;
//# import javax.microedition.media.control.StopTimeControl;
//# import javax.microedition.media.control.VolumeControl;
//#endif
//#if NOKIA
//# import com.nokia.mid.ui.DeviceControl;
//#endif
//#if SAMSUNG
//# import com.samsung.util.LCDLight;
//#endif
public class Media 
//#ifdef sounds
//#         implements PlayerListener 
//#endif 
{
         private static long SilenceTime=60000;
        //#ifdef sounds
//#     	public static final int SOUND_TYPE_HIGHLIGHT = 1;
//# 	public static final int SOUND_TYPE_QUERY = 2;
//# 	public static final int SOUND_TYPE_DISCON = 3;
//#         public static final int SOUND_TYPE_ONLINE = 4;
//#         public static final int SOUND_TYPE_OFFLINE = 5;
//#         public static final int SOUND_TYPE_PRIVMSG = 6;
//#         public static final int SOUND_TYPE_NOTICE = 7;
//#         
//#         private static ResourcesUTF8 mediastrs;
//#         
//# 	private static Media _this;
//#         
//#         
//#         public static boolean playerFree = true;
//# 	private static long stopTime = StopTimeControl.RESET;       
//#      
//#         
//#         static
//#         {
//#             // initializer
//#             
//#         }
//#        public Media()
//#        {
//#             	_this = this;
//#                 mediastrs = new ResourcesUTF8("/sounds.dat");
//#        }
        //#endif
        
        public static int LightState=1;
  
	public static void Vibro() { 
                if(System.currentTimeMillis()<jmIrc.ConnectTime+SilenceTime)return;
		if (Window.EnVibro)
                    //#ifdef NOKIA
//#                     com.nokia.mid.ui.DeviceControl.startVibra(100, Database.VibroDuration);
                    //#else
                    jmIrc.display.vibrate(Database.VibroDuration);  
                    //#endif
	}
	/*
	public static void Effect(int Eff, int Ext) { 
		switch (Eff) {
		case 0:
			//if (Ext==null) Ext = 1000;
			jmIrc.display.vibrate(Ext);
		}
	}
	*/
	public static void BackLight(int State) {
                LightState = State;
		if(!Window.EnLgtChg)return;
                switch (State) {
		case 0: 
                        //#if NOKIA
//#                         //com.nokia.mid.ui.DeviceControl.setLights(0,100);  //регион (0-экран), яркость (0..100)
//#                         com.nokia.mid.ui.DeviceControl.setLights(0,Database.OffBrg);
                        //#elif SAMSUNG
//#                         com.samsung.util.LCDLight.off();
                        //#else
			jmIrc.display.flashBacklight(0x7fffffff);
			jmIrc.display.flashBacklight(0);                        
                        //#endif
			break;
		case 1: 
                        //#if NOKIA
//#                         com.nokia.mid.ui.DeviceControl.setLights(0,Database.OnBrg);
                        //#elif SAMSUNG
//#                         com.samsung.util.LCDLight.on(0x0fffffff);  //время в мс
                        //#else
			jmIrc.display.flashBacklight(0x7fffffff);
                        //#endif
		}
	}
        
        public static void Beep(){
                if(System.currentTimeMillis()<jmIrc.ConnectTime+SilenceTime)return;
		if (Window.EnBeep)AlertType.ALARM.playSound(jmIrc.display);
	}
	
        //#ifdef sounds 
//# 	private static Vector soundQueue = new Vector();
//# 	
//# 	private static void startPlayer(Player player)
//# 	{
//# 		try
//# 		{
//# 			if (player != null)
//# 			{
//# 				if (player.getState() == Player.UNREALIZED)
//# 				{
//# 					player.prefetch();
//# 					player.realize();
//# 				}
//# 
//# 				if (player.getState() != Player.CLOSED)
//# 				{
//# 					Thread.sleep(0);
//# 					player.start();
//# 				}
//# 			} else {
//# 				playerFree = true;
//# 			}
//# 			Thread.sleep(0);
//# 		}
//# 		catch (MediaException e)
//# 		{
//# 			discardPlayer(player);
//# 		}
//# 		catch (Exception e)
//# 		{
//# 			discardPlayer(player);
//# 		}
//# 	}
//# 
//# 	private static void discardPlayer(Player player)
//# 	{
//# 		if (player != null)
//# 		{
//# 			player.close();
//# 		}
//# 		playerFree = true;
//# 	}
//# 
//# 
//# 	private static void playSound_Internal(String fileName, int volume)
//# 	{
//# 
//# 		try
//# 		{
//# 			//Siemens 65-75 bugfix
//#ifdef SIEMENS2
//# 			Player p1 = createPlayer("silence.wav");
//# 			setVolume(p1,100);
//# 			p1.start();
//# 			p1.close();
//# 			playerFree = true;
//#endif
//# 			Player p = createPlayer(fileName);
//# 			if (p == null) return;
//# 			setVolume(p, volume);
//# 			startPlayer (p);
//# 		}
//# 		catch (Exception e) {} 
//# 	}
//# 	
//# 	private static void playSound(String fileName, int volume)
//# 	{
//# 		if (playerFree) playSound_Internal(fileName, volume);
//# 		else 
//#                 {
//# 			soundQueue.addElement(fileName);
//# 			soundQueue.addElement(new Integer(volume));
//# 		}
//# 	}
//# 	
//# 	public static boolean testSoundFile(String source)
//# 	{
//# 		playerFree = true;
//# 		Player player = createPlayer(source);
//# 		boolean ok = (player != null);
//# 		if (player != null) player.close();
//# 		playerFree = true;
//# 		return ok;
//# 	}
//# 
//# 	// Reaction to player events. (Thanks to Alexander Barannik for idea!)
//# 	public void playerUpdate(final Player player, final String event, Object eventData)
//# 	{
//# 		if (event.equals(PlayerListener.END_OF_MEDIA))
//# 		{
//# 			if (player!=null) {
//# 				player.removePlayerListener(_this);
//# 				player.close();
//# 			}
//# 			playerFree = true;
//# 			
//# 			if (soundQueue.size() != 0)
//# 			{
//# 				String name = (String)soundQueue.elementAt(0);
//# 				Integer volume = (Integer)soundQueue.elementAt(1);
//# 				playSound(name, volume.intValue());
//# 				soundQueue.removeElementAt(0);
//# 				soundQueue.removeElementAt(0);
//# 			}
//# 		} else if (event.equals(PlayerListener.CLOSED))
//# 		{
//# 			playerFree = true;
//# 		}
//# 	}
//# 
//# 	/* Creates player for file 'source' */
//# 	static private Player createPlayer(String source)
//# 	{
//# 		if (!playerFree) return null;
//# 
//# 		String url, mediaType;
//# 		Player p;
//# 
//# 		url = source.toLowerCase();
//# 
//# 		/* What is media type? */
//# 		if (url.endsWith("mp3")) 
//# 			mediaType = "audio/mpeg";
//# 		else if (url.endsWith("amr"))
//# 			mediaType = "audio/amr";
//# 		else if (url.endsWith("jts"))
//# 			mediaType = "audio/x-tone-seq";
//# 		else if (url.endsWith("mid") || url.endsWith("midi"))
//# 			mediaType = "audio/midi";
//# 		else
//# 			mediaType = "audio/X-wav";
//# 
//# 		try
//# 		{
//# 			Class cls = new Object().getClass();
//# 			InputStream is = cls.getResourceAsStream(source);
//# 			if (is == null)
//# 				is = cls.getResourceAsStream("/" + source);
//# 			if (is == null)
//# 				return null;
//# 			p = Manager.createPlayer(is, mediaType);
//# 			p.realize();
//# 			p.prefetch();
//# 			updateStopTime(p);
//# 			p.addPlayerListener(_this);
//# 			playerFree = false;
//# 		} catch (MediaException e)
//# 		{
//# 			return null;
//# 		} catch (IOException e)
//# 		{
//# 			return null;
//# 		}
//# 		return p;
//# 	}
//# 	
//# 	// sets volume for player
//# 	static private void setVolume(Player p, int value) throws MediaException
//# 	{
//# 		VolumeControl c = (VolumeControl) p.getControl("VolumeControl");
//# 		if (c != null) c.setLevel(value);
//# 		}
//# 
//# 	static private void updateStopTime(Player p)
//# 	{
//# 		StopTimeControl c = (StopTimeControl) p.getControl("StopTimeControl");
//# 		if (c != null) c.setStopTime(stopTime);
//# 	}
//# 
//# 	// Play a sound notification
//# 	static public void playSoundNotification(int notType)
//# 	{
//#                 if(((notType==SOUND_TYPE_HIGHLIGHT)||
//#                         (notType==SOUND_TYPE_NOTICE)||
//#                         (notType==SOUND_TYPE_OFFLINE))&&
//#                         (System.currentTimeMillis()<jmIrc.ConnectTime+SilenceTime))return;
//# 		synchronized (_this)
//# 		{
//# 			if (!Window.EnBeep) return;
//# 
//# 			int not_mode = 0;
//# 
//# 			switch (notType)
//# 			{
//# 			case SOUND_TYPE_DISCON:
//# 				if(!Database.BeepDisscon)return;
//# 				break;
//# 
//# 			case SOUND_TYPE_HIGHLIGHT:
//# 				if(!Database.BeepHighLight)return;
//# 				break;			
//#                             
//#                         case SOUND_TYPE_NOTICE:
//# 				if(!Database.BeepNotice)return;
//# 				break;
//#                                 
//#                         case SOUND_TYPE_OFFLINE:
//# 				if(!Database.BeepWatchOffline)return;
//# 				break;
//# 
//# 			case SOUND_TYPE_ONLINE:
//# 				if(!Database.BeepWatchOnline)return;
//# 				break;
//#                                 
//# 			case SOUND_TYPE_PRIVMSG:
//# 				if(!Database.BeepPrivmsg)return;
//# 				break;
//#                                 
//#                         case SOUND_TYPE_QUERY:
//# 				if(!Database.BeepQuery)return;
//# 				break;    
//# 			}
//#                         
//#                         playSound(mediastrs.get("snd"+notType), Database.SoundVol);
//# 		}
//# 	}
//# 
            //#endif
	
}
