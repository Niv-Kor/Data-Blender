package sa_atarim.dblender.sheets;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class Sheet
{
	private DataFormatter formatter;
	private XSSFSheet xssfSheet;
	
	/**
	 * @param xssf - The original XSSFSheet object to work on
	 */
	public Sheet(XSSFSheet xssf) {
		this.xssfSheet = xssf;
		this.formatter = new DataFormatter();
	}
	
	public String[] getColumnNames() {
		XSSFRow firstRow = xssfSheet.getRow(getFirstRow());
		int columns = firstRow.getPhysicalNumberOfCells();
		String[] columnNames = new String[columns];
		
		for (int i = 0; i < columns; i++)
			columnNames[i] = cellValueString(firstRow, i);
		
		return columnNames;
	}
	
	public String[] getColumnAsString(String colName) {
		return getColumnAsString(getColumnIndex(colName));
	}
	
	public String[] getColumnAsString(int colIndex) {
		int rowCount = xssfSheet.getPhysicalNumberOfRows();
		String[] columnContent = new String[rowCount];
		
		for (int i = getFirstRow() + 1; i < xssfSheet.getLastRowNum(); i++) {
			XSSFRow row = xssfSheet.getRow(i);
			columnContent[i] = cellValueString(row, colIndex);
		}
		
		return columnContent;
	}
	
	public int getFirstRow() {
		return 0;
	}
	
	/**
	 * Find column's index by giving the column's name.
	 * 
	 * @param col - The column's name (not case-sensitive)
	 * @return column index if it was found, or -1 if it wasn't found.
	 */
	public int getColumnIndex(String col) {
		Row row = xssfSheet.getRow(0);
		
		for (int i = 0; i < row.getPhysicalNumberOfCells(); i++)
			if (cellValueString(row, i).equalsIgnoreCase(col)) return i;
		
		System.out.println("Could not find the column " + col);
		return -1;
	}
	
	public void deleteRow(int rowIndex) {
		
//		xssfSheet.removeRow(xssfSheet.getRow(rowIndex));
//		xssfSheet.shiftRows(rowIndex + 1, xssfSheet.getLastRowNum(), -1);
//		
//	    for (int i = xssfSheet.getFirstRowNum(); i < xssfSheet.getLastRowNum() + 1; i++) {
//	    	XSSFRow row = xssfSheet.getRow(i); 
//	    	
//	    	if (row != null) {
//	    		long rRef = row.getCTRow().getR();
//	    		
//	    		for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
//	    			XSSFCell cell = row.getCell(c);
//	    			String cRef = cell.getCTCell().getR();
//	    			cell.getCTCell().setR(cRef.replaceAll("[0-9]", "") + rRef);
//	    		}
//	    	}
//	    }
	    
	    xssfSheet.shiftRows(rowIndex + 1, xssfSheet.getLastRowNum() - 1, -1);

	     for (int r = xssfSheet.getFirstRowNum(); r < xssfSheet.getLastRowNum() + 1; r++) {
	      XSSFRow row = xssfSheet.getRow(r); 
	      if (row != null) {
	       long rRef = row.getCTRow().getR();
	       for (Cell cell : row) {
	        String cRef = ((XSSFCell) cell).getCTCell().getR();
	        ((XSSFCell) cell).getCTCell().setR(cRef.replaceAll("[0-9]", "") + rRef);
	       }
	      }
	     }
	}
	
	public Object getGenericCellValue(int rowIndex, int cellIndex) {
		XSSFRow row = xssfSheet.getRow(rowIndex);
		XSSFCell cell = row.getCell(cellIndex);
		return getGenericCellValue(cell);
	}
	
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
	 * @param column - Column index
	 * @return cell's string value.
	 */
	public String cellValueString(Row row, int column) {
		return formatter.formatCellValue(row.getCell(column));
	}
	
	/**
	 * Get the String value of a cell.
	 * 
	 * @param row - The row of the cell (Row object)
	 * @param column - Column name
	 * @return cell's string value.
	 */
	public String cellValueString(Row row, String column) {
		return formatter.formatCellValue(row.getCell(getColumnIndex(column)));
	}
	
	/**
	 * Get the double value of a cell.
	 * 
	 * @param row - The row of the cell (Row object)
	 * @param column - Column index
	 * @return cell's double value.
	 */
	public Double cellValueDouble(Row row, int column) {
		return Double.parseDouble(cellValueString(row, column));
	}
	
	/**
	 * Get the double value of a cell.
	 * 
	 * @param row - The row of the cell (Row object)
	 * @param column - Column name
	 * @return cell's double value.
	 */
	public Double cellValueDouble(Row row, String column) {
		return Double.parseDouble(cellValueString(row, column));
	}
	
	/**
	 * Get the source xssf sheet of this Sheet object.
	 * The source object is of XSSFSheet type and has more operative functionality.
	 * 
	 * @return XSSFSheet object that this object is built upon.
	 */
	public XSSFSheet getSource() { return xssfSheet; }
}