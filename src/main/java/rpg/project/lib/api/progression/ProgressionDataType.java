package rpg.project.lib.api.progression;

import rpg.project.lib.api.data.SubSystemConfig;

public interface ProgressionDataType extends SubSystemConfig{
	
	public static enum Comparison {
		EQUALS,
		GREATER_THAN,
		LESS_THAN,
		GREATER_THAN_OR_EQUAL,
		LESS_THAN_OR_EQUAL;
	}
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
	
	public static enum Modification {
		/**Similar to an add function, this operator summates the two
		 * values according to the logical summation of the data type*/
		INCREASE,
		/**Similar to a subtract function, this operator finds the
		 * difference between values according to the logical difference
		 * comparison of the data type*/
		DECREASE,
		/**Similar to a multiplication function, this operator scales
		 * the orignal value by the second value according ot the logical
		 * scaling of the data type*/
		MULTIPLY,
		/**Similar to division, this operator partitions the value into
		 * a smaller portion according to the value provided and the 
		 * logical division of the data type*/
		DIVIDE,
		/**Simply replaces the existing value with the one provided.*/
		REPLACE;
	}
	/**returns a modified instance according to the operator using
	 * the provided data reference.
	 * 
	 * @param operator the operation used on this instance
	 * @param with the value used by the operator on this instance
	 * @return a new instance after modification
	 */
	ProgressionDataType modify(Modification operator, ProgressionDataType with);
}
