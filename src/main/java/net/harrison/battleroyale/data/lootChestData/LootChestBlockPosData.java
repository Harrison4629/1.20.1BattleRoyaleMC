package net.harrison.battleroyale.data.lootChestData;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.List;

public class LootChestBlockPosData extends SavedData {
    private static final String ID = "loot_container_data";
    private final List<BlockPos> locations = new ArrayList<>();

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (BlockPos pos : locations) {
            list.add(NbtUtils.writeBlockPos(pos));
        }
        tag.put("locations", list);
        return tag;
    }

    public void addLocation(BlockPos pos) {
        locations.add(pos);
        setDirty();
    }

    public void removeLocation(BlockPos pos) {
        locations.remove(pos);
        setDirty();
    }

    public List<BlockPos> getLocations() {
        return locations;
    }

    public static LootChestBlockPosData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(LootChestBlockPosData::create, LootChestBlockPosData::new, ID);
    }

    public static LootChestBlockPosData create(CompoundTag tag) {
        LootChestBlockPosData data = new LootChestBlockPosData();
        ListTag list = tag.getList("locations", Tag.TAG_COMPOUND);
        for (Tag posTag : list) {
            CompoundTag posCompound = (CompoundTag) posTag;
            data.locations.add(NbtUtils.readBlockPos(posCompound));
        }
        return data;
    }
}
