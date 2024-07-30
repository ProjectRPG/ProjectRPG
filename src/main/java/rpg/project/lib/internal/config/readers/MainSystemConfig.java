package rpg.project.lib.internal.config.readers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import rpg.project.lib.api.data.MergeableData;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.api.gating.GateUtils.Type;
import rpg.project.lib.internal.registry.SubSystemCodecRegistry;
import rpg.project.lib.internal.util.Functions;

public record MainSystemConfig(
		boolean override, 
		List<String> tagValues,
		Map<Type, List<SubSystemConfig>> gates,
		List<SubSystemConfig> progression,
		List<SubSystemConfig> abilities,
		List<SubSystemConfig> features
		) implements MergeableData{
	
	public MainSystemConfig() {this(false, List.of(), Map.of(), List.of(), List.of(), List.of());}
	
	public static final Codec<MainSystemConfig> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.optionalFieldOf("replace").forGetter(msc -> Optional.of(msc.override())),
			Codec.STRING.listOf().optionalFieldOf("copyTo").forGetter(msc -> Optional.of(msc.tagValues())),
			Codec.optionalField("gates",  Codec.simpleMap(
					Type.CODEC,
					Codec.list(SubSystemCodecRegistry.CODEC.dispatch("type", SubSystemConfig::getType, SubSystemConfigType::getCodec)),
					StringRepresentable.keys(Type.values())).codec()
				, true)
				.forGetter(msc -> Optional.of(msc.gates())),
			Codec.list(SubSystemCodecRegistry.CODEC.dispatch("type", SubSystemConfig::getType, SubSystemConfigType::getCodec))
				.optionalFieldOf("progression")
				.forGetter(msc -> Optional.of(msc.progression())),
			Codec.list(SubSystemCodecRegistry.CODEC.dispatch("type", SubSystemConfig::getType, SubSystemConfigType::getCodec))
				.optionalFieldOf("abilities")
				.forGetter(msc -> Optional.of(msc.abilities())),
			Codec.list(SubSystemCodecRegistry.CODEC.dispatch("type", SubSystemConfig::getType, SubSystemConfigType::getCodec))
				.optionalFieldOf("features")
				.forGetter(msc -> Optional.of(msc.features()))
			).apply(instance, (o, t, g, p, a, f) -> new MainSystemConfig(
					o.orElse(false),
					new ArrayList<>(t.orElse(List.of())),
					g.orElse(Map.of()),
					p.orElse(List.of()),
					a.orElse(List.of()),
					f.orElse(List.of())
					)))
			);
	public static final StreamCodec<FriendlyByteBuf, MainSystemConfig> STREAM_CODEC = StreamCodec.of(
			(buf, obj) -> buf.writeNbt(CODEC.encodeStart(NbtOps.INSTANCE, obj).result().orElse(new CompoundTag())),
			pBuffer -> CODEC.parse(NbtOps.INSTANCE, pBuffer.readNbt(NbtAccounter.unlimitedHeap())).result().orElse(new MainSystemConfig()));

	@Override
	public MergeableData combine(MergeableData two) {
		if (!(two instanceof MainSystemConfig second))
			return this;
		List<String> tagValues = new ArrayList<>();
		Map<Type, List<SubSystemConfig>> gates = new HashMap<>();
		List<SubSystemConfig> progression = new ArrayList<>();
		List<SubSystemConfig> abilities = new ArrayList<>();
		List<SubSystemConfig> features = new ArrayList<>();
		
		BiConsumer<MainSystemConfig, MainSystemConfig> bothOrNeither = (o, t) -> {
			tagValues.addAll(o.tagValues());
			t.tagValues.forEach((rl) -> {
				if (!tagValues.contains(rl))
					tagValues.add(rl);
			});	
			
			gates.putAll(o.gates());
			t.gates().forEach((type, configList) -> {
				gates.merge(type, configList, (oldList, newList) -> {
					List<SubSystemConfig> combinedList = new ArrayList<>(oldList);
					newList.forEach(config -> {
						for (SubSystemConfig altConfig : configList) {
							if (altConfig.getType().equals(config.getType())) {
								altConfig = (SubSystemConfig) altConfig.combine(config);
								return;
							}
						}
						combinedList.add(config);
					});
					return combinedList;
				});
			});
			
			progression.addAll(o.progression());
			t.progression().forEach(config -> {
				for (SubSystemConfig altConfig : progression) {
					if (altConfig.getType().equals(config.getType())) {
						altConfig = (SubSystemConfig) altConfig.combine(config);
						return;
					}
				}
				progression.add(config);
			});
			
			abilities.addAll(o.abilities());
			t.abilities().forEach(config -> {
				for (SubSystemConfig altConfig : abilities) {
					if (altConfig.getType().equals(config.getType())) {
						altConfig = (SubSystemConfig) altConfig.combine(config);
						return;
					}
				}
				abilities.add(config);
			});
			
			features.addAll(o.features());
			t.features().forEach(config -> {
				for (SubSystemConfig altConfig : features) {
					if (altConfig.getType().equals(config.getType())) {
						altConfig = (SubSystemConfig) altConfig.combine(config);
						return;
					}
				}
				features.add(config);
			});
		};
		
		Functions.biPermutation(this, second, this.override(), second.override(), (o, t) -> {
			tagValues.addAll(o.tagValues().isEmpty() ? t.tagValues() : o.tagValues());
			gates.putAll(o.gates().isEmpty() ? t.gates() : o.gates());
			progression.addAll(o.progression().isEmpty() ? t.progression() : o.progression());
			abilities.addAll(o.abilities().isEmpty() ? t.abilities() : o.abilities());
			features.addAll(o.features().isEmpty() ? t.features() : o.features());
		}, 
		bothOrNeither, 
		bothOrNeither);
		
		return new MainSystemConfig(this.override() || second.override(), tagValues, gates, progression, abilities, features);
	}

	@Override
	public boolean isUnconfigured() {
		return false;
	}
}
