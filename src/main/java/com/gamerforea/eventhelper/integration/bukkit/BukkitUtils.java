package com.gamerforea.eventhelper.integration.bukkit;

import com.gamerforea.eventhelper.EventHelperMod;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public final class BukkitUtils
{
	private static final Method getBukkitEntity;

	public static Player getPlayer(@Nonnull net.minecraft.world.entity.player.Player player)
	{
		//return (Player) getEntity(player);
		return Bukkit.getPlayerExact(player.getName().getString());
	}

	@Nonnull
	public static org.bukkit.entity.Entity getEntity(@Nonnull Entity entity)
	{
		try
		{
			return (org.bukkit.entity.Entity) Objects.requireNonNull(getBukkitEntity.invoke(entity), "Entity.getBukkitEntity() result must not be null");
		}
		catch (IllegalAccessException | InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Nonnull
	public static BlockFace getBlockFace(@Nonnull Direction side)
	{
		return switch (side) {
			case DOWN -> BlockFace.DOWN;
			case UP -> BlockFace.UP;
			case NORTH -> BlockFace.NORTH;
			case SOUTH -> BlockFace.SOUTH;
			case WEST -> BlockFace.WEST;
			case EAST -> BlockFace.EAST;
			default -> BlockFace.SELF;
		};
	}

	static
	{
		Method getBukkitEntityMethod = null;
		try
		{
			getBukkitEntityMethod = Entity.class.getDeclaredMethod("getBukkitEntity");
			getBukkitEntityMethod.setAccessible(true);
		}
		catch (Throwable throwable)
		{
			EventHelperMod.LOGGER.warn("Failed hooking CraftBukkit methods", throwable);
		}
		getBukkitEntity = getBukkitEntityMethod;
	}
}
