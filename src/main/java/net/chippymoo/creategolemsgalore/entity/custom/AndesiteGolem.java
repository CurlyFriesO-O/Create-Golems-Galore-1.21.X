package net.chippymoo.creategolemsgalore.entity.custom;

import com.simibubi.create.AllItems;
import net.chippymoo.creategolemsgalore.entity.AndesiteGolemVariants;
import net.chippymoo.creategolemsgalore.goals.AndesiteGolemPress;
import net.minecraft.Util;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class AndesiteGolem extends Animal {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState pressingAnimationState = new AnimationState();

    private int pressingTimer = 0;
    private boolean isFrozen = false;
    private PressAction pressCallback;

    private static final int ANIMATION_LENGTH = 40;
    private static final int IMPACT_TICK = 27;


    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(AndesiteGolem.class, EntityDataSerializers.INT);



    private static final EntityDataAccessor<Boolean> DATA_PRESSING =
            SynchedEntityData.defineId(AndesiteGolem.class, EntityDataSerializers.BOOLEAN);

    public AndesiteGolem(EntityType<? extends Animal> entityType, Level level)
    {
        super(entityType, level);
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0));
        this.goalSelector.addGoal(2, new AndesiteGolemPress(this, 1.5));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, stack -> stack.is(AllItems.ANDESITE_ALLOY), false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 2.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.1D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    public boolean isFood(ItemStack stack)
    {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner)
    {
        return null;
    }


    public interface PressAction
    {
        void onImpact();
    }

    public void setPressCallback(PressAction callback)
    {
        this.pressCallback = callback;
    }

    public boolean isPressing()
    {
        return this.entityData.get(DATA_PRESSING);
    }

    public void setPressing(boolean pressing)
    {
        this.entityData.set(DATA_PRESSING, pressing);
    }

    public void setFrozen(boolean frozen)
    {
        this.isFrozen = frozen;
    }

    public boolean isFrozen()
    {
        return this.isFrozen;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_PRESSING, false);
        builder.define(VARIANT, 0);
    }



    @Override
    public void handleEntityEvent(byte id) {
        if (id == 11)
        {
            this.pressingAnimationState.start(this.tickCount);
        }
        else
        {
            super.handleEntityEvent(id);
        }
    }

    public void startPressAnimation()
    {

        if (this.isPressing()) return;

        this.setPressing(true);
        this.setFrozen(true);
        this.pressingTimer = ANIMATION_LENGTH;


        if (this.getNavigation().isInProgress())
        {
            this.getNavigation().stop();
        }


        if (level().isClientSide)
        {
            this.pressingAnimationState.start(this.tickCount);
            this.pressingAnimationState.animateWhen(true, this.tickCount);
        }
        else
        {
            this.level().broadcastEntityEvent(this, (byte) 11);
        }
    }

    @Override
    public void tick() {
        super.tick();


        if (level().isClientSide && this.isPressing())
        {
            this.pressingAnimationState.animateWhen(true, this.tickCount);
        }


        if (!level().isClientSide && this.isPressing())
        {
            pressingTimer--;


            if (pressingTimer == ANIMATION_LENGTH - IMPACT_TICK)
            {
                this.level().playSound(null, this.blockPosition(),
                        SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);

                if (this.level() instanceof ServerLevel server)
                {
                    server.sendParticles(
                            ParticleTypes.CRIT,
                            this.getX(),
                            this.getY() + 1.0,
                            this.getZ(),
                            5, 0.2, 0.2, 0.2, 0.01
                    );
                }


                if (pressCallback != null)
                {
                    pressCallback.onImpact();
                }
            }


            if (pressingTimer <= 0)
            {
                this.setPressing(false);
                this.setFrozen(false);
                pressCallback = null;
            }
        }
    }
    private int getTypeVariant()
    {
        return this.entityData.get(VARIANT);
    }

    public AndesiteGolemVariants getVariant()
    {
        return AndesiteGolemVariants.byId(this.getTypeVariant() & 255);
    }

    private void setVariant(AndesiteGolemVariants variant)
    {
        this.entityData.set(VARIANT, variant.getId() & 255);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.getTypeVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(VARIANT, compound.getInt("Variant"));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData)
    {
        AndesiteGolemVariants variant = Util.getRandom(AndesiteGolemVariants.values(), this.random);
        this.setVariant(variant);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }
}