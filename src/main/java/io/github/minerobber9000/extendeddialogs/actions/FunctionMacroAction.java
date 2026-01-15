package io.github.minerobber9000.extendeddialogs.actions;

import java.util.Optional;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import io.github.minerobber9000.extendeddialogs.ExtendedDialogs;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.commands.functions.InstantiatedFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;

/**
 * Calls a function with the dialog inputs as macro arguments.
*/
public class FunctionMacroAction implements ExtendedDialogAction {
    protected FunctionMacroAction() {}

    public static Logger LOGGER = LogUtils.getLogger();

    @Override
    public void doAction(ServerPlayer player, CompoundTag tag) {
        // get name of function and remove it from the arguments (abort if missing)
        Optional<String> ofunctionstr = tag.getString(ExtendedDialogs.FUNCTION_ARG);
        if (ofunctionstr.isEmpty()) {
            LOGGER.error("function_macro action without function, ignoring");
            return;
        }
        String functionstr = ofunctionstr.get();
        tag.remove(ExtendedDialogs.FUNCTION_ARG);
        // get the function itself (abort if nonexistent or not in allowed functions tag)
        Identifier functionid = Identifier.tryParse(functionstr);
        if (functionid==null) {
            LOGGER.error("function_macro action with invalid identifier {}, ignoring", functionstr);
            return;
        }
        MinecraftServer server = player.level().getServer();
        ServerFunctionManager sfm = server.getFunctions();
        Optional<CommandFunction<CommandSourceStack>> ofunction = sfm.get(functionid);
        if (ofunction.isEmpty()) {
            LOGGER.error("function_macro action with nonexistent function {}, ignoring", functionstr);
            return;
        }
        CommandFunction<CommandSourceStack> function = ofunction.get();
        if (!sfm.getTag(ExtendedDialogs.ALLOWED_FUNCTION_TAG).stream().anyMatch(fn->{
            LOGGER.debug("checking {} against function {}: {}",function.id(),fn.id(),Boolean.toString(fn.id().equals(function.id())));
            return fn.id().equals(function.id());
        })) {
            LOGGER.error("function_macro action with function {} not in {} tag, ignoring", functionstr, ExtendedDialogs.ALLOWED_FUNCTION_TAG.toString());
            return;
        }
        // run the function with the dialog inputs as macro arguments
        try {
            CommandSourceStack css = player.createCommandSourceStack()
                .withPermission(LevelBasedPermissionSet.GAMEMASTER)
                .withSuppressedOutput();
            InstantiatedFunction<CommandSourceStack> instantiated = function.instantiate(tag, sfm.getDispatcher());
            Commands.executeCommandInContext(css, (executionContext) -> {
                ExecutionContext.queueInitialFunctionCall(executionContext, instantiated, css, CommandResultCallback.EMPTY);
            });
        } catch (FunctionInstantiationException e) {
            LOGGER.error("error instantiating function {} for function_macro action", functionstr, e);
        }
    }
}
