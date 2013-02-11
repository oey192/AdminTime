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
	public static Logger log = Logger.getLogger("Minecraft");
	public static String logPref = "[AdminTime] ";
	public static String chPref = ChatColor.LIGHT_PURPLE + logPref + ChatColor.RESET;
	public static HashMap<Player, Boolean> inAdminMode;
	public static HashMap<Player, Location> lastLocs;
	
	public void onLoad()
	{
		
	}
	
	public void onEnable()
	{
		inAdminMode = new HashMap<Player, Boolean>();
		lastLocs = new HashMap<Player, Location>();
		
		log.info(logPref + "Enabled");
	}
	
	public void onDisable()
	{
		log.info(logPref + "Disabled");
	}
	
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args)
	{
		if (isToggle(cmd.getName(), args))
			return toggle(s);
		else if (isReload(cmd.getName(), args))
			return reloadConfig(s);
		else if (isVersion(cmd.getName(), args))
			return showVersion(s);
		
		return false;
	}
	
	
	private boolean isToggle(String cmdName, String[] args)
	{
		return cmdName.equalsIgnoreCase("admintime") && args.length == 1;
	}
	
	private boolean isReload(String cmdName, String[] args)
	{
		return cmdName.equalsIgnoreCase("admintime") && args.length == 2 && args[1].equalsIgnoreCase("reload");
	}
	
	private boolean isVersion(String cmdName, String[] args)
	{
		return cmdName.equalsIgnoreCase("admintime") && args.length == 2 && args[1].equalsIgnoreCase("version");
	}
	
	private boolean toggle(CommandSender s)
	{
		if (s instanceof ConsoleCommandSender)
			return noConsoleAccess(s);
		else if (!s.hasPermission("admintime.toggle"))
			return noAccess(s);
		
		Player p = (Player)s;
		if (!inAdminMode.containsKey(p))
			inAdminMode.put(p, false);
		
		inAdminMode.put(p, !inAdminMode.get(p));
		lastLocs.put(p, p.getLocation());
		
		
		if (inAdminMode.get(p))
			//add permissions
			p.sendMessage("Adding perms");
		else
			p.sendMessage("Removing perms...");
		
		return true;
	}
	
	private boolean reloadConfig(CommandSender s)
	{
		if (!(s instanceof ConsoleCommandSender || (s instanceof Player && ((Player)s).hasPermission("admintime.reload"))))
			return noAccess(s);
		
		//ATConfig.reload();
		//ATPerms.reload();
		s.sendMessage(chPref + "Config reloaded");
		
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
}
