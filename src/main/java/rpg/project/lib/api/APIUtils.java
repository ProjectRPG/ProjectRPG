package rpg.project.lib.api;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.api.events.EventListenerSpecification;
import rpg.project.lib.internal.registry.SubSystemCodecRegistry;
import rpg.project.lib.internal.util.Reference;

public class APIUtils {
	/**{@link ResourceKey} for use with a {@link net.minecraftforge.registries.DeferredRegister DeferredRegister}
	 * to register custom events.  Custom events can be referenced by other addons' configurations to apply their
	 * system implementation behavior to your event specification.*/
	public static final ResourceKey<Registry<EventListenerSpecification<?>>> GAMEPLAY_EVENTS = ResourceKey.createRegistryKey(Reference.resource("gameplay_events"));
	
	public static Codec<SubSystemConfigType> getDispatchCodec() {
		return SubSystemCodecRegistry.CODEC;
	}
}
