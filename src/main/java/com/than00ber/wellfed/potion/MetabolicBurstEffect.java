package com.than00ber.wellfed.potion;

import com.than00ber.wellfed.food.ConsumableFoodData;
import com.than00ber.wellfed.food.DietHolder;
import com.than00ber.wellfed.registry.GameRuleRegistry;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MetabolicBurstEffect extends InstantenousMobEffect {

    public MetabolicBurstEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFB57A4C);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity instanceof DietHolder holder) {
            float missing = entity.getMaxHealth() - entity.getHealth();

            if (missing > 0) {
                List<ConsumableFoodData> slots = holder.getDiet().getSlots();
                int drain = entity.level().getGameRules().getInt(GameRuleRegistry.REGEN_HEALTH_FOOD_DRAIN);

                for (int i = slots.size() - 1; i >= 0 && missing > 0; i--) {
                    ConsumableFoodData data = slots.get(i);

                    int remaining = data.duration - data.time;
                    if (remaining <= 0) continue;

                    float healable = remaining / (float) drain;
                    if (healable <= 0) continue;

                    float heal = Math.min(missing, healable);
                    entity.heal(heal);
                    missing -= heal;
                    data.time += Math.round(heal * drain); 
                    // the rest will be taken care of in the diet.
                }
            }
        }
    }
}