package pe.fuji.gulag.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
   public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "gulag");
   public static final RegistryObject<EntityType<EntityTopo>> TOPO_GULAG = ENTITIES.register(
      "topo_gulag", () -> Builder.of(EntityTopo::new, MobCategory.MISC).sized(1.0F, 0.6F).build("topo_gulag")
   );
}
