package rpg.project.lib.internal.client.glossary;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.fml.LogicalSide;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.client.ResponsiveLayout;
import rpg.project.lib.api.client.types.DisplayType;
import rpg.project.lib.api.client.types.GlossaryFilter;
import rpg.project.lib.api.client.types.PositionType;
import rpg.project.lib.api.client.wrappers.BoxDimensions;
import rpg.project.lib.api.client.wrappers.Positioner;
import rpg.project.lib.api.client.wrappers.SizeConstraints;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.client.glossary.components.BiomeHeader;
import rpg.project.lib.internal.client.glossary.components.BlockHeader;
import rpg.project.lib.internal.client.glossary.components.DimensionHeader;
import rpg.project.lib.internal.client.glossary.components.EffectHeader;
import rpg.project.lib.internal.client.glossary.components.EnchantmentHeader;
import rpg.project.lib.internal.client.glossary.components.EntityHeader;
import rpg.project.lib.internal.client.glossary.components.EventHeader;
import rpg.project.lib.internal.client.glossary.components.ItemHeader;
import rpg.project.lib.internal.config.readers.MainSystemConfig;
import rpg.project.lib.internal.setup.datagen.LangProvider;
import rpg.project.lib.internal.util.MsLoggy;
import rpg.project.lib.internal.util.RegistryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        List<Positioner.Widget> explanation = this.minecraft.font.getSplitter()
                .splitLines(LangProvider.LOADING_EXPLANATION.asComponent().getString(), this.width/2, Style.EMPTY)
                .stream().map(fcs -> new Positioner.Widget(
                        new StringWidget(Component.literal(fcs.getString()), this.minecraft.font),
                        PositionType.STATIC.constraint,
                        SizeConstraints.builder().absoluteHeight(12).build()))
                .toList();

        explanation.forEach(this::addChild);
        RegistryAccess access = Minecraft.getInstance().player.registryAccess();
        CompletableFuture<List<Panel>> items = async(() ->
            CreativeModeTabs.searchTab().getDisplayItems().stream().map(stack -> {
                Identifier id = RegistryUtil.getId(access, stack);
                MainSystemConfig config = core.getLoader().getLoader(ObjectType.ITEM).getData(id);
                ItemHeader header = new ItemHeader(stack);
                return new Panel(new PanelWidget(0x882e332e, width, config, header));
            }).toList(), executor);

        CompletableFuture<List<Panel>> blocks = async(() ->
                access.lookupOrThrow(Registries.BLOCK).listElements().map(ref -> {
                    Identifier id = ref.key().identifier();
                    MainSystemConfig config = core.getLoader().getLoader(ObjectType.BLOCK).getData(id);
                    BlockHeader header = new BlockHeader(ref.value());
                    return new Panel(new PanelWidget(0x882e2f33, width, config, header));
                }).toList(), executor);

        CompletableFuture<List<Panel>> entities = async(() ->
                access.lookupOrThrow(Registries.ENTITY_TYPE).listElements()
                        .map(ref -> ref.value().create(Minecraft.getInstance().level, EntitySpawnReason.COMMAND))
                        .filter(Objects::nonNull)
                        .map(entity -> {
                            Identifier id = RegistryUtil.getId(access, entity);
                            MainSystemConfig config = core.getLoader().getLoader(ObjectType.ENTITY).getData(id);
                            EntityHeader header = new EntityHeader(entity.getType());
                            return new Panel(new PanelWidget(0x88394045, width, config, header));}
                        ).toList(), executor);

        CompletableFuture<List<Panel>> biomes = async(() ->
                access.lookupOrThrow(Registries.BIOME).listElements()
                        .filter(Objects::nonNull)
                        .map(biome -> {
                            MainSystemConfig config = core.getLoader().getLoader(ObjectType.BIOME).getData(biome.unwrapKey().get().identifier());
                            BiomeHeader header = new BiomeHeader(biome);
                            return new Panel(new PanelWidget(0x88394045, width, config, header));
                        }).toList(), executor);

        CompletableFuture<List<Panel>> dimensions = async(() ->
                Minecraft.getInstance().getConnection().levels().stream()
                        .filter(Objects::nonNull)
                        .map(key -> {
                            MainSystemConfig config = core.getLoader().getLoader(ObjectType.DIMENSION).getData(key.identifier());
                            DimensionHeader header = new DimensionHeader(key);
                            return new Panel(new PanelWidget(0x88394045, width, config, header));
                        }).toList(), executor);
//
        CompletableFuture<List<Panel>> effects = async(() ->
                access.lookupOrThrow(Registries.MOB_EFFECT).listElements()
                        .filter(Objects::nonNull)
                        .map(holder -> {
                            MainSystemConfig config = core.getLoader().getLoader(ObjectType.EFFECT).getData(holder.unwrapKey().get().identifier());
                            EffectHeader header = new EffectHeader(holder);
                            return new Panel(new PanelWidget(0x88394045, width, config, header));
                        }).toList(), executor);

        CompletableFuture<List<Panel>> enchantments = async(() ->
                access.lookupOrThrow(Registries.ENCHANTMENT).listElements()
                        .filter(Objects::nonNull)
                        .map(enchant -> {
                            MainSystemConfig config = core.getLoader().getLoader(ObjectType.ENCHANTMENT).getData(enchant.unwrapKey().get().identifier());
                            EnchantmentHeader header = new EnchantmentHeader(enchant.value());
                            return new Panel(new PanelWidget(0x88394045, width, config, header));
                        }).toList(), executor);

        CompletableFuture<List<Panel>> events = async(() ->
                access.lookupOrThrow(APIUtils.GAMEPLAY_EVENTS).listElements()
                        .filter(Objects::nonNull)
                        .map(event -> {
                            MainSystemConfig config = core.getLoader().getLoader(ObjectType.EVENT).getData(event.unwrapKey().get().identifier());
                            EventHeader header = new EventHeader(event.unwrapKey().get().identifier());
                            return new Panel(new PanelWidget(0x88394045, width, config, header));
                        }).toList(), executor);

        CompletableFuture.allOf(
                items,
                blocks,
                entities,
                biomes,
                dimensions,
                effects,
                enchantments,
                events
        ).thenRun(() -> {
            try {
                allItems.addAll(items.join());
                allItems.addAll(blocks.join());
                allItems.addAll(entities.join());
                allItems.addAll(biomes.join());
                allItems.addAll(dimensions.join());
                allItems.addAll(effects.join());
                allItems.addAll(enchantments.join());
                allItems.addAll(events.join());
                explanation.forEach(poser -> poser.get().visible = false);
                this.replaceEntries(allItems);
            } catch (Exception e) {
                MsLoggy.ERROR.log(MsLoggy.LOG_CODE.GUI, e.getLocalizedMessage());
            }
        });
    }
}
