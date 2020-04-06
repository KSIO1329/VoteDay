package ksio.voteday.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import ksio.voteday.commands.VoteDayCommand;
import ksio.voteday.listeners.MaxPlayerChange;


public class VoteDay extends JavaPlugin{
	
	public Vote vote = null;
	private int voteID = 0;
	private List<Integer> IDs = new ArrayList<Integer>();
	
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
		//
		Bukkit.getServer().getLogger().info("Disabled " + Bukkit.getName() + " v" + Bukkit.getVersion() + ".");
	}
	
	
	
	
	
	public void stopVote() {
		vote = null;
	}
	public void startVote(String world) {
		vote = new Vote(world, this, voteID);
		if (voteID == Integer.MAX_VALUE) voteID = 0;
		IDs.add(voteID);
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                if (vote != null) {
                	if (vote.id == IDs.get(0)) {
                	vote.clearGUI();
                	stopVote();
                	Bukkit.broadcastMessage(getPrefix() + ChatColor.RED + "Not enough votes to pass, voting failed.");
                	}
                	IDs.remove(0);
                }
            }
        }, 1200L);
		voteID++;
	}
	public void vote(Player p) {
		if (vote != null)
			if (!vote.hasVoted(p))
				vote.addPlayer(p);
			else p.sendMessage(getPrefix() + ChatColor.RED + "You already voted.");
	}
	public boolean isVoting(String world) {
		if (vote == null) return false;
		if (vote.getWorld().equalsIgnoreCase(world))
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
