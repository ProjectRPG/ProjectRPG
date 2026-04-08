package rpg.project.lib.api.client.types;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.gating.GateUtils;
import rpg.project.lib.api.progression.ProgressionUtils;
import rpg.project.lib.internal.client.glossary.DropDownComponent;
import rpg.project.lib.internal.setup.datagen.LangProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public enum SystemOptions {
    NONE(LangProvider.NONE_DROPDOWN.asComponent(), (reg) -> wrap(List.of())),
    ABILITY_GATE(LangProvider.GATE_TYPE_ABILITY.asComponent(), reg -> wrap(reg.lookupOrThrow(GateUtils.GATES_ABILITIES).keySet())),
    FEATURE_GATE(LangProvider.GATE_TYPE_FEATURE.asComponent(), reg -> wrap(reg.lookupOrThrow(GateUtils.GATES_FEATURES).keySet())),
    PROGRESSION_GATE(LangProvider.GATE_TYPE_PROGRESSION.asComponent(), reg -> wrap(reg.lookupOrThrow(GateUtils.GATES_PROGRESS).keySet())),
    EVENT_GATE(LangProvider.GATE_TYPE_EVENT.asComponent(), reg -> wrap(reg.lookupOrThrow(GateUtils.GATES_EVENTS).keySet())),
    ABILITY(LangProvider.ABILITY_DROPDOWN.asComponent(), reg -> wrap(reg.lookupOrThrow(APIUtils.ABILITY).keySet())),
    FEATURE(LangProvider.FEATURE_DROPDOWN.asComponent(), reg -> wrap(reg.lookupOrThrow(APIUtils.FEATURE).keySet())),
    PROGRESSION(LangProvider.PROGRESSION_DROPDOWN.asComponent(), reg -> wrap(reg.lookupOrThrow(ProgressionUtils.PROGRESSION_ADDON).keySet()));

    public final MutableComponent text;
    public final Function<RegistryAccess, Collection<Identifier>> entries;
    SystemOptions(MutableComponent text, Function<RegistryAccess, Collection<Identifier>> entries) {
        this.text = text;
        this.entries = entries;
    }

    public List<DropDownComponent.SelectionEntry<Identifier>> getEntries(RegistryAccess access) {
        return this.entries.apply(access).stream().map(val -> new DropDownComponent.SelectionEntry<>(Component.literal(val.toString()), val)).toList();
    }

    public static final Identifier BLANK = Identifier.fromNamespaceAndPath("make","choice");
    private static List<Identifier> wrap(Collection<Identifier> values) {
        List<Identifier> output = new ArrayList<>();
        output.add(BLANK);
        output.addAll(values);
        return output;
    }

    public static final List<DropDownComponent.SelectionEntry<SystemOptions>> CHOICE_LIST = Arrays.stream(SystemOptions.values())
        .map(val -> new DropDownComponent.SelectionEntry<>(val.text, val))
        .toList();
}
