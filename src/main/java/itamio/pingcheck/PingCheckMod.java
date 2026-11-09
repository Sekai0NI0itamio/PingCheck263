package itamio.pingcheck;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
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
                int ping = ctx.getSource().getEntity().getLatency(); ctx.getSource().sendSuccess(() -> Component.literal("Your ping: " + ping + " ms"), false);
                return 1;
            }));
    }
}
