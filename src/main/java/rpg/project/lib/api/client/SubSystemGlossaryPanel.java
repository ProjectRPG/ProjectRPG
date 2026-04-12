package rpg.project.lib.api.client;

import rpg.project.lib.api.data.SubSystemConfig;

/**A functional interface to create glossary panel from a subsystem
 * instance.  The Glossary uses the function to create the panel
 * widgets at invocation using configuration data available.
 */
@FunctionalInterface
public interface SubSystemGlossaryPanel {
    /**Creates a new glossary panel from the subsystem instance config
     *
     * @param config the config as obtained from the configuration data of the object
     * @return a panel constructed from the config data
     */
    ReactiveWidget make(SubSystemConfig config);
}
