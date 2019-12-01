package sa_atarim.dblender.sheets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javaNK.util.files.FileLoader;

public class XLSFile extends FileLoader
{
	private XSSFWorkbook workbook;
	private InputStream inputStream;
	private Sheet sheet;
	private File file;

	/**
	 * @param path - The logical path of the sheet
	 * @param createNew - True to create a new file or false to open an existing one
	 */
	public XLSFile(String path, boolean createNew) throws IOException {
		if (createNew) createFile(path);
		
		this.file = new File(path);
		this.inputStream = new FileInputStream(file);
		this.workbook = new XSSFWorkbook(inputStream);
		this.sheet = new Sheet(workbook.getSheetAt(0));
	}
	
	/**
	 * Write to the workbook.
	 * This method needs to be called after all changes to the workbook are done.
	 */
	public void write() {
		if (!isOpen()) {
			System.err.println("Cannot write to the file because it is not properly loaded.");
			return;
		}
		
		try {
			FileOutputStream outputStream = new FileOutputStream(file);
			workbook.write(outputStream);
			outputStream.close();
		}
		catch (IOException e) {
			System.err.println("Could not write to the file.");
			e.printStackTrace();
		}
	}
	
	public void close() {
		try { workbook.close();	}
		catch (IOException e) {
			System.err.println("Could not close the file.");
			e.printStackTrace();
		}
	}
	
	/**
	 * @return true if the book contains a downloaded workbook or false otherwise. 
	 */
	public boolean isOpen() { return workbook != null; }
	
	/**
	 * @return the report sheet in the workbook that was loaded to this library.
	 */
	public Sheet getSheet() { return sheet; }
	
	/**
	 * @return the workbook that was loaded to this library.
	 */
	public XSSFWorkbook getWorkbook() { return workbook; }
	
	private File createFile(String path) {
		File newFile = new File(path);
		System.out.println("new file " + newFile);
		return newFile;
	}
}