package com.than00ber.wellfed.registry;

import com.than00ber.wellfed.WellfedMod;
import com.than00ber.wellfed.potion.MetabolicBurstEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public final class PotionRegistry {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, WellfedMod.MODID);
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, WellfedMod.MODID);

    public static final RegistryObject<MobEffect> METABOLIC_BURST = EFFECTS.register("metabolic_burst", MetabolicBurstEffect::new);
    public static final RegistryObject<Potion> METABOLIC_BURST_POTION = POTIONS.register("metabolic_burst", () -> new Potion(new MobEffectInstance(METABOLIC_BURST.get(), 1)));

    public static void init() {
        BrewingRecipeRegistry.addRecipe(new IBrewingRecipe() {
            @Override
            public boolean isInput(@NotNull ItemStack stack) {
                return PotionUtils.getMobEffects(stack).stream().noneMatch(x -> x.getEffect() == METABOLIC_BURST.get()) && 
                        stack.getItem() == Items.POTION || 
                        stack.getItem() == Items.LINGERING_POTION || 
                        stack.getItem() == Items.SPLASH_POTION;
            }

            @Override
            public boolean isIngredient(@NotNull ItemStack stack) {
                return stack.isEdible();
            }

            @Override
            public @NotNull ItemStack getOutput(@NotNull ItemStack input, @NotNull ItemStack ingredient) {
                if (isInput(input) && ingredient.isEdible()) {
                    return PotionUtils.setPotion(new ItemStack(input.getItem()), PotionRegistry.METABOLIC_BURST_POTION.get());
                }
                return ItemStack.EMPTY;
            }
        });
    }
}
