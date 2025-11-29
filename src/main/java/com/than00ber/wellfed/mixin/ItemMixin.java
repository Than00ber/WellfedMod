package com.than00ber.wellfed.mixin;

import com.than00ber.wellfed.food.DietHolder;
import com.than00ber.wellfed.food.EatingOutcome;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin implements FeatureElement, ItemLike, IForgeItem {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> callback) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.isEdible() && !level.isClientSide() && player instanceof DietHolder holder) {
            EatingOutcome outcome = holder.getDiet().canEat((ServerPlayer) player, stack);

            if (outcome.isSuccess()) {
                player.startUsingItem(hand);
                callback.setReturnValue(InteractionResultHolder.consume(stack));
            } else {
                player.displayClientMessage(outcome.message(), true);
                callback.setReturnValue(InteractionResultHolder.fail(stack));
            }
        } else {
            callback.setReturnValue(InteractionResultHolder.pass(player.getItemInHand(hand)));
        }
    }
}