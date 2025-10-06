package net.chippymoo.creategolemsgalore.goals;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import net.chippymoo.creategolemsgalore.entity.custom.AndesiteGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Optional;

public class AndesiteGolemPress extends Goal {

    private final AndesiteGolem golem;
    private final double speed;
    private BlockPos targetDepot;

    public AndesiteGolemPress(AndesiteGolem golem, double speed)
    {
        this.golem = golem;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    //If it can use the press function or not.
    @Override
    public boolean canUse()
    {
        Level level = golem.level();
        BlockPos golemPos = golem.blockPosition();
        int radius = 8;

        for (BlockPos pos : BlockPos.betweenClosed(
                golemPos.offset(-radius, -2, -radius),
                golemPos.offset(radius, 2, radius))) {

            if (level.getBlockState(pos).is(AllBlocks.DEPOT.get()))
            {
                if (level.getBlockEntity(pos) instanceof DepotBlockEntity depot)
                {
                    ItemStack item = depot.getHeldItem();
                    if (!item.isEmpty() && isPressable(item, level))
                    {
                        targetDepot = pos.immutable();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse()
    {
        if (targetDepot == null)
        {
            return false;
        }

        if (!(golem.level().getBlockEntity(targetDepot) instanceof DepotBlockEntity depot))
        {
            return false;
        }

        ItemStack stack = depot.getHeldItem();

        if (stack.isEmpty() || !isPressable(stack, golem.level()))
        {
            return false;
        }

        return golem.isPressing() || !golem.blockPosition().closerThan(targetDepot, 2.5);
    }

    @Override
    public void start()
    {
        if (targetDepot != null)
        {
            Vec3 depotCenter = Vec3.atCenterOf(targetDepot);
            golem.getNavigation().moveTo(depotCenter.x, depotCenter.y, depotCenter.z, speed);
        }
    }

    @Override
    public void tick() {
        if (targetDepot == null) return;
        Level level = golem.level();

        if (!(level.getBlockEntity(targetDepot) instanceof DepotBlockEntity depot))
        {
            stopPressing();
            return;
        }

        ItemStack stack = depot.getHeldItem();
        if (stack.isEmpty() || !isPressable(stack, level))
        {
            stopPressing();
            return;
        }

        Vec3 depotCenter = Vec3.atCenterOf(targetDepot);
        double distSq = golem.distanceToSqr(depotCenter);

        if (distSq > 2.25)
        {
            if (golem.getNavigation().isDone() && !golem.isFrozen())
            {
                golem.getNavigation().moveTo(depotCenter.x, depotCenter.y, depotCenter.z, speed);
            }
            return;
        }

        golem.getLookControl().setLookAt(depotCenter.x, depotCenter.y + 0.5, depotCenter.z);

        if (!golem.isPressing())
        {
            golem.setPressCallback(() -> pressDepot(targetDepot, golem));
            golem.startPressAnimation();
        }
    }

    @Override
    public void stop()
    {
        stopPressing();
    }

    private void stopPressing()
    {
        golem.setPressing(false);
        golem.setFrozen(false);
        targetDepot = null;
    }


    private boolean isPressable(ItemStack stack, Level level)
    {
        SingleRecipeInput input = new SingleRecipeInput(stack.copy());
        Optional<RecipeHolder<PressingRecipe>> maybe =
                level.getRecipeManager().getRecipeFor(AllRecipeTypes.PRESSING.getType(), input, level);
        return maybe.isPresent();
    }


    public static void pressDepot(BlockPos pos, AndesiteGolem golem)
    {
        Level level = golem.level();
        if (!(level.getBlockEntity(pos) instanceof DepotBlockEntity depot))
        {
            return;
        }

        ItemStack input = depot.getHeldItem();

        if (input.isEmpty())
        {
            return;
        }

        SingleRecipeInput recipeInput = new SingleRecipeInput(input.copy());
        Optional<RecipeHolder<PressingRecipe>> maybe =
                level.getRecipeManager().getRecipeFor(AllRecipeTypes.PRESSING.getType(), recipeInput, level);

        if (maybe.isEmpty())
        {
            return;
        }

        PressingRecipe recipe = maybe.get().value();
        ItemStack output = recipe.assemble(recipeInput, level.registryAccess());
        output.setCount(Math.min(input.getCount(), output.getMaxStackSize()));

        depot.setHeldItem(output.copy());
        depot.setChanged();
        level.sendBlockUpdated(pos, depot.getBlockState(), depot.getBlockState(), 3);

        //Effects
        if (level instanceof ServerLevel server)
        {
            server.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.7F, 1.0F);
            server.sendParticles(
                    ParticleTypes.CRIT,
                    pos.getX() + 0.5,
                    pos.getY() + 1.0,
                    pos.getZ() + 0.5,
                    8,
                    0.1, 0.1, 0.1,
                    0.02
            );
        }
    }
}