package game2D;

public class Spikes extends Sprite{

    Animation spikeAnim;
    boolean onRoof;
    
    public Spikes(Animation anim) {
        super(anim, 0);
    }

    //RETURNS CLONE OF SPIKES
    public Spikes copy() throws CloneNotSupportedException {
        return (Spikes) this.clone();
    }

    //HANDLES COLLISION WITH PLAYER
    public void handleCollisionWithPlayer(Player p){
            p.kill();
        }

    //GETTERS AND SETTERS
    //----------------------------------------
    public void setOnRoof(boolean r){
        onRoof = r;
        if (onRoof)
            setRotation(180);
    }

    public boolean isOnRoof(){
        return onRoof;
    }
}