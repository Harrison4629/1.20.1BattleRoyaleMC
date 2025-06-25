#开始游戏

execute as @a[tag=inGame] run attribute @s forge:nametag_distance base set 0
item replace entity @a[tag=inGame] armor.chest with vc_gliders:paraglider_wood{Unbreakable:1,Enchantments:[{}]}


title @a actionbar {"text":"Go Go Go !!!","color":"yellow","bold":true}
tellraw @a[tag=inGame] {"text":"空中按下空格以打开降落伞","color":"yellow"}

#从pospoint中随机选一个作为center,调用模组缩圈
execute as @e[tag=pospoint, sort=random, limit=1] run data modify entity @s Tags append value "center"
execute at @e[tag=center, limit=1] run brzone start

#执行重置
function battleroyale:game/reset
