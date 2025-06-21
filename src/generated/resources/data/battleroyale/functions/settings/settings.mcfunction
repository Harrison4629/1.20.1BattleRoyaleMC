#地图制作者设置

#清空背包
clear @p
gamemode creative

#给物品:marker盔甲架
give @s armor_stand{EntityTag:{Glowing:true,Tags:["marker","res"],NoGravity:true,CustomName:'{"text":"resource","color":"yellow"}',CustomNameVisible:true},display:{Name:'{"text":"resource","color":"yellow"}'}}
give @s armor_stand{EntityTag:{Glowing:true,Tags:["marker","hobby"],NoGravity:true,CustomName:'{"text":"hobby","color":"blue"}',CustomNameVisible:true},display:{Name:'{"text":"hobby","color":"blue"}'}}
give @s armor_stand{EntityTag:{Glowing:true,Tags:["marker","platform"],NoGravity:true,CustomName:'{"text":"platform","color":"red"}',CustomNameVisible:true},display:{Name:'{"text":"platform","color":"red"}'}}
give @s armor_stand{EntityTag:{Glowing:true,Tags:["marker","pospoint"],NoGravity:true,CustomName:'{"text":"pospoint","color":"gray"}',CustomNameVisible:true},display:{Name:'{"text":"pospoint","color":"gray"}'}}
give @s minecraft:command_block

#将所有盔甲架可见
execute as @e[type=minecraft:armor_stand,tag=marker] run data modify entity @s CustomNameVisible set value true
execute as @e[type=minecraft:armor_stand,tag=marker] run data modify entity @s Marker set value false
execute as @e[type=minecraft:armor_stand,tag=marker] run data modify entity @s Invisible set value false







