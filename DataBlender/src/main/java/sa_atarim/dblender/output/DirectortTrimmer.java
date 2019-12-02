package sa_atarim.dblender.output;

public class DirectortTrimmer
{
	/**
	 * @param path - The path of the file
	 * @return The file type (without the period).
	 */
	public static String extractFileExtension(String path) {
		return path.substring(path.indexOf('.') + 1);
	}
	
	/**
	 * @param path - The path of the file
	 * @return The name of the file (without extension).
	 */
	public static String extractFileName(String path) {
		return path.substring(path.lastIndexOf(getSlashVersion(path)) + 1, path.indexOf('.'));
	}
	
	/**
	 * @param path - The path of the file
	 * @return The name of the file (without extension).
	 */
	public static String extractDirectory(String path) {
		return path.substring(0, path.lastIndexOf(getSlashVersion(path)));
	}
	
	private static char getSlashVersion(String path) {
		return path.indexOf('\\') != -1 ? '\\' : '/';
	}
}