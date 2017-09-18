package fr.plx0wn;

import java.lang.reflect.Constructor;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.plx0wn.Compatibility.V110;
import fr.plx0wn.Compatibility.V111;
import fr.plx0wn.Compatibility.V112;
import fr.plx0wn.Compatibility.V18;
import fr.plx0wn.Compatibility.V19;
import net.md_5.bungee.api.ChatColor;

public class Tools {
	
	public static void scm(CommandSender sender, String msg){
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}

    public static void sendActionBar(Player player, String msg) {
    	String s = ChatColor.translateAlternateColorCodes('&', msg);
        try {
            Constructor<?> constructor = getNMSClass("PacketPlayOutChat").getConstructor(getNMSClass("IChatBaseComponent"), getNMSClass("ChatMessageType"));
               
            Object icbc = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + s + "\"}");
            Object packet = constructor.newInstance(icbc, getNMSClass("ChatMessageType").getEnumConstants()[2]);
            Object entityPlayer= player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);

            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
              e.printStackTrace();
        }
    }

    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + getVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
         
    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

	public static void sendTitle(Player p, String msg) {
		if (Bukkit.getVersion().contains("1.12")) {
			V112.sendTitle(p, msg);
		}
		if (Bukkit.getVersion().contains("1.11")) {
			V111.sendTitle(p, msg);
		}
		if (Bukkit.getVersion().contains("1.10")) {
			V110.sendTitle(p, msg);
		}
		if (Bukkit.getVersion().contains("1.9")) {
			V19.sendTitle(p, msg);
		}
		if (Bukkit.getVersion().contains("1.8")) {
			V18.sendTitle(p, msg);
		}
		if (Bukkit.getVersion().contains("1.7")) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Title feature can't be enable in 1.7!");
		}
	}

}
