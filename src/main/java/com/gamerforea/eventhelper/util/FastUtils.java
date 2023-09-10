package com.gamerforea.eventhelper.util;

import com.gamerforea.eventhelper.fake.FakePlayerContainer;
import com.gamerforea.eventhelper.fake.IFakeTile;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public final class FastUtils
{
	public static void stopPotionEffect(@Nonnull LivingEntity entity, @Nonnull MobEffect potion)
	{
		stopPotionEffect(entity.getEffect(potion));
	}

	public static void stopPotionEffect(@Nullable MobEffectInstance potionEffect)
	{
		if (potionEffect != null && potionEffect.getDuration() > 0)
			ObfuscationReflectionHelper.setPrivateValue(MobEffectInstance.class, potionEffect, 0, "field_76460_b");
	}

	public static <T extends BlockEntity> boolean setProfile(
			@Nonnull Level world,
			@Nonnull BlockPos pos, @Nonnull Entity entity, Class<T> tileClass, Function<T, FakePlayerContainer> mapper)
	{
		if (world.isLoaded(pos)) {
			BlockEntity tile = world.getBlockEntity(pos);
			if (tileClass.isInstance(tile))
			{
				FakePlayerContainer fake = mapper.apply((T) tile);
				return fake.setProfile(entity);
			}
		}
		return false;
	}

	public static <T extends IFakeTile> boolean setFakeProfile(
			@Nonnull Level world,
			@Nonnull BlockPos pos, @Nonnull Entity entity, Class<T> tileClass, Function<T, FakePlayerContainer> mapper) {
		if (world.isLoaded(pos)) {
			BlockEntity tile = world.getBlockEntity(pos);
			if (tileClass.isInstance(tile)) {
				FakePlayerContainer fake = mapper.apply((T) tile);
				return fake.setProfile(entity);
			}
		}
		return false;
	}

	@Deprecated
	public static boolean isOnline(@Nonnull ServerPlayer player)
	{
		if (player instanceof FakePlayer)
			return true;

		for (Player playerOnline : getServer().getPlayerList().getPlayers()) {
			if (playerOnline.equals(player))
				return true;
		}

		return false;
	}

	public static boolean isValidRealPlayer(@Nullable ServerPlayer player)
	{
		return isValidRealPlayer(player, true);
	}

	public static boolean isValidRealPlayer(@Nullable ServerPlayer player, boolean checkAlive)
	{
		if (player == null || player instanceof FakePlayer)
			return false;

		ServerGamePacketListener connection = player.connection;
		if (connection == null || !connection.getConnection().isConnected())
			return false;

		return !checkAlive || player.isAlive();
	}

	@Nonnull
	public static FakePlayer getFake(@Nullable Level world, @Nonnull FakePlayer fake)
	{
		fake.level = world == null ? getEntityWorld() : world;
		return fake;
	}

	@Nonnull
	public static FakePlayer getFake(@Nullable Level world, @Nonnull GameProfile profile)
	{
		return getFake(world, FakePlayerFactory.get((ServerLevel) (world == null ? getEntityWorld() : world), profile));
	}

	@Nonnull
	public static Player getLivingPlayer(@Nullable LivingEntity entity, @Nonnull FakePlayer modFake)
	{
		return entity instanceof Player ? (Player) entity : getFake(entity == null ? null : entity.level, modFake);
	}

	@Nonnull
	public static Player getLivingPlayer(@Nullable LivingEntity entity, @Nonnull GameProfile modFakeProfile)
	{
		return entity instanceof Player ? (Player) entity : getFake(entity == null ? null : entity.level, modFakeProfile);
	}

//	@Nonnull
//	public static Player getThrowerPlayer(@Nullable LivingEntity entity, @Nonnull FakePlayer modFake)
//	{
//		return getLivingPlayer(entity == null ? null : entity.get(), modFake);
//	}
//
//	@Nonnull
//	public static Player getThrowerPlayer(@Nullable LivingEntity entity, @Nonnull GameProfile modFakeProfile)
//	{
//		return getLivingPlayer(entity == null ? null : entity.getThrower(), modFakeProfile);
//	}
//
//	@Nonnull
//	public static EntityLivingBase getThrower(@Nullable LivingEntity entity, @Nonnull FakePlayer modFake)
//	{
//		EntityLivingBase thrower = entity == null ? null : entity.getThrower();
//		return thrower == null ? getFake(entity == null ? null : entity.world, modFake) : thrower;
//	}
//
//	@Nonnull
//	public static EntityLivingBase getThrower(@Nullable LivingEntity entity, @Nonnull GameProfile modFakeProfile)
//	{
//		EntityLivingBase thrower = entity == null ? null : entity.getThrower();
//		return thrower == null ? getFake(entity == null ? null : entity.world, modFakeProfile) : thrower;
//	}

	@Nonnull
	private static MinecraftServer getServer()
	{
		return ServerLifecycleHooks.getCurrentServer();
	}

	@Nonnull
	private static Level getEntityWorld()
	{
		return getServer().getAllLevels().iterator().next();
	}
}