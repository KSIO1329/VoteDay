package ksio.voteday.main;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class Vote {

	int id;
	
	BossBar bar;
	
	VoteDay vd;
	
	int maxPlayers;
	String world;
	
	List<UUID> players = new ArrayList<UUID>();
	
	public Vote(String world, VoteDay vd, int id) {
		this.id = id;
		this.world = world;
		bar = Bukkit.createBossBar(ChatColor.GOLD + "Vote for daytime", BarColor.YELLOW, BarStyle.SEGMENTED_10);
		bar.setVisible(true);
		this.vd = vd;
		// add all players
		editMaxPlayers();
		Bukkit.broadcastMessage(vd.getPrefix() + ChatColor.GOLD + "Voting for day time, type " + ChatColor.WHITE + "/voteday" + ChatColor.GOLD + " or " + ChatColor.WHITE + "/vd" + ChatColor.GOLD + " to vote.");
	}
	
	public void addPlayer(Player p) {
		players.add(p.getUniqueId());
		// update GUI
		updateGUI();
		// you voted
		TextComponent mc = new TextComponent(vd.getPrefix() + ChatColor.WHITE + p.getDisplayName() + ChatColor.GOLD + " voted for day time. (" );
		mc.addExtra(getStatus());
		mc.addExtra(new TextComponent(ChatColor.GOLD + ")"));
		Bukkit.getServer().spigot().broadcast(mc);
		passVote();
	}
	
	public void removePlayer(Player p){
		if (players.contains(p.getUniqueId())) 
			players.remove(p.getUniqueId());
		updateGUI();
		TextComponent mc = new TextComponent(vd.getPrefix() + ChatColor.WHITE + p.getDisplayName() + ChatColor.RED + " is no longer voting. (" );
		mc.addExtra(getStatus());
		mc.addExtra(new TextComponent(ChatColor.RED + ")"));
		Bukkit.getServer().spigot().broadcast(mc);
	}
	
	private void updateGUI() {
		bar.setProgress(((double) players.size()) / maxPlayers);
	}
	
	private void updatePlayersGUI() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getWorld().getName().equalsIgnoreCase(world))
				bar.addPlayer(p);
			else if (bar.getPlayers().contains(p))
				bar.removePlayer(p);
			
		}
	}
	
	
	private void passVote() {
		if (bar.getProgress() >= 0.5) {
			Bukkit.getWorld(world).setTime(1000);
			// Clear GUI
			clearGUI();
			// Announce
			Bukkit.broadcastMessage(vd.getPrefix() + ChatColor.GOLD + "Vote passed, rise and shine.");
			vd.stopVote();
		}
	}
	public String getWorld() {
		return world;
	}
	public void clearGUI() {
		bar.setVisible(false);
	}
	public boolean hasVoted(Player player) {
		return players.contains(player.getUniqueId());
	} 
	public int getVoters() {
		return players.size();
	}
	public int getMaxPlayers() {
		return maxPlayers;
	}
	public TextComponent getStatus() {
		TextComponent mainComponent = new TextComponent(getVoters() + "/" + getMaxPlayers());
		mainComponent.setColor(net.md_5.bungee.api.ChatColor.WHITE);
		mainComponent.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(getPlayerNamesString()).create() ) );
		return mainComponent;
	}
	public List<String> getPlayerNames(){
		List<String> playerNames = new ArrayList<String>();
		if (players != null)
		for (UUID uuid : players)
			playerNames.add(Bukkit.getPlayer(uuid).getName());
		return playerNames;
	}
	public String getPlayerNamesString() {
		String r = "";
		List<String> playerNames = getPlayerNames();
		if (playerNames != null)
		for (String s : playerNames)
			r = r + s + "\n";
		return r;
	}

	public void editMaxPlayers() {
		this.maxPlayers = Bukkit.getWorld(world).getPlayers().size();
		updatePlayersGUI();
		updateGUI();
		passVote();
	}
	public void editMaxPlayers(int i) {
		this.maxPlayers = Bukkit.getWorld(world).getPlayers().size() + i;
		updatePlayersGUI();
		updateGUI();
		passVote();
		
	}
	
}
