package game2D;

public class Slime extends Sprite{

    Animation slimeMove, slimeDie;
    boolean onRoof, dying;
    char memory;
    public Slime(Animation anim) {
        super(anim, 0.1f);
        slimeMove = anim;
        memory = 'a';
        loadAnimations();
    }

    public void loadAnimations(){
        initialiseMovement();
        slimeDie = new Animation();
        slimeDie.loadAnimationFromSheet("images/slime/die.png", 2, 2, 200);
    }

    public void initialiseMovement(){
        if (memory == 'r'){
            setVelocityX(-super.getSpeed());
        }
        else{
            setVelocityX(super.getSpeed());
        }
    }
    public void updateAnimations(){
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
        dying = false;
        slimeDie.setLooped(false);
        super.setAnimation(slimeMove);
    }

    public Slime copy() throws CloneNotSupportedException {
        return (Slime) this.clone();
    }

    public void handleCollisionWithPlayer(Player p, char c, float g) {
        if (c == 'y' && (Math.signum(g) == Math.signum(p.getVelocityY()))){
            kill();
            p.setVelocityY(g*-500);
        }
        else if (!((c == 'n') || dying))
            p.kill();
    }
    public void move(char edge){
        if (super.getVelocityX() == 0){
            setVelocityX(super.getSpeed());
        }
        if (!(edge == 'n' || edge == memory || dying)){
            super.setDirection(memory);
            memory = edge;
            setVelocityX(-super.getVelocityX());
            super.setScaleX((float)-super.getScaleX());
        }
    }

    public void kill(){
        dying = true;
        setVelocityX(0);
        super.setAnimation(slimeDie);
    }

    public void setOnRoof(boolean r){
        onRoof = r;
        if(onRoof){
            super.setRotation(180);
        }
    }

    public boolean isOnRoof(){
        return onRoof;
    }

    public void setVelocityX(float dx){
        if (!dying || super.isGrounded())
            super.setVelocityX(dx);
    }

}