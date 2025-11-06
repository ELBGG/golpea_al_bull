package pe.fuji.gulag.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EntityTopo extends Entity implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private static final EntityDataAccessor<Boolean> FUERA = SynchedEntityData.defineId(EntityTopo.class, EntityDataSerializers.BOOLEAN);

   public EntityTopo(EntityType<? extends Entity> entityType, Level world) {
      super(entityType, world);
   }

   protected void defineSynchedData() {
      this.entityData.define(FUERA, false);
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
   }

   private <E extends GeoEntity> PlayState predicate(AnimationState<E> event) {
      if (this.entityData.get(FUERA)) {
         event.getController().setAnimation(RawAnimation.begin().thenPlay("outhole"));
      } else {
         event.getController().setAnimation(RawAnimation.begin().thenPlay("inhole"));
      }

      return PlayState.CONTINUE;
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public void abrirCofre() {
      this.entityData.set(FUERA, true);
   }

   public void cerrarCofre() {
      this.entityData.set(FUERA, false);
   }

   public boolean estaAbierto() {
      return this.entityData.get(FUERA);
   }

   public boolean isPickable() {
      return true;
   }

    protected void readAdditionalSaveData(CompoundTag compound) {
      if (compound.contains("Fuera")) {
         this.entityData.set(FUERA, compound.getBoolean("Fuera"));
      }
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putBoolean("Fuera", this.entityData.get(FUERA));
   }

   public void tick() {
      super.tick();
   }
}
