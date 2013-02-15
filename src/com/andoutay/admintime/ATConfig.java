package com.andoutay.admintime;

import org.bukkit.configuration.Configuration;

public class ATConfig
{
	private static Configuration config;
	
	public static boolean useGod, usePexForWE;
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
		usePexForWE = config.getBoolean("usePexForWE");
	}
}
