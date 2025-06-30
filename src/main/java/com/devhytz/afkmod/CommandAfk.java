package com.devhytz.afkmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
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
                    while (running) {
                        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                            player.addChatMessage(new ChatComponentText("Afk mode disabled"));
                            running = false;
                            break;
                        }

                        mc.thePlayer.sendChatMessage("/l classic");
                        Thread.sleep(1000);

                        mc.thePlayer.inventory.currentItem = 5;
                        Thread.sleep(300);

                        KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
                        Thread.sleep(300);

                        mc.thePlayer.inventory.currentItem = 8;
                        Thread.sleep(500);
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