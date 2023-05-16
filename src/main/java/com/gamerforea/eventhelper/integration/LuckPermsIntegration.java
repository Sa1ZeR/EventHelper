package com.gamerforea.eventhelper.integration;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class LuckPermsIntegration {

    public static boolean hasPermissions(CommandSourceStack stack, String permission) {
        return hasPermission(stack.getPlayer(), permission);
    }

    public static boolean hasPermission(ServerPlayer player, String permission) {
        if (player == null)
            return false;

        User user = LuckPermsProvider.get().getPlayerAdapter(ServerPlayer.class).getUser(player);

        return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }
}
