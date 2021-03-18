package game2D;

public class Bat extends Sprite{

    Animation batNeutral, batAngry, batDying;
    public Bat(Animation anim, float s) {
        super(anim, 0);
        batAngry = anim;
    }

    public Bat clone(){
        Bat b = new Bat(batAngry, 0);

        return b;
    }
}
