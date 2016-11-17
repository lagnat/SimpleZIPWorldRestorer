package fr.arsleust.simplezipworldrestorer;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.arsleust.simplezipworldrestorer.Exceptions.SendableException;
import fr.arsleust.simplezipworldrestorer.Exceptions.UsageException;
import fr.arsleust.simplezipworldrestorer.WorldOperators.WorldLoader;
import fr.arsleust.simplezipworldrestorer.WorldOperators.WorldOperator;
import fr.arsleust.simplezipworldrestorer.WorldOperators.WorldRestorer;
import fr.arsleust.simplezipworldrestorer.WorldOperators.WorldSaver;
import fr.arsleust.simplezipworldrestorer.WorldOperators.WorldZipper;

/**
 * SimpleZIPWorldRestorer main class
 * 
 * @author Arlseust (aka Arceus02)
 */
public class Plugin extends JavaPlugin {

	@Override
	public void onEnable() {
		
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Now depending on the command specified, we call for the correspoding world operator
		WorldOperator worldOperator = null;
		
		try {
			if (command.getName().equalsIgnoreCase("worldloader")) {
				worldOperator = new WorldLoader(this, sender, args);
			} else if (command.getName().equalsIgnoreCase("worldsaver")) {
				worldOperator = new WorldSaver(this, sender, args);
			} else if (command.getName().equalsIgnoreCase("worldzipper")) {
				worldOperator = new WorldZipper(this, sender, args);
			} else if (command.getName().equalsIgnoreCase("worldrestorer")) {
				worldOperator = new WorldRestorer(this, sender, args);
			}
			// Check if usage is right, if not return false to show usage
			if(worldOperator == null) {
				return false;
			}
			// Execute the world operator
			worldOperator.execute();
			sender.sendMessage(ChatColor.GREEN + worldOperator.getResultMessage());
		} catch (SendableException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
		} catch (UsageException e) {
			// Return false to show usage
			return false;
		}
		// Return true (usage is checked earlier)
		return true;
	}
	
	public String buildWorldName(CommandSender sender, String[] args, int firstArg) throws UsageException {
		// Get world name specified by the command sender in the arguments
		WorldNameBuilder worldNameBuilder = new WorldNameBuilder(args, firstArg);
		worldNameBuilder.buildWorldName();
		String worldName = worldNameBuilder.getWorldName();
		// If command sender calls for special "*this" (alias "*") try to get the world of the command sender
		if(worldName.equalsIgnoreCase("*this") || worldName.equalsIgnoreCase("*")) {
			if(sender instanceof Player) {
				worldName = ((Player) sender).getLocation().getWorld().getName();
			} else if (sender instanceof CommandBlock) {
				worldName = ((CommandBlock) sender).getLocation().getWorld().getName();
			} else {
				worldName = null;
				sender.sendMessage(ChatColor.RED + "You are neither a player nor a command block ... Who are you ? (can't use *this argument)");
			}
		}
		
		if (worldName == null || worldName.isEmpty())
			throw new UsageException("Missing world name");
		
		return worldName;
	}
	
	public static File getBackupFolder() {
		File dataFolder = Bukkit.getPluginManager().getPlugin("SimpleZIPWorldRestorer").getDataFolder();
		File backupFolder = new File(dataFolder, "backup");
		if(!backupFolder.isDirectory()) {
			backupFolder.mkdirs();
		}
		return backupFolder;
	}

}
