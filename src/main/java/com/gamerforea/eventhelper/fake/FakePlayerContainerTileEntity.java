package com.gamerforea.eventhelper.fake;

import com.mojang.authlib.GameProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;

public final class FakePlayerContainerTileEntity extends FakePlayerContainer
{
	private final BlockEntity tile;

	public FakePlayerContainerTileEntity(@Nonnull FakePlayerContainer fake, @Nonnull BlockEntity tile)
	{
		super(fake);
		this.tile = tile;
	}

	public FakePlayerContainerTileEntity(@Nonnull GameProfile modFakeProfile, @Nonnull BlockEntity tile)
	{
		super(modFakeProfile);
		this.tile = tile;
	}

	@Override
	@Nonnull
	public final Level getWorld()
	{
		return this.tile.getLevel();
	}
}