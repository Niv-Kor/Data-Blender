package sa_atarim.dblender.GUI.states;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javaNK.util.GUI.swing.components.InteractiveIcon;
import javaNK.util.GUI.swing.containers.Window;
import javaNK.util.GUI.swing.state_management.State;
import javaNK.util.math.DimensionalHandler;
import sa_atarim.dblender.Constants;
import sa_atarim.dblender.GUI.column_selection.ColumnsList;
import sa_atarim.dblender.GUI.column_selection.ListEntry;
import sa_atarim.dblender.GUI.column_selection.ListEntry.EntryIcon;
import sa_atarim.dblender.sheets.XLSFile;

public class ColumnSelectionState extends State
{
	public static enum FileIndex {
		FILE_1(EntryIcon.GREEN),
		FILE_2(EntryIcon.BLUE);
		
		private EntryIcon icon;
		
		private FileIndex(EntryIcon icon) {
			this.icon = icon;
		}
	}
	
	private ColumnsList file1List, file2List, combinedList;
	private JComboBox<String> keyColumnDropdown;
	private PropertyChangeSupport propertyChange;
	private JCheckBox intersectBox;
	
	public ColumnSelectionState(Window window) {
		super(window, 3);
		this.propertyChange = new PropertyChangeSupport(this);
		Dimension panelDim = DimensionalHandler.adjust(window.getDimension(), 42.5, 100);
		GridBagConstraints gbc = new GridBagConstraints();
		
		//west pane (files lists)
		Dimension subPanelDim = DimensionalHandler.adjust(panelDim, 100, 50);
		Dimension fileListDim = DimensionalHandler.adjust(subPanelDim, 80, 80);
		createPanel(new BorderLayout(), panelDim, null);
		JPanel northWest = new JPanel(new GridBagLayout());
		northWest.setPreferredSize(subPanelDim);
		northWest.setBackground(Color.GREEN);
		
		this.file1List = new ColumnsList();
		file1List.setPreferredSize(fileListDim);
		gbc.gridx = 0;
		gbc.gridy = 0;
		northWest.add(file1List, gbc);
		
		panes[0].add(northWest, BorderLayout.NORTH);
		
		JPanel southWest = new JPanel(new GridBagLayout());
		southWest.setPreferredSize(subPanelDim);
		southWest.setBackground(Color.BLUE);
		
		this.file2List = new ColumnsList();
		file2List.setPreferredSize(fileListDim);
		gbc.gridx = 0;
		gbc.gridy = 0;
		southWest.add(file2List, gbc);
		
		panes[0].add(southWest, BorderLayout.SOUTH);
		
		//east pane (combined list)
		Dimension combinedListDim = DimensionalHandler.adjust(panelDim, 80, 70);
		Dimension comboBoxDim = DimensionalHandler.adjust(panelDim, 80, 0);
		createPanel(new GridBagLayout(), panelDim, Color.ORANGE);
		
			//combined list
			this.combinedList = new ColumnsList();
			combinedList.setPreferredSize(combinedListDim);
			
			gbc.insets.top = 15;
			gbc.insets.bottom = 20;
			gbc.gridx = 0;
			gbc.gridy = 0;
			panes[1].add(combinedList, gbc);
			
			//key column label
			JLabel keyColumnLabel = new JLabel("- Key Column -");
			
			gbc.insets.top = 0;
			gbc.insets.bottom = 0;
			gbc.gridx = 0;
			gbc.gridy = 1;
			panes[1].add(keyColumnLabel, gbc);
			
			//key column dropdown
			this.keyColumnDropdown = new JComboBox<String>();
			keyColumnDropdown.setPreferredSize(new Dimension(comboBoxDim.width, 20));
			keyColumnDropdown.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					String item = (String) e.getItem();
					propertyChange.firePropertyChange("key column set", null, item);
					
					
				}
			});
			
			gbc.insets.bottom = 10;
			gbc.gridx = 0;
			gbc.gridy = 2;
			panes[1].add(keyColumnDropdown, gbc);
			
			//intersection option
			this.intersectBox = new JCheckBox("<html>Show only<br>intersected rows</html>");
			intersectBox.setOpaque(false);
			intersectBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					boolean flag = intersectBox.isSelected();
					propertyChange.firePropertyChange("intersection set", !flag, flag);
				}
			});
			
			gbc.gridx = 0;
			gbc.gridy = 3;
			panes[1].add(intersectBox, gbc);
		
		//center pane (buttons)
		Dimension buttonsPaneDim = DimensionalHandler.adjust(panelDim, 15, 100);
		createPanel(new GridBagLayout(), buttonsPaneDim, Color.PINK);
		
		//shift forward button
		InteractiveIcon shiftFwdButton = new InteractiveIcon(Constants.Icons.SHIFT_FWD);
		shiftFwdButton.setHoverIcon(Constants.Icons.HOVER_SHIFT_FWD);
		shiftFwdButton.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				shiftForward();
				return null;
			}
		});
		
		gbc.insets.bottom = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panes[2].add(shiftFwdButton, gbc);
		
		//shift back button
		InteractiveIcon shiftBckButton = new InteractiveIcon(Constants.Icons.SHIFT_BCK);
		shiftBckButton.setHoverIcon(Constants.Icons.HOVER_SHIFT_BCK);
		shiftBckButton.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				shiftBack();
				return null;
			}
		});
		
		gbc.insets.top = 20;
		gbc.gridx = 0;
		gbc.gridy = 1;
		panes[2].add(shiftBckButton, gbc);
	}
	
	public void addFile(XLSFile file, FileIndex fileIndex) {
		reset(fileIndex);
		
		String[] columnNames = file.getSheet().getColumnNames();
		ColumnsList list;
		
		switch (fileIndex) {
			case FILE_1: list = file1List; break;
			case FILE_2: list = file2List; break;
			default: return;
		}
		
		for (String col : columnNames)
			list.addEntry(new ListEntry(col, fileIndex.icon));
		
		//find the key column candidates and highlight them
		suggestCandidates();
		refresh();
	}
	
	private void shiftForward() {
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
			if (!entry.isGrayed() && !combinedList.hasEntry(entry)) {
				String propertyName;
				
				switch (entry.getEntryIcon()) {
					case GREEN:
					case GREEN_CANDIDATE:
						file1List.removeEntry(entry);
						propertyName = "file 1 add";
						break;
					case BLUE:
					case BLUE_CANDIDATE:
						file2List.removeEntry(entry);
						propertyName = "file 2 add";
						break;
					default: continue iteration;
				}
				
				//add to the list and fire property change
				propertyChange.firePropertyChange(propertyName, "", entry.getValue());
				combinedList.addEntry(entry);
				
				//add candidate to the key columns dropdown
				if (entry.isCandidate()) keyColumnDropdown.addItem(entry.getValue());
			}
		}
		
		manageGrayedOut();
		refresh();
	}
	
	private void shiftBack() {
		List<ListEntry> combinedSelected = combinedList.getSelected();
		
		interation:
		for (ListEntry entry : combinedSelected) {
			String propertyName;
			
			switch (entry.getEntryIcon()) {
				case GREEN:
				case GREEN_CANDIDATE:
					file1List.addEntry(entry);
					propertyName = "file 1 remove";
					break;
				case BLUE:
				case BLUE_CANDIDATE:
					file2List.addEntry(entry);
					propertyName = "file 2 remove";
					break;
				default: continue interation;
			}
			
			//remove the candidate from both the list and the key columns dropdown
			if (entry.isCandidate()) {
				keyColumnDropdown.removeItem(entry.getValue());
			}
			
			//remove from the list and fire property change
			propertyChange.firePropertyChange(propertyName, "", entry.getValue());
			combinedList.removeEntry(entry);
		}
		
		manageGrayedOut();
		refresh();
	}
	
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
	
	private void suggestCandidates() {
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
	
	private void manageGrayedOut() {
		for (ListEntry entry : file1List.getAll()) {
			EntryIcon entryIcon = entry.getEntryIcon();
			
			//gray out
			if (combinedList.hasEntry(entry)) {
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
			if (combinedList.hasEntry(entry)) {
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
	
	public void reset(FileIndex fileIndex) {
		//clear the combined list
		combinedList.selectAll();
		shiftBack();
		intersectBox.setSelected(false);
		keyColumnDropdown.removeAllItems();
		
		//clear the specified file list
		switch (fileIndex) {
			case FILE_1: file1List.removeAllEntries(); break;
			case FILE_2: file2List.removeAllEntries(); break;
		}
		
		suggestCandidates();
		manageGrayedOut();
		refresh();
	}
	
	private void refresh() {
		file1List.revalidate();
		file1List.repaint();
		file2List.revalidate();
		file2List.repaint();
		combinedList.revalidate();
		combinedList.repaint();
	}
	
	public void subscribePropertyChange(PropertyChangeListener listener) {
		propertyChange.addPropertyChangeListener(listener);
	}

	@Override
	public void applyPanels() {
		window.insertPanel(panes[0], BorderLayout.WEST);
		window.insertPanel(panes[1], BorderLayout.EAST);
		window.insertPanel(panes[2], BorderLayout.CENTER);
	}
}