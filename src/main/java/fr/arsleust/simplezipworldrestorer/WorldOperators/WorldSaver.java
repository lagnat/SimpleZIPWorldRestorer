package fr.arsleust.simplezipworldrestorer.WorldOperators;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import fr.arsleust.simplezipworldrestorer.Plugin;
import fr.arsleust.simplezipworldrestorer.Exceptions.NoSuchWorldException;
import fr.arsleust.simplezipworldrestorer.Exceptions.UsageException;

public class WorldSaver extends WorldOperator {
	
	private final String worldName;
	
	public WorldSaver(Plugin plugin, CommandSender sender, String [] args) throws UsageException {
		super(plugin, sender, args);
		
		worldName = plugin.buildWorldName(sender, args, 0);
	}
	
	public void execute() throws NoSuchWorldException {
		World world = Bukkit.getWorld(worldName);
		if(world == null) {
			throw new NoSuchWorldException(worldName);
		}
		world.save();
		jobDone = true;
	}
	
	public Boolean isJobDone() {
		return this.jobDone;
	}
	
	public String getResultMessage() {
		if(isJobDone()) {
			return "Successfully saved world " + worldName;
		} else {
			return "World " + worldName + " isn't saved yet.";
		}
	}
	
}
