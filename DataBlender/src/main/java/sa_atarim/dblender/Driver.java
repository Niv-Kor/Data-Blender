package sa_atarim.dblender;
import sa_atarim.dblender.GUI.StateManager;
import sa_atarim.dblender.GUI.StateManager.Substate;
import sa_atarim.dblender.GUI.windows.MainWindow;

public class Driver
{
	public static void main(String[] args) {
		MainWindow mainWindow = new MainWindow();
		StateManager.setState(mainWindow, Substate.INPUT);
	}
}