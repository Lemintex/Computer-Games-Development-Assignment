package game2D;

public class LaserGate extends Sprite{

    Animation deactivatedAnim, activatedAnim;
    boolean activated, playerOn, crateOn;
    boolean toggleActivate;
    
    public LaserGate(Animation anim) {
        super(anim, 0);
        deactivatedAnim = anim;
        activated = false;
        toggleActivate = true;
        playerOn = false;
        crateOn = false;
    }

    //RETURNS CLOSE OF LASERGATE
    public LaserGate copy() throws CloneNotSupportedException {
        return (LaserGate) this.clone();
    }

    //HANDLES COLLISION WITH PLAYER
    public void handleCollisionWithPlayer(Player p){
        if (isVisible())
            p.kill();            
    }

    //ACTIVATE LASERGATE
    public void activate(boolean a){
        if (a == activated)
            return;
        activated = !activated;
        if (activated)
            setAnimation(activatedAnim);
        else
            setAnimation(deactivatedAnim);
    }
}
