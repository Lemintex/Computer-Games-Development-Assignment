package game2D;

public class Coin extends Sprite{

    Animation coinRotate;
    public Coin(Animation anim) {
        super(anim, 0);
        coinRotate = anim;
    }

    public Coin copy() throws CloneNotSupportedException {
        return (Coin) this.clone();
    }

    public boolean hitCoin(char c){
        if (c == 'y' || c == 'x'){
            Sound coinSound = new Sound("sounds/coinCollect.wav", false);
            coinSound.start();
            return true;
        }        
        return false;
    }
}
