package com.grim3212.assorted.decor.common.inventory;

import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.crafting.DecorRecipeTypes;
import com.grim3212.assorted.decor.common.crafting.LumberMillRecipe;
import com.grim3212.assorted.lib.crafting.SyncedRecipes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * The stonecutter's menu, reading lumber mill recipes. Vanilla's reads its list from a set only the
 * stonecutter is given, so this lists them itself, in the same order on both sides.
 */
public class LumberMillMenu extends AbstractContainerMenu {

    private final ContainerLevelAccess access;
    private final DataSlot selectedRecipeIndex = DataSlot.standalone();
    private final Level level;
    private List<RecipeHolder<LumberMillRecipe>> recipesForInput = List.of();
    private ItemStack input = ItemStack.EMPTY;
    private long lastSoundTime;
    private final Slot inputSlot;
    private final Slot resultSlot;
    private Runnable slotUpdateListener = () -> {
    };
    public final Container container = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            LumberMillMenu.this.slotsChanged(this);
            LumberMillMenu.this.slotUpdateListener.run();
        }
    };
    private final ResultContainer resultContainer = new ResultContainer();

    public LumberMillMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public LumberMillMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(DecorContainerTypes.LUMBER_MILL.get(), containerId);
        this.access = access;
        this.level = inventory.player.level();
        this.inputSlot = this.addSlot(new Slot(this.container, 0, 20, 33));
        this.resultSlot = this.addSlot(new Slot(this.resultContainer, 1, 143, 33) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack carried) {
                carried.onCraftedBy(player, carried.getCount());
                LumberMillMenu.this.resultContainer.awardUsedRecipes(player, List.of(LumberMillMenu.this.inputSlot.getItem()));
                ItemStack remaining = LumberMillMenu.this.inputSlot.remove(1);
                if (!remaining.isEmpty()) {
                    LumberMillMenu.this.setupResultSlot(LumberMillMenu.this.selectedRecipeIndex.get());
                }

                access.execute((level, pos) -> {
                    long gameTime = level.getGameTime();
                    if (LumberMillMenu.this.lastSoundTime != gameTime) {
                        level.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                        LumberMillMenu.this.lastSoundTime = gameTime;
                    }
                });
                super.onTake(player, carried);
            }
        });
        this.addStandardInventorySlots(inventory, 8, 84);
        this.addDataSlot(this.selectedRecipeIndex);
    }

    public int getSelectedRecipeIndex() {
        return this.selectedRecipeIndex.get();
    }

    public List<RecipeHolder<LumberMillRecipe>> getVisibleRecipes() {
        return this.recipesForInput;
    }

    public int getNumberOfVisibleRecipes() {
        return this.recipesForInput.size();
    }

    public boolean hasInputItem() {
        return this.inputSlot.hasItem() && !this.recipesForInput.isEmpty();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, DecorBlocks.LUMBER_MILL.get());
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (this.selectedRecipeIndex.get() == buttonId) {
            return false;
        }

        if (buttonId >= 0 && buttonId < this.recipesForInput.size()) {
            this.selectedRecipeIndex.set(buttonId);
            this.setupResultSlot(buttonId);
        }

        return true;
    }

    @Override
    public void slotsChanged(Container container) {
        ItemStack input = this.inputSlot.getItem();
        if (!input.is(this.input.getItem())) {
            this.input = input.copy();
            this.selectedRecipeIndex.set(-1);
            this.resultSlot.set(ItemStack.EMPTY);
            this.recipesForInput = input.isEmpty() ? List.of() : recipes(this.level).stream()
                    .filter(recipe -> recipe.value().input().test(input))
                    .sorted(Comparator.comparing(recipe -> recipe.id().identifier()))
                    .toList();
        }
    }

    private void setupResultSlot(int index) {
        if (index >= 0 && index < this.recipesForInput.size()) {
            RecipeHolder<LumberMillRecipe> recipe = this.recipesForInput.get(index);
            this.resultContainer.setRecipeUsed(recipe);
            this.resultSlot.set(recipe.value().assemble(new SingleRecipeInput(this.container.getItem(0))));
        } else {
            this.resultSlot.set(ItemStack.EMPTY);
            this.resultContainer.setRecipeUsed(null);
        }
        this.broadcastChanges();
    }

    /** The server's recipes, or on the client the copy the server sent it. */
    @SuppressWarnings("unchecked")
    private static Collection<RecipeHolder<LumberMillRecipe>> recipes(Level level) {
        if (level instanceof ServerLevel server) {
            return server.recipeAccess().getRecipes().stream().filter(recipe -> recipe.value().getType() == DecorRecipeTypes.LUMBER_MILL.get())
                    .map(recipe -> (RecipeHolder<LumberMillRecipe>) recipe).toList();
        }
        return SyncedRecipes.byType(DecorRecipeTypes.LUMBER_MILL.get());
    }

    public void registerUpdateListener(Runnable slotUpdateListener) {
        this.slotUpdateListener = slotUpdateListener;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack carried, Slot target) {
        return target.container != this.resultContainer && super.canTakeItemForPickAll(carried, target);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack clicked = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            clicked = stack.copy();
            if (slotIndex == 1) {
                stack.getItem().onCraftedBy(stack, player);
                if (!this.moveItemStackTo(stack, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, clicked);
            } else if (slotIndex == 0) {
                if (!this.moveItemStackTo(stack, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (recipes(this.level).stream().anyMatch(recipe -> recipe.value().input().test(stack))) {
                if (!this.moveItemStackTo(stack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex < 29) {
                if (!this.moveItemStackTo(stack, 29, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex < 38 && !this.moveItemStackTo(stack, 2, 29, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (stack.getCount() == clicked.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
            if (slotIndex == 1) {
                player.drop(stack, false);
            }

            this.broadcastChanges();
        }

        return clicked;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.resultContainer.removeItemNoUpdate(1);
        this.access.execute((level, pos) -> this.clearContainer(player, this.container));
    }
}
