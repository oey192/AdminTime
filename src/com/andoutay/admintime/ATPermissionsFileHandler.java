package com.andoutay.admintime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ATPermissionsFileHandler implements Listener
{
	private AdminTime plugin;
	private File permFile;
	private YamlConfiguration perms;
	private Logger log = Logger.getLogger("Minecraft");
	private final String adMode = "adminMode";
	private final String regMode = "regMode";
	
	ATPermissionsFileHandler(AdminTime plugin)
	{
		permFile = new File(plugin.getDataFolder(), "permissions.yml");
		perms = YamlConfiguration.loadConfiguration(permFile);
		this.plugin = plugin;
	}
	
	public void onEnable()
	{
		List<World> worlds = plugin.getServer().getWorlds();
		
		for (World w : worlds)
		{
			ArrayList<String> list = new ArrayList<String>(), list2 = new ArrayList<String>();
			list.add("permission.for." + w.getName() + ".for.admin.mode");
			list2.add("permission.for." + w.getName() + ".for.normal.mode");
			perms.addDefault(adMode + "." + w.getName(), list);
			perms.addDefault(regMode + "." + w.getName(), list2);
		}
		
		ArrayList<String> allList = new ArrayList<String>(), allList2 = new ArrayList<String>();
		allList.add("permission.for.all.worlds.in.admin.mode");
		allList2.add("permission.for.all.worlds.in.normal.mode");
		perms.addDefault(adMode + ".allWorlds", allList);
		perms.addDefault(regMode + ".allWorlds", allList2);
		
		perms.options().copyDefaults(true);
		try {
			perms.save(permFile);
		} catch (IOException e) {
			log.severe(AdminTime.logPref + "Could not save permissions file");
		}
	}
	
	public void enterAdminMode(Player p, String worldName)
	{
		swapPermSets(adMode, regMode, p, worldName);
	}
	
	public void exitAdminMode(Player p, String worldName)
	{
		swapPermSets(regMode, adMode, p, worldName);
	}
	
	private void swapPermSets(String set1, String set2, Player p, String worldName)
	{
		if (!(worldName == null || worldName.equalsIgnoreCase("")))
		{
			List<String> newPerms = perms.getStringList(set1 + "." + worldName);
			List<String> oldPerms = perms.getStringList(set2 + "." + worldName);
			setPerms(oldPerms, p, false);
			setPerms(newPerms, p, true);
		}
		
		List<String> allNewPerms = perms.getStringList(set1 + ".allWorlds");
		List<String> allOldPerms = perms.getStringList(set2 + ".allWorlds");
		setPerms(allOldPerms, p, false);
		setPerms(allNewPerms, p, true);
	}
	
	private void setPerms(List<String> permsToMod, Player p, boolean tf)
	{
		for (String perm : permsToMod)
				p.addAttachment(plugin, perm, tf);
		
		p.recalculatePermissions();				
	}
	
	public void reload()
	{
		perms = YamlConfiguration.loadConfiguration(permFile);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent evt)
	{
		exitAdminMode(evt.getPlayer(), evt.getPlayer().getWorld().getName());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent evt)
	{
		Player p = evt.getPlayer();
		if (AdminTime.inAdminMode.containsKey(p) && AdminTime.inAdminMode.get(p))
		{
			plugin.tellAll(p.getDisplayName(), "left", "");
			exitAdminMode(p, p.getWorld().getName());
			p.teleport(AdminTime.lastLocs.get(p));
			AdminTime.inAdminMode.remove(p);
			AdminTime.lastLocs.remove(p);
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerChangeWorld(PlayerChangedWorldEvent evt)
	{
		Player p = evt.getPlayer();
		if (AdminTime.inAdminMode.containsKey(p) && AdminTime.inAdminMode.get(p))
		{
			exitAdminMode(p, evt.getFrom().getName());
			enterAdminMode(p, p.getWorld().getName());
		}
	}
}
