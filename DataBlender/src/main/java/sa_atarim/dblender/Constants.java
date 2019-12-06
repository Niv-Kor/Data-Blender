package sa_atarim.dblender;
import java.awt.Color;
import javax.swing.ImageIcon;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import javaNK.util.files.ImageHandler;

public class Constants
{
	public static final String PROGRAM_NAME = "Data Blender";
	public static final String PROGRAM_TITLE = "S.A Atarim - " + PROGRAM_NAME;
	public static final Color WINDOW_COLOR = new Color(239, 239, 239);
	public static final String[] ALLOWED_FILE_TYPES = { "xls", "xlsx" };
	public static final String SAVED_FILE_TYPE = "xlsx";
	
	//format style
	public static final HorizontalAlignment HEADERS_ALIGNMENT = HorizontalAlignment.CENTER;
	public static final HorizontalAlignment DATA_ALIGNMENT = HorizontalAlignment.RIGHT;
	
	public static final class Icons
	{
		private static final String FOLDER = "icons/";
		
		//main window
		public static final ImageIcon CLEAR_X = ImageHandler.loadIcon(FOLDER + "clear.png");
		public static final ImageIcon HOVER_CLEAR_X = ImageHandler.loadIcon(FOLDER + "hover_clear.png");
		
		//blender
		public static final ImageIcon BLENDER = ImageHandler.loadIcon(FOLDER + "blender.png");
		public static final ImageIcon HOVER_BLENDER = ImageHandler.loadIcon(FOLDER + "hover_blender.png");
		public static final ImageIcon ERROR = ImageHandler.loadIcon(FOLDER + "broken_icon_60x78.png");
		
		//column settings icon
		public static final ImageIcon EMPTY_COLUMN_SETTINGS = ImageHandler.loadIcon(FOLDER + "empty_column_settings.png");
		public static final ImageIcon HOVER_EMPTY_COLUMN_SETTINGS = ImageHandler.loadIcon(FOLDER + "hover_empty_column_settings.png");
		public static final ImageIcon FILLED_COLUMN_SETTINGS = ImageHandler.loadIcon(FOLDER + "filled_column_settings.png");
		public static final ImageIcon HOVER_FILLED_COLUMN_SETTINGS = ImageHandler.loadIcon(FOLDER + "hover_filled_column_settings.png");
		
		//column settings window
		public static final ImageIcon SHIFT_FWD = ImageHandler.loadIcon(FOLDER + "shift_forward.png");
		public static final ImageIcon SHIFT_BCK = ImageHandler.loadIcon(FOLDER + "shift_back.png");
		public static final ImageIcon HOVER_SHIFT_FWD = ImageHandler.loadIcon(FOLDER + "hover_shift_forward.png");
		public static final ImageIcon HOVER_SHIFT_BCK = ImageHandler.loadIcon(FOLDER + "hover_shift_back.png");
		public static final ImageIcon GREEN_SQUARE = ImageHandler.loadIcon(FOLDER + "green_square.png");
		public static final ImageIcon BLUE_SQUARE = ImageHandler.loadIcon(FOLDER + "blue_square.png");
		public static final ImageIcon GRAY_SQUARE = ImageHandler.loadIcon(FOLDER + "gray_square.png");
		public static final ImageIcon GREEN_CANDIDATE_SQUARE = ImageHandler.loadIcon(FOLDER + "green_candidate_square.png");
		public static final ImageIcon BLUE_CANDIDATE_SQUARE = ImageHandler.loadIcon(FOLDER + "blue_candidate_square.png");
		public static final ImageIcon GRAY_CANDIDATE_SQUARE = ImageHandler.loadIcon(FOLDER + "gray_candidate_square.png");
	}
}