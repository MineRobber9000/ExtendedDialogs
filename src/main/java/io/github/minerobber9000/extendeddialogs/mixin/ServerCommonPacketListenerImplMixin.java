package io.github.minerobber9000.extendeddialogs.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.minerobber9000.extendeddialogs.actions.ExtendedDialogAction;
import io.github.minerobber9000.extendeddialogs.actions.ExtendedDialogActions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

/**
 * Mixin for ServerCommonPacketListenImpl which handles Extended Dialogs' custom events.
*/
@Mixin(ServerCommonPacketListenerImpl.class)
public class ServerCommonPacketListenerImplMixin {
    @Shadow
    @Final
    protected MinecraftServer server;

    @Inject(at = @At("HEAD"), method = "handleCustomClickAction", cancellable = true)
    public void extendeddialogs$handleCustomClickAction(ServerboundCustomClickActionPacket packet, CallbackInfo ci) {
        // Get ServerCommonPacketListenerImpl instance and ensure we're on the server's main thread
        ServerCommonPacketListenerImpl scpli = (ServerCommonPacketListenerImpl) (Object) this;
        PacketUtils.ensureRunningOnSameThread(packet, scpli, this.server.packetProcessor());
        // Resolve the packet ID to an ExtendedDialogAction
        ExtendedDialogAction action = ExtendedDialogActions.resolveAction(packet.id());
        // If the action doesn't exist, or the packet payload is empty, or the packet payload isn't a compound tag, ignore it
        if (action==null || packet.payload().isEmpty() || !(packet.payload().get() instanceof CompoundTag)) return;
        // Get the ServerGamePacketListenerImpl instance, if we are that instance
        if (!(scpli instanceof ServerGamePacketListenerImpl)) return;
        ServerGamePacketListenerImpl sgpli = (ServerGamePacketListenerImpl) scpli;
        // Call the ExtendedDialogAction
        action.doAction(sgpli.player, ((CompoundTag)packet.payload().get()).copy());
        // Cancel the default handling of the custom click action packet
        ci.cancel();
    }
}