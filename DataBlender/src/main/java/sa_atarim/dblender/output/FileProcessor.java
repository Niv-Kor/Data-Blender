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
		Workbook workbook = new XSSFWorkbook();
		workbook.createSheet(sheetName);
		String currentDir = Constants.CURRENT_DIRECTORY;
		String fileType = Constants.SAVED_FILE_TYPE;
		String fileName = generateTempName(currentDir, Constants.PROGRAM_NAME + " temp", fileType);
		String filePath = currentDir + fileName;
		FileOutputStream fileOut = new FileOutputStream(filePath);
		workbook.write(fileOut);
		workbook.close();
		fileOut.close();
		
		return filePath;
	}
	
	private static String generateTempName(String directory, String prefix, String postfix) {
		int i = 0;
		
		while (true) {
			String filePath = directory + prefix + " (" + i + ")." + postfix;
			File file = new File(filePath);
			if (!file.exists()) break;
			else i++;
		}
		
		return prefix + " (" + i + ")." + postfix;
	}
}