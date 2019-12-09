package sa_atarim.dblender.GUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.List;
import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javaNK.util.IO.DirectoryTrimmer;
import sa_atarim.dblender.Constants;
import sa_atarim.dblender.error.PopupError;

public class DropArea extends JTextPane
{
	private static class CustomizedDropTarget extends DropTarget
	{
		private static final long serialVersionUID = 5250399025632065423L;
		
		private String filePath;
		private DropArea dropArea;
		
		/**
		 * @param dropArea - The area that contains this target
		 */
		public CustomizedDropTarget(DropArea dropArea) {
			this.dropArea = dropArea;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public synchronized void drop(DropTargetDropEvent evt) {
	        try {
	            evt.acceptDrop(DnDConstants.ACTION_COPY);
	            Transferable eventTransferable = evt.getTransferable();
	            DataFlavor javaFilesFlavour = DataFlavor.javaFileListFlavor;
				List<File> droppedFile = (List<File>) eventTransferable.getTransferData(javaFilesFlavour);
				
				//validate input
				if (droppedFile.size() > 1) PopupError.MULTIPLE_FILES.pop();
				else {
					File file = droppedFile.get(0);
					String path = file.getPath();
					
					if (!isTypeAllowed(DirectoryTrimmer.extractFileExtension(path)))
						PopupError.TYPE_NOT_SUPPORTED.pop();
					else {
						filePath = path;
						dropArea.setFile(path);
					}
				}
	        }
	        catch (Exception ex) { ex.printStackTrace(); }
	    }
		
		/**
		 * Remove the file from this target.
		 */
		private void clear() { filePath = null; }
		
		/**
		 * @return The path of the file that had been dropped.
		 */
		public String getFilePath() { return filePath; }
		
		/**
		 * @return True if the target received a file.
		 */
		public boolean hasFile() { return filePath != null; }
		
		/**
		 * @param type - The type of the file that had been dropped
		 * @return True if the file is allowed to be dropped.
		 */
		private boolean isTypeAllowed(String type) {
			for (String extension : Constants.ALLOWED_FILE_TYPES)
				if (type.equals(extension)) return true;
			
			return false;
		}
	}
	
	private static class MyEditorKit extends StyledEditorKit
	{
		private static final long serialVersionUID = -7130624122663493785L;
		
		public ViewFactory getViewFactory() {
	        return new StyledViewFactory();
	    }
		
	    private static class StyledViewFactory implements ViewFactory
	    {
	        public View create(Element elem) {
	            String kind = elem.getName();
	            
	            if (kind != null) {
	                if (kind.equals(AbstractDocument.ContentElementName))
	                    return new LabelView(elem);
	                else if (kind.equals(AbstractDocument.ParagraphElementName))
	                    return new ParagraphView(elem);
	                else if (kind.equals(AbstractDocument.SectionElementName))
	                    return new CenteredBoxView(elem, View.Y_AXIS);
	                else if (kind.equals(StyleConstants.ComponentElementName))
	                    return new ComponentView(elem);
	                else if (kind.equals(StyleConstants.IconElementName))
	                    return new IconView(elem);
	            }
	 
	            return new LabelView(elem);
	        }
	    }
	}
	
	private static class CenteredBoxView extends BoxView
	{
	    public CenteredBoxView(Element elem, int axis) {
			super(elem, axis);
		}
	    
	    @Override
		protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
	        super.layoutMajorAxis(targetSpan,axis,offsets,spans);
	        int textBlockHeight = 0;
	        int offset = 0;
	 
	        for (int i = 0; i < spans.length; i++)
	            textBlockHeight = spans[i];
	        
	        offset = (targetSpan - textBlockHeight) / 2;
	        for (int i = 0; i < offsets.length; i++)
	            offsets[i] += offset;
	    }
	}   
	
	private static final long serialVersionUID = 1L;
	private static final float COMPRESSED_FONT = 10f;
	private static final Font FONT = Constants.Fonts.MAIN;
	private static final Color DEFAULT_TEXT_COLOR = new Color(180, 180, 180);
	private static final Color FILE_NAME_COLOR = new Color(80, 137, 63);
	private static final Color EMPTY_COLOR = new Color(0, 195, 255);
	private static final Color BRIGHT_EMPTY_COLOR = new Color(171, 235, 255);
	private static final Color OCCUPIED_COLOR = new Color(53, 221, 71);
	private static final Color BRIGHT_OCCUPIED_COLOR = new Color(186, 255, 193);
	
	private CustomizedDropTarget dropTarget;
	private PropertyChangeSupport propertyChange;
	private String defaultText, propertyChangeCode;
	private Color areaColor, brightAreaColor;

	/**
	 * @param defaultText - The text to show as a default when no file is present
	 * @param propertyChangeCode - The name of the property that notifies about a file being set
	 */
	public DropArea(String defaultText, String propertyChangeCode) {
		this.dropTarget = new CustomizedDropTarget(this);
		this.defaultText = defaultText;
		this.areaColor = EMPTY_COLOR;
		this.brightAreaColor = BRIGHT_EMPTY_COLOR;
		this.propertyChange = new PropertyChangeSupport(this);
		this.propertyChangeCode = propertyChangeCode;
		
		setEditable(false);
		setHighlighter(null);
		setOpaque(false);
		setDropTarget(dropTarget);
		setText(defaultText);
		setForeground(DEFAULT_TEXT_COLOR);
		setBackground(Constants.BOX_BACKGROUND);
		setFont(FONT);
		
		//center text
		setEditorKit(new MyEditorKit());
		StyledDocument doc = getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength() - 1, center, false);
        setText(defaultText);
	}
	
	/**
	 * @return The path of the file that had been dropped to the area.
	 */
	public String getPath() { return dropTarget.getFilePath(); }
	
	/**
	 * @return True if a file had been dropped to the area.
	 */
	public boolean isOccupied() { return dropTarget.hasFile(); }
	
	/**
	 * Consider this area occupied.
	 * 
	 * @param fileName - The name of the file
	 */
	public void setFile(String filePath) {
		String fileName = (filePath != null) ? DirectoryTrimmer.extractFileName(filePath) : null;
		
		if (fileName != null) {
			this.areaColor = OCCUPIED_COLOR;
			this.brightAreaColor = BRIGHT_OCCUPIED_COLOR;
			setForeground(FILE_NAME_COLOR);
			setText(fileName);
		}
		//clear file
		else {
			areaColor = EMPTY_COLOR;
			brightAreaColor = BRIGHT_EMPTY_COLOR;
			setForeground(DEFAULT_TEXT_COLOR);
			setText(defaultText);
			dropTarget.clear();
		}
		
		//send the file path to the listeners
		propertyChange.firePropertyChange(propertyChangeCode, "", filePath);
	}
	
	/**
	 * Assign a listener that will get notified whenever a file is set to the drop area.
	 * When a file is set, the listener will be notified with its path.
	 * 
	 * @param listener - The object to notify about changes
	 */
	public void subscribePropertyChange(PropertyChangeListener listener) {
		propertyChange.addPropertyChangeListener(listener);
	}
	
	/**
	 * Resize the file's name relative to its length,
	 * in order to make it compatible with the box's area.
	 */
	private void calcMaxCharsPerLine() {
		String text = getText();
		if (text == null || text.equals("")) return;
		
		Font font = Constants.Fonts.setByLanguage(text).deriveFont(15f);
		
		int fontSize = font.getSize();
		int width = getPreferredSize().width;
		int textLength = getText().length();
		int maxCharsPerLine = (int) (width / fontSize * 1.85f);
		float compressedScale = (textLength > maxCharsPerLine) ? COMPRESSED_FONT : font.getSize();
		setFont(font.deriveFont((float) compressedScale));
	}
	
	@Override
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(preferredSize);
		calcMaxCharsPerLine();
	}
	
	@Override
	public void setText(String text) {
		super.setText(text);
		calcMaxCharsPerLine();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
        super.paintComponent(g);
	}
	
	@Override
    protected void paintBorder(Graphics g) {
        g.setColor(areaColor);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
        g.setColor(brightAreaColor);
        g.drawRoundRect(3, 3, getWidth() - 7, getHeight() - 7, 25, 25);
	}
}