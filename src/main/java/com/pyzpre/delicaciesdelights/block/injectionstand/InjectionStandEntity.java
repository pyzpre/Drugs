package com.pyzpre.delicaciesdelights.block.injectionstand;

import com.pyzpre.delicaciesdelights.index.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
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
    private static final int INGREDIENT1_SLOT = 0; // For potions
    public static final int INGREDIENT2_SLOT = 1;  // For items receiving the potion
    public static final int BLAZE_POWDER_SLOT = 2;
    private int potionColor = 0xFFFFFF;
    private ItemStack storedPotion = ItemStack.EMPTY;
    private boolean potionNeedsConsumption = false;
    private boolean isConsumingPotion = false; // Flag to prevent recursion

    private static final Logger LOGGER = LogManager.getLogger();

    private final NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    private int processTime;
    private static final int PROCESS_TIME_TOTAL = 180;  // Example value, adjust as needed
    public boolean powered = false;
    public boolean blazePowderConsumed = false;
    public boolean blazePowderAdded = false;



    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            if (isConsumingPotion) {
                return; // Prevent recursion
            }

            setChanged();
            if (level != null && !level.isClientSide) {
                if (slot == BLAZE_POWDER_SLOT && !powered) {
                    ItemStack blazeStack = getStackInSlot(slot);
                    if (!blazeStack.isEmpty() && blazeStack.getCount() > 0) {
                        blazePowderAdded = true; // Indicate that blaze powder has been added
                    }
                }

                if (slot == INGREDIENT2_SLOT) {
                    ItemStack itemStack = getStackInSlot(slot);
                    if (itemStack.getCount() > 8) {
                        itemStack.setCount(8); // Reduce to max limit of 8 if exceeded
                    }
                }

                if (slot == INGREDIENT1_SLOT) {
                    ItemStack itemStack = getStackInSlot(slot);
                    if (!itemStack.isEmpty() && itemStack.is(Items.POTION)) {
                        // Consume the potion immediately
                        isConsumingPotion = true;
                        consumePotion();
                        isConsumingPotion = false;
                    } else {
                        // Reset storedPotion and potionColor if slot is empty or not a potion
                        InjectionStandEntity.this.storedPotion = ItemStack.EMPTY;
                        InjectionStandEntity.this.potionColor = 0xFFFFFF;
                    }

                    // Notify client of the change
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == BLAZE_POWDER_SLOT) {
                return stack.is(Items.BLAZE_POWDER);
            }
            if (slot == INGREDIENT1_SLOT) {
                // Only accept a potion if storedPotion is empty
                return InjectionStandEntity.this.storedPotion.isEmpty() && stack.is(Items.POTION);
            }
            if (slot == INGREDIENT2_SLOT) {
                // Accept the ingredient items and the result items
                return true; // Modify if you need to restrict items
            }
            return super.isItemValid(slot, stack);
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == INGREDIENT1_SLOT) {
                return 1; // Limit to 1 potion in the potion slot
            }
            if (slot == INGREDIENT2_SLOT) {
                return 8; // Limit to 8 items in the ingredient 2 slot
            }
            return super.getSlotLimit(slot);
        }
    };
    private final LazyOptional<IItemHandler> handlers = LazyOptional.of(() -> itemHandler);

    public InjectionStandEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.INJECTION_STAND.get(), pos, state);
    }
    public ItemStack getStoredPotion() {
        return storedPotion;
    }


    private void consumePotion() {
        ItemStack potionStack = itemHandler.getStackInSlot(INGREDIENT1_SLOT);
        if (!potionStack.isEmpty() && potionStack.is(Items.POTION)) {
            // Store the potion's data
            this.storedPotion = potionStack.copy(); // Keep a copy of the potion
            this.potionColor = PotionUtils.getColor(potionStack);

            // Remove the potion from the slot
            itemHandler.setStackInSlot(INGREDIENT1_SLOT, ItemStack.EMPTY);

            // Notify client of the change
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        } else {
            // Reset storedPotion and potionColor if no potion is found
            this.storedPotion = ItemStack.EMPTY;
            this.potionColor = 0xFFFFFF;
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, InjectionStandEntity blockEntity) {
        // No need for potionNeedsConsumption flag anymore
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
                    blockEntity.craftItem();
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

    // Method to handle blaze powder consumption and powering on
    private void consumeBlazePowder() {
        ItemStack blazeStack = itemHandler.getStackInSlot(BLAZE_POWDER_SLOT);
        if (!blazeStack.isEmpty() && blazeStack.getCount() > 0) {
            blazeStack.shrink(1); // Consume one blaze powder
            blazePowderConsumed = true;
            blazePowderAdded = false; // Reset the flag
            setPowered(true); // Power on the stand

            // Notify the client of the change
            setChanged();
            if (this.level != null) {
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
            }
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.handleUpdateTag(pkt.getTag());
    }

    private boolean hasRecipe() {
        Level level = this.level;
        if (level == null) {
            return false;
        }
        Optional<InjectionRecipe> match = level.getRecipeManager().getRecipeFor(InjectionRecipeType.INSTANCE, this, level);
        boolean hasIngredients = !itemHandler.getStackInSlot(INGREDIENT2_SLOT).isEmpty() && !storedPotion.isEmpty();
        boolean validRecipe = match.isPresent();

        // Remove or adjust the canOutput check
        return validRecipe && hasIngredients;
    }





    private void craftItem() {
        Level level = this.level;
        if (level == null || !powered) return;

        Optional<InjectionRecipe> match = level.getRecipeManager().getRecipeFor(InjectionRecipeType.INSTANCE, this, level);
        if (match.isPresent()) {
            InjectionRecipe recipe = match.get();

            ItemStack ingredient2Stack = itemHandler.getStackInSlot(INGREDIENT2_SLOT);
            int ingredient2Count = ingredient2Stack.getCount();

            // Determine how many items to process, capped at 8
            int processCount = Math.min(8, ingredient2Count);

            // Ensure that processCount is greater than 0 before proceeding
            if (processCount > 0) {
                // Get the result for the given count of ingredients
                ItemStack resultStack = recipe.assembleWithCount(this, level.registryAccess(), processCount);

                // Remove the ingredients
                itemHandler.extractItem(INGREDIENT2_SLOT, processCount, false);

                // Place the result back into INGREDIENT2_SLOT
                ItemStack existingStack = itemHandler.getStackInSlot(INGREDIENT2_SLOT);

                if (existingStack.isEmpty()) {
                    itemHandler.setStackInSlot(INGREDIENT2_SLOT, resultStack);
                } else if (ItemStack.isSameItemSameTags(existingStack, resultStack)) {
                    int newCount = existingStack.getCount() + resultStack.getCount();
                    if (newCount <= existingStack.getMaxStackSize()) {
                        existingStack.setCount(newCount);
                        itemHandler.setStackInSlot(INGREDIENT2_SLOT, existingStack);
                    } else {
                        // Exceeds max stack size, drop the excess in the world
                        existingStack.setCount(existingStack.getMaxStackSize());
                        itemHandler.setStackInSlot(INGREDIENT2_SLOT, existingStack);

                        int excess = newCount - existingStack.getMaxStackSize();
                        ItemStack excessStack = resultStack.copy();
                        excessStack.setCount(excess);
                        Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), excessStack);
                    }
                } else {
                    // INGREDIENT2_SLOT contains a different item, drop the result in the world
                    Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), resultStack);
                }

                // After crafting, consume the stored potion
                this.storedPotion = ItemStack.EMPTY;
                this.potionColor = 0xFFFFFF;

                // Notify client of the change
                setChanged();
                if (level != null && !level.isClientSide) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }

                // Turn off the powered state when crafting completes
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

        // Notify the client of the change
        setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public boolean isPowered() {
        return powered;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        this.processTime = tag.getInt("ProcessTime");
        this.powered = tag.getBoolean("Powered");
        this.blazePowderConsumed = tag.getBoolean("BlazePowderConsumed");
        this.potionColor = tag.getInt("PotionColor");

        if (tag.contains("StoredPotion")) {
            this.storedPotion = ItemStack.of(tag.getCompound("StoredPotion"));
        } else {
            this.storedPotion = ItemStack.EMPTY;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", itemHandler.serializeNBT());
        tag.putInt("ProcessTime", this.processTime);
        tag.putBoolean("Powered", this.powered);
        tag.putBoolean("BlazePowderConsumed", this.blazePowderConsumed);
        tag.putInt("PotionColor", this.potionColor);

        if (!storedPotion.isEmpty()) {
            CompoundTag potionTag = new CompoundTag();
            storedPotion.save(potionTag);
            tag.put("StoredPotion", potionTag);
        }
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
                    return InjectionStandEntity.this.isPowered() ? 1 : 0;
                case 3:
                    return InjectionStandEntity.this.blazePowderConsumed ? 1 : 0;
                case 4:
                    return InjectionStandEntity.this.potionColor;
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
            } else if (index == 3) {
                InjectionStandEntity.this.blazePowderConsumed = value == 1;
            } else if (index == 4) {
                InjectionStandEntity.this.potionColor = value;
            }
        }

        @Override
        public int getCount() {
            return 5; // Updated to include the potion color
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

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        this.saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.load(tag);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
}
