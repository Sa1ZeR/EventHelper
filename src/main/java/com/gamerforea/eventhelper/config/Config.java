package com.gamerforea.eventhelper.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<String> example_string = BUILDER.comment("Example string. Default is ez example")
            .define("Example string", "ez example");

    static {
        BUILDER.push("Config for EventHelper by gameforea; Ported by Sa1ZeR_");
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
