package net.harrison.battleroyale.networking.s2cpacket;

import net.harrison.battleroyale.data.ClientMarkerData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class MarkerDataS2CPacket {
    private final Set<BlockPos> platform_locations;
    private final BlockPos hobby_location;


    public MarkerDataS2CPacket(Set<BlockPos> platform_locations, BlockPos hobby_location) {
        this.hobby_location = hobby_location;
        this.platform_locations = platform_locations;
    }

    public MarkerDataS2CPacket(FriendlyByteBuf buf) {
        if (buf.readBoolean()) {
            this.hobby_location = buf.readBlockPos();
        } else {
            this.hobby_location = null;
        }
        int size = buf.readInt();
        Set<BlockPos> set = new HashSet<>();
        for (int i = 0; i< size; i++) {
            set.add(buf.readBlockPos());
        }
        this.platform_locations = set;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(this.hobby_location != null);
        if (this.hobby_location != null) {
            buf.writeBlockPos(this.hobby_location);
        }
        buf.writeInt(this.platform_locations.size());
        for (BlockPos pos : this.platform_locations) {
            buf.writeBlockPos(pos);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ClientMarkerData.hobby_location = this.hobby_location;
            ClientMarkerData.platform_locations = this.platform_locations;
        });
        context.setPacketHandled(true);
    }
}
