package com.gamerforea.eventhelper.util;

import com.gamerforea.eventhelper.ModConstants;
import com.gamerforea.eventhelper.fake.FakePlayerContainer;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = ModConstants.MODID)
public class ExplosionByPlayer extends Explosion
{
	private final Player player;

	public ExplosionByPlayer(
			@Nonnull FakePlayerContainer fake,
			@Nonnull Level world, @Nullable
					Entity exploder, double x, double y, double z, float size, boolean flaming, boolean damagesTerrain)
	{
		this(fake.getPlayer(), world, exploder, x, y, z, size, flaming, damagesTerrain);
	}

	public ExplosionByPlayer(
			@Nonnull Player player,
			@Nonnull Level world, @Nullable
					Entity exploder, double x, double y, double z, float size, boolean flaming, boolean damagesTerrain)
	{
		super(world, exploder, x, y, z, size, flaming, damagesTerrain ? BlockInteraction.BREAK : BlockInteraction.NONE);
		this.player = player;
	}

	@Nonnull
	public static ExplosionByPlayer createExplosion(
			@Nonnull FakePlayerContainer fake,
			@Nonnull Level world,
			@Nullable Entity exploder, double x, double y, double z, float strength, boolean isSmoking)
	{
		return newExplosion(fake, world, exploder, x, y, z, strength, false, isSmoking);
	}

	@Nonnull
	public static ExplosionByPlayer createExplosion(
			@Nonnull ServerPlayer player,
			@Nonnull Level world,
			@Nullable Entity exploder, double x, double y, double z, float strength, boolean isSmoking)
	{
		return newExplosion(player, world, exploder, x, y, z, strength, false, isSmoking);
	}

	@Nonnull
	public static ExplosionByPlayer newExplosion(
			@Nonnull FakePlayerContainer fake,
			@Nonnull Level world, @Nullable
					Entity exploder, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking)
	{
		return newExplosion(new ExplosionByPlayer(fake, world, exploder, x, y, z, strength, isFlaming, isSmoking), world, x, y, z, strength, isSmoking);
	}

	@Nonnull
	public static ExplosionByPlayer newExplosion(
			@Nonnull ServerPlayer player,
			@Nonnull Level world, @Nullable
			Entity exploder, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking)
	{
		return newExplosion(new ExplosionByPlayer(player, world, exploder, x, y, z, strength, isFlaming, isSmoking), world, x, y, z, strength, isSmoking);
	}

	@Nonnull
	private static ExplosionByPlayer newExplosion(
			@Nonnull ExplosionByPlayer explosion,
			@Nonnull Level world, double x, double y, double z, float strength, boolean isSmoking)
	{
		if (ForgeEventFactory.onExplosionStart(world, explosion))
			return explosion;

		boolean isServerWorld = world instanceof ServerLevel;
		explosion.explode();
		explosion.finalizeExplosion(!isServerWorld);

		if (isServerWorld) {
			if (!isSmoking)
				explosion.clearToBlow();

			for (Player player : world.players())
			{
				if (player.distanceToSqr(x, y, z) < 4096)
					((ServerPlayer)player).connection.send(new ClientboundExplodePacket(x, y, z, strength, explosion.getToBlow(), explosion.getHitPlayers().get(player)));
			}
		}

		return explosion;
	}

	@Deprecated
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onDetonate(ExplosionEvent.Detonate event)
	{
		Explosion explosion = event.getExplosion();
		if (explosion instanceof ExplosionByPlayer explosionByPlayer)
		{
			Player player = explosionByPlayer.player;
			event.getAffectedBlocks().removeIf(pos -> EventUtils.cantBreak(player, pos));
			event.getAffectedEntities().removeIf(entity -> EventUtils.cantAttack(player, entity));
		}
	}
}
