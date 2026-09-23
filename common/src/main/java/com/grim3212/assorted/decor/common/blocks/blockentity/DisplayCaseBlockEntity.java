package com.grim3212.assorted.decor.common.blocks.blockentity;

import com.grim3212.assorted.decor.common.blocks.DisplayCaseBlock;
import com.grim3212.assorted.lib.core.inventory.IInventoryBlockEntity;
import com.grim3212.assorted.lib.core.inventory.IPlatformInventoryStorageHandler;
import com.grim3212.assorted.lib.core.inventory.impl.ItemStackStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

/** The nine slots every display case shares. No menu, and a slot never holds more than one item. */
public class DisplayCaseBlockEntity extends BlockEntity implements IInventoryBlockEntity, Nameable {

    private Component customName;

    protected IPlatformInventoryStorageHandler platformInventoryStorageHandler;
    private final ItemStackStorageHandler storageHandler;

    public DisplayCaseBlockEntity(BlockPos pos, BlockState state) {
        this(DecorBlockEntityTypes.DISPLAY_CASE.get(), pos, state);
    }

    public DisplayCaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        this.storageHandler = new ItemStackStorageHandler(DisplayCaseBlock.SLOTS) {
            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Override
            public void onContentsChanged(int slot) {
                DisplayCaseBlockEntity.this.setChanged();
            }
        };
    }

    @Override
    public IPlatformInventoryStorageHandler getStorageHandler() {
        if (this.platformInventoryStorageHandler == null) {
            this.platformInventoryStorageHandler = this.createStorageHandler();
        }

        return this.platformInventoryStorageHandler;
    }

    public IPlatformInventoryStorageHandler createStorageHandler() {
        return Services.INVENTORY.createStorageInventoryHandler(this.storageHandler);
    }

    public ItemStackStorageHandler getItemStackStorageHandler() {
        return this.storageHandler;
    }

    public ItemStack getItem(int slot) {
        return this.storageHandler.getStackInSlot(slot);
    }

    public void setItem(int slot, ItemStack stack) {
        this.storageHandler.setStackInSlot(slot, stack);
    }

    public ItemStack removeItem(int slot) {
        return this.storageHandler.extractItem(slot, 1, false);
    }

    /** Drops what a case of {@code size} cannot show. Indices are stable, so growing it back finds the rest. */
    public void dropSlotsBeyond(int size) {
        if (this.level == null) {
            return;
        }

        for (int slot = 0; slot < DisplayCaseBlock.SLOTS; slot++) {
            if (DisplayCaseBlock.isShown(size, slot) || this.getItem(slot).isEmpty()) {
                continue;
            }

            Containers.dropItemStack(this.level, this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5D,
                    this.storageHandler.extractItem(slot, 1, false));
        }
    }

    public void setCustomName(@Nullable Component name) {
        this.customName = name;
        this.setChanged();
    }

    @Override
    public Component getName() {
        return this.customName != null ? this.customName : this.getBlockState().getBlock().getName();
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Override
    @Nullable
    public Component getCustomName() {
        return this.customName;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.platformInventoryStorageHandler != null) {
            this.platformInventoryStorageHandler.invalidate();
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.storageHandler.deserialize(input.childOrEmpty("Inventory"));
        this.customName = parseCustomNameSafe(input, "CustomName");
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        this.storageHandler.serialize(output.child("Inventory"));
        output.storeNullable("CustomName", ComponentSerialization.CODEC, this.customName);
    }

    /** Here rather than {@code affectNeighborsAfterRemoval}, which runs after the block entity is gone. */
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);

        if (this.level != null) {
            StorageUtil.dropContents(this.level, pos, this.storageHandler);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setChanged() {
        super.setChanged();

        // The contents are what the block draws, so every change has to reach the client.
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }
}
