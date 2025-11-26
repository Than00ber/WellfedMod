package com.than00ber.wellfed.event;

import com.than00ber.wellfed.food.ConsumableFoodData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public final class TooltipEvent {

    @SubscribeEvent
    public static void onItemTooltipEvent(ItemTooltipEvent event) {
        Player player = Minecraft.getInstance().player;
        ItemStack stack = event.getItemStack();

        if (player != null && stack.getItem().isEdible()) {
            ConsumableFoodData data = new ConsumableFoodData(stack, player);
            List<Component> tooltip = event.getToolTip();
            
            String fed = StringUtil.formatTickDuration(data.duration);
            tooltip.add(Component.translatable("tooltip.fed", fed)
                    .withStyle(ChatFormatting.BLUE));

            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("tooltip.eaten")
                    .withStyle(ChatFormatting.DARK_PURPLE));

            String amount = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(data.hearts.getAmount());
            Component description = Component.translatable(Attributes.MAX_HEALTH.getDescriptionId());
            String key = "attribute.modifier.plus." + data.hearts.getOperation().toValue();
            tooltip.add(Component.translatable(key, amount, description)
                    .withStyle(ChatFormatting.BLUE));
        }
    }
}
