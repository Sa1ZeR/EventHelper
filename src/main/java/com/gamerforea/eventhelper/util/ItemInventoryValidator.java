package com.gamerforea.eventhelper.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ItemInventoryValidator {
	public static final String NBT_KEY_ID = "UID";
	private static final Random RANDOM = new Random();

	@Nonnull
	private final Item item;
	private final int uniqueId;

	@Nonnull
	private final String nbtIdKey;
	@Nullable
	private final Predicate<Item> itemValidator;
	@Nullable
	private final Function<Player, ItemStack> stackGetter;

	private boolean itemInHotbar = true;
	private int slotIndex = -1;
	private int slotId = -1;

	public ItemInventoryValidator(@Nullable ItemStack stack)
	{
		this(stack, null, true);
	}

	public ItemInventoryValidator(@Nullable ItemStack stack, @Nullable Predicate<Item> itemValidator)
	{
		this(stack, null, true, itemValidator);
	}

	public ItemInventoryValidator(@Nullable ItemStack stack, @Nullable Function<Player, ItemStack> stackGetter)
	{
		this(stack, null, true, stackGetter);
	}

	public ItemInventoryValidator(
			@Nullable ItemStack stack,
			@Nullable Predicate<Item> itemValidator, @Nullable Function<Player, ItemStack> stackGetter)
	{
		this(stack, null, true, itemValidator, stackGetter);
	}

	public ItemInventoryValidator(@Nullable ItemStack stack, @Nullable String nbtKeyId, boolean generateIdIfAbsent)
	{
		this(stack, nbtKeyId, generateIdIfAbsent, null, null);
	}

	public ItemInventoryValidator(
			@Nullable ItemStack stack,
			@Nullable String nbtKeyId, boolean generateIdIfAbsent, @Nullable Predicate<Item> itemValidator)
	{
		this(stack, nbtKeyId, generateIdIfAbsent, itemValidator, null);
	}

	public ItemInventoryValidator(
			@Nullable ItemStack stack,
			@Nullable String nbtKeyId, boolean generateIdIfAbsent,
			@Nullable Function<Player, ItemStack> stackGetter)
	{
		this(stack, nbtKeyId, generateIdIfAbsent, null, stackGetter);
	}

	public ItemInventoryValidator(
			@Nullable ItemStack stack,
			@Nullable String nbtKeyId, boolean generateIdIfAbsent,
			@Nullable Predicate<Item> itemValidator, @Nullable Function<Player, ItemStack> stackGetter)
	{
		this.item = stack == null ? Items.AIR : stack.getItem();
		int uniqueId = 0;
		this.nbtIdKey = nbtKeyId = StringUtils.defaultIfBlank(nbtKeyId, NBT_KEY_ID);
		this.itemValidator = itemValidator;
		this.stackGetter = stackGetter;

		if (stack != null && !stack.isEmpty() && (itemValidator == null || itemValidator.test(stack.getItem())))
		{
			CompoundTag nbt = stack.getTag();
			if (nbt == null && generateIdIfAbsent) {
				nbt = new CompoundTag();
				stack.setTag(nbt);
			}

			if (nbt != null) {
				if (nbt.hasUUID(nbtKeyId))
					uniqueId = nbt.getInt(nbtKeyId);
				else {
					uniqueId = RANDOM.nextInt();
					nbt.putInt(nbtKeyId, uniqueId);
				}
			}
		}

		this.uniqueId = uniqueId;
	}

	public boolean isItemInHotbar()
	{
		return this.itemInHotbar;
	}

	public void setItemInHotbar(boolean itemInHotbar)
	{
		this.itemInHotbar = itemInHotbar;
	}

	public int getSlotIndex()
	{
		return this.slotIndex;
	}

	public void setSlotIndex(int slotIndex)
	{
		this.slotIndex = slotIndex;
	}

	public void setSlotIndex(int slotIndex, boolean itemInHotbar)
	{
		this.setSlotIndex(slotIndex);
		this.setItemInHotbar(itemInHotbar);
	}

	public int getSlotId()
	{
		return this.slotId;
	}

	public void setSlotId(int slotId)
	{
		this.slotId = slotId;
	}

	public boolean tryGetSlotIdFromPlayerSlot(@Nonnull Slot slot)
	{
		if (this.slotIndex >= 0 && slot.container instanceof PlayerContainerEvent && slot.getSlotIndex() == this.slotIndex)
		{
			this.setSlotId(slot.index);
			return true;
		}
		return false;
	}

	public boolean canInteractWith(@Nonnull Player player)
	{
		if (this.itemInHotbar && this.slotIndex >= 0 && this.slotIndex != player.getInventory().selected)
			return false;

		if (this.item != Items.AIR)
		{
			ItemStack stackToCheck;
			if (this.stackGetter == null) {
				if (this.slotIndex < 0)
					return true;
				stackToCheck = player.getInventory().getItem(this.slotIndex);
			}
			else
				stackToCheck = this.stackGetter.apply(player);
			return stackToCheck != null && (this.itemValidator == null || this.itemValidator.test(stackToCheck.getItem())) && this.isSameItemInventory(stackToCheck);
		}

		return true;
	}

	public boolean canSlotClick(int slotId, int dragType, @Nonnull ClickType clickType, @Nonnull Player player) {
		if (this.slotId >= 0 && slotId == this.slotId)
			return false;
		if (clickType == ClickType.SWAP && this.itemInHotbar && this.slotIndex >= 0 && dragType == this.slotIndex)
			return false;
		return this.canInteractWith(player);
	}

	private boolean isSameItemInventory(@Nullable ItemStack comparison)
	{
		if (comparison == null || comparison.isEmpty())
			return false;
		if (this.item != comparison.getItem())
			return false;

		CompoundTag comparisonNbt = comparison.getTag();
		return comparisonNbt != null && this.uniqueId == comparisonNbt.getInt(this.nbtIdKey);
	}
}
