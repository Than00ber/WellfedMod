package com.than00ber.wellfed.food;

import com.than00ber.wellfed.Configuration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class ConsumableFoodData {

    private static final int ONE_HEART = 1;
    private static final int THIRTY_SECONDS = 20 * 30;

    public final Item item;
    public AttributeModifier hearts;
    public int duration;
    public int time;

    public ConsumableFoodData(ItemStack stack, Player player) {
        this.item = stack.getItem();
        FoodProperties properties = stack.getFoodProperties(player);

        if (properties != null) {
            int nutrition = properties.getNutrition();
            float saturation = properties.getSaturationModifier();
            hearts = toHearts(nutrition);
            duration = toDuration(nutrition, saturation);
        } else {
            hearts = toHearts(ONE_HEART);
            duration = THIRTY_SECONDS;
        }
    }

    private ConsumableFoodData(Item item, AttributeModifier hearts, int duration, int time) {
        this.item = item;
        this.hearts = hearts;
        this.duration = duration;
        this.time = time;
    }

    public ConsumableFoodData copy() {
        return new ConsumableFoodData(item, hearts, duration, time); 
    }
    
    private static AttributeModifier toHearts(int nutrition) {
        double multiplier = Configuration.Common.getInstance().foodHeartsMultiplier();
        int health = Math.toIntExact(Math.round(Mth.clamp(nutrition / 2, 1, 10) * multiplier));
        return new AttributeModifier(UUID.randomUUID(), "food_hearts", health, AttributeModifier.Operation.ADDITION);
    }

    private static int toDuration(int nutrition, float saturation) {
        double multiplier = Configuration.Common.getInstance().foodDurationMultiplier();
        int base = THIRTY_SECONDS + (int) (THIRTY_SECONDS * (nutrition * saturation * 2.0F));
        return Math.round(Math.round(base * multiplier) / (float) THIRTY_SECONDS) * THIRTY_SECONDS;
    }
    
    public static CompoundTag save(ConsumableFoodData data) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("Item", ForgeRegistries.ITEMS.getKey(data.item).toString());
        compoundTag.put("Hearts", data.hearts.save());
        compoundTag.putInt("Duration", data.duration);
        compoundTag.putInt("Time", data.time);
        return compoundTag;
    }

    public static ConsumableFoodData load(CompoundTag compoundTag) {
        Item item = ForgeRegistries.ITEMS.getValue(new net.minecraft.resources.ResourceLocation(compoundTag.getString("Item")));
        AttributeModifier hearts = AttributeModifier.load(compoundTag.getCompound("Hearts"));
        int duration = compoundTag.getInt("Duration");
        int time = compoundTag.getInt("Time");
        return new ConsumableFoodData(item, hearts, duration, time);
    }
}
