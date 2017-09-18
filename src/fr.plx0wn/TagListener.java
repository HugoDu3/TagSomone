package fr.plx0wn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.plugin.Plugin;

import io.netty.handler.codec.string.StringEncoder;

public class TagListener implements Listener {

	Plugin plugin = TagSomeone.instance;

	FileConfiguration config = plugin.getConfig();

	ArrayList<Player> cooldownlist = new ArrayList<>();

	private void cooldownremove(final Player player, int cooldowntime) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				cooldownlist.remove(player);
			}
		}, cooldowntime * 20);

	}

	private void sendNotifications(AsyncPlayerChatEvent e, final Player player, Player tagged, String message,
			String othermessage) {
		// Global
		String msgtagged = TagSomeone.msgconf.getString("global-messages.tagged");

		// Cooldown
		int cooldowntime = config.getInt("tag-option.cooldown.time");

		// Notifications

		if (config.getBoolean("tag-option.notification.chat-message")) {
			tagged.sendMessage(
					ChatColor.translateAlternateColorCodes('&', msgtagged).replace("<player>", player.getName()));
		}
		if (config.getBoolean("tag-option.notification.action-bar-message")) {
			Tools.sendActionBar(tagged, msgtagged.replace("<player>", player.getName()));
		}
		if (config.getBoolean("tag-option.notification.title-message")) {
			Tools.sendTitle(tagged, msgtagged.replace("<player>", player.getName()));
		}
		if (config.getBoolean("tag-option.notification.sound.enable")) {
			tagged.playSound(tagged.getLocation(),
					Sound.valueOf(config.getString("tag-option.notification.sound.name")), 0.5F, 3.0F);
		}

		if (config.getBoolean("tag-option.color-option.colored-symbol"))
			e.setMessage(othermessage);
		else
			e.setMessage(message);

	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onTag(final AsyncPlayerChatEvent e) {
		String message = e.getMessage();

		// Global
		String symbol = config.getString("tag-option.symbol");
		String color = config.getString("tag-option.color-option.color");
		String colorafter = config.getString("tag-option.color-option.color-after-tag");
		String msgnotonline = TagSomeone.msgconf.getString("global-messages.not-online");
		String msgtagged = TagSomeone.msgconf.getString("global-messages.tagged");
		String msgnoperms = TagSomeone.msgconf.getString("global-messages.no-permissions");

		// Cooldown
		int cooldowntime = config.getInt("tag-option.cooldown.time");
		String cooldownmsg = TagSomeone.msgconf.getString("global-messages.cooldown-message").replace("<time>",
				"" + cooldowntime);

		// Blacklist
		String msgblacklisted = TagSomeone.msgconf.getString("black-list-messages.black-listed");
		List blacklist = TagSomeone.listsconf.getList("black-list");

		// DisableList
		List disabledlist = TagSomeone.listsconf.getList("disabled-list");

		if (message.contains(symbol)) {
			if (e.getPlayer().hasPermission("tag.use")) {
				if (!disabledlist.contains(e.getPlayer().getName())) {
					if (e.getMessage().contains(symbol)) {

						while (message.contains(symbol)) {
							message = message.substring(message.indexOf(symbol));
							String tag;
							if (message.indexOf(" ") == -1)
								tag = message.substring(1);
							else
								tag = message.substring(message.indexOf(symbol) + 1,
										message.indexOf(" ", message.indexOf(symbol)));
							String nocoloredcode = e.getMessage().replace(tag,
									ChatColor.translateAlternateColorCodes('&', color + tag + colorafter));
							String coloredcode = nocoloredcode.replace(symbol,
									ChatColor.translateAlternateColorCodes('&', color + symbol + colorafter));

							try {
								Player tagged = Bukkit.getPlayer(tag);
								// IF ALL IS GOOD
								if (tag.equalsIgnoreCase(tagged.getName().toLowerCase())) {
									if (tagged.isOnline()) {
										if (blacklist.contains(tag)) {
											e.getPlayer().sendMessage(
													ChatColor.translateAlternateColorCodes('&', msgblacklisted));
											return;
											// e.setCancelled(true);
										} else {
											// setMessage
											if (config.getBoolean("tag-option.cooldown.enable")) {
												if (!e.getPlayer().hasPermission("tag.cooldown.bypass")) {
													if (cooldownlist.contains(e.getPlayer())) {
														e.setCancelled(true);
													} else {
														sendNotifications(e, e.getPlayer(), tagged, nocoloredcode,
																coloredcode);
													}
												} else {
													sendNotifications(e, e.getPlayer(), tagged, nocoloredcode,
															coloredcode);
												}
											} else {
												sendNotifications(e, e.getPlayer(), tagged, nocoloredcode, coloredcode);
											}
											if (message.indexOf(" ") != -1)
												message = message.substring(message.indexOf(" "));
											else
												message = "";
										}
									}
								} else {
									e.getPlayer()
											.sendMessage(ChatColor.translateAlternateColorCodes('&', msgnotonline));
									if (config.getBoolean("tag-option.cooldown.enable")) {
										if (cooldownlist.contains(e.getPlayer())) {
											if (!e.getPlayer().hasPermission("tag.cooldown.bypass")) {
												e.getPlayer().sendMessage(
														ChatColor.translateAlternateColorCodes('&', cooldownmsg));
											}
										} else {
											cooldownlist.add(e.getPlayer());
											cooldownremove(e.getPlayer(), cooldowntime);
										}
									}
									return;
								}
							} catch (Exception i) {
								e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', msgnotonline));
								if (config.getBoolean("tag-option.cooldown.enable")) {
									if (cooldownlist.contains(e.getPlayer())) {
										if (!e.getPlayer().hasPermission("tag.cooldown.bypass")) {
											e.getPlayer().sendMessage(
													ChatColor.translateAlternateColorCodes('&', cooldownmsg));
										}
									} else {
										cooldownlist.add(e.getPlayer());
										cooldownremove(e.getPlayer(), cooldowntime);
									}
								}
								return;
							}
						}
						if (config.getBoolean("tag-option.cooldown.enable")) {
							if (cooldownlist.contains(e.getPlayer())) {
								if (!e.getPlayer().hasPermission("tag.cooldown.bypass")) {
									e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', cooldownmsg));
								}
							} else {
								cooldownlist.add(e.getPlayer());
								cooldownremove(e.getPlayer(), cooldowntime);
							}
						}
					}
				}
			} else {
				e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', msgnoperms));
				e.setCancelled(true);
			}
		}
	}

}
