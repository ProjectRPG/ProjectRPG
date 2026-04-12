package rpg.project.lib.api.data;

import net.minecraft.resources.Identifier;

import java.util.Map;

/// Used by the scripting engine as recipes for processing specific keywords in a script.
///
/// ### Understanding Scripting for NodeConsumers
/// A basic script will have an object specifier, a function, and values that function needs.
///
/// `type(id).function(param).value(data);`
///
/// A more complicated script might include multiple functions. for example setting progression
/// and a gate in the same call
///
/// `type(id).progression(systemid).xp(100).gate(systemID).requires(param);`
///
/// In the above case, the values are unique ("xp" and "requires") so we do not need to qualify them.
/// However, if there is overlap, it is recommended for users and implementations to prefer them.
/// In the next example we have a hypothetical function which routes values internally.
///
/// `type(id).routeA$hypo(param).routeB$hypo(param).routeA$hypoValue(123).routeB$hypoValue(456);`
///
/// As shown the function name "hypo" and it's expected value of "hypoValue" are used twice each.
/// However, the use of "qualifier$" prefixes allows a node consumer to know that it was called
/// with this distinction and to use that to find corresponding values as it sees fit.  Ultimately,
/// the decision to use or ignore qualifiers is on the implementation and whether it matters for
/// the function being implemented.
@FunctionalInterface
public interface NodeConsumer {
    /**Invoked by the scripting engine's function lookup, this method executes the
     * commit behavior of a scripting function by passing in the parsed values for
     * the node.
     *
     * @param qualifier a prefix used to distinguish functions of the same type in a single call.
     * @param param the specific value used inside the function parameter parentheses
     * @param id the object ID being scripted
     * @param type the object Type being scripted
     * @param value All "value"s data.
     */
    void consume(String qualifier, String param, Identifier id, ObjectType type, Map<String, String> value);
}
