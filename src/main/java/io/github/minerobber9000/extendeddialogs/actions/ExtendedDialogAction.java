package io.github.minerobber9000.extendeddialogs.actions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

/**
 * Interface for actions. Defines one function, that being the one which does the action.
*/
public interface ExtendedDialogAction {
    void doAction(ServerPlayer player, CompoundTag tag);
}
