package game2D;

import java.util.ArrayList;

public class Activator extends Sprite{

    Animation deactivatedAnim, activatedAnim;
    boolean activated, playerOn, crateOn;
    boolean toggleActivate;
    ArrayList<LaserGate> laserGates;

    public Activator(Animation anim) {
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

    public Activator copy() throws CloneNotSupportedException {
        return (Activator) this.clone();
    }

    public void handleCollisionWithPlayer(Sprite p, char c, float g){
        if (c == 'y' && g>0){
            playerOn = true;
            p.setY(super.getY()-p.getHeight()+1);
            p.setVelocityY(0);
            activate(true);
            toggleActivate = false;
            p.setGrounded(true);
        }
        else if (c == 'n'){
            playerOn = false;
            if (!crateOn){
                activate(false);
                toggleActivate = true;
            }
            
        }
    }

    public void handleCollisionWithCrate(Sprite crate, char c, float g){
        if (c == 'y' && g>0){
            crateOn = true;
            crate.setY(super.getY()-crate.getHeight()+1);
            crate.setVelocityY(0);
            activate(true);
            toggleActivate = false;
            crate.setGrounded(true);
        }
        else if (c == 'n'){
            crateOn = false;
            if (!playerOn){
                toggleActivate = true;
                activate(false);
            }
        }
    }

    public void activate(boolean a){
        if (a == toggleActivate)
            return;
        activated = !activated;
        if (activated){
            super.setAnimation(activatedAnim);
            for(LaserGate lg: laserGates)
                lg.setVisible(false);
        }
        else{
            super.setAnimation(deactivatedAnim);
            for(LaserGate lg: laserGates)
                lg.setVisible(true);
        }
    }   

    public void getLaserGates(ArrayList<LaserGate> laserGateList){
        laserGates = laserGateList;        
    }
}
