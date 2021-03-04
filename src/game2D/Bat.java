package game2D;

public class Bat extends Sprite{

    Animation batNeutral, batAngry, batDying;
    public Bat(Animation anim, float s) {
        super(anim, 0);
        batAngry = anim;
    }
}
