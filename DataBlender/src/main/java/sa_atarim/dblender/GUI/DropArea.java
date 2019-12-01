package sa_atarim.dblender.GUI;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;
import javax.swing.JTextArea;

public class DropArea extends JTextArea
{
	private static class CustomizedDropTarget extends DropTarget
	{
		private static enum Error {
			ONLY_ONE_FILE("This area can only accept one file."),
			TYPE_NOT_SUPPORTED("The file type you entered is not supported.");
			
			private String message;
			
			private Error(String msg) {
				this.message = msg;
			}
		}
		
		private static final long serialVersionUID = 5250399025632065423L;
		private static final String[] ALLOWED_TYPES = { "xls", "xlsx" };
		
		private String filePath;
		
		@SuppressWarnings("unchecked")
		public synchronized void drop(DropTargetDropEvent evt) {
	        try {
	            evt.acceptDrop(DnDConstants.ACTION_COPY);
	            Transferable eventTransferable = evt.getTransferable();
	            DataFlavor javaFilesFlavour = DataFlavor.javaFileListFlavor;
				List<File> droppedFile = (List<File>) eventTransferable.getTransferData(javaFilesFlavour);
				
				//validate input
				if (droppedFile.size() > 1) throwError(Error.ONLY_ONE_FILE);
				else {
					File file = droppedFile.get(0);
					String path = file.getPath();
					
					if (!isTypeAllowed(getFileExtension(path))) throwError(Error.TYPE_NOT_SUPPORTED);
					else filePath = path;
				}
	        }
	        catch (Exception ex) { ex.printStackTrace(); }
	    }
		
		private String getFileExtension(String path) {
			return path.substring(path.indexOf('.') + 1);
		}
		
		public String getFilePath() { return filePath; }
		
		public boolean hasFile() { return filePath != null; }
		
		private boolean isTypeAllowed(String type) {
			for (String extension : ALLOWED_TYPES)
				if (type.equals(extension)) return true;
			
			return false;
		}
		
		private static void throwError(Error error) {
			System.out.println(error.message);
		}
	}
	
	private static final long serialVersionUID = 1L;
	
	private CustomizedDropTarget dropTarget;

	/**
	 * @param dim - The dimension of the area
	 */
	public DropArea() {
		this.dropTarget = new CustomizedDropTarget();
		
		setEditable(false);
		setDropTarget(dropTarget);
	}
	
	public String getPath() { return dropTarget.getFilePath(); }
	
	public boolean hasFile() { return dropTarget.hasFile(); }
}