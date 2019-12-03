package sa_atarim.dblender.error;
import java.awt.Font;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javaNK.util.files.FontHandler;
import javaNK.util.files.FontHandler.FontStyle;
import sa_atarim.dblender.Constants;

public enum ErrorMessage
{
	MULTIPLE_FILES("MULTIPLE FILES ERROR",
				   "This area can only accept one file.",
				   Constants.Icons.ERROR,
				   JOptionPane.ERROR_MESSAGE, 101),
	
	TYPE_NOT_SUPPORTED("UNSOPPORTED FILE ERROR",
					   "The file type you entered is not supported.<br>"
					 + "Supported types: xls, xlsx",
					   Constants.Icons.ERROR,
					   JOptionPane.ERROR_MESSAGE, 102),
	
	NO_FILES("MISSING FILES ERROR",
			 "The blender must receive exactly 2 files.",
			 Constants.Icons.ERROR,
			 JOptionPane.ERROR_MESSAGE, 103);
	
	private static final Font FONT = FontHandler.load("Raleway", FontStyle.PLAIN, 14);
	
	private String windowTitle;
	private JLabel message;
	private Icon icon;
	private int type;
	
	/**
	 * @param title - The title of the error
	 * @param msg - An informative message explaining the error
	 * @param icon - The icon to show in the window
	 * @param msgType - JOptionPane constant
	 * @param errorIndex - The index to in the title
	 */
	private ErrorMessage(String title, String msg, Icon icon, int msgType, int errorIndex) {
		String errorTitle = title + ":";
		this.message = new JLabel("<html>" + errorTitle + "<br><br>" + msg + "</html>");
		this.type = msgType;
		this.icon = icon;
		this.windowTitle = Constants.PROGRAM_NAME + " Error #" + errorIndex;
	}
	
	/**
	 * Pop a message to the screen.
	 */
	public void pop() {
		message.setFont(FONT);
		JOptionPane.showMessageDialog(null, message, windowTitle, type, icon);
	}
}