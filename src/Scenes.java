import edu.utc.game.*;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import java.util.LinkedList;

public class Scenes {
	//Code is SimpleMenu in game_package with small changes
	public static interface SelectableObject
	{
		void select();
		void deselect();
		void update(int delta);
		void draw();
	}
	public static class SimpleTextureObject extends GameObject{
		protected Texture texture;
		public SimpleTextureObject(int x, int y, int size, Texture texture){
			this.texture = texture;
			this.hitbox.setLocation(x,y);
			this.hitbox.setSize(size,size);
		}
		public void draw(){
			texture.draw(this);
		}
	}
	public static class SelectableText  extends Text implements SelectableObject
	{
		private float activeR, activeG, activeB;
		private float inactiveR, inactiveG, inactiveB;

		public SelectableText(int x, int y, int w, int h, String text, 
				float aR, float aG, float aB, float iR, float iG, float iB)
		{
			super(x,y,w,h,text);
			activeR=aR;
			activeG=aG;
			activeB=aB;
			inactiveR=iR;
			inactiveG=iG;
			inactiveB=iB;
			//Allows use of new default color
			this.setColor(inactiveR, inactiveG, inactiveB);
		}
		public void select()
		{
			this.setColor(activeR, activeG, activeB);
		}

		public void deselect()
		{
			this.setColor(inactiveR, inactiveG, inactiveB);
		}
	}
	
	public static class Item
	{
		public SelectableObject label;
		public Scene scene;

		public Item(SelectableObject label, Scene scene)
		{
			this.label=label;
			this.scene=scene;
		}
	}
	public static class EasyMenu implements Scene{

		private float backgroundR, backgroundG, backgroundB;
		private LinkedList<Item> selItems;
		private LinkedList<GameObject> unselItems;
		private int selected;
		private boolean go=false;
		private boolean firstRun = true;
		private Sound menuMusic;
		//Added background color defaults
		public EasyMenu(float backgroundR, float backgroundG, float backgroundB)
		{
			this.backgroundR = backgroundR;
			this.backgroundG = backgroundG;
			this.backgroundB = backgroundB;
			selItems=new LinkedList<>();
			unselItems=new LinkedList<>();
			selected=0;
			go=false;
		}

		public void setMusic(Sound menuMusic){
			this.menuMusic = menuMusic;
		}

		public void reset()
		{
			go=false;
			select(0);
		}
	
		public void addSelItem(SelectableObject label, Scene scene)
		{
			selItems.add(new Item(label, scene));
		}
		
		public void addUnselItem(int x, int y, int w, int h, String text, float r, float g, float b)
		{
			Text addition = new Text(x,y,w,h,text);
			addition.setColor(r, g, b);
			unselItems.add(addition);
		}

		public void addUnselItem(int x, int y, int size, Texture texture) {
			SimpleTextureObject addition = new SimpleTextureObject(x, y, size, texture);
			unselItems.add(addition);
		}
		
		public void addUnselItem(Text text) {
			unselItems.add(text);
		}
	
		public void select(int p)
		{
			selItems.get(selected).label.deselect();
			selItems.get(p).label.select();
			selected=p;
		}
	
		public void go()
		{
			go=true;
		}
		
		public void onKeyEvent(int key, int scancode, int action, int mods)  
		{
			if (action== 1 || action == 2)
			{
				if (key == 87 || key == 265) //W key and up arrow key
				{
					select((selected+selItems.size()-1)%selItems.size());
				}
				else if (key == 83 || key == 264) //S key and down arrow key
				{
					select((selected+1)%selItems.size());
				}
				else if (key == 257) //enter key and esc key
				{
					go();
				}
			}
			
		};
	
	
		public Scene drawFrame(int delta)
		{
			if(firstRun && menuMusic != null){
				menuMusic.play();
				firstRun = false;
			}
			glClearColor(backgroundR, backgroundG, backgroundB, .0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
	
			if (go) { 
				go = false;
				if(menuMusic != null){
					menuMusic.stop();
					firstRun = true;
				}
				return selItems.get(selected).scene; 
			}
	
			for (Item item : selItems)
			{	
				item.label.update(delta);
				item.label.draw();
			}
			for (GameObject item: unselItems){
				item.update(delta);	//Text update method doesn't do anything but this might not always be the case...
				item.draw();
			}
	
			return this;
	
		}
		}
}
