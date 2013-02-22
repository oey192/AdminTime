package com.andoutay.admintime;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.vanish.staticaccess.VanishNoPacket;

public class AdminTime extends JavaPlugin {
	/*
	 * TODO: Tie in with Essentials /back command when teleporting a player back
	 * to their location when they're done admining
	 * 
	 * Keep track of who the player's helping and display this in /at list
	 * 
	 * Add mChat support (see below)
	 * 
	 * Add Metrics support
	 * 
	 * Add Auto-update support
	 * 
	 * put on BukkitDev
	 */

	public static Logger log = Logger.getLogger("Minecraft");
	public static String logPref = "[AdminTime] ";
	public static String chPref = ChatColor.LIGHT_PURPLE + logPref + ChatColor.RESET;
	public static HashMap<Player, Boolean> inAdminMode;
	public static HashMap<Player, Location> lastLocs;
	public static ATPermissionsFileHandler permHandler;
	public static Server server;

	public void onLoad() {
		server = getServer();
		new ATConfig(this);
		permHandler = new ATPermissionsFileHandler(this);
	}

	public void onEnable() {
		inAdminMode = new HashMap<Player, Boolean>();
		lastLocs = new HashMap<Player, Location>();

		ATConfig.onEnable();
		permHandler.onEnable();
		getServer().getPluginManager().registerEvents(permHandler, this);

		log.info(logPref + "Enabled");
	}

	public void onDisable() {
		log.info(logPref + "Disabled");
	}

	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		if (isReload(cmd.getName(), args))
			return reloadConfig(s);
		else if (isVersion(cmd.getName(), args))
			return showVersion(s);
		else if (isEject(cmd.getName(), args))
			return eject(s, args);
		else if (isList(cmd.getName(), args))
			return showList(s, args);
		else if (isHelp(cmd.getName(), args))
			return showHelp(s);
		else if (isToggle(cmd.getName(), args))
			return toggle(s, args);

		return false;
	}

	private boolean isToggle(String cmdName, String[] args) {
		return cmdName.equalsIgnoreCase("admintime") && (args.length == 0 || args.length == 1);
	}

	private boolean isReload(String cmdName, String[] args) {
		return cmdName.equalsIgnoreCase("admintime") && args.length == 1 && args[0].equalsIgnoreCase("reload");
	}

	private boolean isVersion(String cmdName, String[] args) {
		return cmdName.equalsIgnoreCase("admintime") && args.length == 1 && args[0].equalsIgnoreCase("version");
	}

	private boolean isEject(String cmdName, String[] args) {
		return cmdName.equalsIgnoreCase("admintime") && args.length == 2 && args[0].equalsIgnoreCase("eject");
	}

	private boolean isList(String cmdName, String[] args) {
		return cmdName.equalsIgnoreCase("admintime") && (args.length == 1 || args.length == 2) && args[0].equalsIgnoreCase("list");
	}

	private boolean isHelp(String cmdName, String[] args) {
		return cmdName.equalsIgnoreCase("admintime") && args.length == 1 && args[0].equalsIgnoreCase("help");
	}

	private boolean toggle(CommandSender s, String[] args) {
		if (s instanceof ConsoleCommandSender)
			return noConsoleAccess(s);
		else if (!(s.hasPermission("admintime.toggle.self") || s.hasPermission("admintime.toggle.others")))
			return noAccess(s);

		Player p = (Player) s;
		if (!inAdminMode.containsKey(p))
			inAdminMode.put(p, false);

		inAdminMode.put(p, !inAdminMode.get(p));

		if (inAdminMode.get(p)) {
			if (args.length == 0) {
				inAdminMode.put(p, false);
				return missingArgument(s);
			} else if (getPlayerForName(args[0]) == null && !getServer().getOfflinePlayer(args[0]).hasPlayedBefore()) {
				inAdminMode.put(p, false);
				return playerNotFound(s);
			}

			String str = getPlayerName(args[0]);
			if (!((p.hasPermission("admintime.toggle.self") && p.getName().equalsIgnoreCase(str)) || (p.hasPermission("admintime.toggle.others") && !p
				.getName().equalsIgnoreCase(str)))) {
				inAdminMode.put(p, false);
				return badPlayer(p);
			}
			if (str == "")
				str = args[0];
			lastLocs.put(p, p.getLocation());
			permHandler.enterAdminMode(p, p.getWorld().getName());
			tellAll(p, "entered", str);
			p.sendMessage(chPref + ChatColor.RED + "You are now in Admin Mode!");
		} else {
			permHandler.exitAdminMode(p, p.getWorld().getName());
			p.teleport(lastLocs.get(p));
			lastLocs.remove(p);
			tellAll(p, "left", "");
			p.sendMessage(chPref + ChatColor.RED + "You have left Admin Mode!");
		}

		return true;
	}

	private boolean reloadConfig(CommandSender s) {
		if (!(s instanceof ConsoleCommandSender || (s instanceof Player && ((Player) s).hasPermission("admintime.reload"))))
			return noAccess(s);

		ATConfig.reload();
		s.sendMessage(chPref + "Config reloaded");
		permHandler.reload();
		s.sendMessage(chPref + "Permissions reloaded");

		return true;
	}

	private boolean showVersion(CommandSender s) {
		if (!(s instanceof ConsoleCommandSender || (s instanceof Player && ((Player) s).hasPermission("admintime.version"))))
			return noAccess(s);

		s.sendMessage(chPref + "Current version: " + getDescription().getVersion());
		return true;
	}

	private boolean eject(CommandSender s, String[] args) {
		if (!(s instanceof ConsoleCommandSender || (s instanceof Player && ((Player) s).hasPermission("admintime.eject"))))
			return noAccess(s);

		Player p = getPlayerForName(args[1]);
		if (p == null)
			return playerNotFound(s);

		if (inAdminMode.containsKey(p) && lastLocs.containsKey(p) && inAdminMode.get(p)) {
			inAdminMode.put(p, false);
			permHandler.exitAdminMode(p, p.getWorld().getName());
			lastLocs.remove(p);
			tellAll(p, "left", "");
		} else
			s.sendMessage(chPref + "That player is not in admin mode");

		return true;
	}

	private boolean showList(CommandSender s, String[] args) {
		if (!(s instanceof ConsoleCommandSender || (s instanceof Player && ((Player) s).hasPermission("admintime.list"))))
			return noAccess(s);

		if (inAdminMode.size() == 0) {
			s.sendMessage(chPref + "No players are currently in Admin Mode");
			return true;
		}

		int page = 0;

		try {
			page = ((args.length == 2) ? Integer.parseInt(args[1]) : 1);
		} catch (NumberFormatException e) {
			return invalidArgument(s);
		}

		showListToSender(s, page);

		return true;
	}

	public void showListToSender(CommandSender s, int page) {
		final int perPage = 9;
		final int totPages = inAdminMode.size() / perPage + 1;
		Object players[] = inAdminMode.keySet().toArray();
		Object values[] = inAdminMode.values().toArray();

		if (page > totPages)
			page = totPages;
		if (page < 1)
			page = 1;

		s.sendMessage(chPref + "Players in Admin Mode (" + page + "/" + totPages + ")");
		int start = (page - 1) * perPage;
		int end = (totPages > page * perPage) ? totPages : page * perPage;
		boolean found = false;
		for (int i = start; i < end && i < inAdminMode.size(); i++) {
			if ((Boolean) values[i]) {
				found = true;
				s.sendMessage(((Player) players[i]).getDisplayName());
			} else if (i >= end)
				i--;
		}

		if (!found)
			s.sendMessage("No players are currently in Admin Mode");
	}

	private boolean showHelp(CommandSender s) {
		s.sendMessage(chPref + "Help:");
		s.sendMessage("Aliases: at, am, adminmode");
		s.sendMessage("/admintime [player]: Toggle admin mode. The name of the player being helped is required for entering admin mode. If that player is offline, their full name must be specified");
		s.sendMessage("/admintime reload: Reload the AdminTime permissions file");
		s.sendMessage("/admintime version: Get the current version of AdminTime");
		s.sendMessage("/admintime list [#]: List players in admin mode. Specify page numbers if necessary");
		s.sendMessage("/admintime eject: Kick a player out of admin mode");
		s.sendMessage("/admintime help: Show this help message");

		return true;
	}

	private boolean noAccess(CommandSender s) {
		s.sendMessage(ChatColor.RED + "You do not have access to that command");
		return true;
	}

	private boolean noConsoleAccess(CommandSender s) {
		s.sendMessage(ChatColor.RED + "You must be in-game to use that command");
		return true;
	}

	private boolean playerNotFound(CommandSender s) {
		s.sendMessage(ChatColor.RED + "Player not found");
		return true;
	}

	private boolean invalidArgument(CommandSender s) {
		s.sendMessage(ChatColor.RED + "Invalid argument");
		return false;
	}

	private boolean missingArgument(CommandSender s) {
		s.sendMessage(ChatColor.RED + "You must supply another argument to use that command");
		return false;
	}

	private boolean badPlayer(CommandSender s) {
		s.sendMessage(ChatColor.RED + "You may not help that player");
		return true;
	}

	public Player getPlayerForName(String partial) {
		Player player = null;
		boolean found = false, foundMult = false;

		player = getServer().getPlayer(partial);

		if (player == null)
			for (Player p : getServer().getOnlinePlayers())
				if (p.getDisplayName().toLowerCase().contains(partial.toLowerCase())) {
					if (found) {
						foundMult = true;
						break;
					}
					player = p;
					found = true;
				}

		if (foundMult)
			return null;

		return player;
	}

	public String getPlayerName(String partial) {
		Player temp = getPlayerForName(partial);
		if (temp == null)
			return getServer().getOfflinePlayer(partial).getName();
		return temp.getDisplayName();

		/*
		 * TODO add mChat support - Parser.parsePlayerName(sender.getName(),
		 * world); get Parser using ExternalJarHandler outlined at top of file
		 */
	}

	private static boolean isVanished(String name) {
		try {
			if (server.getPluginManager().getPlugin("VanishNoPacket") != null)
				return VanishNoPacket.isVanished(name);
		} catch (Exception localException) {
		}
		return false;
	}

	private boolean canSee(Player looking, Player uncertain) {
		try {
			if (server.getPluginManager().getPlugin("VanishNoPacket") != null)
				return VanishNoPacket.canSee(looking, uncertain);
		} catch (Exception localException) {
		}
		return true;
	}

	public void tellAll(Player player, String msg, String recipient)
	  {
		for (Player p : getServer().getOnlinePlayers()) {
	    	if (p.hasPermission("admintime.notify") && (!isVanished(p.getName()) || canSee(p, player)))
	    			p.sendMessage(ChatColor.WHITE + player.getDisplayName() + " " + ChatColor.GRAY + msg + " Admin Mode" + (recipient.equalsIgnoreCase("") ? "!" : new StringBuilder(" to help ").append(recipient.equalsIgnoreCase(player.getDisplayName()) ? "themself" : recipient).append("!").toString()));
	    }
	    log.info(logPref + player.getName() + " " + msg + " Admin Mode" + (recipient.equalsIgnoreCase("") ? "!" : new StringBuilder(" to help ").append(recipient.equalsIgnoreCase(player.getDisplayName()) ? "themself" : recipient).append("!").toString()));
	  }
}
