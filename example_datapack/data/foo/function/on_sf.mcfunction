data modify storage foo:bar args.function set value "foo:on_sf"
function foo:getme_sf

tellraw @s [{"text":"You said: "},{"nbt":"me.text_input","storage":"extended_dialogs:storage_function","color":"gray"}]
function foo:give_diamonds with storage extended_dialogs:storage_function me

function foo:clear_sf