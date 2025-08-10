package net.harrison.battleroyale.data.lootChestData;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;

public class EpicLootChestBlockPosData extends LootChestBlockPosData{
    private static final String ID = "epic_loot_container_data";

    public EpicLootChestBlockPosData() {
        super();
    }

    public static EpicLootChestBlockPosData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(EpicLootChestBlockPosData::create, EpicLootChestBlockPosData::new, ID);
    }

    public static EpicLootChestBlockPosData create(CompoundTag tag) {
        EpicLootChestBlockPosData data = new EpicLootChestBlockPosData();
        ListTag list = tag.getList("locations", Tag.TAG_COMPOUND);
        for (Tag posTag : list) {
            CompoundTag posCompound = (CompoundTag) posTag;
            data.getLocations().add(NbtUtils.readBlockPos(posCompound));
        }
        return data;
    }
}
