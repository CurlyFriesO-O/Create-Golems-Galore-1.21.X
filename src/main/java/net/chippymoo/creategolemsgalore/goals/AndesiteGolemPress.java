package net.chippymoo.creategolemsgalore.goals;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.SingleRecipeInput;


import java.util.EnumSet;
import java.util.Optional;

public class AndesiteGolemPress extends Goal {
    private final PathfinderMob golem;
    private final double speed;
    private BlockPos targetDepot;

    public AndesiteGolemPress(PathfinderMob golem, double speed) {
        this.golem = golem;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Level level = golem.level();
        BlockPos golemPos = golem.blockPosition();
        int radius = 8;

        for (BlockPos pos : BlockPos.betweenClosed(
                golemPos.offset(-radius, -2, -radius),
                golemPos.offset(radius, 2, radius))) {

            if (level.getBlockState(pos).is(AllBlocks.DEPOT.get())) {
                if (level.getBlockEntity(pos) instanceof DepotBlockEntity depot) {
                    if (!depot.getHeldItem().isEmpty()) {
                        targetDepot = pos.immutable();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return targetDepot != null && !golem.blockPosition().closerThan(targetDepot, 2.0);
    }

    @Override
    public void start() {
        if (targetDepot != null) {
            golem.getNavigation().moveTo(
                    targetDepot.getX() + 0.5,
                    targetDepot.getY() + 1,
                    targetDepot.getZ() + 0.5,
                    speed
            );
        }
    }

    @Override
    public void tick() {
        if (targetDepot != null && golem.blockPosition().closerThan(targetDepot, 2.0)) {
            pressDepot(targetDepot);
            targetDepot = null;
        }
    }

    private void pressDepot(BlockPos pos) {


            if (!(golem.level().getBlockEntity(pos) instanceof DepotBlockEntity depot)) return;

            ItemStack input = depot.getHeldItem();
            if (input.isEmpty()) return;

            Level level = golem.level();

            SimpleContainer container = new SimpleContainer(input.copy());

// Ask the manager for a pressing recipe that matches this container
        SingleRecipeInput recipeInput = new SingleRecipeInput(input.copy());
// (if your mappings use SingleStackRecipeInput, swap that class name)

// Now RecipeManager will accept it
        Optional<RecipeHolder<PressingRecipe>> maybe =
                level.getRecipeManager().getRecipeFor(AllRecipeTypes.PRESSING.getType(), recipeInput, level);

        if (maybe.isPresent()) {
            PressingRecipe recipe = maybe.get().value();
            ItemStack output = recipe.assemble(recipeInput, level.registryAccess());
            output.setCount(Math.min(input.getCount(), output.getMaxStackSize()));

            depot.setHeldItem(output.copy());
            depot.setChanged();
            level.sendBlockUpdated(pos, depot.getBlockState(), depot.getBlockState(), 3);
        }


    }
}