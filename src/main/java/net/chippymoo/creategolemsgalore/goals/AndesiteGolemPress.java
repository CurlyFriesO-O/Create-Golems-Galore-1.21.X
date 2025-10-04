package net.chippymoo.creategolemsgalore.goals;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import net.chippymoo.creategolemsgalore.entity.custom.AndesiteGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
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
import net.minecraft.world.level.pathfinder.PathFinder;


import java.util.EnumSet;
import java.util.Optional;

public class AndesiteGolemPress extends Goal {

    private final AndesiteGolem golem;
    private final double speed;
    private BlockPos targetDepot;

    private static final int PRESS_DURATION = 40; // animation length in ticks
    private static final int IMPACT_TICK = 35; // tick where pressing occurs

    public AndesiteGolemPress(AndesiteGolem golem, double speed) {
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
                    ItemStack item = depot.getHeldItem();
                    if (!item.isEmpty() && isPressable(item, level)) {
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
        if (targetDepot == null) return false;

        // Cancel if depot is empty or item is no longer pressable
        if (!(golem.level().getBlockEntity(targetDepot) instanceof DepotBlockEntity depot)) return false;
        ItemStack stack = depot.getHeldItem();
        if (stack.isEmpty() || !isPressable(stack, golem.level())) return false;

        return golem.isPressing() || !golem.blockPosition().closerThan(targetDepot, 2.0);
    }

    @Override
    public void start() {
        if (targetDepot != null) {
            // Move to a side of the depot
            BlockPos sidePos = getAdjacentSide(targetDepot);
            golem.getNavigation().moveTo(
                    sidePos.getX() + 0.5,
                    sidePos.getY(),
                    sidePos.getZ() + 0.5,
                    speed
            );
        }
    }

    @Override
    public void tick() {
        if (targetDepot == null) return;

        // Cancel if depot becomes invalid mid-animation
        if (!(golem.level().getBlockEntity(targetDepot) instanceof DepotBlockEntity depot)) {
            targetDepot = null;
            golem.setPressing(false);
            golem.setFrozen(false);
            return;
        }
        ItemStack stack = depot.getHeldItem();
        if (stack.isEmpty() || !isPressable(stack, golem.level())) {
            targetDepot = null;
            golem.setPressing(false);
            golem.setFrozen(false);
            return;
        }

        // Trigger pressing if close enough
        if (golem.blockPosition().closerThan(targetDepot, 1.5) && !golem.isPressing()) {
            // Set callback to execute pressDepot at impact frame
            golem.setPressCallback(() -> pressDepot(targetDepot, golem));

            // Start pressing animation (entity handles freeze and timing)
            golem.startPressAnimation();
        }
    }

    // -------------------------
    // Helper to pick a side of the depot
    // -------------------------
    private BlockPos getAdjacentSide(BlockPos depotPos) {
        BlockPos[] sides = {
                depotPos.offset(1, 0, 0),
                depotPos.offset(-1, 0, 0),
                depotPos.offset(0, 0, 1),
                depotPos.offset(0, 0, -1)
        };

        // Pick the closest side to the golem
        BlockPos closest = sides[0];
        double minDist = golem.blockPosition().distSqr(closest);
        for (BlockPos pos : sides) {
            double dist = golem.blockPosition().distSqr(pos);
            if (dist < minDist) {
                minDist = dist;
                closest = pos;
            }
        }
        return closest;
    }

    // -------------------------
    // Check if item has a pressing recipe
    // -------------------------
    private boolean isPressable(ItemStack stack, Level level) {
        SingleRecipeInput input = new SingleRecipeInput(stack.copy());
        Optional<RecipeHolder<PressingRecipe>> maybe =
                level.getRecipeManager().getRecipeFor(AllRecipeTypes.PRESSING.getType(), input, level);
        return maybe.isPresent();
    }

    // -------------------------
    // Pressing logic
    // -------------------------
    public static void pressDepot(BlockPos pos, Animal self) {
        if (!(self.level().getBlockEntity(pos) instanceof DepotBlockEntity depot)) return;

        ItemStack input = depot.getHeldItem();
        if (input.isEmpty()) return;

        Level level = self.level();

        SimpleContainer container = new SimpleContainer(input.copy());

        SingleRecipeInput recipeInput = new SingleRecipeInput(input.copy());

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

