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
		if (vd.isVotingInWorld(event.getPlayer().getWorld())) {
			vd.getVote().editMaxPlayers();
			
		}
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (vd.isVotingInWorld(event.getPlayer().getWorld())) {
			Vote vote = vd.getVote();
			if (vote.hasVoted(event.getPlayer())) {
				vote.editMaxPlayersSafe(-1);
				vote.removePlayer(event.getPlayer());
			} else {
				vote.editMaxPlayers(-1);
			}
		}
	}
	@EventHandler
	public void onPortal(PlayerChangedWorldEvent event) {
		if (vd.isVotingInWorld(event.getPlayer().getWorld()))
			vd.getVote().editMaxPlayers();
		else if (vd.isVotingInWorld(event.getFrom())) {
			Vote vote = vd.getVote();
			if (vote.hasVoted(event.getPlayer())) {
				vote.editMaxPlayersSafe();
				vote.removePlayer(event.getPlayer());
			} else {
				vote.editMaxPlayers();
			}
		}
	}
}
