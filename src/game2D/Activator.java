package game2D;

import java.util.ArrayList;

public class Activator extends Sprite{

    Animation deactivatedAnim, activatedAnim;
    boolean activated, playerOn, crateOn, onRoof;
    ArrayList<LaserGate> laserGates;

    public Activator(Animation anim) {
        super(anim, 0);
        deactivatedAnim = anim;
        activated = false;
        playerOn = false;
        crateOn = false;
        onRoof = false;
        loadAnimations();
    }

    public void loadAnimations(){
        activatedAnim = new Animation();
        activatedAnim.loadAnimationFromSheet("images/activator/pressed.png", 1, 1, 1000);
    }

    public Activator copy() throws CloneNotSupportedException {
        return (Activator) this.clone();
    }

    public void handleCollisionWithPlayer(Player p){
        playerOn = true;
        // if (!onRoof){
        //     p.setY(getY());
        // }
        // else{
        //     p.setY(getY()-p.getHeight());
        // }
        activate(true);
        }

    public void handleCollisionWithCrate(Crate crate){
            crateOn = true;
            if (onRoof){
                crate.setY(getY());
            }
            else{
                crate.setY(getY()-crate.getHeight());
            }
            activate(true);
        }


    public void activate(boolean a){
        if (a == activated)
            return;
        activated = !activated;
        if (activated && (playerOn || crateOn)){
            setAnimation(activatedAnim);
            for(LaserGate lg: laserGates)
                lg.setVisible(false);
        }
        else{
            setAnimation(deactivatedAnim);
                for(LaserGate lg: laserGates)
                    lg.setVisible(true);
        }   
    }
    public void getLaserGates(ArrayList<LaserGate> laserGateList){
        laserGates = laserGateList;
    }

    public void setOnRoof(boolean r){
        onRoof = r;
        if (onRoof)
            super.setRotation(180);
    }

    public boolean isOnRoof(){
        return onRoof;
    }

    public boolean getPlayerOn(){
        return playerOn;
    }

    public void setPlayerOn(boolean p){
        playerOn = p;
    }

    public boolean getCrateOn(){
        return crateOn;
    }

    public void setCrateOn(boolean c){
        crateOn = c;
    }
}
