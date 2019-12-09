import java.util.LinkedList;
import java.util.Random;

import edu.utc.game.GameObject;
import edu.utc.game.Sound;

enum Spell {ICE_BLAST,ARCANE_MISSILE,FIREBALL,ARCANE_SHIELD,LEVITATE,BLINK};
//Class holds behavior of any physical entities, ie players and monsters
//Class should be extended as any object that needs wall detection.
public abstract class Corporeal extends GameObject {
	//Needed for random elements
	public static Random rand = new Random();
	//Time
	protected double time;
	//Tracking I-Frames
	private double oTime = 0;
	private double iTime = 1;
	//Tracking Spell CD
	protected double spellLeftCD;
	protected double spellRightCD;
	//Tracking slowing
	private double sTime = 0;
	private double slowTime = 4;
	private float slow = 0;
	//Tracking levitate
	private double lTime = 0;
	private double leviTime = 20;
	private boolean levitate = false;
	//Hurt Sound
	protected Sound hurt = new Sound("res\\DefaultHurt.wav");
	//INTIALIZE THESE IN CONSTRUCTOR//
	//Levitate tracking
	protected boolean permaLevitate;
	//I-Frame Color Change
	protected float[] oColor = {0,0,0};
	protected float[] iColor ={0,0,1};
	//Hp and Shields
	protected boolean shield;
	protected int health;
	//Statistics
	protected double speed;
	protected int size;
	protected int score;
	protected boolean monsterous;
	protected Spell spellLeft;
	protected Spell spellRight;


	//Some bad practices in here, but oh well
	public int[] startingLoc(Player chara, int size, boolean[][] walls){
		int x,y;
		while(true){
			x = Corporeal.rand.nextInt(GameLoop.worldWidth-size);
			y = Corporeal.rand.nextInt(GameLoop.worldWidth-size);
			if (this.legalLoc(x,y, chara,700, walls)){break;}
		}
		int[] loc = {x,y};
		return loc;
	}
	// Absolute legal location checking, used for spawning things not too close to the players
    public boolean legalLoc(int x, int y, Player chara, int dist, boolean[][] currentWalls){
    	double distance = 0;
        distance = Math.pow(Math.pow(x-chara.getX(),2) + Math.pow(y-chara.getY(),2) , 0.5);
    	if (distance < dist){return false;}
    	return true;
    }
    //Delta legal location chekcing
    public boolean legalLoc(double delta, double delta2, Player chara, int dist){
    	double distance = Math.pow(Math.pow(delta+this.getX()-chara.getX(),2) + Math.pow(delta2+this.getY()-chara.getY(),2) , 0.5);
    	if (distance < dist){return false;}
		return true;
    }
	//Health Methods
	public void damage(int ammount){
		if(time > oTime){
			hurt.play();
			this.setColor(iColor[0],iColor[1],iColor[2]);
			this.oTime = iTime + time;
			if (shield){shield = false;
			} else {
				health -= ammount;
				if (health <= 0){
					this.deactivate();
				}
			}
		}
	}
	public int getHealth(){
		return health;
	}
	public boolean getShield() {
		return shield;
	}
	public void setShield(){
		shield = true;
	}
	//Levitate and slow methods
	public void slow(float slow) {
		sTime = time + slowTime;
		this.slow = slow;
	}
	public void setLevitate(){
		levitate = true;
		lTime = time + leviTime;
	}
	//UPDATE SHOULD RUN EVERY FRAME
	public void update(double time, boolean[][] walls){
		//Calculating new coordinates
		this.time = time;
		checkState(walls);
		double[] deltaCoord = moveCalculate();
		checkIllegalMove(deltaCoord,walls, true);
	}
	//Doing some state checking house keeping
	public void checkState(boolean[][] walls){
		//setting color to iframe color
		if(time > oTime){
			this.setColor(oColor[0],oColor[1],oColor[2]);
		}
		//unslowing people
		if(time > sTime){
			slow = 0;
		}
		//Letting people levitate if neccessary (ie stuck in a wall)
		if(time > lTime){
			int[] cellNumber = {(int) this.hitbox.getCenterX()/GameLoop.wallSize, (int) this.hitbox.getCenterY()/GameLoop.wallSize};
			if(walls[cellNumber[0]][cellNumber[1]]){
				levitate = false;
			} else{
				lTime = time+1;
				levitate = true;
			}
		}
		if(permaLevitate){
			levitate = true;
		}
	}
	public double getTime(){
		return time;
	}
	public void checkIllegalMove(double[] deltaCoord, boolean[][] walls, boolean delta){
		double[] newCoord = deltaCoord;
		//System.out.println(newCoord[0] + "," + newCoord[1]);
		//Converting coords to cell location
		if(delta){
			newCoord[0] = (this.hitbox.getCenterX()+(1f-slow)*(float) newCoord[0]);
			newCoord[1] = (this.hitbox.getCenterY()+(1f-slow)*(float) newCoord[1]);
			//if(slow > 0){System.out.println("Slowed: "+newCoord[0]+","+newCoord[1]);}
		}
		if((newCoord[0]<GameLoop.worldWidth && newCoord[0]>0) && (newCoord[1]<GameLoop.worldHeight && newCoord[1]>0)){
			//Checks for wall collision and out of bounds
				int[] cellNumber = {(int) newCoord[0]/GameLoop.wallSize, (int) newCoord[1]/GameLoop.wallSize};
			if(walls[cellNumber[0]][cellNumber[1]] || levitate){
				this.hitbox.setLocation((int)newCoord[0]-size/2, (int)newCoord[1]-size/2);
			}
		}
		
	}
	//Spell getters
	public Spell getLeftSpell(){
		return spellLeft;
	}
	public Spell getRightSpell(){
		return spellRight;
	}
	public double getLeftSpellCD(){
		return spellLeftCD;
	}
	public double getRightSpellCD(){
		return spellRightCD;
	}
	//Spell casting method
	public SpellList.GenericSpell castSpell(int[] targetLocation, boolean walls[][], LinkedList<Corporeal> enemies, boolean leftSpell){
		Spell curSpell;
		double curSpellCD = 0;
		SpellList.GenericSpell spellObject = new SpellList.GenericSpell();
		if (leftSpell){
			curSpell = spellLeft;
			curSpellCD = spellLeftCD;
		} else {
			curSpell = spellRight;
			curSpellCD = spellRightCD;
		}
		//We need to return a spell object either way, so this will overwrite with the correct one if off CD
		if(time >= curSpellCD){
			switch(curSpell){
				case FIREBALL:
					spellObject = new SpellList.FireBall(targetLocation, this, enemies);
					break;
				case ARCANE_MISSILE:
					spellObject = new SpellList.ArcaneMissle(targetLocation, this, enemies);
					break;
				case ARCANE_SHIELD:
					spellObject = new SpellList.ArcaneShield(this);
					break;
				case BLINK:
					spellObject = new SpellList.Blink(walls, targetLocation, this);
					break;
				case ICE_BLAST:
					spellObject = new SpellList.IceBlast(targetLocation, this, enemies);
					break;
				case LEVITATE:
					spellObject = new SpellList.Levitate(this);
					break;
				default:
					System.out.println("ERROR: SPELL NOT FOUND!");
			}
			newSpell(leftSpell);
			curSpellCD = time + spellObject.getCD();
			if(leftSpell){
				spellLeftCD = curSpellCD;
			} else {
				spellRightCD = curSpellCD;
			}
		}
		return spellObject;
	}
	//Position get methods
	public int getX() {
		return (int) this.hitbox.getCenterX();
	}
	public int getY() {
		return (int) this.hitbox.getCenterY();
	}
	public int[] getLoc(){
		int[] curLoc = {this.getX(),this.getY()};
		return curLoc;
	}
	//Math method used by many things
	public static float[] resolveAngle(int[] targetLocation, int[] startingLocation){
		float xDif = (float) (targetLocation[0] - startingLocation[0]);
		float yDif = (float) (targetLocation[1] - startingLocation[1]);
		boolean test = false;
		//Preventing divide by zero error.
		if(xDif == 0){xDif = 0.000001f;}
		double angle = Math.atan(yDif/xDif);
		//Assigning unit vectors and correcting direction.
		float[] direction = {(float) Math.cos(angle) * xDif/Math.abs(xDif), (float) Math.sin(angle) * xDif/Math.abs(xDif)};
		if(test){System.out.println(direction[0]+","+direction[1]);}
		return direction;
	}
	public boolean isMonsterous() {
		return monsterous;
	}
	public int getScore(){
		return score;
	}
	//Will need to be overwritten by object if used.
	public void newSpell(boolean leftSpell) {
	}
	//Will need to be overwritten by object if used.
	public double[] moveCalculate(){
		double[] zeroDelta = {0, 0};
		return zeroDelta;
	}
	//Will need to be overwritten for spell AI
	public SpellList.GenericSpell spellBrain(boolean[][] walls) {
		return null;
	}
}
