package rpg.project.lib.internal.util;

import net.minecraft.nbt.*;

import java.util.ArrayList;
import java.util.List;

public class TagUtils {
    /**
     * An enhanced version of {@link net.minecraft.nbt.CompoundTag#merge(CompoundTag) CompoundTag.merge}
     * which is adds values for shared keys instead of complete replacement
     *
     * @param tag1 a CompoundTag instance
     * @param tag2 a different CompoundTag instance
     * @return a merged tag
     */
    public static CompoundTag mergeTags(CompoundTag tag1, CompoundTag tag2) {
        CompoundTag output = new CompoundTag();
        List<String> allKeys = new ArrayList<>(tag1.getAllKeys());
        
        for (String key : tag2.getAllKeys()) {
            if (!allKeys.contains(key) && key != null) {
                allKeys.add(key);
            }
        }
        
        for (String key : allKeys) {
            if (tag1.contains(key) && tag2.contains(key)) {
                if (tag1.get(key) instanceof NumericTag) {
                    if (tag1.get(key) instanceof DoubleTag) {
                        output.putDouble(key, tag1.getDouble(key) + tag2.getDouble(key));
                    } else if (tag1.get(key) instanceof FloatTag) {
                        output.putFloat(key, tag1.getFloat(key) + tag2.getFloat(key));
                    } else if (tag1.get(key) instanceof IntTag) {
                        output.putInt(key, tag1.getInt(key) + tag2.getInt(key));
                    } else if (tag1.get(key) instanceof LongTag) {
                        output.putLong(key, tag1.getLong(key) + tag2.getLong(key));
                    } else if (tag1.get(key) instanceof ShortTag) {
                        output.putShort(key, (short) (tag1.getShort(key) + tag2.getShort(key)));
                    } else {
                        output.put(key, tag1.get(key));
                    }
                } else {
                    output.put(key, tag1.get(key));
                }
            } else if (tag1.contains(key) && !tag2.contains(key)) {
                output.put(key, tag1.get(key));
            } else if (!tag1.contains(key) && tag2.contains(key)) {
                output.put(key, tag2.get(key));
            }
        }
        
        return output;
    }
}
