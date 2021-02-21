
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
@SuppressWarnings("serial")

public class Game extends GameCore 
{
	// Useful game constants
	static int screenWidth = 1024;
	static int screenHeight = 768;

    float 	lift = 0.025f;
    float	gravity = 0.0001f;
    int xo = 0, yo = 0;

    // Game resources
    Animation landing;
    
    Sprite	player = null;
    ArrayList<Sprite> clouds = new ArrayList<Sprite>();

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

        // Load the tile map and print it out so we can check it is valid
        tmap.loadMap("maps", "map.txt");
        
        setSize(tmap.getPixelWidth()/4, tmap.getPixelHeight()+64);
        setVisible(true);

        // Create a set of background sprites that we can 
        // rearrange to give the illusion of motion
        
        landing = new Animation();
        landing.loadAnimationFromSheet("images/idle.png", 4, 1, 250);
        
        // Initialise the player with an animation
        player = new Sprite(landing);

        // Load a single cloud animation
        Animation ca = new Animation();
        ca.addFrame(loadImage("images/cloud.png"), 1000);

        loadAllBackgrounds(backgroundList, level1Backgrounds, "level1/");
        // Create 3 clouds at random positions off the screen
        // to the right
        for (int c=0; c<3; c++)
        {
        	s = new Sprite(ca);
        	s.setX(screenWidth + (int)(Math.random()*200.0f));
        	s.setY(30 + (int)(Math.random()*150.0f));
        	s.setVelocityX(-0.02f);
        	s.show();
        	clouds.add(s);
        }

        initialiseGame();
      		
        System.out.println(tmap);
    }

    /**
     * You will probably want to put code to restart a game in
     * a separate method so that you can call it to restart
     * the game.
     */
    public void initialiseGame()
    {
    	total = 0;
        player.setX(100);
        player.setY(600);
        player.setVelocityX(0);
        player.setVelocityY(0);
        player.show();
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
        drawParallaxSprites(g, backgroundList);
//        g.setColor(Color.white);
//        g.fillRect(0, 0, getWidth(), getHeight());

        // Apply offsets to sprites then draw them
        for (Sprite s: clouds)
        {
        	s.setOffsets(xo,yo);
        	s.draw(g);
        }

        // Apply offsets to player and draw 
        player.setOffsets(xo, yo);
        player.drawTransformed(g);
        player.drawBoundingBox(g);
                
        // Apply offsets to tile map and draw  it
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
    public void update(long elapsed)
    {
       	player.setAnimationSpeed(1.0f);

       	for (LinkedList<Sprite> l : backgroundList){
       	    for (Sprite s : l){
       	        s.update(elapsed);
            }
        }
       	for (Sprite s: clouds)
       		s.update(elapsed);

        // Then check for any collisions that may have occurred
        handleScreenEdge(player, tmap, elapsed);
        checkTileCollision(player, tmap);

        // Now update the sprites animation and position
        player.update(elapsed);
       

//        if (!player.isGrounded()) {
            player.setVelocityY(player.getVelocityY() + (gravity * elapsed));
//        }
//        else{
//            player.setVelocityY(0);
//        }
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
        	s.setY(tmap.getPixelHeight() - s.getHeight() - 1); 
        	
        	// and make them bounce
        	s.setVelocityY(-s.getVelocityY());
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
    	
    	if (key == KeyEvent.VK_ESCAPE) stop();
    	
    	if (key == KeyEvent.VK_UP)

    	if (key == KeyEvent.VK_S)
    	{
    		// Example of playing a sound as a thread
    	//	Sound s = new Sound("sounds/caw.wav");
    	//	s.start();
    	}

        if (key == KeyEvent.VK_D) {
            player.setVelocityX(0.175f);
            player.setScaleX(-1);
        }
        else if (key == KeyEvent.VK_A) {
            player.setVelocityX(-0.175f);
            player.setScaleX(1);
        }
        if (key == KeyEvent.VK_SPACE && player.isGrounded()){
            gravity = -gravity;
            player.setScaleY((float)-player.getScaleY());
            player.setGrounded(false);
        }
        if (key == KeyEvent.VK_R){
            initialiseGame();
        }
    }

    public boolean boundingBoxCollision(Sprite s1, Sprite s2)
    {
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
        // Find out how wide and how tall a tile is
        float tileWidth = tmap.getTileWidth();
        float tileHeight = tmap.getTileHeight();

        //TOP RIGHT
        int TRxtile = (int) ((s.getX() + s.getWidth()) / tileWidth);
        int TRytile = (int) (s.getY() / tileHeight);
        int TRXmid = (int) (tmap.getTileXC(TRxtile, TRytile) + tileWidth/2);
        int TRYmid = (int) (tmap.getTileYC(TRxtile, TRytile) + tileHeight/2);
        TR = tmap.getTileChar(TRxtile, TRytile);

        //BOTTOM RIGHT
        int BRxtile = (int) ((s.getX() + s.getWidth()) / tileWidth);
        int BRytile = (int) ((s.getY() + s.getHeight()) / tileHeight);
        int BRXmid = (int) (tmap.getTileXC(BRxtile, BRytile) + tileWidth/2);
        int BRYmid = (int) (tmap.getTileYC(BRxtile, BRytile) + tileHeight/2);
        BR = tmap.getTileChar(BRxtile, BRytile);

        //TOP LEFT
        int TLxtile = (int) (s.getX() / tileWidth);
        int TLytile = (int) (s.getY() / tileHeight);
        int TLXmid = (int) (tmap.getTileXC(TLxtile, TLytile) + tileWidth/2);
        int TLYmid = (int) (tmap.getTileYC(TLxtile, TLytile) + tileHeight/2);
        TL = tmap.getTileChar(TLxtile, TLytile);

        //BOTTOM LEFT
        int BLxtile = (int) (s.getX() / tileWidth);
        int BLytile = (int) ((s.getY() + s.getHeight()) / tileHeight);
        int BLXmid = (int) (tmap.getTileXC(BLxtile, BLytile) + tileWidth/2);
        int BLYmid = (int) (tmap.getTileYC(BLxtile, BLytile) + tileHeight/2);
        BL = tmap.getTileChar(BLxtile, BLytile);

        boolean ground = false, ceiling = false, leftWall = false, rightWall = false;

        if ((TR != '.' && TRXmid - sx - swidth > TRYmid - sy) || (BR != '.' && BRXmid - sx - swidth > TRYmid - sy - swidth)) {
            System.out.println("Rwall");
            s.setVelocityX(0);
            s.setX(tmap.getTileXC(TRxtile, TRytile) - s.getWidth());
        }
        if ((TL != '.' && TLXmid - sx > TLYmid - sy) || (BL != '.' && BLXmid - sx > TLYmid - sy - swidth)){
            System.out.println("Lwall");
            System.out.println(sx);
            s.setVelocityX(0);
            s.setX(tmap.getTileXC(TLxtile, TLytile) + tileWidth);
        }
//        if ((BL != '.' || BR != '.') && s.getVelocityY()>0){
//            ground = true;
//            if (gravity > 0) {
//                s.setGrounded(true);
//            }
//            System.out.println("ground");
//            s.setVelocityY(0);
//            s.setY(tmap.getTileYC(BLxtile, BLytile) - s.getHeight()-1);
//        }

//        //TOP RIGHT
//        TRxtile = (int) ((s.getX() + s.getWidth()) / tileWidth);
//        TRytile = (int) (s.getY() / tileHeight);
//        TR = tmap.getTileChar(TRxtile, TRytile);
//
//        //TOP LEFT
//        TLxtile = (int) (s.getX() / tileWidth);
//        TLytile = (int) (s.getY() / tileHeight);
//        TL = tmap.getTileChar(TLxtile, TLytile);
//        if ((TL != '.' || TR != '.') && s.getVelocityY()<0) {
//            if (gravity < 0){
//                s.setGrounded(true);
//            }
//            s.setVelocityY(0);
//            s.setY(tmap.getTileYC(TLxtile, TLytile) + tileHeight + 1);
//        }


//        if (leftWall){
//            s.setVelocityX(0);
//            s.setX(tmap.getTileXC(TLxtile, TLytile) + tileWidth);
//        }
//        else if (rightWall){
//            s.setVelocityX(0);
//            s.setX(tmap.getTileXC(TRxtile, TRytile) - s.getWidth());
//        }
//        if (ceiling){
//
//        }
//        if (ground){
//
//        }
    }

	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		// Switch statement instead of lots of ifs...
		// Need to use break to prevent fall through.
		switch (key)
		{
			case KeyEvent.VK_ESCAPE: stop(); break;
			case KeyEvent.VK_UP: {
                break;
            }
            case KeyEvent.VK_D:
            case KeyEvent.VK_A: {
                player.setVelocityX(0f);
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
                Sprite s = new Sprite(bg);
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