package dev.tacoagha.mces;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MCESMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("mces");
    
    @Override
    public void onInitialize() {
        LOGGER.info("MCES Mod initialized!");
        // TODO: Initialize mod
    }
}
