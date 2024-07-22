package com.pyzpre.delicaciesdelights.events;

import com.pyzpre.delicaciesdelights.DelicaciesDelights;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = DelicaciesDelights.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class FOVHandler {
    @SubscribeEvent
    public static void onComputeFovModifier(ComputeFovModifierEvent event) {
        Player player = event.getPlayer();
        float newFovModifier = event.getNewFovModifier();

        for (MobEffectInstance effect : player.getActiveEffects()) {
            String effectName = effect.getEffect().getDescriptionId();
            List<OverlayManager.OverlayMetadata> overlays = OverlayManager.getOverlays(effectName);
            if (overlays != null) {
                for (OverlayManager.OverlayMetadata overlay : overlays) {
                    if (overlay.fovChange != 0.0f) {
                        newFovModifier += (overlay.fovChange * (effect.getAmplifier() + 1));
                    }
                }
            }
        }

        event.setNewFovModifier(newFovModifier);
    }
}
