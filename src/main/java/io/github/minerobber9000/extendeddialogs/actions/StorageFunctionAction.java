package io.github.minerobber9000.extendeddialogs.actions;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import io.github.minerobber9000.extendeddialogs.ExtendedDialogs;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.world.level.storage.CommandStorage;

/**
 * Stores the user inputs in a data storage, and then calls a function.
 * 
 * Specifically, user inputs are stored in the `inputs` list inside the `extended_dialogs:storage_function` storage.
 * The UUID of the player who triggered this action is stored in the UUID key of the tag alongside the inputs.
 * 
 * The function to call is sent alongside the user inputs as the value of the `extended_dialogs:function` key. However,
 * as a rudimentary security feature, the function that will be called *must* be in the `extended_dialogs:allowed_functions`
 * function tag, or else it will not be called. This is because hacked clients can theoretically send a custom click event
 * packet with *any* arguments, so by at least limiting which functions can be called by dialogs, it limits the possible
 * attack surface.
*/
public class StorageFunctionAction implements ExtendedDialogAction {
    protected StorageFunctionAction() {}

    public static Logger LOGGER = LogUtils.getLogger();

    public static Identifier DATA_STORAGE = ExtendedDialogs.identifier("storage_function");

    @Override
    public void doAction(ServerPlayer player, CompoundTag tag) {
        // get name of function and remove it from the arguments (abort if missing)
        Optional<String> ofunctionstr = tag.getString(ExtendedDialogs.FUNCTION_ARG);
        if (ofunctionstr.isEmpty()) {
            LOGGER.error("storage_function action without function, ignoring");
            return;
        }
        String functionstr = ofunctionstr.get();
        // get the function itself (abort if nonexistent or not in allowed functions tag)
        Identifier functionid = Identifier.tryParse(functionstr);
        if (functionid==null) {
            LOGGER.error("storage_function action with invalid identifier {}, ignoring", functionstr);
            return;
        }
        MinecraftServer server = player.level().getServer();
        ServerFunctionManager sfm = server.getFunctions();
        Optional<CommandFunction<CommandSourceStack>> ofunction = sfm.get(functionid);
        if (ofunction.isEmpty()) {
            LOGGER.error("storage_function action with nonexistent function {}, ignoring", functionstr);
            return;
        }
        CommandFunction<CommandSourceStack> function = ofunction.get();
        if (!sfm.getTag(ExtendedDialogs.ALLOWED_FUNCTION_TAG).stream().anyMatch(fn->{
            LOGGER.debug("checking {} against function {}: {}",function.id(),fn.id(),Boolean.toString(fn.id().equals(function.id())));
            return fn.id().equals(function.id());
        })) {
            LOGGER.error("storage_function action with function {} not in {} tag, ignoring", functionstr, ExtendedDialogs.ALLOWED_FUNCTION_TAG.toString());
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
                if (Arrays.equals(inputTag.getIntArray("UUID").orElse(null), playerUuidIntArray) && inputTag.getString(ExtendedDialogs.FUNCTION_ARG).orElse(ExtendedDialogs.PLACEHOLDER).equals(functionstr)) {
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
        // call function
        sfm.execute(function, player.createCommandSourceStack().withPermission(LevelBasedPermissionSet.GAMEMASTER).withSuppressedOutput());
    }
}
