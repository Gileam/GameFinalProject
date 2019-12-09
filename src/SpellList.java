import java.util.LinkedList;

import edu.utc.game.GameObject;
import edu.utc.game.Texture;

//Holds code for spell casts 
public class SpellList{
	//Loading Textures
	static Texture FireballTexture = new Texture("res\\FireBall.png");
	static Texture ArcaneMissleTexture = new Texture("res\\ArcaneMissles.png");
	static Texture IceBlastTexture = new Texture("res\\IceBlast.png");
	static Texture LevitateTexture = new Texture("res\\Levitate.png");
	static Texture SpellEfectTexture = new Texture("res\\SpellEfect.png");
	static Texture MonsterSpellEffectTexture = new Texture("res\\SpellEfectEvil.png");
	static Texture GenericSpellTexture = new Texture("res\\Blank.png");
	static Texture ArcaneHeart = new Texture("res\\heartArcane.png");
	public static Object[] getTexture(Spell spell){
		//Note, strings must be 16 characters long
		Object[] spellStuff = {GenericSpellTexture, "No Spell!"};
		switch(spell){
			case FIREBALL:
				spellStuff[0] = FireballTexture;
				spellStuff[1] = "    Fireball    ";
				break;
			case ARCANE_MISSILE:
				spellStuff[0] = ArcaneMissleTexture;
				spellStuff[1] = " Arcane Missile ";
				break;
			case ARCANE_SHIELD:
				spellStuff[0] = ArcaneHeart;
				spellStuff[1] = " Arcane Shield  ";
				break;
			case BLINK:
				spellStuff[0] = SpellEfectTexture;
				spellStuff[1] = "     Blink      ";
				break;
			case ICE_BLAST:
				spellStuff[0] = IceBlastTexture;
				spellStuff[1] = "   Ice Blast    ";
				break;
			case LEVITATE:
				spellStuff[0] = LevitateTexture;
				spellStuff[1] = "    Levitate    ";
				break;
			default:
				System.out.println("ERROR: SPELL NOT FOUND!");
			}
		return spellStuff;
	}
	public static class GenericSpell extends GameObject{
		public boolean monsterous;
		public double getCD() {
			return CD;
		}
		public void draw(){
			spellTexture.draw(this);
		}
		public void create(int[] targetLocation){
			//System.out.println("Created");
			this.hitbox.setLocation(targetLocation[0]-size/2,targetLocation[1]-size/2);
			hitbox.setSize(size,size);
		}
		//UPDATE THESE FOR THE ACTUAL SPELL
		public double CD;
		public int size;
		protected Texture spellTexture = GenericSpellTexture;
		public void monsterous(){}
		public void effect(Corporeal effected){}
		//Making so generic spells will deactivate automatically
		public void update(int delta){
			deactivate();
		}
		//
	}
	//To make a spell cast-able, you need to add the code here, add it to the enum in corporeal, then add it to the cast spell method
	public static class GenericProjectile extends GenericSpell{
		public float[] direction;
		public LinkedList<Corporeal> targets;
		//UPDATE THESE FOR THE ACTUAL SPELL
		public int speed;
		public int damage;
		//
		public GenericProjectile(int[] targetLocation,Corporeal caster, LinkedList<Corporeal> enemies){
			this.targets = enemies;
			this.setColor(0, 0, 0);
			this.monsterous = caster.isMonsterous();
			int[] casterLoc = {caster.getX(),caster.getY()};
			direction = Corporeal.resolveAngle(targetLocation, casterLoc);
		}
		public void update(int delta){
			//Change this for how far a spell needs to go off screen to disappear
			int buffer = 500;
			//Need to cast to an integer of pixels
			int xDif = Math.round(speed * direction[0]);
			int yDif = Math.round(speed * direction[1]);
			//Stopping frozen in time bug
			if (Math.abs(xDif) + Math.abs(yDif) == 0){
				yDif = speed;
			}
			this.hitbox.translate(xDif, yDif);
			if (this.hitbox.getY()< -buffer || this.hitbox.getY() > GameLoop.worldHeight+buffer) {
				this.deactivate();
			}
			if (this.hitbox.getX()< -buffer || this.hitbox.getX() >  GameLoop.worldWidth+buffer) {
				this.deactivate();
			}
			for (Corporeal t : targets){
				if (this.intersects(t)){
					effect(t);
				}
			}
		}
	}
	public static class GenericEffect extends GenericSpell{
		Corporeal caster;
		boolean effected = false;
		private int spellCast = 50;
		private int spriteLinger = 20;
		public GenericEffect(Corporeal caster){
			this.caster = caster;
			this.monsterous = caster.isMonsterous();
			if(monsterous){
				this.spellTexture = MonsterSpellEffectTexture;
			} else {
				this.spellTexture = SpellEfectTexture;
			}
			this.size = 32;
			int[] casterLoc = {caster.getX(),caster.getY()};
			create(casterLoc);
		}
		public void update(int delta){
			//Making sure no double effect because of cleanup time
			if (spellCast > 0){
				spellCast -= 1;
			} else {
				if(!effected){
					effected = true;
					effect(caster);
				} else {
					if (spriteLinger > 0){
						spriteLinger -= 1;
					} else {
						deactivate();
					}
				}
			}
		}
	}
	
	
	//ACTUAL SPELL DEFINITIONS BELLOW
	public static class FireBall extends GenericProjectile{
		public FireBall(int[] targetLocation, Corporeal caster, LinkedList<Corporeal> enemies) {
			super(targetLocation, caster, enemies);
			CD = 4;
			spellTexture = FireballTexture;
			speed = 6;
			damage = 2;
			size = 128;
			monsterous();
			int[] casterLoc = {caster.getX(),caster.getY()};
			create(casterLoc);
		}
		public void effect(Corporeal target){
			target.damage(damage);
		}
		public void monsterous(){
			if(monsterous){
				speed *=0.5;
				damage *=1.5;
				CD *= 0.25;
			}
		}
	}
	public static class IceBlast extends GenericProjectile{
		public float slow = 0.8f;
		public IceBlast(int[] targetLocation, Corporeal caster, LinkedList<Corporeal> enemies) {
			super(targetLocation, caster, enemies);
			CD = 2;
			spellTexture = IceBlastTexture;
			speed = 10;
			damage = 1;
			size = 32;
			monsterous();
			int[] casterLoc = {caster.getX(),caster.getY()};
			create(casterLoc);
		}
		public void effect(Corporeal target){
			target.damage(damage);
			target.slow(slow);
			this.deactivate();
		}
		public void monsterous(){
			if(monsterous){
				size *= 2;
				damage *= 2;
				speed *= 0.5;
				slow *= 0.5;
			}
		}
	}
	public static class ArcaneMissle extends GenericProjectile{
		public ArcaneMissle(int[] targetLocation, Corporeal caster, LinkedList<Corporeal> enemies) {
			super(targetLocation, caster, enemies);
			CD = 0.5;
			spellTexture = ArcaneMissleTexture;
			speed = 14;
			damage = 1;
			size = 32;
			monsterous();
			int[] casterLoc = {caster.getX(),caster.getY()};
			create(casterLoc);
		}
		public void effect(Corporeal target){
			target.damage(damage);
			this.deactivate();
		}
		public void monsterous(){
			if(monsterous){
				CD *= 0.25;
				speed *= 0.35;
			}
		}
	}
	public static class Blink extends GenericEffect{
		private double[] targetLocation = {0,0};
		private boolean[][] walls;
		public Blink(boolean[][] walls, int[] targetLocation, Corporeal caster) {
			super(caster);
			this.targetLocation[0] = (double) targetLocation[0];
			this.targetLocation[1] = (double) targetLocation[1];
			this.walls = walls;
			CD = 1;
			monsterous();
			create(targetLocation);
		}
		public void effect(Corporeal caster){
			caster.checkIllegalMove(targetLocation, walls, false);
		}
	}
	public static class ArcaneShield extends GenericEffect{
		public ArcaneShield(Corporeal caster) {
			super(caster);
			CD = 1;
			monsterous();
		}
		public void effect(Corporeal caster){
			caster.setShield();
		}
		public void monsterous(){
			if(monsterous){
				CD *= 10;
			}
		}
	}
	public static class Levitate extends GenericEffect{
		public Levitate(Corporeal caster) {
			super(caster);
			CD = 0.5;
		}
		public void effect(Corporeal caster){
			caster.setLevitate();
		}
	}
}