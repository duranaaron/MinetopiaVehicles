package nl.mtvehicles.core.commands.vehiclesubs;

import nl.mtvehicles.core.infrastructure.helpers.TextUtils;
import nl.mtvehicles.core.infrastructure.models.MTVehicleSubCommand;
import nl.mtvehicles.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class VehicleUpdate extends MTVehicleSubCommand {
    @Override
    public boolean execute(CommandSender sender, Command cmd, String s, String[] args) {
        if (!checkPermission("mtvehicles.update")) return true;

        if (!Main.defaultConfig.getConfig().getBoolean("auto-update")) {
            sendMessage(Main.messagesConfig.getMessage("updateDisabled"));
            return false;
        }

        checkNewVersion();

        return true;
    }

    public void checkNewVersion() {
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
                sendMessage(TextUtils.colorize("&aWe have found an update, please remain patient!"));
                File dest = new File("plugins");
                try {
                    download(new URL("https://minetopiavehicles.nl/api/MTVehicles.jar"), dest);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else {
                sendMessage(TextUtils.colorize("&cNo update has been found, if you think this is incorrect please report it in the discord. https://mtvehicles.nl"));
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().info("We couldn't make a connection to the servers of MinetopiaVehicles.");
        }
    }

    public void download(URL file, File dest) {
        try {
            InputStream is = file.openStream();
            File finaldest = new File(dest + "/" + file.getFile().replace("/api/MTVehicles.jar", "/" + Main.fol().replace("plugins", "")));
            // File finaldest = new File(dest + "/" + file.getFile());
            finaldest.getParentFile().mkdirs();
            finaldest.createNewFile();
            System.out.println("For this last step we have to reload the server!");

            OutputStream os = new FileOutputStream(finaldest);
            byte[] data = new byte[1024];
            int count;
            while ((count = is.read(data, 0, 1024)) != -1) {
                os.write(data, 0, count);
            }
            os.flush();
            is.close();
            os.close();
            sendMessage(Main.messagesConfig.getMessage("updatedSucces2"));


        } catch (Exception ec) {
            sendMessage(Main.messagesConfig.getMessage("updateFailed"));
            ec.printStackTrace();
        }
    }
}