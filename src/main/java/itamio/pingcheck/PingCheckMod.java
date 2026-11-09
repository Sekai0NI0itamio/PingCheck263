package itamio.pingcheck;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// PingCheck — /pingcheck shows your latency (lowest RTT seen by the server).
// 1.20.1 Forge (official mappings): ServerPlayer.connection.getLatency().
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
                    ping = sp.connection.getLatency();
                }
                int finalPing = ping;
                ctx.getSource().sendSuccess(() -> Component.literal("Your ping: " + (finalPing < 0 ? "unknown" : finalPing + " ms")), false);
                return 1;
            }));
    }
}
