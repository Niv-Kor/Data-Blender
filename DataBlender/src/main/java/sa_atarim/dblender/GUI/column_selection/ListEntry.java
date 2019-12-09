package sa_atarim.dblender.GUI.column_selection;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import sa_atarim.dblender.Constants;

public class ListEntry implements Comparable<ListEntry>
{
	public static enum EntryIcon
	{
		GREEN(Constants.Icons.GREEN_SQUARE),
		BLUE(Constants.Icons.BLUE_SQUARE),
		GRAY(Constants.Icons.GRAY_SQUARE),
		GREEN_CANDIDATE(Constants.Icons.GREEN_CANDIDATE_SQUARE),
		BLUE_CANDIDATE(Constants.Icons.BLUE_CANDIDATE_SQUARE),
		GRAY_CANDIDATE(Constants.Icons.GRAY_CANDIDATE_SQUARE);
		
		private ImageIcon icon;
		
		/**
		 * @param icon - The actual icon to show
		 */
		private EntryIcon(Icon icon) {
			this.icon = (ImageIcon) icon;
		}
		
		/**
		 * @return The actual icon that the icon type uses.
		 */
		public ImageIcon getIcon() { return icon; }
	}
	
	private String value;
	private EntryIcon entryIcon;
	
	/**
	 * @param icon - The entry icon that represents the entry's value
	 * @param value - The String value of the entry
	 */
	public ListEntry(EntryIcon icon, String value) {
		this.entryIcon = icon;
		this.value = value;
	}
	
	/**
	 * @return True if the entry has an icon that resembles a candidate.
	 */
	public boolean isCandidate() {
		return entryIcon == EntryIcon.GREEN_CANDIDATE ||
			   entryIcon == EntryIcon.BLUE_CANDIDATE ||
			   entryIcon == EntryIcon.GRAY_CANDIDATE;
	}
	
	/**
	 * @return True if the entry has a gray icon.
	 */
	public boolean isGrayed() {
		return entryIcon == EntryIcon.GRAY ||
			   entryIcon == EntryIcon.GRAY_CANDIDATE;
	}
	
	/**
	 * @return The value of the entry.
	 */
	public String getValue() { return value; }
	
	/**
	 * @param val - The new value of the entry
	 */
	public void setValue(String val) { value = val; }
	
	/**
	 * @return The icon type of the entry.
	 */
	public EntryIcon getEntryIcon() { return entryIcon; }
	
	/**
	 * @param icon - The new icon type to set
	 */
	public void setEntryIcon(EntryIcon icon) { entryIcon = icon; } 
	
	@Override
	public String toString() { return getValue(); }

	@Override
	public int compareTo(ListEntry o) {
		boolean entry1Cand = isCandidate();
		boolean entry2Cand = o.isCandidate();
		
		if (entry1Cand != entry2Cand) return entry1Cand ? -1 : 1;
		else return value.compareTo(o.value);
	}
}