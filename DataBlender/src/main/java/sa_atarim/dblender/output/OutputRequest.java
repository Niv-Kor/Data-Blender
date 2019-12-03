package sa_atarim.dblender.output;
import java.util.ArrayList;
import java.util.List;

public class OutputRequest
{
	private List<FileSpecification> files;
	private String filePath, keyColumn;
	private boolean intersect;
	
	public OutputRequest() {
		this.files = new ArrayList<FileSpecification>();
		this.intersect = false;
	}
	
	public boolean isValid() {
		return filePath != null &&
			   keyColumn != null &&
			   files.size() > 1;
	}
	
	public void addFile(FileSpecification fileRequest) { files.add(fileRequest); }
	
	public void removeFile(FileSpecification fileRequest) { files.remove(fileRequest); }
	
	public List<FileSpecification> getFiles() { return files; }
	
	public String getFilePath() { return filePath; }
	
	public String getKeyColumn() { return keyColumn; }
	
	public void setFilePath(String path) { filePath = path; }
	
	public void setKeyColumn(String col) {
		keyColumn = col;
		
		//add key column to all files
		if (col != null) {
			for (FileSpecification file : files) {
				file.addColumn(col);
				file.sortKeyFirst(col);
			}
		}
	}
	
	public boolean usesIntersection() { return intersect; }
	
	public void setIntersection(boolean flag) { intersect = flag; }
}