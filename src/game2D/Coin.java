//2715375
package game2D;
public class Coin extends Sprite{
    boolean coinCollected;
    Animation coinRotate;
    public Coin(Animation anim) {
        super(anim, 0);
        coinRotate = anim;
        coinCollected = false;
    }

    //RETURN COPY OF COIN
    public Coin copy() throws CloneNotSupportedException {
        return (Coin) this.clone();
    }

    //COIN COLLECTED
    public void hitCoin(){
        if (!coinCollected){
            Sound coinSound = new Sound("sounds/coinCollect.wav", false, true, false);
            coinSound.start();
        }
    }

    //HANDLES COLLISION WITH PLAYER
    public void handleCollisionWithPlayer(Player p){
        hitCoin();
    }

    //RETURNS IS COIN COLLECTED
    public boolean getCoinCollect(){
        if (!coinCollected){
            coinCollected = true;
            return false;
        }
        return true;
    }

    //RESPAWN COIN
    public void respawn(){
        setX(getInitialX());
        setY(getInitialY());
        coinCollected = false;
    }
}
