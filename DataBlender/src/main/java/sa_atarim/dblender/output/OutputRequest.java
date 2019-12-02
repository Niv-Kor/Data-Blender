package sa_atarim.dblender.output;
import java.util.ArrayList;
import java.util.List;

public class OutputRequest
{
	public static final String OUTPUT_FILE_TYPE = "xlsx";
	
	private List<FileSpecification> files;
	private String filePath, fileName;
	private String keyColumn;
	private boolean intersect;
	
	public OutputRequest(String path, String name, String keyColumn, boolean intersect) {
		this.files = new ArrayList<FileSpecification>();
		this.filePath = path;
		this.fileName = name;
		this.keyColumn = keyColumn;
		this.intersect = intersect;
	}
	
	public void addFile(FileSpecification fileRequest) { files.add(fileRequest); }
	
	public List<FileSpecification> getFiles() { return files; }
	
	public String getFileName() { return fileName; }
	
	public String getFilePath() { return filePath; }
	
	public String getFullPath() { return filePath + "\\" + fileName + "." + OUTPUT_FILE_TYPE; }
	
	public String getKeyColumn() { return keyColumn; }
	
	public boolean usesIntersection() { return intersect; }
}