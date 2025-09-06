package net.tuples.captcha;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Captcha {
    public static final String MOD_ID = "assets/captcha";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOGGER.info("Initializing Simple Captcha Mod");

        CommandRegistrationEvent.EVENT.register(((dispatcher, registry, selection) -> {
            dispatcher.register(
                    Commands.literal("captcha")
                            .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                            .executes(ctx -> {
                                CommandSourceStack source = ctx.getSource();
                                if (source.getEntity() instanceof ServerPlayer player) {
                                    NetworkManager.sendToPlayer(player, new OpenCaptchaS2CPayload());
                                } else {
                                    source.sendFailure(Component.literal("This command can only be run by a player."));
                                }
                                return 1;
                            })
                            .then(Commands.argument("player", EntityArgument.player()))
                            .executes(ctx -> {
                                ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
                                NetworkManager.sendToPlayer(player, new OpenCaptchaS2CPayload());
                                return 1;
                            })
            );
        }));
    }
}
