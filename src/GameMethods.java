import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import org.lwjgl.opengl.GL11;

import edu.utc.game.Game;
import edu.utc.game.GameObject;
import edu.utc.game.Text;
import edu.utc.game.Texture;

//Class holds methods needed for the game, but keeps things tidy by not making GameLoop overcrowded.
public class GameMethods{
	//Method reads map file to create walls.
	public static boolean[][] generateWalls(String str){
		try {
			BufferedImage mapImage = ImageIO.read(new File(str));
			int mapWidth = mapImage.getWidth();
			int mapHeight = mapImage.getHeight();
			boolean[][] walls = new boolean[mapWidth][mapHeight];
			//Iterating through map file pixel by pixel
			for (int y = 0; y < mapHeight; y++){
				for (int x = 0; x < mapWidth; x++){
					//REMINDER: IF THERE IS A WALL, IT IS FALSE
					if(mapImage.getRGB(x,y) == -1){
						walls[x][y] = true;
					} else{
						walls [x][y] = false;
					}
				}
			}
			return walls;
		} catch (IOException e) {
			System.out.println("MAP ERROR!");
			return null;
		}
		
	}
	//Method draws walls in scene
	public static void drawWalls(boolean[][] walls, int wallSize){
		for (int x=0; x<walls.length; x++){
			for (int y=0; y<walls[0].length; y++){
				if (walls[x][y])
				{
					GL11.glColor3f(1,1,1);
					float thisX = x*wallSize;
					float thisY = y*wallSize;
					float width = wallSize;
					float height = wallSize;

					GL11.glBegin(GL11.GL_QUADS);
					GL11.glVertex2f(thisX, thisY);
					GL11.glVertex2f(thisX+width, thisY);
					GL11.glVertex2f(thisX+width, thisY+height);
					GL11.glVertex2f(thisX, thisY+height);
					GL11.glEnd();
				}

			}
		}
	}
	public static void cameraFollow(Corporeal focus, int screenWidth, int screenHeight) {
		// move camera
		// set up orthographic projection to map world pixels to screen
        Game.ui.setViewport(0, 0, 1, 1);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(focus.getX()-screenWidth/2, focus.getX()+screenWidth/2, focus.getY()+screenHeight/2, focus.getY()-screenHeight/2, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);   
	}
	public static void newViewPort(int portWidth, int portHeight) {
        Game.ui.setViewport(0, 0, 1, 1);
		//GL11.glViewport(300,  610,  448, 120);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, portWidth, portHeight, 0, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
	public static void drawBackground() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the frame buffer
		glClearColor(.5f, .5f, .5f, .5f);	// Grey background
	}
	public static class SpellHolder extends GameObject{
		Player caster;
		Text nameDisplay;
		Text CDDisplay;
		Object[] spell;
		boolean leftSpell;
		int size = 64;
		SpellHolder(int x, int y, Player caster, boolean leftSpell){
			this.hitbox.setBounds(x,y,size,size);
			this.caster = caster;
			this.leftSpell = leftSpell;
			nameDisplay = new Text((int) hitbox.getCenterX()-85,(int) hitbox.getCenterY()+67,20,20,"");
			nameDisplay.setColor(0, 0, 0);
			CDDisplay = new Text((int) hitbox.getCenterX()-20,y+64,40,40,"");
			CDDisplay.setColor(0, 0, 0);
		}
		public void update(int delta){
			double cd;
			double time = caster.getTime();
			String cdText = " ";
			if(leftSpell){
				cd = caster.getLeftSpellCD()-time;
				spell = SpellList.getTexture(caster.getLeftSpell());
			} else{
				cd = caster.getRightSpellCD()-time;
				spell = SpellList.getTexture(caster.getRightSpell());
			}
			//System.out.println(cd);
			if (cd <= 0){
				cdText = " ";
			} else {
				//Plus one to counteract flooring
				cdText = Integer.toString((int) cd+1);
			}
			CDDisplay.setText(cdText);
			nameDisplay.setText((String) spell[1]);
		}
		public void draw(){
			((Texture) spell[0]).draw(this);
			CDDisplay.draw();
			nameDisplay.draw();
		}
	}
	//public static class
	public static class HeartContainer extends GameObject{
		private static Texture fullHeart = new Texture("res/heartFull.png");
		private static Texture emptyHeart = new Texture("res/heartEmpty.png");
		private static Texture arcaneHeart = new Texture("res/heartArcane.png");
		private static int size = 64;
		public Player tracked;
		public int containerNumber;
		public Texture currentTexture;
		HeartContainer(int topRightCorner, int topLeftCorner, Player tracked, int hpNumb){
			this.hitbox.setBounds(topRightCorner,topLeftCorner,size,size);
			this.currentTexture = fullHeart;
			this.tracked = tracked;
			this.containerNumber = hpNumb;
		}
		public void update(int delta){
			if (containerNumber <= tracked.getHealth()){
				if (tracked.getShield()){
					currentTexture = arcaneHeart;
				} else {
					currentTexture = fullHeart;
				}
			} else{
				currentTexture = emptyHeart;
			}
		}
		public void draw(){
			currentTexture.draw(this);
		}
	}
	public static LinkedList<GameObject> initHud(Player chara, int worldWidth, int worldHeight) {
		LinkedList<GameObject> HUD = new java.util.LinkedList<>();
		SpellHolder leftSpell = new SpellHolder(48,72, chara, true);
		HUD.add(leftSpell);
		SpellHolder rightSpell = new SpellHolder(224,72, chara, false);
		HUD.add(rightSpell);
		for(int h = 1; h <= chara.getHealth(); h++){
			HeartContainer heart = new HeartContainer((h-1)*68,4,chara,h);
			HUD.add(heart);
		}
		return HUD;
	}
	//Generates waves of enemies by algoithm
	//Algorithm the same as OSRS TzHaar Fight Caves. That minigame inspired this whole project
	public static LinkedList<Corporeal> generateEnemies(int wave, Player chara, boolean[][] currentWalls) {
		int number = wave;
		Corporeal e;
		LinkedList<Corporeal> enemies = new java.util.LinkedList<>();
		String Test = "Wave "+wave+": ";
		for(int power = 6; power > 0; power--){
			int memory = number;
			while(number > 0){
				number += 1-Math.pow(2, power);
				
				if(number >= 0){
					memory = number;
					switch(power){
						case 6:
							e = new Monster.Boss(chara, currentWalls);
							Test = Test+"Boss ";
							enemies.add(e);
							break;
						case 5:
							e = new Monster.Bulwark(chara, currentWalls);
							Test = Test+"Bulwark ";
							enemies.add(e);
							break;
						case 4:
							e = new Monster.Chaser(chara, currentWalls);
							Test = Test+"Chaser ";
							enemies.add(e);
							break;
						case 3:
							e = new Monster.TeleBat(chara, currentWalls);
							Test = Test+"TeleBat ";
							enemies.add(e);
							break;
						case 2:
							e = new Monster.IceMonster(chara, currentWalls);
							Test = Test+"IceMonster ";
							enemies.add(e);
							break;
						case 1:
							e = new Monster.Hornet(chara, currentWalls);
							Test = Test+"Hornet ";
							enemies.add(e);
							break;
					}
				}
			}
			number = memory;
		}
		//System.out.println(Test);
		//enemies = new java.util.LinkedList<>();
		return enemies;
	}
}