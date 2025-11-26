package com.than00ber.wellfed.mixin;

import com.than00ber.wellfed.food.ConsumableFoodData;
import com.than00ber.wellfed.food.Diet;
import com.than00ber.wellfed.food.DietHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgePlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements IForgePlayer, DietHolder {

    @Unique private static final EntityDataAccessor<Diet> DIET_ACCESSOR = SynchedEntityData.defineId(Player.class, Diet.DATA_SERIALIZER);

    protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public Diet getDiet() {
        return entityData.get(DIET_ACCESSOR);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynchedData(CallbackInfo callback) {
        entityData.define(DIET_ACCESSOR, new Diet());
    }

    @Inject(method = "eat", at = @At("HEAD"))
    public void eat(Level level, ItemStack food, CallbackInfoReturnable<ItemStack> callback) {
        if (self() instanceof ServerPlayer player) {
            Diet diet = getDiet();
            ConsumableFoodData data = new ConsumableFoodData(food, player);

            if (diet.canEat(player, data).isSuccess()) {
                diet.addToSlot(player, data);
                entityData.set(DIET_ACCESSOR, diet, true);
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo callback) {
        if (self() instanceof ServerPlayer player) {
            Diet diet = getDiet();

            if (isDeadOrDying()) {
                AttributeInstance attribute = self().getAttribute(Attributes.MAX_HEALTH);

                if (attribute != null) {
                    for (ConsumableFoodData data : diet.getSlots()) {
                        attribute.removeModifier(data.hearts);
                    }
                }
                entityData.set(DIET_ACCESSOR, diet, true);
            } else if (diet.tick(player)) {
                entityData.set(DIET_ACCESSOR, diet, true);
            }
        }
    }
    
    @Inject(method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo callback) {
        compoundTag.put("Diet", Diet.save(getDiet()));
    }

    @Inject(method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo callback) {
        entityData.set(DIET_ACCESSOR, Diet.load(compoundTag.getCompound("Diet")), true);
    }
}