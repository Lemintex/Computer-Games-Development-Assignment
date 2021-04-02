package game2D;

public class Bat extends Sprite{

    public Bat(Animation anim, float s) {
        super(anim, 0.125f);
    }

    //SETS BAT MOVING
    public void initialiseMovement(){
        setVelocityX(getSpeed());
    }

    //RETURNS COPY OF BAT
    public Bat copy() throws CloneNotSupportedException {
        return (Bat) this.clone();
    }

    //KILL PLAYER IF COLLIDED WITH
    public void handleCollisionWithPlayer(Player p) {
        p.kill();
    }

    //BAT MOVEMENT BRAIN
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
