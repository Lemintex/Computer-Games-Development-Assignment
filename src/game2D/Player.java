package game2D;

public class Player extends Sprite {//implements Cloneable{

    Animation playerIdle, playerRunning, playerJumping, playerFalling, playerDying;
    boolean onCrate, respawn, dying, godMode;
    /**
     * Creates a new Sprite object with the specified Animation.
     *
     * @param anim The animation to use for the sprite.
     * @param s
     */
    public Player(Animation anim, float s) {
        super(anim, s);
        playerIdle = anim;
        onCrate = false;
        respawn = false;
        dying = false;
        godMode = false;
    }

    public void loadAnimations(){
        playerRunning = new Animation();
        playerRunning.loadAnimationFromSheet("images/player/run.png", 3, 2, 150);

        playerJumping = new Animation();
        playerJumping.loadAnimationFromSheet("images/player/jump.png", 2, 2, 150);

        playerFalling = new Animation();
        playerFalling.loadAnimationFromSheet("images/player/fall.png", 2, 1, 150);

        playerDying = new Animation();
        playerDying.loadAnimationFromSheet("images/player/die.png", 3, 2, 100);
        // playerDying.setLoop(false);
    }

    public void updateAnimations(float gravity){
        if(dying && super.getAnimation() == playerDying){
            if (playerDying.hasLooped()){
                respawn = true;
                // respawn();
            }
            return;
        }
        if (super.isGrounded() || onCrate) {
            if (super.getVelocityX() == 0){
                super.setAnimation(playerIdle);
        }
        else{
                super.setAnimation(playerRunning);
            }
        }
        else if (Math.signum(gravity) != Math.signum(super.getVelocityY()) && gravity != 0){
            setAnimation(playerJumping);
        }
        else if ((Math.signum(gravity) == Math.signum(getVelocityY()) && gravity != 0 && !super.isGrounded())){
            super.setAnimation(playerFalling);
        }
        else{
            super.setAnimation(playerIdle);
        }
    }

    
    public void jump(float force, float gravity){
        if (onCrate || super.isGrounded()){
            Sound jumpSound = new Sound("sounds/playerJump.wav", false);
            jumpSound.start();
            if (gravity > 0)
                super.setVelocityY(-force);
            else if (gravity < 0)
                super.setVelocityY(force);
            super.setGrounded(false);
            setOnCrate(false);
        }
    }

    public boolean flipGravity(){
        return true;
    }

    public void setVelocityX(float dx){
        if (!dying)
            super.setVelocityX(dx);
    }

    public void setVelocityY(float dy){
        if (!dying)
            super.setVelocityY(dy);
    }

    public void kill(){
        if(!dying && !godMode){
        dying = true;
        Sound killSound = new Sound("sounds/playerDeath.wav", false);
        killSound.start();
        super.setAnimation(playerDying);
        super.setDirection('i');
        super.setVelocity(0, 0);
        super.setGrounded(false);
        // respawn = true;
        }
    }

    public void respawn(){
        super.setPosition(initX, initY);
        playerDying.setLooped(false);
        onCrate = false;
        respawn = false;
        dying = false;
        setGrounded(true);
    }

    public Player copy() throws CloneNotSupportedException {
//        super.clone();
        return (Player) this.clone();
    }

    public boolean isOnCrate(){
        return onCrate;
    }

    public void setOnCrate(boolean c){
        onCrate = c;
    }

    public boolean getRespawn(){
        return respawn;
    }

    public void setGodMode(boolean g){
        godMode = g;
    }
}
