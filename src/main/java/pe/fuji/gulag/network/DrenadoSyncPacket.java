package pe.fuji.gulag.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import pe.fuji.gulag.client.DrenadoOverlay;

import java.util.function.Supplier;

public class DrenadoSyncPacket {
    private final long tiempo;

    public DrenadoSyncPacket(long tiempo) {
        this.tiempo = tiempo;
    }

    public DrenadoSyncPacket(FriendlyByteBuf buf) {
        this.tiempo = buf.readLong();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(tiempo);
    }

    public static void handle(DrenadoSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DrenadoOverlay.actualizarTiempo(msg.tiempo);
        });
        ctx.get().setPacketHandled(true);
    }
}
