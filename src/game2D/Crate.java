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

    public void handleCollisionWithPlayer(Sprite p, char c, float g){
        if (c == 'x') {
            super.setVelocityX(p.getVelocityX());
        }
        else if (c == 'y'){
            if (g>0)
                p.setY(super.getY()-p.getHeight());
            else
                p.setY(super.getY()+super.getHeight());
            p.setVelocityY(0);
            p.setGrounded(true);
            p.setOnCrate(true);
        }
        else if (c == 'n'){
            super.setVelocityX(0);
        }
    }
}
