package rpg.project.lib.api.progression;

import rpg.project.lib.api.data.SubSystemConfig;

public interface ProgressionDataType extends SubSystemConfig{
	
	enum Comparison {
		EQUALS,
		GREATER_THAN,
		LESS_THAN,
		GREATER_THAN_OR_EQUAL,
		LESS_THAN_OR_EQUAL
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
	
	enum Modification {
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
		REPLACE
    }
	/**returns a modified instance according to the operator using
	 * the provided data reference.
	 * 
	 * @param operator the operation used on this instance
	 * @param with the value used by the operator on this instance
	 * @return a new instance after modification
	 */
	ProgressionDataType modify(Modification operator, ProgressionDataType with);

	/**<p>Progression, conceptually, is the incremental stepping from one state of
	 * progress to another.  Whether your implementation is numeric and continuous
	 * by nature or not, the RPG experience mandates that player experience a
	 * progressive adventure from start through completion.  It is up to your implementation
	 * to decide how to represent the position in that continuum numerically.
	 * <p>The following list gives examples of implementations based on hypothetical
	 * data types <ul>
	 * <li>"levels" can be returned literally</li>
	 * <li>"unlocks" (boolean values) can be zero or one</li>
	 * <li>"quests" (complex multi-factor unlocks) can be represented as 1-100 as decimal shifted percentages</li>
	 * </ul><p>Ultimately, you will be responsible for providing your users with an explanation of
	 * how your system is represented numerically for them to use in their configurations.
	 * <p>
	 * It is strongly recommended you overwrite this javadoc with your implementation details
	 * when overriding this method.
	 *
	 * @return a numeric representation of the progression.
	 */
	long getProgressAsNumber();
}
