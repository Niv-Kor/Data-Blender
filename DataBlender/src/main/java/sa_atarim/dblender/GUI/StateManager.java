package sa_atarim.dblender.GUI;
import java.util.ArrayList;
import java.util.List;
import javaNK.util.GUI.swing.containers.Window;
import javaNK.util.GUI.swing.state_management.State;

public class StateManager
{
	/**
	 * Enum of specific states.
	 * Use this enum to tell Launcher which state to set and where.
	 * 
	 * @author Niv Kor
	 */
	public static enum Substate {
		INPUT(InputState.class);
		
		private Class<? extends State> stateClass;
		
		private Substate(Class<? extends State> c) {
			this.stateClass = c;
		}
		
		/**
		 * Create an instance of the state.
		 * Every State instance needs a mutable window where it has the room to strech,
		 * and cannot exist without one.
		 * A state can take place in more than one window simultaneously.
		 * 
		 * @param window - The window the will contain the state instance
		 * @return an instance of the state that fits the size of the argument window.
		 */
		public State createInstance(Window window) {
			//create instance
			try { return stateClass.asSubclass(State.class).getConstructor(Window.class).newInstance(window); }
			catch (Exception e) { System.err.println("Cannot create an instance of class " + stateClass.getName()); }
			
			return null;
		}
		
		/**
		 * @return a reflection of the Substate's compatible class.
		 */
		public Class<? extends State> getStateClass() { return stateClass; }
	}
	
	/**
	 * A way to save both the window and the state that's running on it, and easily find it.
	 * WindowCache should be created every time a state is applied to any window,
	 * and should be deleted every time the state on that window is changed,
	 * or perhaps when that window is closed. 
	 */
	public static class WindowCache
	{
		private Window window;
		private State currentState;
		
		public WindowCache(Window w) {
			this.window = w;
		}
		
		public Window getWindow() { return window; }
		public State getCurrentState() { return currentState; }
		public void setCurrentState(State s) { currentState = s; }
	}
	
	private static List<WindowCache> windowCacheList = new ArrayList<WindowCache>();
	
	/**
	 * Set a state on a window.
	 * 
	 * @param window - The window that needs to contain the state
	 * @param substate - The requested state to set
	 */
	public static void setState(Window window, Substate substate) {
		if (substate == null) return;
		
		State instance = substate.createInstance(window);
		
		//find the correct window cache
		WindowCache tempCache = null;
		for (WindowCache wm : windowCacheList)
			if (wm.getWindow() == window) tempCache = wm;
		
		//create a new window cache if couldn't find it
		if (tempCache == null) {
			tempCache = new WindowCache(window);
			windowCacheList.add(tempCache);
		}
		
		//apply state to window and update its window memory
		window.applyState(instance);
		tempCache.setCurrentState(instance);
	}
	
	/**
	 * Get the cache that contains a currently open window with some state.
	 * 
	 * @param window - Currently open window to get its cache
	 * @return cache of that window.
	 */
	public static WindowCache getWindowCache(Window window) {
		for (WindowCache wc : windowCacheList)
			if (wc.getWindow() == window) return wc;
		
		return null;
	}
}