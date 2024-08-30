package net.tuples.captcha;

import io.netty.buffer.Unpooled;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.tuples.captcha.sounds.CaptchaSounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Captcha implements ModInitializer {
	public  static final String MOD_ID = "captcha";
    public static final Logger LOGGER = LoggerFactory.getLogger("captcha");

	public static final Identifier OPEN_CAPTCHA = new Identifier(Captcha.MOD_ID, "open_captcha");

	public static CaptchaConfig config;

	@Override
	public void onInitialize() {
		CaptchaSounds.initializeSounds();
		AutoConfig.register(CaptchaConfig.class, JanksonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(CaptchaConfig.class).getConfig();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("captcha")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.executes(context -> {
					ServerCommandSource source = context.getSource();
					if (source.getEntity() instanceof ServerPlayerEntity player) {
						player.networkHandler.sendPacket(new CustomPayloadS2CPacket(OPEN_CAPTCHA, new PacketByteBuf(Unpooled.buffer())));
					} else {
						source.sendError(Text.literal("This command can only be run by a player."));
					}
					return 1;
				})
				.then(argument("player", EntityArgumentType.player())
						.executes(context -> {
							ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
							player.networkHandler.sendPacket(new CustomPayloadS2CPacket(OPEN_CAPTCHA, new PacketByteBuf(Unpooled.buffer())));
							return 1;
						})
				)
		));
	}
}