package pe.fuji.gulag.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DrenadoExplotarC2SPacket {
    public DrenadoExplotarC2SPacket() {}
    public DrenadoExplotarC2SPacket(FriendlyByteBuf buf) {}
    public void encode(FriendlyByteBuf buf) {}

    public static void handle(DrenadoExplotarC2SPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.kill();
                ModNetworking.sendToPlayer(new DrenadoExplotePacket(), player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
