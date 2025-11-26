package com.than00ber.wellfed.food;

import com.than00ber.wellfed.registry.GameRuleRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Diet {

    public static final EntityDataSerializer<Diet> DATA_SERIALIZER = new EntityDataSerializer<>() {
        @Override
        public void write(@NotNull FriendlyByteBuf buffer, @NotNull Diet value) {
            buffer.writeNbt(Diet.save(value));
        }

        @Override
        public @NotNull Diet read(FriendlyByteBuf buffer) {
            return Diet.load(Objects.requireNonNull(buffer.readNbt()));
        }

        @Override
        public @NotNull Diet copy(@NotNull Diet value) {
            return value;
        }
    };

    private final List<ConsumableFoodData> slots = new ArrayList<>();
    private int regen;

    public List<ConsumableFoodData> getSlots() {
        return slots;
    }

    public EatingOutcome canEat(ServerPlayer player, ConsumableFoodData data) {
        int max = player.level().getGameRules().getInt(GameRuleRegistry.MAX_CONSUMABLE_FOOD);
        boolean allow = player.level().getGameRules().getBoolean(GameRuleRegistry.ALLOW_EATING_SAME_ITEM);
        boolean enough = slots.size() < max;
        boolean balanced = allow || slots.stream().noneMatch(x -> x.item == data.item);
        return !enough ? EatingOutcome.TOO_MANY : !balanced ? EatingOutcome.NOT_BALANCED : EatingOutcome.SUCCESS;
    }

    public void addToSlot(ServerPlayer player, ConsumableFoodData data) {
        slots.add(data);
        Optional.ofNullable(player.getAttribute(Attributes.MAX_HEALTH)).ifPresent(x -> x.addPermanentModifier(data.hearts));
        player.heal((float) (data.hearts.getAmount() / 2.0F));
    }

    public boolean tick(ServerPlayer player) {
        boolean changed = false;
        GameRules rules = player.level().getGameRules();
        Set<Item> ticked = new HashSet<>();
        boolean needsRegen = rules.getBoolean(GameRules.RULE_NATURAL_REGENERATION) && player.isHurt();

        if (needsRegen) {
            regen++;
        }
        for (int i = slots.size() - 1; i >= 0; i--) {
            ConsumableFoodData data = slots.get(i);

            if (needsRegen) {
                if (regen >= rules.getInt(GameRuleRegistry.REGEN_HEALTH_TICK_INTERVAL)) {
                    player.heal(1.0F);
                    data.time += rules.getInt(GameRuleRegistry.REGEN_HEALTH_FOOD_DRAIN);
                    regen = 0;
                }
            }
            if (rules.getBoolean(GameRuleRegistry.FOOD_ITEM_STACKS) || !ticked.contains(data.item)) {
                ticked.add(data.item);
                data.time += player.hasEffect(MobEffects.HUNGER) ? rules.getInt(GameRuleRegistry.HUNGER_FOOD_DRAIN) : 1;
            }
            if (data.time >= data.duration) {
                slots.remove(i);
                Optional.ofNullable(player.getAttribute(Attributes.MAX_HEALTH)).ifPresent(x -> x.removeModifier(data.hearts));
                changed = true;
            }
        }
        return changed;
    }

    public static CompoundTag save(Diet diet) {
        CompoundTag compoundTag = new CompoundTag();
        ListTag list = new ListTag();
        diet.slots.forEach(x -> list.add(ConsumableFoodData.save(x)));
        compoundTag.put("Slots", list);
        return compoundTag;
    }

    public static Diet load(CompoundTag compoundTag) {
        Diet diet = new Diet();
        ListTag list = compoundTag.getList("Slots", Tag.TAG_COMPOUND);

        for (int i = 0; i < list.size(); i++) {
            diet.slots.add(ConsumableFoodData.load(list.getCompound(i)));
        }
        return diet;
    }
}
