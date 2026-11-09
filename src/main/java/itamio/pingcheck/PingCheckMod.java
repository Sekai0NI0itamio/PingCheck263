package itamio.pingcheck;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// /pingcheck — reports your latency floor as observed by the server.
// 1.20.1 Forge uses official mappings: the keepalive latency lives behind
// ServerPlayer.connection (ServerGamePacketListenerImpl). Its 1.20.1 accessor
// is `latency` field (private) — so we derive ping from the packet handler's
// KeepAlive bookkeeping instead via the network stack to stay mapping-proof.
@Mod("pingcheck")
public class PingCheckMod {
    public PingCheckMod() {
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("pingcheck").requires(s -> s.hasPermission(0))
            .executes(ctx -> {
                int ping = -1;
                if (ctx.getSource().getEntity() instanceof ServerPlayer sp && sp.connection != null) {
                    // 1.20.1 official: ServerGamePacketListenerImpl.latency is private -> use reflection-free path:
                    // the player's connection tracks keepalive in `latency` — accessible through getLatency() in Yarn,
                    // but in official 1.20.1 it is field `latency` accessed via `sp.connection.latency`.
                    // Reflection fallback keeps this compiling on all mappings:
                    try {
                        var f = sp.connection.getClass().getDeclaredField("latency");
                        f.setAccessible(true);
                        ping = f.getInt(sp.connection);
                    } catch (Exception ignored) {
                        ping = -1;
                    }
                }
                int finalPing = ping;
                ctx.getSource().sendSuccess(() -> Component.literal("Your ping: " + (finalPing < 0 ? "unknown" : finalPing + " ms")), false);
                return 1;
            }));
    }
}
