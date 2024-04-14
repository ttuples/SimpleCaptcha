package net.tuples.captcha;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "simplecaptcha")
public class CaptchaConfig implements ConfigData {
    public boolean enableCreepyCaptchas = true;
    public boolean soundEffects = true;
}