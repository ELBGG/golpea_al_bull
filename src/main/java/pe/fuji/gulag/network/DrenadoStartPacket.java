package pe.fuji.gulag.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DrenadoStartPacket {
    public DrenadoStartPacket() {}

    public DrenadoStartPacket(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public static void handle(DrenadoStartPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            pe.fuji.gulag.client.DrenadoOverlay.iniciar(20);
        });
        ctx.get().setPacketHandled(true);
    }
}
