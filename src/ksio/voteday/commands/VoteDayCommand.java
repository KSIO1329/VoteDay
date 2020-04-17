package ksio.voteday.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import ksio.voteday.main.VoteDay;
import ksio.voteday.messages.MessageManager.Message;

public class VoteDayCommand implements CommandExecutor, TabCompleter{
	
	VoteDay vd;
	
	public VoteDayCommand(VoteDay vd) {
		this.vd = vd;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("voteday") || label.equalsIgnoreCase("vd")) {
			Player p = null;
			if (args.length == 0) {
				if (sender instanceof Player) {
					p = (Player) sender;
					voteConfirm(p);
				}
			}
			else if (args.length > 0) {
				switch (args[0].toLowerCase()) {
				case "help":
					sender.sendMessage(Message.helpCommand.value);
					break;
				case "confirm":
					if (sender instanceof Player) {
						p = (Player) sender;
						voteConfirm(p);
					}
					break;
				case "cancel":
					if (sender instanceof Player) {
						p = (Player) sender;
						voteCancel(p);
					}
					break;
				case "status":
					if (sender instanceof Player) {
						p = (Player) sender;
						if (vd.isVotingInWorld(p.getWorld())) {
							p.spigot().sendMessage(vd.getVote().fitStatus(Message.statusCommand.value));
						}
						else
							p.sendMessage(Message.noVote.value);
					}
					break;
				case "reload":{
					if (!sender.hasPermission("voteday.reload")) {
						sender.sendMessage(Message.noPermission.value);
					}
					else {
						sender.sendMessage(vd.getFileManager().reloadAll().value);
					}
					break;
				}
				default:
					sender.sendMessage(Message.invalidUsage.value);
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
	private void voteConfirm(Player p) {
		if (p.getWorld().getName().equalsIgnoreCase("world_nether") || p.getWorld().getName().equalsIgnoreCase("world_the_end")) {
			// Send wrong world bruh
			p.sendMessage(vd.getMessageManager().parseWorld(Message.voteFailWorld, p.getWorld()));
			return;
		}
		if (p.getWorld().getTime() > 0 && p.getWorld().getTime() < 12000) {
			// Send wrong time bruh
			p.sendMessage(Message.voteFailTime.value);
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
		else p.sendMessage(Message.alreadyVoted.value);
	}
}
