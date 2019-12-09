import edu.utc.game.Game;
import edu.utc.game.GameObject;

import java.util.Iterator;
import java.util.LinkedList;

import edu.utc.game.Scene;
import edu.utc.game.Sound;
import edu.utc.game.Text;
import edu.utc.game.Texture;
import edu.utc.game.XYPair;

public class GameLoop extends Game implements Scene {
	//Game Constants
	static int screenWidth = 1280;
	static int screenHeight = 960;
	static int wallSize = 100;
	//Important Values
	static int worldWidth;
	static int worldHeight;
	boolean[][] currentWalls;
	//Game Objects
	Player chara;
	private LinkedList<SpellList.GenericSpell> spells;
	private LinkedList<Corporeal> enemies;
	LinkedList<GameObject> hudElements;
	//HUD Tracking
    static Text timeText;
    static Text scoreText;
    static Text waveText;
	//GameOver Thinks
    static Scene oLauncher;
    static Text timeTextGO;
    static Text scoreTextGO;
    static Text waveTextGO;
    //Music
	static Sound music;
    //Game Variables
    int score;
    int wave;
	//Time tracking
	long start;
	long elasped;
	double time;	//Exception to reinit, uncessary
	//Menu Variables
	Boolean pause;
	Boolean paused;
	//Menus (Not reinited)
	static Scenes.EasyMenu mainMenu = new Scenes.EasyMenu(1,1,1);
	static Scenes.EasyMenu pauseMenu = new Scenes.EasyMenu(1,1,1);
	static Scenes.EasyMenu gameOverMenu = new Scenes.EasyMenu(0,0,0);
	
	public GameLoop(){
		// inherited from the Game class, this sets up the window and allows us to access
		// Game.ui
		currentWalls = GameMethods.generateWalls("res\\map.png");
		worldWidth = currentWalls.length*wallSize;
		worldHeight = currentWalls[0].length*wallSize;
		initUI(screenWidth, screenHeight, "Spells!");
	}
	public static void main(String[] args){
		//Constructing menus and game
		GameLoop game=new GameLoop();
		game.registerGlobalCallbacks();
		//Game loop and setting initial scene
		music = new Sound("res\\Music.wav");
		music.setLoop(true);
		gameOverMenu.setMusic(new Sound("res\\GameOver.wav"));
		timeText =  new Text(screenWidth*9/10-100,screenHeight*1/32-20,20,20,"");
		timeText.setColor(0, 0, 0);
		scoreText = new Text(screenWidth*9/10-100,screenHeight*1/32,20,20,"");
		scoreText.setColor(0, 0, 0);
		waveText = new Text(screenWidth*9/10-100,screenHeight*1/32+20,20,20,"");
		waveText.setColor(0, 0, 0);
		
		initScene iLauncher = new initScene(game);
		oLauncher = new overScene(game);
		
		mainMenu.addUnselItem(screenWidth/2-165, screenHeight/2-300, 50, 50, "Spell Flingers",0,0,1);
		mainMenu.addUnselItem(25, screenHeight-45, 20, 20, "CONTROLS: WASD to move, esc to pause, and mouse buttons to cast spells!",0,0,0);
		mainMenu.addUnselItem(screenWidth/2-265, screenHeight/2-308, 64, (Texture) SpellList.getTexture(Spell.ARCANE_MISSILE)[0]);
		mainMenu.addUnselItem(screenWidth/2+265, screenHeight/2-308, 64, (Texture) SpellList.getTexture(Spell.ARCANE_MISSILE)[0]);
		mainMenu.addSelItem(new Scenes.SelectableText(screenWidth/2-165, screenHeight/2-100, 30, 30, "Play Game", 1, 0, 0, 0, 0, 0), iLauncher);
		mainMenu.addSelItem(new Scenes.SelectableText(screenWidth/2-165, screenHeight/2-50, 30, 30, "Exit", 1, 0, 0, 0, 0, 0), null);
		mainMenu.select(0);
		
		pauseMenu.addUnselItem(screenWidth/2-165, screenHeight/2-300, 50, 50, "PAUSED",0,1,0);
		pauseMenu.addUnselItem(timeText);
		pauseMenu.addUnselItem(scoreText);
		pauseMenu.addUnselItem(waveText);
		pauseMenu.addSelItem(new Scenes.SelectableText(screenWidth/2-165, screenHeight/2-100, 30, 30, "Resume Game", 1, 0, 0, 0, 0, 0), game);
		pauseMenu.addSelItem(new Scenes.SelectableText(screenWidth/2-165, screenHeight/2-50, 30, 30, "Abandon Run", 1, 0, 0, 0, 0, 0), oLauncher);

		timeTextGO =  new Text(screenWidth/2-165, 240, 20, 20,"");
		timeTextGO.setColor(1, 1, 1);
		scoreTextGO = new Text(screenWidth/2-165, 270, 20, 20,"");
		scoreTextGO.setColor(1, 1, 1);
		waveTextGO = new Text(screenWidth/2-165, 300, 20, 20,"");
		waveTextGO.setColor(1, 1, 1);
		gameOverMenu.addUnselItem(screenWidth/2-165, 100, 50, 50, "GAME OVER",1,0,0);
		gameOverMenu.addUnselItem(timeTextGO);
		gameOverMenu.addUnselItem(scoreTextGO);
		gameOverMenu.addUnselItem(waveTextGO);
		gameOverMenu.addSelItem(new Scenes.SelectableText(screenWidth/2-165, screenHeight/2+50, 30, 30, "New Game", 1, 0, 0, 1, 1, 1), iLauncher);
		gameOverMenu.addSelItem(new Scenes.SelectableText(screenWidth/2-165, screenHeight/2+100, 30, 30, "Return to Main Menu", 1, 0, 0, 1, 1, 1), mainMenu);
		gameOverMenu.addSelItem(new Scenes.SelectableText(screenWidth/2-165, screenHeight/2+150, 30, 30, "Exit", 1, 0, 0, 1, 1, 1), null);
		
		game.setScene(mainMenu);
		game.gameLoop();
	}
	public static void textIntialize(){
		
	}
	//Initializes game variables. Allows for game restarting.
	public static class initScene implements Scene{
		GameLoop game;
		initScene(GameLoop game){
			this.game = game;
		}
		public Scene drawFrame(int delta) {
			music.play();
			textIntialize();
			game.chara = new Player();
			game.hudElements = GameMethods.initHud(game.chara, screenWidth, screenHeight);
			game.elasped = 0;
			game.start = System.nanoTime();
			game.pause = false;
			game.paused = false;
			game.wave = 62;
			game.score = 0;
			game.spells = new java.util.LinkedList<>();
			game.enemies = new java.util.LinkedList<>();
			mainMenu.select(0);
			return game;
		}
	}
	public static class overScene implements Scene{
		GameLoop game;
		public overScene(GameLoop game) {
			this.game = game;
		}
		public Scene drawFrame(int delta) {
			timeTextGO.setText("End Time: "+formatTime(game.time));
			scoreTextGO.setText("Final Score: " + game.score);
			waveTextGO.setText("Last Wave: " + game.wave);
			gameOverMenu.select(0);
			return gameOverMenu;
		}
		
	}
	//Moving the Player Character and Pausing the Game
	public void onKeyEvent(int key, int scancode, int action, int mods){
		//System.out.println("Key: " + key);
		//System.out.println("Action: " + action);
		if(key ==  87 && (action == 1 || action == 2)){	//W key
			chara.moveUp();
		} else if(key == 83 && (action == 1 || action == 2)){ //S key
			chara.moveDown();
		} else if ((key == 83 || key == 87) && action == 0) {
			chara.vStill();
		}
		if(key == 65 && (action == 1 || action == 2)){ //A key
			chara.moveLeft();
		} else if(key == 68 && (action == 1 || action == 2)){ //D key
			chara.moveRight();
		} else if ((key == 65 || key == 68) && action == 0){
			chara.hStill();
		}
		if(key == 256){ //esc key
			pause = true;
			paused = true;
		}
	}

	private XYPair<Integer> currentPos;
	public void onMouseEvent(int button,int action, int mods){
		//System.out.println("Button: "+button);
		//System.out.println("Action: "+action);
		currentPos = Game.ui.getMouseLocation();
		//Converting mouse screen location to mouse world location
		int[] mouseLocation = {(chara.getX() + currentPos.x-screenWidth/2), (currentPos.y + chara.getY())-screenHeight/2};
		//System.out.println(mouseLocation[0]+","+mouseLocation[1]);
		if(button == 0 && action == 1){
			spells.add(chara.castSpell(mouseLocation, currentWalls, enemies, true));
		} else if (button == 1 && action == 1){
			spells.add(chara.castSpell(mouseLocation, currentWalls, enemies, false));
		}
	}
	
	//Method does time math
	public void time(){
		if(paused){
			paused = false;
			music.play();
			start = System.nanoTime();
		} else {
			elasped += System.nanoTime() - start;
			start = System.nanoTime();
		}
		time = (double) elasped/ (double) 1000000000l;
		//System.out.println(time);
	}
	private static String formatTime(double time) {
		String timeText = (int) (time/60)+":";
		if((int)(time%60) < 10){
			timeText = timeText + "0"+(int)(time%60);
		} else {
			timeText = timeText+(int)(time%60);
		}
		return timeText;
	}
	//Draws the frames
	public Scene drawFrame(int delta) {
		//Pre-Drawing Stuff
		if(pause){
			music.stop();
			pause = false;
			pauseMenu.select(0);
			return pauseMenu;
			}
		//Putting this up here allows a 1-frame zombie state for the player, but thats acceptable
		if (!chara.isActive()){
			music.stop();
			score += chara.getScore();
			return oLauncher;}
		time();
		
		//Drawing Game Objects
		GameMethods.cameraFollow(chara,screenWidth,screenHeight);
        
		GameMethods.drawBackground();
		GameMethods.drawWalls(currentWalls, wallSize);	//Drawing walls (actually drawing not walls)
		chara.update(time, currentWalls);	//Updating Player
		chara.draw();									//Drawing Player

		// draw existing targets, remove nonexisting ones.
		Iterator<SpellList.GenericSpell> cast = spells.iterator();
		while (cast.hasNext()) {
			GameObject o = cast.next();
			o.update(delta);
			if (!o.isActive())
			{
				cast.remove();
			}
			o.update(delta);
			o.draw();
		}
		Iterator<Corporeal> enemy = enemies.iterator();
		while (enemy.hasNext()) {
			Corporeal o = enemy.next();
			o.update(delta);
			if (!o.isActive())
			{
				score += o.getScore();
				enemy.remove();
			}
			o.update(time, currentWalls);
			spells.add(o.spellBrain(currentWalls));
			o.draw();
		}
		if(enemies.isEmpty()){
			wave += 1;
			enemies = GameMethods.generateEnemies(wave, chara, currentWalls);
		}
		//System.out.println("Spells Active: "+spells.size());

		//Drawing HUD
		GameMethods.newViewPort(screenWidth, screenHeight);
		for(GameObject hudEle : hudElements){
			hudEle.update(delta);
			hudEle.draw();
		}
		timeText.setText(" Time: "+ formatTime(time));
		scoreText.setText("Score: " + score);
		waveText.setText(" Wave: " + wave);
		scoreText.draw();
		timeText.draw();
		waveText.draw();
		return this;
	}


}
