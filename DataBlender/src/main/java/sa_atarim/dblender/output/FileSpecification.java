package sa_atarim.dblender.output;
import java.util.HashSet;
import java.util.Set;
import sa_atarim.dblender.sheets.XLSFile;

public class FileSpecification
{
	private XLSFile file;
	private Set<String> columns;
	
	public FileSpecification(XLSFile file) {
		this.file = file;
		this.columns = new HashSet<String>();
	}
	
	public void addColumn(String col) { columns.add(col); }
	
	public Set<String> getColumns() { return columns; }
	
	public XLSFile getFile() { return file; }
}