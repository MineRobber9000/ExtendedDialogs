# ExtendedDialogs

A mod that adds the ability for dialogs to take input from non-operator users.

## Rationale

When it comes to dialog inputs in Minecraft, there's actually not a whole lot you can do with them if the user is not an operator. You can run a command (except that command has to be one that a non-operator can run which doesn't output directly to chat, so in practice just `trigger`), and that's about it. If the user is an operator, you can at least theoretically call a function using `minecraft:dynamic/run_command` to construct some sort of `function your:function with {arg1:$(arg1)}`-style macro.

However, Minecraft does provide another vanilla way for dialogs with inputs to use those inputs: the custom actions (`minecraft:custom` and `minecraft:dynamic/custom`). These actions send packets to the Minecraft server, which it can then use for whatever purpose it wants. Unfortunately, the vanilla Minecraft server doesn't actually do anything with those packets besides logging them to the console. Fortunately, that's what mods are for.

## How to use

See `example_datapack`.

### Okay, but, like, quick reference?

Use a `minecraft:custom` or `minecraft:dynamic/custom` action (you probably want the latter if you're taking inputs from your dialog) with an ID of `extended_dialogs:storage_trigger`. In the `payload` or `additions` (resp.), define a `extended_dialogs:trigger` key with a value of a trigger objective. As long as the trigger objective exists, is a trigger, and is enabled for the user, the input data will be stored in the `extended_dialogs:storage_trigger` storage inside the `inputs` list (you can index that by the UUID of the targeted user), and the trigger objective will be triggered.