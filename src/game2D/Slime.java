package game2D;

public class Slime extends Sprite{

    Animation slimeIdle, slimeMove, slimeDie;
    public Slime(Animation anim) {
        super(anim, 0);
        slimeIdle = anim;
        loadAnimations();
    }

    public void loadAnimations(){
        slimeMove = new Animation();
        slimeMove.loadAnimationFromSheet("images/slime/move.png", 2, 2, 200);
        
        slimeDie = new Animation();
        slimeDie.loadAnimationFromSheet("images/slime/die.png", 2, 2, 200);
    }

    public Slime copy() throws CloneNotSupportedException {
        return (Slime) this.clone();
    }

    public void handleCollisionWithPlayer(Player p, char c, float g) {
        if (c == 'y')
            kill();
        else if (c == 'x')
            p.kill();
    }

    public void kill(){

    }
}