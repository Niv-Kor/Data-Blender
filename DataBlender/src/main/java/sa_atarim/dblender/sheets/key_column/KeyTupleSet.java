package sa_atarim.dblender.sheets.key_column;
import java.util.HashSet;

public class KeyTupleSet extends HashSet<KeyTuple>
{
	private static final long serialVersionUID = -2883857792760452910L;
	
	public KeyTupleSet() { super(); }
	
	public KeyTupleSet(KeyTupleSet set) { super(set); }
	
	/**
	 * @param set - A set of key values
	 * @param value - The value to retrieve
	 * @return A key tuple from the set that has the same value.
	 */
	public KeyTuple getSimilarValue(Object value) {
		for (KeyTuple key : this)
			if (key.valueEquals(value)) return key;

		return null;
	}
	
	public boolean containsValue(Object value) {
		for (KeyTuple key : this)
			if (key.valueEquals(value)) return true;

		return false;
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
		
		for (KeyTuple key1 : this) {
			boolean match = false;
			
			for (KeyTupleSet integrator : integrators) {
				for (KeyTuple key2 : integrator) {
					if (key1.valueEquals(key2.value)) {
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
}