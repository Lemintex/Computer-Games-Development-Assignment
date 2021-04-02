package game2D;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;

public class SoundFilterQuieter extends FilterInputStream {

    public SoundFilterQuieter(InputStream in) {
        super(in);
    }
    
    public short getSample(byte[] buffer, int position){
        return (short) (((buffer[position+1] & 0xff) << 8) | (buffer[position] & 0xff));
    }

    public void setSample(byte[] buffer, int position, short sample){
        buffer[position] = (byte) (sample & 0xff);
        buffer[position+1] = (byte) ((sample >> 8) & 0xff);
    }

    public int read(byte [] sample, int offset, int length) throws IOException{
		//GET NUMBER OF BYTES
		int bytesRead = super.read(sample,offset,length);
        //80% VOLUME
		float volume = .8f;
		short amp=0;
        //LOOP THROUGH 2 BYTES PER ITERATION
		for (int p=0; p<bytesRead; p = p + 2)
		{
            //READ CURRENT VOLUME
			amp = getSample(sample,p);
            //DECREASE
			amp = (short)((float)amp * volume);
            //AMP VOLUME
			setSample(sample,p,amp);
		}
		return length;
	}
}
