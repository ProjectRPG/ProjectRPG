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
        List<String> allKeys = new ArrayList<>(tag1.keySet());
        
        for (String key : tag2.keySet()) {
            if (!allKeys.contains(key) && key != null) {
                allKeys.add(key);
            }
        }
        
        for (String key : allKeys) {
            if (tag1.contains(key) && tag2.contains(key)) {
                if (tag1.get(key) instanceof NumericTag) {
                    if (tag1.get(key) instanceof DoubleTag) {
                        output.putDouble(key, tag1.getDoubleOr(key, 0d) + tag2.getDoubleOr(key, 0d));
                    } else if (tag1.get(key) instanceof FloatTag) {
                        output.putFloat(key, tag1.getFloatOr(key, 0f) + tag2.getFloatOr(key, 0f));
                    } else if (tag1.get(key) instanceof IntTag) {
                        output.putInt(key, tag1.getIntOr(key, 0) + tag2.getIntOr(key, 0));
                    } else if (tag1.get(key) instanceof LongTag) {
                        output.putLong(key, tag1.getLongOr(key, 0L) + tag2.getLongOr(key, 0L));
                    } else if (tag1.get(key) instanceof ShortTag) {
                        output.putShort(key, (short) (tag1.getShortOr(key, (short)0) + tag2.getShortOr(key, (short)0)));
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
