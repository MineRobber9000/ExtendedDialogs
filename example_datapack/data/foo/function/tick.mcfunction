execute as @a[scores={foo_trigger_objective=1..}] run function foo:on_trigger
execute as @a[scores={foo_trigger_objective=1..}] run scoreboard players reset @s foo_trigger_objective
execute as @a[scores={foo_give_item=1..}] run function foo:on_give_item
execute as @a[scores={foo_give_item=1..}] run scoreboard players reset @s foo_give_item

scoreboard players enable @a foo_trigger_objective
scoreboard players enable @a foo_give_item