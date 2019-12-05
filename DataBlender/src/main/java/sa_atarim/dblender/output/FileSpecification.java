package sa_atarim.dblender.output;
import java.util.ArrayList;
import java.util.List;
import sa_atarim.dblender.sheets.XLSFile;

public class FileSpecification
{
	private XLSFile file;
	private List<String> columns;
	
	/**
	 * @param file - The origin file
	 */
	public FileSpecification(XLSFile file) {
		this.file = file;
		this.columns = new ArrayList<String>();
	}
	
	/**
	 * Add a column to the specification.
	 * Only column's that are added can be integrated into an output file.
	 * 
	 * @param col - The column's name 
	 */
	public void addColumn(String col) {
		if (!columns.contains(col)) columns.add(col);
	}
	
	/**
	 * Remove a column from the specification.
	 * 
	 * @param col - The column's name
	 */
	public void removeColumn(String col) { columns.remove(col); }
	
	/**
	 * Sort the columns so the key column is first.
	 * 
	 * @param key - The name of the key column
	 */
	public void sortKeyFirst(String key) {
		if (!columns.contains(key)) return;
		
		List<String> sortedList = new ArrayList<String>();
		sortedList.add(key);
		
		for (String col : columns)
			if (!col.equals(key)) sortedList.add(col);
		
		columns = sortedList;
	}
	
	/**
	 * @return A list of the added columns.
	 */
	public List<String> getColumns() { return columns; }
	
	/**
	 * @return The origin file.
	 */
	public XLSFile getFile() { return file; }
}