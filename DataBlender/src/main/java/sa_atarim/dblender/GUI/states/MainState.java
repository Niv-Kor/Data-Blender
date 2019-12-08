package sa_atarim.dblender.GUI.states;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javaNK.util.GUI.swing.components.InteractiveIcon;
import javaNK.util.GUI.swing.containers.Window;
import javaNK.util.GUI.swing.state_management.State;
import javaNK.util.IO.DirectortTrimmer;
import javaNK.util.math.DimensionalHandler;
import sa_atarim.dblender.Constants;
import sa_atarim.dblender.GUI.Circuits;
import sa_atarim.dblender.GUI.DropArea;
import sa_atarim.dblender.GUI.column_selection.ColumnsList;
import sa_atarim.dblender.GUI.column_selection.ListEntry;
import sa_atarim.dblender.GUI.column_selection.ListEntry.EntryIcon;
import sa_atarim.dblender.error.PopupError;
import sa_atarim.dblender.output.Blender;
import sa_atarim.dblender.output.FileProcessor;
import sa_atarim.dblender.output.FileSpecification;
import sa_atarim.dblender.output.OutputRequest;
import sa_atarim.dblender.sheets.XLSFile;

public class MainState extends State implements PropertyChangeListener
{
	public static enum FileIndex
	{	
		FILE_1(EntryIcon.GREEN),
		FILE_2(EntryIcon.BLUE);
		
		EntryIcon icon;
		
		private FileIndex(EntryIcon icon) {
			this.icon = icon;
		}
	}
	
	private static final int DROP_AREA_HEIGHT = 40;
	
	private GridBagConstraints gbc;
	private DropArea fileDrop1, fileDrop2;
	private XLSFile droppedFile1, droppedFile2;
	private FileSpecification file1Specification, file2Specification;
	private ColumnsList file1List, file2List, combinedList;
	private InteractiveIcon currentArrow, shiftFwdButton, shiftBckButton;
	private JComboBox<String> keyColumnDropdown;
	private OutputRequest outputRequest;
	private JCheckBox intersectBox;
	private Circuits circuits;
	private Blender blender;
	
	public MainState(Window window) {
		super(window, 2);
		
		this.gbc = new GridBagConstraints();
		this.outputRequest = new OutputRequest();
		this.blender = new Blender();
		
		createPanel(new GridBagLayout(), window.getDimension(), null);
		createPanel(new GridBagLayout(), DimensionalHandler.adjust(getWindow().getDimension(), 100, 30), null);
		
		createFileAreas();
		createSelectionLists();
		
		//the visual circuits drawn onto the panel 
		this.circuits = new Circuits(getWindow(), fileDrop1, fileDrop2, combinedList);
	}
	
	/**
	 * Create the components that are responsible for dropping the files onto the window and managing them. 
	 */
	private void createFileAreas() {
		//company logo
		JLabel companyLogo = new JLabel(Constants.Icons.COMPANY_LOGO);
		
		gbc.insets.right = -130;
		gbc.insets.top = -140;
		gbc.gridx = 2;
		gbc.gridy = 0;
		panes[0].add(companyLogo, gbc);
		
		//blender icon
		InteractiveIcon blenderIcon = new InteractiveIcon(Constants.Icons.BLENDER);
		blenderIcon.setHoverIcon(Constants.Icons.HOVER_BLENDER);
		blenderIcon.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				processBlend();
				return null;
			}
		});
		
		gbc.insets.right = 0;
		gbc.insets.top = 0;
		gbc.gridx = 1;
		gbc.gridy = 0;
		panes[0].add(blenderIcon, gbc);
		
		//drop areas
		Dimension dropAreaDim = DimensionalHandler.adjust(window.getDimension(), 35, 0);
		dropAreaDim.height = (int) DROP_AREA_HEIGHT;
		
		//file 1 drop area
		String defaultDropText = "Drop a file here";
		this.fileDrop1 = new DropArea(defaultDropText, "file 1 set");
		fileDrop1.setPreferredSize(dropAreaDim);
		fileDrop1.subscribePropertyChange(this);
		
		gbc.insets.top = 10;
		gbc.gridx = 0;
		gbc.gridy = 1;
		panes[0].add(fileDrop1, gbc);
		
		//file 1 clear button
		InteractiveIcon clearFile1 = new InteractiveIcon(Constants.Icons.CLEAR_X);
		clearFile1.setHoverIcon(Constants.Icons.HOVER_CLEAR_X);
		clearFile1.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				clearFile(FileIndex.FILE_1);
				return null;
			}
		});
		
		gbc.insets.left = -155;
		gbc.insets.bottom = -100;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panes[0].add(clearFile1, gbc);
		
		//file 2 drop area
		this.fileDrop2 = new DropArea(defaultDropText, "file 2 set");
		fileDrop2.setPreferredSize(dropAreaDim);
		fileDrop2.subscribePropertyChange(this);
		
		gbc.insets.left = 0;
		gbc.insets.right = 0;
		gbc.insets.bottom = 0;
		gbc.gridx = 2;
		gbc.gridy = 1;
		panes[0].add(fileDrop2, gbc);
		
		//file 1 clear button
		InteractiveIcon clearFile2 = new InteractiveIcon(Constants.Icons.CLEAR_X);
		clearFile2.setHoverIcon(Constants.Icons.HOVER_CLEAR_X);
		clearFile2.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				clearFile(FileIndex.FILE_2);
				return null;
			}
		});
		
		gbc.insets.right = -155;
		gbc.insets.bottom = -100;
		gbc.gridx = 2;
		gbc.gridy = 0;
		panes[0].add(clearFile2, gbc);
	}
	
	/**
	 * Create the components that are responsible for selecting the columns for each of the files.
	 */
	private void createSelectionLists() {
		//files lists
		Dimension fileListDim = DimensionalHandler.adjust(getWindow().getDimension(), 30, 30);
		
		this.file1List = new ColumnsList();
		file1List.setPreferredSize(fileListDim);
		
		gbc.insets.right = 0;
		gbc.insets.bottom = 0;
		gbc.insets.top = 50;
		gbc.gridx = 0;
		gbc.gridy = 2;
		panes[0].add(file1List, gbc);
		
		this.file2List = new ColumnsList();
		file2List.setPreferredSize(fileListDim);
		
		gbc.gridx = 2;
		gbc.gridy = 2;
		panes[0].add(file2List, gbc);
		
		//shift button
		this.shiftFwdButton = new InteractiveIcon(Constants.Icons.SHIFT_FWD);
		shiftFwdButton.setHoverIcon(Constants.Icons.HOVER_SHIFT_FWD);
		shiftFwdButton.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				shiftEntriesForward();
				return null;
			}
		});
		
		this.shiftBckButton = new InteractiveIcon(Constants.Icons.SHIFT_BCK);
		shiftBckButton.setHoverIcon(Constants.Icons.HOVER_SHIFT_BCK);
		shiftBckButton.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				shiftEntriesBack();
				return null;
			}
		});
		
		changeShiftArrow(shiftFwdButton);
		
		//combined list
		Dimension combinedListDim = DimensionalHandler.adjust(panes[1].getPreferredSize(), 82.8, 60);
		
		this.combinedList = new ColumnsList();
		combinedList.setPreferredSize(combinedListDim);
		combinedList.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				combinedList.clearSelection();
				changeShiftArrow(shiftFwdButton);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				file1List.clearSelection();
				file2List.clearSelection();
				changeShiftArrow(shiftBckButton);
			}
		});
		
		gbc.insets.top = -40;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panes[1].add(combinedList, gbc);
		
		//intersection option
		this.intersectBox = new JCheckBox("Keep only intersected rows "
										+ "(remove rows that don't contain a shared key)");
		
		intersectBox.setIcon(Constants.Icons.UNCHECKED_BOX);
		intersectBox.setSelectedIcon(Constants.Icons.CHECKED_BOX);
		intersectBox.setFont(Constants.Fonts.MAIN.deriveFont(14f));
		intersectBox.setOpaque(false);
		intersectBox.setBorder(null);
		intersectBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean flag = intersectBox.isSelected();
				outputRequest.setIntersection(flag);
			}
		});
		
		intersectBox.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {}
			
			@Override
			public void focusGained(FocusEvent e) {
				window.requestFocus();
			}
		});
		
		gbc.insets.top = 50;
		gbc.insets.left = -12;
		gbc.gridx = 0;
		gbc.gridy = 1;
		panes[1].add(intersectBox, gbc);
		
		//key column label
		JLabel keyColumnLabel = new JLabel("Key column:");
		keyColumnLabel.setFont(Constants.Fonts.MAIN.deriveFont(14f));
		
		gbc.insets.top = 10;
		gbc.insets.left = -429;
		gbc.gridx = 0;
		gbc.gridy = 2;
		panes[1].add(keyColumnLabel, gbc);
		
		//key column dropdown
		Dimension comboBoxDim = DimensionalHandler.adjust(panes[1].getPreferredSize(), 30, 0);
		comboBoxDim.height = 20;
		
		this.keyColumnDropdown = new JComboBox<String>();
		keyColumnDropdown.setPreferredSize(comboBoxDim);
		keyColumnDropdown.setFont(Constants.Fonts.GENERIC);
		keyColumnDropdown.setBackground(Constants.BOX_BACKGROUND);
		keyColumnDropdown.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				String item = (String) keyColumnDropdown.getSelectedItem();
				outputRequest.setKeyColumn(item);
			}
		});
		
		gbc.insets.left = -170;
		gbc.gridx = 0;
		gbc.gridy = 2;
		panes[1].add(keyColumnDropdown, gbc);
	}
	
	/**
	 * Change the shift arrow (shift forward or shift back).
	 * 
	 * @param arrow - The icon to set
	 */
	private void changeShiftArrow(InteractiveIcon arrow) {
		if (arrow == currentArrow) return;
		
		//remove current arrow
		if (currentArrow != null) panes[0].remove(currentArrow);
		
		//save grid bag properties
		Insets backupInsets = gbc.insets;
		int backupGridX = gbc.gridx;
		int backupGridY = gbc.gridy;
		
		//add arrow
		gbc.insets.right = 0;
		gbc.insets.bottom = 0;
		gbc.insets.left = 0;
		gbc.insets.top = 80;
		gbc.gridx = 1;
		gbc.gridy = 2;
		panes[0].add(arrow, gbc);
		currentArrow = arrow;
		
		//refresh window
		Component window = getWindow();
		window.revalidate();
		window.repaint();
		
		//restore grid bag properties
		gbc.insets = backupInsets;
		gbc.gridx = backupGridX;
		gbc.gridy = backupGridY;
	}
	
	/**
	 * Process the new file according to the user inputs.
	 * This method checks if all inputs are valid, and throws a pop-up error in case they're not.
	 */
	private void processBlend() {
		try {
			//cannot blend
			if (!fileDrop1.isOccupied() || !fileDrop2.isOccupied()) {
				PopupError.NO_FILES.pop();
				return;
			}
			else if (outputRequest.getKeyColumn() == null) {
				PopupError.NO_KEY.pop();
				return;
			}
			
			else {
				String filePath = FileProcessor.saveFileToDirectory();
				String directory = DirectortTrimmer.extractDirectory(filePath);
				String fileName = DirectortTrimmer.extractFileName(filePath);
				
				if (!directory.equals("") && !fileName.equals(""))
					outputRequest.setFilePath(filePath);
				
				//blend files
				if (outputRequest.isValid()) {
					blender.blend(outputRequest);
				}
			}
		}
		catch (IOException e) { e.printStackTrace(); }
	}
	
	/**
	 * Clear a file that had been dropped onto the window and remove
	 * all of its columns from the corresponding list.
	 * 
	 * @param fileIndex - The index of the file to clear
	 */
	private void clearFile(FileIndex fileIndex) {
		switch (fileIndex) {
			case FILE_1:
				fileDrop1.setFile(null);
				outputRequest.removeFile(file1Specification);
				resetList(FileIndex.FILE_1);
				break;
			case FILE_2:
				fileDrop2.setFile(null);
				outputRequest.removeFile(file2Specification);
				resetList(FileIndex.FILE_2);
				break;
		}
		
		circuits.wake();
	}
	
	/**
	 * Set a file that had been dropped onto the window and add
	 * all of its columns to the corresponding list.
	 * 
	 * @param fileIndex - The index of the file to add
	 */
	private void setFile(FileIndex fileIndex, String filePath) throws IOException {
		if (filePath == null) return;
		
		ColumnsList list = null;
		String[] columnNames;
		resetList(fileIndex);
		
		switch (fileIndex) {
			case FILE_1:
				outputRequest.removeFile(file1Specification);
				droppedFile1 = new XLSFile(filePath);
				file1Specification = new FileSpecification(droppedFile1);
				outputRequest.addFile(file1Specification);
				columnNames = droppedFile1.getSheet().getColumnNames();
				list = file1List;
				break;
			case FILE_2:
				outputRequest.removeFile(file2Specification);
				droppedFile2 = new XLSFile(filePath);
				file2Specification = new FileSpecification(droppedFile2);
				outputRequest.addFile(file2Specification);
				columnNames = droppedFile2.getSheet().getColumnNames();
				list = file2List;
				break;
			default: return;
		}
		
		for (String col : columnNames)
			list.addEntry(new ListEntry(col, fileIndex.icon));
		
		//find the key column candidates and highlight them
		suggestCandidatesEntries();
		refreshLists();
		circuits.wake();
	}
	
	/**
	 * Shift all selected columns from the files' lists into the combined list.
	 */
	private void shiftEntriesForward() {
		List<ListEntry> list1Selected = file1List.getSelected();
		List<ListEntry> list2Selected = file2List.getSelected();
		
		//intersect both sets
		List<ListEntry> selectedIntersection = intersectLists(list1Selected, list2Selected);
		List<ListEntry> selectedUnion = new ArrayList<ListEntry>(list1Selected);
		selectedUnion.addAll(list2Selected);
		
		//get both selections without the intersected ones
		for (int i = 0; i < selectedUnion.size(); i++) {
			for (ListEntry intersectedEntry : selectedIntersection) {
				if (selectedUnion.get(i).getValue().equals(intersectedEntry.getValue())) {
					selectedUnion.remove(selectedUnion.get(i));
					break;
				}
			}
		}
		
		//shift all the valid ones
		iteration:
		for (ListEntry entry : selectedUnion) {
			if (!entry.isGrayed() && !combinedList.containsEntry(entry)) {
				FileSpecification fileSpecification;
				
				switch (entry.getEntryIcon()) {
					case GREEN:
					case GREEN_CANDIDATE:
						file1List.removeEntry(entry);
						fileSpecification = file1Specification;
						break;
					case BLUE:
					case BLUE_CANDIDATE:
						file2List.removeEntry(entry);
						fileSpecification = file2Specification;
						break;
					default: continue iteration;
				}
				
				//add to the list and to the specification
				fileSpecification.addColumn(entry.getValue());
				combinedList.addEntry(entry);
				
				//add candidate to the key columns dropdown
				if (entry.isCandidate()) keyColumnDropdown.addItem(entry.getValue());
			}
		}
		
		manageGrayedOutEntries();
		refreshLists();
		circuits.wake();
	}
	
	/**
	 * Shift all selected columns from the combined list into the files' lists.
	 * Each column returns to its parent list.
	 */
	private void shiftEntriesBack() {
		List<ListEntry> combinedSelected = combinedList.getSelected();
		
		interation:
		for (ListEntry entry : combinedSelected) {
			FileSpecification fileSpecification;
			
			switch (entry.getEntryIcon()) {
				case GREEN:
				case GREEN_CANDIDATE:
					file1List.addEntry(entry);
					fileSpecification = file1Specification;
					break;
				case BLUE:
				case BLUE_CANDIDATE:
					file2List.addEntry(entry);
					fileSpecification = file2Specification;
					break;
				default: continue interation;
			}
			
			//remove the candidate from both the list and the key columns dropdown
			if (entry.isCandidate()) {
				keyColumnDropdown.removeItem(entry.getValue());
			}
			
			//remove from the list
			fileSpecification.removeColumn(entry.getValue());
			combinedList.removeEntry(entry);
		}
		
		manageGrayedOutEntries();
		refreshLists();
		circuits.wake();
	}
	
	/**
	 * Create a list that only contains of the intersected values from two given lists.
	 * 
	 * @param list1 - The first list to intersect
	 * @param list2 - The second list to intersect
	 * @return A list that only contains values that appear in both lists.
	 */
	private List<ListEntry> intersectLists(List<ListEntry> list1, List<ListEntry> list2) {
		List<ListEntry> intersection = new ArrayList<ListEntry>();
		
		for (ListEntry entry1 : list1) {
			for (ListEntry entry2 : list2) {
				if (entry1.getValue().equals(entry2.getValue())) {
					intersection.add(entry1);
					intersection.add(entry2);
				}
			}
		}
		
		return intersection;
	}
	
	/**
	 * For each list, find the entries that appear in both of them and highlight them with a special icon.
	 * If an entry is highlighted, but only appears in one list, remove the special icon. 
	 */
	private void suggestCandidatesEntries() {
		List<ListEntry> intersection = intersectLists(file1List.getAll(), file2List.getAll());
		List<ListEntry> union = new ArrayList<ListEntry>(file1List.getAll());
		union.addAll(file2List.getAll());
		union.removeAll(intersection);
		
		//suggest candidates
		for (ListEntry entry : intersection) {
			switch (entry.getEntryIcon()) {
				case GREEN: entry.setEntryIcon(EntryIcon.GREEN_CANDIDATE); break;
				case BLUE: entry.setEntryIcon(EntryIcon.BLUE_CANDIDATE); break;
				case GRAY: entry.setEntryIcon(EntryIcon.GRAY_CANDIDATE); break;
				default: break;
			}
		}
		
		//cancel candidates
		for (ListEntry entry : union) {
			switch (entry.getEntryIcon()) {
				case GREEN_CANDIDATE: entry.setEntryIcon(EntryIcon.GREEN); break;
				case BLUE_CANDIDATE: entry.setEntryIcon(EntryIcon.BLUE); break;
				case GRAY_CANDIDATE: entry.setEntryIcon(EntryIcon.GRAY); break;
				default: break;
			}
		}
	}
	
	/**
	 * For each list, find the entries that already appear in the combined list,
	 * and mark them with a gray icon. Switch back if they don't appear there anymore.
	 */
	private void manageGrayedOutEntries() {
		for (ListEntry entry : file1List.getAll()) {
			EntryIcon entryIcon = entry.getEntryIcon();
			
			//gray out
			if (combinedList.containsEntry(entry)) {
				switch (entryIcon) {
					case GREEN: entry.setEntryIcon(EntryIcon.GRAY); break;
					case GREEN_CANDIDATE: entry.setEntryIcon(EntryIcon.GRAY_CANDIDATE); break;
					default: break;
				}
			}
			//colorize
			else {
				switch (entryIcon) {
					case GRAY: entry.setEntryIcon(EntryIcon.GREEN); break;
					case GRAY_CANDIDATE: entry.setEntryIcon(EntryIcon.GREEN_CANDIDATE); break;
					default: break;
				}
			}
		}
		
		for (ListEntry entry : file2List.getAll()) {
			EntryIcon entryIcon = entry.getEntryIcon();
			
			//gray out
			if (combinedList.containsEntry(entry)) {
				switch (entryIcon) {
					case BLUE: entry.setEntryIcon(EntryIcon.GRAY); break;
					case BLUE_CANDIDATE: entry.setEntryIcon(EntryIcon.GRAY_CANDIDATE); break;
					default: break;
				}
			}
			//colorize
			else {
				switch (entryIcon) {
					case GRAY: entry.setEntryIcon(EntryIcon.BLUE); break;
					case GRAY_CANDIDATE: entry.setEntryIcon(EntryIcon.BLUE_CANDIDATE); break;
					default: break;
				}
			}
		}
	}
	
	/**
	 * Remove all entries from a list.
	 * The combined list also shifts all of its entries back.
	 * 
	 * @param fileIndex - The list to reset
	 */
	private void resetList(FileIndex fileIndex) {
		//clear the combined list
		combinedList.selectAll();
		shiftEntriesBack();
		intersectBox.setSelected(false);
		keyColumnDropdown.removeAllItems();
		
		//clear the specified file list
		switch (fileIndex) {
			case FILE_1: file1List.removeAllEntries(); break;
			case FILE_2: file2List.removeAllEntries(); break;
		}
		
		suggestCandidatesEntries();
		manageGrayedOutEntries();
		refreshLists();
	}
	
	/**
	 * Refresh the lists' view.
	 */
	private void refreshLists() {
		file1List.sort();
		file1List.revalidate();
		file1List.repaint();
		file1List.clearSelection();
		file2List.sort();
		file2List.revalidate();
		file2List.repaint();
		file2List.clearSelection();
		combinedList.revalidate();
		combinedList.repaint();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		try {
			switch (evt.getPropertyName()) {
				case "file 1 set": setFile(FileIndex.FILE_1, (String) evt.getNewValue()); break;
				case "file 2 set": setFile(FileIndex.FILE_2, (String) evt.getNewValue()); break;
				default: return;
			}
		}
		catch (IOException e) { System.err.println("Could not open " + Constants.SAVED_FILE_TYPE + " files."); }
	}
	
	@Override
	public void applyPanels() {
		window.insertPanel(panes[0], BorderLayout.CENTER);
		window.insertPanel(panes[1], BorderLayout.SOUTH);
	}
	
	@Override
	public void paintComponent(Graphics g, Dimension windowDim) {
		circuits.paintCircuit(g);
	}
}