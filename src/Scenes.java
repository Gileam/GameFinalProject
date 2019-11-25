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
	public static class SelectableText  extends Text implements SelectableObject
	{
		private float activeR, activeG, activeB;
		private float inactiveR, inactiveG, inactiveB;

		public SelectableText(int x, int y, int w, int h, String text, 
				float aR, float aG, float aB, float iR, float iG, float iB)
		{
			super(x,y,w,h,text);
			//Allows use of new default color
			this.setColor(inactiveR, inactiveG, inactiveB);
			activeR=aR;
			activeG=aG;
			activeB=aB;
			inactiveR=iR;
			inactiveG=iG;
			inactiveB=iB;
		}
		public void select()
		{
			this.setColor(activeR, activeG, activeB);
		}
		//New method sets the label to new value
		public void setLabel(String label){
			this.textArray = label.split("");
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
	
	public static class MainMenu implements Scene{

		private float backgroundR, backgroundG, backgroundB;
		private LinkedList<Item> items;
		private int selected;
		private boolean go=false;
		//Added background color defaults
		public MainMenu(float backgroundR, float backgroundG, float backgroundB)
		{
			this.backgroundR = backgroundR;
			this.backgroundG = backgroundG;
			this.backgroundB = backgroundB;
			items=new LinkedList<>();
			selected=0;
			go=false;
		}
	
		public void reset()
		{
			go=false;
			select(0);
		}
	
		public void addItem(SelectableObject label, Scene scene)
		{
			items.add(new Item(label, scene));
		}
	
		public void select(int p)
		{
			items.get(selected).label.deselect();
			items.get(p).label.select();
			selected=p;
		}
	
		public void go()
		{
			go=true;
		}
		
		public void onKeyEvent(int key, int scancode, int action, int mods)  
		{
			if (action==org.lwjgl.glfw.GLFW.GLFW_PRESS)
			{
				if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_UP)
				{
					select((selected+items.size()-1)%items.size());
				}
				else if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN)
				{
					select((selected+1)%items.size());
				}
				else if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER)
				{
					go();
				}
			}
			
		};
	
	
		public Scene drawFrame(int delta)
		{
			glClearColor(backgroundR, backgroundG, backgroundB, .0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
	
			if (go) { 
				go = false;
				return items.get(selected).scene; 
			}
	
			for (Item item : items)
			{	
				item.label.update(delta);
				item.label.draw();
			}
	
			return this;
	
		}
		}
}
