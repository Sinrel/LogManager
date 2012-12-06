package org.sinrel.logmanager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.sinrel.logmanager.LogManager;

public class Logtime implements CommandExecutor {

	private LogManager plugin;
	
	public Logtime(LogManager manager){
		plugin = manager;
	}
	
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if(!arg0.hasPermission("logmanager.logtime")) return true;
		plugin.getConfig().set("auto-log.time", Integer.parseInt(arg3[0]));
		plugin.saveConfig();
		plugin.resetAutoLogTimer();
		arg0.sendMessage(plugin.getMessage("auto-logTimeChanged"));
		return true;
	}

}
