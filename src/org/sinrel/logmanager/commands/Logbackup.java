package org.sinrel.logmanager.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.sinrel.logmanager.LogManager;


public class Logbackup implements CommandExecutor {
	 
	@SuppressWarnings("unused")
	private LogManager plugin;
	
	public Logbackup(LogManager plugin){
		this.plugin = plugin;
	}
	
	private String getDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
	}
 
	@Override
	public boolean onCommand(final CommandSender sender, Command command, String label, String[] args){
		if(!sender.hasPermission("logmanager.logbackup")) return true;
		
		Runnable r = new Runnable(){
			public void run(){
				File backupdir = new File("backup");
				File backup = new File(backupdir,"server-" + getDate() + ".log");
				try {
					if(!backupdir.exists()) backupdir.mkdirs();
					backup.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				copyFile(new File("server.log"), backup);
			}
		};
		r.run();
		return true;
	}
	
    private static Boolean copyFile(File source, File dest) {
        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            int nLength;
            byte[] buf = new byte[8000];
            while (true) {
                nLength = is.read(buf);
                if (nLength < 0) {
                    break;
                }
                os.write(buf, 0, nLength);
            }
            return true;
        } catch (IOException ex) {
             ex.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception ex) {
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (Exception ex) {
                }
            }
        }
        return false;
    }
}