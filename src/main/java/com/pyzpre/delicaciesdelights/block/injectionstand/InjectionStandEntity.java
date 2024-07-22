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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;

public class InjectionStandEntity extends BlockEntity implements Container, MenuProvider {
    private static final int INGREDIENT1_SLOT = 0;
    private static final int INGREDIENT2_SLOT = 1;
    private static final int RESULT_SLOT = 2;

    private final NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    private int processTime;
    private static final int PROCESS_TIME_TOTAL = 100;  // Example value, adjust as needed
    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private final LazyOptional<IItemHandler> handlers = LazyOptional.of(() -> itemHandler);
    private final ContainerData dataAccess;

    public InjectionStandEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.INJECTION_STAND.get(), pos, state);
        this.dataAccess = new ContainerData() {
            @Override
            public int get(int index) {
                switch (index) {
                    case 0:
                        return InjectionStandEntity.this.processTime;
                    case 1:
                        return PROCESS_TIME_TOTAL;
                    default:
                        return 0;
                }
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) {
                    InjectionStandEntity.this.processTime = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, InjectionStandEntity blockEntity) {
        if (blockEntity.hasRecipe()) {
            blockEntity.processTime++;
            if (blockEntity.processTime >= PROCESS_TIME_TOTAL) {
                blockEntity.processTime = 0;
                blockEntity.craftItem();
            }
        } else {
            blockEntity.processTime = 0;
        }
    }

    private boolean hasRecipe() {
        Level level = this.level;
        if (level == null) {
            return false;
        }

        Optional<InjectionRecipe> match = level.getRecipeManager().getRecipeFor(InjectionRecipeType.INSTANCE, this, level);
        if (match.isPresent()) {
            ItemStack resultItem = match.get().getResultItem(level.registryAccess());
            boolean canInsert = canInsertItemIntoOutputSlot(resultItem);
            return canInsert;
        } else {
            return false;
        }
    }

    private void craftItem() {
        Level level = this.level;
        if (level == null) return;
        Optional<InjectionRecipe> match = level.getRecipeManager().getRecipeFor(InjectionRecipeType.INSTANCE, this, level);
        if (match.isPresent()) {
            ItemStack result = match.get().assemble(this, level.registryAccess());
            itemHandler.extractItem(INGREDIENT1_SLOT, 1, false);
            itemHandler.extractItem(INGREDIENT2_SLOT, 1, false);
            if (itemHandler.getStackInSlot(RESULT_SLOT).isEmpty()) {
                itemHandler.setStackInSlot(RESULT_SLOT, result);
            } else {
                itemHandler.getStackInSlot(RESULT_SLOT).grow(result.getCount());
            }
        }
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack stack) {
        boolean canInsert = itemHandler.getStackInSlot(RESULT_SLOT).isEmpty() || (itemHandler.getStackInSlot(RESULT_SLOT).getItem() == stack.getItem() && itemHandler.getStackInSlot(RESULT_SLOT).getCount() + stack.getCount() <= itemHandler.getStackInSlot(RESULT_SLOT).getMaxStackSize());
        return canInsert;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, this.items);
        itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        this.processTime = tag.getInt("ProcessTime");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items);
        tag.put("Inventory", itemHandler.serializeNBT());
        tag.putInt("ProcessTime", this.processTime);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Injection Stand");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new InjectionStandContainer(id, playerInventory, this, this.dataAccess);
    }
    public int getProcessTime() {
        return processTime;
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
