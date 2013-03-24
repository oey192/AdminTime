package com.andoutay.admintime;

import org.bukkit.configuration.Configuration;

public class ATConfig
{
	private static Configuration config;
	
	public static boolean useGod, useFly, usePex, dispDebug;
	private static AdminTime plugin;
	
	ATConfig(AdminTime plugin)
	{
		ATConfig.plugin = plugin;
		config = plugin.getConfig().getRoot();
		config.options().copyDefaults(true);
		plugin.saveConfig();
	}
	
	public static void onEnable()
	{
		loadConfigVals();
	}
	
	public static void reload()
	{
		plugin.reloadConfig();
		config = plugin.getConfig().getRoot();
		onEnable();
	}
	
	public static void loadConfigVals()
	{
		useGod = config.getBoolean("useGod");
		useFly = config.getBoolean("useFly");
		usePex = config.getBoolean("usePex");
		dispDebug = config.getBoolean("dispDebug");
	}
}
