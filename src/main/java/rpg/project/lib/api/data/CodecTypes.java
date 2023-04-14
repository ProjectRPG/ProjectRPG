package rpg.project.lib.api.data;

import java.util.UUID;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public class CodecTypes {
	public static final PrimitiveCodec<UUID> UUID_CODEC = new PrimitiveCodec<>() {
		@Override
		public <T> DataResult<UUID> read(DynamicOps<T> ops, T input) {
			return DataResult.success(UUID.fromString(ops.getStringValue(input).getOrThrow(false, null)));
		}
		@Override
		public <T> T write(DynamicOps<T> ops, UUID value) {
			return ops.createString(value.toString());
		}
		@Override
		public String toString() { return "uuid";}
	};
	
	public static final PrimitiveCodec<BlockPos> BLOCKPOS_CODEC = new PrimitiveCodec<>() {
		@Override
		public <T> DataResult<BlockPos> read(DynamicOps<T> ops, T input) {
			return DataResult.success(BlockPos.of(ops.getStringValue(input).map(Long::valueOf).getOrThrow(false, null)));
		}
		@Override
		public <T> T write(DynamicOps<T> ops, BlockPos value) {
			return ops.createString(String.valueOf(value.asLong()));
		}
		@Override
		public String toString() { return "blockpos";}
	};
	
	public static final PrimitiveCodec<ChunkPos> CHUNKPOS_CODEC = new PrimitiveCodec<>() {
		@Override
		public <T> DataResult<ChunkPos> read(DynamicOps<T> ops, T input) {
			return DataResult.success(new ChunkPos(ops.getNumberValue(input).map(Number::longValue).getOrThrow(false, null)));
		}
		@Override
		public <T> T write(DynamicOps<T> ops, ChunkPos value) {
			return ops.createLong(value.toLong());
		}
		@Override
		public String toString() { return "chunkpos";}
	};
}
