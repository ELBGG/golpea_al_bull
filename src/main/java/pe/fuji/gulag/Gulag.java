package pe.fuji.gulag;


import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import pe.fuji.gulag.commands.GulagAddcomand;
import pe.fuji.gulag.commands.TopoComando;
import pe.fuji.gulag.entity.ModEntities;
import pe.fuji.gulag.network.ModNetworking;

@Mod(Gulag.MODID)
public class Gulag {
    public static final String MODID = "gulag";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Gulag() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        ModEntities.ENTITIES.register(eventBus);
        ModNetworking.register();
    }


    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        GulagAddcomand.register(event.getDispatcher());
        TopoComando.register(event.getDispatcher());
    }
}