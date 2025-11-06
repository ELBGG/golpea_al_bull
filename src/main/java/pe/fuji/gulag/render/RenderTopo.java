package pe.fuji.gulag.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import pe.fuji.gulag.entity.EntityTopo;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@EventBusSubscriber(
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class RenderTopo extends GeoEntityRenderer<EntityTopo> {
   public RenderTopo(Context context) {
      super(context, new ModeloTopo());
   }

   public static class ModeloTopo extends GeoModel<EntityTopo> {
      public ResourceLocation getModelResource(EntityTopo object) {
         return ResourceLocation.tryParse("topo:geo/topogulag.geo.json");
      }

      public ResourceLocation getTextureResource(EntityTopo object) {
         return ResourceLocation.tryParse("topo:textures/entity/bull.png");
      }

      public ResourceLocation getAnimationResource(EntityTopo object) {
         return ResourceLocation.tryParse("topo:animations/topogulag.animation.json");
      }
   }
}
