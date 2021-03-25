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
        loadAnimations();
    }

    public void loadAnimations(){
        activatedAnim = new Animation();
        activatedAnim.loadAnimationFromSheet("images/activator/pressed.png", 1, 1, 1000);
    }

    public LaserGate copy() throws CloneNotSupportedException {
        return (LaserGate) this.clone();
    }

    public void handleCollisionWithPlayer(Sprite p, char c, float g){
        if (c == 'y' || c == 'x')
            p.kill();            
        }
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
