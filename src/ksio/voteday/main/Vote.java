package ksio.voteday.main;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class Vote {

	private int runnableID;
	
	private BossBar bar;
	
	private VoteDay vd;
	
	private int maxPlayers;
	private World world;
	private boolean isPassed = false;
	
	private List<UUID> players = new ArrayList<UUID>();
	
	public Vote(World world, VoteDay vd) {
		this.world = world;
		bar = Bukkit.createBossBar(ChatColor.GOLD + "Vote for daytime", BarColor.YELLOW, BarStyle.SEGMENTED_10);
		bar.setVisible(true);
		this.vd = vd;
		// add all players
		editMaxPlayers();
		Bukkit.broadcastMessage(vd.getPrefix() + ChatColor.GOLD + "Voting for daytime, type " + ChatColor.WHITE + "/voteday" + ChatColor.GOLD + " or " + ChatColor.WHITE + "/vd" + ChatColor.GOLD + " to vote.");
	}
	// HANDLE PLAYERS
	public void addPlayer(Player player) {
		players.add(player.getUniqueId());
		// update GUI
		updateBar();
		// you voted
		TextComponent mc = new TextComponent(vd.getPrefix() + ChatColor.WHITE + player.getDisplayName() + ChatColor.GOLD + " voted for daytime. (" );
		mc.addExtra(getStatus());
		mc.addExtra(new TextComponent(ChatColor.GOLD + ")"));
		Bukkit.getServer().spigot().broadcast(mc);
		check();
	}
	
	public void removePlayer(Player player){
		if (players.contains(player.getUniqueId())) 
			players.remove(player.getUniqueId());
		updateBar();
		TextComponent mc = new TextComponent(vd.getPrefix() + ChatColor.WHITE + player.getDisplayName() + ChatColor.RED + " is no longer voting. (" );
		mc.addExtra(getStatus());
		mc.addExtra(new TextComponent(ChatColor.RED + ")"));
		Bukkit.getServer().spigot().broadcast(mc);
	}
	public boolean hasVoted(Player player) {
		return players.contains(player.getUniqueId());
	} 
	public void editMaxPlayers() {
		editMaxPlayers(0);
	}
	public void editMaxPlayersSafe() {
		editMaxPlayersSafe(0);
	}
	public void editMaxPlayers(int offset) {
		editMaxPlayersSafe(offset);
		check();
	}
	public void editMaxPlayersSafe(int offset) {
		maxPlayers = world.getPlayers().size() + offset;
		updatePlayers();
		updateBar();
	}
	// GUI AND BAR STUFF
	private void updateBar() {
		if (maxPlayers == 0)
			bar.setProgress(0.0);
		else
			bar.setProgress(((double) players.size()) / maxPlayers);
	}
	
	private void updatePlayers() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getWorld().equals(world))
				bar.addPlayer(p);
			else if (bar.getPlayers().contains(p))
				bar.removePlayer(p);
			
		}
	}
	public void clearBar() {
		bar.setVisible(false);
		bar = null;
	}	
	// PASS AND CHECK
	private void check() {
		if (bar.getProgress() >= 0.5) {
			pass();
		}
	}
	public void pass() {
		world.setTime(1000);
		clearBar();
		Bukkit.broadcastMessage(vd.getPrefix() + ChatColor.GOLD + "Vote passed, rise and shine.");
		Bukkit.getScheduler().cancelTask(runnableID);
		isPassed = true;
	}
	// TEXT COMPONENT
	public TextComponent getStatus() {
		TextComponent mainComponent = new TextComponent(players.size() + "/" + maxPlayers);
		mainComponent.setColor(net.md_5.bungee.api.ChatColor.WHITE);
		if (players.size() > 0)
		mainComponent.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(getPlayerNamesString()).create() ) );
		return mainComponent;
	}
	public String getPlayerNamesString() {
		String r = "";
		if (players != null)
			for (UUID uuid : players)
				r = r + Bukkit.getPlayer(uuid).getName() + "\n";
		return r;
	}
	// SETTERS
	public void setID(int ID) {
		runnableID = ID;
	}
	// GETTERS
	public World getWorld() {
		return world;
	}
	// BOOLS
	public boolean isPassed() {
		return isPassed;
	}
	
}
