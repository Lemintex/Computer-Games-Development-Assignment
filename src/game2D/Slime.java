package game2D;

public class Slime extends Sprite{

    Animation slimeMove, slimeDie;
    boolean onRoof, dead, respawned;
    char memory;
    public Slime(Animation anim) {
        super(anim, 0.1f);
        slimeMove = anim;
        memory = 'a';
        respawned = true;
        load();
    }

    public void load(){
        loadAnimations();
        initialiseMovement();
    }

    public void loadAnimations(){
        slimeDie = new Animation();
        slimeDie.loadAnimationFromSheet("images/slime/die.png", 2, 2, 200);
    }

    public void initialiseMovement(){
        setVelocityX(getSpeed());
    }

    public boolean isDead(){
        if (slimeDie.hasLooped()){
            resetSlime();
            initialiseMovement();
            return true;
        }
        return false;
    }

    public void resetSlime(){
        dead = false;
        slimeDie.setLooped(false);
        setPosition(getInitialX(), getInitialY());
        setAnimation(slimeMove);
        setVelocityX(0);
        if (onRoof){
            setGrounded(true);
        }
        else {
            setGrounded(false);
        }
    }

    public Slime copy() throws CloneNotSupportedException {
        return (Slime) this.clone();
    }

    public void handleCollisionWithPlayer(Player p, char c, float g) {
        if (c == 'y' && (Math.signum(g) == Math.signum(p.getVelocityY()) && !dead && respawned)){
            kill();
            p.setVelocityY(g*-500);
        }
        else if (!((c == 'n') || dead))
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

    public void kill(){
        setVelocityX(0);
        dead = true;
        setGrounded(false);
        respawned = false;
        setAnimation(slimeDie);
    }

    public void setOnRoof(boolean r){
        onRoof = r;
        if(onRoof){
            setRotation(180);
        }
    }

    public boolean isOnRoof(){
        return onRoof;
    }

    public void setVelocityX(float dx){
        if (!dead && (isGrounded() || onRoof)){
            respawned = true;
            super.setVelocityX(dx);
        }
    }

    public void setGrounded(boolean g){
        super.setGrounded(g);
    }
}