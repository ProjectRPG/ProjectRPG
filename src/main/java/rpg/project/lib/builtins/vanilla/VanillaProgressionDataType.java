package rpg.project.lib.builtins.vanilla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.data.MergeableData;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.api.progression.ProgressionDataType;

import java.util.EnumSet;

public record VanillaProgressionDataType() implements SubSystemConfigType{
	public static final ResourceLocation ID = ResourceLocation.withDefaultNamespace("exp");
	public static final VanillaProgressionDataType IMPL = new VanillaProgressionDataType();

	@Override
	public ResourceLocation getId() {return ID;}
	@Override
	public MapCodec<SubSystemConfig> getCodec() {
		return VanillaProgressionData.CODEC;
	}

	@Override
	public SubSystemConfig getDefault(RegistryAccess access) {return new VanillaProgressionData(0);}

	@Override
	public EnumSet<APIUtils.SystemType> applicableSystemTypes() {
		return EnumSet.of(APIUtils.SystemType.PROGRESSION_DATA);
	}


	public record VanillaProgressionData(int exp) implements ProgressionDataType {
		public static final MapCodec<SubSystemConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
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
		public MapCodec<SubSystemConfig> getCodec() {
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
			};
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
			};
		}

		/**Depending on context, this may return the raw XP value or the whole player level.
		 * @return the raw XP value of the player
		 */
		@Override
		public long getProgressAsNumber() {
			return exp();
		}
	}
}
