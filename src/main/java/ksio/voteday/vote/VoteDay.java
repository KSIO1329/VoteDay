package ksio.voteday.vote;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import ksio.voteday.main.VotePlugin;
import ksio.voteday.messages.Message;

public class VoteDay extends Vote{

	public VoteDay(VotePlugin plugin, World world) {
		super(plugin, world);	
		// HANDLE BOSS BAR - WIP
		ConfigurationSection bossbarConfig = plugin.getFileManager().getConfig().getConfigurationSection("bossBar");
		createBar(bossbarConfig.getBoolean("enabled"), ChatColor.translateAlternateColorCodes('&', bossbarConfig.getString("name")), bossbarConfig.getString("color"), bossbarConfig.getString("style"));
		// add all players
		updatePlayers(false);
		if (!done) {
			Message.broadcastVote(world, Message.voteStartMessage.getString());
			doCancelEvent(plugin.getFileManager().getConfig().getConfigurationSection("vote").getLong("duration"));
		}
	}
	// HANDLE PLAYERS
	@Override
	protected void messageAdd(Player player) {
		Message.broadcastVote(world, Message.parseStatus(Message.parsePlayer(Message.voteNotifyAdd, player), players, goal));
	}
	@Override
	protected void messageRemove(Player player) {
		Message.broadcastVote(world, Message.parseStatus(Message.parsePlayer(Message.voteNotifyRemove, player), players, goal));
	}
	@Override
	public void handlePlayer(Player player) {
		if (hasVoted(player)) {
			player.sendMessage(Message.alreadyVoted.getString());
			return;
		}
		addPlayer(player);
		updateBar();
	}
	// PASS AND CHECK
	@Override
	protected void check() {
		super.check(plugin.getFileManager().getConfig().getConfigurationSection("vote").getDouble("percentage"));
	}
	@Override
	protected void finish(boolean success) {
		if (!success) {
			Message.broadcastVote(getWorld(), Message.voteFailedMessage.getString());
			super.finish(success);
		}
		else {
			Message.broadcastVote(world, Message.votePassedMessage.getString());
			if (plugin.getFileManager().getConfig().getConfigurationSection("dayAnimation").getBoolean("enabled")) {
				super.finish(success, false);
				dayAnimation();
			}
			else {
				world.setTime(1000);
				super.finish(success);
			}
		}
	}
	private void dayAnimation() {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
		        if (world.getTime() < 1000 || world.getTime() > 12000) {
		        	long step = plugin.getFileManager().getConfig().getConfigurationSection("dayAnimation").getLong("time_step");
		        	long newTime = world.getTime() + step;
		        	world.setTime(newTime < 24000 ? newTime : newTime - 24000);
		        	dayAnimation();
		        } else {
		        	done = true;
		        	plugin.getVoteManager().clearVote(VoteDay.this);
		        }
            }
        }, plugin.getFileManager().getConfig().getConfigurationSection("dayAnimation").getLong("tick_delay"));
	}	
}
