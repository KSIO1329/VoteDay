package ksio.voteday.main;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import ksio.voteday.commands.VoteDayCommand;
import ksio.voteday.listeners.MaxPlayerChange;


public class VoteDay extends JavaPlugin{
	
	private Vote vote = null;
	
	@Override
	public void onEnable() {
		// REGISTER COMMANDS
		VoteDayCommand vdc = new VoteDayCommand(this);
		this.getCommand("voteday").setExecutor(vdc);
		this.getCommand("voteday").setTabCompleter(vdc);
		// REGISTER LISTENERS
		this.getServer().getPluginManager().registerEvents(new MaxPlayerChange(this), this);
		//
		Bukkit.getServer().getLogger().info("Enabled " + Bukkit.getName() + " v" + Bukkit.getVersion() + ".");
	}
	@Override
	public void onDisable() {
		// TURN OFF BAR
		if (vote != null)
			vote.clearBar();
		Bukkit.getServer().getLogger().info("Disabled " + Bukkit.getName() + " v" + Bukkit.getVersion() + ".");
	}
	
	public void startVote(World world) {
		vote = new Vote(world, this);
		vote.setID(getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
		        vote.finish();
		        Bukkit.broadcastMessage(getPrefix() + ChatColor.RED + "Not enough votes to pass, voting failed.");
            }
        }, 1200L));
	}
	
	public boolean isVotingInWorld(World world) {
		if (vote == null) return false;
		if (vote.isDone()) return false;
		if (vote.getWorld().equals(world))
			return true;
		return false;
	}
	public Vote getVote() {
		return vote;
	}
	public String getPrefix() {
		return ChatColor.GOLD + "[" + ChatColor.YELLOW + "Vote Day" + ChatColor.GOLD + "] " + ChatColor.RESET;
	}
}
