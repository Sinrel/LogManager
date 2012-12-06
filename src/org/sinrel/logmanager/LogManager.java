package org.sinrel.logmanager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import javax.swing.Timer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.sinrel.logmanager.commands.Logauto;
import org.sinrel.logmanager.commands.Logbackup;
import org.sinrel.logmanager.commands.Loginfo;
import org.sinrel.logmanager.commands.Logtime;

public class LogManager extends JavaPlugin{
	
	public Logger log = Logger.getLogger("Minecraft");
	private Boolean autoLogEnabled; 
	private Timer autoLogTimer;
	private boolean delete = false;
	
	private PluginDescriptionFile plugin;
	private FileConfiguration conf;
	
	@Override
	public void onEnable(){
		plugin = this.getDescription();
		
		log.info("["+this.getDescription().getName()+"] "+this.getDescription().getName()+" version "+this.getDescription().getVersion()+" is enabled");
		
		if(!new File(this.getDataFolder(),"config.yml").exists()){
			log.info("["+this.getDescription().getName()+"] Config not found. Create default config");
			createConfig();
			log.info("["+this.getDescription().getName()+"] Config successfully created!");
		}
		
		if(this.getConfig().getBoolean("checkUpdate")){
			checkUpdate();
		}

		getCommand("loginfo").setExecutor(new Loginfo(this));
		getCommand("logbackup").setExecutor(new Logbackup(this));
		getCommand("logauto").setExecutor(new Logauto(this));
		getCommand("logtime").setExecutor(new Logtime(this));
		autoLogEnabled = this.getConfig().getBoolean("auto-log.enabled");
		autoLogStart();
	}
	
	public void resetAutoLogTimer(){
		autoLogEnabled = this.getConfig().getBoolean("auto-log.enabled");
		autoLogTimer.stop();
		autoLogStart();
	}
	
	private void autoLogStart(){
		autoLogTimer = new Timer(this.getConfig().getInt("auto-log.time") * 1000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				getCommand("logbackup").execute(Bukkit.getConsoleSender(), "logbackup", null);
				getCommand("logclear").execute(Bukkit.getConsoleSender(), "logclear", null);
			}
		});
		if(autoLogEnabled) autoLogTimer.start();
	}
	
	private void createConfig(){
		conf = this.getConfig(); 
		
		conf.options().header("Copyright (c) 2012, Sinrel Group");
		
		conf.set("enabled",true);
		conf.set("debug", false);
		conf.set("lang", "en");
		conf.set("checkUpdate", false);
		
		conf.set("auto-log.enabled", false);
		conf.set("auto-log.time", 86400);
		
		conf.set("en.clear", "Log file is successfully cleared");
		conf.set("en.delete","Log file will be deleted on shutdown");
		conf.set("en.deleteNo","Log file will already deleted on shutdown. You can not disable the removal");
		conf.set("en.clearError", "Log file not found! Plugin can not clear log file");
		conf.set("en.checkUpdateError","Check updates now not available");
		conf.set("en.auto-logOn","autosaving logs on");
		conf.set("en.auto-logOff", "autosaving logs off");
		conf.set("en.auto-logTimeChanged", "autosaving logs time changed");
		
		conf.set("ru.clear","Содержимое лог файла успешно удалено");
		conf.set("ru.delete","Лог файл будет удалён при выключении сервера");
		conf.set("ru.deleteNo", "Удаление лог файла при выключении уже включено. Вы не можете отключить удаление");
		conf.set("ru.clearError", "Лог файл не найден! Плагин не может его очистить");
		conf.set("ru.checkUpdateError","Проверка обновления сейчас недоступно");
		conf.set("ru.auto-logOn","Авто сохранение логов включено");
		conf.set("ru.auto-logOff", "Авто сохранение логов выключено");
		conf.set("ru.auto-logTimeChanged", "время авто сохранения логов изменено");
		
		saveConfig();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("logclear") & sender.hasPermission("logmanager.logclear")){ 
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter("server.log"));
				out.write("");
				out.close();
			}catch(IOException e) {
				sender.sendMessage("["+plugin.getName()+"] "+getMessage("clearError"));
				return true;
			}
			sender.sendMessage("["+this.getDescription().getName()+"] "+getMessage("clear"));
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("logdelete") & sender.hasPermission("logmanager.logdelete")){
			if(!delete){
				 new File("config.yml").deleteOnExit();
				 delete = true;
				 sender.sendMessage(getMessage("delete"));
				 return true;
			}else{
				sender.sendMessage(getMessage("deleteNo"));
				return true;
			}				
		}
		return false;
	}	

	public String getMessage(String path){
		if(getConfig().isString(getConfig().getString("lang")+"."+path)){
			return getConfig().getString(getConfig().getString("lang")+"."+path);
		}else{
			return getConfig().getString("en."+path);
		}
	}

	private void checkUpdate(){
		try{
			URLConnection con = new URL("https://dl.dropbox.com/u/56137671/logmanager_update.txt").openConnection();
			
			String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine().trim();	
			
			if(this.getConfig().getBoolean("debug")){
				log.info("Version from update-server: "+version);
				log.info("Version in plugin: "+this.getDescription().getVersion().trim());
			}
			
			if(!version.equalsIgnoreCase(this.getDescription().getVersion().trim())){
				log.info("Update found!");
			}else{
				log.info("Update not found because you using the last version");
			}
		}catch(CommandException | IOException e){
			if(this.getConfig().getBoolean("debug")){
				this.log.warning("File with update information not found!");
			}	
			log.info(this.getMessage("checkUpdateError"));
		}
	}
	
	public void checkUpdate(CommandSender sender){
		try{
			URLConnection con = new URL("https://dl.dropbox.com/u/56137671/logmanager_update.txt").openConnection();
			
			String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine().trim();	
			
			if(this.getConfig().getBoolean("debug")){
				this.log.info("Version from update-server: "+version);
				this.log.info("Version in plugin: "+this.getDescription().getVersion().trim());
			}
			
			if(!version.equalsIgnoreCase(this.getDescription().getVersion().trim())){
				sender.sendMessage("Update found!");
			}else{
				sender.sendMessage("Update not found because you using the last version");
			}
		}catch(CommandException | IOException e){
			if(this.getConfig().getBoolean("debug")){
				this.log.warning("File with update information not found!");
			}	
			sender.sendMessage(this.getMessage("checkUpdateError"));
		}
	}
	
	@Override
	public void onDisable(){
		log.info("["+this.getDescription().getName()+"] "+this.getDescription().getName()+" version "+this.getDescription().getVersion()+" disabled");
	
		if(delete){
			 new Runnable(){
				 public void run(){
					 try {
						    BufferedWriter out = new BufferedWriter(new FileWriter("server.log"));
						    out.write("");
						    out.close();
						    new File("config.yml").deleteOnExit();
					} catch(Exception e){
						e.printStackTrace();
					}
				 }
			 };
		}
	}
}
