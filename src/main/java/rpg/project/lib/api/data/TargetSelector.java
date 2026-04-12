package rpg.project.lib.api.data;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;

import java.util.List;

/// Used in scripting to parse custom target selectors.  examples in the default
/// implementation include "food" and "tools" which are narrower in scope than
/// the built-in implementation of "item".
@FunctionalInterface
public interface TargetSelector {
    /**Contains the type and value(s) obtained during a target parsing operation.
     * For custom implementations, the type is usually known by the implementation
     * and the IDs are what are returned dynamically.
     *
     * @param type the type of this object
     * @param IDs the identifier(s) being targeted.
     */
    record Selection(ObjectType type, List<Identifier> IDs) {}

    /**When the custom target keyword is invoked, this function is given the raw
     * parameter "functionName(parameter)" value, and a registry access to then
     * construct and return the object IDs for the type that this scripting targeter
     * represents.
     *
     * @param param raw function string to be parsed
     * @param access access, if needed
     * @return the ID(s) and their type for the engine to pass along to function calls
     */
    Selection read(String param, RegistryAccess access);
}
