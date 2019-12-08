package sa_atarim.dblender.sheets.key_column;
import java.util.HashSet;

public class KeyTupleSet extends HashSet<ConstantCell>
{
	private static final long serialVersionUID = -2883857792760452910L;
	
	public KeyTupleSet() { super(); }
	
	public KeyTupleSet(KeyTupleSet set) { super(set); }
	
	/**
	 * @param set - A set of key values
	 * @param value - The value to retrieve
	 * @return A key tuple from the set that has the same value.
	 */
	public ConstantCell getSimilarValue(Object value) {
		for (ConstantCell key : this)
			if (key.value.equals((String) value)) return key;

		return null;
	}
	
	public boolean containsValue(Object value) {
		return getSimilarValue(value) != null;
	}
	
	/**
	 * Create a set of key values that contains an intersection of all given sets.
	 * 
	 * @param base - The base key values set (it contains the important row indexes)
	 * @param integrators - All other key values sets
	 * @return An intersection of all sets.
	 */
	public KeyTupleSet intersect(KeyTupleSet ... integrators) {
		KeyTupleSet intersectionKeyVals = new KeyTupleSet(this);
		
		for (ConstantCell key1 : this) {
			boolean match = false;
			
			for (KeyTupleSet integrator : integrators) {
				for (ConstantCell key2 : integrator) {
					if (key1.equals(key2)) {
						match = true;
						break;
					}
				}
			}
			
			//remove key from intersection
			if (!match) intersectionKeyVals.remove(key1);
		}
		
		return intersectionKeyVals;
	}
	
	/**
	 * Shift all of the key tuple row indices by a specified amount of rows.
	 * 
	 * @param n - The amount of rows to shift the indices by (negative to shift upwards)
	 */
	public void shiftAllIndices(int n) {
		for (ConstantCell key : this) key.index += n;
	}
}