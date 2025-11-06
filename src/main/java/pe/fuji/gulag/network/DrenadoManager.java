package pe.fuji.gulag.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;

public class DrenadoManager {
    private static final Set<UUID> jugadoresConDrenado = Collections.synchronizedSet(new HashSet<>());

    private static final Map<UUID, Long> tiempoPorJugador = Collections.synchronizedMap(new HashMap<>());

    public static void iniciarPara(ServerPlayer jugador) {
        UUID id = jugador.getUUID();
        if (!jugadoresConDrenado.contains(id)) {
            jugadoresConDrenado.add(id);
            ModNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> jugador), new DrenadoStartPacket());
        }
    }

    public static void agregarTiempo(ServerPlayer jugador, long segundos) {
        UUID id = jugador.getUUID();
        tiempoPorJugador.put(id, tiempoPorJugador.getOrDefault(id, 0L) + segundos);
    }

    public static long obtenerTiempo(ServerPlayer jugador) {
        return tiempoPorJugador.getOrDefault(jugador.getUUID(), 0L);
    }

    public static void reiniciarTiempo(ServerPlayer jugador) {
        tiempoPorJugador.remove(jugador.getUUID());
    }

    public static void detenerPara(ServerPlayer jugador) {
        jugadoresConDrenado.remove(jugador.getUUID());
    }

    public static boolean estaActivo(ServerPlayer jugador) {
        return jugadoresConDrenado.contains(jugador.getUUID());
    }

    public static Set<UUID> obtenerJugadoresConDrenado() {
        return jugadoresConDrenado;
    }

    public static void reiniciar() {
        jugadoresConDrenado.clear();
    }
}
