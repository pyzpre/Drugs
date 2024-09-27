package com.pyzpre.delicaciesdelights.block.injectionstand;

import com.pyzpre.delicaciesdelights.index.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;

public class InjectionStandEntity extends BlockEntity implements Container, MenuProvider {
    private static final int INGREDIENT1_SLOT = 0;
    public static final int INGREDIENT2_SLOT = 1;
    public static final int BLAZE_POWDER_SLOT = 2;

    private static final Logger LOGGER = LogManager.getLogger(); // Logger declaration

    private final NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    private int processTime;
    private static final int PROCESS_TIME_TOTAL = 100;  // Example value, adjust as needed
    private boolean powered = false; // Track if the stand is powered
    private boolean blazePowderConsumed = false; // Track if blaze powder has been consumed
    public boolean blazePowderAdded = false; // New flag to indicate blaze powder was added

    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (slot == BLAZE_POWDER_SLOT && !powered) {
                ItemStack blazeStack = getStackInSlot(slot);
                if (!blazeStack.isEmpty() && blazeStack.getCount() > 0) {
                    blazePowderAdded = true; // Indicate that blaze powder has been added

                }
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            // Only allow blaze powder in the blaze powder slot
            if (slot == BLAZE_POWDER_SLOT) {
                return stack.is(Items.BLAZE_POWDER); // Allow only blaze powder
            }
            return true; // Allow any item in other slots
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == INGREDIENT1_SLOT || slot == INGREDIENT2_SLOT) {
                return 8; // Limit to 8 items in each ingredient slot
            }
            return super.getSlotLimit(slot);
        }
    };

    private final LazyOptional<IItemHandler> handlers = LazyOptional.of(() -> itemHandler);

    public InjectionStandEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.INJECTION_STAND.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, InjectionStandEntity blockEntity) {
        if (blockEntity.isPowered()) {
            // Consume blaze powder if it hasn't been consumed yet
            if (blockEntity.blazePowderAdded && !blockEntity.blazePowderConsumed) {
                ItemStack blazeStack = blockEntity.itemHandler.getStackInSlot(BLAZE_POWDER_SLOT);
                if (!blazeStack.isEmpty() && blazeStack.getCount() > 0) {
                    blazeStack.shrink(1); // Consume one blaze powder
                    blockEntity.blazePowderConsumed = true;
                    blockEntity.blazePowderAdded = false; // Reset the flag
                    blockEntity.setChanged();

                } else {
                    // No blaze powder to consume, power off
                    blockEntity.setPowered(false);

                }
            }

            if (blockEntity.hasRecipe()) {
                // If the stand is powered and has a recipe, start processing
                blockEntity.processTime++;


                if (blockEntity.processTime >= PROCESS_TIME_TOTAL) {
                    blockEntity.processTime = 0;
                    blockEntity.craftItem(); // This will handle setting powered state to false when crafting completes
                }
            } else {
                // If not processing, reset process time
                blockEntity.processTime = 0;
            }
        } else {
            // Check if blaze powder is available to consume after powering off
            if (!blockEntity.blazePowderConsumed && !blockEntity.itemHandler.getStackInSlot(BLAZE_POWDER_SLOT).isEmpty()) {
                blockEntity.consumeBlazePowder(); // Re-consume blaze powder and power on
            }
        }
    }

    // New method to handle blaze powder consumption and powering on
    private void consumeBlazePowder() {
        ItemStack blazeStack = itemHandler.getStackInSlot(BLAZE_POWDER_SLOT);
        if (!blazeStack.isEmpty() && blazeStack.getCount() > 0) {
            blazeStack.shrink(1); // Consume one blaze powder
            blazePowderConsumed = true;
            blazePowderAdded = false; // Reset the flag
            setPowered(true); // Power on the stand
            setChanged(); // Mark as changed for saving

        }
    }

    private boolean hasRecipe() {
        Level level = this.level;
        if (level == null) {
            return false;
        }
        Optional<InjectionRecipe> match = level.getRecipeManager().getRecipeFor(InjectionRecipeType.INSTANCE, this, level);
        boolean hasIngredients = !itemHandler.getStackInSlot(INGREDIENT1_SLOT).isEmpty() && !itemHandler.getStackInSlot(INGREDIENT2_SLOT).isEmpty();
        boolean validRecipe = match.isPresent();
        return validRecipe && hasIngredients;
    }

    private void craftItem() {
        Level level = this.level;
        if (level == null || !powered) return;

        Optional<InjectionRecipe> match = level.getRecipeManager().getRecipeFor(InjectionRecipeType.INSTANCE, this, level);
        if (match.isPresent()) {
            InjectionRecipe recipe = match.get();

            // Get the number of items in the ingredient slots
            int ingredient1Count = itemHandler.getStackInSlot(INGREDIENT1_SLOT).getCount();
            int ingredient2Count = itemHandler.getStackInSlot(INGREDIENT2_SLOT).getCount();

            // Determine how many items to process, capped at 8
            int processCount = Math.min(8, ingredient2Count);

            // Ensure that processCount is greater than 0 before proceeding
            if (processCount > 0) {
                // Get the result for the given count of ingredients
                ItemStack resultStack = recipe.assembleWithCount(this, level.registryAccess(), processCount);

                // Remove the ingredients
                itemHandler.extractItem(INGREDIENT1_SLOT, processCount, false);
                itemHandler.extractItem(INGREDIENT2_SLOT, processCount, false);

                // Add result to INGREDIENT2_SLOT
                itemHandler.setStackInSlot(INGREDIENT2_SLOT, resultStack);


                // Turn off the powered state when result is inserted
                setPowered(false);
                blazePowderConsumed = false; // Reset the flag

            }
        }
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
        if (!powered) {
            blazePowderConsumed = false; // Reset when powered off
            blazePowderAdded = false;    // Reset the addition flag
        }


        // Check for blaze powder if powered off to re-power on automatically
        if (!powered && !itemHandler.getStackInSlot(BLAZE_POWDER_SLOT).isEmpty()) {
            consumeBlazePowder(); // Re-consume blaze powder and power on
        }
    }

    public boolean isPowered() {
        return powered;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, this.items);
        itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        this.processTime = tag.getInt("ProcessTime");
        this.powered = tag.getBoolean("Powered");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items);
        tag.put("Inventory", itemHandler.serializeNBT());
        tag.putInt("ProcessTime", this.processTime);
        tag.putBoolean("Powered", this.powered);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Injection Stand");
    }
    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return InjectionStandEntity.this.processTime;
                case 1:
                    return PROCESS_TIME_TOTAL;
                case 2:
                    return InjectionStandEntity.this.isPowered() ? 1 : 0; // Check powered state
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                InjectionStandEntity.this.processTime = value;
            } else if (index == 2) {
                InjectionStandEntity.this.setPowered(value == 1);
            }
        }

        @Override
        public int getCount() {
            return 3; // Update to 3 to match the number of data values
        }
    };


    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new InjectionStandContainer(id, playerInventory, this, this.dataAccess);
    }

    public ContainerData getContainerData() {
        return this.dataAccess;
    }


    public int getProcessTime() {
        return this.processTime;
    }

    public int getProcessTimeTotal() {
        return PROCESS_TIME_TOTAL;
    }
    @Override
    public int getContainerSize() {
        return itemHandler.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return itemHandler.getStackInSlot(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return itemHandler.extractItem(index, count, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return itemHandler.extractItem(index, itemHandler.getStackInSlot(index).getCount(), false);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        itemHandler.setStackInSlot(index, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return handlers.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        handlers.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(items.size());
        for (int i = 0; i < items.size(); i++) {
            inventory.setItem(i, items.get(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
}