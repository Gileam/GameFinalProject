import edu.utc.game.Sound;

enum vdir{UP, DOWN, STILL}
enum hdir{LEFT,RIGHT, STILL}

public class Player extends Corporeal{
	//Player Unique Vars
	public vdir currVdir;
	public hdir currHdir;
	protected Sound cast = new Sound("res\\PlayerSpell.wav");
	public Player(){
		permaLevitate = false;
		oColor[1] = 1;
		iColor[1] = 1;
		shield = true;
		health = 5;
		speed = 8;
		size = 25;
		score = 0;
		monsterous = false;
		newSpell(true);
		newSpell(false);
		hurt = new Sound("res\\PlayerHurt.wav");
		currVdir = vdir.STILL;
		currHdir = hdir.STILL;
		hitbox.setLocation(75,75);
		hitbox.setSize(size,size);
	}
	//Movement Methods
	public void moveUp(){
		currVdir = vdir.UP;
	}
	public void moveDown(){
		currVdir = vdir.DOWN;
	}
	public void vStill(){
		currVdir = vdir.STILL;
	}
	public void moveLeft(){
		currHdir = hdir.LEFT;
	}
	public void moveRight(){
		currHdir = hdir.RIGHT;
	}
	public void hStill(){
		currHdir = hdir.STILL;
	}
	public void newSpell(boolean leftSpell) {
		cast.play();
		//Here you can change spell weighting for generation. Must be under 100. Note a spell needs a weight of at least one to be selected.
		int weight = 0;
		Spell newSpell = Spell.ARCANE_MISSILE;
		while (rand.nextInt(100+weight)<100){
			newSpell = Spell.values()[rand.nextInt(Spell.values().length)];
			switch (newSpell){
				case ARCANE_MISSILE:
					weight = 80;
					break;
				case ARCANE_SHIELD:
					weight = 5;
					break;
				case BLINK:
					weight = 20;
					break;
				case FIREBALL:
					weight = 40;
					break;
				case ICE_BLAST:
					weight = 70;
					break;
				case LEVITATE:
					weight = 10;
					break;
				default:
					weight = 0;
					break;
				}
		}
		if(leftSpell){
			spellLeft = newSpell;
		} else {
			spellRight = newSpell;
		}
	}
	public double[] moveCalculate(){
		double[] deltaCoord = {speed,speed};
		boolean diagMove = true;
		if(currVdir == vdir.UP){
			deltaCoord[1] *=-1;
		} else if (currVdir == vdir.DOWN){
			deltaCoord[1] *= 1;
		} else if (currVdir == vdir.STILL){
			diagMove = false;
			deltaCoord[1] *= 0;
		}
		if(currHdir == hdir.LEFT){
			deltaCoord[0] *=-1;
		} else if (currHdir == hdir.RIGHT){
			deltaCoord[0] *= 1;
		} else if (currHdir == hdir.STILL){
			deltaCoord[0] *= 0;
			diagMove = false;
		}
		//Accounting for longer distance when moving diagonals
		if(diagMove){
			deltaCoord[0] *= Math.sqrt(2)/2;
			deltaCoord[1] *= Math.sqrt(2)/2;
		}
		return deltaCoord;
	}
	
}
