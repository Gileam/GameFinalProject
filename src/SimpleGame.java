import edu.utc.game.Game;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import edu.utc.game.Scene;
import edu.utc.game.Sound;
import edu.utc.game.Text;

public class SimpleGame extends Game implements Scene {
	//Score Variable
	static int score;
	//Time tracking variables.
	static int time;
	static long start;
	static long elasped = 0;
	//Needed to pause the game
	boolean pauseGame = false;
	//Scenes. Menu is the main menu, pause is the pause menu, and win is the victory screen.
	static Scenes.MainMenu menu = new Scenes.MainMenu(1,1,1);
	static Scenes.MainMenu pause = new Scenes.MainMenu(1,1,1);
	static Scenes.MainMenu win = new Scenes.MainMenu(0,1,1);
	//Objects display text in win screen
	static Scenes.SelectableText scoreInMenu = null;
	static Scenes.SelectableText timeInMenu = null;
	//Objects display text in main game.
	static updatableText scoreText = null;
	static updatableText timeText = null;
	Sound chirp;
	Sound victory;
	Sound click;
	
	public static void main(String[] args)
	{
		//Constructing menus and game
		SimpleGame game=new SimpleGame();
		game.registerGlobalCallbacks();
		
		//Pseudo scenes that run code between scenes
		initScene launcher = new initScene(game);
		resumeTime timer = new resumeTime(game);
		
		menu.addItem(new Scenes.SelectableText(20, 20, 20, 20, "Launch Game", 1, 0, 0, 0, 0, 0), launcher);
		menu.addItem(new Scenes.SelectableText(20, 60, 20, 20, "Exit", 1, 0, 0, 0, 0, 0), null);
		menu.select(0);

		scoreInMenu = new Scenes.SelectableText(500, 20, 20, 20, "", 0, 0, 0, 0, 0, 0);
		timeInMenu = new Scenes.SelectableText(500, 60, 20, 20, "", 0, 0, 0, 0, 0, 0);
		
		pause.addItem(new Scenes.SelectableText(20, 20, 20, 20, "Resume", 1, 0, 0, 0, 0, 0), timer);
		pause.addItem(new Scenes.SelectableText(20, 60, 20, 20, "Quit to Main Menu", 1, 0, 0, 0, 0, 0), menu);
		pause.addItem(new Scenes.SelectableText(20, 100, 20, 20, "Quit to Desktop", 1, 0, 0, 0, 0, 0), null);
		pause.addItem(scoreInMenu, pause);
		pause.select(0);
		
		win.addItem(new Scenes.SelectableText(20, 60, 20, 20, "Quit to Main Menu", 1, 0, 0, 0, 0, 0), menu);
		win.addItem(new Scenes.SelectableText(20, 100, 20, 20, "Quit to Desktop", 1, 0, 0, 0, 0, 0), null);
		win.addItem(new Scenes.SelectableText(250, 250, 40, 100, "A WINNER IS YOU!", 1, 0, 0, 0, 0, 0), win);
		win.addItem(scoreInMenu, win);
		win.addItem(timeInMenu, win);
		pause.select(0);

		scoreText = new updatableText(20,20,20,20, "");
		timeText = new updatableText(20,60,20,20, "");
		
		//Game loop and setting initial scene
		game.setScene(menu);
		game.gameLoop();
	}

	public SimpleGame()
	{
		// inherited from the Game class, this sets up the window and allows us to access
		// Game.ui
		initUI(640, 480, "DemoGame");
		chirp=new Sound("res/chirping.wav");
		click=new Sound("res/click.wav");
		victory=new Sound("res/victory_music.wav");
	}
	//This small class is a way to ensure that anything that needs to reset the game does.
	public static class initScene implements Scene{
		SimpleGame game;
		initScene(SimpleGame game){
			this.game = game;
		}
		public Scene drawFrame(int delta) {
			game.init();
			return game;
		}
	}
	//This small class is a way to ensure that anything that needs to resume the time does.
	public static class resumeTime implements Scene{
		SimpleGame game;
		resumeTime(SimpleGame game){
			this.game = game;
		}
		public Scene drawFrame(int delta) {
			start = System.nanoTime();
			return game;
		}
	}
	//Detects escape key to pause the game
	public void onKeyEvent(int key, int scancode, int action, int mods){
		//System.out.println("Key: " + key);
		//System.out.println("Action: " + action);
		if(key== 256 && action == 1){
			pauseGame = true;
		}
	}
	//Detects mouse clicks to incriment score
	public void onMouseEvent(int button,int action, int mods){
		//System.out.println("Button: "+button);
		//System.out.println("Action: "+action);
		if (button==0 && action==1)
		{
			click.play();
			score++;
		}		
	}
	//Initializes game variables
	private void init(){
		glClearColor(1f, 1f, 1f, 1f);
		start = System.nanoTime();
		elasped = 0;
		// screen clear is black (this could go in drawFrame if you wanted it to change
		score = 0;
	}
	//Extends text so that it can get updated
	private static class updatableText extends Text{
		public updatableText(int x, int y, int w, int h, String text) {
			super(x, y, w, h, text);
			this.setColor(0f, 0f, 0f);
		}
		public void setLabel(String text){
			this.textArray = text.split("");
		}
	}
	//Draws the frames
	public Scene drawFrame(int delta) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the frame buffer
		//Calculating time in seconds
		time = (int) ((System.nanoTime() - start)/ 1000000000l + elasped);
		//if the puase game flag was thrown, pause the game.
		if (pauseGame){
			pauseGame = false;
			//Preparing pause menu and time stopping
			chirp.play();
			scoreInMenu.setLabel("Score: "+score);
			elasped = time;
			return pause; 
		}
		//Drawing text labels
		scoreText.setLabel("Score: "+score);
		scoreText.draw();
		timeText.setLabel("Time: "+time);
		timeText.draw();
		//Win condition
        if (score >= 5){
        	//Preparing win screen
        	victory.play();
			scoreInMenu.setLabel("Score: "+score);
			timeInMenu.setLabel("Time: "+time);
			return win;
        }
		return this;
	}

}
