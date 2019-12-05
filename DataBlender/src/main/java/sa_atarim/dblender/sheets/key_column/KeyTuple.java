package sa_atarim.dblender.sheets.key_column;
import org.apache.poi.xssf.usermodel.XSSFCell;
import sa_atarim.dblender.sheets.SheetModifier;

public class KeyTuple
{
	public int rowIndex;
	public Object value;
	
	/**
	 * A singleton way of creating a KeyTuple object.
	 * A key tuple contains of a key value and a row index when the value exists.
	 * 
	 * @param rowIndex - The index of the row that contains the key value
	 * @param cell - The cell that contains the key value
	 * @return A new KeyTuple object.
	 */
	public static KeyTuple create(int rowIndex, XSSFCell cell) {
		return new KeyTuple(rowIndex, SheetModifier.getGenericCellValue(cell));
	}
	
	/**
	 * @param row - The index of the row that contains the key value
	 * @param val - Key value
	 */
	private KeyTuple(int row, Object val) {
		this.rowIndex = row;
		this.value = val;
	}
	
	/**
	 * Compare this object's key value with another key value, using String comparison.
	 * 
	 * @param other - The other value
	 * @return True if both values are equal.
	 */
	public boolean valueEquals(Object other) {
		String otherVal = (String) other;
		String thisVal = (String) value;
		return thisVal.equals(otherVal);
	}
}