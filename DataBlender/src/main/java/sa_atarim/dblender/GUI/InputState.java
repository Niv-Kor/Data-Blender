package sa_atarim.dblender.GUI;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.Callable;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javaNK.util.GUI.swing.components.InteractiveIcon;
import javaNK.util.GUI.swing.containers.Window;
import javaNK.util.GUI.swing.state_management.State;
import javaNK.util.files.ImageHandler;
import javaNK.util.math.DimensionalHandler;
import sa_atarim.dblender.Constants;
import sa_atarim.dblender.error.ErrorMessage;
import sa_atarim.dblender.output.DirectortTrimmer;
import sa_atarim.dblender.output.FileSpecification;
import sa_atarim.dblender.output.OutputRequest;
import sa_atarim.dblender.sheets.Blender;
import sa_atarim.dblender.sheets.XLSFile;

public class InputState extends State
{
	private static final int DROP_AREA_HEIGHT = 40;
	
	private GridBagConstraints gbc;
	private Dimension dropAreaDim;
	private DropArea fileDrop1, fileDrop2;
	private InteractiveIcon emptyColumnSettings, filledColumnSettings;
	
	public InputState(Window window) {
		super(window, 1);
		
		Dimension windowDim = window.getDimension();
		this.gbc = new GridBagConstraints();
		this.dropAreaDim = DimensionalHandler.adjust(windowDim, 70, 100);
		this.emptyColumnSettings = new InteractiveIcon((ImageIcon) Constants.EMPTY_COLUMN_SETTINGS_ICON);
		emptyColumnSettings.setHoverIcon((ImageIcon) Constants.HOVER_EMPTY_COLUMN_SETTINGS_ICON);
		this.filledColumnSettings = new InteractiveIcon((ImageIcon) Constants.FILLED_COLUMN_SETTINGS_ICON);
		filledColumnSettings.setHoverIcon((ImageIcon) Constants.HOVER_FILLED_COLUMN_SETTINGS_ICON);
		
		ImageIcon clearButton = ImageHandler.loadIcon("/icons/clear.png");
		ImageIcon hoverClearButton = ImageHandler.loadIcon("/icons/hover_clear.png");
		dropAreaDim.height = (int) DROP_AREA_HEIGHT;
		createPanel(new GridBagLayout(), windowDim, window.getColor());
		
		//blender icon
		InteractiveIcon blenderIcon = new InteractiveIcon((ImageIcon) Constants.BLENDER_ICON);
		blenderIcon.setHoverIcon((ImageIcon) Constants.HOVER_BLENDER_ICON);
		blenderIcon.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				try {
					XLSFile droppedFile1 = fileDrop1.isOccupied() ? new XLSFile(fileDrop1.getPath(), false) : null;
					XLSFile droppedFile2 = fileDrop2.isOccupied() ? new XLSFile(fileDrop2.getPath(), false) : null;
					
					//cannot blend
					if (droppedFile1 == null || droppedFile2 == null) {
						ErrorMessage.NO_FILES.pop();
						return null;
					}
					else {
						String filePath = saveFileToDirectory();
						processBlend(filePath, droppedFile1, droppedFile2);
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
		this.fileDrop1 = new DropArea(defaultDropText);
		fileDrop1.setPreferredSize(dropAreaDim);
		
		gbc.insets.top = 10;
		gbc.gridx = 1;
		gbc.gridy = 1;
		panes[0].add(fileDrop1, gbc);
		
		//file 1 clear button
		InteractiveIcon clearFile1 = new InteractiveIcon(clearButton);
		clearFile1.setHoverIcon(hoverClearButton);
		clearFile1.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				fileDrop1.setFile(null);
				return null;
			}
		});
		
		gbc.insets.right = -40;
		gbc.gridx = 2;
		gbc.gridy = 1;
		panes[0].add(clearFile1, gbc);
		
		//file 2 drop area
		this.fileDrop2 = new DropArea(defaultDropText);
		fileDrop2.setPreferredSize(dropAreaDim);
		
		gbc.insets.right = 0;
		gbc.gridx = 1;
		gbc.gridy = 2;
		panes[0].add(fileDrop2, gbc);
		
		//file 1 clear button
		InteractiveIcon clearFile2 = new InteractiveIcon(clearButton);
		clearFile2.setHoverIcon(hoverClearButton);
		clearFile2.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				fileDrop2.setFile(null);
				return null;
			}
		});
		
		gbc.insets.right = -40;
		gbc.gridx = 2;
		gbc.gridy = 2;
		panes[0].add(clearFile2, gbc);
		
		//column settings
		changeColumnSettingsIcon(false);
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
	
	private void processBlend(String filePath, XLSFile file1, XLSFile file2) throws IOException {
		Blender blender = new Blender();
		String directory = DirectortTrimmer.extractDirectory(filePath);
		String fileName = DirectortTrimmer.extractFileName(filePath);
		OutputRequest request = new OutputRequest(directory, fileName, "מק'ט", false);
		
		FileSpecification file1Specification = new FileSpecification(file1);
		String[] file1ColNames = file1.getSheet().getColumnNames();
		for (int i = 0; i < file1ColNames.length; i++)
			file1Specification.addColumn(file1ColNames[i]);
		
		FileSpecification file2Specification = new FileSpecification(file2);
		String[] file2ColNames = file2.getSheet().getColumnNames();
		for (int i = 0; i < file2ColNames.length; i++)
			file2Specification.addColumn(file2ColNames[i]);
		
		request.addFile(file1Specification);
		request.addFile(file2Specification);
		blender.blend(request);
	}

	@Override
	public void applyPanels() {
		window.add(panes[0], BorderLayout.PAGE_START);
	}
	
}