package sa_atarim.dblender.GUI.column_selection;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Arrays;
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
			setIcon(value.getIcon());
			
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
	
	public void sort() {
		ListEntry[] arr = new ListEntry[listModel.getSize()];
		
		for (int i = 0; i < listModel.getSize(); i++)
			arr[i] = listModel.get(i);
			
		Arrays.sort(arr);
		removeAllEntries();
		
		for (int i = 0; i < arr.length; i++)
			addEntry(arr[i]);
	}
	
	
	public void addEntry(ListEntry entry) {
		listModel.addElement(entry);
	}
	
	public void removeEntry(ListEntry entry) {
		listModel.removeElement(entry);
	}
	
	public void removeAllEntries() {
		listModel.removeAllElements();
	}
	
	public void selectAll() {
		list.setSelectionInterval(0, listModel.getSize() - 1);
	}
	
	public void clearSelection() {
		list.clearSelection();
	}
	
	public boolean containsEntry(ListEntry entry) {
		return getEntry(entry.getValue()) != null;
	}
	
	public boolean containsEntry(EntryIcon ... icons) {
		for (int i = 0; i < listModel.getSize(); i++) {
			EntryIcon entryIcon = listModel.get(i).getEntryIcon();
			
			for (int j = 0; j < icons.length; j++)
				if (entryIcon == icons[j]) return true;
		}
		
		return false;
	}
	
	public ListEntry getEntry(String value) {
		for (int i = 0; i < listModel.getSize(); i++)
			if (listModel.get(i).getValue().equals(value)) return listModel.get(i);
		
		return null;
	}
	
	public boolean isEmpty() {
		return listModel.getSize() == 0;
	}
	
	public List<ListEntry> getSelected() {
		return list.getSelectedValuesList();
	}
	
	public int getSelectedEntriesAmount() {
		return list.getSelectedIndices().length;
	}
	
	public List<ListEntry> getAll() {
		List<ListEntry> entries = new ArrayList<ListEntry>();
		
		for (int i = 0; i < listModel.getSize(); i++)
			entries.add(listModel.get(i));
		
		return entries;
	}
	
	@Override
	public void addFocusListener(FocusListener listener) {
		list.addFocusListener(listener);
	}
	
	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		if (list != null) list.setBackground(color);
	}
}