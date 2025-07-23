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
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

@Mod(modid = "loopermod", name = "LooperMod", version = "1.0")
public class LooperMod {

    private String apiKey = "";
    private String uuid = "";

    private void cargarConfig() {
        try {
            File file = new File("config.json");
            try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                Gson gson = new Gson();
                Map<String, String> data = gson.fromJson(reader, Map.class);
                if (data != null) {
                    apiKey = data.getOrDefault("apiKey", "");
                    uuid = data.getOrDefault("uuid", "");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long getPetJourneyTimestamp() {
        if (apiKey.isEmpty() || uuid.isEmpty()) return -1;
        try {
            String urlStr = "https://api.hypixel.net/v2/player?key=" + apiKey + "&uuid=" + uuid;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            int status = conn.getResponseCode();
            if (status == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    content.append(line);
                }
                in.close();
                JSONObject obj = new JSONObject(content.toString());
                JSONObject player = obj.optJSONObject("player");
                if (player != null && player.has("petJourneyTimestamp")) {
                    return player.getLong("petJourneyTimestamp");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void esperarHastaProximaMision() throws InterruptedException {
        long timestamp = getPetJourneyTimestamp();
        if (timestamp == -1) {
           
            esperarConMovimiento(30 * 60 * 1000);
            return;
        }
        long nextMission = (timestamp + 3600) * 1000L; 
        long now = System.currentTimeMillis();
        long waitMillis = nextMission - now;
        if (waitMillis > 0) {
            esperarConMovimiento(waitMillis);
        }
    }

@Mod(modid = "loopermod", name = "LooperMod", version = "1.0")
public class LooperMod {

    private boolean shouldFeed = false;
    private Thread feedThread = null;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final AtomicBoolean guiReady = new AtomicBoolean(false);
    private final LinkedHashMap<String, int[]> mascotas = new LinkedHashMap<>();
    private int currentSlot = -1;
    private boolean spacePressedBefore = false;

    private void cargarMascotas() {
        mascotas.clear();
        try {
            File file = new File(Minecraft.getMinecraft().mcDataDir, "pets.json");
            try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, int[]>>() {}.getType();
                Map<String, int[]> data = gson.fromJson(reader, type);
                if (data != null) mascotas.putAll(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new CommandFeedPets());
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

                new Thread(() -> {
                    try {
                        Thread.sleep(400);
                        mc.addScheduledTask(() -> {
                            mc.playerController.windowClick(
                                    chest.inventorySlots.windowId,
                                    slotToClick,
                                    0,
                                    0,
                                    mc.thePlayer
                            );
                            new Thread(() -> {
                                try {
                                    Thread.sleep(1050);
                                    mc.addScheduledTask(() -> mc.thePlayer.closeScreen());
                                } catch (InterruptedException ignored) {}
                            }).start();
                        });
                    } catch (InterruptedException ignored) {}
                }).start();
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        boolean isShiftPressed = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);

        if (isShiftPressed && !spacePressedBefore) {
            spacePressedBefore = true;

            if (shouldFeed && feedThread != null && feedThread.isAlive()) {
                feedThread.interrupt();
                feedThread = null;
                shouldFeed = false;
                mc.thePlayer.closeScreen();
            }
        } else if (!isShiftPressed) {
            spacePressedBefore = false;
        }
    }

    private void alimentarMascotasSinThread() {
        try {
            for (Map.Entry<String, int[]> entry : mascotas.entrySet()) {
                if (Thread.currentThread().isInterrupted()) return;
                String nombreMascota = entry.getKey();
                int[] slots = entry.getValue();
                for (int slot : slots) {
                    if (Thread.currentThread().isInterrupted()) return;
                    currentSlot = slot;
                    guiReady.set(false);
                    mc.thePlayer.sendChatMessage("/purchasepet " + nombreMascota);

                    int intentos = 0;
                    while (!guiReady.get() && intentos < 100) {
                        Thread.sleep(100);
                        intentos++;
                    }
                    Thread.sleep(3300);
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException ignored) {}
    }

private void enviarMascotasAMision() {
    mc.addScheduledTask(() -> {
        int cofreSlotHotbar = 4;
        mc.thePlayer.inventory.currentItem = cofreSlotHotbar;
        mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                mc.addScheduledTask(() -> mc.playerController.windowClick(
                        mc.thePlayer.openContainer.windowId,
                        9,
                        0,
                        0,
                        mc.thePlayer
                ));

                Thread.sleep(2000);
                mc.addScheduledTask(() -> mc.playerController.windowClick(
                        mc.thePlayer.openContainer.windowId,
                        45,
                        0,
                        0,
                        mc.thePlayer
                ));
            } catch (InterruptedException ignored) {}
        }).start();
    });
}

    private void esperarConMovimiento(long millis) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < millis) {
            if (!shouldFeed || Thread.currentThread().isInterrupted()) return;

            mc.addScheduledTask(() -> {
                if (mc.thePlayer != null && mc.theWorld != null) {
                    mc.thePlayer.jump();
                }
            });

            Thread.sleep(45000);
        }
    }

    private void ejecutarCicloInfinito() {
        feedThread = new Thread(() -> {
            try {
                cargarConfig();
                while (!Thread.currentThread().isInterrupted() && shouldFeed) {
                    alimentarMascotasSinThread();
                    Thread.sleep(1000);
                    enviarMascotasAMision();
                    esperarHastaProximaMision();
                    alimentarMascotasSinThread();
                    esperarConMovimiento(10 * 60 * 1000);
                    enviarMascotasAMision();
                    esperarHastaProximaMision();
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
            ejecutarCicloInfinito();
        }

        @Override
        public int getRequiredPermissionLevel() {
            return 0;
        }
    }
 }
}
