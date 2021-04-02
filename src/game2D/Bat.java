package game2D;

public class Bat extends Sprite{

    public Bat(Animation anim, float s) {
        super(anim, 0.125f);
    }

    public void initialiseMovement(){
        setVelocityX(getSpeed());
    }

    public Bat copy() throws CloneNotSupportedException {
        return (Bat) this.clone();
    }

    @Override
    public void handleCollisionWithPlayer(Player p) {
        p.kill();
    }

    public void move(boolean turn){
        if (getVelocityX() == 0){
            setVelocityX(getSpeed());
        }
        if (turn){
            setVelocityX(-getVelocityX());
            setScaleX((float)-getScaleX());
        }
    }
}