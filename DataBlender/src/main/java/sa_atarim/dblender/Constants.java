package sa_atarim.dblender;
import java.awt.Color;
import javax.swing.Icon;
import javaNK.util.files.ImageHandler;

public class Constants
{
	public static final String PROGRAM_NAME = "Data Blender";
	public static final String PROGRAM_TITLE = "S.A Atarim - " + PROGRAM_NAME;
	public static final Icon BLENDER_ICON = ImageHandler.loadIcon("icons/blender.png");
	public static final Icon HOVER_BLENDER_ICON = ImageHandler.loadIcon("icons/hover_blender.png");
	public static final Icon EMPTY_COLUMN_SETTINGS_ICON = ImageHandler.loadIcon("icons/empty_column_settings.png");
	public static final Icon HOVER_EMPTY_COLUMN_SETTINGS_ICON = ImageHandler.loadIcon("icons/hover_empty_column_settings.png");
	public static final Icon FILLED_COLUMN_SETTINGS_ICON = ImageHandler.loadIcon("icons/filled_column_settings.png");
	public static final Icon HOVER_FILLED_COLUMN_SETTINGS_ICON = ImageHandler.loadIcon("icons/hover_filled_column_settings.png");
	public static final Icon ERROR_ICON = ImageHandler.loadIcon("icons/broken_icon_60x78.png");
	public static final Color WINDOW_COLOR = new Color(239, 239, 239);
	public static final String[] ALLOWED_FILE_TYPES = { "xls", "xlsx" };
	public static final String SAVED_FILE_TYPE = "xlsx";
}