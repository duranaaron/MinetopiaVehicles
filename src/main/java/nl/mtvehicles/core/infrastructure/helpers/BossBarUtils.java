package nl.mtvehicles.core.infrastructure.helpers;

import nl.mtvehicles.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarUtils {
    public static BossBar fuelBossBar;

    public static void setBossBarValue(double counter, String ken) {
        if (Main.defaultConfig.getConfig().getBoolean("benzine") && Main.vehicleDataConfig.getConfig().getBoolean("vehicle." + ken + ".benzineEnabled")) {
            fuelBossBar.setProgress(counter);
            fuelBossBar.setTitle(Math.round(counter * 100.0D) + "% " + TextUtils.colorize(Main.messagesConfig.getMessage("bossbarFuel")));

            Double fuel = VehicleData.fuel.get(ken);

            Main.vehicleDataConfig.getConfig().set(String.format("vehicle.%s.benzine", ken), fuel);
            Main.vehicleDataConfig.save();

            if (fuel < 30) {
                fuelBossBar.setColor(BarColor.RED);
                return;
            }
            if (fuel < 60) {
                fuelBossBar.setColor(BarColor.YELLOW);
                return;
            }
            if (fuel < 100) {
                fuelBossBar.setColor(BarColor.GREEN);
            }
        }
    }

    public static void removeBossBar(Player player, String ken) {
        if (Main.defaultConfig.getConfig().getBoolean("benzine") && Main.vehicleDataConfig.getConfig().getBoolean("vehicle." + ken + ".benzineEnabled")) {
            fuelBossBar.removePlayer(player);
        }
    }

    public static void addBossBar(Player player, String ken) {
        if (Main.defaultConfig.getConfig().getBoolean("benzine") && Main.vehicleDataConfig.getConfig().getBoolean("vehicle." + ken + ".benzineEnabled")) {
            double fuel = Main.vehicleDataConfig.getConfig().getDouble(String.format("vehicle.%s.benzine", player.getVehicle().getCustomName().replace("MTVEHICLES_MAINSEAT_", "")));
            String fuelString = String.valueOf(fuel);
            fuelBossBar = Bukkit.createBossBar(Math.round(Double.parseDouble(fuelString)) + "% " + TextUtils.colorize(Main.messagesConfig.getMessage("bossbarFuel")), BarColor.GREEN, BarStyle.SOLID);
            if (fuel < 30) {
                fuelBossBar.setColor(BarColor.RED);
            }
            if (fuel < 60) {
                fuelBossBar.setColor(BarColor.YELLOW);
            }
            if (fuel < 100) {
                fuelBossBar.setColor(BarColor.GREEN);
            }
            fuelBossBar.addPlayer(player);
        }
    }
}
