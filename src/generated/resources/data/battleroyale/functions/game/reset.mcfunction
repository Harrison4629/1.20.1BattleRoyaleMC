#重置


#替换箱子战利品
execute at @e[type=minecraft:armor_stand,tag=res] run data modify block ~ ~ ~ LootTable set value ""
execute at @e[type=minecraft:armor_stand,tag=res] run setblock ~ ~ ~ minecraft:chest replace
execute at @e[type=minecraft:armor_stand,tag=res] run data modify block ~ ~ ~ LootTable set value "battleroyale:chests"

#