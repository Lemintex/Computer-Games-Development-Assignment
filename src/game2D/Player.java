package game2D;

public class Player extends Sprite{

    Animation playerIdle, playerRunning, playerJumping, playerFalling, playerDying;
    /**
     * Creates a new Sprite object with the specified Animation.
     *
     * @param anim The animation to use for the sprite.
     * @param s
     */
    public Player(Animation anim, float s) {
        super(anim, s);
        playerIdle = anim;
    }

    public void loadAnimations(){

        playerRunning = new Animation();
        playerRunning.loadAnimationFromSheet("images/player/run.png", 3, 2, 150);

        playerJumping = new Animation();
        playerJumping.loadAnimationFromSheet("images/player/jump.png", 2, 2, 150);
        playerJumping.setLoop(false);

        playerFalling = new Animation();
        playerFalling.loadAnimationFromSheet("images/player/fall.png", 2, 1, 150);

        playerDying = new Animation();
        playerDying.loadAnimationFromSheet("images/player/die.png", 3, 2, 100);
    }

    public void updateAnimations(float gravity){
        if (super.isGrounded()) {
            if (super.getVelocityX() == 0)
                super.setAnimation(playerIdle);
            else
                super.setAnimation(playerRunning);
        }
        else if (Math.signum(gravity) != Math.signum(super.getVelocityY()) && gravity != 0)
            super.setAnimation(playerJumping);
        else if ((Math.signum(gravity) == Math.signum(getVelocityY()) && gravity != 0 && !super.isGrounded()))
            super.setAnimation(playerFalling);
    }

    public void kill(){
        super.setAnimation(playerDying);
        super.setVelocityX(0);
    }
}