package ksio.voteday.vote;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;

import ksio.voteday.main.VotePlugin;
import ksio.voteday.messages.Message;

public class VoteManager {
	
	final VotePlugin plugin;
	
	private List<Vote> votes = new ArrayList<Vote>();
	
	public VoteManager(VotePlugin plugin) {
		this.plugin = plugin;
	}
	public void disable() {
		for (Vote vote : votes) {
			vote.clearBar();
		}
	}
	protected Vote getVote(World world) {
		for (Vote vote : votes) {
			if (vote.getWorld().equals(world)) return vote;
		}
		return null;
	}
	public void vote(Player player, VoteType type) {
		Vote vote = getVote(player.getWorld());
		if (vote != null) {
			vote.handlePlayer(player);
		} else {
			switch(type) {
			case Day:
				List<String> worlds = plugin.getFileManager().getConfig().getConfigurationSection("vote").getStringList("worlds");
				if (!worlds.contains(player.getWorld().getName())) {
					//if (p.getWorld().getName().equalsIgnoreCase("world_nether") || p.getWorld().getName().equalsIgnoreCase("world_the_end")) {
					// Send wrong world bruh
					player.sendMessage(Message.parseWorld(Message.voteFailWorld, player.getWorld()));
					return;
				}
				if (player.getWorld().getTime() > 0 && player.getWorld().getTime() < 12000) {
					// Send wrong time bruh
					player.sendMessage(Message.voteFailTime.getString());
					return;
				}					
				vote = new VoteDay(plugin, player.getWorld());
				votes.add(vote);
				vote.handlePlayer(player);
				break;
			default:
				break;
			}
		}
		
	}
	public void unvote(Player player) {
		Vote vote = getVote(player.getWorld());
		if (vote != null) {
			vote.removePlayer(player);
		} else {
			// There is no vote
		}
	}
	public void clearVote(Vote vote) {
		if (vote == null) return;
		votes.removeIf(voteD -> voteD.getWorld().equals(vote.getWorld()));
	}
	public void clearAll() {
		for (Vote vote : votes) {
			vote.clearBar();
			vote.cancelEvent();
		}
		votes = new ArrayList<Vote>();
	}
	public void sendStatusMessage(Player player) {
		// CHECK IF HE IS VOTING
		for (Vote vote : votes)
			if (vote.hasVoted(player)) {
				player.spigot().sendMessage(Message.parseStatus(Message.statusCommand.getString(), vote.players, vote.goal));
				return;
			}
		player.sendMessage(Message.noVote.getString());
	}
	public void playerChange(World world) {
		Vote vote = getVote(world);
		if (vote == null) return;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
		        vote.updatePlayers(true);
            }
        }, 1L);
	}
}
