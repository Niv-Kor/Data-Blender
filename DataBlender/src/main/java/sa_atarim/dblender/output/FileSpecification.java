package sa_atarim.dblender.output;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sa_atarim.dblender.sheets.SheetModifier;
import sa_atarim.dblender.sheets.XLSFile;

public class FileSpecification
{
	private XLSFile file;
	private Map<String, Integer> columns;
	private boolean isKeyUnique;

	/**
	 * @param file
	 *            - The origin file
	 */
	public FileSpecification(XLSFile file) {
		this.file = file;
		this.columns = new HashMap<String, Integer>();
		this.isKeyUnique = false;
	}

	/**
	 * Add a column to the specification. Only columns that are added can be
	 * integrated into an output file.
	 * 
	 * @param col - The column's name
	 * @param index - The index of the column
	 */
	public void addColumn(String col, int index) {
		columns.put(col, index);
		System.out.println("after add " + col + " now " + columns);
	}

	/**
	 * Add a key column to the specification.
	 * 
	 * @param col - The column's name
	 */
	public void addKeyColumn(String col) {
		columns.put(col, 0);
		isKeyUnique = isUnique(col);
	}

	/**
	 * Check if every value in a column is unique.
	 * 
	 * @param col - The column's name
	 * @return True if every value in the column in unique.
	 */
	private boolean isUnique(String col) {
		SheetModifier sheet = file.getSheet();
		String[] columnContent = sheet.getEntireColumn(col);
		Set<String> values = new HashSet<String>();

		for (String value : columnContent) {
			if (!values.contains(value))
				values.add(value);
			else
				return false;
		}

		return true;
	}

	/**
	 * Remove a column from the specification.
	 * 
	 * @param col
	 *            - The column's name
	 */
	public void removeColumn(String col) {
		columns.remove(col);
	}

	/**
	 * @return A list of the added columns.
	 */
	public Map<String, Integer> getColumns() {
		return columns;
	}

	/**
	 * @return True if every value in the key column is unique.
	 */
	public boolean isKeyUnique() {
		return isKeyUnique;
	}

	/**
	 * @return The origin file.
	 */
	public XLSFile getFile() {
		return file;
	}
}