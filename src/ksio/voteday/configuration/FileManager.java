package ksio.voteday.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import ksio.voteday.main.VoteDay;
import ksio.voteday.messages.MessageManager;

public class FileManager {
	
	VoteDay plugin;
	private File ConfigFile = null;
	private FileConfiguration Config = null;

	private File MessagesFile = null;
	private FileConfiguration Messages = null;
	
	public FileManager(VoteDay plugin) {
		this.plugin = plugin;
		saveDefaultConfig();
		reloadConfig();
		saveDefaultMessages();
		reloadMessages();
	}
	public MessageManager.Message reloadAll() {
		if (reloadConfig() && reloadMessages()) {
			try {
			MessageManager.Message.refreshAll();
			} catch (Exception e) {
				return MessageManager.Message.reloadFailure;
			}
		} else return MessageManager.Message.reloadFailure;
		return MessageManager.Message.reloadSuccess;
	}
	public boolean reloadConfig() {
		if (ConfigFile == null) {
			    ConfigFile = new File(plugin.getDataFolder(), "config.yml");
		}
		Config = YamlConfiguration.loadConfiguration(ConfigFile);

		// Look for defaults in the jar
		Reader defConfigStream;
		try {
			defConfigStream = new InputStreamReader(plugin.getResource("config.yml"), "UTF8");
			if (defConfigStream != null) {
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				Config.setDefaults(defConfig);
			}
			return true;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	public FileConfiguration getConfig() {
	    if (Config == null) {
	        reloadConfig();
	    }
	    return Config;
	}
	public void saveConfig() {
	    if (Config == null || ConfigFile == null) {
	        return;
	    }
	    try {
	        getConfig().save(ConfigFile);
	    } catch (IOException ex) {
	        plugin.getLogger().log(Level.SEVERE, "Could not save config to " + ConfigFile, ex);
	    }
	}
	public void saveDefaultConfig() {
	    if (ConfigFile == null) {
	        ConfigFile = new File(plugin.getDataFolder(), "config.yml");
	    }
	    if (!ConfigFile.exists()) {            
	         plugin.saveResource("config.yml", false);
	     }
	}
	
	public boolean reloadMessages() {
		if (MessagesFile == null) {
			MessagesFile = new File(plugin.getDataFolder(), "messages.yml");
		}
		Messages = YamlConfiguration.loadConfiguration(MessagesFile);
	
		// Look for defaults in the jar
		Reader defConfigStream;
		try {
			defConfigStream = new InputStreamReader(plugin.getResource("messages.yml"), "UTF8");
			if (defConfigStream != null) {
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				Messages.setDefaults(defConfig);
			}
			return true;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	public FileConfiguration getMessages() {
		if (Messages == null) {
			reloadMessages();
		}
		return Messages;
	}
	public void saveMessages() {
	    if (Messages == null || MessagesFile == null) {
	        return;
	    }
	    try {
	        getMessages().save(MessagesFile);
	    } catch (IOException ex) {
	        plugin.getLogger().log(Level.SEVERE, "Could not save config to " + MessagesFile, ex);
	    }
	}
	public void saveDefaultMessages() {
	    if (MessagesFile == null) {
	    	MessagesFile = new File(plugin.getDataFolder(), "messages.yml");
	    }
	    if (!MessagesFile.exists()) {            
	         plugin.saveResource("messages.yml", false);
	     }
	}
}
