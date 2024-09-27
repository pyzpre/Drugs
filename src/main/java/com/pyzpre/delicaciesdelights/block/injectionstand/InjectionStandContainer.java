package com.pyzpre.delicaciesdelights.block.injectionstand;

import com.pyzpre.delicaciesdelights.index.BlockRegistry;
import com.pyzpre.delicaciesdelights.index.ContainerRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.common.util.LazyOptional;

public class InjectionStandContainer extends AbstractContainerMenu {
    private final InjectionStandEntity blockEntity;
    private final ContainerLevelAccess containerLevelAccess;
    public final ContainerData data;

    // Server-side constructor
    public InjectionStandContainer(int id, Inventory playerInventory, InjectionStandEntity blockEntity, ContainerData data) {
        super(ContainerRegistry.INJECTION_STAND.get(), id);
        this.blockEntity = blockEntity;
        this.containerLevelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.data = data;

        // Initialize container slots
        addSlots(playerInventory);

        // Add the ContainerData object to the container to sync it
        addDataSlots(data); // Sync data values (ensure data has a length of 3)
    }

    // Client-side constructor
    public InjectionStandContainer(int id, Inventory playerInventory, FriendlyByteBuf extraData) {
        super(ContainerRegistry.INJECTION_STAND.get(), id);
        this.blockEntity = (InjectionStandEntity) playerInventory.player.level().getBlockEntity(extraData.readBlockPos());
        this.containerLevelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.data = new SimpleContainerData(3); // Initialize data with the correct size

        // Initialize container slots
        addSlots(playerInventory);

        // Add the ContainerData object to the container to sync it
        addDataSlots(data); // Sync data values
    }
    public int getCraftingProgress() {
        return this.data.get(0); // Index 0 corresponds to processTime
    }

    public int getTotalCraftingTime() {
        return this.data.get(1); // Index 1 corresponds to PROCESS_TIME_TOTAL
    }


    // Method to initialize all slots
    private void addSlots(Inventory playerInventory) {
        LazyOptional<IItemHandler> itemHandlerOptional = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER);
        IItemHandler itemHandler = itemHandlerOptional.orElseThrow(RuntimeException::new);

        // Add custom slots for the block entity
        this.addSlot(new SlotItemHandler(itemHandler, 0, 44, 17)); // Ingredient slot 1
        this.addSlot(new SlotItemHandler(itemHandler, 1, 80, 58)); // Ingredient slot 2
        this.addSlot(new SlotItemHandler(itemHandler, 2, 116, 17)); // Blaze powder slot

        // Player Inventory Slots
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // Player Hotbar Slots
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    // Check if the container is still valid
    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.containerLevelAccess, player, BlockRegistry.INJECTION_STAND.get());
    }

    // Handle shift-clicking behavior for quick move stack
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 3) {
                if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 3, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    // Check if crafting is in progress
    public boolean isCrafting() {
        return this.data.get(0) > 0;
    }

    // Get scaled progress for the progress bar
    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 24; // Adjust this value based on your texture
        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    // Get the potion color in the first ingredient slot
    public int getPotionColor() {
        ItemStack stack = this.blockEntity.getItem(0); // Adjust to the correct slot index
        if (stack.is(Items.POTION)) {
            int color = PotionUtils.getColor(stack);
            return color;
        }
        return 0xffffff; // Default color
    }

    // Check if blaze powder is present
    public boolean hasBlazePowder() {
        ItemStack stack = this.blockEntity.getItem(2);
        return stack.is(Items.BLAZE_POWDER);
    }

    // Get the scaled fill level of the bubble based on blaze powder (0 to 100)
    public int getBubbleFillLevel() {
        // Assume full fill for 1 item, adjust logic based on exact needs
        ItemStack stack = this.blockEntity.getItem(2);
        return stack.getCount() * 10; // Scale factor, adjust as needed
    }

    // Get the block entity associated with this container
    public InjectionStandEntity getBlockEntity() {
        return blockEntity;
    }
    public boolean getBlazePowderConsumed() {
        return this.data.get(3) == 1;
    }

    public boolean isPowered() {
        return this.data.get(2) == 1;
    }

}