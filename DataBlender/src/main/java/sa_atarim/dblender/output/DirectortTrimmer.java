package sa_atarim.dblender.output;

public class DirectortTrimmer
{
	/**
	 * @param path - The path of the file
	 * @return The file type (without the period).
	 */
	public static String extractFileExtension(String path) {
		try { return path.substring(path.indexOf('.') + 1); }
		catch (StringIndexOutOfBoundsException e) { return ""; }
	}
	
	/**
	 * @param path - The path of the file
	 * @return The name of the file (without extension).
	 */
	public static String extractFileName(String path) {
		try { return path.substring(path.lastIndexOf(getSlashVersion(path)) + 1, path.indexOf('.')); }
		catch (StringIndexOutOfBoundsException e) { return ""; }
	}
	
	/**
	 * @param path - The path of the file
	 * @return The name of the file (without extension).
	 */
	public static String extractDirectory(String path) {
		try { return path.substring(0, path.lastIndexOf(getSlashVersion(path))); }
		catch (StringIndexOutOfBoundsException e) { return ""; }
	}
	
	private static char getSlashVersion(String path) {
		return path.indexOf('\\') != -1 ? '\\' : '/';
	}
}