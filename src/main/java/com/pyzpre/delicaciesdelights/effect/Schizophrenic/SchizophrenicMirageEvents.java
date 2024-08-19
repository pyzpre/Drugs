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

    private static BlockPos hologramPos;
    private static final int DESPAWN_DISTANCE = 10;
    private static final int MAX_DESPAWN_DISTANCE = 100;
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
        if (localPlayer == null || !localPlayer.equals(event.player)) return;

        if (localPlayer.level().isClientSide && localPlayer.hasEffect(EffectRegistry.CRAZY.get())) {
            int effectLevel = localPlayer.getEffect(EffectRegistry.CRAZY.get()).getAmplifier();
            if (effectLevel >= 1) {
            if (hologramPos == null) {
                spawnHologram(localPlayer);
            } else {
                double distance = localPlayer.position().distanceTo(Vec3.atCenterOf(hologramPos));

                // Despawn if within the minimum distance or beyond the maximum distance
                if (distance < DESPAWN_DISTANCE || distance > MAX_DESPAWN_DISTANCE) {
                    hologramPos = null;
                    pickRandomHologramBlock();
                }
            }
        }} else {
            hologramPos = null;
        }
    }


    @SubscribeEvent
    public static void onRenderWorldLast(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
            BlockPos posToRender = hologramPos;
            if (posToRender != null) {
                renderHologram(posToRender, event.getPoseStack(), event.getPartialTick());
            }
        }
    }

    private static synchronized void spawnHologram(Player player) {
        if (hologramPos == null) {
            int searchRadius = 35;
            int minDistance = 10;

            Vec3 playerPosition = player.position();
            BlockPos basePos = null;

            for (int attempts = 0; attempts < 100; attempts++) {
                double offsetX = (player.getRandom().nextDouble() - 0.5) * 2 * searchRadius;
                double offsetZ = (player.getRandom().nextDouble() - 0.5) * 2 * searchRadius;
                Vec3 hologramPosition = playerPosition.add(offsetX, 0, offsetZ);

                if (hologramPosition.distanceTo(playerPosition) < minDistance) {
                    continue;
                }

                BlockPos potentialBasePos = findSolidGround(
                        player.level(),
                        new BlockPos(
                                Mth.floor(hologramPosition.x()),
                                Mth.floor(hologramPosition.y()),
                                Mth.floor(hologramPosition.z())
                        )
                );

                if (isSuitablePosition(player.level(), potentialBasePos)) {
                    basePos = potentialBasePos;
                    break;
                }
            }

            if (basePos != null) {
                hologramPos = basePos;
            }
        }
    }

    private static void pickRandomHologramBlock() {
        Random random = new Random();
        currentHologramBlock = HOLOGRAM_BLOCKS.get(random.nextInt(HOLOGRAM_BLOCKS.size()));
    }

    private static BlockPos findSolidGround(Level world, BlockPos pos) {
        while (world.isEmptyBlock(pos) && pos.getY() > world.getMinBuildHeight()) {
            pos = pos.below();
        }
        return pos.above();
    }

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
