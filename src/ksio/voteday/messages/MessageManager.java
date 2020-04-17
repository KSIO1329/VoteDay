package ksio.voteday.messages;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import ksio.voteday.main.VoteDay;
import net.md_5.bungee.api.chat.TextComponent;

public class MessageManager {

	static VoteDay plugin;
	
	public MessageManager(VoteDay plugin) {
		MessageManager.plugin = plugin;
		Message.refreshAll();
	}
	
	public void broadcastVote(World world, String message) {
		for (Player p : world.getPlayers()) {
			p.sendMessage(message);
		}
		Bukkit.getConsoleSender().sendMessage(message);
	}
	public void broadcastVoteSpigot(World world,TextComponent message) {
		for (Player p : world.getPlayers()) {
			p.spigot().sendMessage(message);
		}
		Bukkit.getConsoleSender().spigot().sendMessage(message);
	}
	public String parseWorld(Message message, World world) {
		return message.value.replaceAll("%world%", world.getName().toUpperCase());
	}
	public String parsePlayer(Message message, Player player) {
		return message.value.replaceAll("%player_name%", player.getDisplayName());
	}
	public static enum Message{
		pluginPrefix,
		voteStartMessage,
		voteFailedMessage,
		votePassedMessage,
		noVote,
		noPermission,
		voteNotifyAdd,
		voteNotifyRemove,
		helpCommand,
		statusCommand,
		reloadSuccess,
		reloadFailure,
		invalidUsage,
		voteFailWorld,
		voteFailTime,
		alreadyVoted;
		
		public String value;
		 
	    public static void refreshAll() {
	    	FileConfiguration messages = plugin.getFileManager().getMessages();
	        for (Message e : values()) {
	        	String rawValue = messages.getString(e.toString());
	        	// REPLACES
	        	String variableValue = rawValue.replaceAll("%name%", plugin.getDescription().getName())
	        			.replaceAll("%version%", plugin.getDescription().getVersion())
	        			.replaceAll("%prefix%", pluginPrefix.value);
	        	String formattedValue = ChatColor.translateAlternateColorCodes('&', variableValue);
	            e.value = formattedValue;
	        }
	    }
	}
}
