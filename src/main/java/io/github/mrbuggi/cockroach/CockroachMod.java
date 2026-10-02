package io.github.mrbuggi.cockroach;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CockroachMod implements ModInitializer {
    public static final String MOD_ID = "cockroach";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        CockroachNetworking.registerServerReceiver();
        LOGGER.info("Cockroach mod initialized");
    }
}
