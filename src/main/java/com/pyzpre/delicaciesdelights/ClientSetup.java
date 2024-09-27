package com.pyzpre.delicaciesdelights;

import com.pyzpre.delicaciesdelights.DelicaciesDelights;
import com.pyzpre.delicaciesdelights.index.EffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DelicaciesDelights.MODID, value = Dist.CLIENT)
public class ClientSetup {

    private static PostChain grayscaleShader;

    public static void initShader() {
        try {

            Minecraft mc = Minecraft.getInstance();
            ResourceLocation shaderRL = new ResourceLocation(DelicaciesDelights.MODID, "shaders/post/echoveil_effect.json");


            // Log before initializing PostChain

            grayscaleShader = new PostChain(mc.getTextureManager(), mc.getResourceManager(), mc.getMainRenderTarget(), shaderRL);

            // Check if PostChain is null or not after creation
            if (grayscaleShader == null) {
                System.err.println("Failed to initialize PostChain.");
                return;
            }

            grayscaleShader.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());


        } catch (Exception e) {
            System.err.println("Error initializing echoveil shader:");
            e.printStackTrace();
            grayscaleShader = null; // Ensure it's null if initialization fails
        }
    }



    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            return; // Log if the wrong stage is hit
        }

        Minecraft mc = Minecraft.getInstance();

        // Add debug information about player and effects
        if (mc.player != null) {

            boolean hasEffect = mc.player.hasEffect(EffectRegistry.ECHOVEIL.get());


            if (hasEffect) {
                if (grayscaleShader == null) {

                    initShader();
                }

                if (grayscaleShader != null) {
                    try {
                        grayscaleShader.process(event.getPartialTick());
                    } catch (Exception e) {
                        System.err.println("Error during shader processing:");
                        e.printStackTrace();
                    }
                }
            } else {
                if (grayscaleShader != null) {
                    grayscaleShader.close();
                    grayscaleShader = null;
                }
            }
        }
    }




}
