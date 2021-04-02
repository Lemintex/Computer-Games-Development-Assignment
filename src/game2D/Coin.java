package game2D;
public class Coin extends Sprite{
    boolean coinCollected;
    Animation coinRotate;
    public Coin(Animation anim) {
        super(anim, 0);
        coinRotate = anim;
        coinCollected = false;
    }

    public Coin copy() throws CloneNotSupportedException {
        return (Coin) this.clone();
    }

    public void hitCoin(){
        if (!coinCollected){
            Sound coinSound = new Sound("sounds/coinCollect.wav", false);
            coinSound.start();
        }
    }

    public void handleCollisionWithPlayer(Player p){
        hitCoin();
    }

    public boolean getCoinCollect(){
        if (!coinCollected){
            coinCollected = true;
            return false;
        }
        return true;
    }

    public void respawn(){
        setX(getInitialX());
        setY(getInitialY());
        coinCollected = false;
    }
}
