package com.devhytz.loopermod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "loopermod", name = "LooperMod", version = "1.0")
public class LooperMod {

    private boolean shouldFeed = false;
    private int feedAmount = 3;
    private Thread feedThread = null;

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
            final GuiChest chest = (GuiChest) gui;
            String title = chest.inventorySlots.getSlot(0).inventory.getDisplayName().getUnformattedText();

            if ("Pet Consumables".equals(title)) {
                final Minecraft mc = Minecraft.getMinecraft();
                final int[] slots = {11, 27, 33};

                feedThread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            for (int i = 0; i < slots.length; i++) {
                                final int slot = slots[i];
                                for (int j = 0; j < feedAmount; j++) {
                                    if (Thread.currentThread().isInterrupted()) return;
                                    Thread.sleep(4000);

                                    mc.addScheduledTask(new Runnable() {
                                        public void run() {
                                            ItemStack heldItem = mc.thePlayer.inventory.getItemStack();
                                            if (heldItem == null) {
                                                mc.playerController.windowClick(
                                                        chest.inventorySlots.windowId,
                                                        slot,
                                                        0,
                                                        0,
                                                        mc.thePlayer
                                                );
                                            }
                                        }
                                    });
                                }
                            }
                        } catch (InterruptedException e) {

                        }
                    }
                });

                feedThread.start();
                shouldFeed = false;
            }
        }
    }

    public class CommandFeedPets extends CommandBase {
        @Override
        public String getCommandName() {
            return "feedpets";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/feedpets <veces>";
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) throws CommandException {
            if (args.length == 1) {
                try {
                    feedAmount = Integer.parseInt(args[0]);
                    shouldFeed = true;
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/purchasepet silverfish");
                } catch (NumberFormatException e) {
                    throw new CommandException("Invalid num");
                }
            } else {
                throw new CommandException("Use: /feedpets <times>");
            }
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
