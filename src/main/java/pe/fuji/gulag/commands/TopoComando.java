package pe.fuji.gulag.commands;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import pe.fuji.gulag.entity.EntityTopo;
import pe.fuji.gulag.network.DrenadoManager;
import pe.fuji.gulag.network.DrenadoStartPacket;
import pe.fuji.gulag.network.DrenadoStopPacket;
import pe.fuji.gulag.network.ModNetworking;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

public class TopoComando {

   private static final Random RANDOM = new Random();
   private static final ScheduledExecutorService programadorTareas = Executors.newScheduledThreadPool(500);
   private static volatile boolean cicloActivo = false;
   private static final Map<EntityTopo, Future<?>> tareasTopos = new ConcurrentHashMap<>();
   private static final Path ARCHIVO_JUGADORES = Paths.get("config/golpeabull/jugadores_drenado.json");
   private static final double RADIO_BUSQUEDA = 100.0;

   // Retrasos configurables
   private static final int RETRASO_BASE_INICIAL = 500;
   private static final int RETRASO_ALEATORIO_MAXIMO = 500;

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
              Commands.literal("topo")
                      .requires(source -> source.hasPermission(2))
                      .then(Commands.literal("start")
                              .executes(context -> iniciarCicloTopos(context.getSource())))
                      .then(Commands.literal("stop")
                              .executes(context -> detenerCicloTopos(context.getSource())))
      );
   }

   private static int iniciarCicloTopos(CommandSourceStack source) throws CommandSyntaxException {
      ServerPlayer jugador = source.getPlayerOrException();

      if (cicloActivo) {
         enviarMensaje(jugador, "El ciclo de topos ya está corriendo.");
         return 1;
      }

      cicloActivo = true;
      enviarMensaje(jugador, "Iniciando ciclo de topos cercanos...");

      ServerLevel nivel = jugador.serverLevel();
      List<EntityTopo> toposCercanos = buscarToposCercanos(jugador, RADIO_BUSQUEDA);

      Collections.shuffle(toposCercanos, RANDOM);

      for (EntityTopo topo : toposCercanos) {
         if (!tareasTopos.containsKey(topo)) {
            int retraso = RETRASO_BASE_INICIAL + RANDOM.nextInt(RETRASO_ALEATORIO_MAXIMO);
            programadorTareas.schedule(() -> {
               Future<?> tarea = programadorTareas.submit(new TopoTask(topo));
               tareasTopos.put(topo, tarea);
            }, retraso, TimeUnit.MILLISECONDS);
         }
      }
      iniciarBarrasDrenado(nivel, jugador);
      return 1;
   }

   private static int detenerCicloTopos(CommandSourceStack source) throws CommandSyntaxException {
      ServerPlayer jugador = source.getPlayerOrException();

      if (!cicloActivo) {
         enviarMensaje(jugador, "El ciclo de topos no está activo.");
         return 1;
      }

      cicloActivo = false;

      for (EntityTopo topo : buscarToposCercanos(jugador, RADIO_BUSQUEDA)) {
         Future<?> tarea = tareasTopos.remove(topo);
         if (tarea != null) {
            tarea.cancel(true);
         }
      }

      cerrarCofresCercanos(jugador);
      detenerDrenadoParaTodos(jugador.serverLevel());

      enviarMensaje(jugador, "Se detuvo el ciclo de topos cercanos y regresaron a sus madrigueras.");
      return 1;
   }

   private static void detenerDrenadoParaTodos(ServerLevel nivel) {
      for (ServerPlayer jugador : nivel.players()) {
         DrenadoManager.detenerPara(jugador);
         ModNetworking.sendToPlayer(new DrenadoStopPacket(), jugador);
      }
   }

   private static List<EntityTopo> buscarToposCercanos(ServerPlayer jugador, double radio) {
      return jugador.level().getEntitiesOfClass(EntityTopo.class,
              jugador.getBoundingBox().inflate(radio, radio, radio),
              topo -> topo.distanceToSqr(jugador) <= radio * radio);
   }

   private static void iniciarBarrasDrenado(ServerLevel nivel, ServerPlayer jugador) {
      if (!Files.exists(ARCHIVO_JUGADORES)) {
         return;
      }

      try {
         List<String> nombres = new Gson().fromJson(Files.newBufferedReader(ARCHIVO_JUGADORES),
                 new TypeToken<List<String>>() {}.getType());
         for (ServerPlayer jugadorCercano : nivel.getEntitiesOfClass(ServerPlayer.class,
                 jugador.getBoundingBox().inflate(RADIO_BUSQUEDA, RADIO_BUSQUEDA, RADIO_BUSQUEDA))) {
            if (nombres.contains(jugadorCercano.getName().getString())) {
               DrenadoManager.iniciarPara(jugadorCercano);
               ModNetworking.sendToPlayer(new DrenadoStartPacket(), jugadorCercano);
            }
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private static void cerrarCofresCercanos(ServerPlayer jugador) {
      ServerLevel nivel = jugador.serverLevel();
      for (EntityTopo topo : buscarToposCercanos(jugador, RADIO_BUSQUEDA)) {
         topo.cerrarCofre();
      }
   }

   private static void enviarMensaje(ServerPlayer jugador, String mensaje) {
      jugador.sendSystemMessage(Component.literal(mensaje));
   }

   private record TopoTask(EntityTopo topo) implements Runnable {

      @Override
      public void run() {
         try {
            while (cicloActivo && topo.isAlive()) {
               if (!topo.estaAbierto()) {
                  topo.abrirCofre();
               }

               sleepSeconds(3 + RANDOM.nextInt(2));
               if (!cicloActivo || !topo.isAlive()) {
                  break;
               }

               topo.cerrarCofre();
               sleepSeconds(3 + RANDOM.nextInt(6));
            }
         } catch (InterruptedException | CancellationException ignored) {
         }
      }

      private void sleepSeconds(int seconds) throws InterruptedException {
         TimeUnit.SECONDS.sleep(seconds);
      }
   }
}