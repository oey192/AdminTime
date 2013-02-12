package com.andoutay.admintime;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AdminTime extends JavaPlugin
{
	/*TODO: Tie in with Essentials /back command when teleporting a player back to their location when they're done admining
	 * 
	 */
	
	public static Logger log = Logger.getLogger("Minecraft");
	public static String logPref = "[AdminTime] ";
	public static String chPref = ChatColor.LIGHT_PURPLE + logPref + ChatColor.RESET;
	public static HashMap<Player, Boolean> inAdminMode;
	public static HashMap<Player, Location> lastLocs;
	public static ATPermissionsFileHandler permHandler;
	
	public void onLoad()
	{
		permHandler = new ATPermissionsFileHandler(this);
	}
	
	public void onEnable()
	{
		inAdminMode = new HashMap<Player, Boolean>();
		lastLocs = new HashMap<Player, Location>();
		
		permHandler.onEnable();
		getServer().getPluginManager().registerEvents(permHandler, this);
		
		log.info(logPref + "Enabled");
	}
	
	public void onDisable()
	{
		log.info(logPref + "Disabled");
	}
	
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args)
	{
		if (isReload(cmd.getName(), args))
			return reloadConfig(s);
		else if (isVersion(cmd.getName(), args))
			return showVersion(s);
		else if (isToggle(cmd.getName(), args))
			return toggle(s, args);
		
		return false;
	}
	
	private boolean isToggle(String cmdName, String[] args)
	{
		return cmdName.equalsIgnoreCase("admintime") && (args.length == 0 || args.length == 1);
	}
	
	private boolean isReload(String cmdName, String[] args)
	{
		return cmdName.equalsIgnoreCase("admintime") && args.length == 1 && args[0].equalsIgnoreCase("reload");
	}
	
	private boolean isVersion(String cmdName, String[] args)
	{
		return cmdName.equalsIgnoreCase("admintime") && args.length == 1 && args[0].equalsIgnoreCase("version");
	}
	
	private boolean toggle(CommandSender s, String[] args)
	{
		if (s instanceof ConsoleCommandSender)
			return noConsoleAccess(s);
		else if (!s.hasPermission("admintime.toggle"))
			return noAccess(s);
		
		Player p = (Player)s;
		if (!inAdminMode.containsKey(p))
			inAdminMode.put(p, false);
		
		inAdminMode.put(p, !inAdminMode.get(p));
		
		if (inAdminMode.get(p))
		{
			if (args.length == 0 || (getServer().getPlayer(args[0]) == null && !getServer().getOfflinePlayer(args[0]).hasPlayedBefore()))
			{
				inAdminMode.put(p, false);
				return false;
			}

			lastLocs.put(p, p.getLocation());
			permHandler.enterAdminMode(p, p.getWorld().getName());
			String str = getPlayerName(args[0]);
			if (str == "") str = args[0];
			tellAll(p.getDisplayName(), "entered", str);
			p.sendMessage(chPref + ChatColor.RED + "You are now in Admin Mode!");
		}
		else
		{
			permHandler.exitAdminMode(p, p.getWorld().getName());
			p.teleport(lastLocs.get(p));
			lastLocs.remove(p);
			tellAll(p.getDisplayName(), "left", "");
			p.sendMessage(chPref + ChatColor.RED + "You have left Admin Mode!");
		}
		
		return true;
	}
	
	private boolean reloadConfig(CommandSender s)
	{
		if (!(s instanceof ConsoleCommandSender || (s instanceof Player && ((Player)s).hasPermission("admintime.reload"))))
			return noAccess(s);
		
		permHandler.reload();
		s.sendMessage(chPref + "Permissions reloaded");
		
		return true;
	}
	
	private boolean showVersion(CommandSender s)
	{
		if (!(s instanceof ConsoleCommandSender || (s instanceof Player && ((Player)s).hasPermission("admintime.version"))))
			return noAccess(s);
		
		s.sendMessage(chPref + "Current version: " + getDescription().getVersion());
		return true;
	}
	
	private boolean noAccess(CommandSender s)
	{
		s.sendMessage(ChatColor.RED + "You do not have access to that command");
		return true;
	}
	
	private boolean noConsoleAccess(CommandSender s)
	{
		s.sendMessage(ChatColor.RED + "You must be in-game to use that command");
		return true;
	}
	
	public String getPlayerName(String partial)
	{
		Player player = null;
		boolean found = false, foundMult = false;
		
		player = getServer().getPlayer(partial);
		
		if (player == null)
			for (Player p: getServer().getOnlinePlayers())
				if (p.getDisplayName().toLowerCase().contains(partial.toLowerCase()))
				{
					if (found)
					{
						foundMult = true;
						break;
					}
					player = p;
					found = true;
				}
		
		if (foundMult)
			return "";
		
		if (player == null)
			return getServer().getOfflinePlayer(partial).getName();
		
		return player.getDisplayName();
	}
	
	public void tellAll(String name, String msg, String recipient)
	{
		for (Player p : getServer().getOnlinePlayers())
			if (p.hasPermission("admintime.notify"))
				p.sendMessage(ChatColor.WHITE + name + " " + ChatColor.GRAY + msg + " Admin Mode" + (recipient.equalsIgnoreCase("") ? "!" : " to help " + recipient + "!"));
		log.info(logPref + name + " " + msg + " Admin Mode" + (recipient.equalsIgnoreCase("") ? "!" : " to help " + recipient + "!"));
	}
}
