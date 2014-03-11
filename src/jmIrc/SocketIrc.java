package jmIrc;
/* SocketIrc.java 18.06.2008 */
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
import java.io.DataOutputStream;


import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
 
public class SocketIrc {
	private DataInputStream in;
	private DataOutputStream out;
	private boolean pollmode, connected;

	private int bytein=0;
	private int byteout=0;

	public SocketIrc(boolean pollmode) {
		this.pollmode = pollmode;
		connected = false;
	}

	public String connect(String host, int port, String init) {
		StreamConnection connector;
		String ret;

		try {
			connector = (StreamConnection) Connector.open("socket://" + host + ":" + port, Connector.READ_WRITE);
			in = connector.openDataInputStream();
			out = connector.openDataOutputStream(); 

			connected = true;
			ret = writeData(init);

		} catch (Exception e) {
			ret=jmIrc.language.get("ErrConnect",""+e.toString());
		}
		return ret;
	}

	public void disconnect() {
		if (connected) {
			try {
				if (out != null) {
					out.flush();
					out.close();
				}
				if (in != null)

					in.close();

				connected = false;
			} catch(Exception e) {}
		}
	}
	
	public String readLine() {
		String ret;
		byte[] buf,buf2;

		ret = null;
		try {
//			buf = Utils.readLine(in);
//			byte[] ret, buf;
			int i;
			buf2 = new byte[512];
			for(i=0; i<512; i++) {
				int readbyte = in.read();
				if (readbyte == -1){
					connected = false;
					return ret;
				}
				buf2[i] = (byte) readbyte;
				if(buf2[i]=='\n')break;
			}
			if(i==512) return null;
			if (i>0 && buf2[i-1] == '\r') i--;
			buf = new byte[i];
			System.arraycopy(buf2, 0, buf, 0, i);
			if (buf==null) return null;
			bytein += buf.length + 40;
			ret = Utils.byteArrayToString(buf,Database.Encoding);

		} catch(Exception e) {
			connected = false;
		}
		return ret;
	}
	
	public synchronized String writeData(String outbuf) {
		byte[] tmp;
		if (outbuf != null && connected) {
			try {
				tmp = Utils.stringToByteArray(outbuf,Database.Encoding);
				out.write(tmp);
				byteout += tmp.length;
				out.flush();
			}
			catch (Exception e) {
				connected = false;
				return jmIrc.language.get("ErrReadWrite",""+e.toString());
			}
		}
		return null;		
		
	}

	public boolean hasDataInBuffer() {
		if (pollmode) {
			try {
				return (in.available() > 0);
			} catch (Exception ioe) {
				return false;
			}
		}
		else return connected;
	}

	public boolean isConnected() {
		return connected;
	}

	public int getBytesIn() {
		return bytein;
	}

	public int getBytesOut() {
		return byteout;
	}
}
