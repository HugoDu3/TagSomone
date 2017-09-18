package fr.plx0wn.Compatibility;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

public class V18 {

	public static void sendTitle(Player player, String msg) {
		String s = ChatColor.translateAlternateColorCodes('&', msg);
		IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\": \" " + s + "\"}");
		PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
	}

}
