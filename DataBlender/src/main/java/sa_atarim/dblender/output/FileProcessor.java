package sa_atarim.dblender.output;
import java.awt.FileDialog;
import java.awt.Frame;
import sa_atarim.dblender.Constants;

public class FileProcessor
{
	public static String saveFileToDirectory() {
		FileDialog dialog = new FileDialog((Frame) null, "Save Blended File");
	    dialog.setMode(FileDialog.SAVE);
	    dialog.setVisible(true);
	    String path = dialog.getDirectory();
	    String name = dialog.getFile();
	    return path + name + "." + Constants.SAVED_FILE_TYPE;
	}
}