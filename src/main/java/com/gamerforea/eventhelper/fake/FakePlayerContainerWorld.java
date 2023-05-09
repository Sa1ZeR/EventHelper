package com.gamerforea.eventhelper.fake;

import com.mojang.authlib.GameProfile;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public final class FakePlayerContainerWorld extends FakePlayerContainer
{
	private final Level world;

	public FakePlayerContainerWorld(@Nonnull FakePlayerContainer fake, @Nonnull Level world)
	{
		super(fake);
		this.world = world;
	}

	public FakePlayerContainerWorld(@Nonnull GameProfile modFakeProfile, @Nonnull Level world)
	{
		super(modFakeProfile);
		this.world = world;
	}

	@Override
	@Nonnull
	public final Level getWorld()
	{
		return this.world;
	}
}