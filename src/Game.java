
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
    Animation initPlayerAnim, initBatAnim, initCoinAnim, initCrateAnim;
    ArrayList<Animation> initAnimations = new ArrayList<Animation>();
    Player player = null;
    ArrayList<Sprite> clouds = new ArrayList<Sprite>();
    ArrayList<Sprite> spriteList = new ArrayList<>();
    ArrayList<LinkedList> backgroundList = new ArrayList<>();

    TileMap tmap = new TileMap();	// Our tile map, note that we load it in init()
    
    long total;         			// The score will be the total time elapsed since a crash
    String[] level1Backgrounds = {"layer07_Sky", "layer06_Rocks", "layer05_Clouds", "layer04_Hills_2", "layer03_Hills_1", "layer02_Trees", "layer01_Ground"};

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
    public void init()
    {         
        Sprite s;	// Temporary reference to a sprite
        initialiseAnimations();
        // Load the tile map and print it out so we can check it is valid
        tmap.loadMap("maps", "map.txt", initAnimations, spriteList);

        for (Sprite sprite: spriteList)
            if (sprite instanceof Player) {
                player = (Player) sprite;
//                spriteList.remove(sprite);
                break;
            }
        setSize(tmap.getPixelWidth()/4, tmap.getPixelHeight());
        setVisible(true);
//        if (spriteList.contains(new Player))

        for (Sprite sprite: spriteList)
            sprite.loadAnimations();

        // Load a single cloud animation
        Animation ca = new Animation();
        ca.addFrame(loadImage("images/cloud.png"), 1000);

        loadAllBackgrounds(backgroundList, level1Backgrounds, "level1/");
        // Create 3 clouds at random positions off the screen
        // to the right
        for (int c=0; c<3; c++)
        {
        	s = new Sprite(ca, 0.03f);
        	s.setX(screenWidth + (int)(Math.random()*200.0f));
        	s.setY(30 + (int)(Math.random()*150.0f));
        	s.setVelocityX(-0.02f);
        	s.show();
        	clouds.add(s);
        }

        initialiseGame();
      		
        System.out.println(tmap);
    }

    public void initialiseAnimations(){
        initPlayerAnim = new Animation();
        initPlayerAnim.loadAnimationFromSheet("images/player/idle.png", 2, 2, 250);
        initAnimations.add(initPlayerAnim);

        initBatAnim = new Animation();
        initBatAnim.loadAnimationFromSheet("images/bat/fly.png", 5, 1, 500);
        initAnimations.add(initBatAnim);

        initCoinAnim = new Animation();
        initCoinAnim.loadAnimationFromSheet("images/coin/coin.png", 3, 2, 250);
        initAnimations.add(initCoinAnim);

        initCrateAnim = new Animation();
        initCrateAnim.loadAnimationFromSheet("images/crate/crate.png", 1, 1, 100);
        initAnimations.add(initCrateAnim);
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
        }

        // Apply offsets to player and draw 
        player.setOffsets(xo, yo);
        player.drawTransformed(g);

        // Apply offsets to tile map and draw it
        tmap.draw(g,xo,yo);    
        
        // Show score and status information
        String msg = String.format("Score: %d", total/100);
        g.setColor(Color.darkGray);
        g.drawString(msg, getWidth() - 80, 50);
    }

    /**
     * Update any sprites and check for collisions
     * 
     * @param elapsed The elapsed time between this call and the previous call of elapsed
     */    
    public void update(long elapsed) {

        for (LinkedList<Sprite> l : backgroundList) {
            for (Sprite s : l)
                s.update(elapsed);
            }
        for (Sprite s: spriteList) {
            s.update(elapsed);
            checkTileCollision(s, tmap);
            s.updateAnimations(gravity);
            s.setVelocityY(s.getVelocityY() + (gravity * elapsed));
        }

        // Then check for any collisions that may have occurred
        handleScreenEdge(player, tmap, elapsed);

        for (Sprite s : spriteList) {
            if (!(s instanceof Player))
                boundingBoxCollision(player, s);
        }
        for (Sprite s : clouds)
            s.update(elapsed);
    }
    
    /**
     * Checks and handles collisions with the edge of the screen
     * 
     * @param s			The Sprite to check collisions for
     * @param tmap		The tile map to check 
     * @param elapsed	How much time has gone by since the last call
     */
    public void handleScreenEdge(Sprite s, TileMap tmap, long elapsed)
    {
    	// This method just checks if the sprite has gone off the bottom screen.
    	// Ideally you should use tile collision instead of this approach
    	
        if (s.getY() + s.getHeight() > tmap.getPixelHeight())
        {
        	// Put the player back on the map 1 pixel above the bottom
            initialiseGame();
        }
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
                if (player.isGrounded()) {
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
     * @return          Do the sprites collide?
     */
    public boolean boundingBoxCollision(Sprite s1, Sprite s2)
    {

        boolean overlapX = true, overlapY = true;
        char c = 'n';
        float s1X = s1.getX();
        float s1Y = s1.getY();
        int s1width = s1.getWidth();
        int s1height = s1.getHeight();
        float s1Xmid = s1X + s1width/2;
        float s1Ymid = s1Y + s1height/2;

        float s2X = s2.getX();
        float s2Y = s2.getY();
        int s2width = s2.getWidth();
        int s2height = s2.getHeight();
        float s2Xmid = s2X + s2width/2;
        float s2Ymid = s2Y + s2height/2;

        if ((s1X + s1width <= s2X || s1X >= s2X + s2width))
            overlapX = false;
        if ((s1Y + s1height <= s2Y || s1Y >= s2Y + s2height))
            overlapY = false;
        if (overlapX && overlapY){
            float a = Math.min(Math.abs(s1X-(s2X+s2width)), Math.abs(s1X+s1width-s2X)), b = Math.min(Math.abs(s1Y-(s2Y+s2height)), Math.abs((s1Y+s1height)-s2Y));
            if (b>a)
                c = 'x';
            else
                c = 'y';
        }
        s2.handleCollisionWithPlayer(s1, c, gravity);
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
        boolean leftWall = false, rightWall = false;

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
            if (gravity > 0)
                s.setGrounded(true);
            s.setVelocityY(0);
            s.setY(tmap.getTileYC(BLxtile, BLytile) - s.getHeight());

        }
        else if (((TL != '.'/* && Math.abs(TLYmid - symid) > Math.abs(TLXmid - sxmid)*/ && !leftWall) || (TR != '.'/* && Math.abs(TRYmid - symid) > Math.abs(TRXmid - sxmid)*/ && !rightWall)) && s.getVelocityY() < 0) {
            if (gravity < 0)
                s.setGrounded(true);
            s.setVelocityY(0);
            s.setY(tmap.getTileYC(TLxtile, TLytile) + tileHeight);
        }
        else if (s.getVelocityY() != 0 && s.isGrounded() && !s.isOnCrate())
            s.setGrounded(false);
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

    public void loadAllBackgrounds(ArrayList<LinkedList> bgList, String[] filenames, String directory) {
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
                s.show();
                bgList.add(s);
            }
            return bgList;
    }

    public void drawParallaxSprites(Graphics2D g, ArrayList<LinkedList> spriteLists){
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