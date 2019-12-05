package sa_atarim.dblender.GUI.column_selection;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

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
	
	private DefaultListModel<ListEntry> listModel;
	private JList<ListEntry> list;
	
	public ColumnsList() {
		this.list = new JList<ListEntry>();
		this.listModel = new DefaultListModel<ListEntry>();
		
		list.setModel(listModel);
	    list.setCellRenderer(new ListEntryCellRenderer());
	    list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	    
	    setViewportView(list);
	    setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
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
	
	public boolean hasEntry(ListEntry entry) {
		return listModel.contains(entry);
	}
	
	public ListEntry getEntry(String value) {
		for (int i = 0; i < listModel.getSize(); i++)
			if (listModel.get(i).getValue().equals(value)) return listModel.get(i);
		
		return null;
	}
	
	public List<ListEntry> getSelected() {
		return list.getSelectedValuesList();
	}
	
	public List<ListEntry> getAll() {
		List<ListEntry> entries = new ArrayList<ListEntry>();
		
		for (int i = 0; i < listModel.getSize(); i++)
			entries.add(listModel.get(i));
		
		return entries;
	}
}