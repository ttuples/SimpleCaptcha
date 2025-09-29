package net.tuples.captcha.commands;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.tuples.captcha.Captcha;

public class CaptchaCommands {
    public static void initialize() {
        CommandRegistrationEvent.EVENT.register(((dispatcher, registry, selection) -> {
            dispatcher.register(
                    Commands.literal("captcha")
                            .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                            .executes(ctx -> {
                                try {
                                    CommandSourceStack source = ctx.getSource();
                                    if (source.getEntity() instanceof ServerPlayer player) {
                                        NetworkManager.sendToPlayer(player, Captcha.OPEN_CAPTCHA, new FriendlyByteBuf(Unpooled.buffer()));
                                    } else {
                                        source.sendFailure(Component.literal("This command can only be run by a player."));
                                    }
                                } catch (Exception e) {
                                    Captcha.LOGGER.error("Error running self captcha command", e);
                                }
                                return 1;
                            })
                            .then(Commands.argument("player", EntityArgument.player())
                                    .executes(ctx -> {
                                        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
                                        NetworkManager.sendToPlayer(player, Captcha.OPEN_CAPTCHA, new FriendlyByteBuf(Unpooled.buffer()));
                                        return 1;
                                    })
                            )
            );
        }));
    }
}
