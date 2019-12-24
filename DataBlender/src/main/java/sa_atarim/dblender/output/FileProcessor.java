package sa_atarim.dblender.output;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sa_atarim.dblender.Constants;

public class FileProcessor
{
	private static final String TITLE = "Save Blended File";
	
	/**
	 * Open a file dialog and let the user choose a path and file name.
	 * 
	 * @return The file name that the user had chosen, including its complete path.
	 */
	public static String getDesiredDirectory() {
		FileDialog dialog = new FileDialog((Frame) null, TITLE);
	    dialog.setMode(FileDialog.SAVE);
	    dialog.setVisible(true);
	    String path = dialog.getDirectory();
	    String name = dialog.getFile();
	    return path + name + "." + Constants.SAVED_FILE_TYPE;
	}
	
	/**
	 * Create an empty file according to the given specification.
	 * 
	 * @param request - A specification of the file's properties
	 * @return The newly created file.
	 * @throws IOException When the new file cannot be created due to bad path.
	 */
	public static String createTempFile(String sheetName) throws IOException {
		//create a temp file
		File tempFile = File.createTempFile(Constants.PROGRAM_NAME, "temp");
		String filePath = tempFile.getAbsolutePath();
		tempFile.deleteOnExit();
		
		//write to the temp file
		Workbook workbook = new XSSFWorkbook();
		workbook.createSheet(sheetName);
		FileOutputStream fileOut = new FileOutputStream(filePath);
		workbook.write(fileOut);
		workbook.close();
		fileOut.close();
		
		return filePath;
	}
}