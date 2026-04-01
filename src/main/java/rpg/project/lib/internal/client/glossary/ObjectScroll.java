package rpg.project.lib.internal.client.glossary;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
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

public class ObjectScroll extends ObjectSelectionList<ObjectScroll.Panel> {
    Font font = Minecraft.getInstance().font;

    public ObjectScroll(int width, int height, int top, int left) {
        super(Minecraft.getInstance(), width, height, top, 20);
        this.setX(left);
        filter(null, null, null);
    }

    @Override
    public int getRowWidth() {return this.width;}
    @Override
    protected int scrollBarX() {return this.getRowLeft() + this.width - 6;}

    @Override
    public NarrationPriority narrationPriority() {return NarrationPriority.NONE;}

    private static <T> boolean validObject(ObjectType type, Identifier eventFilter, String searchFilter, Identifier objectID, String name, Core core) {
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

    public void filter(ObjectType typeFilter, Identifier eventFilter, String searchFilter) {
        this.clearEntries();
        RegistryAccess reg = Minecraft.getInstance().level.registryAccess();
        Core core = Core.get(LogicalSide.CLIENT);

        if (typeFilter == null || typeFilter == ObjectType.ITEM) {
            reg.lookupOrThrow(Registries.ITEM).entrySet().stream()
                    .filter(item -> validObject(ObjectType.ITEM, eventFilter, searchFilter, item.getKey().identifier(), item.getValue().getDefaultInstance().getDisplayName().getString(), core))
                    .forEach(item -> this.addEntry(new Panel(item.getValue().getDefaultInstance())));
        }
        if (typeFilter == null || typeFilter == ObjectType.BLOCK) {
            reg.lookupOrThrow(Registries.BLOCK).entrySet().stream()
                    .filter(entry -> validObject(ObjectType.BLOCK, eventFilter, searchFilter, entry.getKey().identifier(), entry.getValue().getName().getString(), core))
                    .forEach(block -> this.addEntry(new Panel(block.getValue())));
        }
        if (typeFilter == null || typeFilter == ObjectType.ENTITY) {
            reg.lookupOrThrow(Registries.ENTITY_TYPE).entrySet().stream()
                    .filter(entry -> validObject(ObjectType.ENTITY, eventFilter, searchFilter, entry.getKey().identifier(), entry.getValue().getDescription().getString(), core))
                    .forEach(entity -> this.addEntry(new Panel(entity.getValue())));
        }
        if (typeFilter == null || typeFilter == ObjectType.DIMENSION) {
            Minecraft.getInstance().getConnection().levels().stream()
                    .filter(level -> validObject(ObjectType.DIMENSION, eventFilter, searchFilter, level.identifier(), level.identifier().toString(), core))
                    .forEach(key -> this.addEntry(new Panel(Component.literal(key.identifier().toString()), ObjectType.DIMENSION)));
        }
        if (typeFilter == null || typeFilter == ObjectType.BIOME) {
            reg.lookupOrThrow(Registries.BIOME).entrySet().stream()
                    .filter(entry -> validObject(ObjectType.BIOME, eventFilter, searchFilter, entry.getKey().identifier(), entry.getKey().identifier().toString(), core))
                    .forEach(entry -> this.addEntry(new Panel(Component.literal(entry.getKey().identifier().toString()), ObjectType.BIOME)));
        }
        if (typeFilter == null || typeFilter == ObjectType.ENCHANTMENT) {
            reg.lookupOrThrow(Registries.ENCHANTMENT).entrySet().stream()
                    .filter(entry -> validObject(ObjectType.ENCHANTMENT, eventFilter, searchFilter, entry.getKey().identifier(), entry.getValue().description().getString(), core))
                    .forEach(entry -> this.addEntry(new Panel(entry.getValue().description(), ObjectType.ENCHANTMENT)));
        }
        if (typeFilter == null || typeFilter == ObjectType.EFFECT) {
            reg.lookupOrThrow(Registries.MOB_EFFECT).entrySet().stream()
                    .filter(entry -> validObject(ObjectType.EFFECT, eventFilter, searchFilter, entry.getKey().identifier(), entry.getValue().getDisplayName().getString(), core))
                    .forEach(entry -> this.addEntry(new Panel(entry.getValue().getDisplayName(), ObjectType.EFFECT)));
        }
        if (typeFilter == null || typeFilter == ObjectType.EVENT) {
            reg.lookupOrThrow(APIUtils.GAMEPLAY_EVENTS).entrySet().stream()
                    .filter(entry -> validObject(ObjectType.EVENT, eventFilter, searchFilter, entry.getKey().identifier(), entry.getKey().identifier().toString(), core))
                    .forEach(entry -> this.addEntry(new Panel(Component.literal(entry.getKey().identifier().toString()), ObjectType.EVENT)));
        }
        if (typeFilter == null || typeFilter == ObjectType.PLAYER) {
            if (validObject(ObjectType.ENTITY, eventFilter, searchFilter, Identifier.withDefaultNamespace("player"), Minecraft.getInstance().player.getDisplayName().getString(), core))
                this.addEntry(new Panel(Minecraft.getInstance().player));
        }
    }

    public class Panel extends ObjectSelectionList.Entry<Panel>{
        public PanelRenderer renderer;
        public final Component text;
        public final ObjectType type;

        public Panel(Component text, ObjectType type) {
            this.text = text;
            this.type = type;
        }
        public Panel(ItemStack stack) {
            this.text = stack.getDisplayName();
            this.renderer = (graphics, x, y) -> graphics.item(stack, x, y);
            this.type = ObjectType.ITEM;
        }
        public Panel(Block block) {
            this.text = block.getName();
            this.renderer = (graphics, x, y) -> graphics.item(block.asItem().getDefaultInstance(), x, y);
            this.type = ObjectType.BLOCK;
        }
        public Panel(EntityType<?> entity) {
            Entity renderEntity = entity.create(Minecraft.getInstance().level, EntitySpawnReason.NATURAL);
            this.text = renderEntity == null ? entity.getDescription() : renderEntity.getDisplayName();
            this.type = ObjectType.ENTITY;
            if (renderEntity instanceof LivingEntity livingEntity)
                this.renderer = (graphics, x, y) -> InventoryScreen.renderEntityInInventoryFollowsAngle(graphics, x, y, x+16, y+16, Math.max(1, 10 / Math.max(1, (int) livingEntity.getBoundingBox().getSize())), 0, 0f, 0f, livingEntity);
        }
        public Panel(LocalPlayer player) {
            this.text = player.getDisplayName();
            this.type = ObjectType.ENTITY;
            this.renderer = (graphics, x, y) -> InventoryScreen.renderEntityInInventoryFollowsAngle(graphics, x, y, x+16, y+16, Math.max(1, 10 / Math.max(1, (int) player.getBoundingBox().getSize())), 0, 0f, 0f, player);
        }

        @Override
        public Component getNarration() {return text;}

        @Override
        public void extractContent(GuiGraphicsExtractor pGuiGraphics, int x, int y, boolean pHovering, float pPartialTick) {
            if (renderer != null)
                renderer.render(pGuiGraphics, this.getX(), this.getY());
            pGuiGraphics.drawScrollingString(pGuiGraphics.textRenderer(), font, text, this.getX() + 20, this.getContentRight(), this.getY() + 2);
        }
    }

    @FunctionalInterface
    private interface PanelRenderer {
        void render(GuiGraphicsExtractor guiGraphics, int x, int y);
    }
}
