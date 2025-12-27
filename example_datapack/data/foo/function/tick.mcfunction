execute as @a[scores={foo_trigger_objective=1..}] run function foo:on_trigger
execute as @a[scores={foo_trigger_objective=1..}] run scoreboard players reset @s foo_trigger_objective

scoreboard players enable @a foo_trigger_objective