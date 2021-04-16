//2715375
package game2D;

import java.io.*;
import javax.sound.sampled.*;

public class Sound extends Thread {

	String filename;	// The name of the file to play
	boolean finished;	// A flag showing that the thread has finished
	boolean loop;
	boolean muffled;
	boolean shhh;
	public Sound(String fname, boolean l, boolean m, boolean shh) {
		filename = fname;
		finished = false;
		loop = l;
		muffled = m;
		shhh = shh;
	}

	/**
	 * run will play the actual sound but you should not call it directly.
	 * You need to call the 'start' method of your sound object (inherited
	 * from Thread, you do not need to declare your own). 'run' will
	 * eventually be called by 'start' when it has been scheduled by
	 * the process scheduler.
	 */
	public void run() {
		AudioInputStream is = null;
		try {
			File file = new File(filename);
			AudioInputStream stream = AudioSystem.getAudioInputStream(file);
			AudioFormat	format = stream.getFormat();
			if (muffled){
				SoundFilterMuffle f = new SoundFilterMuffle(stream);
				is = new AudioInputStream(f, format, stream.getFrameLength());
			}
			else if (shhh){
				SoundFilterQuieter f = new SoundFilterQuieter(stream);
				is = new AudioInputStream(f, format, stream.getFrameLength());
			}
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip = (Clip)AudioSystem.getLine(info);
			if(muffled || shhh){
				clip.open(is);
			}
			else{
				clip.open(stream);
			}
			if (loop)
				clip.loop(clip.LOOP_CONTINUOUSLY);
			else
				clip.start();
			Thread.sleep(100);
			while (clip.isRunning() && !finished) { Thread.sleep(10); }
			
				clip.close();
		}
		catch (Exception e) {	}
			finished = true;
	}

	public void setStop(boolean s){
		finished = s;
	}
}
