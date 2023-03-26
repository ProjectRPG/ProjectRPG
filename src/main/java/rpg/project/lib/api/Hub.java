package rpg.project.lib.api;

import rpg.project.lib.api.party.PartySystem;

/**Implementations of this provide access to shared
 * features of the library.  Project RPG provides an
 * implementation internally and expects you to access
 * all shared resources through this object when
 * supplied.
 */
public interface Hub {
	/**@return the active {@link PartySystem} implementation for this instance
	 */
	PartySystem getParty();
}
