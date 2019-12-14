package sa_atarim.dblender.sheets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javaNK.util.IO.DirectoryTrimmer;
import javaNK.util.files.FileLoader;

public class XLSFile extends FileLoader
{
	private XSSFWorkbook workbook;
	private InputStream inputStream;
	private SheetModifier sheet;
	private String fileName;
	private File file;

	/**
	 * @param path - The logical path of the sheet
	 * @throws IOExcetion If the file cannot be opened.
	 */
	public XLSFile(String path) throws IOException {
		this.file =  new File(path);
		this.fileName = DirectoryTrimmer.extractFileName(path);
		this.inputStream = new FileInputStream(file);
		this.workbook = new XSSFWorkbook(inputStream);
		this.sheet = new SheetModifier(workbook.getSheetAt(0));
	}
	
	/**
	 * Write to the workbook.
	 * This method needs to be called after all changes to the workbook are done,
	 * in order to physically write them to the file.
	 */
	public void write() {
		write(file);
	}
	
	/**
	 * Write to the workbook.
	 * This method needs to be called after all changes to the workbook are done,
	 * in order to physically write them to the file.\
	 * 
	 * @param file - The file to write into
	 */
	public void write(File file) {
		if (!isOpen()) {
			System.err.println("Cannot write to the file  '" + fileName + "' because it is not properly loaded.");
			return;
		}
		
		//write to file
		try {
			//modify sheet
			sheet.ignoreCellFormatWarnings();
			
			FileOutputStream outputStream = new FileOutputStream(file);
			workbook.write(outputStream);
			outputStream.close();
		}
		catch (IOException e) {
			System.err.println("Could not write to the file '" + fileName + "'.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Close the file.
	 */
	public void close() {
		try {
			workbook.close();
		}
		catch (IOException e) {
			System.err.println("Could not close the file '" + fileName + "'.");
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
	public SheetModifier getSheet() { return sheet; }
	
	/**
	 * @return the workbook that was loaded to this library.
	 */
	public XSSFWorkbook getWorkbook() { return workbook; }
}