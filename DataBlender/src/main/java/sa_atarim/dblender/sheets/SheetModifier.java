package sa_atarim.dblender.sheets;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class SheetModifier
{
	private static final int FIRST_ROW_CHECK_SAMPLE = 10;
	
	private DataFormatter formatter;
	private XSSFSheet xssfSheet;
	
	/**
	 * @param xssf - The original XSSFSheet object to modify
	 */
	public SheetModifier(XSSFSheet xssf) {
		this.xssfSheet = xssf;
		this.formatter = new DataFormatter();
	}
	
	/**
	 * @return An array of the columns names (the values of the headers).
	 */
	public String[] getColumnNames() {
		XSSFRow firstRow = xssfSheet.getRow(headerRowIndex());
		int columns = firstRow.getPhysicalNumberOfCells();
		String[] columnNames = new String[columns];
		
		for (int i = 0; i < columns; i++)
			columnNames[i] = cellValueString(firstRow, i);
		
		return columnNames;
	}
	
	/**
	 * Get an entire column as an array of String.
	 * 
	 * @param colName - The name of the column (header value)
	 * @return The entire column as an array of String.
	 */
	public String[] getEntireColumn(String colName) {
		return getEntireColumn(getColumnIndex(colName));
	}
	
	/**
	 * Get an entire column as an array of String.
	 * 
	 * @param colIndex - The index of the column
	 * @return The entire column as an array of String.
	 */
	public String[] getEntireColumn(int colIndex) {
		int rowCount = xssfSheet.getPhysicalNumberOfRows();
		String[] columnContent = new String[rowCount];
		
		for (int i = headerRowIndex() + 1; i < xssfSheet.getLastRowNum(); i++) {
			XSSFRow row = xssfSheet.getRow(i);
			columnContent[i] = cellValueString(row, colIndex);
		}
		
		return columnContent;
	}
	
	/**
	 * @return The index of the row that contains the headers.
	 */
	public int headerRowIndex() {
		int maxCells = 0, largestRow = 0;
		
		for (int i = 0; i < xssfSheet.getPhysicalNumberOfRows() && i < FIRST_ROW_CHECK_SAMPLE; i++) {
			Row row = xssfSheet.getRow(i);
			
			if (row != null) {
				int rowCells = row.getPhysicalNumberOfCells();
				
				if (rowCells > maxCells) {
					maxCells = rowCells;
					largestRow = i;
				}
			}
			else break;
		}
		
		return largestRow;
	}
	
	/**
	 * Find a column's index by its name.
	 * 
	 * @param col - The column's name (not case-sensitive)
	 * @return Column index if it was found, or -1 if it's not.
	 */
	public int getColumnIndex(String col) {
		Row row = xssfSheet.getRow(0);
		
		for (int i = 0; i < row.getPhysicalNumberOfCells(); i++)
			if (cellValueString(row, i).equalsIgnoreCase(col)) return i;
		
		System.err.println("Could not find the column " + col);
		return -1;
	}
	
	/**
	 * Delete an entire row (not only clear its content).
	 * 
	 * @param rowIndex - The index or the row to remove
	 */
	public void deleteRow(int rowIndex) {
		xssfSheet.removeRow(xssfSheet.getRow(rowIndex));
		xssfSheet.shiftRows(rowIndex + 1, xssfSheet.getLastRowNum(), -1);
		
	    for (int i = xssfSheet.getFirstRowNum(); i < xssfSheet.getLastRowNum() + 1; i++) {
	    	XSSFRow row = xssfSheet.getRow(i); 

	    	if (row != null) {
	    		long rRef = row.getCTRow().getR();

	    		for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
	    			XSSFCell cell = row.getCell(c);
	    			String cRef = cell.getCTCell().getR();
	    			cell.getCTCell().setR(cRef.replaceAll("[0-9]", "") + rRef);
	    		}
	    	}
	    }
	}
	
	/**
	 * Get a cell's value as type Object.
	 * 
	 * @param rowIndex - The index of the cell's row
	 * @param cellIndex - The index of the specific cell
	 * @return The value of the cell as type Object.
	 */
	public Object getGenericCellValue(int rowIndex, int cellIndex) {
		XSSFRow row = xssfSheet.getRow(rowIndex);
		XSSFCell cell = row.getCell(cellIndex);
		return getGenericCellValue(cell);
	}
	
	/**
	 * Get a cell's value as type Object.
	 * 
	 * @param cell - The cell object
	 * @return The value of the cell as type Object.
	 */
	public static Object getGenericCellValue(XSSFCell cell) {
		Object value;
		
		switch (cell.getCellType()) {
			case NUMERIC: value = cell.getNumericCellValue(); break;
			case BOOLEAN: value = cell.getBooleanCellValue(); break;
			default: value = cell.getStringCellValue();
		}
		
		return value;
	}
	
	/**
	 * Get the String value of a cell.
	 * @param row - The row of the cell (Row object)
	 * @param column - Cell index
	 * @return The cell's value as type String.
	 */
	public String cellValueString(Row row, int column) {
		return formatter.formatCellValue(row.getCell(column));
	}
	
	/**
	 * Get the String value of a cell.
	 * 
	 * @param row - The row of the cell (Row object)
	 * @param column - Column name
	 * @return The cell's value as type String.
	 */
	public String cellValueString(Row row, String column) {
		return formatter.formatCellValue(row.getCell(getColumnIndex(column)));
	}
	
	/**
	 * Get the numeric value of a cell.
	 * 
	 * @param row - The row of the cell (Row object)
	 * @param column - Cell index
	 * @return The cell's value as type double.
	 */
	public Double cellValueNumeric(Row row, int column) {
		return Double.parseDouble(cellValueString(row, column));
	}
	
	/**
	 * Get the numeric value of a cell.
	 * 
	 * @param row - The row of the cell (Row object)
	 * @param column - Column name
	 * @return The cell's value as type double.
	 */
	public Double cellValueNumeric(Row row, String column) {
		return Double.parseDouble(cellValueString(row, column));
	}
	
	/**
	 * Get the numeric value of a cell.
	 * 
	 * @param row - The row of the cell (Row object)
	 * @param column - Cell index
	 * @return The cell's value as type boolean.
	 */
	public Boolean cellValueBoolean(Row row, int column) {
		return Boolean.parseBoolean(cellValueString(row, column));
	}
	
	/**
	 * Get the boolean value of a cell.
	 * 
	 * @param row - The row of the cell (Row object)
	 * @param column - Column name
	 * @return The cell's value as type boolean.
	 */
	public Boolean cellValueBoolean(Row row, String column) {
		return Boolean.parseBoolean(cellValueString(row, column));
	}
	
	/**
	 * Get the source xssf sheet of this Sheet object.
	 * The source object is of XSSFSheet type and has more operative functionality.
	 * 
	 * @return XSSFSheet object that this object uses.
	 */
	public XSSFSheet getSource() { return xssfSheet; }
}