package rpg.project.lib.internal.client.glossary;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import rpg.project.lib.api.client.ResponsiveLayout;
import rpg.project.lib.api.client.components.CollapsingPanel;
import rpg.project.lib.api.client.types.DisplayType;
import rpg.project.lib.api.client.types.GlossaryFilter.Filter;
import rpg.project.lib.api.client.types.PositionType;
import rpg.project.lib.api.client.types.SystemOptions;
import rpg.project.lib.api.client.wrappers.SizeConstraints;
import rpg.project.lib.api.data.ObjectType;

public class Glossary extends Screen {
    public Glossary() {
        super(Component.literal("PRPG Glossary"));
    }

    private EditBox searchBar;
    private DropDownComponent<DropDownComponent.SelectionEntry<SystemOptions>> systemFilterBox;
    private DropDownComponent<DropDownComponent.SelectionEntry<ObjectType>> categoryFilterBox;
    private DropDownComponent<DropDownComponent.SelectionEntry<Identifier>> namespaceFilterBox;
    private Filter filter = new Filter("");

    @Override
    protected void init() {
        super.init();
        ResponsiveLayout screenLayout = new ResponsiveLayout.Impl(this.width, this.height, DisplayType.INLINE);
        ContentScroll contentPanel = new ContentScroll();

        int filterSectionWidth = this.width/3 - 16;
        searchBar = new EditBox(font, 8, 11, filterSectionWidth, 20, Component.literal("search bar"));
        searchBar.setResponder(str -> {contentPanel.applyFilter(filter.with(str));});

        systemFilterBox = new DropDownComponent<DropDownComponent.SelectionEntry<SystemOptions>>(9, 31, filterSectionWidth, Component.literal("system_option_box"), selection -> {
           contentPanel.applyFilter(filter.with(selection.reference).with(SystemOptions.BLANK));
           namespaceFilterBox.setEntries(selection.reference.getEntries(Minecraft.getInstance().player.registryAccess()));
        });
        systemFilterBox.setEntries(SystemOptions.CHOICE_LIST);
        categoryFilterBox = new DropDownComponent<DropDownComponent.SelectionEntry<ObjectType>>(10, 22, filterSectionWidth, Component.literal("category_filter_box"), selection -> {
            contentPanel.applyFilter(filter.with(selection.reference));
        });
        categoryFilterBox.setEntries(ObjectType.CHOICE_LIST);
        namespaceFilterBox = new DropDownComponent<DropDownComponent.SelectionEntry<Identifier>>(categoryFilterBox.getX(), categoryFilterBox.getY() + 42, filterSectionWidth, Component.literal("event_filter_box"), selection -> {
            contentPanel.applyFilter(filter.with(selection.reference));
        });

        ResponsiveLayout filterPanel = new CollapsingPanel(0, 0, this.width/3, this.height, true)
                .addCallback(_ -> {
                    screenLayout.arrangeElements();
                    contentPanel.repositionEntries();
                })
                .addChild(searchBar, PositionType.STATIC.constraint, SizeConstraints.builder().absoluteHeight(20).build())
                .addChild(systemFilterBox, PositionType.STATIC.constraint, SizeConstraints.builder().absoluteHeight(20).build())
                .addChild(categoryFilterBox, PositionType.STATIC.constraint, SizeConstraints.builder().absoluteHeight(20).build())
                .addChild(namespaceFilterBox, PositionType.STATIC.constraint, SizeConstraints.builder().absoluteHeight(20).build());

        screenLayout.addChild(filterPanel,
                PositionType.STATIC.constraint,
                SizeConstraints.builder().internalWidth().build());
        screenLayout.addChild((ResponsiveLayout) contentPanel,
                PositionType.STATIC.constraint,
                SizeConstraints.DEFAULT);
        screenLayout.arrangeElements();
        screenLayout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T widget) {
        //this exists to make sure ContentScroll isn't registered twice because it's both a layout and a widget.
        if (this.renderables.contains(widget)) return widget;
        return super.addRenderableWidget(widget);
    }
}
