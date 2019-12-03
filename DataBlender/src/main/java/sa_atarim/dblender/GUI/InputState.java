package sa_atarim.dblender.GUI;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.concurrent.Callable;
import javaNK.util.GUI.swing.components.InteractiveIcon;
import javaNK.util.GUI.swing.containers.Window;
import javaNK.util.GUI.swing.state_management.State;
import javaNK.util.math.DimensionalHandler;
import sa_atarim.dblender.Constants;
import sa_atarim.dblender.GUI.StateManager.Substate;
import sa_atarim.dblender.GUI.column_selection.ColumnSelectionState;
import sa_atarim.dblender.GUI.column_selection.ColumnSelectionState.FileIndex;
import sa_atarim.dblender.GUI.column_selection.ColumnSelectionWindow;
import sa_atarim.dblender.error.ErrorMessage;
import sa_atarim.dblender.output.DirectortTrimmer;
import sa_atarim.dblender.output.FileSpecification;
import sa_atarim.dblender.output.OutputRequest;
import sa_atarim.dblender.sheets.Blender;
import sa_atarim.dblender.sheets.XLSFile;

public class InputState extends State implements PropertyChangeListener
{
	private static final int DROP_AREA_HEIGHT = 40;
	
	private GridBagConstraints gbc;
	private Dimension dropAreaDim;
	private DropArea fileDrop1, fileDrop2;
	private InteractiveIcon emptyColumnSettings, filledColumnSettings;
	private ColumnSelectionWindow columnSelectionWindow;
	private ColumnSelectionState selectionState;
	private XLSFile droppedFile1, droppedFile2;
	private FileSpecification file1Specification, file2Specification;
	private OutputRequest outputRequest;
	private Blender blender;
	
	public InputState(Window window) {
		super(window, 1);
		
		Dimension windowDim = window.getDimension();
		this.gbc = new GridBagConstraints();
		this.columnSelectionWindow = new ColumnSelectionWindow();
		this.outputRequest = new OutputRequest();
		this.blender = new Blender();
		this.dropAreaDim = DimensionalHandler.adjust(windowDim, 70, 100);
		
		State state = Substate.COLUMN_SELECTION.createInstance(columnSelectionWindow);
		this.selectionState = (ColumnSelectionState) state;
		selectionState.subscribePropertyChange(this);
		
		this.emptyColumnSettings = new InteractiveIcon(Constants.Icons.EMPTY_COLUMN_SETTINGS);
		emptyColumnSettings.setHoverIcon(Constants.Icons.HOVER_EMPTY_COLUMN_SETTINGS);
		this.filledColumnSettings = new InteractiveIcon(Constants.Icons.FILLED_COLUMN_SETTINGS);
		filledColumnSettings.setHoverIcon(Constants.Icons.HOVER_FILLED_COLUMN_SETTINGS);
		
		dropAreaDim.height = (int) DROP_AREA_HEIGHT;
		createPanel(new GridBagLayout(), windowDim, window.getColor());
		
		//blender icon
		InteractiveIcon blenderIcon = new InteractiveIcon(Constants.Icons.BLENDER);
		blenderIcon.setHoverIcon(Constants.Icons.HOVER_BLENDER);
		blenderIcon.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				try {
					//cannot blend
					if (droppedFile1 == null || droppedFile2 == null) {
						ErrorMessage.NO_FILES.pop();
						return null;
					}
					else {
						String filePath = saveFileToDirectory();
						processBlend(filePath);
					}
				}
				catch (IOException e) { e.printStackTrace(); }
				return null;
			}
		});
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		panes[0].add(blenderIcon, gbc);
		
		//file 1 drop area
		String defaultDropText = "Drop a file here";
		this.fileDrop1 = new DropArea(defaultDropText, "file 1 set");
		fileDrop1.setPreferredSize(dropAreaDim);
		fileDrop1.subscribePropertyChange(this);
		
		gbc.insets.top = 10;
		gbc.gridx = 1;
		gbc.gridy = 1;
		panes[0].add(fileDrop1, gbc);
		
		//file 1 clear button
		InteractiveIcon clearFile1 = new InteractiveIcon(Constants.Icons.CLEAR_X);
		clearFile1.setHoverIcon(Constants.Icons.HOVER_CLEAR_X);
		clearFile1.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				fileDrop1.setFile(null);
				outputRequest.removeFile(file1Specification);
				file1Specification = null;
				selectionState.reset(FileIndex.FILE_1);
				return null;
			}
		});
		
		gbc.insets.right = -40;
		gbc.gridx = 2;
		gbc.gridy = 1;
		panes[0].add(clearFile1, gbc);
		
		//file 2 drop area
		this.fileDrop2 = new DropArea(defaultDropText, "file 2 set");
		fileDrop2.setPreferredSize(dropAreaDim);
		fileDrop2.subscribePropertyChange(this);
		
		gbc.insets.right = 0;
		gbc.gridx = 1;
		gbc.gridy = 2;
		panes[0].add(fileDrop2, gbc);
		
		//file 1 clear button
		InteractiveIcon clearFile2 = new InteractiveIcon(Constants.Icons.CLEAR_X);
		clearFile2.setHoverIcon(Constants.Icons.HOVER_CLEAR_X);
		clearFile2.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				fileDrop2.setFile(null);
				outputRequest.removeFile(file2Specification);
				file2Specification = null;
				selectionState.reset(FileIndex.FILE_2);
				return null;
			}
		});
		
		gbc.insets.right = -40;
		gbc.gridx = 2;
		gbc.gridy = 2;
		panes[0].add(clearFile2, gbc);
		
		//column settings
		changeColumnSettingsIcon(false);
		Callable<Void> columnsSelectionFunction = new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				if (droppedFile1 == null || droppedFile2 == null)
					ErrorMessage.NO_FILES.pop();
				else
					StateManager.setState(columnSelectionWindow, selectionState);
				
				return null;
			}
		};
		
		emptyColumnSettings.setFunction(columnsSelectionFunction);
		filledColumnSettings.setFunction(columnsSelectionFunction);
	}
	
	private void changeColumnSettingsIcon(boolean filled) {
		InteractiveIcon icon = filled ? filledColumnSettings : emptyColumnSettings;
		InteractiveIcon oldIcon = filled ? emptyColumnSettings : filledColumnSettings;
		
		gbc.insets.right = 0;
		gbc.insets.left = -58;
		gbc.insets.bottom = -47;
		gbc.gridx = 0;
		gbc.gridy = 1;
		panes[0].remove(oldIcon);
		panes[0].add(icon, gbc);
	}
	
	public String saveFileToDirectory() {
		FileDialog dialog = new FileDialog((Frame) null, "Save Blended File");
	    dialog.setMode(FileDialog.SAVE);
	    dialog.setVisible(true);
	    String path = dialog.getDirectory();
	    String name = dialog.getFile();
	    return path + name + "." + Constants.SAVED_FILE_TYPE;
	}
	
	private void processBlend(String filePath) throws IOException {
		String directory = DirectortTrimmer.extractDirectory(filePath);
		String fileName = DirectortTrimmer.extractFileName(filePath);
		
		//verify file path
		if (directory != "" && fileName != "")
			outputRequest.setFilePath(filePath);
		
		//in case no directory has been selected
		if (outputRequest.isValid()) blender.blend(outputRequest);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		try {
			switch (evt.getPropertyName()) {
				case "file 1 set":
					String file1Path = (String) evt.getNewValue();
					
					if (file1Path != null) {
						droppedFile1 = new XLSFile(file1Path, false);
						file1Specification = new FileSpecification(droppedFile1);
						outputRequest.addFile(file1Specification);
						selectionState.addFile(droppedFile1, FileIndex.FILE_1);
					}
					break;
				case "file 2 set":
					String file2Path = (String) evt.getNewValue();
					
					if (file2Path != null) {
						droppedFile2 = new XLSFile(file2Path, false);
						file2Specification = new FileSpecification(droppedFile2);
						outputRequest.addFile(file2Specification);
						selectionState.addFile(droppedFile2, FileIndex.FILE_2);
					}
					break;
				case "file 1 add": file1Specification.addColumn((String) evt.getNewValue()); break;
				case "file 2 add": file2Specification.addColumn((String) evt.getNewValue()); break;
				case "file 1 remove": file1Specification.removeColumn((String) evt.getNewValue()); break;
				case "file 2 remove": file2Specification.removeColumn((String) evt.getNewValue()); break;
				case "key column set": outputRequest.setKeyColumn((String) evt.getNewValue()); break;
				case "intersection set": outputRequest.setIntersection((boolean) evt.getNewValue()); break;
				default: return;
			}
		}
		catch (IOException e) { System.err.println("Could not create " + Constants.SAVED_FILE_TYPE + " files."); }
	}
	
	@Override
	public void applyPanels() {
		window.insertPanel(panes[0], BorderLayout.PAGE_START);
	}
}