//2715375
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.*;
import java.util.LinkedList;

import game2D.*;
import java.awt.event.MouseEvent;

/**
 * @author David Cairns
 * @author 2715375
 */
public class Game extends GameCore {
    // Useful game constants
    static int screenWidth = 1600;
    static int screenHeight = 1024;

    float jump = 0.375f;
    float gravity = 0.0005f;
    int xo = 0, yo = 0;

    // Game resources
    Animation initPlayerAnim, initBatAnim, initCoinAnim, initCrateAnim, initActivatorAnim, initSpikesAnim,
            initLaserAnim, initSlimeAnim;
    ArrayList<Animation> initAnimations = new ArrayList<Animation>();
    Player player;
    ArrayList<Sprite> removeSprites = new ArrayList<Sprite>();
    ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
    ArrayList<LaserGate> laserGateList = new ArrayList<LaserGate>();
    ArrayList<LinkedList<Sprite>> backgroundList = new ArrayList<LinkedList<Sprite>>();
    String[] levelBackgrounds = { "layer07_Sky", "layer06_Rocks", "layer05_Clouds", "layer04_Hills_2",
            "layer03_Hills_1", "layer02_Trees", "layer01_Ground" };
    String[] musicLevel = { "sounds/musicLevel1.wav", "sounds/musicLevel2.wav" };

    boolean menu = true, end = false, boundingBoxes = false, godMode = false, parralax = false;
    TileMap[] tmap; // Our tile map, note that we load it in init()
    String mapFolder = "maps";
    HashMap<Integer, String[]> levels = new HashMap<Integer, String[]>();
    String[] level1Layers = { "level1BackLayer.txt", "level1FrontLayer.txt" };
    String[] level2Layers = { "level2BackLayer.txt", "level2FrontLayer.txt" };
    int total; // The score will be the total time elapsed since a crash
    int numCoins, currentLevel = 1, numberOfLevels = 2;

    Sound music;

    /**
     * The obligatory main method that creates an instance of our class and starts
     * it running
     * 
     * @param args The list of parameters this program might use (ignored)
     */
    public static void main(String[] args) {
        Game gct = new Game();
        gct.init();
        //START WINDOW WITH HEIGHT AND WIDTH
        gct.run(false, screenWidth, screenHeight);
    }

    //INITIALISE LEVEL SPRITEMAP STRINGS
    public void initialise() {
        levels.put(1, level1Layers);
        levels.put(2, level2Layers);
    }

    //DISPLAYS THE MENU WHEN CALLED
    public void mainMenu(Graphics2D g) {
        Rectangle playButton = new Rectangle(screenWidth / 3, screenHeight / 2, 250, 100);
        Image menu = Toolkit.getDefaultToolkit().getImage("images/menu/wolf.png");
        g.setColor(Color.white);
        g.drawImage(menu, 0, 0, null);
        g.draw(playButton);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 60));
        g.drawString("Gravity Dude", screenWidth / 4, screenHeight / 4);
        g.drawString("PLAY", screenWidth / 3 + 50, screenHeight / 2 + 80);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
        g.drawString("Flip gravity with LMB and collect the coins!", 100, 800);
    }

    //DISPLAYS THE END SCREEN WHEN CALLED
    public void endScreen(Graphics2D g) {
        Image menu = Toolkit.getDefaultToolkit().getImage("images/menu/wolf.png");
        g.setColor(Color.white);
        g.drawImage(menu, 0, 0, null);
        g.drawString("Thanks for playing!", screenWidth / 3, screenHeight / 2);
    }

    //LOADS A NEW LEVELS TILEMAPS, MUSIC, AND CLEARS OLD LEVEL SPRITES AND SUCH
    public void loadLevel() {
        removeSprites.clear();
        spriteList.clear();
        laserGateList.clear();
        numCoins = 0;
        total = 0;
        if (numberOfLevels < currentLevel) {
            end = true;
            return;
        }
        music = new Sound(musicLevel[currentLevel - 1], true, false, true);
        music.setStop(false);
        music.start();
        if (gravity < 0) {
            gravity = -gravity;
        }
        int i = 0;

        tmap = new TileMap[levels.get(currentLevel).length];
        //LOAD AND READ EACH TILEMAP
        for (String s : levels.get(currentLevel)) {
            tmap[i] = new TileMap();
            tmap[i].loadMap(mapFolder, s, initAnimations, spriteList);
            i++;
        }

        //COUNT SPECIFIC SPRITES AND DO STUFF WITH THEM
        for (Sprite sprite : spriteList) {
            if (sprite instanceof Coin) {
                numCoins++;
            }
            if (sprite instanceof LaserGate) {
                laserGateList.add((LaserGate) sprite);
            }
            sprite.loadAnimations();
        }

        //THIS IS AN INEFFICIENT SOLUTION BUT I AM TYPING THIS 2 HOURS BEFORE THE DEADLINE AND DON'T WANT TO BREAK ANYTHING
        boolean p = false;
        for (Sprite sprite : spriteList) {
            if (sprite instanceof Activator) {
                ((Activator) sprite).getLaserGates(laserGateList);
            }
            //CHECK FOR PLAYER AND ONLY ONE PLAYER, AND GIVE IT A SPECIAL VARIABLE
            if (!p && sprite instanceof Player) {
                player = (Player) sprite;
                p = true;
            }
        }
    }

    //ONCE WE'RE OFF THE MENU, GET THE GAME GOING
    public void init() {
        initialiseAnimations();
        initialise();
        loadLevel();
        setSize(screenWidth, screenHeight);
        setVisible(true);
        loadAllBackgrounds(backgroundList, levelBackgrounds, "level1/");
    }

    //THESE ARE THE ANIMATIONS FOR ALL THE EXISTING SPRITES
    public void initialiseAnimations() {
        initPlayerAnim = new Animation();
        initPlayerAnim.loadAnimationFromSheet("images/player/idle.png", 2, 2, 250);
        initAnimations.add(initPlayerAnim);

        initBatAnim = new Animation();
        initBatAnim.loadAnimationFromSheet("images/bat/fly.png", 5, 1, 100);
        initAnimations.add(initBatAnim);

        initCoinAnim = new Animation();
        initCoinAnim.loadAnimationFromSheet("images/coin/coin.png", 3, 2, 100);
        initAnimations.add(initCoinAnim);

        initCrateAnim = new Animation();
        initCrateAnim.loadAnimationFromSheet("images/crate/crate.png", 1, 1, 1000);
        initAnimations.add(initCrateAnim);

        initActivatorAnim = new Animation();
        initActivatorAnim.loadAnimationFromSheet("images/activator/released.png", 1, 1, 1000);
        initAnimations.add(initActivatorAnim);

        initSpikesAnim = new Animation();
        initSpikesAnim.loadAnimationFromSheet("images/spikes/spikes.png", 1, 1, 1000);
        initAnimations.add(initSpikesAnim);

        initLaserAnim = new Animation();
        initLaserAnim.loadAnimationFromSheet("images/laser/laser.png", 1, 1, 1000);
        initAnimations.add(initLaserAnim);

        initSlimeAnim = new Animation();
        initSlimeAnim.loadAnimationFromSheet("images/slime/move.png", 2, 2, 200);
        initAnimations.add(initSlimeAnim);
    }

    //DRAWS THE SCREEN
    public void draw(Graphics2D g) {
        //IF FINISHED, DRAW END SCREEN
        if (end) {
            endScreen(g);
        }
        //IF IN THE MENU, DRAW IT
        else if (menu) {
            mainMenu(g);
        }
        //OTHERWISE DRAW THE GAME
        else {
            //CALCULATE OFFSET
            if (player.getX() >= 250 && player.getX() <= (tmap[0].getPixelWidth()) - screenWidth + 250) {
                xo = 250 - (int) player.getX();
            } else if (player.getX() < 250) {
                xo = 0;
            } else if (player.getX() <= (tmap[0].getPixelWidth()) - screenWidth) {
                xo = tmap[0].getPixelWidth();
            }
            //DRAW THE BACKGROUNDS
            drawParallaxSprites(g, backgroundList);

            //DRAW EACH ALIVE SPRITE
            for (Sprite s : spriteList) {
                s.setOffsets(xo, yo);
                s.drawTransformed(g);
                if (boundingBoxes) {
                    s.drawBoundingBox(g);
                    s.drawBoundingCircle(g);
                }
            }

            //DRAW TILEMAPS FROM BACK TO FRONT
            for (TileMap m : tmap) {
                if (m != null) {
                    m.draw(g, xo, yo);
                }
            }
            //DRAW NUMBER OF COINS COLLECTED
            String msg = String.valueOf(total);
            g.setColor(Color.YELLOW);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 60));
            g.drawString(msg, 75, getHeight() - 75);
            ;
        }
    }

    public void respawnAll() {
        //IF THE PLAYER IS READY TO RESPAWN AFTER DYING
        if (player.getRespawn()) {
            total = 0;
            numCoins = 0;
            //UNDEADIFY SPRITES
            for (Sprite s : removeSprites) {
                spriteList.add(s);
            }
            removeSprites.clear();
            //RESPAWN SPRITES AND COUNT COINS
            for (Sprite s : spriteList) {
                s.respawn();
                if (s instanceof Coin) {
                    numCoins++;
                }
            }
        }
    }

    /**
     * Update any sprites and check for collisions
     * 
     * @param elapsed The elapsed time between this call and the previous call of
     *                elapsed
     */
    public void update(long elapsed) {
        if (!menu) {
            if (total == numCoins) {
                music.setStop(true);
                currentLevel++;
                loadLevel();
                return;
            }
            respawnAll();
            for (LinkedList<Sprite> l : backgroundList) {
                for (Sprite b : l) {
                    b.update(elapsed);
                }
            }

            for (Sprite s : spriteList) {
                float gravityConstant = 0;
                s.update(elapsed);
                checkTileCollision(s, tmap[0]);
                if (s instanceof Player) {
                    ((Player) s).updateAnimations(gravity);
                }
                if (s instanceof Slime) {
                    if (((Slime) s).isDead()) {
                        removeSprites.add(s);
                    }
                    if (((Slime) s).isOnRoof()) {
                        gravityConstant = Math.signum(-gravity);
                    } else {
                        gravityConstant = Math.signum(gravity);
                    }
                } else if (s instanceof Activator) {
                    if (((Activator) s).isOnRoof()) {
                        gravityConstant = Math.signum(-gravity);
                    } else {
                        gravityConstant = Math.signum(gravity);
                    }

                } else if (s instanceof Spikes) {
                    if (((Spikes) s).isOnRoof()) {
                        gravityConstant = Math.signum(-gravity);
                    } else {
                        gravityConstant = Math.signum(gravity);
                    }

                } else if (!(s instanceof Bat || s instanceof Coin || (s instanceof Activator && gravity < 0)
                        || s instanceof LaserGate)) {
                    gravityConstant = 1;
                }
                s.setVelocityY(s.getVelocityY() + (gravityConstant * gravity * elapsed));

                //HANDLE EDGE FOR CURRENT SPRITE
                if (handleScreenEdge(s, tmap[0], elapsed)) {
                    if (s instanceof Player) {
                        s.kill();
                    } else {
                        removeSprites.add(s);
                    }
                }
            }
            //HANDLE COLLISION
            for (Sprite s1 : spriteList) {
                if (!(s1 instanceof Player))
                    boundingBoxCollision(player, s1);
                for (Sprite s2 : spriteList) {
                    if (s1 instanceof Activator && s2 instanceof Crate)
                        boundingBoxCollision(s1, s2);
                }
            }
            for (Sprite dead : removeSprites) {
                spriteList.remove(dead);
            }
        }
    }

    /**
     * Checks and handles collisions with the edge of the screen
     * 
     * @param s       The Sprite to check collisions for
     * @param tmap    The tile map to check
     * @param elapsed How much time has gone by since the last call
     */
    public boolean handleScreenEdge(Sprite s, TileMap tmap, long elapsed) {

        //CHECKS IF THE SPRITE HAS FALLEN AND CAN'T GET UP
        if (Float.compare(s.getY() + s.getHeight(), tmap.getPixelHeight()) > 0 || s.getY() < 0) {
            return true;
        }
        return false;
    }

    /**
     * Override of the keyPressed event defined in GameCore to catch our own events
     * 
     * @param e The event that has been generated
     */
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
        case KeyEvent.VK_ESCAPE:
            stop();
            break;
        case KeyEvent.VK_D: {
            player.setDirection('r');
            player.setScaleX(1);
            break;
        }
        case KeyEvent.VK_A: {
            player.setDirection('l');
            player.setScaleX(-1);
            break;
        }
        case KeyEvent.VK_W: {
            player.jump(jump, gravity);
            break;
        }
        case KeyEvent.VK_L: {
            numCoins = total;
            break;
        }
        case KeyEvent.VK_B: {
            boundingBoxes = !boundingBoxes;
            break;
        }
        case KeyEvent.VK_G: {
            godMode = !godMode;
            player.setGodMode(godMode);
            break;
        }
        default:
            break;
        }
    }

    //MOUSE PRESSED EVENTS
    public void mousePressed(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        if (mouseX >= screenWidth / 3 && mouseX <= (screenWidth / 3) + 250 && mouseY >= screenHeight / 2
                && mouseY <= (screenHeight / 2) + 100 && menu) { //IF BUTTON IN MENU IS CLICKED
            menu = false;
        } else if (!end && !menu && player.isGrounded() && !player.isOnCrate()) {
            Sound flipGravity = new Sound("sounds/flipGravity.wav", false, true, false);
            flipGravity.start();
            gravity = -gravity;
            player.setScaleY((float) -player.getScaleY());
            player.setGrounded(false);
        }
    }

    /**
     * Check and handles collisions with the sprite s2 for the given Sprite 's1'.
     * 
     * @param s1 The sprite to check collisions for
     * @param s2 The sprite to check collisions with
     */
    public void boundingBoxCollision(Sprite s1, Sprite s2) {
        boolean overlapX = true, overlapY = true;
        float s1X = s1.getX();
        float s1Y = s1.getY();
        int s1width = s1.getWidth();
        int s1height = s1.getHeight();

        float s2X = s2.getX();
        float s2Y = s2.getY();
        int s2width = s2.getWidth();
        int s2height = s2.getHeight();

        //BASIC BOX COLLISION
        if ((s1X + s1width < s2X || s1X > s2X + s2width))
            overlapX = false;
        if ((s1Y + s1height < s2Y || s1Y > s2Y + s2height))
            overlapY = false;

        //IF BOUNDING BOXES COLLIDE, CHECK MORE PRECISE COLLISION
        if (overlapX && overlapY) {
            char c = 'y';
            if (Math.min(Math.abs(s1Y - (s2Y + s2height)), Math.abs((s1Y + s1height) - s2Y)) > Math
                    .min(Math.abs(s1X - (s2X + s2width)), Math.abs(s1X + s1width - s2X))) {
                c = 'x';
            }
            //IF THE SPRITES COLLIDE
            if (boundingCircleCollision(s1, s2)) {
                if (s1 instanceof Activator && s2 instanceof Crate) {
                    ((Activator) s1).handleCollisionWithCrate((Crate) s2);
                } else {
                    s2.handleCollisionWithPlayer((Player) s1, c, gravity);
                    if (s2 instanceof Coin && !((Coin) s2).getCoinCollect()) {
                        total++;
                        removeSprites.add(s2);
                    }
                }
            }
        }
        //IF THE SPRITES DON'T COLLIDE
        else if (s2 instanceof Crate && s1 instanceof Player) {
            ((Crate) s2).stopCrate((Player) s1);
        } else if (s2 instanceof Activator) {
            if (s1 instanceof Crate) {
                ((Activator) s2).setCrateOn(false);
            } else if (s1 instanceof Player) {
                ((Activator) s2).setPlayerOn(false);
            }
            ((Activator) s2).activate(false);
        }
    }

    //USE FANCY MAFS TO CALCULATE CIRCLE COLLISION
    public boolean boundingCircleCollision(Sprite s1, Sprite s2) {
        double distance = Math.hypot(Math.abs(s1.getX() - s2.getX()), Math.abs(s1.getY() - s2.getY()));
        if (distance <= s1.getRadius() + s2.getRadius()) {
            return true;
        }
        return false;
    }

    /**
     * Check and handles collisions with a tile map for the
     * given sprite 's'. Initial functionality is limited...
     * 
     * @param s			The Sprite to check collisions for
     * @param tmap		The tile map to check 
     */

    public void checkTileCollision(Sprite s, TileMap tmap) {
        //SPRITE INFO
        //IDEALLY THESE ARE ONLY DECLARED WHEN NEEDED BUT I HAVE NO TIME TO CHANGE IT
        char TL, BL, TR, BR;
        float sx = s.getX();
        float sy = s.getY();
        int swidth = s.getWidth();
        int sheight = s.getHeight();
        float sxmid = sx + swidth / 2;
        float symid = sy + sheight / 2;
        
        //TILE INFO
        float tileWidth = tmap.getTileWidth();
        float tileHeight = tmap.getTileHeight();

        //TOP RIGHT
        int TRxtile = (int) ((sx + swidth) / tileWidth);
        int TRytile = (int) (sy / tileHeight);
        int TRXmid = (int) (tmap.getTileXC(TRxtile, TRytile) + tileWidth / 2);
        int TRYmid = (int) (tmap.getTileYC(TRxtile, TRytile) + tileHeight / 2);
        TR = tmap.getTileChar(TRxtile, TRytile);

        //BOTTOM RIGHT
        int BRxtile = (int) ((sx + swidth) / tileWidth);
        int BRytile = (int) ((sy + sheight) / tileHeight);
        int BRXmid = (int) (tmap.getTileXC(BRxtile, BRytile) + tileWidth / 2);
        int BRYmid = (int) (tmap.getTileYC(BRxtile, BRytile) + tileHeight / 2);
        BR = tmap.getTileChar(BRxtile, BRytile);

        //TOP LEFT
        int TLxtile = (int) (sx / tileWidth);
        int TLytile = (int) (sy / tileHeight);
        int TLXmid = (int) (tmap.getTileXC(TLxtile, TLytile) + tileWidth / 2);
        int TLYmid = (int) (tmap.getTileYC(TLxtile, TLytile) + tileHeight / 2);
        TL = tmap.getTileChar(TLxtile, TLytile);

        //BOTTOM LEFT
        int BLxtile = (int) (sx / tileWidth);
        int BLytile = (int) ((sy + sheight) / tileHeight);
        int BLXmid = (int) (tmap.getTileXC(BLxtile, BLytile) + tileWidth / 2);
        int BLYmid = (int) (tmap.getTileYC(BLxtile, BLytile) + tileHeight / 2);
        BL = tmap.getTileChar(BLxtile, BLytile);
        boolean leftWall = false, rightWall = false, topFloor = false, bottomFloor = false;
            if (((TR != '.' && Math.abs(TRXmid - sxmid) >= Math.abs(TRYmid - symid) && TL == '.')
                    || (BR != '.' && Math.abs(BRXmid - sxmid) >= Math.abs(BRYmid - symid) && BL == '.'))) {
                rightWall = true;
                if (s.getDirection() == 'r'){
                    s.setVelocityX(0);
                    s.setDirection('i');
                    }
                s.setX(tmap.getTileXC(TRxtile, TRytile) - s.getWidth());
            }
            else if (s.getDirection() == 'r') {
                s.setVelocityX(s.getSpeed());
            }
        
            if (((TL != '.' && Math.abs(TLXmid - sxmid) >= Math.abs(TLYmid - symid) && TR == '.')
                    || (BL != '.' && Math.abs(BLXmid - sxmid) >= Math.abs(BLYmid - symid) && BR == '.'))) {
                leftWall = true;
                if (s.getDirection() == 'l')
                s.setVelocityX(0);
            s.setX(tmap.getTileXC(TLxtile, TLytile) + tileWidth - 1);
        }
    
        else if (s.getDirection() == 'l'){
            s.setVelocityX(-s.getSpeed());
        }
    
    if (BL!='.'||BR!='.'){
        if (((BL != '.' && !leftWall) || (BR != '.' && !rightWall)) && s.getVelocityY() > 0) {
            bottomFloor = true;
            if (gravity > 0 || (s instanceof Slime)){
                s.setGrounded(true);
                if (s instanceof Player){
                    ((Player)s).setOnCrate(false);
                }
            }
            s.setVelocityY(0);
            s.setY(tmap.getTileYC(BLxtile, BLytile) - s.getHeight());

        }
    }
    if(TL!='.'||TR!='.'){
         if (((TL != '.' && !leftWall) || (TR != '.' && !rightWall)) && s.getVelocityY() < 0) {
            topFloor = true;
            if (gravity < 0 || (s instanceof Slime)){
                s.setGrounded(true);
                if (s instanceof Player){
                    ((Player)s).setOnCrate(false);
                }
            }
            s.setVelocityY(0);
            s.setY(tmap.getTileYC(TLxtile, TLytile) + tileHeight);
        }
    }
        else if(!(bottomFloor || topFloor))
        s.setGrounded(false);
        Boolean leftContact = (BL!='.' || TL!='.'), rightContact = (BR!='.' || TR!='.');
        if (leftContact != rightContact || (rightWall||leftWall)){
            s.move(true);
        }
        else{
            s.move(false);
        }
    }


    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
        case KeyEvent.VK_ESCAPE:
            stop();
            break;
        case KeyEvent.VK_W: {
            break;
        }
        case KeyEvent.VK_D: {
            if (player.getDirection() == 'r') {
                player.setDirection('i');
                player.setVelocityX(0f);
            }
        }
        case KeyEvent.VK_A: {
            if (player.getDirection() == 'l') {
                player.setDirection('i');
                player.setVelocityX(0f);
            }
            break;
        }
        default:
            break;
        }
    }

    //LOAD ALL BACKGROUND
    public void loadAllBackgrounds(ArrayList<LinkedList<Sprite>> bgList, String[] filenames, String directory) {
        float bgSpeed = 0;
        for (String s : filenames) {
            bgList.add(loadBackgrounds("backgrounds/" + directory + s + ".png", bgSpeed));
            bgSpeed++;
        }
    }

    //LOAD BACKGROUNDS
    public LinkedList<Sprite> loadBackgrounds(String path, float bgSpeed) {
        Animation bg = new Animation();
        bg.addFrame(loadImage(path), 1000);
        LinkedList<Sprite> bgList = new LinkedList<>();
        for (int j = 0; j < 3; j++) {
            Sprite s = new Sprite(bg, bgSpeed / 100);
            s.setX(0 + j * s.getWidth());
            s.setY(0);
            s.setVisible(true);
            bgList.add(s);
        }
        return bgList;
    }

    //DRAW PARRALAX
    public void drawParallaxSprites(Graphics2D g, ArrayList<LinkedList<Sprite>> spriteLists) {
        int iterator = 1;
        boolean swapL = false, swapR = false;
        float backgroundSpeed = 0f;
        if (player.getVelocityX() < 0) {
            backgroundSpeed = 0.01f;
        } else if (player.getVelocityX() > 0) {
            backgroundSpeed = -0.01f;
        }
        for (LinkedList<Sprite> l : spriteLists) {
            for (Sprite s : l) {
                s.setVelocityX(backgroundSpeed * iterator);
                if (s.getX() + s.getWidth() < 0) {
                    s.setX(l.getLast().getX() + l.getLast().getWidth());
                    swapR = true;
                } else if (s.getX() - s.getWidth() > screenWidth) {
                    s.setX(l.getFirst().getX() - l.getFirst().getWidth());
                    swapL = true;
                }
                s.draw(g);
            }
            iterator++;
            if (swapR) {
                l.add(l.pop());
                swapR = false;
            } else if (swapL) {
                l.add(0, l.removeLast());
                swapL = false;
            }
        }
        iterator++;

    }
}