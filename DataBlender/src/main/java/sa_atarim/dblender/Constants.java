package sa_atarim.dblender;
import java.awt.Color;
import java.awt.Font;
import javax.swing.ImageIcon;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import javaNK.util.files.FontHandler;
import javaNK.util.files.FontHandler.FontStyle;
import javaNK.util.files.ImageHandler;
import javaNK.util.math.Range;

public class Constants
{
	public static final String PROGRAM_NAME = "Data Blender";
	public static final String PROGRAM_TITLE = "S.A Atarim - " + PROGRAM_NAME;
	public static final Color WINDOW_COLOR = new Color(239, 239, 239);
	public static final String[] ALLOWED_FILE_TYPES = { "xls", "xlsx" };
	public static final String SAVED_FILE_TYPE = "xlsx";
	public static final String CURRENT_DIRECTORY = System.getProperty("user.dir") + "/";
	public static final Color BOX_BACKGROUND = new Color(249, 249, 249);
	
	//format style
	public static final HorizontalAlignment HEADERS_ALIGNMENT = HorizontalAlignment.CENTER;
	public static final HorizontalAlignment DATA_ALIGNMENT = HorizontalAlignment.RIGHT;
	
	public static final class Fonts
	{
		private static enum Language {
			ENGLISH, HEBREW, UNCERTAIN;
		}
		
		public static final Font GENERIC = FontHandler.load("Arial", FontStyle.PLAIN, 15);
		public static final Font MAIN = FontHandler.load("Raleway", FontStyle.PLAIN, 18);
		public static final Font COLUMNS = FontHandler.load("VarelaRound", FontStyle.PLAIN, 12);
		
		private static final Range<Integer> HEBREW_ASCII = new Range<Integer>(1488, 1514);
		private static final Range<Integer> LOWER_ENGLISH_ASCII = new Range<Integer>(97, 122);
		private static final Range<Integer> UPPER_ENGLISH_ASCII = new Range<Integer>(65, 90);
		
		/**
		 * Find the correct font for a string according to its language.
		 * 
		 * @param str - A string to find the correct font to
		 * @return A font that best cooperates with the string.
		 */
		public static Font setByLanguage(String str) {
			Language lang = getLanguage(str);
			
			switch (lang) {
				case ENGLISH: return MAIN;
				case HEBREW: return COLUMNS;
				default: return GENERIC;
			}
		}
		
		/**
		 * Find the language of a string.
		 * 
		 * @param str - The string to check
		 * @return Hebrew if the string contains at least one character in hebrew;
		 * 		   English if the string doesn't contain hebrew characters, but rather English ones;
		 * 		   Generic if the string contains no Hebrew or English characters.
		 */
		private static Language getLanguage(String str) {
			boolean containsEnglish = false;;
			
			for (int i = 0; i < str.length(); i++) {
				int character = (int) str.charAt(i);
				
				if (HEBREW_ASCII.intersects(character)) return Language.HEBREW;
				else if (!containsEnglish) {
					if (LOWER_ENGLISH_ASCII.intersects(character) || UPPER_ENGLISH_ASCII.intersects(character))
					containsEnglish = true;
				}
			}
			
			return containsEnglish ? Language.ENGLISH : Language.UNCERTAIN;
		}
	}
	
	public static final class Icons
	{
		private static final String FOLDER = "/icons/";
		public static final ImageIcon ICON = ImageHandler.loadIcon(FOLDER + "icon.png");
		public static final ImageIcon COMPANY_LOGO = ImageHandler.loadIcon(FOLDER + "company_logo.png");
		public static final ImageIcon CLEAR_X = ImageHandler.loadIcon(FOLDER + "clear.png");
		public static final ImageIcon HOVER_CLEAR_X = ImageHandler.loadIcon(FOLDER + "hover_clear.png");
		public static final ImageIcon BLENDER = ImageHandler.loadIcon(FOLDER + "blender.png");
		public static final ImageIcon HOVER_BLENDER = ImageHandler.loadIcon(FOLDER + "hover_blender.png");
		public static final ImageIcon ERROR = ImageHandler.loadIcon(FOLDER + "error.png");
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
		public static final ImageIcon UNCHECKED_BOX = ImageHandler.loadIcon(FOLDER + "unchecked_box.png");
		public static final ImageIcon CHECKED_BOX = ImageHandler.loadIcon(FOLDER + "checked_box.png");
		public static final ImageIcon SELECT_ALL = ImageHandler.loadIcon(FOLDER + "selectall.png");
		public static final ImageIcon HOVER_SELECT_ALL = ImageHandler.loadIcon(FOLDER + "hover_selectall.png");
		public static final ImageIcon SORT_UP = ImageHandler.loadIcon(FOLDER + "sort_up.png");
		public static final ImageIcon HOVER_SORT_UP = ImageHandler.loadIcon(FOLDER + "hover_sort_up.png");
		public static final ImageIcon SORT_DOWN = ImageHandler.loadIcon(FOLDER + "sort_down.png");
		public static final ImageIcon HOVER_SORT_DOWN = ImageHandler.loadIcon(FOLDER + "hover_sort_down.png");
	}
}