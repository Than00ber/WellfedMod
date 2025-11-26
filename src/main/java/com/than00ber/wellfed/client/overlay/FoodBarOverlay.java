package com.than00ber.wellfed.client.overlay;

import com.than00ber.wellfed.Configuration;
import com.than00ber.wellfed.client.atlas.MiniTexture;
import com.than00ber.wellfed.client.atlas.MiniTextureAtlas;
import com.than00ber.wellfed.client.atlas.MiniTextureAtlasResourceLoader;
import com.than00ber.wellfed.food.ConsumableFoodData;
import com.than00ber.wellfed.food.DietHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FoodBarOverlay implements IGuiOverlay {

    private int previousFoodCount;
    private int foodBlinkEndTick;

    private boolean isVisible() {
        Minecraft minecraft = Minecraft.getInstance();

        return minecraft.player != null && !(minecraft.player.getVehicle() != null 
                && minecraft.player.getVehicle().showVehicleHealth())
                && minecraft.gameMode != null
                && !minecraft.options.hideGui
                && minecraft.gameMode.canHurtPlayer()
                && minecraft.getCameraEntity() instanceof Player;
    }

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        MiniTextureAtlas atlas = MiniTextureAtlasResourceLoader.getInstance().getAtlas();
        Player player = Minecraft.getInstance().player;

        if (isVisible() && atlas != null && player instanceof DietHolder holder) {
            List<ConsumableFoodData> slots = holder.getDiet().getSlots();
            
            if (!slots.isEmpty()) {
                Point offset = Configuration.Client.getInstance().foodBarOffset();
                Point point = new Point(offset.x + screenWidth / 2 + 10, offset.y + screenHeight - 39);
                renderFoodBar(graphics, atlas, point, player, slots);
            }
        }
    }

    private void renderFoodBar(GuiGraphics graphics, MiniTextureAtlas atlas, Point pos, Player player, List<ConsumableFoodData> slots) {
        int tick = Minecraft.getInstance().gui.getGuiTicks();      
        int count = slots.size();
        
        if (count < previousFoodCount) {
            foodBlinkEndTick = tick + 20;     
        }
        previousFoodCount = count;
        boolean blink = foodBlinkEndTick > tick && ((foodBlinkEndTick - tick) / 3) % 2 == 1;
        boolean hunger = player.hasEffect(MobEffects.HUNGER);
        
        for (Map.Entry<ConsumableFoodData, Integer> entry : computeShares(merge(slots)).entrySet()) {
            renderFood(graphics, atlas, pos, entry.getKey(), entry.getValue(), tick, blink, hunger);
            pos.x += 8 * entry.getValue();
        }
    }

    private void renderFood(GuiGraphics graphics, MiniTextureAtlas atlas, Point pos, ConsumableFoodData data, int size, int tick, boolean blink, boolean hunger) {
        MiniTexture[] textures = atlas.getTextures(data.item);
        int width = (size * 8) - (int) ((((float) size / data.duration) * data.time) * 8);

        for (int i = 0; i < size; i++) {
            int offset = pos.y + computeWobbleOffset(data, i, tick);
            textures[2].render(graphics, pos.x + i * 8, offset, 0xFF282828);
        }
        graphics.pose().pushPose();
        graphics.enableScissor(pos.x, pos.y, pos.x + width, pos.y + 9);

        for (int i = 0; i < size; i++) {
            int offset = pos.y + computeWobbleOffset(data, i, tick);
            textures[hunger ? 1 : 0].render(graphics, pos.x + i * 8, offset, 0xFFFFFFFF);
        }
        graphics.disableScissor();
        graphics.pose().popPose();

        for (int i = 0; i < size; i++) {
            int color = blink ? 0xFFFFFFFF : hunger ? 0xFF12410B : 0xFF000000;
            int offset = pos.y + computeWobbleOffset(data, i, tick);
            textures[3].render(graphics, pos.x + i * 8, offset, color);
        }
    }

    private List<ConsumableFoodData> merge(List<ConsumableFoodData> slots) {
        Map<Item, ConsumableFoodData> merged = new LinkedHashMap<>();

        for (ConsumableFoodData data : slots) {

            if (!merged.containsKey(data.item)) {
                merged.put(data.item, data.copy());
            } else {
                ConsumableFoodData existing = merged.get(data.item);
                existing.duration += data.duration;
                existing.time += data.time;
            }
        }
        return new ArrayList<>(merged.values());
    }

    private Map<ConsumableFoodData, Integer> computeShares(List<ConsumableFoodData> merged) {
        int totalDuration = merged.stream().mapToInt(x -> x.duration).sum();
        Map<ConsumableFoodData, Integer> result = new LinkedHashMap<>();

        if (merged.isEmpty()) {
            return result;
        }
        int maxSlots = 10;
        int remainingSlots = maxSlots;

        for (ConsumableFoodData data : merged) {
            result.put(data, 1);
            remainingSlots--;
        }
        if (remainingSlots <= 0) {
            return result;
        }
        for (ConsumableFoodData data : merged) {
            if (remainingSlots == 0) break;

            float ratio = (float) data.duration / (float) totalDuration;
            int extra = Math.round(ratio * maxSlots) - 1;

            if (extra > remainingSlots) {
                extra = remainingSlots;
            }
            if (extra < 0) extra = 0;

            result.put(data, result.get(data) + extra);
            remainingSlots -= extra;
        }
        if (remainingSlots > 0) {
            ConsumableFoodData last = merged.get(merged.size() - 1);
            result.put(last, result.get(last) + remainingSlots);
        }
        return result;
    }

    private int computeWobbleOffset(ConsumableFoodData food, int index, int tick) {
        int timeLeft = food.duration - food.time;
        float minute = 60 * 20;
        if (timeLeft > minute) return 0;
        float lowFactor = Mth.clamp(timeLeft / minute, 0.0F, 1.0F);
        int wobble = Mth.clamp(Math.round(lowFactor * 20), 1, 20);
        return tick % (wobble * 3 + 1) == 0 ? ((tick + index) % 2 == 0) ? 1 : -1 : 0;
    }
}
