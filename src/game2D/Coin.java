package game2D;

import java.util.ArrayList;

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
            return true;
        }        
        return false;
    }
}
