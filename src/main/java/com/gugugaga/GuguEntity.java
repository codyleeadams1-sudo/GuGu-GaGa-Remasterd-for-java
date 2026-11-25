package com.gugugaga;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.damagesource.DamageSource;

public class GuguEntity extends TamableAnimal {
    public GuguEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.COD) || stack.is(Items.SALMON) ||
               stack.is(Items.TROPICAL_FISH) || stack.is(Items.PUFFERFISH);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (this.isFood(held)) {
            if (!this.level().isClientSide()) {
                if (!this.isTame()) {
                    if (this.random.nextInt(3) == 0) {
                        this.tame(player);
                        this.setOrderedToSit(false);
                        this.navigation.stop();
                    }
                } else if (this.isOwnedBy(player)) {
                    if (this.getHealth() < this.getMaxHealth()) {
                        this.heal(4.0F);
                    } else {
                        this.setOrderedToSit(!this.isOrderedToSit());
                    }
                }
                this.playSound(GuguSounds.FEED, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) held.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        if (this.isTame() && this.isOwnedBy(player) && !this.level().isClientSide()) {
            this.setOrderedToSit(!this.isOrderedToSit());
            this.navigation.stop();
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.15, 5.0F, 2.0F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    protected SoundEvent getAmbientSound() { return GuguSounds.AMBIENT; }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return GuguSounds.HURT; }
    @Override
    protected SoundEvent getDeathSound() { return GuguSounds.DEATH; }
}
