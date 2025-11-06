package pe.fuji.gulag.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DrenadoExplotePacket {
    public DrenadoExplotePacket() {}

    public DrenadoExplotePacket(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public static void handle(DrenadoExplotePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(DrenadoExplotePacket::mostrarExplosionCliente);
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void mostrarExplosionCliente() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            assert mc.level != null;
            mc.level.addParticle(
                    ParticleTypes.EXPLOSION,
                    mc.player.getX(), mc.player.getY() + mc.player.getBbHeight() / 2, mc.player.getZ(),
                    1.0, 0.0, 0.0
            );
        }
    }
}
