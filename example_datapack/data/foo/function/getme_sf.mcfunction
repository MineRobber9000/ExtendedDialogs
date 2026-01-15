data modify storage foo:bar args.UUID0 set from entity @s UUID[0]
data modify storage foo:bar args.UUID1 set from entity @s UUID[1]
data modify storage foo:bar args.UUID2 set from entity @s UUID[2]
data modify storage foo:bar args.UUID3 set from entity @s UUID[3]
function foo:zzprivate_getme_sf with storage foo:bar args
data remove storage foo:bar args