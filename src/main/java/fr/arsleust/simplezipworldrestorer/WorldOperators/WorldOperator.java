package fr.arsleust.simplezipworldrestorer.WorldOperators;

import org.bukkit.command.CommandSender;

import fr.arsleust.simplezipworldrestorer.Plugin;
import fr.arsleust.simplezipworldrestorer.Exceptions.SendableException;
import fr.arsleust.simplezipworldrestorer.Exceptions.UsageException;

public abstract class WorldOperator {
	
	protected boolean jobDone = false;
	protected String resultMessage;
	protected Plugin plugin;
	protected CommandSender sender;
	protected String [] args;
	
	public WorldOperator(Plugin plugin, CommandSender sender, String [] args) {
		this.plugin = plugin;
		this.sender = sender;
		this.args = args;
	}

	public abstract void execute() throws SendableException, UsageException;
	
	public Boolean isJobDone() { 
		return jobDone;
	}

	public String getResultMessage() {
		return resultMessage;
	}
}
