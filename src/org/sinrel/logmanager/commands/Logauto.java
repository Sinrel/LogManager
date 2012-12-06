package org.sinrel.logmanager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.sinrel.logmanager.LogManager;

public class Logauto implements CommandExecutor {

	private LogManager plugin;
	
	public Logauto(LogManager manager){
		plugin = manager;
	}
	
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if(!arg0.hasPermission("logmanager.logauto")) return true;
		Boolean value = !(plugin.getConfig().getBoolean("auto-log.enabled"));
		plugin.getConfig().set("auto-log.enabled", value);
		if(value)
			arg0.sendMessage(plugin.getMessage("auto-logOn"));
		else
			arg0.sendMessage(plugin.getMessage("auto-logOff"));
		plugin.saveConfig();
		plugin.resetAutoLogTimer();
		return true;
	}

}
