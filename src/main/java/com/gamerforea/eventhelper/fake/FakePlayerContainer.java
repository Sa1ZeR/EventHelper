package com.gamerforea.eventhelper.fake;

import com.gamerforea.eventhelper.integration.IIntegration;
import com.gamerforea.eventhelper.util.EventUtils;
import com.gamerforea.eventhelper.util.ExplosionByPlayer;
import com.gamerforea.eventhelper.util.FastUtils;
import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeBlockState;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.UUID;

;

public abstract class FakePlayerContainer
{
	private static final String NBT_FAKE_NAME = "eventhelper_fakeName";
	private static final String NBT_FAKE_ID_MOST = "eventhelper_fakeUUID_Most";
	private static final String NBT_FAKE_ID_LEAST = "eventhelper_fakeUUID_Least";

	private final GameProfile modFakeProfile;
	private FakePlayer modFake;

	private GameProfile profile;
	private FakePlayer fakePlayer;

	private WeakReference<ServerPlayer> realPlayer;

	protected FakePlayerContainer(@Nonnull FakePlayerContainer fake)
	{
		this(fake.modFakeProfile);
		this.modFake = fake.modFake;
		this.setParent(fake);
	}

	protected FakePlayerContainer(@Nonnull GameProfile modFakeProfile)
	{
		Preconditions.checkArgument(modFakeProfile.isComplete(), "modFakeProfile is incomplete");
		this.modFakeProfile = modFakeProfile;
	}

	@Nonnull
	public abstract Level getWorld();

	@Nonnull
	public final ServerPlayer getPlayer()
	{
		if (this.realPlayer != null)
		{
			ServerPlayer p = this.realPlayer.get();
			if (p == null)
				this.realPlayer = null;
			else
				return p;
		}

		return this.getFakePlayer();
	}

	@Nonnull
	public final FakePlayer getFakePlayer()
	{
		if (this.fakePlayer != null)
			return FastUtils.getFake(this.getWorld(), this.fakePlayer);
		if (this.profile != null)
			return this.fakePlayer = FastUtils.getFake(this.getWorld(), this.profile);
		if (this.modFake != null)
			return FastUtils.getFake(this.getWorld(), this.modFake);
		return this.modFake = FastUtils.getFake(this.getWorld(), this.modFakeProfile);
	}

	public final boolean setRealPlayer(@Nullable Entity entity)
	{
		return entity instanceof Player && this.setRealPlayer((Player) entity);
	}

	public final boolean setRealPlayer(@Nullable ServerPlayer player)
	{
		if (this.setProfile(player))
		{
			if (!(player instanceof FakePlayer))
				this.realPlayer = new WeakReference<>(player);
			return true;
		}
		return false;
	}

	public final boolean setParent(@Nullable FakePlayerContainer container)
	{
		if (container != null && container.profile != null)
		{
			this.reset();
			this.profile = container.profile;
			this.fakePlayer = container.fakePlayer;
			this.realPlayer = container.realPlayer;
			return true;
		}
		return false;
	}

	@Nullable
	public final GameProfile getProfile()
	{
		return this.profile;
	}

	public final boolean setProfile(@Nullable Entity entity)
	{
		if (entity instanceof Player)
			return this.setProfile(entity);
		return false;
	}

	public final boolean setProfile(@Nullable Player player)
	{
		return player != null && this.setProfile(player.getGameProfile());
	}

	public final boolean setProfile(@Nullable GameProfile profile)
	{
		if (profile != null && profile.isComplete())
		{
			this.reset();
			this.profile = profile;
			return true;
		}
		return false;
	}

	public final boolean cantBreak(@Nonnull BlockPos pos)
	{
		return EventUtils.cantBreak(this.getPlayer(), pos);
	}

	public final boolean cantPlace(@Nonnull BlockPos pos, @Nonnull IForgeBlockState blockState)
	{
		return EventUtils.cantPlace(this.getPlayer(), pos, blockState);
	}

	public final boolean cantReplace(@Nonnull BlockPos pos, @Nonnull IForgeBlockState blockState)
	{
		return EventUtils.cantReplace(this.getPlayer(), pos, blockState);
	}

	public final boolean cantAttack(@Nonnull Entity target)
	{
		return EventUtils.cantAttack(this.getPlayer(), target);
	}

	public final boolean cantInteract(
			@Nonnull InteractionHand hand, @Nonnull BlockPos targetPos, @Nonnull Direction targetSide)
	{
		return EventUtils.cantInteract(this.getPlayer(), hand, targetPos, targetSide);
	}

	public final boolean cantInteract(
			@Nonnull InteractionHand hand,
			@Nonnull BlockPos interactionPos, @Nonnull BlockPos targetPos, @Nonnull Direction targetSide)
	{
		return EventUtils.cantInteract(this.getPlayer(), hand, interactionPos, targetPos, targetSide);
	}

	public final boolean cantInteract(@Nonnull IIntegration.BlockInteractParams params)
	{
		return EventUtils.cantInteract(this.getPlayer(), params);
	}

	@Nonnull
	public final ExplosionByPlayer createExplosion(
			@Nullable Entity entityIn, double x, double y, double z, float strength, boolean isSmoking)
	{
		return ExplosionByPlayer.createExplosion(this, this.getWorld(), entityIn, x, y, z, strength, isSmoking);
	}

	@Nonnull
	public final ExplosionByPlayer newExplosion(@Nullable
														Entity entityIn, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking)
	{
		return ExplosionByPlayer.newExplosion(this, this.getWorld(), entityIn, x, y, z, strength, isFlaming, isSmoking);
	}

	public final void writeToNBT(CompoundTag nbt) {
		if (this.profile != null)
		{
			nbt.putString(NBT_FAKE_NAME, this.profile.getName());
			UUID id = this.profile.getId();
			nbt.putLong(NBT_FAKE_ID_MOST, id.getMostSignificantBits());
			nbt.putLong(NBT_FAKE_ID_LEAST, id.getLeastSignificantBits());
		}
	}

	public final void readFromNBT(CompoundTag nbt)
	{
		String name = nbt.getString(NBT_FAKE_NAME);
		if (!name.isEmpty())
		{
			long most = nbt.getLong(NBT_FAKE_ID_MOST);
			long least = nbt.getLong(NBT_FAKE_ID_LEAST);
			if (least != 0 || most != 0)
			{
				UUID id = new UUID(most, least);
				this.profile = new GameProfile(id, name);
			}
		}
	}

	private void reset()
	{
		this.profile = null;
		this.fakePlayer = null;
		this.realPlayer = null;
	}
}