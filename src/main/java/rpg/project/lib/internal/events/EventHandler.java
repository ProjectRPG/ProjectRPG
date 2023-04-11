package rpg.project.lib.internal.events;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import rpg.project.lib.api.abilities.AbilityUtils;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.registry.EventRegistry;
import rpg.project.lib.internal.util.Reference;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void tickPerks(TickEvent.LevelTickEvent event) {
        Core.get(event.level).getAbilityRegistry().executeAbilityTicks(event);
    }
    
    // TEST
    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void onBlockBreak(PlayerEvent.BreakSpeed event) {
        if (event.isCanceled()) { return; }
        
        Core core = Core.get(event.getEntity().getLevel());
        CompoundTag dataIn = new CompoundTag();
        
        dataIn.putFloat(AbilityUtils.BREAK_SPEED_INPUT_VALUE, event.getNewSpeed());
        dataIn.putLong(AbilityUtils.BLOCK_POS, event.getPosition().orElse(new BlockPos(0,0,0)).asLong());
        
        CompoundTag dataOut = core.getAbilityRegistry().executeAbility(EventRegistry.BREAK.getId(), event.getEntity(), dataIn);
        if (dataOut.contains(AbilityUtils.BREAK_SPEED_OUTPUT_VALUE)) {
            float newSpeed = Math.max(0, dataOut.getFloat(AbilityUtils.BREAK_SPEED_OUTPUT_VALUE));
            event.setNewSpeed(newSpeed);
        }
    }
}
