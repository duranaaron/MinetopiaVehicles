package nl.mtvehicles.core.events;

import nl.mtvehicles.core.infrastructure.helpers.ItemUtils;
import nl.mtvehicles.core.infrastructure.helpers.TextUtils;
import nl.mtvehicles.core.infrastructure.models.ConfigUtils;
import nl.mtvehicles.core.Main;
import nl.mtvehicles.core.movement.MovementManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;

public class JoinEvent implements Listener {
    public static HashMap<UUID, Boolean> languageCheck = new HashMap<>();

    @EventHandler
    public void onJoinEventPlayer(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        MovementManager.MovementSelector(p);

        if (Main.defaultConfig.getConfig().getString("messagesLanguage").equals("ns")) {
            if (p.hasPermission("mtvehicles.language")) {
                p.sendMessage(TextUtils.colorize("&cHey! You have not yet changed the language of the plugin. Do this by with &4/vehicle language&c!"));
            }
        }

        if (!p.hasPermission("mtvehicles.update") || !Main.defaultConfig.getConfig().getBoolean("auto-update")) {
            return;
        }

        checkNewVersion(p);
    }

    public static void checkLanguage(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "Choose your language");
        inv.setItem(11, ItemUtils.mItem("GOLD_BLOCK", 1, (short) 0, "&eEnglish", "&7When you press this, your messages will be sent in English."));
        inv.setItem(15, ItemUtils.mItem("DIAMOND_BLOCK", 1, (short) 0, "&9Dutch (Nederlands)", "&7Wanneer je hierop drukt worden jouw berichten in het Nederlands gestuurd"));
        p.openInventory(inv);
        languageCheck.put(p.getUniqueId(), true);
    }

    private static void readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        PrintWriter writer = new PrintWriter("plugins/MTVehicles/messages.yml");
        writer.print(sb);
        writer.close();
        Main.configList.forEach(ConfigUtils::reload);
    }

    public static void changeLanguageDutch() {
        try {
            URLConnection connection = new URL("https://minetopiavehicles.nl/api/translate-nl.php").openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            readAll(r);
        } catch (IOException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().info("We couldn't make a connection to the servers of MinetopiaVehicles.");
        }
    }

    public static void changeLanguageEnglish() {
        try {
            URLConnection connection = new URL("https://minetopiavehicles.nl/api/translate-en.php").openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            readAll(r);
        } catch (IOException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().info("We couldn't make a connection to the servers of MinetopiaVehicles.");
        }
    }

    public void getUpdateMessage(Player p) {
        try {
            URLConnection connection = new URL("https://minetopiavehicles.nl/api/update-api-check.php").openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line);
            }
            String value = sb.toString();
            String[] vet = value.split("@");
            PluginDescriptionFile pdf = Main.instance.getDescription();
            for (String s : vet) {
                p.sendMessage(TextUtils.colorize(s.replace("<oldVer>", pdf.getVersion())));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().info("We couldn't make a connection to the servers of MinetopiaVehicles.");
        }
    }

    public void checkNewVersion(Player p) {
        try {
            URLConnection connection = new URL("https://minetopiavehicles.nl/api/update-api-version.php").openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line);
            }
            String value = sb.toString();
            PluginDescriptionFile pdf = Main.instance.getDescription();
            if (!value.contains(pdf.getVersion())) {
                getUpdateMessage(p);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().info("We couldn't make a connection to the servers of MinetopiaVehicles.");
        }
    }
}