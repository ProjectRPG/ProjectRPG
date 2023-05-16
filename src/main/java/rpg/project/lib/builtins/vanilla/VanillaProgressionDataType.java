package rpg.project.lib.builtins.vanilla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.data.MergeableData;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.api.progression.ProgressionDataType;

public record VanillaProgressionDataType() implements SubSystemConfigType{
	public static final ResourceLocation ID = new ResourceLocation("exp");
	public static final VanillaProgressionDataType IMPL = new VanillaProgressionDataType();

	@Override
	public Codec<SubSystemConfig> getCodec() {
		return VanillaProgressionData.CODEC;
	}

	public static record VanillaProgressionData(int exp) implements ProgressionDataType {
		public static final Codec<SubSystemConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.INT.fieldOf("value").forGetter(ssc -> ((VanillaProgressionData)ssc).exp())
				).apply(instance, VanillaProgressionData::new));

		@Override
		public MergeableData combine(MergeableData two) {
			int greater = Math.max(exp(), ((VanillaProgressionData)two).exp());
			return new VanillaProgressionData(greater);
		}

		@Override
		public boolean isUnconfigured() {
			return this.exp() == 0;
		}

		@Override
		public SubSystemConfigType getType() {
			return IMPL;
		}

		@Override
		public Codec<SubSystemConfig> getCodec() {
			return CODEC;
		}

		@Override
		public SubSystemConfig getDefault() {
			return new VanillaProgressionData(0);
		}

		@Override
		public float compare(Comparison operator, ProgressionDataType with) {
			return switch (operator) {
			case EQUALS -> this.exp() == ((VanillaProgressionData)with).exp() ? 1f : 0f;
			case GREATER_THAN -> this.exp() > ((VanillaProgressionData)with).exp() ? 1f : 0f;
			case LESS_THAN -> this.exp() < ((VanillaProgressionData)with).exp() ? 1f : 0f;
			case GREATER_THAN_OR_EQUAL -> this.exp() >= ((VanillaProgressionData)with).exp() ? 1f : 0f;
			case LESS_THAN_OR_EQUAL -> this.exp() <= ((VanillaProgressionData)with).exp() ? 1f : 0f;
			default -> 0f;};
		}

		@Override
		public ProgressionDataType modify(Modification operator, ProgressionDataType with) {
			return switch (operator) {
			case INCREASE -> new VanillaProgressionData(this.exp() + ((VanillaProgressionData)with).exp());
			case DECREASE -> new VanillaProgressionData(this.exp() - ((VanillaProgressionData)with).exp());
			case MULTIPLY -> new VanillaProgressionData(this.exp() * ((VanillaProgressionData)with).exp());
			case DIVIDE -> new VanillaProgressionData(this.exp() / ((VanillaProgressionData)with).exp() != 0 
					? ((VanillaProgressionData)with).exp() : 1);
			case REPLACE -> with;
			default -> this;};
		}
	}
}
