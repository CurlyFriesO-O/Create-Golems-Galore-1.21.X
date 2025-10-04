package net.chippymoo.creategolemsgalore.entity.custom;

import net.chippymoo.creategolemsgalore.goals.AndesiteGolemPress;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class AndesiteGolem extends Animal {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState pressingAnimationState = new AnimationState();

    private int idleAnimationTimeout = 40;
    private int pressingTicks = 0;

    private static final EntityDataAccessor<Boolean> DATA_PRESSING =
            SynchedEntityData.defineId(AndesiteGolem.class, EntityDataSerializers.BOOLEAN);


    public AndesiteGolem(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0));
        this.goalSelector.addGoal(2, new AndesiteGolemPress(this, 1.5));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, stack -> stack.is(Items.IRON_NUGGET), false));

        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

    }


    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10d)
                .add(Attributes.KNOCKBACK_RESISTANCE, 2)
                .add(Attributes.MOVEMENT_SPEED, 0.1D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }


    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }


    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }




    private int pressingTimer = 0;
    private boolean isFrozen = false;
    private PressAction pressCallback;

    private static final int ANIMATION_LENGTH = 40; // total ticks of pressing animation
    private static final int IMPACT_TICK = 35;      // tick where the press happens

    // Callback interface for pressing
    public interface PressAction {
        void onImpact();
    }

    public void setPressCallback(PressAction callback) {
        this.pressCallback = callback;
    }

    public boolean isPressing() {
        return this.entityData.get(DATA_PRESSING);
    }

    public void setPressing(boolean pressing) {
        this.entityData.set(DATA_PRESSING, pressing);
    }

    public void setFrozen(boolean frozen) {
        this.isFrozen = frozen;
    }

    public boolean isFrozen() {
        return this.isFrozen;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_PRESSING, false);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 11) {
            // client receives server trigger
            this.pressingAnimationState.start(this.tickCount);
        } else {
            super.handleEntityEvent(id);
        }
    }

    // -------------------------
    // Start pressing animation
    // -------------------------
    public void startPressAnimation() {
        if (this.isPressing()) return; // don't retrigger

        // Freeze movement
        this.setFrozen(true);
        this.getNavigation().stop();

        // Start pressing animation
        this.setPressing(true);
        this.pressingTimer = ANIMATION_LENGTH;

        if (level().isClientSide) {
            this.pressingAnimationState.start(this.tickCount);
            this.pressingAnimationState.animateWhen(true, this.tickCount);
        } else {
            this.level().broadcastEntityEvent(this, (byte) 11);
        }
    }

    @Override
    public void tick() {
        super.tick();

        // Tick animation on client
        if (level().isClientSide && this.isPressing()) {
            this.pressingAnimationState.animateWhen(true, this.tickCount);
        }

        // Countdown pressing timer on server
        if (!level().isClientSide && this.isPressing()) {
            pressingTimer--;

            // Trigger impact tick
            if (pressingTimer == ANIMATION_LENGTH - IMPACT_TICK) {
                // Play sound
                this.level().playSound(null, this.blockPosition(),
                        SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);

                // Call the goal’s press logic
                if (pressCallback != null) {
                    pressCallback.onImpact();
                }
            }

            // End pressing
            if (pressingTimer <= 0) {
                this.setPressing(false);
                this.setFrozen(false);
                pressCallback = null; // clear callback after pressing
            }
        }
    }
}





