package game2D;

public class Coin extends Sprite{

    Animation coinRotate;
    public Coin(Animation anim, float s) {
        super(anim, 0);
        coinRotate = anim;
    }

    public Coin copy() throws CloneNotSupportedException {
        return (Coin) this.clone();
    }
}
