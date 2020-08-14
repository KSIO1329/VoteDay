package ksio.voteday.configuration;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import ksio.voteday.main.VotePlugin;

public class FileManager {
	
	VotePlugin plugin;
	private File ConfigFile = null;
	private FileConfiguration Config = null;

	private File MessagesFile = null;
	private FileConfiguration Messages = null;
	
	public FileManager(VotePlugin plugin) {
		this.plugin = plugin;
		saveDefaultConfig();
		reloadConfig();
		saveDefaultMessages();
		reloadMessages();
	}
	public ksio.voteday.messages.Message reloadAll() {
		if (reloadConfig() && reloadMessages()) {
			try {
				plugin.getVoteManager().clearAll();
				ksio.voteday.messages.Message.refreshAll();
			} catch (Exception e) {
				return ksio.voteday.messages.Message.reloadFailure;
			}
		} else return ksio.voteday.messages.Message.reloadFailure;
		return ksio.voteday.messages.Message.reloadSuccess;
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
	public void saveDefaultMessages() {
	    if (MessagesFile == null) {
	    	MessagesFile = new File(plugin.getDataFolder(), "messages.yml");
	    }
	    if (!MessagesFile.exists()) {            
	         plugin.saveResource("messages.yml", false);
	     }
	}
}
