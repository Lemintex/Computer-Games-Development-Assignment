
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import java.awt.*;
import java.util.LinkedList;


import game2D.*;

// Game demonstrates how we can override the GameCore class
// to create our own 'game'. We usually need to implement at
// least 'draw' and 'update' (not including any local event handling)
// to begin the process. You should also add code to the 'init'
// method that will initialise event handlers etc. By default GameCore
// will handle the 'Escape' key to quit the game but you should
// override this with your own event handler.

/**
 * @author David Cairns
 *
 */
public class Game extends GameCore {
	// Useful game constants
	static int screenWidth = 1024;
	static int screenHeight = 768;

	float jump = 0.375f;
    float gravity = 0.0005f;
    int xo = 0, yo = 0;

    // Game resources
    Animation initPlayerAnim, initBatAnim, initCoinAnim, initCrateAnim, initActivatorAnim, initSpikesAnim, initLaserAnim, initSlimeAnim;
    ArrayList<Animation> initAnimations = new ArrayList<Animation>();
    Player player;
    ArrayList<Sprite> removeSprites = new ArrayList<Sprite>();
    ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
    ArrayList<LaserGate> laserGateList = new ArrayList<LaserGate>();
    ArrayList<LinkedList<Sprite>> backgroundList = new ArrayList<LinkedList<Sprite>>();   
    String[] level1Backgrounds = {"layer07_Sky", "layer06_Rocks", "layer05_Clouds", "layer04_Hills_2", "layer03_Hills_1", "layer02_Trees", "layer01_Ground"};
    String[] musicLevel = {"sounds/music.wav"};

    TileMap tmap = new TileMap();	// Our tile map, note that we load it in init()
    String mapFolder = "maps";
    String[] mapNames = {"level1.txt", "level2.txt"};
    long total;         			// The score will be the total time elapsed since a crash
    int currentLevel = 1;

    /**
	 * The obligatory main method that creates
     * an instance of our class and starts it running
     * 
     * @param args	The list of parameters this program might use (ignored)
     */
    public static void main(String[] args) {
        Game gct = new Game();
        gct.init();
        // Start in windowed mode with the given screen height and width
        gct.run(false,screenWidth,screenHeight);
    }

    /**
     * Initialise the class, e.g. set up variables, load images,
     * create animations, register event handlers
     */

    public void loadLevel(){
        removeSprites.clear();
        spriteList.clear();
        tmap.loadMap(mapFolder, mapNames[currentLevel - 1], initAnimations, spriteList);
        for (Sprite sprite: spriteList){
            if(sprite instanceof LaserGate){
                laserGateList.add((LaserGate)sprite);
            }
        }

        boolean p = false;
        for (Sprite sprite: spriteList){
            if(sprite instanceof Activator){
                Activator a;
                a = (Activator) sprite;
                a.getLaserGates(laserGateList);
            }
            if (!p && sprite instanceof Player) {
                player = (Player) sprite;
                p = true;
                break;
            }
        }
    }

    public void init()
    {         
        initialiseAnimations();
        // Load the tile map and print it out so we can check it is valid
        // tmap.loadMap("maps", "map.txt", initAnimations, spriteList);
        loadLevel();
        Sound music = new Sound(musicLevel[0], true);
        // music.start();
        
        setSize(tmap.getPixelWidth()/4, tmap.getPixelHeight());
        setVisible(true);
//        if (spriteList.contains(new Player))

        for (Sprite sprite: spriteList)
            sprite.loadAnimations();

        // Load a single cloud animation
        Animation ca = new Animation();
        ca.addFrame(loadImage("images/cloud.png"), 1000);

        loadAllBackgrounds(backgroundList, level1Backgrounds, "level1/");
        initialiseGame();
      		
        System.out.println(tmap);
    }

    public void musicPlay(String filename){

    }

    public void initialiseAnimations(){
        initPlayerAnim = new Animation();
        initPlayerAnim.loadAnimationFromSheet("images/player/idle.png", 2, 2, 250);
        initAnimations.add(initPlayerAnim);

        initBatAnim = new Animation();
        initBatAnim.loadAnimationFromSheet("images/bat/fly.png", 5, 1, 100);
        initAnimations.add(initBatAnim);

        initCoinAnim = new Animation();
        initCoinAnim.loadAnimationFromSheet("images/coin/coin.png", 3, 2, 250);
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

    /**
     * You will probably want to put code to restart a game in
     * a separate method so that you can call it to restart
     * the game.
     */
    public void initialiseGame()
    {
    	total = 0;
//        player.show();
    }
    
    /**
     * Draw the current state of the game
     */
    public void draw(Graphics2D g)
    {    	
    	// Be careful about the order in which you draw objects - you
    	// should draw the background first, then work your way 'forward'

    	// First work out how much we need to shift the view 
    	// in order to see where the player is.

        // If relative, adjust the offset so that
        // it is relative to the player
        // ...?
        if (player.getX() >= 250){
            xo = 250 - (int)player.getX();
        }
        else
        {
            xo = 0;
        }
        drawParallaxSprites(g, backgroundList);
//        g.setColor(Color.white);
//        g.fillRect(0, 0, getWidth(), getHeight());

        // Apply offsets to sprites then draw them
        for (Sprite s: spriteList)
        {
        	s.setOffsets(xo,yo);
        	s.drawTransformed(g);
            s.drawBoundingBox(g);
            s.drawBoundingCircle(g);
        }

        // Apply offsets to player and draw 
        player.setOffsets(xo, yo);
        player.drawTransformed(g);

        // Apply offsets to tile map and draw it
        tmap.draw(g,xo,yo);    
        
        // Show score and status information
        String msg = String.valueOf(total);
        g.setColor(Color.YELLOW);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 60));
        g.drawString(msg, 75, getHeight()-75);;
    }

    public void respawnAll(){
        if (player.getRespawn()){                   
            for (Sprite s: removeSprites){
                spriteList.add(s);
            }
            removeSprites.clear();
            for (Sprite s: spriteList){
                if (!(s instanceof Player)){
                    s.setPosition(s.getInitialX(), s.getInitialY());
                }
            }
        }
    }

    /**
     * Update any sprites and check for collisions
     * 
     * @param elapsed The elapsed time between this call and the previous call of elapsed
     */    
    public void update(long elapsed) {
        
        for (LinkedList<Sprite> l : backgroundList) {
            for (Sprite s : l){
                s.update(elapsed);
            }
        }
            
        for (Sprite s: spriteList) {
            float gravityConstant = 0;
            s.update(elapsed);
            checkTileCollision(s, tmap);
            if (s instanceof Player){
                ((Player)s).updateAnimations(gravity);
            }
            if (s instanceof Slime){
                if (((Slime)s).isDead()){
                    removeSprites.add(s);
                }
                if (((Slime)s).isOnRoof()){
                    gravityConstant = Math.signum(-gravity);
                }
                else{
                    gravityConstant = Math.signum(gravity);
                }
            }
            else if(s instanceof Spikes){
                if (((Spikes)s).isOnRoof()){
                    gravityConstant = Math.signum(-gravity);
                }
                else{
                    gravityConstant = Math.signum(gravity);
                }
            } 
            else if (!(s instanceof Bat || (s instanceof Activator && gravity<0) || s instanceof LaserGate)){
                gravityConstant = 1;
            }
            s.setVelocityY(s.getVelocityY() + (gravityConstant * gravity * elapsed));  
                
            // Then check for any collisions that may have occurred
            if (handleScreenEdge(s, tmap, elapsed)){
                if (s instanceof Player){
                    s.kill();
                }
                else{
                    removeSprites.add(s);
                }
            }
        }

        // Then check for any collisions that may have occurred
        // if (handleScreenEdge(player, tmap, elapsed);


        for (Sprite s1 : spriteList) {
            if (!(s1 instanceof Player))
                boundingBoxCollision(player, s1);
            for(Sprite s2 : spriteList){
                if (s1 instanceof Activator && s2 instanceof Crate)
                    boundingBoxCollision(s1, s2);
            }                
        }
        
        for (Sprite dead: removeSprites){
            spriteList.remove(dead);
        }
    }
    
    /**
     * Checks and handles collisions with the edge of the screen
     * 
     * @param s			The Sprite to check collisions for
     * @param tmap		The tile map to check 
     * @param elapsed	How much time has gone by since the last call
     */
    public boolean handleScreenEdge(Sprite s, TileMap tmap, long elapsed)
    {
    	// This method just checks if the sprite has gone off the bottom screen.
    	// Ideally you should use tile collision instead of this approach
    	
        if (Float.compare(s.getY() + s.getHeight(), tmap.getPixelHeight())>0 || s.getY()<0)
        {
            return true;
        }
        return false;
    }
    
    
     
    /**
     * Override of the keyPressed event defined in GameCore to catch our
     * own events
     * 
     *  @param e The event that has been generated
     */
    public void keyPressed(KeyEvent e)
    {
    	int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_ESCAPE:
                stop();
                break;
            case KeyEvent.VK_D: {
                player.setDirection('r');
//                player.setVelocityX(0.25f);
                player.setScaleX(1);
                break;
            }
            case KeyEvent.VK_A: {
                player.setDirection('l');
//                player.setVelocityX(-0.25f);
                player.setScaleX(-1);
                break;
            }
            case KeyEvent.VK_W: {
                player.jump(jump, gravity);
                break;
            }
            case KeyEvent.VK_SPACE: {
                if (player.isGrounded() && !player.isOnCrate()) {
                    gravity = -gravity;
                    player.setScaleY((float) -player.getScaleY());
                    player.setGrounded(false);
                }
                break;
            }
            case KeyEvent.VK_R: {
                initialiseGame();
                break;
            }
            default:
                break;
        }
    }

    /**
     * Check and handles collisions with the sprite s2 for
     * the given Sprite 's1'.
     * @param s1        The sprite to check collisions for
     * @param s2        The sprite to check collisions with
     */
    public void boundingBoxCollision(Sprite s1, Sprite s2)
    {

        boolean overlapX = true, overlapY = true;
        float s1X = s1.getX();
        float s1Y = s1.getY();
        int s1width = s1.getWidth();
        int s1height = s1.getHeight();

        float s2X = s2.getX();
        float s2Y = s2.getY();
        int s2width = s2.getWidth();
        int s2height = s2.getHeight();

        if ((s1X + s1width < s2X || s1X > s2X + s2width))
            overlapX = false;
        if ((s1Y + s1height < s2Y || s1Y > s2Y + s2height))
            overlapY = false;
        if (overlapX && overlapY){
            char c = 'y';
            if (Math.min(Math.abs(s1Y-(s2Y+s2height)), Math.abs((s1Y+s1height)-s2Y))>Math.min(Math.abs(s1X-(s2X+s2width)), Math.abs(s1X+s1width-s2X))){
                c = 'x';
            }
            else {
                c = 'y';
            }
            if (s2 instanceof Crate || s2 instanceof LaserGate || boundingCircleCollision(s1, s2)){
                if (s1 instanceof Activator && s2 instanceof Crate){
                    s1.handleCollisionWithCrate((Crate)s2, c, gravity);
                }
                else if (s1 instanceof Player){
                    if (s2 instanceof Coin){
                        Coin coin = (Coin) s2;
                        if(coin.hitCoin(c)){
                            total++;
                            removeSprites.add(coin);
                            coin.setVisible(false);
                        }
                    }
                    else{
                        s2.handleCollisionWithPlayer((Player)s1, c, gravity);
                    }
                }
            }
        }
        else if(s2 instanceof Crate && s1 instanceof Player && s1.getVelocityX() == 0){
            ((Crate)s2).stopCrate((Player)s1);
        }
    }
    
    public boolean boundingCircleCollision(Sprite s1, Sprite s2){
        double distance = Math.hypot(Math.abs(s1.getX() - s2.getX()), Math.abs(s1.getY() - s2.getY()));
        // double distance = s1.getRadius() + s2.a
        if (distance <= s1.getRadius() + s2.getRadius()){
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
        char TL, BL, TR, BR;
        float sx = s.getX();
        float sy = s.getY();
        int swidth = s.getWidth();
        int sheight = s.getHeight();
        float sxmid = sx + swidth / 2;
        float symid = sy + sheight / 2;

        // Find out how wide and how tall a tile is
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
        if (((TR != '.' && Math.abs(TRXmid - sxmid) >= Math.abs(TRYmid - symid) && TL == '.') || (BR != '.' && Math.abs(BRXmid - sxmid) >= Math.abs(BRYmid - symid) && BL == '.'))){
            rightWall = true;
            if (s.getDirection() == 'r')
                s.setVelocityX(0);
            s.setX(tmap.getTileXC(TRxtile, TRytile) - s.getWidth());
        }
        else if (s.getDirection() == 'r'){
            s.setVelocityX(s.getSpeed());
        }

        if (((TL != '.' && Math.abs(TLXmid - sxmid) >= Math.abs(TLYmid - symid) && TR == '.') || (BL != '.' && Math.abs(BLXmid - sxmid) >= Math.abs(BLYmid - symid) && BR == '.'))){
            leftWall = true;
            if (s.getDirection() == 'l')
                s.setVelocityX(0);
            s.setX(tmap.getTileXC(TLxtile, TLytile) + tileWidth - 1);
        }
        else if (s.getDirection() == 'l'){
            s.setVelocityX(-s.getSpeed());
        }

        if (((BL != '.'/* && Math.abs(BLYmid - symid) >= Math.abs(BLXmid-sxmid)*/ && !leftWall) || (BR != '.'/* && Math.abs(BRYmid-symid) >= Math.abs(BLXmid-sxmid)*/ && !rightWall)) && s.getVelocityY() > 0) {
            bottomFloor = true;
            if (gravity > 0 || (s instanceof Slime)){
                s.setGrounded(true);
            }
            s.setVelocityY(0);
            s.setY(tmap.getTileYC(BLxtile, BLytile) - s.getHeight());

        }
        else if (((TL != '.'/* && Math.abs(TLYmid - symid) > Math.abs(TLXmid - sxmid)*/ && !leftWall) || (TR != '.'/* && Math.abs(TRYmid - symid) > Math.abs(TRXmid - sxmid)*/ && !rightWall)) && s.getVelocityY() < 0) {
            topFloor = true;
            if (gravity < 0 || (s instanceof Slime)){
                s.setGrounded(true);
            }
            s.setVelocityY(0);
            s.setY(tmap.getTileYC(TLxtile, TLytile) + tileHeight);
        }
        else{
            s.setGrounded(false);
        }
        Boolean onLeftEdge = (BL!='.' || TL!='.'), onRightEdge = (BR!='.' || TR!='.');
        char edge = 'n';
        if ((onLeftEdge && !onRightEdge) || rightWall){
            edge = 'r';
        }
        else if ((onRightEdge && !onLeftEdge) || leftWall){
            edge = 'l';
        }
        s.move(edge);
    }

	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		// Switch statement instead of lots of ifs...
		// Need to use break to prevent fall through.
		switch (key)
		{
			case KeyEvent.VK_ESCAPE: stop(); break;
			case KeyEvent.VK_W: {
                break;
            }
            case KeyEvent.VK_D:{
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
            default :  break;
		}
	}

    public void loadAllBackgrounds(ArrayList<LinkedList<Sprite>> bgList, String[] filenames, String directory) {
        float bgSpeed = 0;
        for (String s : filenames) {
            bgList.add(loadBackgrounds("backgrounds/" + directory + s + ".png", bgSpeed));
            bgSpeed++;
        }
    }

    public LinkedList<Sprite> loadBackgrounds(String path, float bgSpeed){
            Animation bg = new Animation();
            bg.addFrame(loadImage(path), 1000);
            LinkedList<Sprite> bgList = new LinkedList<>();
            for (int j = 0; j < 3; j++){
                Sprite s = new Sprite(bg, bgSpeed/100);
                s.setX(0+j*s.getWidth());
                s.setY(0);
                s.setVisible(true);
                bgList.add(s);
            }
            return bgList;
    }

    public void drawParallaxSprites(Graphics2D g, ArrayList<LinkedList<Sprite>> spriteLists){
        int iterator = 1;
        boolean swapL = false, swapR = false;
        float backgroundSpeed = 0f;
        if (player.getVelocityX()<0){
            backgroundSpeed = 0.01f;
        }
        else if (player.getVelocityX()>0){
            backgroundSpeed = -0.01f;
        }
        for (LinkedList<Sprite> l: spriteLists){
            for (Sprite s: l){
                s.setVelocityX(backgroundSpeed*iterator);
                if (s.getX()+s.getWidth()<0){
                    s.setX(l.getLast().getX()+l.getLast().getWidth());
                    swapR = true;
                }
                else if (s.getX()-s.getWidth()>screenWidth){
                    s.setX(l.getFirst().getX()-l.getFirst().getWidth());
                    swapL = true;
                }
                s.draw(g);
            }
            iterator++;
            if (swapR) {
                l.add(l.pop());
                swapR = false;
            }
            else if (swapL){
                l.add(0, l.removeLast());
                swapL = false;
            }
        }
        iterator++;

    }
}