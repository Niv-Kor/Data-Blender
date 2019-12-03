package sa_atarim.dblender.GUI;
import java.util.ArrayList;
import java.util.List;
import javaNK.util.GUI.swing.containers.Window;
import javaNK.util.GUI.swing.state_management.State;
import sa_atarim.dblender.GUI.column_selection.ColumnSelectionState;

public class StateManager
{
	/**
	 * Enum of specific states.
	 * Use this enum to tell Launcher which state to set and where.
	 * 
	 * @author Niv Kor
	 */
	public static enum Substate {
		INPUT(InputState.class),
		COLUMN_SELECTION(ColumnSelectionState.class);
		
		private Class<? extends State> stateClass;
		
		private Substate(Class<? extends State> c) {
			this.stateClass = c;
		}
		
		/**
		 * Create an instance of the state.
		 * Every State instance needs a mutable window where it has the room to stretch,
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
	 * @return The state that had been applied.
	 */
	public static State setState(Window window, Substate substate) {
		return setState(window, substate.createInstance(window));
	}
	
	/**
	 * Set a state on a window.
	 * 
	 * @param window - The window that needs to contain the state
	 * @param stateInstance - The exact state instance to set
	 * @return The state that had been applied.
	 */
	public static State setState(Window window, State stateInstance) {
		if (stateInstance == null) return null;
		
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
		window.applyState(stateInstance);
		tempCache.setCurrentState(stateInstance);
		
		return stateInstance;
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