package sa_atarim.dblender.GUI;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.util.concurrent.Callable;
import javaNK.util.GUI.swing.components.InteractiveLabel;
import javaNK.util.GUI.swing.containers.Window;
import javaNK.util.GUI.swing.state_management.State;
import javaNK.util.math.DimensionalHandler;
import sa_atarim.dblender.output.FileSpecifications;
import sa_atarim.dblender.output.OutputRequest;
import sa_atarim.dblender.sheets.Blender;
import sa_atarim.dblender.sheets.XLSFile;

public class InputState extends State
{
	private static final int DROP_AREA_HEIGHT = 40;
	
	private GridBagConstraints gbc;
	private Dimension dropAreaDim;
	private DropArea fileDrop1, fileDrop2;
	
	public InputState(Window window) {
		super(window, 1);
		
		Dimension windowDim = window.getDimension();
		this.gbc = new GridBagConstraints();
		this.dropAreaDim = DimensionalHandler.adjust(windowDim, 70, 100);
		dropAreaDim.height = (int) DROP_AREA_HEIGHT;
		createPanel(new GridBagLayout(), windowDim, null);
		
		//file 1 drop area
		this.fileDrop1 = new DropArea();
		fileDrop1.setPreferredSize(dropAreaDim);
		gbc.gridx = 0;
		gbc.gridy = 0;
		panes[0].add(fileDrop1, gbc);
		
		//file 2 drop area
		this.fileDrop2 = new DropArea();
		fileDrop2.setPreferredSize(dropAreaDim);
		gbc.insets.top = 10;
		gbc.gridx = 0;
		gbc.gridy = 1;
		panes[0].add(fileDrop2, gbc);
		
		InteractiveLabel blendButton = new InteractiveLabel("Blend");
		blendButton.enableSelectionColor(false);
		blendButton.setFunction(new Callable<Void>() {
			public Void call() throws Exception {
				try { processBlend(); }
				catch (IOException e) { e.printStackTrace(); }
				return null;
			}
		});
		
		gbc.insets.top = 20;
		gbc.gridx = 0;
		gbc.gridy = 2;
		panes[0].add(blendButton, gbc);
	}
	
	private void processBlend() throws IOException {
		XLSFile droppedFile1 = fileDrop1.hasFile() ? new XLSFile(fileDrop1.getPath(), false) : null;
		XLSFile droppedFile2 = fileDrop2.hasFile() ? new XLSFile(fileDrop2.getPath(), false) : null;
		Blender blender = new Blender();
		
		OutputRequest request = new OutputRequest("C:\\Users\\Korach\\Desktop\\Data Blender", "some file 1", "מק'ט", false);
		
		FileSpecifications file1Specifications = new FileSpecifications(droppedFile1);
		String[] file1ColNames = droppedFile1.getSheet().getColumnNames();
		for (int i = 0; i < file1ColNames.length; i++)
			file1Specifications.addColumn(file1ColNames[i]);
		
		FileSpecifications file2Specifications = new FileSpecifications(droppedFile2);
		String[] file2ColNames = droppedFile2.getSheet().getColumnNames();
		for (int i = 0; i < file2ColNames.length; i++)
			file2Specifications.addColumn(file2ColNames[i]);
		
		request.addFile(file1Specifications);
		request.addFile(file2Specifications);
		blender.blend(request);
	}

	@Override
	public void applyPanels() {
		window.add(panes[0], BorderLayout.PAGE_START);
	}
}