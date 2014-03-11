package DrawControls;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public final class TPropFont {

  //public static TPropFont font = null;

    public TPropFont(String s) {
        try {
            InputStream inputstream;
            (inputstream = getClass().getResourceAsStream(s)).read();
            e = inputstream.read();
            f = inputstream.read();
            x = new int[e];
            y = new int[e];
            w = new int[e];
            for(int i = 0; i < e; i++) {
                x[i] = inputstream.read();
                y[i] = inputstream.read();
                w[i] = inputstream.read() + 1;
            }
            return;
        }
        catch(IOException ex) {}
    }

    public final void setImage(String s) {
        try{
          d[0] = Image.createImage(s);
        }catch(IOException ex) {}
        createColor();
    }

    public final void setImage(Image image) {
        d[0] = image;
        createColor();
    }

    public final int getWidth(char c1) {
    	int i;
    	i = GetCharNum(c1);
        if(i < 0 || i >= e)
            return 0;
        else
            return w[i];
    }

    public final int getStringWidth(String s) {
        int i = 0;
        for(int j = 0; j < s.length(); j++)
            i += getWidth(s.charAt(j));
        return i;
    }

    public final int getHeight() {
        return f;
    }

    public final void drawString(Graphics g, int s_x, int s_y, String s, int color) {
        int cur_x = g.getClipX();
        int cur_y = g.getClipY();
        int cur_w = g.getClipWidth();
        int cur_h = g.getClipHeight();
        int scr_x = s_x;
	//boolean modifycolor = false;
        int index = color;
		if (index==0) index=1;else if (index==1) index=0;
		if (index>15) index=0;
		
//        for (index = 0; (mcolor[index] != color) && (index <= 15); index++) ;

        for(int i = 0; i < s.length(); i++) {
            int ch;
            ch = GetCharNum(s.charAt(i));
            if(ch < 0 || ch >= e)
                continue;

            int ch_w = w[ch];

            if(ch > 0) {
                int i3 = scr_x - x[ch];
                int j3 = s_y - y[ch];

                if(scr_x < cur_x)
                    if(scr_x + ch_w > cur_x) {
                        ch_w -= cur_x - scr_x;
                        scr_x = cur_x;
                    } else {
                        scr_x += ch_w;
                        continue;
                    }

                if(scr_x + ch_w > cur_x + cur_w) {
                    if(scr_x >= cur_x + cur_w)
                        break;
                    ch_w = (cur_x + cur_w) - scr_x;
                }

                g.setClip(scr_x, s_y, ch_w, f);
                g.drawImage(d[index], i3, j3, 20);

            }
            scr_x += ch_w;
        }
        g.setClip(cur_x, cur_y, cur_w, cur_h);
    }

    private int GetCharNum(char c1){
        int i;

        i = c1;

        if(i == 13 || i == 10 || (i >= 127 && i <= 144) ||
          (i >= 152 && i <= 163) || (i >= 165 && i <= 167) || i == 172 ||
          (i >= 175 && i <= 183) || (i >= 187 && i <= 191))
            return 0;

        if((i) > '\037' && i < 127)
            i -= 32;
        //////////////////////////////
        i=(i==160?0:(i==145||i==146||i==164)?7:(i==147||i==148)?2:(i==149)?10:(i==150||i==151||i==173)?13:(i==169)?96:(i==170)?101:(i==171)?97:(i==174)?98:(i==185)?100:(i==186)?101:(i==187)?102:(i>1039&&i<1104)?i-937:(i==1025)?i-928:(i==1105)?i-1004:i);
        /*if(i == 160)
            i = 0;
        if(i == 145 || i == 146 || i == 164)
        	i = 7;
        if(i == 147 || i == 148)
        	i = 2;
        if(i == 149)
            i = 10;
        if(i == 150 || i == 151 || i == 173)
        	i = 13;
        if(i == 169)
            i = 96;
        if(i == 170)
            i = 101;
        if(i == 171)
            i = 97;
        if(i == 174)
            i = 98;
        if(i == 185)
            i = 100;
        if(i == 186)
            i = 101;
        if(i == 187)
            i = 102; */
        //////////////////////////////
        /*if(i > 1039 && i < 1104)
            i -= 937;
        if(i == 1025)
            i -= 928;
        if(i == 1105)
            i -= 1004;*/
//        if(i < 0 || i >= e || i == 13 || i == 10)  //sandy
        return i;
    }

    private void createColor() {
      int h = d[0].getHeight();
      int w = d[0].getWidth();

      long usedmemory = Runtime.getRuntime().freeMemory();

      int[] buf = new int[h * w];
for(int i = 1; i <= 15; i++) {
      d[0].getRGB(buf, 0, w, 0, 0, w, h);
      for(int x = 0;x < w * h; x++) {
        if(buf[x] == 0xff000000) {
          buf[x] = 0xff000000 | mcolor[i];
        }
      }

      d[i] = Image.createRGBImage(buf, w, h, true);
}
      buf = null;
     System.gc();
     System.out.println("\ncreateimage mem used: "+(Runtime.getRuntime().freeMemory()-usedmemory));
     System.out.println("total mem used: "+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()));
    }

    private int x[];
    private int y[];
    private int w[];
    private Image[] d = new Image[17];
    private int e;
    private int f;
    private static final int[] mcolor =
					{0x00000000, // 0x00000000,
					0x00ffffff,  // 0x00aa0000,
					0x0000007f,  // 0x0000d200,
					0x00009300,  // 0x00aa5522,
					0x00ff0000,  // 0x000000aa,
					0x007f0000,  // 0x00aa00aa,
					0x009c009c,	 // 0x0000aaaa,
					0x00fc7f00,  // 0x00aaaaaa,
					0x00ffff00,  // 0x00444444,
					0x0000fc00,  // 0x00ff4444,
					0x00009393,	 // 0x0044ff44,
					0x0000ffff,  // 0x00ffff44,
					0x000000fc,  // 0x004444ff,
					0x00ff00ff,  // 0x00ff44ff,
					0x007f7f7f,  // 0x0044ffff,
					0x00d4d0c8}; // 0x00ffffff
}
