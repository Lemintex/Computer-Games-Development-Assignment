package game2D;

public class Crate extends Sprite{

    Animation crate;
    public Crate(Animation anim) {
        super(anim, 0);
        crate = anim;
    }

    public Crate copy() throws CloneNotSupportedException {
        return (Crate) this.clone();
    }

    public void handleCollisionWithPlayer(Player p, char c, float g){
        if (c == 'x') {
            if (p.getVelocityX()>0 && p.getX()<getX() && p.getDirection() == 'r'){
                p.setX(getX()-p.getWidth());
                setVelocityX(p.getVelocityX());
            }
            else if (p.getVelocityX()<0 && p.getX()>getX() && p.getDirection() == 'l'){
                p.setX(getX()+getWidth());
                setVelocityX(p.getVelocityX());
            }
            p.setOnCrate(false);
            p.setGrounded(true);
        }
        else if (c == 'y'){
            if (g>0)
                p.setY(getY()-p.getHeight()+1);
            else
                p.setY(getY()+getHeight()-1);
            p.setVelocityY(0);
            p.setOnCrate(true);
            p.setGrounded(false);
        }
    }

    public void stopCrate(Player p){
        if (getVelocityX()!=0){
            super.setVelocityX(0);
        }
    }
}
