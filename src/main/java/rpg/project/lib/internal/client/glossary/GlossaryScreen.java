package rpg.project.lib.internal.client.glossary;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;

public class GlossaryScreen extends Screen {
    private DropDownComponent categoryFilterBox;
    private DropDownComponent eventFilterBox;
    private EditBox searchBox;
    //TODO scroll panel for objects

    private DropDownComponent.SelectionEntry<CategoryType> categorySelection;
    private DropDownComponent.SelectionEntry<ResourceLocation> eventFilter;

    private static enum CategoryType {PROGRESSION, GATING, FEATURE, ABILITY}

    public GlossaryScreen() {super(Component.literal("projectrpg_glossary"));}

    @Override
    protected void init() {
        super.init();
        int filterSectionWidth = this.width/5;

        categoryFilterBox = new DropDownComponent<DropDownComponent.SelectionEntry<CategoryType>>(10, 22, filterSectionWidth, Component.literal("category_filter_box"), selection -> {
            categorySelection = selection;
        });
        //TODO add translations for the entry strings
        categoryFilterBox.setEntries(Arrays.stream(CategoryType.values()).map(value -> new DropDownComponent.SelectionEntry<CategoryType>(Component.literal(value.name()), value)).toList());
        searchBox = new EditBox(this.font, categoryFilterBox.getX(), categoryFilterBox.getY() + 32, filterSectionWidth, 20, Component.translatable("gui.recipebook.search_hint"));

        this.addRenderableWidget(categoryFilterBox);
        this.addRenderableWidget(searchBox);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        /* TODO render the following
         * - Box around the filter section
         * - Text above the search box
         * - Text above the Category Filter
         * - Text above the Event Filter
         */
    }
}
