package com.pyzpre.delicaciesdelights.effect.Schizophrenic;

import com.pyzpre.delicaciesdelights.index.EffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "delicacies_delights", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class SchizophrenicVisionEventsL1 {

    private static final Map<UUID, Entity> entityToReplacementMap = new HashMap<>();
    private static final EntityType<?>[] replacementTypes = new EntityType[]{
            EntityType.ALLAY,
            EntityType.AXOLOTL,
            EntityType.BAT,
            EntityType.CAMEL,
            EntityType.CAT,
            EntityType.CHICKEN,
            EntityType.COW,
            EntityType.DONKEY,
            EntityType.FROG,
            EntityType.HORSE,
            EntityType.MOOSHROOM,
            EntityType.OCELOT,
            EntityType.PIG,
            EntityType.SHEEP,
            EntityType.SNIFFER,
            EntityType.TURTLE,
            EntityType.VILLAGER,
            EntityType.WANDERING_TRADER,
            EntityType.FOX,
            EntityType.GOAT,
            EntityType.PANDA,
            EntityType.WOLF,
    };
    private static final Random random = new Random();
    private static final Logger LOGGER = LogManager.getLogger(SchizophrenicVisionEventsL1.class);

    private static boolean isReplacingEntity = false;

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Pre event) {
        if (isReplacingEntity) return; // Prevent re-entrant calls

        isReplacingEntity = true;
        try {
            Entity entity = event.getEntity();
            if (entity.level().isClientSide && isTargetMob(entity)) {
                Player player = Minecraft.getInstance().player;

                if (player != null && player.hasEffect(EffectRegistry.CRAZY.get())) {
                    int effectLevel = player.getEffect(EffectRegistry.CRAZY.get()).getAmplifier();
                    if (effectLevel == 1) {
                        // Get or create a replacement entity for this entity
                        Entity replacementEntity = entityToReplacementMap.computeIfAbsent(entity.getUUID(), k -> {
                            EntityType<?> replacementType = getRandomReplacementType();
                            return replacementType.create(entity.level());
                        });

                        if (replacementEntity != null) {
                            // Set the replacement entity's position to match the original entity
                            replacementEntity.setPos(entity.getX(), entity.getY(), entity.getZ());

                            // Copy movement and rotation details
                            replacementEntity.xo = entity.xo;
                            replacementEntity.yo = entity.yo;
                            replacementEntity.zo = entity.zo;
                            replacementEntity.setDeltaMovement(entity.getDeltaMovement());

                            // Copy rotation details
                            float interpolatedYaw = interpolateRotation(entity.yRotO, entity.getYRot(), event.getPartialTick());
                            float interpolatedPitch = interpolateRotation(entity.xRotO, entity.getXRot(), event.getPartialTick());
                            replacementEntity.setYRot(interpolatedYaw);
                            replacementEntity.setXRot(interpolatedPitch);

                            if (replacementEntity instanceof LivingEntity) {
                                LivingEntity livingReplacement = (LivingEntity) replacementEntity;

                                // Align body rotation
                                livingReplacement.yBodyRot = interpolatedYaw;
                                livingReplacement.yBodyRotO = interpolatedYaw;

                                // Interpolate head yaw and pitch
                                float targetHeadYaw = interpolateRotation(((LivingEntity) entity).yHeadRotO, entity.getYHeadRot(), event.getPartialTick());
                                float targetHeadPitch = interpolateRotation(((LivingEntity) entity).xRotO, entity.getXRot(), event.getPartialTick());

                                // Avoid large, sudden changes in rotation
                                float headRotDifference = Mth.wrapDegrees(targetHeadYaw - interpolatedYaw);
                                float maxHeadRotation = 45.0F;
                                if (headRotDifference > maxHeadRotation) {
                                    headRotDifference = maxHeadRotation;
                                } else if (headRotDifference < -maxHeadRotation) {
                                    headRotDifference = -maxHeadRotation;
                                }

                                livingReplacement.setYHeadRot(interpolatedYaw + headRotDifference);
                                livingReplacement.yHeadRotO = livingReplacement.getYHeadRot();
                                livingReplacement.setXRot(targetHeadPitch);
                                livingReplacement.xRotO = livingReplacement.getXRot();

                                // Sync walk animation for leg movement
                                syncWalkAnimation(((LivingEntity) entity).walkAnimation, livingReplacement.walkAnimation);
                            }

                            // Render the replacement entity instead of the original entity
                            Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(replacementEntity)
                                    .render(replacementEntity, interpolatedYaw, event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());

                            // Cancel the event to prevent the original entity from rendering
                            event.setCanceled(true);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to render entity replacement", e);
        } finally {
            isReplacingEntity = false; // Reset the flag
        }
    }

    private static boolean isTargetMob(Entity entity) {
        return entity instanceof Monster || entity instanceof FlyingMob;
    }

    private static EntityType<?> getRandomReplacementType() {
        return replacementTypes[random.nextInt(replacementTypes.length)];
    }

    private static float interpolateRotation(float prevYaw, float yaw, float partialTicks) {
        float deltaYaw = Mth.wrapDegrees(yaw - prevYaw);
        return prevYaw + partialTicks * deltaYaw;
    }

    private static void syncWalkAnimation(WalkAnimationState source, WalkAnimationState target) {
        float sourceSpeed = source.speed();
        float adjustedSpeed = sourceSpeed * 0.2F;

        if (source.isMoving()) {
            target.setSpeed(adjustedSpeed);
            target.update(adjustedSpeed, 0.5F);
        } else {
            target.setSpeed(0.0F);
            target.update(0.0F, 0.5F);
        }
    }
}
