package ksio.voteday.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ksio.voteday.main.VotePlugin;

public class PlayerChange implements Listener{
	
	VotePlugin plugin;
	
	public PlayerChange(VotePlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		plugin.getVoteManager().playerChange(event.getPlayer().getWorld());
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		plugin.getVoteManager().playerChange(event.getPlayer().getWorld());
	}
	@EventHandler
	public void onPortal(PlayerChangedWorldEvent event) {
		plugin.getVoteManager().playerChange(event.getFrom());
	}
}
