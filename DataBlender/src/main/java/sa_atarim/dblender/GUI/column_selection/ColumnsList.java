package sa_atarim.dblender.GUI.column_selection;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import sa_atarim.dblender.Constants;
import sa_atarim.dblender.GUI.column_selection.ListEntry.EntryIcon;

public class ColumnsList extends JScrollPane
{
	private static class ListEntryCellRenderer extends JLabel implements ListCellRenderer<ListEntry>
	{
		private static final long serialVersionUID = 8216699270312094465L;
		
		public Component getListCellRendererComponent(JList<? extends ListEntry> list, ListEntry value,
			   										 int index, boolean isSelected, boolean cellHasFocus) {
			setText(value.toString());
			setIcon(value.getEntryIcon().getIcon());
			
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}
			else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			
			setEnabled(list.isEnabled());
			setFont(list.getFont());
			setOpaque(true);
			
			return this;
		}
	}
	
	private static final long serialVersionUID = 3103333593176935098L;
	private static final Border BORDER = new EtchedBorder(EtchedBorder.LOWERED);
	
	private DefaultListModel<ListEntry> listModel;
	private JList<ListEntry> list;
	
	public ColumnsList() {
		this.list = new JList<ListEntry>();
		this.listModel = new DefaultListModel<ListEntry>();
		
		list.setModel(listModel);
	    list.setCellRenderer(new ListEntryCellRenderer());
	    list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	    list.setFont(Constants.Fonts.COLUMNS);
	    
	    setBorder(BORDER);
	    setBackground(Constants.BOX_BACKGROUND);
	    setViewportView(list);
	    setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
	}
	
	/**
	 * Sort the list.
	 * Candidate entries go on top, and everything else gets sorted alphabetically.
	 */
	public void sort() {
		ListEntry[] arr = new ListEntry[listModel.getSize()];
		
		for (int i = 0; i < listModel.getSize(); i++)
			arr[i] = listModel.get(i);
			
		Arrays.sort(arr);
		
		//re-add all entries to the list
		removeAllEntries();
		for (int i = 0; i < arr.length; i++) addEntry(arr[i]);
	}
	
	/**
	 * @param entry - The entry to add
	 */
	public void addEntry(ListEntry entry) { listModel.addElement(entry); }
	
	/**
	 * @param entry - The entry to remove
	 */
	public void removeEntry(ListEntry entry) { listModel.removeElement(entry); }
	
	/**
	 * Remove all entries from the list.
	 */
	public void removeAllEntries() { listModel.removeAllElements(); }
	
	/**
	 * Select all of the list's entries.
	 */
	public void selectAll() { list.setSelectionInterval(0, listModel.getSize() - 1); }
	
	/**
	 * @return A list of the selected entries in the list.
	 */
	public List<ListEntry> getSelected() { return list.getSelectedValuesList(); }
	
	/**
	 * Get the amount of selected entries in the list.
	 * This method does the same as getSelected().size() but with a lower time complexity. 
	 * 
	 * @return The amount of selected entries in the list.
	 */
	public int getSelectedEntriesAmount() { return list.getSelectedIndices().length; }
	
	/**
	 * @return The amount of entries in the list.
	 */
	public int getEntriesAmount() { return listModel.getSize(); }
	
	/**
	 * Cancel all selections in the list.
	 */
	public void clearSelection() { list.clearSelection(); }
	
	/**
	 * Check if the list contains an entry with a similar value to a given entry.
	 * 
	 * @param entry - The entry to check
	 * @return True if the list contains an entry with a similar value.
	 */
	public boolean containsEntry(ListEntry entry) { return getEntry(entry.getValue()) != null; }
	
	/**
	 * Check if the list contains an entry with a certain entry icon.
	 * 
	 * @param icons - A vararg array of entry icons to check
	 * @return True if the list contains at least one of the entry icons.
	 */
	public boolean containsEntry(EntryIcon ... icons) {
		for (int i = 0; i < listModel.getSize(); i++) {
			EntryIcon entryIcon = listModel.get(i).getEntryIcon();
			
			for (int j = 0; j < icons.length; j++)
				if (entryIcon == icons[j]) return true;
		}
		
		return false;
	}
	
	/**
	 * Find an entry in the list that has a certain value.
	 * 
	 * @param value - The value of the desired entry
	 * @return An entry from the list that has the given value, or null if it doesn't exist.
	 */
	public ListEntry getEntry(String value) {
		for (int i = 0; i < listModel.getSize(); i++)
			if (listModel.get(i).getValue().equals(value)) return listModel.get(i);
		
		return null;
	}
	
	/**
	 * Get the index of an entry.
	 * 
	 * @param entry - The entry to look for in the list
	 * @return The index of the entry.
	 */
	public int findIndex(ListEntry entry) {
		for (int i = 0; i < listModel.getSize(); i++)
			if (listModel.get(i).equals(entry)) return i;
		
		return -1;
	}
	
	/**
	 * @return True if the list is empty.
	 */
	public boolean isEmpty() { return listModel.getSize() == 0; }
	
	/**
	 * @return A list of all entries.
	 */
	public List<ListEntry> getAll() {
		List<ListEntry> entries = new ArrayList<ListEntry>();
		
		for (int i = 0; i < listModel.getSize(); i++)
			entries.add(listModel.get(i));
		
		return entries;
	}
	
	/**
	 * Shift the selected entries 1 place upwards.
	 */
	public void shiftUpSelected() {
		int[] selectedIndices = list.getSelectedIndices();
		
		for (int i = 0; i < selectedIndices.length; i++) {
			int index = selectedIndices[i];
			int topIndex = index - 1;
			
			if (!list.isSelectedIndex(topIndex) && swap(index, topIndex))
				selectedIndices[i] = topIndex;
		}
		
		list.setSelectedIndices(selectedIndices);
	}
	
	/**
	 * Shift the selected entries 1 place downwards.
	 */
	public void shiftDownSelected() {
		int[] selectedIndices = list.getSelectedIndices();
		
		for (int i = selectedIndices.length - 1; i >= 0; i--) {
			int index = selectedIndices[i];
			int bottomIndex = index + 1;
			
			if (!list.isSelectedIndex(bottomIndex) && swap(index, bottomIndex))
				selectedIndices[i] = bottomIndex;
		}
		
		list.setSelectedIndices(selectedIndices);
	}
	
	/**
	 * @return The indices of the selected entries.
	 */
	public int[] getSelectedIndices() { return list.getSelectedIndices(); }
	
	/**
	 * Mark entries as selected.
	 * 
	 * @param indices - The indices of the entries to select
	 */
	public void setSelectedIndices(int[] indices) { list.setSelectedIndices(indices); }
	
	/**
	 * Switch the places of 2 entries in the list.
	 * 
	 * @param value - The value of the first entry
	 * @param index - The index of the second entry
	 * @return True if the two entries had been successfully swapped.
	 */
	public boolean swap(String value, int index) {
		ListEntry entry = getEntry(value);
		int valIndex = findIndex(entry);
		
		return swap(valIndex, index);
	}
	
	/**
	 * Switch the places of 2 entries in the list.
	 * 
	 * @param indexA - The index of the first entry
	 * @param indexB - The index of the second entry
	 * @return True if the two entries had been successfully swapped.
	 */
	public boolean swap(int indexA, int indexB) {
		int amount = getEntriesAmount();
		
		if (indexA < 0 || indexA > amount - 1) return false;
		else if (indexB < 0 || indexB > amount - 1) return false;
		else if (indexA == indexB) return false;
		
		List<ListEntry> entriesList = getAll();
		Collections.swap(entriesList, indexA, indexB);
		
		//re-add all entries to the list
		removeAllEntries();
		for (int i = 0; i < entriesList.size(); i++) addEntry(entriesList.get(i));
		
		return true;
	}
	
	@Override
	public void addFocusListener(FocusListener listener) { list.addFocusListener(listener); }
	
	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		if (list != null) list.setBackground(color);
	}
}