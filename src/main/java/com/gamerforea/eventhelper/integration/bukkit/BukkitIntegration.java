package com.gamerforea.eventhelper.integration.bukkit;

import com.gamerforea.eventhelper.EventHelperMod;
import com.gamerforea.eventhelper.integration.IIntegration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.extensions.IForgeBlockState;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

import static com.gamerforea.eventhelper.integration.bukkit.BukkitUtils.*;

public final class BukkitIntegration
{
	private static final IIntegration INTEGRATION;

	private BukkitIntegration()
	{
	}

	public static boolean isBukkitPresent()
	{
		return INTEGRATION != null;
	}

	@Nullable
	public static IIntegration getIntegration()
	{
		return INTEGRATION;
	}

	static
	{
		IIntegration integration = null;

		try
		{
			Class.forName("org.bukkit.Server");
			integration = new BukkitIntegration0();
		}
		catch (NoClassDefFoundError | ClassNotFoundException e)
		{
			if (EventHelperMod.debug)
				EventHelperMod.LOGGER.info("BukkitAPI not found", e);
			else
				EventHelperMod.LOGGER.info("BukkitAPI not found");
		}

		INTEGRATION = integration;
	}

	private static final class BukkitIntegration0 implements IIntegration
	{
		private BukkitIntegration0()
		{
		}

		@Override
		public boolean cantBreak(@Nonnull net.minecraft.world.entity.player.Player player, @Nonnull BlockPos pos) {
			Player bukkitPlayer = getPlayer(player);
			if(bukkitPlayer == null) return false;

			Block block = bukkitPlayer.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
			BlockBreakEvent event = new BlockBreakEvent(block, bukkitPlayer);
			Bukkit.getPluginManager().callEvent(event);
			return event.isCancelled();
		}

		@Override
		public boolean cantPlace(@Nonnull net.minecraft.world.entity.player.Player player, @Nonnull BlockPos pos, @Nonnull IForgeBlockState blockState)
		{
			// TODO Make correct implementation
			return this.cantBreak(player, pos);
		}

		@Override
		public boolean cantReplace(@Nonnull net.minecraft.world.entity.player.Player player, @Nonnull BlockPos pos, @Nonnull IForgeBlockState blockState)
		{
			// TODO Make correct implementation
			return this.cantBreak(player, pos);
		}

		@Override
		public boolean cantAttack(@Nonnull net.minecraft.world.entity.player.Player player, @Nonnull Entity victim)
		{
			Player bukkitPlayer = getPlayer(player);
			if(bukkitPlayer == null) return false;

			org.bukkit.entity.Entity bukkitVictim = getEntity(victim);
			EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(bukkitPlayer, bukkitVictim, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 0);
			Bukkit.getPluginManager().callEvent(event);
			return event.isCancelled();
		}

		@Override
		public boolean cantInteract(@Nonnull net.minecraft.world.entity.player.Player player, @Nonnull BlockInteractParams params)
		{
			Player bukkitPlayer = getPlayer(player);
			if(bukkitPlayer == null) return false;

			PlayerInventory inventory = bukkitPlayer.getInventory();
			ItemStack stack = params.getHand() == InteractionHand.MAIN_HAND ? inventory.getItemInMainHand() : inventory.getItemInOffHand();
			Block block = bukkitPlayer.getWorld().getBlockAt(params.getTargetPos().getX(), params.getTargetPos().getY(), params.getTargetPos().getZ());
			PlayerInteractEvent event = new PlayerInteractEvent(bukkitPlayer, params.getAction() == BlockInteractAction.RIGHT_CLICK ? Action.RIGHT_CLICK_BLOCK : Action.LEFT_CLICK_BLOCK, stack, block, getBlockFace(params.getTargetSide()), params.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND);
			Bukkit.getPluginManager().callEvent(event);
			return event.isCancelled();
		}

		@Override
		public boolean hasPermission(@Nonnull net.minecraft.world.entity.player.Player player, @Nonnull String permission)
		{
			Player bukkitPlayer = getPlayer(player);
			if(bukkitPlayer == null) return false;

			return getPlayer(player).hasPermission(permission);
		}

		@Override
		public boolean hasPermission(@Nonnull UUID playerId, @Nonnull String permission) {
			Player player = Bukkit.getPlayer(playerId);
			return player != null && player.hasPermission(permission);
		}

		@Override
		public boolean hasPermission(@Nonnull String playerName, @Nonnull String permission) {
			Player player = Bukkit.getPlayerExact(playerName);
			return player != null && player.hasPermission(permission);
		}
	}
}
