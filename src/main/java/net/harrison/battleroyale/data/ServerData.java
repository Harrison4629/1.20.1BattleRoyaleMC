package net.harrison.battleroyale.data;

import net.harrison.battleroyale.init.ModMessages;
import net.harrison.battleroyale.networking.s2cpacket.MarkerDataS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;

public class ServerData extends SavedData {
    private static final String ID = "battleroyale_data";
    private BlockPos HobbyLocation;
    private final Set<BlockPos> PlatformLocations = new HashSet<>();

    public void addPlatform(BlockPos pos) {
        PlatformLocations.add(pos);
        setDirty();
        ModMessages.sendToAllPlayer(new MarkerDataS2CPacket(PlatformLocations, HobbyLocation));
    }

    public void removePlatform(BlockPos pos) {
        PlatformLocations.remove(pos);
        setDirty();
        ModMessages.sendToAllPlayer(new MarkerDataS2CPacket(PlatformLocations, HobbyLocation));
    }

    public void setHobbyLocation(BlockPos pos) {
        HobbyLocation = pos;
        setDirty();
        ModMessages.sendToAllPlayer(new MarkerDataS2CPacket(PlatformLocations, HobbyLocation));
    }

    public Set<BlockPos> getPlatformLocations() {
        return PlatformLocations;
    }

    public BlockPos getHobbyLocation() {
        return HobbyLocation;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag listTag= new ListTag();
        for (BlockPos blockPos : PlatformLocations) {
            listTag.add(NbtUtils.writeBlockPos(blockPos));
        }
        tag.put("platform_locations", listTag);
        if (HobbyLocation != null) {
            tag.put("hobby_location", NbtUtils.writeBlockPos(HobbyLocation));
        }
        return tag;
    }


    public static ServerData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(ServerData::create, ServerData::new, ID);
    }

    public static ServerData create(CompoundTag tag) {
        ServerData data = new ServerData();
        ListTag list = tag.getList("platform_locations", Tag.TAG_COMPOUND);
        for (Tag posTag : list) {
            CompoundTag posCompound = (CompoundTag) posTag;
            data.PlatformLocations.add(NbtUtils.readBlockPos(posCompound));
        }

        if (tag.contains("hobby_location")) {
            data.HobbyLocation = NbtUtils.readBlockPos(tag.getCompound("hobby_location"));
        }


        return data;
    }
}
