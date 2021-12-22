package nl.mtvehicles.core.infrastructure.dataconfig;

import nl.mtvehicles.core.Main;
import nl.mtvehicles.core.infrastructure.helpers.TextUtils;
import nl.mtvehicles.core.infrastructure.models.ConfigUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

public class MessagesConfig extends ConfigUtils {
    public MessagesConfig() {
        this.setFileName("messages/messages_en.yml");
        saveLanguageFile("nl");
        saveLanguageFile("es");
        saveLanguageFile("cs");
    }

    public String getMessage(String key) {
        return TextUtils.colorize((String) this.getConfig().get(key));
    }

    public void sendMessage(CommandSender sender, String key) {
        Object object = this.getConfig().get(key);
        if (object instanceof List) {
            for (String s : this.getConfig().getStringList(key)) {
                sender.sendMessage(TextUtils.colorize(s));
            }
        }
        sender.sendMessage(TextUtils.colorize(String.valueOf(object)));
    }

    public void sendMessage(Player player, String key) {
        Object object = this.getConfig().get(key);
        if (object instanceof List) {
            for (String s : this.getConfig().getStringList(key)) {
                player.sendMessage(TextUtils.colorize(s));
            }
        }
        player.sendMessage(TextUtils.colorize(String.valueOf(object)));
    }

    public boolean setLanguageFile(String countryCode){
        String fileName = "messages/messages_" + countryCode + ".yml";
        File languageFile = new File(Main.instance.getDataFolder(), fileName);
        if (!languageFile.exists()) return false;

        this.setFileName(fileName);
        this.setCustomConfigFile(new File(Main.instance.getDataFolder(), fileName));
        this.reload();
        return true;
    }

    private void saveLanguageFile(String countryCode){
        String fileName = "messages/messages_" + countryCode + ".yml";

        File languageFile = new File(Main.instance.getDataFolder(), fileName);
        if (!languageFile.exists()){
            Main.instance.saveResource(fileName, false);
        }
    }
}