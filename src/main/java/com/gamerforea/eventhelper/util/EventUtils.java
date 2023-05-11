package com.gamerforea.eventhelper.util;

import com.gamerforea.eventhelper.EventHelperMod;
import com.gamerforea.eventhelper.cause.DummyCauseStackManager;
import com.gamerforea.eventhelper.cause.ICauseStackManager;
import com.gamerforea.eventhelper.integration.IIntegration;
import com.gamerforea.eventhelper.integration.bukkit.BukkitIntegration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.extensions.IForgeBlockState;

import javax.annotation.Nonnull;
import java.util.UUID;

import static com.gamerforea.eventhelper.EventHelperMod.LOGGER;

public final class EventUtils
{
	private static final IIntegration INTEGRATION;

	public static boolean cantBreak(@Nonnull Player player, @Nonnull BlockPos pos)
	{
		try
		{
			return INTEGRATION.cantBreak(player, pos);
		}
		catch (Throwable throwable)
		{
			LOGGER.error("Failed call break block event: [Player: {}, Pos: {}]", player, pos);
			if (EventHelperMod.debug)
				throwable.printStackTrace();
			return true;
		}
	}

	public static boolean cantPlace(
			@Nonnull Player player, @Nonnull BlockPos pos, @Nonnull IForgeBlockState blockState)
	{
		try
		{
			return INTEGRATION.cantPlace(player, pos, blockState);
		}
		catch (Throwable throwable)
		{
			LOGGER.error("Failed call place block event: [Player: {}, Pos: {}, Block State: {}]", player, pos, blockState);
			if (EventHelperMod.debug)
				throwable.printStackTrace();
			return true;
		}
	}

	public static boolean cantReplace(
			@Nonnull Player player, @Nonnull BlockPos pos, @Nonnull IForgeBlockState blockState)
	{
		try
		{
			return INTEGRATION.cantReplace(player, pos, blockState);
		}
		catch (Throwable throwable)
		{
			LOGGER.error("Failed call replace block event: [Player: {}, Pos: {}, Block State: {}]", player, pos, blockState);
			if (EventHelperMod.debug)
				throwable.printStackTrace();
			return true;
		}
	}

	public static boolean cantAttack(@Nonnull Player player, @Nonnull Entity victim)
	{
		try
		{
			return INTEGRATION.cantAttack(player, victim);
		}
		catch (Throwable throwable)
		{
			LOGGER.error("Failed call attack entity event: [Player: {}, Victim: {}]", player, victim);
			if (EventHelperMod.debug)
				throwable.printStackTrace();
			return true;
		}
	}

	public static boolean cantInteract(
			@Nonnull Player player,
			@Nonnull InteractionHand hand, @Nonnull BlockPos targetPos, @Nonnull Direction targetSide)
	{
		try
		{
			return INTEGRATION.cantInteract(player, hand, targetPos, targetSide);
		}
		catch (Throwable throwable)
		{
			LOGGER.error("Failed call interact event: [Player: {}, Hand: {}, Pos: {}, Side: {}]", player, hand, targetPos, targetSide);
			if (EventHelperMod.debug)
				throwable.printStackTrace();
			return true;
		}
	}

	public static boolean cantInteract(
			@Nonnull Player player,
			@Nonnull InteractionHand hand,
			@Nonnull BlockPos interactionPos, @Nonnull BlockPos targetPos, @Nonnull Direction targetSide)
	{
		try
		{
			return INTEGRATION.cantInteract(player, hand, interactionPos, targetPos, targetSide);
		}
		catch (Throwable throwable)
		{
			LOGGER.error("Failed call interact event: [Player: {}, Hand: {}, Pos: {}, Side: {}]", player, hand, targetPos, targetSide);
			if (EventHelperMod.debug)
				throwable.printStackTrace();
			return true;
		}
	}

	public static boolean cantInteract(@Nonnull Player player, @Nonnull IIntegration.BlockInteractParams params)
	{
		try
		{
			return INTEGRATION.cantInteract(player, params);
		}
		catch (Throwable throwable)
		{
			LOGGER.error("Failed call interact event: [Player: {}, Params: {}]", player, params);
			if (EventHelperMod.debug)
				throwable.printStackTrace();
			return true;
		}
	}

	public static boolean hasPermission(@Nonnull Player player, @Nonnull String permission)
	{
		try
		{
			return INTEGRATION.hasPermission(player, permission);
		}
		catch (Throwable throwable)
		{
			LOGGER.error("Failed checking permission: [Player: {}, Permission: {}]", player, permission);
			if (EventHelperMod.debug)
				throwable.printStackTrace();
			return false;
		}
	}

	public static boolean hasPermission(@Nonnull UUID playerId, @Nonnull String permission)
	{
		try
		{
			return INTEGRATION.hasPermission(playerId, permission);
		}
		catch (Throwable throwable)
		{
			LOGGER.error("Failed checking permission: [Player name: {}, Permission: {}]", playerId, permission);
			if (EventHelperMod.debug)
				throwable.printStackTrace();
			return false;
		}
	}

	public static boolean hasPermission(@Nonnull String playerName, @Nonnull String permission)
	{
		try
		{
			return INTEGRATION.hasPermission(playerName, permission);
		}
		catch (Throwable throwable)
		{
			LOGGER.error("Failed checking permission: [Player UUID: {}, Permission: {}]", playerName, permission);
			if (EventHelperMod.debug)
				throwable.printStackTrace();
			return false;
		}
	}

	public static ICauseStackManager getCauseStackManager()
	{
		ICauseStackManager specificCauseStackManager = INTEGRATION.getSpecificCauseStackManager();
		return specificCauseStackManager == null ? DummyCauseStackManager.INSTANCE : specificCauseStackManager;
	}

	static {
		INTEGRATION = BukkitIntegration.getIntegration();
	}
}