package sa_atarim.dblender.sheets;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IgnoredErrorType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class SheetModifier
{
	private static final int FIRST_ROW_CHECK_SAMPLE = 25;
	
	private DataFormatter formatter;
	private XSSFSheet xssfSheet;
	private int headerRowIndex; 
	
	/**
	 * @param xssf - The original XSSFSheet object to modify
	 */
	public SheetModifier(XSSFSheet xssf) {
		this.xssfSheet = xssf;
		this.formatter = new DataFormatter();
		this.headerRowIndex = findHeaderRowIndex();
	}
	
	/**
	 * @return An array of the columns names (the values of the headers).
	 */
	public String[] getColumnNames() {
		Row firstRow = xssfSheet.getRow(headerRowIndex);
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
		String[] columnContent = new String[rowCount - 1];
		
		for (int r = headerRowIndex + 1, i = 0; r < rowCount && i < columnContent.length; r++, i++) {
			Row row = xssfSheet.getRow(r);
			columnContent[i] = cellValueString(row, colIndex);
		}
		
		return columnContent;
	}
	
	/**
	 * Check if an entire column is full with data.
	 * 
	 * @param colName - The name of the column
	 * @return True if the entire column is full with data.
	 */
	public boolean isColumnFull(String colName) {
		return isColumnFull(getColumnIndex(colName));
	}
	
	/**
	 * Check if an entire column is full with data.
	 * 
	 * @param colIndex - The index of the column
	 * @return True if the entire column is full with data.
	 */
	public boolean isColumnFull(int colIndex) {
		String[] colContent = getEntireColumn(colIndex);
		int nonNullCounter = 0;
		
		for (String row : colContent)
			if (row != null && !row.equals("")) nonNullCounter++;
		
		return nonNullCounter == colContent.length;
	}
	
	/**
	 * @return The index of the row that contains the headers.
	 */
	public int findHeaderRowIndex() {
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
	 * @return True if the file contains no data.
	 */
	public boolean isEmpty() { return getColumnsAmount() == 0; }
	
	/**
	 * @return The amount of columns in the sheet.
	 */
	public int getColumnsAmount() {
		try {
			Row headerRow = xssfSheet.getRow(headerRowIndex);
			return headerRow.getPhysicalNumberOfCells();
		}
		catch (Exception e) { return 0; }
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
		
		if (rowIndex < xssfSheet.getPhysicalNumberOfRows())
			shiftRows(rowIndex + 1, xssfSheet.getPhysicalNumberOfRows(), -1);
	}
	
	/**
	 * Shift a block of rows downwards or upwards.
	 * This method overrides a bug in the XSSFSheet's 'shiftRows' method.
	 * 
	 * @param startRow - The index of the first row in the block
	 * @param n - Amount of rows to shift downwards (negative to shift upwards)
	 */
	public void shiftRows(int startRow, int n) {
		shiftRows(startRow, xssfSheet.getPhysicalNumberOfRows(), n);
	}
	
	/**
	 * Shift a block of rows downwards or upwards.
	 * This method overrides a bug in the XSSFSheet's 'shiftRows' method.
	 * 
	 * @param startRow - The index of the first row in the block
	 * @param endRow - The index of the last row in the block
	 * @param n - Amount of rows to shift downwards (negative to shift upwards)
	 */
	public void shiftRows(int startRow, int endRow, int n) {
		xssfSheet.shiftRows(startRow, endRow, n);
		
		for (int i = xssfSheet.getFirstRowNum(); i < xssfSheet.getLastRowNum() + 1; i++) {
	    	XSSFRow row = xssfSheet.getRow(i); 

	    	if (row != null) {
	    		long rRef = row.getCTRow().getR();

	    		for (int c = 0; c < getColumnsAmount(); c++) {
	    			XSSFCell cell = row.getCell(c);
	    			
	    			if (cell == null) continue;
	    			
	    			String cRef = cell.getCTCell().getR();
	    			cell.getCTCell().setR(cRef.replaceAll("[0-9]", "") + rRef);
	    		}
	    	}
	    }
	}
	
	/**
	 * Take a row of the sheet and insert it right after another row.
	 * The original row is deleted and created again after the desired index.
	 * 
	 * @param rowIndex - The index of the row to shift
	 * @param afterRow - Insert the shifted row after this row's index
	 */
	public void insertRowAfter(int rowIndex, int afterRow) {
		//copy the row
		Row originRow = xssfSheet.getRow(rowIndex);
		ConstantCell[] originValues = new ConstantCell[xssfSheet.getRow(headerRowIndex).getPhysicalNumberOfCells()];
		
		for (int i = 0; i < originValues.length; i++)
			originValues[i] = new ConstantCell(i, originRow.getCell(i));
		
		//delete the row
		deleteRow(rowIndex);
		if (afterRow > rowIndex) afterRow--;
		
		insertRowAfter(originValues, afterRow);
	}
	
	/**
	 * @see #insertRowAfter(int, int)
	 * @param rowBuffer - An array that contains the entire row to insert
	 * @param afterRow - Insert the row after this row's index
	 */
	public void insertRowAfter(ConstantCell[] rowBuffer, int afterRow) {
		//shift everything down by 1
		shiftRows(afterRow + 1, 1);
		
		//paste the row
		Row destRow = xssfSheet.createRow(afterRow + 1);
		
		for (int i = 0; i < rowBuffer.length; i++) {
			Cell destCell = destRow.createCell(i);
			CellFormat.setGenericValue(destCell, rowBuffer[i].value);
		}
	}
	
	/**
	 * Add a statement that makes the file ignore cell format warnings.
	 * Writing a String value to a numeric cell will not cause problems.
	 */
	public void ignoreCellFormatWarnings() {
		int rowsAmount = xssfSheet.getPhysicalNumberOfRows();
		
		if (rowsAmount >= 1) {
			int colAmount = xssfSheet.getRow(headerRowIndex).getPhysicalNumberOfCells();
			CellRangeAddress cellRange = new CellRangeAddress(0, rowsAmount, 0, colAmount);
			xssfSheet.addIgnoredErrors(cellRange, IgnoredErrorType.NUMBER_STORED_AS_TEXT);
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
		Row row = xssfSheet.getRow(rowIndex);
		Cell cell = row.getCell(cellIndex);
		return CellFormat.getGenericValue(cell);
	}
	
	/**
	 * Horizontally align all cells in the sheet.
	 * 
	 * @param headers - Horizontal alignment for the headers row
	 * @param data - Horizontal alignment for data
	 */
	public void alignCells(HorizontalAlignment headers, HorizontalAlignment data) {
		int colCount = getColumnsAmount();
		int headerIndex = headerRowIndex;
		CellStyle headerStyle = xssfSheet.getWorkbook().createCellStyle();
		CellStyle dataStyle = xssfSheet.getWorkbook().createCellStyle();
		headerStyle.setAlignment(headers);
		dataStyle.setAlignment(data);
		
		for (int r = 0; r < xssfSheet.getPhysicalNumberOfRows(); r++) {
			HorizontalAlignment alignment = (r == headerIndex) ? headers : data;
			CellStyle style = (r == headerIndex) ? headerStyle : dataStyle;
			Row row = xssfSheet.getRow(r);
			
			if (row != null) {
				for (int c = 0; c < colCount; c++) {
					Cell cell = row.getCell(c);
					
					if (cell != null) {
						style.cloneStyleFrom(cell.getCellStyle());
						style.setAlignment(alignment);
						cell.setCellStyle(style);
					}
				}
			}
		}
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
	
	public int getHeaderRowIndex() { return headerRowIndex; }
	
	/**
	 * Get the source xssf sheet of this Sheet object.
	 * The source object is of XSSFSheet type and has more operative functionality.
	 * 
	 * @return XSSFSheet object that this object uses.
	 */
	public XSSFSheet getSource() { return xssfSheet; }
}