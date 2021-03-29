package game2D;

public class Bat extends Sprite{

    Animation batAngry, batDying;
    public Bat(Animation anim, float s) {
        super(anim, 0);
        batAngry = anim;
        loadAnimations();
    }

    public void loadAnimations(){
        batDying = new Animation();
        batDying.loadAnimationFromSheet("images/bat/die.png", 5, 1, 100);
    }

    public Bat copy() throws CloneNotSupportedException {
        return (Bat) this.clone();
    }

    @Override
    public void handleCollisionWithPlayer(Player p, char c, float g) {

    }
}
