package game2D;

public class Spikes extends Sprite{

    Animation spikeAnim;
    boolean onRoof;
    
    public Spikes(Animation anim) {
        super(anim, 0);
      }

    public Spikes copy() throws CloneNotSupportedException {
        return (Spikes) this.clone();
    }

    public void handleCollisionWithPlayer(Player p, char c, float g){
        if (c == 'y' || c == 'x'){
            p.kill();
        }
    }

    public void setOnRoof(boolean r){
        onRoof = r;
        if (onRoof)
            super.setRotation(180);
    }

    public boolean isOnRoof(){
        return onRoof;
    }
}