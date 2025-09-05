package net.tuples.captcha;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Captcha {
    public static final String MOD_ID = "captcha";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOGGER.info("Initializing Simple Captcha Mod");
    }
}
