package pe.fuji.gulag.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import pe.fuji.gulag.client.DrenadoOverlay;

import java.util.function.Supplier;

public class DrenadoTiempoPacket {
    private final int segundos;

    public DrenadoTiempoPacket(int segundos) {
        this.segundos = segundos;
    }

    public DrenadoTiempoPacket(FriendlyByteBuf buf) {
        this.segundos = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(segundos);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            DrenadoOverlay.agregarTiempo(segundos);
        });
        context.setPacketHandled(true);
    }
}
