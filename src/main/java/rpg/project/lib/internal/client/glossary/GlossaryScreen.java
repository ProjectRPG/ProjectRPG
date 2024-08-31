package rpg.project.lib.internal.client.glossary;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.LogicalSide;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.internal.Core;

import java.util.Arrays;

public class GlossaryScreen extends Screen {
    public static final ResourceLocation INWORLD_MENU_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/inworld_menu_background.png");

    private DropDownComponent categoryFilterBox;
    private DropDownComponent eventFilterBox;
    private EditBox searchBox;
    private ObjectScroll objectScroll;

    private DropDownComponent.SelectionEntry<ObjectType> categorySelection;
    private DropDownComponent.SelectionEntry<ResourceLocation> eventFilter;
    private String filterString;

    public GlossaryScreen() {super(Component.literal("projectrpg_glossary"));}

    @Override
    protected void init() {
        super.init();
        int filterSectionWidth = this.width/5;

        categoryFilterBox = new DropDownComponent<DropDownComponent.SelectionEntry<ObjectType>>(10, 22, filterSectionWidth, Component.literal("category_filter_box"), selection -> {
            categorySelection = selection;
            filter();
        });
        eventFilterBox = new DropDownComponent<DropDownComponent.SelectionEntry<ResourceLocation>>(categoryFilterBox.getX(), categoryFilterBox.getY() + 42, filterSectionWidth, Component.literal("event_filter_box"), selection -> {
           eventFilter = selection;
           filter();
        });
        //TODO add translations for the entry strings
        categoryFilterBox.setEntries(Arrays.stream(ObjectType.values())
                .map(value -> new DropDownComponent.SelectionEntry<ObjectType>(Component.literal(value.name()), value))
                .toList());
        eventFilterBox.setEntries(Minecraft.getInstance().level.registryAccess().registryOrThrow(APIUtils.GAMEPLAY_EVENTS).keySet().stream()
                .map(rl -> new DropDownComponent.SelectionEntry<>(Component.literal(rl.toString()), rl))
                .toList());
        searchBox = new EditBox(this.font, eventFilterBox.getX(), eventFilterBox.getY() + 42, filterSectionWidth, 20, Component.translatable("gui.recipebook.search_hint"));
        searchBox.setResponder(str -> {
            filterString = str;
            filter();
        });
        objectScroll = new ObjectScroll(filterSectionWidth, this.height - searchBox.getY() - 30, searchBox.getY() + 20, searchBox.getX());

        this.addRenderableWidget(categoryFilterBox);
        this.addRenderableWidget(eventFilterBox);
        this.addRenderableWidget(searchBox);
        this.addRenderableWidget(objectScroll);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.drawString(this.font, Component.literal("Category Filter"), categoryFilterBox.getX(), categoryFilterBox.getY() - 12, 0xFFFFFF);
        pGuiGraphics.drawString(this.font, Component.literal("Event Filter"), eventFilterBox.getX(), eventFilterBox.getY() - 12, 0xFFFFFF);
        pGuiGraphics.drawString(this.font, Component.literal("Item Search"), searchBox.getX(), searchBox.getY() - 12, 0xFFFFFF);
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderMenuBackgroundTexture(pGuiGraphics, INWORLD_MENU_BACKGROUND, 5, 5, 0.0F, 0.0F, (this.width/5) + 10, this.height-10);
    }

    /**Passes through the current filter values to the object list box to update its filtering*/
    private void filter() {
        objectScroll.filter(
                categorySelection == null ? null : categorySelection.reference,
                eventFilter == null ? null : eventFilter.reference,
                filterString);
    }
}
