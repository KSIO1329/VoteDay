package ksio.voteday.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import ksio.voteday.main.VoteDay;
import net.md_5.bungee.api.chat.TextComponent;

public class VoteDayCommand implements CommandExecutor, TabCompleter{
	
	VoteDay vd;
	
	public VoteDayCommand(VoteDay vd) {
		this.vd = vd;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (label.equalsIgnoreCase("voteday") || label.equalsIgnoreCase("vd")) {
				Player p = (Player) sender;
				if (args.length == 0) {
					voteConfirm(p);
				}
				else if (args.length > 0) {
					switch (args[0].toLowerCase()) {
					case "help":
						p.sendMessage(vd.getPrefix() + ChatColor.GOLD + "This is a plugin that allows you to vote for daytime, no more sleeping needed.");
						p.sendMessage(ChatColor.GOLD + "Main command: " + ChatColor.WHITE + "/voteday" + ChatColor.GOLD + " (" + ChatColor.WHITE + "/vd" + ChatColor.GOLD + " also works). List of commands:");
						p.sendMessage(ChatColor.WHITE + "/voteday help" + ChatColor.YELLOW + " - " + ChatColor.GOLD + "Displays help for the plugin (this page).");
						p.sendMessage(ChatColor.WHITE + "/voteday cancel" + ChatColor.YELLOW + " - " + ChatColor.GOLD + "Removes your vote.");
						p.sendMessage(ChatColor.WHITE + "/voteday confirm" + ChatColor.YELLOW + " - " + ChatColor.GOLD + "Adds your vote.");
						p.sendMessage(ChatColor.WHITE + "/voteday status" + ChatColor.YELLOW + " - " + ChatColor.GOLD + "Shows the current number of players that voted and the maximum players that can vote. Note that only half of that is needed to pass the vote.");
						break;
					case "confirm":
						voteConfirm(p);
						break;
					case "cancel":
						voteCancel(p);
						break;
					case "status":
						if (vd.isVotingInWorld(p.getWorld())) {
							TextComponent mc = new TextComponent(vd.getPrefix() + ChatColor.GOLD + "Currently, (");
							mc.addExtra(vd.getVote().getStatus());
							mc.addExtra(new TextComponent(ChatColor.GOLD + ") have voted."));
							p.spigot().sendMessage(mc);
						}
						else
							p.sendMessage(vd.getPrefix() + ChatColor.RED + "There are currently no voting sessions active.");
						break;
					default:
						p.sendMessage(vd.getPrefix() + ChatColor.RED + "Invalid usage, please use " + ChatColor.WHITE + "/voteday help" + ChatColor.RED + " for help.");
					}
				}
				return true;
			}
		}
		return false;
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
		List<String> commands = new ArrayList<String>();
		commands.add("help");
		commands.add("cancel");
		commands.add("confirm");
		commands.add("status");
		if (sender instanceof Player) {
			if (label.equalsIgnoreCase("voteday") || label.equalsIgnoreCase("vd")) {
				if (args.length == 1) {
					List<String> returns = new ArrayList<String>();
					for (String s : commands) {
						if (s.startsWith(args[0].toLowerCase())) returns.add(s);
					}
					return returns;
				}
			}
		}
		return null;
		
	}
	private void voteConfirm(Player p) {
		if (p.getWorld().getName().equalsIgnoreCase("world_nether") || p.getWorld().getName().equalsIgnoreCase("world_the_end")) {
			// Send wrong world bruh
			p.sendMessage(vd.getPrefix() + ChatColor.RED + "You can't vote from the world " + ChatColor.WHITE + p.getWorld().getName().toUpperCase() + ChatColor.RED + ".");
			return;
		}
		if (p.getWorld().getTime() > 0 && p.getWorld().getTime() < 12000) {
			// Send wrong time bruh
			p.sendMessage(vd.getPrefix() + ChatColor.RED + "You can't vote during daytime.");
			return;
		}
		if (vd.isVotingInWorld(p.getWorld())) {
			vote(p);
		} else {
			vd.startVote(p.getWorld());
			vote(p);
		}
	}
	private void voteCancel(Player p) {
		if (vd.isVotingInWorld(p.getWorld()))
			if (vd.getVote().hasVoted(p))
				vd.getVote().removePlayer(p);
	}
	private void vote(Player p) {
		if (!vd.getVote().hasVoted(p))
			vd.getVote().addPlayer(p);
		else p.sendMessage(vd.getPrefix() + ChatColor.RED + "You already voted.");
	}
}
