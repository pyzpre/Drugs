package com.pyzpre.delicaciesdelights.block.injectionstand;

import com.pyzpre.delicaciesdelights.index.BlockRegistry;
import com.pyzpre.delicaciesdelights.index.ContainerRegistry;
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
    private final ContainerData data;

    public InjectionStandContainer(int id, Inventory playerInventory, InjectionStandEntity blockEntity, ContainerData data) {
        super(ContainerRegistry.INJECTION_STAND.get(), id);
        this.blockEntity = blockEntity;
        this.containerLevelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.data = data;

        LazyOptional<IItemHandler> itemHandlerOptional = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER);
        IItemHandler itemHandler = itemHandlerOptional.orElseThrow(RuntimeException::new);

        // Ingredient slot (ingredient1, potion)
        this.addSlot(new SlotItemHandler(itemHandler, 0, 44, 17));

        // Ingredient slot (ingredient2, edible)
        this.addSlot(new SlotItemHandler(itemHandler, 1, 80, 58));

        // Result slot using the custom ResultSlot class


        // Player Inventory Slots
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }

        addDataSlots(data);
    }

    private static class ResultSlot extends SlotItemHandler {
        private final InjectionStandEntity blockEntity;

        public ResultSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, InjectionStandEntity blockEntity) {
            super(itemHandler, index, xPosition, yPosition);
            this.blockEntity = blockEntity;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            // Prevents placing items in the result slot
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            // Allows pickup only if there is a valid result in the INGREDIENT2_SLOT
            return !blockEntity.getItem(InjectionStandEntity.INGREDIENT2_SLOT).isEmpty();
        }

    }
    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.containerLevelAccess, player, BlockRegistry.INJECTION_STAND.get());
    }

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


    public boolean isCrafting() {
        return this.data.get(0) > 0;
    }


    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 24; // Adjust this value based on your texture
        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    public int getPotionColor() {
        ItemStack stack = this.blockEntity.getItem(0); // Adjust to the correct slot index
        if (stack.is(Items.POTION)) {
            int color = PotionUtils.getColor(stack);
            return color;
        }
        return 0xffffff;
    }
}