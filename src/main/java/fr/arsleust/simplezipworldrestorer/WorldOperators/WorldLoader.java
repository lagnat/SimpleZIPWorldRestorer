package fr.arsleust.simplezipworldrestorer.WorldOperators;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;

import fr.arsleust.simplezipworldrestorer.Plugin;
import fr.arsleust.simplezipworldrestorer.Exceptions.NoSuchWorldException;
import fr.arsleust.simplezipworldrestorer.Exceptions.SendableException;
import fr.arsleust.simplezipworldrestorer.Exceptions.UsageException;

public class WorldLoader extends WorldOperator {
		
	private String worldName;
	
	public WorldLoader(Plugin plugin, CommandSender sender, String [] args) throws UsageException {
		super(plugin, sender, args);
		worldName = plugin.buildWorldName(sender, args, 0);
	}

	public void execute() throws SendableException, UsageException {
		if(exists()) {
			if(!isWorldAlreadyLoaded()) {
				createWorld();
				jobDone = true;
			} else {
				throw new SendableException("World " + worldName + " is already loaded.");
			}
		} else {
			throw new NoSuchWorldException(worldName);
		}
	}
	
	private void createWorld() {
		WorldCreator worldCreator = new WorldCreator(worldName);
		worldCreator.createWorld();
	}
	
	private boolean isWorldAlreadyLoaded() {
		World world = Bukkit.getWorld(worldName);
		if(world != null) {
			return true;
		} else {
			return false;
		}
	}

	private File getWorldFolder() {
		File worldContainer = Bukkit.getWorldContainer();
		File worldFolder = new File(worldContainer, worldName);
		return worldFolder;
	}
	
	private boolean exists() {
		File worldFolder = getWorldFolder();
		return worldFolder.isDirectory();
	}
	
	public Boolean isJobDone() {
		return this.jobDone;
	}
	
	public String getResultMessage() {
		if(isJobDone()) {
			return "Successfully loaded world " + worldName;
		} else {
			return "World " + worldName + " isn't loaded yet.";
		}
	}
	
}
