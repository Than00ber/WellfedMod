package com.than00ber.wellfed;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;

public final class Configuration {

	private static final Pair<Common, ForgeConfigSpec> COMMON = new ForgeConfigSpec.Builder().configure(Common::new);
	private static final Pair<Client, ForgeConfigSpec> CLIENT = new ForgeConfigSpec.Builder().configure(Client::new);

	public static void init() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.COMMON.getValue());
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Configuration.CLIENT.getValue());
	}

	public static class Common {

		public static Common getInstance() {
			return Configuration.COMMON.getKey();
		}

        private final ForgeConfigSpec.ConfigValue<Double> foodHeartsMultiplier;
        private final ForgeConfigSpec.ConfigValue<Double> foodDurationMultiplier;

        public Common(ForgeConfigSpec.Builder builder) {
            foodHeartsMultiplier = builder.define("foodHeartsMultiplier", 1.0D);
            foodDurationMultiplier = builder.define("foodDurationMultiplier", 1.0D);
        }

        public double foodHeartsMultiplier() {
            return foodHeartsMultiplier.get();
        }

        public double foodDurationMultiplier() {
            return foodDurationMultiplier.get();
        }
    }

	public static class Client {

		public static Client getInstance() {
			return CLIENT.getKey();
		}

        private final ForgeConfigSpec.ConfigValue<Integer> foodBarOffsetX;
        private final ForgeConfigSpec.ConfigValue<Integer> foodBarOffsetY;
        
		public Client(ForgeConfigSpec.Builder builder) {
            foodBarOffsetX = builder.define("foodBarOffsetX", 0);
            foodBarOffsetY = builder.define("foodBarOffsetY", 0);
		}

        public Point foodBarOffset() {
            return new Point(foodBarOffsetX.get(), foodBarOffsetY.get());
        }
    }
}
