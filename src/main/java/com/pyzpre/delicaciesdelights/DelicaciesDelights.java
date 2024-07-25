package com.pyzpre.delicaciesdelights;

import com.pyzpre.delicaciesdelights.block.injectionstand.InjectionStandScreen;
import com.pyzpre.delicaciesdelights.index.*;
import com.pyzpre.delicaciesdelights.network.NetworkSetup;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.pyzpre.delicaciesdelights.DelicaciesDelightsTabs.CREATIVE_MODE_TABS;
import static com.pyzpre.delicaciesdelights.index.ItemRegistry.ITEMS;

@Mod(DelicaciesDelights.MODID)
public class DelicaciesDelights {
    public static final String MODID = "delicacies_delights";
    private static final Logger LOGGER = LoggerFactory.getLogger(DelicaciesDelights.class);

    public DelicaciesDelights() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::doClientStuff);

        ITEMS.register(modEventBus);
        BlockRegistry.BLOCKS.register(modEventBus);
        BlockEntityRegistry.BLOCK_ENTITIES.register(modEventBus);
        ContainerRegistry.MENU_TYPES.register(modEventBus);
        EffectRegistry.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        RecipeRegistry.RECIPE_TYPES.register(modEventBus);
        RecipeRegistry.RECIPE_SERIALIZERS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        NetworkSetup.registerMessages();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Common setup tasks
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Server starting tasks
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(ContainerRegistry.INJECTION_STAND.get(), InjectionStandScreen::new);
        }
    }
}