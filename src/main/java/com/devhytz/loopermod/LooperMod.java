package com.devhytz.loopermod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Mod(modid = "loopermod", name = "LooperMod", version = "1.0")
public class LooperMod {

    private boolean shouldFeed = false;
    private Thread feedThread = null;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final AtomicBoolean guiReady = new AtomicBoolean(false);
    private final LinkedHashMap<String, int[]> mascotas = new LinkedHashMap<>();
    private int currentSlot = -1;

    private void cargarMascotas() {
        mascotas.clear();
        mascotas.put("silverfish", new int[]{11, 27, 33});
        mascotas.put("cat_black", new int[]{12, 28, 34});
        mascotas.put("cat_red", new int[]{12, 28, 34});
        mascotas.put("cat_siamese", new int[]{12, 28, 34}); //-------------
        mascotas.put("cat_black_baby", new int[]{11, 28, 35});
        mascotas.put("cat_red_baby", new int[]{11, 28, 35});
        mascotas.put("cat_siamese_baby", new int[]{11, 28, 35});
        mascotas.put("wild_ocelot", new int[]{12, 28, 34});
        mascotas.put("wild_ocelot_baby", new int[]{12, 28, 35}); //------------
        mascotas.put("wolf", new int[]{15, 27, 35 });
        mascotas.put("wolf_baby", new int[]{10, 28, 34});
        mascotas.put("bat", new int[]{1, 27, 34});
        mascotas.put("villager_blacksmith", new int[]{14, 29, 44});
        mascotas.put("villager_blacksmith_baby", new int[]{2, 29, 34});
        mascotas.put("villager_butcher", new int[]{14, 29, 44});
        mascotas.put("villager_butcher_baby", new int[]{10, 28, 34});
        mascotas.put("villager_farmer", new int[]{4, 28, 42});
        mascotas.put("villager_farmer_baby", new int[]{11, 28, 33});
        mascotas.put("villager_librarian", new int[]{0, 27, 43}); //-----
        mascotas.put("villager_librarian_baby", new int[]{10, 28, 42});
        mascotas.put("villager_priest", new int[]{9, 27, 43});
        mascotas.put("villager_priest_baby", new int[]{11, 28, 42});
        mascotas.put("zombie_villager", new int[]{16, 29, 44});
        mascotas.put("witch", new int[]{2, 28, 42});
        mascotas.put("zombie", new int[]{16, 29,44});
        mascotas.put("zombie_baby", new int[]{11, 28, 44});
        mascotas.put("frozen_zombie", new int[]{16, 27, 33});
        mascotas.put("growing_zombie", new int[]{16, 27, 33});
        mascotas.put("red_helper", new int[]{10, 28, 43});
        mascotas.put("green_helper", new int[]{11, 28, 34}); //-----
        mascotas.put("snowman", new int[]{3, 27, 33});
        mascotas.put("herobrine", new int[]{10, 28, 44});
        mascotas.put("endermite", new int[]{11, 29, 44});
        mascotas.put("mini_wither", new int[]{16, 29, 44});
        mascotas.put("bee", new int[]{6, 27, 34});
        mascotas.put("spider", new int[]{16, 27, 35});
        mascotas.put("cave_spider", new int[]{14, 27, 44});
        mascotas.put("bouncy_spider", new int[]{14, 27, 44});
        mascotas.put("snowman_jockey", new int[]{3, 27, 33});
        mascotas.put("chicken", new int[]{6, 27, 43});
        mascotas.put("chicken_baby", new int[]{10, 28, 33}); //--------
        mascotas.put("cow", new int[]{8, 28, 42});
        mascotas.put("cow_baby", new int[]{10, 28, 34});
        mascotas.put("mooshroom", new int[]{5, 28,42});
        mascotas.put("mooshroom_baby", new int[]{10, 28, 42});
        mascotas.put("creeper", new int[]{11, 29, 35});
        mascotas.put("creeper_powered", new int[]{11, 29, 44});
        mascotas.put("horse_black", new int[]{0, 27, 35});
        mascotas.put("horse_brown", new int[]{0, 27, 35});
        mascotas.put("horse_chestnut", new int[]{0, 27, 35});
        mascotas.put("horse_dark_brown", new int[]{0, 27, 35});
        mascotas.put("horse_grey", new int[]{0, 27, 35}); //---------
        mascotas.put("horse_creamy", new int[]{0, 27, 35});
        mascotas.put("horse_white", new int[]{0, 27, 35});
        mascotas.put("brown_horse_baby", new int[]{11, 28, 35});
        mascotas.put("horse_chestnut_baby", new int[]{11, 28, 35});
        mascotas.put("horse_dark_brown_baby", new int[]{11, 28, 35});
        mascotas.put("horse_creamy_baby",new int[]{11, 28, 35});
        mascotas.put("horse_gray_baby", new int[]{11, 28, 35});
        mascotas.put("horse_undead", new int[]{16, 29, 33});
        mascotas.put("mule", new int[]{7, 27, 35});
        mascotas.put("donkey",  new int[]{7, 27, 35});
        mascotas.put("black_rabbit", new int[]{3, 27, 33}); //------------
        mascotas.put("black_white_rabbit", new int[]{3, 27, 34});
        mascotas.put("brown_rabbit", new int[]{3, 27, 33});
        mascotas.put("gold_rabbit", new int[]{3, 27, 34});
        mascotas.put("salt_pepper_rabbit", new int[]{3, 27, 34});
        mascotas.put("white_rabbit", new int[]{3, 27, 33});
        mascotas.put("rabbit_jockey", new int[]{3, 27, 33});
        mascotas.put("pig", new int[]{0, 28, 43});
        mascotas.put("pig_baby", new int[]{0, 28, 35});
        mascotas.put("pig_zombie", new int[]{1, 29, 43});
        mascotas.put("pig_zombie_baby", new int[]{10, 28, 44});
        mascotas.put("sheep_black", new int[]{8, 27, 35}); //---------
        mascotas.put("sheep_white", new int[]{8, 27, 35});
        mascotas.put("sheep_gray", new int[]{8, 27, 35});
        mascotas.put("sheep_brown", new int[]{8, 27, 35});
        mascotas.put("sheep_silver", new int[]{8, 27, 35});
        mascotas.put("sheep_orange", new int[]{8, 27, 35});
        mascotas.put("sheep_magenta", new int[]{8, 27, 35});
        mascotas.put("sheep_light_blue", new int[]{8, 27, 35});
        mascotas.put("sheep_yellow", new int[]{8, 27, 35});
        mascotas.put("sheep_lime", new int[]{8, 27, 35});
        mascotas.put("sheep_cyan",new int[]{8, 27, 35});
        mascotas.put("sheep_purple", new int[]{8, 27, 35});
        mascotas.put("sheep_blue", new int[]{8, 27, 35});
        mascotas.put("sheep_green", new int[]{8, 27, 35});
        mascotas.put("sheep_red", new int[]{8, 27, 35});
        mascotas.put("sheep_pink", new int[]{8, 27, 35});
        mascotas.put("sheep_rainbow", new int[]{8, 27, 35}); //----------
        mascotas.put("merry_sheep", new int[]{1, 28, 43});
        mascotas.put("pastel_sheep", new int[]{1, 28, 43});
        mascotas.put("sheep_black_baby", new int[]{1, 28, 43});
        mascotas.put("sheep_white_baby", new int[]{1, 28, 43});
        mascotas.put("sheep_gray_baby", new int[]{1, 28, 43});
        mascotas.put("sheep_brown_baby", new int[]{1, 28, 43});
        mascotas.put("sheep_silver_baby", new int[]{1, 28, 43});
        mascotas.put("sheep_orange_baby", new int[]{1, 28, 43});
        mascotas.put("sheep_magenta_baby", new int[]{1, 28, 43});
        mascotas.put("sheep_light_blue_baby", new int[]{1, 28, 43});
        mascotas.put("sheep_yellow_baby", new int[]{1, 28, 43});
        mascotas.put("sheep_lime_baby", new int[]{1, 28, 43});
        mascotas.put("sheep_cyan_baby", new int[]{1, 28, 43});
        mascotas.put("sheep_purple_baby", new int[]{1, 28, 43});
        mascotas.put("sheep_blue_baby", new int[]{1, 28, 43});
        mascotas.put("sheep_green_baby", new int[]{1, 28, 43});
        mascotas.put("sheep_red_baby", new int[]{1, 28, 43});
        mascotas.put("sheep_pink_baby", new int[]{1, 28, 43}); //--------------
        mascotas.put("slime_big", new int[]{15, 28, 34});
        mascotas.put("slime_small", new int[]{6, 28, 33});
        mascotas.put("slime_tiny", new int[]{12, 28, 43});
        mascotas.put("magma_cube_big", new int[]{17, 29, 42});
        mascotas.put("magma_cube_small", new int[]{17, 29, 34});
        mascotas.put("magma_cube_tiny", new int[]{17, 28, 43});
        mascotas.put("skeleton", new int[]{16, 29, 43});
        mascotas.put("frozen_skeleton", new int[]{15, 27, 34});
        mascotas.put("smoldering_skeleton", new int[]{15, 27, 33});
        mascotas.put("fish", new int[]{12, 27, 33});
        mascotas.put("hay_bale", new int[]{8, 28, 33}); //-----------------
        mascotas.put("enderman", new int[]{4, 28, 34});
        mascotas.put("iron_golem", new int[]{6, 29, 34});
        mascotas.put("iron_golem_rose", new int[]{6, 29, 34});
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new CommandFeedPets());
        ClientCommandHandler.instance.registerCommand(new CommandStopFeed());
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (!shouldFeed) return;

        GuiScreen gui = event.gui;
        if (gui instanceof GuiChest) {
            GuiChest chest = (GuiChest) gui;
            String title = chest.inventorySlots.getSlot(0).inventory.getDisplayName().getUnformattedText();
            if ("Pet Consumables".equals(title)) {
                guiReady.set(true);
                final int slotToClick = currentSlot;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                            mc.addScheduledTask(new Runnable() {
                                @Override
                                public void run() {
                                    mc.playerController.windowClick(
                                            chest.inventorySlots.windowId,
                                            slotToClick,
                                            0,
                                            0,
                                            mc.thePlayer
                                    );

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Thread.sleep(2300);
                                                mc.addScheduledTask(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mc.thePlayer.closeScreen();
                                                    }
                                                });
                                            } catch (InterruptedException ignored) {}
                                        }
                                    }).start();
                                }
                            });
                        } catch (InterruptedException ignored) {}
                    }
                }).start();
            }
        }
    }

    private int modoFeed = 1;

    private void alimentarMascotas() {
        feedThread = new Thread(() -> {
            try {
                for (Map.Entry<String, int[]> entry : mascotas.entrySet()) {
                    if (Thread.currentThread().isInterrupted()) return;

                    String nombreMascota = entry.getKey();
                    int[] slots = entry.getValue();

                    if (modoFeed == 3) {
                        for (int slot : slots) {
                            currentSlot = slot;
                            guiReady.set(false);
                            mc.thePlayer.sendChatMessage("/purchasepet " + nombreMascota);

                            int intentos = 0;
                            while (!guiReady.get() && intentos < 100) {
                                Thread.sleep(100);
                                intentos++;
                            }

                            Thread.sleep(5000);

                            for (int i = 0; i < 3; i++) {
                                final int clickSlot = slot;
                                mc.addScheduledTask(() -> {
                                    mc.playerController.windowClick(
                                            mc.thePlayer.openContainer.windowId,
                                            clickSlot,
                                            0,
                                            0,
                                            mc.thePlayer
                                    );
                                });
                                Thread.sleep(800);
                            }

                            mc.addScheduledTask(() -> mc.thePlayer.closeScreen());
                            Thread.sleep(1000);
                        }
                    } else {
                        for (int slot : slots) {
                            currentSlot = slot;
                            guiReady.set(false);
                            mc.thePlayer.sendChatMessage("/purchasepet " + nombreMascota);

                            int intentos = 0;
                            while (!guiReady.get() && intentos < 100) {
                                Thread.sleep(100);
                                intentos++;
                            }

                            Thread.sleep(3500);
                        }
                        Thread.sleep(1000);
                    }
                }
            } catch (InterruptedException ignored) {}
        });
        feedThread.start();
    }

    public class CommandFeedPets extends CommandBase {
        @Override
        public String getCommandName() {
            return "feedpets";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/feedpets";
        }



        @Override
        public void processCommand(ICommandSender sender, String[] args) throws CommandException {
            shouldFeed = true;
            cargarMascotas();

            if (args.length == 1) {
                try {
                    modoFeed = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    modoFeed = 1;
                }
            } else {
                modoFeed = 1;
            }

            alimentarMascotas();
        }


        @Override
        public int getRequiredPermissionLevel() {
            return 0;
        }
    }

    public class CommandStopFeed extends CommandBase {
        @Override
        public String getCommandName() {
            return "stopfeed";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/stopfeed";
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) throws CommandException {
            if (feedThread != null && feedThread.isAlive()) {
                feedThread.interrupt();
                feedThread = null;
            }
        }

        @Override
        public int getRequiredPermissionLevel() {
            return 0;
        }
    }
}