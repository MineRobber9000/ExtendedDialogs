package io.github.minerobber9000.extendeddialogs;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtendedDialogs implements ModInitializer {
	public static final String MOD_ID = "extended-dialogs";
	public static final String RESOURCE_NAMESPACE = "extended_dialogs";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier ALLOWED_FUNCTION_TAG = identifier("allowed_functions");
	public static String FUNCTION_ARG = identifier("function").toString();
	public static String TRIGGER_ARG = identifier("trigger").toString();
	public static String PLACEHOLDER = identifier("placeholder_you_shouldnt_use_this").toString();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
	}

	public static Identifier identifier(String path) {
		return Identifier.fromNamespaceAndPath(RESOURCE_NAMESPACE, path);
	}
}