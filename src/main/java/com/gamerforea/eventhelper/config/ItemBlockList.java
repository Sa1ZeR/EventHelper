package com.gamerforea.eventhelper.config;

import com.gamerforea.eventhelper.EventHelperMod;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.IntSupplier;

public final class ItemBlockList
{
	private static final String[] DEFAULT_VALUES = { "minecraft:bedrock", "modid:block_name@meta" };
	private static final char SEPARATOR = '@';
	private static final int ALL_META = -1;

	private final Set<String> rawSet = new HashSet<>();
	private final Map<Item, IntSet> items = new HashMap<>();
	private final Map<Block, IntSet> blocks = new HashMap<>();
	private boolean loaded = true;

	public ItemBlockList()
	{
		this(false);
	}

	public ItemBlockList(boolean initWithDefaultValues)
	{
		if (initWithDefaultValues)
			this.addRaw(Arrays.asList(DEFAULT_VALUES));
	}

	public void clear()
	{
		this.loaded = true;
		this.items.clear();
		this.blocks.clear();
		this.rawSet.clear();
	}

	public Set<String> getRaw()
	{
		return Collections.unmodifiableSet(this.rawSet);
	}

	public void addRaw(@Nonnull Collection<String> strings)
	{
		this.loaded = false;
		this.items.clear();
		this.blocks.clear();
		this.rawSet.addAll(strings);
	}

	public boolean isEmpty()
	{
		this.load();
		return this.items.isEmpty() && this.blocks.isEmpty();
	}

	public boolean contains(@Nullable ItemStack stack)
	{
		return stack != null && this.contains(stack.getItem(), stack.getDamageValue());
	}

	public boolean contains(@Nonnull Item item, int meta)
	{
		this.load();
		return item instanceof BlockItem && this.contains(((BlockItem) item).getBlock(), meta) || contains(this.items, item, meta);
	}

	public boolean contains(@Nonnull Block block, int meta)
	{
		this.load();
		return contains(this.blocks, block, meta);
	}

	private void load() {
		if (!this.loaded) {
			this.loaded = true;
			for (String s : this.rawSet) {
				s = s.trim();
				if (!s.isEmpty()) {
					String[] parts = StringUtils.split(s, '@');
					if (parts != null && parts.length > 0) {
						String name = parts[0];
						int meta = (parts.length > 1) ? safeParseInt(parts[1]) : -1;
						ResourceLocation resourceLocation = new ResourceLocation(name);
						Item item = (Item)ForgeRegistries.ITEMS.getValue(resourceLocation);
						if (item != null)
							put(this.items, item, meta);
						Block block = ForgeRegistries.BLOCKS.getValue(resourceLocation);
						if (block != Blocks.AIR)
							put(this.blocks, block, meta);
						if (EventHelperMod.debug && item == null && block == Blocks.AIR)
							EventHelperMod.LOGGER.warn("Item/block {} not found", resourceLocation);
					}
				}
			}
		}
	}

	private static <K> boolean put(Map<K, IntSet> map, K key, int value)
	{
		IntSet set = map.get(key);
		if (set == null)
			map.put(key, set = new IntOpenHashSet());
		return set.add(value);
	}

	private static <K> boolean contains(Map<K, IntSet> map, K key, int value)
	{
		IntSet set = map.get(key);
		return set != null && (set.contains(ALL_META) || set.contains(value));
	}

	private static <K> boolean contains(Map<K, IntSet> map, K key, IntSupplier valueSupplier)
	{
		IntSet set = map.get(key);
		return set != null && (set.contains(ALL_META) || set.contains(valueSupplier.getAsInt()));
	}

	private static int safeParseInt(String s)
	{
		try
		{
			return Integer.parseInt(s);
		}
		catch (Throwable throwable)
		{
			return ALL_META;
		}
	}
}
