package com.gamerforea.eventhelper;

import com.gamerforea.eventhelper.config.Config;
import com.gamerforea.eventhelper.util.ExplosionByPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.gamerforea.eventhelper.ModConstants.MODID;
import static com.gamerforea.eventhelper.ModConstants.NAME;

@Mod(MODID)
public final class EventHelperMod {
	public static final Logger LOGGER = LogManager.getLogger(NAME);

	public static boolean debug = true;

	public EventHelperMod() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(ExplosionByPlayer.class);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, "Events.toml");
	}
}