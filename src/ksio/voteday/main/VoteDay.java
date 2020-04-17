package ksio.voteday.main;


import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import ksio.voteday.commands.VoteDayCommand;
import ksio.voteday.configuration.FileManager;
import ksio.voteday.listeners.MaxPlayerChange;
import ksio.voteday.messages.MessageManager;


public class VoteDay extends JavaPlugin{
	
	private Vote vote = null;
	
	private FileManager fileManager;
	private MessageManager messageManager;
	
	@Override
	public void onEnable() {
		// REGISTER CONFIG
		fileManager = new FileManager(this);
		// REGISTER MESSAGES
		messageManager = new MessageManager(this);
		// REGISTER COMMANDS
		VoteDayCommand vdc = new VoteDayCommand(this);
		this.getCommand("voteday").setExecutor(vdc);
		this.getCommand("voteday").setTabCompleter(vdc);
		// REGISTER LISTENERS
		this.getServer().getPluginManager().registerEvents(new MaxPlayerChange(this), this);
		//
		Bukkit.getServer().getLogger().info("Enabled " + getDescription().getName() + " v" + getDescription().getVersion() + ".");
	}
	@Override
	public void onDisable() {
		// TURN OFF BAR
		if (vote != null)
		if (!vote.isDone())
			vote.clearBar();
		Bukkit.getServer().getLogger().info("Disabled " + getDescription().getName() + " v" + getDescription().getVersion() + ".");
	}
	
	public void startVote(World world) {
		vote = new Vote(world, this);
	}
	
	public boolean isVotingInWorld(World world) {
		if (vote == null) return false;
		if (vote.isDone()) return false;
		return vote.getWorld().equals(world);
	}
	public Vote getVote() {
		return vote;
	}
	public FileManager getFileManager() {
		return fileManager;
	}
	public MessageManager getMessageManager() {
		return messageManager;
	}
}
