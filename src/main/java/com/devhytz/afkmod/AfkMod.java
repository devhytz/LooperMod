package com.devhytz.afkmod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.client.ClientCommandHandler;

@Mod(modid = AfkMod.MODID, name = AfkMod.NAME, version = AfkMod.VERSION)
public class AfkMod {
    public static final String MODID = "afkmod";
    public static final String NAME = "AFK Automation Mod";
    public static final String VERSION = "1.0";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new CommandAfk());
    }
}