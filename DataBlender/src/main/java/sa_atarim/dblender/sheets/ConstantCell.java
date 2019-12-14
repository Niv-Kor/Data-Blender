package sa_atarim.dblender.sheets;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

public class ConstantCell
{
	public int index;
	public Object value;
	public CellStyle cellStyle;
	
	/**
	 * @param index - Index of the cell
	 * @param cell - The cell to take the value from
	 */
	public ConstantCell(int index, Cell cell) {
		this.index = index;
		this.value = CellFormat.getGenericValue(cell);
		this.cellStyle = (cell != null) ? cell.getCellStyle() : null;
	}
	
	@Override
	public boolean equals(Object other) {
		try {
			ConstantCell otherCell = (ConstantCell) other;
			return value.equals(otherCell.value);
		}
		catch (ClassCastException e) { return false; }
	}
	
	@Override
	public String toString() { return String.valueOf(value); }
}