package sa_atarim.dblender.sheets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import javaNK.util.math.Range;

public enum CellFormat
{
	NUMERIC,
	BOOLEAN,
	STRING,
	DATE;
	
	private final static Range<Integer> DIGITS = new Range<Integer>((int) '0', (int) '9');
	private final static NumberFormat DECIMAL_FORMAT = new DecimalFormat("#,###.00");
	private final static DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	private static DataFormatter formatter = new DataFormatter();
	
	/**
	 * Get a cell's format type. 
	 * 
	 * @param cell - The cell instance to check
	 * @return the cell's format type
	 */
	public static CellFormat getFormat(Cell cell) {
		if (cell == null) return STRING;
		
		String strVal = formatter.formatCellValue(cell);
		boolean containsSlash = false;
		boolean containsNumSign = false;
		boolean allNums = true;
		
		if (strVal.equals("true") || strVal.equals("false")) return BOOLEAN;
		
		//analyze string representation
		for (int i = 0; i < strVal.length(); i++) {
			char c = strVal.charAt(i);
			
			if (c == '/') containsSlash = true;
			else if (c == '-' || c == '.' || c == ',') containsNumSign = true;
			else if (!DIGITS.intersects((int) c)) allNums = false;
		}
		
		//period cancels slash
		containsSlash = containsSlash && !containsNumSign;
		
		if (containsSlash && allNums) return DATE;
		else if (allNums && containsNumSign && !strVal.equals("")) return NUMERIC;
		else return STRING;
	}
	
	/**
	 * Get a cell's value as type Object.
	 * 
	 * @param cell - The cell object
	 * @return The value of the cell as type Object.
	 */
	public static Object getGenericValue(Cell cell) {
		if (cell == null) return "";
		
		CellFormat format = getFormat(cell);
		Object value = null;
		
		switch (format) {
			case STRING: {
				try { value = cell.getStringCellValue(); }
				catch (IllegalStateException e) {
					double numeric = cell.getNumericCellValue();
					String strVal = "" + numeric;
					
					//cut all decimal digits
					int periodIndex = strVal.indexOf('.');
					if (periodIndex != -1) strVal = strVal.substring(0, periodIndex);
					value = strVal.replace("-", "").replace(".", "").replace(",", "");
				}
				break;
			}
			case BOOLEAN:
				value = cell.getBooleanCellValue();
				break;
				
			case DATE:
				try { value = DATE_FORMAT.format(cell.getDateCellValue()); }
				catch (IllegalStateException e) { value = cell.getStringCellValue(); }
				break;
				
			case NUMERIC:
				try { value = DECIMAL_FORMAT.format(cell.getNumericCellValue()); }
				catch (IllegalStateException | ArithmeticException e) { value = cell.getStringCellValue(); }
				break;
		}
		
		return value;
	}
	
	/**
	 * Set a cell's value using a generic Object instance.
	 * 
	 * @param cell - The cell to change
	 * @param value - The content to set
	 */
	public static void setGenericValue(Cell cell, Object value) {
		if (cell == null) return;
		
		CellFormat format = getFormat(cell);
		String strVal = String.valueOf(value);
		
		switch (format) {
			case STRING: cell.setCellValue(strVal); break;
			case NUMERIC: cell.setCellValue(Double.parseDouble(strVal)); break;
			case BOOLEAN: cell.setCellValue(Boolean.parseBoolean(strVal)); break;
			case DATE: cell.setCellValue(strVal); break;
		}
	}
	
	/**
	 * Copy a cell's content to another cell.
	 * 
	 * @param origin - The cell to copy the value from
	 * @param dest - The cell to copy the value into
	 */
	public static void copyCell(Cell origin, Cell dest) {
		Object originValue = getGenericValue(origin);
		setGenericValue(dest, originValue);
	}
}