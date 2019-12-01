package sa_atarim.dblender.output;
import java.util.HashSet;
import java.util.Set;
import sa_atarim.dblender.sheets.XLSFile;

public class FileSpecifications
{
	private XLSFile file;
	private Set<String> columns;
	
	public FileSpecifications(XLSFile file) {
		this.file = file;
		this.columns = new HashSet<String>();
	}
	
	public void addColumn(String col) { columns.add(col); }
	
	public Set<String> getColumns() { return columns; }
	
	public XLSFile getFile() { return file; }
}