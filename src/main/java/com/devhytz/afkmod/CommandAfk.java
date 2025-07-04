package com.devhytz.afkmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CommandAfk extends CommandBase {

    private boolean running = false;

    @Override
    public String getCommandName() {
        return "afk";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/afk";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        final Minecraft mc = Minecraft.getMinecraft();
        final EntityPlayerSP player = mc.thePlayer;

        if (running) {
            player.addChatMessage(new ChatComponentText("\n" +
                    "You are now in AFK mode"));
            return;
        }

        player.addChatMessage(new ChatComponentText("\n" +
                "You are now in AFK mode"));
        running = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    player.sendChatMessage("/lobby classic");
                    Thread.sleep(2500);

                    int toggle = 0;

                    while (running) {
                        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                            player.addChatMessage(new ChatComponentText("AFK mode deactivated (Shift pressed)."));
                            running = false;
                            break;
                        }

                        mc.thePlayer.inventory.currentItem = 5;
                        Thread.sleep(300);

                        KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
                        Thread.sleep(500);

                        mc.thePlayer.inventory.currentItem = 8;
                        Thread.sleep(400);

                        KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
                        Thread.sleep(800);

                        int attempts = 0;
                        while (!(mc.currentScreen instanceof GuiContainer) && attempts < 10) {
                            Thread.sleep(200);
                            attempts++;
                        }

                        if (mc.currentScreen instanceof GuiContainer) {
                            GuiContainer gui = (GuiContainer) mc.currentScreen;

                            int[] quartzSlots = {0, 1};
                            int chosen = quartzSlots[toggle % 2];
                            toggle++;

                            mc.playerController.windowClick(gui.inventorySlots.windowId, chosen, 0, 0, mc.thePlayer);
                            Thread.sleep(1000);
                        }

                        mc.displayGuiScreen(null);
                        Thread.sleep(1000);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}