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

    public LaserGate copy() throws CloneNotSupportedException {
        return (LaserGate) this.clone();
    }

    public void handleCollisionWithPlayer(Player p, char c, float g){
        if ((c == 'y' || c == 'x') && super.isVisible())
            p.kill();            
    }

    public void activate(boolean a){
        if (a == activated)
            return;
        activated = !activated;
        if (activated)
            super.setAnimation(activatedAnim);
        else
            super.setAnimation(deactivatedAnim);
    }
}
