package pe.fuji.gulag.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent.Post;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "gulag",
   value = {Dist.CLIENT}
)
public class DrenadoOverlay {
   private static final ResourceLocation TEXTURA = ResourceLocation.tryParse("topo:textures/gui/carga_barra.png");
   private static final int DURACION_MAXIMA_S = 15;
   private static boolean activo = false;
   private static float tiempoRestanteS = 20.0F;
   private static float tiempoMostradoS = 20.0F;
   private static long tiempoUltimoTickNs;
   private static final float SUAVIZADO_FACTOR = 0.1F;
   private static boolean yaExplotado = false;
   private static long tiempoRestante = 0;



   private static long duracion;

   public static void iniciar(long tiempo) {
      duracion = tiempo;
   }

   public static void detenerDrenado() {
      activo = false;
      tiempoRestante = 0;
   }


   public static void actualizarDuracion(long nuevaDuracion) {
      duracion = nuevaDuracion;
   }

   public static void actualizarTiempo(long tiempoRestante) {
   }

   public static void iniciar(int segundosIniciales) {
      activo = true;
      yaExplotado = false;
      tiempoRestanteS = Math.min(segundosIniciales, DURACION_MAXIMA_S);
      tiempoMostradoS = tiempoRestanteS;
      tiempoUltimoTickNs = System.nanoTime();
   }


   public static void detener() {
      activo = false;
      tiempoRestanteS = 0.0F;
      tiempoMostradoS = 0.0F;
      tiempoUltimoTickNs = 0L;
   }

   public static void agregarTiempo(int segundos) {
      if (activo) {
         tiempoRestanteS = Math.min(tiempoRestanteS + segundos, DURACION_MAXIMA_S);
      }
   }

   @SubscribeEvent
   public static void render(Post event) {
      if (activo && !Minecraft.getInstance().options.hideGui) {
         long ahoraNs = System.nanoTime();
         float deltaS = (ahoraNs - tiempoUltimoTickNs) / 1_000_000_000.0F;
         tiempoUltimoTickNs = ahoraNs;

         tiempoRestanteS = Math.max(tiempoRestanteS - deltaS, 0.0F);
         tiempoMostradoS += (tiempoRestanteS - tiempoMostradoS) * SUAVIZADO_FACTOR;

         if (tiempoRestanteS <= 0.0F) {
            if (!yaExplotado) {
               yaExplotado = true;
               pe.fuji.gulag.network.ModNetworking.INSTANCE.sendToServer(new pe.fuji.gulag.network.DrenadoExplotarC2SPacket());
            }
            detener();
            return;
         }

         float progreso = tiempoMostradoS / DURACION_MAXIMA_S;
         int anchoPantalla = Minecraft.getInstance().getWindow().getGuiScaledWidth();
         int altoPantalla = Minecraft.getInstance().getWindow().getGuiScaledHeight();
         int barraAncho = 300;
         int barraAlto = 34;
         int x = (anchoPantalla - barraAncho) / 2;
         int y = altoPantalla - 80;

         RenderSystem.enableBlend();

         assert TEXTURA != null;

         event.getGuiGraphics().blit(TEXTURA, x, y, 0.0F, 0.0F, barraAncho, barraAlto, barraAncho, barraAlto);

         int progresoAncho = (int)((float)barraAncho * progreso);
         int progresoAlto = barraAlto - 8;
         int color;

         if (progreso > 0.75F) {
            color = 0xFF00FF00;
         } else if (progreso > 0.5F) {
            float f = (progreso - 0.5F) / 0.25F;
            color = interpolarColor(0xFFFFFF00, 0xFF00FF00, f);
         } else if (progreso > 0.25F) {
            float f = (progreso - 0.25F) / 0.25F;
            color = interpolarColor(0xFFFF8800, 0xFFFFFF00, f);
         } else if (progreso > 0.15F) {
            float f = (progreso - 0.15F) / 0.1F;
            color = interpolarColor(0xFFFF0000, 0xFFFF8800, f);
         } else {
            boolean parpadeo = (System.nanoTime() / 500_000_000L) % 2 == 0;
            color = parpadeo ? 0xFFFFFFFF : 0xFFFF0000;
         }

         event.getGuiGraphics().fill(x + 4, y + 4, x + 4 + Math.max(progresoAncho - 8, 0), y + 4 + progresoAlto, color);
         RenderSystem.disableBlend();
      }
   }


   private static int interpolarColor(int color1, int color2, float factor) {
      int a1 = (color1 >> 24) & 0xFF;
      int r1 = (color1 >> 16) & 0xFF;
      int g1 = (color1 >> 8) & 0xFF;
      int b1 = color1 & 0xFF;
      int a2 = (color2 >> 24) & 0xFF;
      int r2 = (color2 >> 16) & 0xFF;
      int g2 = (color2 >> 8) & 0xFF;
      int b2 = color2 & 0xFF;

      int a = (int)(a1 + (a2 - a1) * factor);
      int r = (int)(r1 + (r2 - r1) * factor);
      int g = (int)(g1 + (g2 - g1) * factor);
      int b = (int)(b1 + (b2 - b1) * factor);

      return (a << 24) | (r << 16) | (g << 8) | b;
   }
}