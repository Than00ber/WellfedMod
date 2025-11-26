package com.than00ber.wellfed.registry;

import net.minecraft.world.level.GameRules;

public final class GameRuleRegistry {

    public static void init() {
        // do nothing
    }
    
    public static final GameRules.Key<GameRules.IntegerValue> PLAYER_STARTING_HEARTS = GameRules.register("wellfed:playerStartingHearts", GameRules.Category.PLAYER, GameRules.IntegerValue.create(20));
    public static final GameRules.Key<GameRules.IntegerValue> MAX_CONSUMABLE_FOOD = GameRules.register("wellfed:maxConsumableFood", GameRules.Category.PLAYER, GameRules.IntegerValue.create(3));
    public static final GameRules.Key<GameRules.BooleanValue> ALLOW_EATING_SAME_ITEM = GameRules.register("wellfed:allowEatingTheSameItem", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
    public static final GameRules.Key<GameRules.BooleanValue> FOOD_ITEM_STACKS = GameRules.register("wellfed:foodItemStacks", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
    public static final GameRules.Key<GameRules.IntegerValue> HUNGER_FOOD_DRAIN = GameRules.register("wellfed:hungerFoodDrain", GameRules.Category.PLAYER, GameRules.IntegerValue.create(2));
    public static final GameRules.Key<GameRules.IntegerValue> REGEN_HEALTH_TICK_INTERVAL = GameRules.register("wellfed:regenHealthTickInterval", GameRules.Category.PLAYER, GameRules.IntegerValue.create(60));
    public static final GameRules.Key<GameRules.IntegerValue> REGEN_HEALTH_FOOD_DRAIN = GameRules.register("wellfed:regenHealthFoodDrain", GameRules.Category.PLAYER, GameRules.IntegerValue.create(3));
}
