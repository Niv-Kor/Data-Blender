package sa_atarim.dblender.output;
import java.util.ArrayList;
import java.util.List;
import sa_atarim.dblender.sheets.XLSFile;

public class OutputRequest
{
	private List<FileSpecification> files;
	private String filePath, keyColumn;
	private boolean intersect;
	
	public OutputRequest() {
		this.files = new ArrayList<FileSpecification>();
		this.intersect = false;
	}
	
	/**
	 * @return True if the request is ready for execution.
	 */
	public boolean isValid() {
		return filePath != null &&
			   keyColumn != null &&
			   files.size() > 1;
	}
	
	/**
	 * Add a file that needs to be integrated into the output file.
	 * 
	 * @param file - The file's specification
	 */
	public void addFile(FileSpecification file) { files.add(file); }
	
	/**
	 * Remove a file from the list of integrating files.
	 * 
	 * @param file - The file's specification
	 */
	public void removeFile(FileSpecification file) { files.remove(file); }
	
	/**
	 * Remove a file from the list of integrating files.
	 * 
	 * @param file - The origin file
	 */
	public void removeFile(XLSFile file) {
		for (FileSpecification spec : files)
			if (spec.getFile() == file) files.remove(spec);
	}
	
	/**
	 * @return A list of all integrating files' specifications.
	 */
	public List<FileSpecification> getFiles() { return files; }
	
	/**
	 * @return The requested path of the output file.
	 */
	public String getFilePath() { return filePath; }
	
	/**
	 * @param path - The new path of the output file
	 */
	public void setFilePath(String path) { filePath = path; }
	
	/**
	 * @return The requested key column of the output file.
	 */
	public String getKeyColumn() { return keyColumn; }
	
	/**
	 * @param col - The new key column of the output file
	 */
	public void setKeyColumn(String col) { keyColumn = col; }
	
	/**
	 * @return True if intersection mode is on.
	 */
	public boolean usesIntersection() { return intersect; }
	
	/**
	 * @param flag - True to activate intersection mode
	 */
	public void setIntersection(boolean flag) { intersect = flag; }
}