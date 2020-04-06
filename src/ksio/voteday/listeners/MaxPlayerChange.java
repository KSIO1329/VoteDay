package ksio.voteday.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ksio.voteday.main.Vote;
import ksio.voteday.main.VoteDay;

public class MaxPlayerChange implements Listener{
	
	VoteDay vd;
	
	public MaxPlayerChange(VoteDay voteDay) {
		vd = voteDay;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (vd.isVoting(event.getPlayer().getWorld().getName())) {
			vd.getVote().editMaxPlayers();
			
		}
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (vd.isVoting(event.getPlayer().getWorld().getName())) {
			Vote vote = vd.getVote();
			if (vote.hasVoted(event.getPlayer())) {
				vote.removePlayer(event.getPlayer());
				vote.editMaxPlayers(-1);
			} else {
				vote.editMaxPlayers(-1);
			}
		}
	}
	@EventHandler
	public void onPortal(PlayerChangedWorldEvent event) {
		if (vd.isVoting(event.getPlayer().getWorld().getName()))
			vd.getVote().editMaxPlayers();
		else if (vd.isVoting(event.getFrom().getName())) {
			Vote vote = vd.getVote();
			if (vote.hasVoted(event.getPlayer())) {
				vote.removePlayer(event.getPlayer());
				vote.editMaxPlayers();
			} else {
				vote.editMaxPlayers();
			}
		}
	}
}
