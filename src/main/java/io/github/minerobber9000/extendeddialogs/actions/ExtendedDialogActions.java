package io.github.minerobber9000.extendeddialogs.actions;

import java.util.Map;

import io.github.minerobber9000.extendeddialogs.ExtendedDialogs;
import net.minecraft.resources.Identifier;

/**
 * Pseudo-registry of the dialog actions.
*/
public class ExtendedDialogActions {
    public static Map<Identifier, ExtendedDialogAction> actions = Map.of(
        Identifier.fromNamespaceAndPath(ExtendedDialogs.RESOURCE_NAMESPACE,"test"), new TestAction(),
        Identifier.fromNamespaceAndPath(ExtendedDialogs.RESOURCE_NAMESPACE, "storage_trigger"), new StorageTriggerAction()
    );

    public static ExtendedDialogAction resolveAction(Identifier id) {
        return actions.get(id);
    }
}
