package com.pyzpre.delicaciesdelights.effect.Schizophrenic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pyzpre.delicaciesdelights.index.EffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = "delicacies_delights", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class SchizophrenicMirageEvents {

    private static BlockPos HologramPos;
    private static final int DESPAWN_DISTANCE = 10;
    private static final List<Block> HOLOGRAM_BLOCKS = Arrays.asList(
            Blocks.CHEST,
            Blocks.DIAMOND_ORE,
            Blocks.BREWING_STAND,
            Blocks.FURNACE
    );
    private static Block currentHologramBlock = Blocks.CHEST;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;

        Player localPlayer = minecraft.player;
        if (localPlayer == null || !localPlayer.equals(event.player)) return; // Ensure it's the local player

        if (localPlayer.level().isClientSide && localPlayer.hasEffect(EffectRegistry.CRAZY.get())) {
            if (HologramPos == null) {
                spawnHologram(localPlayer);
            } else {
                double distance = localPlayer.position().distanceTo(Vec3.atCenterOf(HologramPos));
                if (distance < DESPAWN_DISTANCE) {
                    HologramPos = null;
                    pickRandomHologramBlock(); // Pick a new block for the next hologram
                }
            }
        } else {
            // If the effect is removed, clear the hologram
            HologramPos = null;
        }
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) { // Or use a more appropriate stage
            if (HologramPos != null) {
                renderHologram(HologramPos, event.getPoseStack(), event.getPartialTick());
            }
        }
    }

    private static void spawnHologram(Player player) {
        if (HologramPos == null) {
            // Define the range around the player where the chest can spawn
            int searchRadius = 35; // Adjust the max radius as needed
            int minDistance = 10; // Minimum distance from the player

            Vec3 playerPosition = player.position();
            BlockPos basePos = null;

            // Search for a suitable position within the range
            for (int attempts = 0; attempts < 100; attempts++) {
                // Generate a random position within the radius
                double offsetX = (player.getRandom().nextDouble() - 0.5) * 2 * searchRadius;
                double offsetZ = (player.getRandom().nextDouble() - 0.5) * 2 * searchRadius;
                Vec3 hologramPosition = playerPosition.add(offsetX, 0, offsetZ);

                // Check if the position is outside the minimum distance
                if (hologramPosition.distanceTo(playerPosition) < minDistance) {
                    continue; // Skip this position if it's too close
                }

                // Find solid ground at this position
                BlockPos potentialBasePos = findSolidGround(
                        player.level(),
                        new BlockPos(
                                Mth.floor(hologramPosition.x()),
                                Mth.floor(hologramPosition.y()),
                                Mth.floor(hologramPosition.z())
                        )
                );

                // Check if the position is suitable
                if (isSuitablePosition(player.level(), potentialBasePos)) {
                    basePos = potentialBasePos;
                    break;
                }
            }

            // If no suitable position is found, do not spawn the chest
            if (basePos != null) {
                HologramPos = basePos;
            }
        }
    }

    // Pick a random block from the list for the next hologram
    private static void pickRandomHologramBlock() {
        Random random = new Random();
        currentHologramBlock = HOLOGRAM_BLOCKS.get(random.nextInt(HOLOGRAM_BLOCKS.size()));
    }

    // Helper method to find the first solid block below a position
    private static BlockPos findSolidGround(Level world, BlockPos pos) {
        while (world.isEmptyBlock(pos) && pos.getY() > world.getMinBuildHeight()) {
            pos = pos.below();
        }
        return pos.above();
    }

    // Helper method to check if the position is suitable for the chest
    private static boolean isSuitablePosition(Level world, BlockPos basePos) {
        return world.getBlockState(basePos.below()).isSolid();
    }

    private static void renderHologram(BlockPos pos, PoseStack poseStack, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        BlockRenderDispatcher blockRenderer = mc.getBlockRenderer();

        poseStack.pushPose();

        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        poseStack.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        BlockState hologramState = currentHologramBlock.defaultBlockState();
        blockRenderer.renderSingleBlock(hologramState, poseStack, bufferSource, 15728880, OverlayTexture.NO_OVERLAY);

        bufferSource.endBatch();

        poseStack.popPose();
    }
}