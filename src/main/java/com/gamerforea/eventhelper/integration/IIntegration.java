package com.gamerforea.eventhelper.integration;

import com.gamerforea.eventhelper.cause.ICauseStackManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.extensions.IForgeBlockState;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public interface IIntegration
{
	boolean cantBreak(@Nonnull ServerPlayer player, @Nonnull BlockPos pos);

	boolean cantPlace(@Nonnull ServerPlayer player, @Nonnull BlockPos pos, @Nonnull IForgeBlockState blockState);

	boolean cantReplace(@Nonnull ServerPlayer player, @Nonnull BlockPos pos, @Nonnull IForgeBlockState blockState);

	boolean cantAttack(@Nonnull ServerPlayer player, @Nonnull Entity victim);

	default boolean cantInteract(
			@Nonnull ServerPlayer player,
			@Nonnull InteractionHand hand, @Nonnull BlockPos targetPos, @Nonnull Direction targetSide)
	{
		return this.cantInteract(player, new BlockInteractParams(hand, targetPos, targetSide));
	}

	default boolean cantInteract(
			@Nonnull ServerPlayer player,
			@Nonnull InteractionHand hand,
			@Nonnull BlockPos interactionPos, @Nonnull BlockPos targetPos, @Nonnull Direction targetSide)
	{
		return this.cantInteract(player, new BlockInteractParams(hand, targetPos, targetSide).setInteractionPos(interactionPos));
	}

	boolean cantInteract(@Nonnull ServerPlayer player, @Nonnull BlockInteractParams params);

	boolean hasPermission(@Nonnull ServerPlayer player, @Nonnull String permission);

	default boolean hasPermission(@Nonnull UUID playerId, @Nonnull String permission)
	{
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server == null)
			return false;
		PlayerList playerList = server.getPlayerList();
		ServerPlayer player = playerList.getPlayer(playerId);
		return player != null && this.hasPermission(player, permission);
	}

	default boolean hasPermission(@Nonnull String playerName, @Nonnull String permission)
	{
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server == null)
			return false;
		PlayerList playerList = server.getPlayerList();
		ServerPlayer player = playerList.getPlayerByName(playerName);
		return player != null && this.hasPermission(player, permission);
	}

	@Nullable
	default ICauseStackManager getSpecificCauseStackManager()
	{
		return null;
	}

	final class BlockInteractParams
	{
		@Nonnull
		private final InteractionHand hand;
		@Nonnull
		private final BlockPos targetPos;
		@Nonnull
		private final Direction targetSide;

		@Nonnull
		private BlockPos interactionPos;
		@Nonnull
		private BlockInteractAction action = BlockInteractAction.RIGHT_CLICK;

		public BlockInteractParams(@Nonnull InteractionHand hand, @Nonnull BlockPos targetPos, @Nonnull Direction targetSide)
		{
			this.hand = hand;
			this.targetPos = targetPos;
			this.interactionPos = targetPos;
			this.targetSide = targetSide;
		}

		@Nonnull
		public InteractionHand getHand()
		{
			return this.hand;
		}

		@Nonnull
		public BlockPos getTargetPos()
		{
			return this.targetPos;
		}

		@Nonnull
		public Direction getTargetSide()
		{
			return this.targetSide;
		}

		@Nonnull
		public BlockPos getInteractionPos()
		{
			return this.interactionPos;
		}

		public BlockInteractParams setInteractionPos(@Nonnull BlockPos interactionPos)
		{
			this.interactionPos = interactionPos;
			return this;
		}

		@Nonnull
		public BlockInteractAction getAction()
		{
			return this.action;
		}

		public BlockInteractParams setAction(@Nonnull BlockInteractAction action)
		{
			this.action = action;
			return this;
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o)
				return true;
			if (o == null || this.getClass() != o.getClass())
				return false;
			BlockInteractParams that = (BlockInteractParams) o;
			return this.hand == that.hand && this.targetPos.equals(that.targetPos) && this.targetSide == that.targetSide && this.interactionPos.equals(that.interactionPos) && this.action == that.action;
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(this.hand, this.targetPos, this.targetSide, this.interactionPos, this.action);
		}

		@Override
		public String toString()
		{
			return "BlockInteractParams{hand=" + this.hand + ", targetPos=" + this.targetPos + ", targetSide=" + this.targetSide + ", interactionPos=" + this.interactionPos + ", action=" + this.action + '}';
		}
	}

	enum BlockInteractAction
	{
		RIGHT_CLICK,
		LEFT_CLICK
	}
}
