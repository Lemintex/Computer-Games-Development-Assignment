package game2D;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.nio.Buffer;

public class SoundFilterMuffle extends FilterInputStream {

    public SoundFilterMuffle(InputStream in) {
        super(in);
    }
    
    public short getSample(byte[] buffer, int position){
        return (short) (((buffer[position+1] & 0xff) << 8) | (buffer[position] & 0xff));
    }

    public void setSample(byte[] buffer, int position, short sample){
        buffer[position] = (byte) (sample & 0xff);
        buffer[position+1] = (byte) ((sample >> 8) & 0xff);
    }
}
