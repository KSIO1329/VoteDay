package ksio.voteday.vote;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import ksio.voteday.main.VotePlugin;

public abstract class Vote {

	protected VotePlugin plugin;
	
	protected int goal;
	protected World world;
	protected boolean done = false;
	
	protected int runnableID;
	
	protected boolean bossBarEnabled;
	protected Object bar;
	
	protected List<UUID> players = new ArrayList<UUID>();
	
	public Vote(VotePlugin plugin, World world) {
		this.world = world;
		this.plugin = plugin;
	}
	protected void doCancelEvent(long duration) {
		runnableID = (plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
		        finish(false);
            }
        }, duration));
	}
	public void cancelEvent() {
		Bukkit.getScheduler().cancelTask(runnableID);
	}
	// HANDLE PLAYERS
	protected void addPlayer(Player player) {
		players.add(player.getUniqueId());
		addToBar(player);
		messageAdd(player);
		check();
	}
	protected void messageAdd(Player player) {}
	protected void addToBar(Player player) {
		if (!bossBarEnabled || done) return;
		if (plugin.getVersion() >= 13)
			((org.bukkit.boss.BossBar)bar).addPlayer(player);
	}
	protected void removePlayer(Player player){
		messageRemove(player);
		removePlayer(player.getUniqueId());
		if (!bossBarEnabled || done) return;
		if (plugin.getVersion() >= 13)
			((org.bukkit.boss.BossBar)bar).removePlayer(player);
	}
	protected void removePlayer(UUID playerUUID) {
		players.remove(playerUUID);
		check();
	}
	protected void messageRemove(Player player) {}
	public void handlePlayer(Player player) {
	}
	public boolean hasVoted(Player player) {
		return players.contains(player.getUniqueId());
	} 
	public void updatePlayers(boolean check) {
		// ADD
		for (Player player : world.getPlayers()) {
			if (!players.contains(player.getUniqueId())) {
				addToBar(player);
			}
		}
		// REMOVE
		for (UUID playerUUID : getPlayers()) {
			Player player = Bukkit.getPlayer(playerUUID);
			if (player == null) {
				removePlayer(playerUUID);
				continue;
			}
			if (!player.getWorld().equals(world)) {
				removePlayer(player);
			}
		}
		goal = world.getPlayers().size();
		if (check)
			check();
		updateBar();
	}
	// BAR
	protected void createBar(boolean enabled, String name, String color, String style) {
		bossBarEnabled = enabled;
		if (bossBarEnabled) {
			if (plugin.getVersion() >= 13) {
				bar = Bukkit.createBossBar(name, org.bukkit.boss.BarColor.valueOf(color), org.bukkit.boss.BarStyle.valueOf(style));
				((org.bukkit.boss.BossBar)bar).setVisible(true);
			} else {
				// OLDER VERSION
			}
		}
	}
	public void updateBar() {
		if (!bossBarEnabled || done || bar == null) return;
		if (plugin.getVersion() >= 13) {
			// PROGRESS
			if (goal == 0)
				((org.bukkit.boss.BossBar)bar).setProgress(0.0);
			else
				((org.bukkit.boss.BossBar)bar).setProgress(((double) players.size()) / goal);
		} else {
			// OLDER VERSION
		}
	}
	public void clearBar() {
		if (!bossBarEnabled) return;
		if (plugin.getVersion() >= 13) {
			if (bar != null)
				((org.bukkit.boss.BossBar)bar).setVisible(false);
		}
	}	
	// CHECK AND FINISH
	protected void check() {}
	protected void check(double percentage) {
		if (done) return;
		if ((double)players.size() /goal >= percentage / 100) {
			finish(true);
		} else if (players.size() == 0) {
			finish(false);
		}
	}
	protected void finish(boolean success) {
		finish(success, true);
	}
	protected void finish(boolean success, boolean done) {
		clearBar();
		this.done = done;
		cancelEvent();
		if (done)
			plugin.getVoteManager().clearVote(this);
	}
	// GETTERS
	public World getWorld() {
		return world;
	}
	List<UUID> getPlayers(){
		List<UUID> addedPlayers = new ArrayList<UUID>();
		for (UUID playerUUID : players) {
			addedPlayers.add(playerUUID);
		}
		return addedPlayers;
	}
	// BOOLS
	public boolean isDone() {
		return done;
	}
}
