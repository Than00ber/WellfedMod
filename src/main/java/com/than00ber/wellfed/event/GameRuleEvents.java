package com.than00ber.wellfed.event;

import com.than00ber.wellfed.registry.GameRuleRegistry;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public final class GameRuleEvents {

    @SubscribeEvent
    public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        applyStartingHearts(event.getEntity());
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            applyStartingHearts(event.getEntity());
        }
    }

    private static void applyStartingHearts(Player player) {
        if (!player.level().isClientSide()) {
            int value = player.level().getGameRules().getInt(GameRuleRegistry.PLAYER_STARTING_HEARTS);
            value = Math.max(2, Math.min(40, value));
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(value);
            player.setHealth(value);
        }
    }
}
