import java.util.LinkedList;

//Class holds monster behavior and stats
public class Monster {
	public static class Hornet extends Corporeal{
		public Player chara;
		public LinkedList<Corporeal> player;
		public Hornet(Player chara, boolean[][] currentWalls){
			this.chara = chara;
			permaLevitate = true;
			oColor[0] = 1; oColor[1] = 1; oColor[2] = 0;
			shield = false;
			health = 1;
			speed = 4;
			size = 15;
			score = 5;
			monsterous = true;
			spellLeft = Spell.ARCANE_MISSILE;
			spellRight = Spell.ARCANE_MISSILE;
			int[] location = this.startingLoc(chara, size, currentWalls);
			hitbox.setLocation(location[0],location[1]);
			hitbox.setSize(size,size);
			player =  new java.util.LinkedList<>();
			player.add(chara);
		}
		//Hornets are dumb and just move towards player
		public double[] moveCalculate(){
			float[] direction = resolveAngle(chara.getLoc(), this.getLoc());
			double[] delta = {(int) (direction[0]*speed), (int) (direction[1]*speed)};
			return delta;
		}
		//Hornets will simply fire spell when available
		public SpellList.GenericSpell spellBrain(boolean[][] walls) {
			SpellList.GenericSpell spellObject = new SpellList.GenericSpell();
			if(time >=spellLeftCD){
				spellObject = castSpell(chara.getLoc(), walls, player, true);
			}
			return spellObject;
		}
	}
	public static class IceMonster extends Corporeal{
		public Player chara;
		public LinkedList<Corporeal> player;
		public IceMonster(Player chara, boolean[][] currentWalls){
			this.chara = chara;
			permaLevitate = false;
			oColor[0] = 0; oColor[1] = 1; oColor[2] = 1;
			shield = true;
			health = 1;
			speed = 2;
			size = 30;
			score = 10;
			monsterous = true;
			spellLeft = Spell.ICE_BLAST;
			spellRight = Spell.ICE_BLAST;
			int[] location = startingLoc(chara, size, currentWalls);
			hitbox.setLocation(location[0],location[1]);
			hitbox.setSize(size,size);
			player =  new java.util.LinkedList<>();
			player.add(chara);
		}
		//IceMosnters are dumb and just move towards player until a certain distance, no care for walls
		public double[] moveCalculate(){
			float[] direction = resolveAngle(chara.getLoc(), this.getLoc());
			double[] delta = {(direction[0]*speed), (direction[1]*speed)};
			if(this.legalLoc(delta[0], delta[1], chara, 400)){
				return delta;
			}
			delta[0] = 0; delta[1] = 0;
			return delta;
		}
		//IceMosnters will simply fire spell when available
		public SpellList.GenericSpell spellBrain(boolean[][] walls) {
			SpellList.GenericSpell spellObject = new SpellList.GenericSpell();
			if(time >=spellLeftCD){
				spellObject = castSpell(chara.getLoc(), walls, player, true);
			}
			return spellObject;
		}
		
	}
	public static class TeleBat extends Corporeal{
		public Player chara;
		public LinkedList<Corporeal> player;
		public int noPortTooFast;
		public TeleBat(Player chara, boolean[][] currentWalls){
			this.chara = chara;
			permaLevitate = true;
			oColor[0] = 1; oColor[1] = 0; oColor[2] = 1;
			shield = false;
			health = 1;
			speed = 20;
			size = 15;
			score = 25;
			noPortTooFast = (int) (chara.getTime()+2);	//Stopping annoying of teleporting on you right when wave starts
			monsterous = true;
			spellLeft = Spell.ARCANE_MISSILE;
			spellRight = Spell.BLINK;
			int[] location = startingLoc(chara, size, currentWalls);
			hitbox.setLocation(location[0],location[1]);
			hitbox.setSize(size,size);
			player =  new java.util.LinkedList<>();
			player.add(chara);
		}
		//TeleBat will stand still and blink to the player, as well as fire arcane missiles
		public SpellList.GenericSpell spellBrain(boolean[][] walls) {
			SpellList.GenericSpell spellObject = new SpellList.GenericSpell();
			if(time >=spellLeftCD){
				spellObject = castSpell(chara.getLoc(), walls, player, true);
			} else if(time >=spellRightCD && time >= noPortTooFast){
		        int distance = (int) Math.pow(Math.pow(this.getX()-chara.getX(),2) + Math.pow(this.getY()-chara.getY(),2) , 0.5);
		        if (distance > 400) {
		        	spellObject = castSpell(chara.getLoc(), walls, player, false);
		        }
			}
			return spellObject;
		}
		
	}
	public static class Chaser extends Corporeal{
		public Player chara;
		public LinkedList<Corporeal> player;
		public Chaser(Player chara, boolean[][] currentWalls){
			this.chara = chara;
			permaLevitate = false;
			oColor[0] = 1; oColor[1] = 0.5f; oColor[2] = 0;
			shield = true;
			health = 1;
			speed = 8.5;
			size = 80;
			score = 50;
			monsterous = true;
			spellLeft = Spell.ICE_BLAST;
			spellRight = Spell.ARCANE_SHIELD;
			int[] location = startingLoc(chara, size, currentWalls);
			hitbox.setLocation(location[0],location[1]);
			hitbox.setSize(size,size);
			player =  new java.util.LinkedList<>();
			player.add(chara);
		}
		//Chasers are dumb and just move towards player until a certain distance, no care for walls
		public double[] moveCalculate(){
			float[] direction = resolveAngle(chara.getLoc(), this.getLoc());
			double[] delta = {(direction[0]*speed), (direction[1]*speed)};
			return delta;
		}
		//Chasers will simply fire spell when available and cast shield when avaliable
		public SpellList.GenericSpell spellBrain(boolean[][] walls) {
			SpellList.GenericSpell spellObject = new SpellList.GenericSpell();
			if(time >=spellLeftCD){
				spellObject = castSpell(chara.getLoc(), walls, player, true);
			} else if(time >=spellRightCD && !shield){
		        spellObject = castSpell(chara.getLoc(), walls, player, false);
			}
			return spellObject;
		}
		
	}
	public static class Bulwark extends Corporeal{
		public Player chara;
		public LinkedList<Corporeal> player;
		public Bulwark(Player chara, boolean[][] currentWalls){
			this.chara = chara;
			permaLevitate = false;
			oColor[0] = 0.5f; oColor[1] = 0.3f; oColor[2] = 0.3f;
			shield = true;
			health = 3;
			speed = 0;
			size = 160;
			score = 100;
			monsterous = true;
			spellLeft = Spell.FIREBALL;
			spellRight = Spell.ARCANE_SHIELD;
			int[] location = startingLoc(chara, size, currentWalls);
			hitbox.setLocation(location[0],location[1]);
			hitbox.setSize(size,size);
			player =  new java.util.LinkedList<>();
			player.add(chara);
		}
		//Bulwarks will simply fire spell when available and cast shield when avaliable
		public SpellList.GenericSpell spellBrain(boolean[][] walls) {
			SpellList.GenericSpell spellObject = new SpellList.GenericSpell();
			if(time >=spellLeftCD){
				spellObject = castSpell(chara.getLoc(), walls, player, true);
			} else if(time >=spellRightCD && !shield){
		        spellObject = castSpell(chara.getLoc(), walls, player, false);
			}
			return spellObject;
		}
	}
	public static class Boss extends Corporeal{
		//Lower cycles means more erratic boss movement
		public int cycles = 20;
		public int cycle = 0;
		public int[] direct = {1,1};
		public Player chara;
		//Again, stopping blinking as soon as it spawns
		public int noPortTooFast;
		public LinkedList<Corporeal> player;
		public Boss(Player chara, boolean[][] currentWalls){
			this.chara = chara;
			permaLevitate = true;
			oColor[0] = 1; oColor[1] = 0; oColor[2] = 0;
			shield = true;
			health = 10;
			speed = 8;
			size = 25;
			score = 500;
			noPortTooFast = (int) (chara.getTime()+2);
			monsterous = true;
			spellLeft = Spell.ARCANE_MISSILE;
			spellRight = Spell.ICE_BLAST;
			int[] location = startingLoc(chara, size, currentWalls);
			hitbox.setLocation(location[0],location[1]);
			hitbox.setSize(size,size);
			player =  new java.util.LinkedList<>();
			player.add(chara);
		}
		//Trying to dynamically pick the best next spell. If at a safe distance, will use arcane shield.
		//Will blink towards and away from the player depending on state. Otherwise ice blast
		public void newSpell(boolean leftSpell) {
			if(!shield && Math.pow(Math.pow(this.getX()-chara.getX(),2) + Math.pow(this.getY()-chara.getY(),2) , 0.5) > 500){
				spellRight = Spell.ARCANE_SHIELD;
			} else if(((shield && Math.pow(Math.pow(this.getX()-chara.getX(),2) + Math.pow(this.getY()-chara.getY(),2) , 0.5) > 300) ||
					(!shield && Math.pow(Math.pow(this.getX()-chara.getX(),2) + Math.pow(this.getY()-chara.getY(),2) , 0.5) < 500)) &&
					time > noPortTooFast){
				spellRight = Spell.BLINK;
			} else {
				spellRight = Spell.ICE_BLAST;
			}
		}
		//Boss will simply fire spell when available and cast shield when avaliable
		public SpellList.GenericSpell spellBrain(boolean[][] walls) {
			SpellList.GenericSpell spellObject = new SpellList.GenericSpell();
			if(time >=spellLeftCD){
				spellObject = castSpell(chara.getLoc(), walls, player, true);
			} else if(time >=spellRightCD){
				newSpell(false);
				if(shield || spellRight != Spell.BLINK){
					spellObject = castSpell(chara.getLoc(), walls, player, false);
				} else {
					spellObject = castSpell(startingLoc(chara, size, walls), walls, player, false);
				}
			}
			return spellObject;
		}
		//Boss moves away from the player
		public double[] moveCalculate(){
			cycle += 1;
			if(cycle >= cycles){
				while(true){
					cycle = 0;
					//Ensures erratic boss movements
					direct[0] = rand.nextInt(3)-1;
					direct[1] = rand.nextInt(3)-1;
					//Boss runs if no shield
					//System.out.println(shield);
					if(!shield){
						direct[0] = -Math.abs(direct[0]);
						direct[1] = -Math.abs(direct[1]);
					}
					//Ensuring boss is not standing still
					if(Math.abs(direct[0])+Math.abs(direct[1]) != 0){
						break;
					}
				}
			}
			float[] direction = resolveAngle(chara.getLoc(), this.getLoc());
			double[] delta = { (direction[0]*speed*direct[0]), (direction[1]*speed*direct[1])};
			return delta;
		}
	}
}
