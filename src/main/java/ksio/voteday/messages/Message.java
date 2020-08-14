package ksio.voteday.messages;

import ksio.voteday.main.VotePlugin;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;


public enum Message{
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
	
	private String value;
	static VotePlugin plugin;
	 
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
	
	public static void load(VotePlugin plugin) {
		Message.plugin = plugin;
		Message.refreshAll();
	}

	public String getString() {
		return value;
	}

	public static void broadcastVote(World world, String message) {
		for (Player p : world.getPlayers()) {
			p.sendMessage(message);
		}
		Bukkit.getConsoleSender().sendMessage(message);
	}

	public static void broadcastVote(World world,TextComponent message) {
		for (Player p : world.getPlayers()) {
			p.spigot().sendMessage(message);
		}
		Bukkit.getConsoleSender().sendMessage(message.toLegacyText());
	}

	public static String parseWorld(Message message, World world) {
		return message.value.replaceAll("%world%", world.getName().toUpperCase());
	}

	public static String parsePlayer(Message message, Player player) {
		return message.value.replaceAll("%player_name%", player.getDisplayName());
	}

	public static TextComponent parseStatus(String message, List<UUID> players, int goal) {
		// CREATE LIST
		String playerList = "";
		if (players != null)
			for (UUID uuid : players) {
				if (!playerList.equals("")) 
					playerList = playerList + "\n";
				playerList = playerList + Bukkit.getPlayer(uuid).getDisplayName();
			}
		// CREATE STATUS COMPONENT
		TextComponent statusComponent = new TextComponent(players.size() + "/" + goal);
		statusComponent.setColor(net.md_5.bungee.api.ChatColor.WHITE);
		if (players.size() > 0)
			statusComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(playerList)));
		// CREATE MAIN COMPONENT
		TextComponent mainComponent = new TextComponent(message.split("%status%")[0]);
		mainComponent.addExtra(statusComponent);
		mainComponent.addExtra(new TextComponent(message.split("%status%")[1]));
		return mainComponent;
	}
}
