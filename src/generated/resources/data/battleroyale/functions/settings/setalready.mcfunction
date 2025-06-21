#盔甲架不可见无碰撞不可破坏
execute as @e[type=minecraft:armor_stand,tag=marker] run data modify entity @s CustomNameVisible set value false
execute as @e[type=minecraft:armor_stand,tag=marker] run data modify entity @s Marker set value true
execute as @e[type=minecraft:armor_stand,tag=marker] run data modify entity @s Invisible set value true


clear @p