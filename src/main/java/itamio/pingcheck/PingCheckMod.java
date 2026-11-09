package itamio.pingcheck;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
                if (ctx.getSource().getEntity() instanceof ServerPlayer sp) {
                    ServerGamePacketListenerImpl listener = sp.connection;
                    if (listener != null) {
                        ping = listener.latency;
                    }
                }
                int finalPing = ping;
                ctx.getSource().sendSuccess(() -> Component.literal("Your ping: " + (finalPing < 0 ? "unknown" : finalPing + " ms")), false);
                return 1;
            }));
    }
}
