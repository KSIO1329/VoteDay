package ksio.voteday.main;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import ksio.voteday.messages.MessageManager.Message;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Vote {

	private int runnableID;
	
	private boolean BossBarEnabled;
	private BossBar bar;
	
	private VoteDay vd;
	
	private int maxPlayers;
	private World world;
	private boolean isDone = false;
	
	private List<UUID> players = new ArrayList<UUID>();
	
	public Vote(World world, VoteDay vd) {
		this.world = world;
		this.vd = vd;
		
		ConfigurationSection bossbarConfig = vd.getFileManager().getConfig().getConfigurationSection("bossBar");
		BossBarEnabled = bossbarConfig.getBoolean("enabled");
		if (BossBarEnabled) {
		bar = Bukkit.createBossBar(ChatColor.translateAlternateColorCodes('&', bossbarConfig.getString("name")), BarColor.valueOf(bossbarConfig.getString("color")), BarStyle.valueOf(bossbarConfig.getString("style")));
		bar.setVisible(true);
		}
		// add all players
		editMaxPlayersSafe();
		if (!isDone) {
			vd.getMessageManager().broadcastVote(world, Message.voteStartMessage.value);
			doCancelEvent();
		}
	}
	public void doCancelEvent() {
		setID(vd.getServer().getScheduler().scheduleSyncDelayedTask(vd, new Runnable() {
            @Override
            public void run() {
		        finish(false);
            }
        }, vd.getFileManager().getConfig().getConfigurationSection("vote").getLong("duration")));
	}
	// HANDLE PLAYERS
	public void addPlayer(Player player) {
		players.add(player.getUniqueId());
		// you voted
		vd.getMessageManager().broadcastVoteSpigot(world, fitStatus(vd.getMessageManager().parsePlayer(Message.voteNotifyAdd, player)));
		check();
		// update GUI
		updateBar();
	}
	
	public void removePlayer(Player player){
		if (players.contains(player.getUniqueId())) 
			players.remove(player.getUniqueId());
		vd.getMessageManager().broadcastVoteSpigot(world, fitStatus(vd.getMessageManager().parsePlayer(Message.voteNotifyRemove, player)));
		check();
		updateBar();
		//Bukkit.getServer().spigot().broadcast(mc);
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
		if (!BossBarEnabled || isDone) return;
		if (maxPlayers == 0)
			bar.setProgress(0.0);
		else
			bar.setProgress(((double) players.size()) / maxPlayers);
	}
	
	private void updatePlayers() {
		if (!BossBarEnabled || isDone) return;
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
		//if (BossBarEnabled)
		//if (bar.getProgress() >= vd.getFileManager().getConfig().getDouble("vote_percentage") / 100) {
		//	pass();
		//} else 
		if ((double)((double)players.size() / (double)maxPlayers) >= vd.getFileManager().getConfig().getConfigurationSection("vote").getDouble("percentage") / 100) {
			pass();
			return;
		}
		if (players.size() == 0) {
			finish(false);
			return;
		}
	}
	public void pass() {
		if (vd.getFileManager().getConfig().getConfigurationSection("dayAnimation").getBoolean("enabled"))
			dayAnimation();
		else
			world.setTime(1000);
		finish(true);
	}
	public void finish(boolean success) {
		clearBar();
		isDone = true;
		Bukkit.getScheduler().cancelTask(runnableID);
		if (!success)
			vd.getMessageManager().broadcastVote(getWorld(), Message.voteFailedMessage.value);
		else
			vd.getMessageManager().broadcastVote(world, Message.votePassedMessage.value);
	}
	void dayAnimation() {
		vd.getServer().getScheduler().scheduleSyncDelayedTask(vd, new Runnable() {
            @Override
            public void run() {
		        if (world.getTime() < 1000 || world.getTime() > 12000) {
		        	long step = vd.getFileManager().getConfig().getConfigurationSection("dayAnimation").getLong("time_step");
		        	long newTime = world.getTime() + step;
		        	world.setTime(newTime < 24000 ? newTime : newTime - 24000);
		        	dayAnimation();
		        }
            }
        }, vd.getFileManager().getConfig().getConfigurationSection("dayAnimation").getLong("tick_delay"));
	}
	// TEXT COMPONENT
	TextComponent getStatus() {
		TextComponent mainComponent = new TextComponent(players.size() + "/" + maxPlayers);
		mainComponent.setColor(net.md_5.bungee.api.ChatColor.WHITE);
		if (players.size() > 0)
		mainComponent.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(getPlayerNamesString()).create() ) );
		return mainComponent;
	}
	public TextComponent fitStatus(String msg) {
		TextComponent mc = new TextComponent(msg.split("%status%")[0]);
		mc.addExtra(getStatus());
		mc.addExtra(new TextComponent(msg.split("%status%")[1]));
		return mc;
	}
	public String getPlayerNamesString() {
		String r = "";
		if (players != null)
			for (UUID uuid : players)
				r = r + Bukkit.getPlayer(uuid).getDisplayName() + "\n";
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
	public boolean isDone() {
		return isDone;
	}
	
}
