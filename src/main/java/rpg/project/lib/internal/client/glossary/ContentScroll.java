package rpg.project.lib.internal.client.glossary;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.fml.LogicalSide;
import rpg.project.lib.api.client.ResponsiveLayout;
import rpg.project.lib.api.client.types.DisplayType;
import rpg.project.lib.api.client.types.GlossaryFilter;
import rpg.project.lib.api.client.wrappers.BoxDimensions;
import rpg.project.lib.api.client.wrappers.Positioner;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.client.glossary.components.ItemHeader;
import rpg.project.lib.internal.config.readers.MainSystemConfig;
import rpg.project.lib.internal.util.MsLoggy;
import rpg.project.lib.internal.util.RegistryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class ContentScroll extends ObjectSelectionList<ContentScroll.Panel> implements GlossaryFilter, ResponsiveLayout {
    @Override public DisplayType getDisplayType() {return DisplayType.BLOCK;}
    @Override public BoxDimensions getMargin() {return new BoxDimensions(0,0,0,0);}
    @Override public BoxDimensions getPadding() {return new BoxDimensions(0,0,0,0);}
    @Override public ResponsiveLayout setMargin(int left, int top, int right, int bottom) {return this;}
    @Override public ResponsiveLayout setPadding(int left, int top, int right, int bottom) {return this;}
    @Override public List<Positioner<?>> getChildren() {return List.of();}
    @Override public void addChild(Positioner<?> child) {}
    @Override public List<Positioner<?>> visibleChildren() {return List.of();}

    /**Wrapper around the {@link PanelWidget}s that render the actual content*/
    public static class Panel extends ObjectSelectionList.Entry<Panel> implements GlossaryFilter {
        private final PanelWidget widget;
        public Panel(PanelWidget widget) {this.widget = widget;}

        public int getHeight() {return widget.getHeight();}

        @Override
        public void extractContent(GuiGraphicsExtractor guiGraphicsExtractor, int i, int i1, boolean b, float v) {
            widget.setPosition(this.getX(), this.getY());
            widget.setWidth(this.getWidth());
            widget.setHeight(this.getHeight());
            widget.extractWidgetRenderState(guiGraphicsExtractor, i, i1, v);
        }

        @Override public Component getNarration() {return Component.literal("Sorry not done yet.");}

        @Override
        public boolean applyFilter(Filter filter) {
            return widget.applyFilter(filter);
        }
    }
    private final List<Panel> allItems = new ArrayList<>();

    public ContentScroll() {
        super(Minecraft.getInstance(), 100, 100, 0, 20);
        LocalPlayer player = minecraft.player;
        CreativeModeTabs.tryRebuildTabContents(player.connection.enabledFeatures(), player.canUseGameMasterBlocks(), player.registryAccess());
        generateContent();
    }

    @Override public int getRowLeft() {return this.getX();}
    @Override public int getRowWidth() {return this.getWidth() - this.scrollbarWidth();}

    @Override
    public boolean applyFilter(Filter filter) {
        List<Panel> filteredEntries = this.allItems.stream().filter(panel -> !panel.applyFilter(filter)).toList();
        this.replaceEntries(filteredEntries);
        return false;
    }

    private CompletableFuture<List<Panel>> async(Supplier<List<Panel>> supplies, Executor executor) {
        return CompletableFuture.supplyAsync(supplies, executor).handle((result, err) -> {
            if (err != null) {
                err.printStackTrace();
                return new ArrayList<>();
            }
            return result;
        });
    }

    private void generateContent() {
        Executor executor= Executors.newCachedThreadPool();
        Core core = Core.get(LogicalSide.CLIENT);
//        List<Positioner.Widget> explanation = this.font.getSplitter()
//                .splitLines(LangProvider.LOADING_EXPLANATION.asComponent().getString(), this.width/2, Style.EMPTY)
//                .stream().map(fcs -> new Positioner.Widget(
//                        new StringWidget(Component.literal(fcs.getString()), font),
//                        PositionType.STATIC.constraint,
//                        SizeConstraints.builder().absoluteHeight(12).build()))
//                .toList();
//
//        explanation.forEach(this::addChild);
        CompletableFuture<List<Panel>> items = async(() ->
            CreativeModeTabs.searchTab().getDisplayItems().stream().map(stack -> {
                Identifier id = RegistryUtil.getId(stack);
                MainSystemConfig config = core.getLoader().getLoader(ObjectType.ITEM).getData(id);
                ItemHeader header = new ItemHeader(stack);
                return new Panel(new PanelWidget(0x882e332e, width, config, header));
            }).toList(), executor);
//
//        RegistryAccess access = Minecraft.getInstance().player.registryAccess();
//        CompletableFuture<List<Positioner.Layout>> blocks = layoutAsync(() ->
//                access.lookupOrThrow(Registries.BLOCK).listElements().map(ref -> new Positioner.Layout(
//                        new BlockObjectPanelWidget(0x882e2f33, width, ref.value()),
//                        PositionType.STATIC.constraint,
//                        SizeConstraints.builder().internalHeight().build()
//                )).toList(), executor);
//
//        CompletableFuture<List<Positioner.Layout>> entities = layoutAsync(() ->
//                access.lookupOrThrow(Registries.ENTITY_TYPE).listElements()
//                        .map(ref -> ref.value().create(Minecraft.getInstance().level, EntitySpawnReason.COMMAND))
//                        .filter(Objects::nonNull)
//                        .map(entity -> new Positioner.Layout(
//                                new EntityObjectPanelWidget(0x88394045, width, entity),
//                                PositionType.STATIC.constraint,
//                                SizeConstraints.builder().internalHeight().build())
//                        ).toList(), executor);
//
//        CompletableFuture<List<Positioner.Layout>> biomes = layoutAsync(() ->
//                access.lookupOrThrow(Registries.BIOME).listElements()
//                        .filter(Objects::nonNull)
//                        .map(biome -> new Positioner.Layout(
//                                new BiomeObjectPanelWidget(0x88394045, width, biome),
//                                PositionType.STATIC.constraint,
//                                SizeConstraints.builder().internalHeight().build())).toList(), executor);
//
//        CompletableFuture<List<Positioner.Layout>> dimensions = layoutAsync(() ->
//                Minecraft.getInstance().getConnection().levels().stream()
//                        .filter(Objects::nonNull)
//                        .map(key -> new Positioner.Layout(
//                                new DimensionObjectPanelWidget(0x88394045, width, key),
//                                PositionType.STATIC.constraint,
//                                SizeConstraints.builder().internalHeight().build())).toList(), executor);
//
//        CompletableFuture<List<Positioner.Layout>> effects = layoutAsync(() ->
//                access.lookupOrThrow(Registries.MOB_EFFECT).listElements()
//                        .filter(Objects::nonNull)
//                        .map(holder -> new Positioner.Layout(
//                                new EffectsObjectPanelWidget(0x88394045, width, holder.value()),
//                                PositionType.STATIC.constraint,
//                                SizeConstraints.builder().internalHeight().build())).toList(), executor);
//
//        CompletableFuture<List<Positioner.Layout>> enchantments = layoutAsync(() ->
//                access.lookupOrThrow(Registries.ENCHANTMENT).listElements()
//                        .filter(Objects::nonNull)
//                        .map(enchant -> new Positioner.Layout(
//                                new EnchantmentsObjectPanelWidget(0x88394045, width, enchant.value()),
//                                PositionType.STATIC.constraint,
//                                SizeConstraints.builder().internalHeight().build())).toList(), executor);
//
//        CompletableFuture<List<Positioner.Layout>> perks = layoutAsync(() ->
//                Config.perks().perks().entrySet().stream().map(entry -> new Positioner.Layout(
//                        new PerkObjectPanelWidget(0x88394045, width, entry.getKey(), entry.getValue()),
//                        PositionType.STATIC.constraint,
//                        SizeConstraints.builder().internalHeight().build())).toList(), executor);
//
        CompletableFuture.allOf(
                items//,
//                blocks,
//                entities,
//                biomes,
//                dimensions,
//                effects,
//                enchantments,
//                abilities
        ).thenRun(() -> {
            try {
                allItems.addAll(items.join());
//                cache.addAll(blocks.join());
//                cache.addAll(entities.join());
//                cache.addAll(biomes.join());
//                cache.addAll(dimensions.join());
//                cache.addAll(effects.join());
//                cache.addAll(enchantments.join());
//                cache.addAll(perks.join());
//                explanation.forEach(poser -> poser.get().visible = false);
                this.replaceEntries(allItems);
            } catch (Exception e) {
                MsLoggy.ERROR.log(MsLoggy.LOG_CODE.GUI, e.getLocalizedMessage());
            }
        });
    }
}
