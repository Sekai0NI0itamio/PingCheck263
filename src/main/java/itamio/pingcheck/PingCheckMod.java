package itamio.pingcheck;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
                    ping = ctx.getSource().getServer().getPlayerList().getPlayer(sp.getUUID()) != null
                        ? ctx.getSource().getServer().getPlayerList().getPlayer(sp.getUUID()).getPlayerListEntry().getLatency()
                        : -1;
                }
                int finalPing = ping;
                ctx.getSource().sendSuccess(() -> Component.literal("Your ping: " + finalPing + " ms"), false);
                return 1;
            }));
    }
}
