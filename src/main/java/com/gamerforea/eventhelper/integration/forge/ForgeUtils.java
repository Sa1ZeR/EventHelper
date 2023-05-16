package com.gamerforea.eventhelper.integration.forge;

import com.gamerforea.eventhelper.util.FastUtils;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.UUID;

public class ForgeUtils {

    public static Player getPlayerByName(String name) {
        PlayerList playerList = ServerLifecycleHooks.getCurrentServer().getPlayerList();
        return playerList.getPlayerByName(name);
    }

    public static Player getPlayerByUUID(UUID uuid) {
        PlayerList playerList = ServerLifecycleHooks.getCurrentServer().getPlayerList();
        return playerList.getPlayer(uuid);
    }
}
