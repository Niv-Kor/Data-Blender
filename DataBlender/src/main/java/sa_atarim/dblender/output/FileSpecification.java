package sa_atarim.dblender.output;
import java.util.ArrayList;
import java.util.List;
import sa_atarim.dblender.sheets.XLSFile;

public class FileSpecification
{
	private XLSFile file;
	private List<String> columns;
	
	public FileSpecification(XLSFile file) {
		this.file = file;
		this.columns = new ArrayList<String>();
	}
	
	public void addColumn(String col) {
		if (!columns.contains(col)) columns.add(col);
	}
	
	public void sortKeyFirst(String key) {
		if (!columns.contains(key)) return;
		
		List<String> sortedList = new ArrayList<String>();
		sortedList.add(key);
		
		for (String col : columns)
			if (!col.equals(key)) sortedList.add(col);
		
		columns = sortedList;
	}
	
	public void removeColumn(String col) { columns.remove(col); }
	
	public List<String> getColumns() { return columns; }
	
	public XLSFile getFile() { return file; }
}