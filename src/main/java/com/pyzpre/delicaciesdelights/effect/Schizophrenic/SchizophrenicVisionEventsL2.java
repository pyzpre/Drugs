package com.pyzpre.delicaciesdelights.effect.Schizophrenic;

import com.pyzpre.delicaciesdelights.index.EffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

@Mod.EventBusSubscriber(modid = "delicacies_delights", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class SchizophrenicVisionEventsL2 {

    private static final Map<UUID, Entity> entityToReplacementMap = new HashMap<>();
    private static final Map<ResourceKey<Biome>, List<EntityType<?>>> biomeToReplacementTypeMap = new HashMap<>();
    private static final Random random = new Random();
    private static final Logger LOGGER = LogManager.getLogger(SchizophrenicVisionEventsL2.class);
    private static boolean isReplacingEntity = false;

    static {
        biomeToReplacementTypeMap.put(Biomes.OCEAN, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.PIG, EntityType.COW, EntityType.HORSE, EntityType.DONKEY));
        biomeToReplacementTypeMap.put(Biomes.DEEP_OCEAN, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.PIG, EntityType.COW, EntityType.HORSE, EntityType.DONKEY));
        biomeToReplacementTypeMap.put(Biomes.WARM_OCEAN, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.PIG, EntityType.COW, EntityType.PANDA, EntityType.PARROT, EntityType.OCELOT));
        biomeToReplacementTypeMap.put(Biomes.LUKEWARM_OCEAN, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.PIG, EntityType.COW, EntityType.PANDA, EntityType.PARROT, EntityType.OCELOT));
        biomeToReplacementTypeMap.put(Biomes.DEEP_LUKEWARM_OCEAN, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.PIG, EntityType.COW, EntityType.PANDA, EntityType.PARROT, EntityType.OCELOT));
        biomeToReplacementTypeMap.put(Biomes.COLD_OCEAN, List.of(EntityType.POLAR_BEAR));
        biomeToReplacementTypeMap.put(Biomes.DEEP_COLD_OCEAN, List.of(EntityType.POLAR_BEAR));
        biomeToReplacementTypeMap.put(Biomes.FROZEN_OCEAN, List.of(EntityType.POLAR_BEAR));
        biomeToReplacementTypeMap.put(Biomes.DEEP_FROZEN_OCEAN, List.of(EntityType.POLAR_BEAR));

        biomeToReplacementTypeMap.put(Biomes.MUSHROOM_FIELDS, List.of(EntityType.MOOSHROOM));

        biomeToReplacementTypeMap.put(Biomes.JAGGED_PEAKS, List.of(EntityType.GOAT));
        biomeToReplacementTypeMap.put(Biomes.FROZEN_PEAKS, List.of(EntityType.GOAT));
        biomeToReplacementTypeMap.put(Biomes.STONY_PEAKS, List.of(EntityType.GOAT));

        biomeToReplacementTypeMap.put(Biomes.MEADOW, List.of(EntityType.SHEEP, EntityType.DONKEY, EntityType.RABBIT));
        biomeToReplacementTypeMap.put(Biomes.CHERRY_GROVE, List.of(EntityType.SHEEP, EntityType.PIG, EntityType.RABBIT));
        biomeToReplacementTypeMap.put(Biomes.GROVE, List.of(EntityType.SHEEP, EntityType.PIG, EntityType.RABBIT, EntityType.CHICKEN, EntityType.COW, EntityType.FOX, EntityType.WOLF));
        biomeToReplacementTypeMap.put(Biomes.SNOWY_SLOPES, List.of(EntityType.RABBIT, EntityType.GOAT));
        biomeToReplacementTypeMap.put(Biomes.WINDSWEPT_HILLS, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.PIG, EntityType.COW, EntityType.LLAMA));
        biomeToReplacementTypeMap.put(Biomes.WINDSWEPT_GRAVELLY_HILLS, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.PIG, EntityType.COW, EntityType.LLAMA));
        biomeToReplacementTypeMap.put(Biomes.WINDSWEPT_FOREST, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.PIG, EntityType.COW, EntityType.LLAMA));

        biomeToReplacementTypeMap.put(Biomes.FOREST, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.COW, EntityType.PIG, EntityType.WOLF));
        biomeToReplacementTypeMap.put(Biomes.FLOWER_FOREST, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.COW, EntityType.PIG, EntityType.RABBIT));
        biomeToReplacementTypeMap.put(Biomes.TAIGA, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.COW, EntityType.PIG, EntityType.RABBIT, EntityType.FOX, EntityType.WOLF));
        biomeToReplacementTypeMap.put(Biomes.OLD_GROWTH_PINE_TAIGA, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.COW, EntityType.PIG, EntityType.RABBIT, EntityType.FOX, EntityType.WOLF));
        biomeToReplacementTypeMap.put(Biomes.OLD_GROWTH_SPRUCE_TAIGA, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.COW, EntityType.PIG, EntityType.RABBIT, EntityType.FOX, EntityType.WOLF));
        biomeToReplacementTypeMap.put(Biomes.SNOWY_TAIGA, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.COW, EntityType.PIG, EntityType.RABBIT, EntityType.FOX, EntityType.WOLF));
        biomeToReplacementTypeMap.put(Biomes.BIRCH_FOREST, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.COW, EntityType.PIG));
        biomeToReplacementTypeMap.put(Biomes.OLD_GROWTH_BIRCH_FOREST, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.COW, EntityType.PIG));
        biomeToReplacementTypeMap.put(Biomes.DARK_FOREST, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.COW, EntityType.PIG));
        biomeToReplacementTypeMap.put(Biomes.JUNGLE, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.PIG, EntityType.COW, EntityType.PANDA, EntityType.PARROT, EntityType.OCELOT));
        biomeToReplacementTypeMap.put(Biomes.SPARSE_JUNGLE, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.PIG, EntityType.COW, EntityType.WOLF));
        biomeToReplacementTypeMap.put(Biomes.BAMBOO_JUNGLE, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.PIG, EntityType.COW, EntityType.PANDA, EntityType.PARROT, EntityType.OCELOT));

        biomeToReplacementTypeMap.put(Biomes.RIVER, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.PIG, EntityType.COW, EntityType.HORSE, EntityType.DONKEY));
        biomeToReplacementTypeMap.put(Biomes.FROZEN_RIVER, List.of(EntityType.RABBIT, EntityType.POLAR_BEAR));
        biomeToReplacementTypeMap.put(Biomes.SWAMP, List.of(EntityType.FROG, EntityType.PIG, EntityType.COW, EntityType.CHICKEN, EntityType.SHEEP));
        biomeToReplacementTypeMap.put(Biomes.MANGROVE_SWAMP, List.of(EntityType.FROG, EntityType.PIG, EntityType.COW, EntityType.CHICKEN, EntityType.SHEEP));
        biomeToReplacementTypeMap.put(Biomes.BEACH, List.of(EntityType.TURTLE));
        biomeToReplacementTypeMap.put(Biomes.SNOWY_BEACH, List.of(EntityType.RABBIT, EntityType.POLAR_BEAR));
        biomeToReplacementTypeMap.put(Biomes.STONY_SHORE, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.PIG, EntityType.COW, EntityType.HORSE, EntityType.DONKEY));

        biomeToReplacementTypeMap.put(Biomes.PLAINS, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.PIG, EntityType.COW, EntityType.HORSE, EntityType.DONKEY));
        biomeToReplacementTypeMap.put(Biomes.SUNFLOWER_PLAINS, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.PIG, EntityType.COW, EntityType.HORSE, EntityType.DONKEY));
        biomeToReplacementTypeMap.put(Biomes.SNOWY_PLAINS, List.of(EntityType.RABBIT, EntityType.POLAR_BEAR));
        biomeToReplacementTypeMap.put(Biomes.ICE_SPIKES, List.of(EntityType.RABBIT, EntityType.POLAR_BEAR));

        biomeToReplacementTypeMap.put(Biomes.DESERT, List.of(EntityType.RABBIT, EntityType.CAMEL));
        biomeToReplacementTypeMap.put(Biomes.SAVANNA, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.PIG, EntityType.COW, EntityType.DONKEY, EntityType.HORSE));
        biomeToReplacementTypeMap.put(Biomes.SAVANNA_PLATEAU, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.PIG, EntityType.COW, EntityType.DONKEY, EntityType.HORSE, EntityType.LLAMA, EntityType.WOLF));
        biomeToReplacementTypeMap.put(Biomes.WINDSWEPT_SAVANNA, List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.PIG, EntityType.COW, EntityType.DONKEY, EntityType.HORSE, EntityType.LLAMA, EntityType.WOLF));
        biomeToReplacementTypeMap.put(Biomes.BADLANDS, List.of(EntityType.RABBIT));
        biomeToReplacementTypeMap.put(Biomes.WOODED_BADLANDS, List.of(EntityType.WOLF));
        biomeToReplacementTypeMap.put(Biomes.ERODED_BADLANDS, List.of(EntityType.RABBIT));

        biomeToReplacementTypeMap.put(Biomes.DEEP_DARK, List.of(EntityType.WARDEN));
        biomeToReplacementTypeMap.put(Biomes.DRIPSTONE_CAVES, List.of(EntityType.AXOLOTL));
        biomeToReplacementTypeMap.put(Biomes.LUSH_CAVES, List.of(EntityType.AXOLOTL));


        // Add more biome-specific replacements here
    }

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
                    if (effectLevel >= 2) {
                        BlockPos entityPos = new BlockPos((int) entity.getX(), (int) entity.getY(), (int) entity.getZ());
                        ResourceKey<Biome> biomeKey = entity.level().getBiome(entityPos).unwrapKey().orElse(null);

                        List<EntityType<?>> replacementTypes = biomeToReplacementTypeMap.getOrDefault(biomeKey, List.of(getRandomReplacementType()));
                        EntityType<?> replacementType = getRandomFromList(replacementTypes);

                        if (replacementType != null) {
                            Entity replacementEntity = entityToReplacementMap.computeIfAbsent(entity.getUUID(), k -> replacementType.create(entity.level()));

                            if (replacementEntity != null) {
                                replacementEntity.setPos(entity.getX(), entity.getY(), entity.getZ());
                                replacementEntity.xo = entity.xo;
                                replacementEntity.yo = entity.yo;
                                replacementEntity.zo = entity.zo;
                                replacementEntity.setDeltaMovement(entity.getDeltaMovement());

                                float interpolatedYaw = interpolateRotation(entity.yRotO, entity.getYRot(), event.getPartialTick());
                                float interpolatedPitch = interpolateRotation(entity.xRotO, entity.getXRot(), event.getPartialTick());
                                replacementEntity.setYRot(interpolatedYaw);
                                replacementEntity.setXRot(interpolatedPitch);

                                if (replacementEntity instanceof LivingEntity) {
                                    LivingEntity livingReplacement = (LivingEntity) replacementEntity;
                                    livingReplacement.yBodyRot = interpolatedYaw;
                                    livingReplacement.yBodyRotO = interpolatedYaw;

                                    float targetHeadYaw = interpolateRotation(((LivingEntity) entity).yHeadRotO, entity.getYHeadRot(), event.getPartialTick());
                                    float targetHeadPitch = interpolateRotation(((LivingEntity) entity).xRotO, entity.getXRot(), event.getPartialTick());

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

                                    syncWalkAnimation(((LivingEntity) entity).walkAnimation, livingReplacement.walkAnimation);
                                }

                                Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(replacementEntity)
                                        .render(replacementEntity, interpolatedYaw, event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());

                                event.setCanceled(true);
                            }
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
        return biomeToReplacementTypeMap.values().stream()
                .flatMap(List::stream)
                .skip(random.nextInt(biomeToReplacementTypeMap.values().stream().mapToInt(List::size).sum()))
                .findFirst()
                .orElse(null);
    }

    private static EntityType<?> getRandomFromList(List<EntityType<?>> entityTypes) {
        return entityTypes.get(random.nextInt(entityTypes.size()));
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