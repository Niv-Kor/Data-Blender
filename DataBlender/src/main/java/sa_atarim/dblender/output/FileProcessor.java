package sa_atarim.dblender.output;
import java.awt.FileDialog;
import java.awt.Frame;
import sa_atarim.dblender.Constants;

public class FileProcessor
{
	private static final String TITLE = "Save Blended File";
	
	/**
	 * Open a file dialog and let the user choose a path and file name.
	 * 
	 * @return The file name that the user had chosen, including its complete path.
	 */
	public static String saveFileToDirectory() {
		FileDialog dialog = new FileDialog((Frame) null, TITLE);
	    dialog.setMode(FileDialog.SAVE);
	    dialog.setVisible(true);
	    String path = dialog.getDirectory();
	    String name = dialog.getFile();
	    return path + name + "." + Constants.SAVED_FILE_TYPE;
	}
}