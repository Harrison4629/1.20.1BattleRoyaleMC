package net.harrison.battleroyale.data.lootChestData;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;

public class CommonLootChestBlockPosData extends LootChestBlockPosData {
    private static final String ID = "common_loot_container_data";

    public CommonLootChestBlockPosData() {
        super();
    }

    public static CommonLootChestBlockPosData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(CommonLootChestBlockPosData::create, CommonLootChestBlockPosData::new, ID);
    }

    public static CommonLootChestBlockPosData create(CompoundTag tag) {
        CommonLootChestBlockPosData data = new CommonLootChestBlockPosData();
        ListTag list = tag.getList("locations", Tag.TAG_COMPOUND);
        for (Tag posTag : list) {
            CompoundTag posCompound = (CompoundTag) posTag;
            data.getLocations().add(NbtUtils.readBlockPos(posCompound));
        }
        return data;
    }
}
