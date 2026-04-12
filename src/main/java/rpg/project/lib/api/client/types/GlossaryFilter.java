package rpg.project.lib.api.client.types;

import net.minecraft.resources.Identifier;
import rpg.project.lib.api.data.ObjectType;

@FunctionalInterface
public interface GlossaryFilter {
    /**Widgets that implement this class receive a record of filter
     * values to operate over as it makes sense for their content.
     * If the result of the implementing class's filter operation
     * results in the entire object being filtered, this method will
     * return true to let parent classes know the child is filtered.
     *
     * @param filter a record of filterable values
     * @return true if this object displays no content after the filter is applied.
     */
    boolean applyFilter(Filter filter);

    static class Filter {
        private String textFilter = "";
        private ObjectType objectType = null;
        private SystemOptions selection = null;
        private Identifier systemID = null;

        public Filter(String textFilter) {this.textFilter = textFilter;}

        public Filter with(String searchTerm) {this.textFilter = searchTerm; return this;}
        public Filter with(SystemOptions selection) {this.selection = selection; return this;}
        public Filter with(ObjectType objectType) {this.objectType = objectType; return this;}
        public Filter with(Identifier systemID) {this.systemID = systemID; return this;}

        public String getTextFilter() {return textFilter;}
        public SystemOptions getSelection() {return selection;}
        public ObjectType getObjectType() {return objectType; }
        public Identifier getSystemID() {return systemID;}

        public boolean matchesTextFilter(String str) {
            return textFilter.isEmpty() || str.contains(textFilter);
        }
        public boolean matchesSystemID(Identifier id) {return !id.equals(SystemOptions.BLANK) && id.equals(this.systemID);}

        public boolean matchesObject(ObjectType obj) {
            return objectType == null || objectType == obj;
        }

        public boolean matchesSelection(SystemOptions sel) {
            return selection == null || selection == SystemOptions.NONE || selection == sel;
        }
    }
}
