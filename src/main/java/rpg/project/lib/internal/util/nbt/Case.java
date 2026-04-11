package rpg.project.lib.internal.util.nbt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

public record Case(List<String> paths, List<Criteria> criteria) {
    public static final Codec<Case> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(Codec.STRING).fieldOf("paths").forGetter(Case::paths),
            Codec.list(Criteria.CODEC).fieldOf("criteria").forGetter(Case::criteria)
    ).apply(instance, Case::new));

    public static record Criteria(Operator operator, Optional<List<String>> comparators) {
        public static final Codec<Criteria> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Operator.CODEC.fieldOf("operator").forGetter(Criteria::operator),
                Codec.list(Codec.STRING).optionalFieldOf("comparators").forGetter(Criteria::comparators)
        ).apply(instance, Criteria::new));
    }
}
