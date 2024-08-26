package rpg.project.lib.internal.client.glossary;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;
import org.apache.commons.lang3.StringUtils;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.config.readers.MainSystemConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ObjectScroll extends ScrollPanel {
    List<Panel> panelList = new ArrayList<>();
    Font font = Minecraft.getInstance().font;

    public ObjectScroll(int width, int height, int top, int left) {
        super(Minecraft.getInstance(), width, height, top, left);
        filter(null, null, null);
    }

    @Override
    protected int getContentHeight() {
        return 20 * panelList.size();
    }

    @Override
    protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, Tesselator tess, int mouseX, int mouseY) {
        for (int i = 0; i < panelList.size(); i++) {
            Panel panel = panelList.get(i);
            if (panel.renderer != null)
                panel.renderer.render(guiGraphics, entryRight - width, relativeY + (i * 20));
            guiGraphics.drawScrollingString(font, panel.text, entryRight - width + 20, entryRight, relativeY + (i * 20) + 2, 0xFFFFFF);
        }
    }

    //TODO add hover, click, and scroll behavior overrides

    @Override
    public NarrationPriority narrationPriority() {return NarrationPriority.NONE;}

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {}

    private static <T> boolean validObject(ObjectType type, ResourceLocation eventFilter, String searchFilter, ResourceLocation objectID, String name, Core core) {
        if (eventFilter == null && (searchFilter == null || searchFilter.isEmpty())) return true;
        if (searchFilter != null && StringUtils.containsIgnoreCase(name, searchFilter) && !searchFilter.isEmpty()) return true;
        MainSystemConfig config = core.getLoader().getLoader(type).getData(objectID);
        if (!config.isUnconfigured()) {
            String rawConfig = MainSystemConfig.CODEC.encodeStart(JsonOps.INSTANCE, config).result().orElse(new JsonObject()).toString();
            if (eventFilter != null && StringUtils.containsIgnoreCase(rawConfig, eventFilter.toString()))
                return true;
            if (searchFilter != null && !searchFilter.isEmpty() && StringUtils.containsIgnoreCase(rawConfig, searchFilter))
                return true;
        }
        return false;
    }

    public void filter(ObjectType typeFilter, ResourceLocation eventFilter, String searchFilter) {
        panelList.clear();
        RegistryAccess reg = Minecraft.getInstance().level.registryAccess();
        Core core = Core.get(LogicalSide.CLIENT);
        //TODO generate content based on filters
        if (typeFilter == null || typeFilter == ObjectType.ITEM) {
            reg.registryOrThrow(Registries.ITEM).entrySet().stream()
                    .filter(item -> validObject(ObjectType.ITEM, eventFilter, searchFilter, item.getKey().location(), item.getValue().getDefaultInstance().getDisplayName().getString(), core))
                    .forEach(item -> panelList.add(new Panel(item.getValue().getDefaultInstance())));
        }
        if (typeFilter == null || typeFilter == ObjectType.BLOCK) {
            reg.registryOrThrow(Registries.BLOCK).entrySet().stream()
                    .filter(entry -> validObject(ObjectType.BLOCK, eventFilter, searchFilter, entry.getKey().location(), entry.getValue().getName().getString(), core))
                    .forEach(block -> panelList.add(new Panel(block.getValue())));
        }
        if (typeFilter == null || typeFilter == ObjectType.ENTITY) {
            reg.registryOrThrow(Registries.ENTITY_TYPE).entrySet().stream()
                    .filter(entry -> validObject(ObjectType.ENTITY, eventFilter, searchFilter, entry.getKey().location(), entry.getValue().getDescription().getString(), core))
                    .forEach(entity -> panelList.add(new Panel(entity.getValue())));
        }
        if (typeFilter == null || typeFilter == ObjectType.DIMENSION) {
            Minecraft.getInstance().getConnection().levels().stream()
                    .filter(level -> validObject(ObjectType.DIMENSION, eventFilter, searchFilter, level.location(), level.location().toString(), core))
                    .forEach(key -> panelList.add(new Panel(Component.literal(key.location().toString()))));
        }
        if (typeFilter == null || typeFilter == ObjectType.BIOME) {
            reg.registryOrThrow(Registries.BIOME).entrySet().stream()
                    .filter(entry -> validObject(ObjectType.BIOME, eventFilter, searchFilter, entry.getKey().location(), entry.getKey().location().toString(), core))
                    .forEach(entry -> panelList.add(new Panel(Component.literal(entry.getKey().location().toString()))));
        }
        if (typeFilter == null || typeFilter == ObjectType.ENCHANTMENT) {
            reg.registryOrThrow(Registries.ENCHANTMENT).entrySet().stream()
                    .filter(entry -> validObject(ObjectType.ENCHANTMENT, eventFilter, searchFilter, entry.getKey().location(), entry.getValue().description().getString(), core))
                    .forEach(entry -> panelList.add(new Panel(entry.getValue().description())));
        }
        if (typeFilter == null || typeFilter == ObjectType.EFFECT) {
            reg.registryOrThrow(Registries.MOB_EFFECT).entrySet().stream()
                    .filter(entry -> validObject(ObjectType.EFFECT, eventFilter, searchFilter, entry.getKey().location(), entry.getValue().getDisplayName().getString(), core))
                    .forEach(entry -> panelList.add(new Panel(entry.getValue().getDisplayName())));
        }
        if (typeFilter == null || typeFilter == ObjectType.EVENT) {
            reg.registryOrThrow(APIUtils.GAMEPLAY_EVENTS).entrySet().stream()
                    .filter(entry -> validObject(ObjectType.EVENT, eventFilter, searchFilter, entry.getKey().location(), entry.getKey().location().toString(), core))
                    .forEach(entry -> panelList.add(new Panel(Component.literal(entry.getKey().location().toString()))));
        }
        if (typeFilter == null || typeFilter == ObjectType.PLAYER) {
            if (validObject(ObjectType.ENTITY, eventFilter, searchFilter, ResourceLocation.withDefaultNamespace("player"), Minecraft.getInstance().player.getDisplayName().getString(), core))
                panelList.add(new Panel(Minecraft.getInstance().player));
        }
    }

    private class Panel {
        public PanelRenderer renderer;
        public Component text;

        public Panel(Component text) {
            this.text = text;
        }
        public Panel(ItemStack stack) {
            this.text = stack.getDisplayName();
            this.renderer = (graphics, x, y) -> graphics.renderItem(stack, x, y);
        }
        public Panel(Block block) {
            this.text = block.getName();
            this.renderer = (graphics, x, y) -> graphics.renderItem(block.asItem().getDefaultInstance(), x, y);

        }
        public Panel(EntityType<?> entity) {
            Entity renderEntity = entity.create(Minecraft.getInstance().level);
            this.text = renderEntity == null ? entity.getDescription() : renderEntity.getDisplayName();
            if (renderEntity instanceof LivingEntity livingEntity)
                this.renderer = (graphics, x, y) -> InventoryScreen.renderEntityInInventoryFollowsAngle(graphics, x, y, x+16, y+16, Math.max(1, 10 / Math.max(1, (int) livingEntity.getBoundingBox().getSize())), 0, 0f, 0f, livingEntity);
        }
        public Panel(LocalPlayer player) {
            this.text = player.getDisplayName();
            this.renderer = (graphics, x, y) -> InventoryScreen.renderEntityInInventoryFollowsAngle(graphics, x, y, x+16, y+16, Math.max(1, 10 / Math.max(1, (int) player.getBoundingBox().getSize())), 0, 0f, 0f, player);
        }
    }

    @FunctionalInterface
    private interface PanelRenderer {
        void render(GuiGraphics guiGraphics, int x, int y);
    }
}
