package net.tuples.captcha;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OpenCaptchaS2CPayload() implements CustomPacketPayload {
    public static final Type<OpenCaptchaS2CPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Captcha.MOD_ID, "open_captcha"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenCaptchaS2CPayload> CODEC =
            StreamCodec.unit(new OpenCaptchaS2CPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
