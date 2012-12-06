package org.sinrel.logmanager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.sinrel.logmanager.LogManager;


public class Loginfo implements CommandExecutor {
	 
	private LogManager plugin;
	
	public Loginfo(LogManager plugin){
		this.plugin = plugin;
	}
 
	@Override
	public boolean onCommand(final CommandSender sender, Command command, String label, String[] args){
		if(!sender.hasPermission("logmanager.loginfo")) return true;
		plugin.checkUpdate(sender);	
		
		return true;
	}
}
