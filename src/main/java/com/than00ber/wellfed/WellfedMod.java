package com.than00ber.wellfed;

import com.than00ber.wellfed.client.atlas.MiniTextureAtlasResourceLoader;
import com.than00ber.wellfed.client.overlay.FoodBarOverlay;
import com.than00ber.wellfed.food.Diet;
import com.than00ber.wellfed.registry.GameRuleRegistry;
import com.than00ber.wellfed.registry.PotionRegistry;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(WellfedMod.MODID)
public final class WellfedMod {

	public static final String MODID = "wellfed";

	public WellfedMod() {
        GameRuleRegistry.init();
        PotionRegistry.init();
		Configuration.init();

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        PotionRegistry.POTIONS.register(bus);
        PotionRegistry.EFFECTS.register(bus);
    }

	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static final class CommonSetupEvents {

		@SubscribeEvent
		public static void onFMLCommonSetupEvent(FMLCommonSetupEvent event) {
            EntityDataSerializers.registerSerializer(Diet.DATA_SERIALIZER);
		}
	}

	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static final class ClientSetupEvents {

        @SubscribeEvent
        public static void onRegisterGuiOverlaysEvent(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("food_bar_overlay", new FoodBarOverlay());
        }
        
        @SubscribeEvent
        public static void onRegisterClientReloadListenersEvent(RegisterClientReloadListenersEvent event) {
            event.registerReloadListener(MiniTextureAtlasResourceLoader.getInstance());
        }
	}
}
