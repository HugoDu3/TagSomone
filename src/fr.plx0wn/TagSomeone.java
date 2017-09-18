package fr.plx0wn;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class TagSomeone extends JavaPlugin implements Listener {

	public static Plugin instance;
	public static File listsfile, msgfile, cfgfile;
	public static FileConfiguration listsconf, msgconf;

	public void onEnable() {
		instance = this;
		Bukkit.getServer().getPluginManager().registerEvents(new TagListener(), this);
		getConfig().options().copyDefaults();
		saveDefaultConfig();
		createFiles();
	}

	private void createFiles() {

		listsfile = new File(getDataFolder(), "lists.yml");
		msgfile = new File(getDataFolder(), "messages.yml");
		cfgfile = new File(getDataFolder(), "config.yml");
		if (!listsfile.exists()) {
			listsfile.getParentFile().mkdirs();
			saveResource("lists.yml", false);
		}
		if (!msgfile.exists()) {
			msgfile.getParentFile().mkdirs();
			saveResource("messages.yml", false);
		}

		try {
			listsconf = new YamlConfiguration();
			listsconf.load(listsfile);

			msgconf = new YamlConfiguration();
			msgconf.load(msgfile);
		} catch (InvalidConfigurationException | IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		// ADMINS

		if (label.equalsIgnoreCase("tag")) {

			if (args.length == 0) {
				if (sender.hasPermission("tag.admin") || sender.hasPermission("tag.use")) {
					Tools.scm(sender, "&aList of commands:");
					if (sender.hasPermission("tag.admin")) {
						Tools.scm(sender, "&a- /tag reload");
						Tools.scm(sender, "&a- /tag blacklist add/remove {username}");
					}
					if (sender.hasPermission("tag.use")) {
						Tools.scm(sender, "&a- /tag enable/disable");
					}
				}
			}

			// RELOAD

			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("reload")) {
					if (sender.hasPermission("tag.admin")) {
						try {
							getConfig().load(cfgfile);
							Tools.scm(sender, "&aconfig.yml reloaded!");
						} catch (IOException | InvalidConfigurationException e) {
							e.printStackTrace();
							Tools.scm(sender, "&cconfig.yml not reloaded. See logs.");
						}
						try {
							listsconf.load(listsfile);
							Tools.scm(sender, "&alist.yml reloaded!");
						} catch (IOException | InvalidConfigurationException e) {
							e.printStackTrace();
							Tools.scm(sender, "&clist.yml not reloaded. See logs.");
						}
						try {
							msgconf.load(msgfile);
							Tools.scm(sender, "&amessages.yml reloaded!");
						} catch (IOException | InvalidConfigurationException e) {
							e.printStackTrace();
							Tools.scm(sender, "&cmessages.yml not reloaded. See logs.");
						}
					} else {
						Tools.scm(sender, msgconf.getString("global-messages.no-permissions"));
					}
				}

				// ENABLE/DISABLE

				if (sender instanceof Player) {
					Player player = ((Player) sender).getPlayer();
					List disabled = listsconf.getList("disabled-list");
					if (args[0].equalsIgnoreCase("disable")) {
						if (sender.hasPermission("tag.use")) {
							if (disabled.contains(player.getName())) {
								Tools.scm(sender, msgconf.getString("disabled-list-messages.already-disabled"));
							} else {
								Tools.scm(sender, msgconf.getString("disabled-list-messages.disable"));
								disabled.add(player.getName());
								try {
									listsconf.save(listsfile);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						} else {
							Tools.scm(sender, msgconf.getString("global-messages.no-permissions"));
						}
					}
					if (args[0].equalsIgnoreCase("enable")) {
						if (sender.hasPermission("tag.use")) {
							if (disabled.contains(player.getName())) {
								Tools.scm(sender, msgconf.getString("disabled-list-messages.enable"));
								disabled.remove(player.getName());
								try {
									listsconf.save(listsfile);
								} catch (IOException e) {
									e.printStackTrace();
								}
							} else {
								Tools.scm(sender, msgconf.getString("disabled-list-messages.already-enabled"));
							}
						} else {
							Tools.scm(sender, msgconf.getString("global-messages.no-permissions"));
						}
					}
				} else {
					Tools.scm(sender, "&cYou need to be a player!");
				}
			}

			// BLACKLIST

			String msgblacklistedadd = msgconf.getString("black-list-messages.add");
			String msgblacklistedremove = msgconf.getString("black-list-messages.remove");

			if (label.equalsIgnoreCase("tag")) {

				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("blacklist")) {
						if (sender.hasPermission("tag.admin")) {
							Tools.scm(sender, "&cTry: /tag blacklist add/remove {username}");
						} else {
							Tools.scm(sender, msgconf.getString("global-messages.no-permissions"));
						}
					}
				}

				if (args.length == 2) {
					if (args[0].equalsIgnoreCase("blacklist")) {
						if (args[1].equalsIgnoreCase("add")) {
							if (sender.hasPermission("tag.admin")) {
								Tools.scm(sender, "&cTry: /tag blacklist add {username}");
							} else {
								Tools.scm(sender, msgconf.getString("global-messages.no-permissions"));
							}
						}
						if (args[1].equalsIgnoreCase("remove")) {
							if (sender.hasPermission("tag.admin")) {
								Tools.scm(sender, "&cTry: /tag blacklist remove {username}");
							} else {
								Tools.scm(sender, msgconf.getString("global-messages.no-permissions"));
							}
						}
					}
				}

				if (args.length == 3) {
					List list = listsconf.getList("black-list");
					if (args[0].equalsIgnoreCase("blacklist")) {
						if (sender.hasPermission("tag.admin")) {
							if (args[1].equalsIgnoreCase("add")) {
								if (list.contains(args[2])) {
									Tools.scm(sender, "&cThis name is already black-listed!");
								} else {
									Tools.scm(sender, msgblacklistedadd.replace("<player>", args[2]));
									list.add(args[2]);
									try {
										listsconf.save(TagSomeone.listsfile);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
							if (args[1].equalsIgnoreCase("remove")) {
								if (list.contains(args[2])) {
									list.remove(args[2]);
									Tools.scm(sender, msgblacklistedremove.replace("<player>", args[2]));
									try {
										listsconf.save(TagSomeone.listsfile);
									} catch (IOException e) {
										e.printStackTrace();
									}
								} else {
									Tools.scm(sender, "&cThis name is not black-listed!");
								}
							}
						} else {
							Tools.scm(sender, msgconf.getString("global-messages.no-permissions"));
						}
					}

				}
			}
		}
		return false;
	}

}
