package net.harrison.battleroyale.init;

import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.networking.s2cpacket.MarkerDataS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {

    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id () {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(ResourceLocation.fromNamespaceAndPath(Battleroyale.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        registerC2SPackets(net);

        registerS2CPackets(net);

    }

    private static void registerC2SPackets(SimpleChannel net) {


    }

    private static void registerS2CPackets(SimpleChannel net) {
        net.messageBuilder(MarkerDataS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(MarkerDataS2CPacket::new)
                .encoder(MarkerDataS2CPacket::toBytes)
                .consumerMainThread(MarkerDataS2CPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToAllPlayer(MSG msg) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
    }
}
