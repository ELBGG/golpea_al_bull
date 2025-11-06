package pe.fuji.gulag.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import pe.fuji.gulag.client.DrenadoOverlay;
import java.util.function.Supplier;

public class DrenadoStopPacket {
    public DrenadoStopPacket() {}

    public static void encode(DrenadoStopPacket pkt, FriendlyByteBuf buf) {}

    public static DrenadoStopPacket decode(FriendlyByteBuf buf) {
        return new DrenadoStopPacket();
    }

    public static void handle(DrenadoStopPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DrenadoOverlay.detenerDrenado();
        });
        ctx.get().setPacketHandled(true);
    }
}
