package ksio.voteday.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import ksio.voteday.main.VotePlugin;
import ksio.voteday.messages.Message;
import ksio.voteday.vote.VoteType;

public class MainCommand implements CommandExecutor, TabCompleter{
	
	VotePlugin plugin;
	
	public MainCommand(VotePlugin plugin) {
		this.plugin = plugin;
		load();
	}
	void load() {
		plugin.getCommand("voteday").setExecutor(this);
		plugin.getCommand("voteday").setTabCompleter(this);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("voteday") || label.equalsIgnoreCase("vd")) {
			Player p = null;
			if (args.length == 0) {
				if (sender instanceof Player) {
					p = (Player) sender;
					plugin.getVoteManager().vote(p, VoteType.Day);
				}
			}
			else if (args.length > 0) {
				switch (args[0].toLowerCase()) {
				case "help":
					sender.sendMessage(Message.helpCommand.getString());
					break;
				case "confirm":
					if (sender instanceof Player) {
						p = (Player) sender;
						plugin.getVoteManager().vote(p, VoteType.Day);
					}
					break;
				case "cancel":
					if (sender instanceof Player) {
						p = (Player) sender;
						plugin.getVoteManager().unvote(p);
					}
					break;
				case "status":
					if (sender instanceof Player) {
						p = (Player) sender;
						plugin.getVoteManager().sendStatusMessage(p);
					}
					break;
				case "reload":{
					if (!sender.hasPermission("voteday.reload")) {
						sender.sendMessage(Message.noPermission.getString());
					}
					else {
						sender.sendMessage(plugin.getFileManager().reloadAll().getString());
					}
					break;
				}
				default:
					sender.sendMessage(Message.invalidUsage.getString());
				}
			}
			return true;
		}
		return false;
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
		String[] commands = {"help", "cancel", "confirm", "status", "reload"};
		if (sender instanceof Player) {
			if (label.equalsIgnoreCase("voteday") || label.equalsIgnoreCase("vd")) {
				if (args.length == 1) {
					List<String> returns = new ArrayList<String>();
					for (String s : commands) {
						if (s.equals("reload") && !sender.hasPermission("voteday.reload")) continue;
						if (s.startsWith(args[0].toLowerCase())) returns.add(s);
					}
					return returns;
				}
			}
		}
		return null;
		
	}
}
