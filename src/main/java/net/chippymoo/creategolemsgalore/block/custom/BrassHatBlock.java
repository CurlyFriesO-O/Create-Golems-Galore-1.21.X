package net.chippymoo.creategolemsgalore.block.custom;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlocks;
import net.chippymoo.creategolemsgalore.entity.ModEntities;
import net.chippymoo.creategolemsgalore.entity.custom.AndesiteGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import org.jetbrains.annotations.Nullable;

public class BrassHatBlock extends HorizontalDirectionalBlock {
    private BlockPattern golemPattern;
    public static final MapCodec<BrassHatBlock> CODEC = simpleCodec(BrassHatBlock ::new);

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        this.trySpawnCustomGolem(level, pos);
    }

    private void trySpawnCustomGolem(Level level, BlockPos pos) {
        if (this.golemPattern == null) {
            this.golemPattern = BlockPatternBuilder.start()
                    .aisle("~")
                    .aisle("#")
                    .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(AllBlocks.ANDESITE_ALLOY_BLOCK.get())))
                    .where('~', BlockInWorld.hasState(BlockStatePredicate.forBlock(this)))
                    .build();
        }

        BlockPattern.BlockPatternMatch match = this.golemPattern.find(level, pos);
        if (match != null) {
            // Remove the snow + pumpkin
            for (int y = 0; y < this.golemPattern.getHeight(); y++) {
                for (int x = 0; x < this.golemPattern.getWidth(); x++) {
                    for (int z = 0; z < this.golemPattern.getDepth(); z++) {
                        BlockInWorld part = match.getBlock(x, y, z);
                        if (part != null)
                            level.setBlock(part.getPos(), Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }

            // Spawn your golem entity
            BlockPos spawnPos = match.getBlock(0, 0, 0).getPos();
            AndesiteGolem golem = ModEntities.ANDESITEGOLEM.get().create(level);
            if (golem != null) {
                golem.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                        0.0F, 0.0F);
                level.addFreshEntity(golem);
            }
        }
    }

    public  BrassHatBlock(Properties properties) {
        super(properties);
    }
    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec(){
        return CODEC;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}

