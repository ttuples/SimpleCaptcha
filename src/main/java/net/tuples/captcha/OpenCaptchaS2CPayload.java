package net.tuples.captcha;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record OpenCaptchaS2CPayload() implements CustomPayload {
    public static final Identifier OPEN_CAPTCHA_PAYLOAD_ID = Identifier.of(Captcha.MOD_ID, "open_captcha");
    public static final CustomPayload.Id<OpenCaptchaS2CPayload> ID = new CustomPayload.Id<>(OPEN_CAPTCHA_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, OpenCaptchaS2CPayload> CODEC = PacketCodec.unit(new OpenCaptchaS2CPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
