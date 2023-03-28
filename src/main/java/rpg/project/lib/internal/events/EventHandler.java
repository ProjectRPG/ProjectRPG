package rpg.project.lib.internal.events;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.util.Reference;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void tickPerks(TickEvent.LevelTickEvent event) {
        Core.get(event.level).getAbilityRegistry().executeAbilityTicks(event);
    }
}
