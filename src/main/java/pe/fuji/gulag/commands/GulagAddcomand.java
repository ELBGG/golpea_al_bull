package pe.fuji.gulag.commands;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GulagAddcomand {
    private static final Path ARCHIVO_JUGADORES = Paths.get("config/golpeabull/jugadores_drenado.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("gulag")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("add").then(
                                Commands.argument("targets", EntityArgument.players())
                                        .executes(ctx -> agregarJugadores(ctx, EntityArgument.getPlayers(ctx, "targets")))
                        ))
        );
    }

    private static int agregarJugadores(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> jugadoresSeleccionados) {
        try {
            Files.createDirectories(ARCHIVO_JUGADORES.getParent());
            List<String> jugadores = cargarJugadores();

            List<String> jugadoresAnadidos = new ArrayList<>();
            for (ServerPlayer jugador : jugadoresSeleccionados) {
                String nombreJugador = jugador.getGameProfile().getName();
                if (!jugadores.contains(nombreJugador)) {
                    jugadores.add(nombreJugador);
                    jugadoresAnadidos.add(nombreJugador);
                }
            }

            guardarJugadores(jugadores);

            if (!jugadoresAnadidos.isEmpty()) {
                enviarMensajeExito(ctx.getSource(), "Jugadores añadidos al gulag: " + String.join(", ", jugadoresAnadidos));
            } else {
                enviarMensajeError(ctx.getSource(), "Todos los jugadores seleccionados ya estaban en el gulag.");
            }
        } catch (IOException e) {
            enviarMensajeError(ctx.getSource(), "Error al escribir en jugadores_drenado.json");
            e.printStackTrace();
        }
        return 1;
    }

    private static List<String> cargarJugadores() throws IOException {
        if (Files.exists(ARCHIVO_JUGADORES)) {
            return GSON.fromJson(Files.newBufferedReader(ARCHIVO_JUGADORES), new TypeToken<List<String>>() {}.getType());
        }
        return new ArrayList<>();
    }

    private static void guardarJugadores(List<String> jugadores) throws IOException {
        Files.writeString(ARCHIVO_JUGADORES, GSON.toJson(jugadores));
    }

    private static void enviarMensajeExito(CommandSourceStack source, String mensaje) {
        source.sendSuccess(() -> Component.literal(mensaje), false);
    }

    private static void enviarMensajeError(CommandSourceStack source, String mensaje) {
        source.sendFailure(Component.literal(mensaje));
    }
}