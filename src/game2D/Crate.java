package game2D;

public class Crate extends Sprite{

    Animation crate;
    public Crate(Animation anim, float s) {
        super(anim, 0);
        crate = anim;
    }

    public Crate copy() throws CloneNotSupportedException {
        return (Crate) this.clone();
    }
}
