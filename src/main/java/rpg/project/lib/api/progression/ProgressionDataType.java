package rpg.project.lib.api.progression;

import rpg.project.lib.api.data.SubSystemConfig;

public interface ProgressionDataType extends SubSystemConfig{
	/**<p>Returns this object's comparison with the supplied object. 
	 * In sequence, this object is the first argument, followed by
	 * the operator, then the supplied object</p>
	 * <p>Examples:<br><code>
	 * this EQUALS with; // this == with;<br>
	 * this GREATER_THAN with; // this > with;
	 * </code></p>
	 * 
	 * @param operator the comparison used
	 * @param with the object being compared to
	 * @return whether the relationship between the objects is true for the operation
	 */
	float compare(Comparison operator, ProgressionDataType with);
	
	public static enum Comparison {
		EQUALS,
		GREATER_THAN,
		LESS_THAN,
		GREATER_THAN_OR_EQUAL,
		LESS_THAN_OR_EQUAL;
	}
}
