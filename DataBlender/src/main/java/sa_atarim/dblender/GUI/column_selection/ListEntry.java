package sa_atarim.dblender.GUI.column_selection;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import sa_atarim.dblender.Constants;

public class ListEntry implements java.lang.Comparable<ListEntry>
{
	public static enum EntryIcon {
		GREEN(Constants.Icons.GREEN_SQUARE),
		BLUE(Constants.Icons.BLUE_SQUARE),
		GRAY(Constants.Icons.GRAY_SQUARE),
		GREEN_CANDIDATE(Constants.Icons.GREEN_CANDIDATE_SQUARE),
		BLUE_CANDIDATE(Constants.Icons.BLUE_CANDIDATE_SQUARE),
		GRAY_CANDIDATE(Constants.Icons.GRAY_CANDIDATE_SQUARE);
		
		private ImageIcon icon;
		
		private EntryIcon(Icon icon) {
			this.icon = (ImageIcon) icon;
		}
	}
	
	private String value;
	private EntryIcon entryIcon;
	
	public ListEntry(String value, EntryIcon icon) {
		this.value = value;
		this.entryIcon = icon;
	}
	
	public boolean isCandidate() {
		return entryIcon == EntryIcon.GREEN_CANDIDATE ||
			   entryIcon == EntryIcon.BLUE_CANDIDATE ||
			   entryIcon == EntryIcon.GRAY_CANDIDATE;
	}
	
	public boolean isGrayed() {
		return entryIcon == EntryIcon.GRAY ||
			   entryIcon == EntryIcon.GRAY_CANDIDATE;
	}
	
	public String getValue() { return value; }
	
	public EntryIcon getEntryIcon() { return entryIcon; }
	
	public ImageIcon getIcon() { return entryIcon.icon; }
	
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