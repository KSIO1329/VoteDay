package ksio.voteday.main;


import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import ksio.voteday.commands.MainCommand;
import ksio.voteday.configuration.FileManager;
import ksio.voteday.listeners.PlayerChange;
import ksio.voteday.messages.Message;
import ksio.voteday.vote.VoteManager;


public class VotePlugin extends JavaPlugin{
	
	private FileManager fileManager;
	private VoteManager voteManager;
	
	private int version = 0;
	Logger logger;
	
	@SuppressWarnings("unused")
	@Override
	public void onEnable() {
		logger = Bukkit.getServer().getLogger();
		// CHECK VERSION
		version = Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().split("v1_")[1].split("_")[0]);
		// REGISTER CONFIG
		fileManager = new FileManager(this);
		// REGISTER MESSAGES
		Message.load(this);
		// REGISTER VOTE MANAGER
		voteManager = new VoteManager(this);
		// REGISTER COMMANDS
		MainCommand mainCommand = new MainCommand(this);
		// REGISTER LISTENERS
		this.getServer().getPluginManager().registerEvents(new PlayerChange(this), this);
		// CHECK FOR UPDATES
		new UpdateChecker(this, 77077).getVersion(version -> {
	        if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
	            logger.info("There is not a new update available.");
	        } else {
	            logger.info("There is a new update available.");
	        }
	    });
		//
		logger.info("Enabled " + getDescription().getName() + " v" + getDescription().getVersion() + ".");
	}
	@Override
	public void onDisable() {
		// TURN OFF BAR
		voteManager.disable();
		logger.info("Disabled " + getDescription().getName() + " v" + getDescription().getVersion() + ".");
	}
	
	public VoteManager getVoteManager() {
		return voteManager;
	}
	public FileManager getFileManager() {
		return fileManager;
	}
	public int getVersion() {
		return version;
	}
}
