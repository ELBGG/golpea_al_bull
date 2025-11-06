package pe.fuji.gulag.server;

import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import pe.fuji.gulag.Gulag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Mod.EventBusSubscriber
public class ServerEvents {
   private static final Path ARCHIVO_JUGADORES = Paths.get("config/golpeabull/jugadores_drenado.json");

   @SubscribeEvent
   public static void onServerStarting(ServerStartingEvent event) {
      try {
         Files.createDirectories(ARCHIVO_JUGADORES.getParent());
         if (!Files.exists(ARCHIVO_JUGADORES)) {
            Files.write(ARCHIVO_JUGADORES, "[]".getBytes(), StandardOpenOption.CREATE_NEW);
            Gulag.LOGGER.info("Archivo jugadores_drenado.json creado.");
         }
      } catch (IOException var2) {
         Gulag.LOGGER.error("No se pudo crear jugadores_drenado.json", var2);
      }
   }
}
