package rpg.project.lib.api.data;

/**Implemented by the internal master config object and 
 * {@link SubSystemConfig}, this interface governs behavior
 * related to data loading which assists with handling 
 * multiple config files of for the same object via 
 * {@link #combine(MergeableData)} and eliminating extra
 * data on load via {@link #isUnconfigured()}.
 *
 */
public interface MergeableData {
	/**Tells other data objects being merged if this object should be considered
	 * to have priority over its counterpart.  This property is intended to be
	 * used within {@link #combine(MergeableData)} to discriminate data sources
	 * that users wish to elevate their relevance for.
	 *
	 * @return if this object is considered high priority in the context of merges
	 */
	boolean isPriorityData();
	/**Combines the current instance with the supplied instance and
	 * returns a new object.  Neither original object are modified
	 * by this method.
	 * 
	 * @param two the object to be integrated into this one
	 * @return a new combined object
	 */
    MergeableData combine(MergeableData two);
	
	/**Used by data loaders to identify objects that have been 
	 * constructed with no customized values.  This is used to 
	 * bypass objects that add no value and would be burdensome
	 * to send as packets to clients.
	 * 
	 * @return if the object contains only empty/default configurations
	 */
    boolean isUnconfigured();
}
