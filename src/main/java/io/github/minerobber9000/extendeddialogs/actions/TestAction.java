package io.github.minerobber9000.extendeddialogs.actions;

import io.github.minerobber9000.extendeddialogs.ExtendedDialogs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

/**
 * A test action, for ensuring the mod works. Just outputs some text to the server console.
*/
public class TestAction implements ExtendedDialogAction {
    protected TestAction() {}

    @Override
    public void doAction(ServerPlayer player, CompoundTag tag) {
        ExtendedDialogs.LOGGER.info("Got it!");
    };
}
