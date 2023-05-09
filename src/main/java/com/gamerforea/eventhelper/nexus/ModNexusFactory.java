package com.gamerforea.eventhelper.nexus;

import com.gamerforea.eventhelper.fake.FakePlayerContainerEntity;
import com.gamerforea.eventhelper.fake.FakePlayerContainerTileEntity;
import com.gamerforea.eventhelper.fake.FakePlayerContainerWorld;
import com.gamerforea.eventhelper.util.FastUtils;
import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;

public final class ModNexusFactory
{
	private final GameProfile modFakeProfile;

	public ModNexusFactory(@Nonnull GameProfile modFakeProfile)
	{
		Preconditions.checkArgument(modFakeProfile.isComplete(), "modFakeProfile is incomplete");
		this.modFakeProfile = modFakeProfile;
	}

	@Nonnull
	public GameProfile getProfile()
	{
		return this.modFakeProfile;
	}

	@Nonnull
	public FakePlayer getFake(@Nonnull Level world)
	{
		return FastUtils.getFake(world, this.modFakeProfile);
	}

	@Nonnull
	public FakePlayerContainerEntity wrapFake(@Nonnull Entity entity)
	{
		return new FakePlayerContainerEntity(this.modFakeProfile, entity);
	}

	@Nonnull
	public FakePlayerContainerTileEntity wrapFake(@Nonnull BlockEntity tile)
	{
		return new FakePlayerContainerTileEntity(this.modFakeProfile, tile);
	}

	@Nonnull
	public FakePlayerContainerWorld wrapFake(@Nonnull Level world)
	{
		return new FakePlayerContainerWorld(this.modFakeProfile, world);
	}
}
