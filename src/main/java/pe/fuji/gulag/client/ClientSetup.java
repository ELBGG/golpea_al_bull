package pe.fuji.gulag.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import pe.fuji.gulag.entity.ModEntities;
import pe.fuji.gulag.render.RenderTopo;

@EventBusSubscriber(
   modid = "gulag",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class ClientSetup {
   @SubscribeEvent
   public static void registerRenderers(RegisterRenderers event) {
      event.registerEntityRenderer(ModEntities.TOPO_GULAG.get(), RenderTopo::new);
   }
}
