package sa_atarim.dblender.error;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import sa_atarim.dblender.Constants;

public enum PopupError
{
	MULTIPLE_FILES("Multiple Files Input", 101,
				   "This area can only accept one file."),
	
	TYPE_NOT_SUPPORTED("Unsupported File", 102,
					   "The file type you entered is not supported.<br>"
					 + "Supported types: xls, xlsx"),
	
	FILE_EMPTY("File is Empty", 103,
		       "The file you entered contains no data.<br>"
		     + "Please check again."),
	
	NO_FILES("Missing Files", 104,
			 "The blender must receive exactly two files."),
	
	NO_KEY("Missing Key Column", 201,
		   "The file cannot be processed without a key column (yellow sqaure)."),
	
	KEY_NOT_UNIQUE("Key is not unique", 202,
			       "Both of the files contain multiple rows with the same key value.<br>"
				 + "The key column must contain unique values for at least one of the files.");
	
	private static final float FONT_SIZE = 13f;
	
	private String windowTitle;
	private JLabel message;
	
	/**
	 * @param title - The title of the error
	 * @param msg - An informative message explaining the error
	 * @param icon - The icon to show in the window
	 * @param errorIndex - The index to in the title
	 */
	private PopupError(String title, int errorIndex, String msg) {
		String error = "Error #" + errorIndex;
		String errorTitle = error + ": " + title + ".";
		this.message = new JLabel("<html>" + errorTitle + "<br><br>" + msg + "</html>");
		this.windowTitle = Constants.PROGRAM_NAME + " " + error;
	}
	
	/**
	 * Pop a message to the screen.
	 */
	public void pop() {
		message.setFont(Constants.Fonts.MAIN.deriveFont(FONT_SIZE));
		JOptionPane.showMessageDialog(null, message, windowTitle, JOptionPane.ERROR_MESSAGE, Constants.Icons.ERROR);
	}
}