package sa_atarim.dblender.error;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import sa_atarim.dblender.Constants;

public enum PopupError
{
	MULTIPLE_FILES("Multiple Files Input",
				   "This area can only accept one file.",
				   Constants.Icons.ERROR, 101),
	
	TYPE_NOT_SUPPORTED("Unsupported File",
					   "The file type you entered is not supported.<br>"
					 + "Supported types: xls, xlsx",
					   Constants.Icons.ERROR, 102),
	
	NO_FILES("Missing Files",
			 "The blender must receive exactly 2 files.",
			 Constants.Icons.ERROR, 103),
	
	NO_KEY("Missing Key Column",
		   "The file cannot be processed without a key column (yellow sqaure).",
		   Constants.Icons.ERROR, 104);
	
	private static final float FONT_SIZE = 13f;
	
	private String windowTitle;
	private JLabel message;
	private Icon icon;
	
	/**
	 * @param title - The title of the error
	 * @param msg - An informative message explaining the error
	 * @param icon - The icon to show in the window
	 * @param errorIndex - The index to in the title
	 */
	private PopupError(String title, String msg, Icon icon, int errorIndex) {
		String error = "Error #" + errorIndex;
		String errorTitle = error + ": " + title + ":";
		this.message = new JLabel("<html>" + errorTitle + "<br><br>" + msg + "</html>");
		this.windowTitle = Constants.PROGRAM_NAME + " " + error;
		this.icon = icon;
	}
	
	/**
	 * Pop a message to the screen.
	 */
	public void pop() {
		message.setFont(Constants.Fonts.MAIN.deriveFont(FONT_SIZE));
		JOptionPane.showMessageDialog(null, message, windowTitle, JOptionPane.ERROR_MESSAGE, icon);
	}
}