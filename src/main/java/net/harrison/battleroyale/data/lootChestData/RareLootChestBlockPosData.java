package net.harrison.battleroyale.data.lootChestData;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;

public class RareLootChestBlockPosData extends LootChestBlockPosData{
    private static final String ID = "rare_loot_container_data";

    public RareLootChestBlockPosData() {
        super();
    }

    public static RareLootChestBlockPosData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(RareLootChestBlockPosData::create, RareLootChestBlockPosData::new, ID);
    }

    public static RareLootChestBlockPosData create(CompoundTag tag) {
        RareLootChestBlockPosData data = new RareLootChestBlockPosData();
        ListTag list = tag.getList("locations", Tag.TAG_COMPOUND);
        for (Tag posTag : list) {
            CompoundTag posCompound = (CompoundTag) posTag;
            data.getLocations().add(NbtUtils.readBlockPos(posCompound));
        }
        return data;
    }
}
