package pe.fuji.gulag.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import pe.fuji.gulag.entity.EntityTopo;
import pe.fuji.gulag.network.DrenadoManager;
import pe.fuji.gulag.network.DrenadoSyncPacket;
import pe.fuji.gulag.network.DrenadoTiempoPacket;
import pe.fuji.gulag.network.ModNetworking;

@EventBusSubscriber
public class TopoPlayerHandler {
   @SubscribeEvent
   public static void onPlayerAttack(AttackEntityEvent event) {
      if (event.getEntity() instanceof ServerPlayer serverPlayer) {
         if (event.getTarget() instanceof EntityTopo topo && topo.estaAbierto()) {
            event.setCanceled(true);

            DrenadoManager.agregarTiempo(serverPlayer, 1);

            long tiempoActual = DrenadoManager.obtenerTiempo(serverPlayer);

            ModNetworking.sendToPlayer(new DrenadoSyncPacket(tiempoActual), serverPlayer);
            ModNetworking.sendToPlayer(new DrenadoTiempoPacket(1), serverPlayer);


            topo.cerrarCofre();
         }
      }
   }

}
