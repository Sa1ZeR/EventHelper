package com.gamerforea.eventhelper.integration.bukkit;

import com.gamerforea.eventhelper.EventHelperMod;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import net.minecraft.world.entity.Entity;

public final class BukkitUtils
{
	private static final Method getBukkitEntity;

	@Nonnull
	public static Player getPlayer(@Nonnull ServerPlayer player)
	{
		return (Player) getEntity(player);
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
