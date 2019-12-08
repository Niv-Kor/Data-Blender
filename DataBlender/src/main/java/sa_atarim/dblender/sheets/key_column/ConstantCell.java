package sa_atarim.dblender.sheets.key_column;
import org.apache.poi.ss.usermodel.Cell;

public class ConstantCell
{
	public int index;
	public Object value;
	
	/**
	 * @param index - Index of the cell
	 * @param val - Value of the cell
	 */
	public ConstantCell(int index, Object val) {
		this.index = index;
		this.value = val;
	}
	
	/**
	 * @param index - Index of the cell
	 * @param cell - The cell to take the value from
	 */
	public ConstantCell(int index, Cell cell) {
		this.index = index;
		this.value = cell.getStringCellValue();
	}
	
	@Override
	public boolean equals(Object other) {
		try {
			ConstantCell otherCell = (ConstantCell) other;
			return value.equals(otherCell.value);
		}
		catch (ClassCastException e) { return false; }
	}
}