package rpg.project.lib.api.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.Component;
import rpg.project.lib.api.client.types.DisplayType;
import rpg.project.lib.api.client.types.PositionType;
import rpg.project.lib.api.client.wrappers.BoxDimensions;
import rpg.project.lib.api.client.wrappers.PositionConstraints;
import rpg.project.lib.api.client.wrappers.Positioner;
import rpg.project.lib.api.client.wrappers.SizeConstraints;
import rpg.project.lib.internal.util.MsLoggy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**This is part of Project RPG's proprietary responsive GUI layer.  Implemented on top
 * of vanilla's widget system, so as to be compatible with screens, this {@link Layout}
 * provides a base for GUI elements which can self-adjust their size and position to fit
 * the available space according to their specified constraints without the need for
 * individual widgets to implement their own callbacks or resizing logic.
 *
 */
public interface ResponsiveLayout extends Layout {
    class Impl implements ResponsiveLayout {
        //Determines how children are arranged in the layout
        private DisplayType displayType;
        //Internal dimensions used for container positioning and rendering
        private int x, y, width, height;
        //Dimensions for external (margin) and internal (padding) buffering of space
        private BoxDimensions margin, padding = null;
        //All widgets and layouts arranged by this layout
        private final List<Positioner<?>> children = new ArrayList<>();

        public Impl() {
            this(10, 10, DisplayType.BLOCK);
        }
        public Impl(int width, int height, DisplayType displayType) {
            this.x = 0;
            this.y = 0;
            this.width = width;
            this.height = height;
            this.displayType = displayType;
            this.setMargin(0, 0, 0, 0);
            this.setPadding(0, 0, 0, 0);
        }

        @Override
        public List<Positioner<?>> visibleChildren() {
            return children.stream().filter(poser -> poser.get() instanceof AbstractWidget widget && widget.visible).toList();
        }

        @Override
        public void addChild(Positioner<?> child) {children.add(child);}

        @Override
        public ResponsiveLayout setMargin(int left, int top, int right, int bottom) {
            margin = new BoxDimensions(left, top, right, bottom);
            return this;
        }
        @Override
        public ResponsiveLayout setPadding(int left, int top, int right, int bottom) {
            padding = new BoxDimensions(left, top, right, bottom);
            return this;
        }
        @Override public void setX(int x) {this.x = x + margin.left();}
        @Override public void setY(int y) {this.y = y + margin.top();}
        @Override public int getX() {return x;}
        @Override public int getY() {return y;}
        @Override public int getWidth() {return width;}
        @Override public int getHeight() {return height;}
        @Override public void setWidth(int width) {this.width = width;}
        @Override public void setHeight(int height) {this.height = height;}
        @Override public int getRight() {return this.x + this.width;}
        @Override public int getBottom() {return this.y + this.height;}
        @Override public BoxDimensions getMargin() {return margin;}
        @Override public BoxDimensions getPadding() {return padding;}
        @Override public DisplayType getDisplayType() {return displayType;}
        @Override public List<Positioner<?>> getChildren() {return children;}

    }

    DisplayType getDisplayType();

    BoxDimensions getMargin();
    BoxDimensions getPadding();
    ResponsiveLayout setMargin(int left, int top, int right, int bottom);
    ResponsiveLayout setPadding(int left, int top, int right, int bottom);

    void setWidth(int width);
    void setHeight(int height);
    int getRight();
    int getBottom();

    List<Positioner<?>> getChildren();
    void addChild(Positioner<?> child);
    List<Positioner<?>> visibleChildren();

    default ResponsiveLayout addChild(AbstractWidget widget, PositionConstraints type, SizeConstraints constraints) {
        addChild(new Positioner.Widget(widget, type ,constraints));
        return this;
    }
    default ResponsiveLayout addChild(ResponsiveLayout layout, PositionConstraints type, SizeConstraints constraints) {
        addChild(new Positioner.Layout(layout, type, constraints));
        return this;
    }

    default ResponsiveLayout addString(Component text, PositionConstraints type, SizeConstraints constraints) {
        return addChild(new StringWidget(this.getWidth(), 12, text, Minecraft.getInstance().font), type, constraints);
    }
    
    default int getInternalWidth() {return getWidth();}
    default int getInternalHeight() {return getHeight();}

    @Override
    default void arrangeElements() {
        switch (getDisplayType()) {
            case BLOCK -> block();
            case INLINE -> inline();
            case FLEX -> flex();
            case GRID -> grid();
        }
    }

    /**Arranges elements such that each element occupies its own row*/
    private void block() {
        int currentRowY = this.getY() + getPadding().top();
        for (Positioner<?> child : visibleChildren()) {
            if (child.positioning().type() == PositionType.ABSOLUTE) {
                child.constraints().apply(child, this.getRight() - child.get().getX(), this.getBottom() - child.get().getY());
                continue;
            }
            child.get().setPosition(this.getX() + getPadding().left() + child.positioning().xOffset(), currentRowY + child.positioning().yOffset());
            child.constraints().apply(child,
                    this.getInternalWidth() - getPadding().left() - getPadding().right() - child.positioning().xOffset(),
                    this.getInternalHeight() - getPadding().bottom() - getPadding().top() - child.positioning().yOffset());
            currentRowY += child.get().getHeight() + child.positioning().yOffset();
        }
    }

    /**<p>Arranges elements such that each element occupies the same row
     * except for when an element would exceed the layout's width. In
     * that case the element moves to the next row.</p>
     * <p>If the width of the element is greater than the entire width of
     * the layout, it will be automatically moved to its own line and
     * rendered.</p>
     */
    private void inline() {
        int currentRowY = this.getY() + getPadding().top();
        int currentRowX = this.getX() + getPadding().left();
        int lastRowMaxHeight = 0;

        for (Positioner<?> child : visibleChildren()) {
            if (child.positioning().type() == PositionType.ABSOLUTE) {
                child.constraints().apply(child, this.getRight() - child.get().getX(), this.getBottom() - child.get().getY());
                continue;
            }
            LayoutElement element = child.get();
            child.constraints().apply(child,
                    this.getRight() - getPadding().right() - currentRowX - child.positioning().xOffset(),
                    this.getBottom() - getPadding().bottom() -currentRowY - child.positioning().yOffset());
            element.setPosition(currentRowX + child.positioning().xOffset(), currentRowY + child.positioning().yOffset());
            currentRowX += inWidth(element) + child.positioning().xOffset();
            lastRowMaxHeight = Math.max(lastRowMaxHeight, inHeight(element) + child.positioning().yOffset());
            //if the updated constraints fall at or outside the boundary dimensions, update the next widget position indices
            if (currentRowX + getPadding().right() >= this.getX() + this.getInternalWidth()) {
                currentRowY += lastRowMaxHeight;
                lastRowMaxHeight = 0;
                currentRowX = this.getX() + getPadding().left();
            }
        }
    }

    private void flex() {
        //maybe implement flex arrangement. TBD
    }

    /**Note that sizing is not recursively self-adaptive (for performance) and special attention is needed for configuring
     * grids so as not to overflow. (e.g. don't put grid dimensions in the thousands if you don't have thousands of elements)
     */
    private void grid() {
        int maxRowCount = getChildren().stream().map(poser -> poser.positioning().row()).reduce(Integer::max).orElse(0) +1; //+1 necessary to translate zero index to array size count
        int maxColCount = getChildren().stream().map(poser -> poser.positioning().col()).reduce(Integer::max).orElse(0) +1;

        final Positioner<?>[][] cells = new Positioner<?>[maxRowCount][maxColCount];
        //Map posers to cells so that we can iterate on them in order
        getChildren().forEach(poser -> {
            int row = poser.positioning().row();
            int col = poser.positioning().col();
            if (cells[row][col] != null) {
                MsLoggy.ERROR.log(MsLoggy.LOG_CODE.GUI, "Duplicate GUI element coded in row {}, column {}, for {}, defined as {}", row, col, this, poser.get().getClass());
                return;
            }
            cells[row][col] = poser;
        });

        final int[] maxRowHeights = new int[maxRowCount];
        final int[] maxColWidths = new int[maxColCount];

        //Run each poser through size constraints assuming full availability and store the
        // dimensions in the respective row/col matrix
        for (int row = 0; row < maxRowCount; row++) {
            for (int col = 0; col < maxColCount; col++) {
                Positioner<?> poser = cells[row][col];
                if (poser == null) continue;
                poser.constraints().apply(poser, this.getInternalWidth(), this.getInternalHeight());
                maxRowHeights[row] = Math.max(maxRowHeights[row], inHeight(poser.get()));
                maxColWidths[col] = Math.max(maxColWidths[col], inWidth(poser.get()));
            }
        }

        //Run each poser through positioning using the now available row/col dimensions as coordinates
        int currentRowY = this.getY();
        for (int row = 0; row < maxRowCount; row++) {
            int currentColX = this.getX();
            for (int col = 0; col < maxColCount; col++) {
                Positioner<?> poser = cells[row][col];
                if (poser != null)
                    poser.get().setPosition(currentColX, currentRowY);
                currentColX += maxColWidths[col];
            }
            currentRowY += maxRowHeights[row];
        }
    }

    @Override
    default void visitChildren(Consumer<LayoutElement> visitor) {
        visitor.accept(this);
        getChildren().forEach(poser -> visitor.accept(poser.get()));
    }

    @Override
    default void visitWidgets(Consumer<AbstractWidget> consumer) {
        getChildren().forEach(poser -> {
            if (poser.get() instanceof AbstractWidget widget)
                consumer.accept(widget);
            if (poser.get() instanceof ResponsiveLayout layout)
                layout.visitWidgets(consumer);
        });
    }

    private int inWidth(LayoutElement element) {return element instanceof ResponsiveLayout layout ? layout.getInternalWidth() : element.getWidth();}
    private int inHeight(LayoutElement element) {return element instanceof ResponsiveLayout layout ? layout.getInternalHeight() : element .getHeight();}
}
