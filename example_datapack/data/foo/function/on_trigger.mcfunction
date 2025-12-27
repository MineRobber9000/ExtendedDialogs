function foo:getme

tellraw @s [{"text":"You said: "},{"nbt":"me.text_input","storage":"extended_dialogs:storage_trigger","color":"gray"}]
function foo:give_diamonds with storage extended_dialogs:storage_trigger me

function foo:clear