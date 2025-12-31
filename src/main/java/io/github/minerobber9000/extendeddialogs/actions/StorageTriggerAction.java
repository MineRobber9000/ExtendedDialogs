package io.github.minerobber9000.extendeddialogs.actions;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.minerobber9000.extendeddialogs.ExtendedDialogs;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.CommandStorage;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

/**
 * Stores the user inputs in a data storage, and then triggers an objective.
 * 
 * Specifically, user inputs are stored in the `inputs` list inside the `extended_dialogs:storage_trigger` storage.
 * The UUID of the player who triggered this action is stored in the UUID key of the tag alongside the inputs.
 * 
 * The objective to trigger is sent alongside the user inputs as the value of the `extended_dialogs:trigger` key.
*/
public class StorageTriggerAction implements ExtendedDialogAction {
    protected StorageTriggerAction() {}

    public static Logger LOGGER = LoggerFactory.getLogger("StorageTriggerAction");

    public static Identifier DATA_STORAGE = Identifier.fromNamespaceAndPath(ExtendedDialogs.RESOURCE_NAMESPACE, "storage_trigger");
    public static String TRIGGER = Identifier.fromNamespaceAndPath(ExtendedDialogs.RESOURCE_NAMESPACE, "trigger").toString();
    public static String PLACEHOLDER = Identifier.fromNamespaceAndPath(ExtendedDialogs.RESOURCE_NAMESPACE, "placeholder_you_shouldnt_use_this").toString();

    @Override
    public void doAction(ServerPlayer player, CompoundTag tag) {
        // get name of trigger (abort if missing)
        Optional<String> otrigger = tag.getString(TRIGGER);
        if (otrigger.isEmpty()) {
            LOGGER.error("storage_trigger action without trigger, ignoring");
            return;
        }
        String trigger = otrigger.get();
        // get trigger objective itself (abort if nonexistant or not trigger or not primed)
        MinecraftServer server = player.level().getServer();
        ServerScoreboard ssb = server.getScoreboard();
        Objective triggerObjective = ssb.getObjective(trigger);
        if (triggerObjective==null) {
            LOGGER.error("storage_trigger action with nonexistant objective %s, ignoring", trigger);
            return;
        }
        if (triggerObjective.getCriteria()!=ObjectiveCriteria.TRIGGER) {
            LOGGER.error("storage_trigger action with non-trigger objective %s, ignoring", trigger);
            return;
        }
        ReadOnlyScoreInfo rosi = ssb.getPlayerScoreInfo(player, triggerObjective);
        if (rosi==null || rosi.isLocked()) {
            LOGGER.error("storage_trigger action with unprimed trigger %s, ignoring", trigger);
            return;
        }
        // get player UUID and convert to int array (secret tool that will help us later)
        UUID playerUuid = player.getUUID();
        int[] playerUuidIntArray = UUIDUtil.uuidToIntArray(playerUuid);
        // get the data storage for this action (create it if it doesn't exist)
        CommandStorage cs = server.getCommandStorage();
        CompoundTag root = cs.get(DATA_STORAGE);
        if (root == null) root = new CompoundTag();
        // get list of user inputs
        ListTag inputs = root.getListOrEmpty("inputs");
        // if one of these already exists for the current player, remove it
        for (int i=0;i<inputs.size();++i) {
            Tag bareInputTag = inputs.get(i);
            if (bareInputTag instanceof CompoundTag) {
                CompoundTag inputTag = (CompoundTag) bareInputTag;
                if (Arrays.equals(inputTag.getIntArray("UUID").orElse(null), playerUuidIntArray) && inputTag.getString(TRIGGER).orElse(PLACEHOLDER).equals(trigger)) {
                    inputs.remove(i);
                    break;
                }
            }
        }
        // add player UUID to the inputs we have and add it to the list
        tag.putIntArray("UUID", playerUuidIntArray);
        inputs.add(tag);
        // save this data
        root.put("inputs", inputs);
        cs.set(DATA_STORAGE, root);
        // trigger objective
        ScoreAccess sa = ssb.getOrCreatePlayerScore(player, triggerObjective);
        sa.add(1);
    }
}
