package pe.fuji.gulag.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkDirection;

import java.util.Optional;

public class ModNetworking {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("gulag", "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    private static int id = 0;

    public static void register() {
        INSTANCE.registerMessage(id++, DrenadoExplotePacket.class,
                DrenadoExplotePacket::encode,
                DrenadoExplotePacket::new,
                DrenadoExplotePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );

        INSTANCE.registerMessage(id++, DrenadoStartPacket.class,
                (msg, buf) -> {
                },
                DrenadoStartPacket::new,
                DrenadoStartPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );

        INSTANCE.registerMessage(id++,
                DrenadoStartPacket.class,
                DrenadoStartPacket::encode,
                DrenadoStartPacket::new,
                DrenadoStartPacket::handle
        );
        INSTANCE.registerMessage(id++, DrenadoSyncPacket.class,
                DrenadoSyncPacket::encode,
                DrenadoSyncPacket::new,
                DrenadoSyncPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );

        INSTANCE.registerMessage(id++, DrenadoTiempoPacket.class,
                DrenadoTiempoPacket::toBytes,
                DrenadoTiempoPacket::new,
                DrenadoTiempoPacket::handle);
        INSTANCE.registerMessage(id++, DrenadoStartPacket.class,
                DrenadoStartPacket::encode,
                DrenadoStartPacket::new,
                DrenadoStartPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );

        INSTANCE.registerMessage(id++, DrenadoExplotarC2SPacket.class,
                (msg, buf) -> {},
                DrenadoExplotarC2SPacket::new,
                DrenadoExplotarC2SPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );

        INSTANCE.registerMessage(id++, DrenadoStopPacket.class, DrenadoStopPacket::encode, DrenadoStopPacket::decode, DrenadoStopPacket::handle);

    }

    public static void sendToPlayer(Object packet, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);

    }
}
