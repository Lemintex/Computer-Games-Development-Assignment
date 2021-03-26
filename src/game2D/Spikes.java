package game2D;

public class Spikes extends Sprite{

    Animation spikeAnim;
    boolean roof;
    
    public Spikes(Animation anim) {
        super(anim, 0);
        }

    public Spikes copy() throws CloneNotSupportedException {
        return (Spikes) this.clone();
    }

    public void handleCollisionWithPlayer(Sprite p, char c, float g){
        if (c == 'y' || c == 'x'){
            p.kill();
        }
    }

    public void setRoof(boolean r){
        roof = r;
        if (roof)
            super.setRotation(180);
    }

    public boolean getRoof(){
        return roof;
    }
}