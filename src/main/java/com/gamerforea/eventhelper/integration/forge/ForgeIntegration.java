package com.gamerforea.eventhelper.integration.forge;

import com.gamerforea.eventhelper.integration.IIntegration;
import com.gamerforea.eventhelper.integration.LuckPermsIntegration;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeBlockState;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ForgeIntegration implements IIntegration {

    private static final IIntegration INTEGRATION = new ForgeIntegration();

    private ForgeIntegration()
    {
    }

    public static boolean isForgePresent()
    {
        return INTEGRATION != null;
    }

    @Nullable
    public static IIntegration getIntegration()
    {
        return INTEGRATION;
    }

    @Override
    public boolean cantBreak(@NotNull Player player, @NotNull BlockPos pos, @NotNull IForgeBlockState blockState) {
        Player forgePlayer = ForgeUtils.getPlayerByName(player.getName().getString());
        if(forgePlayer == null) return true;

        BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(player.level, pos, (BlockState) blockState, forgePlayer);
        return MinecraftForge.EVENT_BUS.post(breakEvent);
    }

    @Override
    public boolean cantPlace(@NotNull Player player, @NotNull BlockPos pos, @NotNull IForgeBlockState blockState) {
        return cantBreak(player, pos, blockState);
    }

    @Override
    public boolean cantReplace(@NotNull Player player, @NotNull BlockPos pos, @NotNull IForgeBlockState blockState) {
        return cantBreak(player, pos, blockState);
    }

    @Override
    public boolean cantAttack(@NotNull Player player, @NotNull Entity victim) {
        Player forgePlayer = ForgeUtils.getPlayerByName(player.getName().getString());
        if(forgePlayer == null) return true;

        LivingAttackEvent event =
                new LivingAttackEvent((LivingEntity) victim, DamageSource.playerAttack(forgePlayer), 1);

        return MinecraftForge.EVENT_BUS.post(event);
    }

    @Override
    public boolean cantInteract(@NotNull Player player, @NotNull IIntegration.BlockInteractParams params) {
        Player forgePlayer = ForgeUtils.getPlayerByName(player.getName().getString());
        if(forgePlayer == null) return true;

        PlayerInteractEvent event;
        if(params.getAction() == BlockInteractAction.LEFT_CLICK)
            if(params.getTargetPos() != null)
                event = new PlayerInteractEvent.LeftClickBlock(forgePlayer, params.getTargetPos(), params.getTargetSide());
            else event = new PlayerInteractEvent.LeftClickEmpty(forgePlayer);
        else {
            event = new PlayerInteractEvent.RightClickEmpty(forgePlayer, params.getHand());
        }

        return MinecraftForge.EVENT_BUS.post(event);
    }

    @Override
    public boolean hasPermission(@NotNull Player player, @NotNull String permission) {
        Player forgePlayer = ForgeUtils.getPlayerByName(player.getName().getString());
        if(forgePlayer == null) return true;

        return LuckPermsIntegration.hasPermission((ServerPlayer) forgePlayer, permission);
    }
}
