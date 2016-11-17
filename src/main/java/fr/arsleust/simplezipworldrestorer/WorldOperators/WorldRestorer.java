package fr.arsleust.simplezipworldrestorer.WorldOperators;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.arsleust.simplezipworldrestorer.Plugin;
import fr.arsleust.simplezipworldrestorer.Exceptions.NoSuchWorldException;
import fr.arsleust.simplezipworldrestorer.Exceptions.SendableException;
import fr.arsleust.simplezipworldrestorer.Exceptions.UsageException;
import fr.arsleust.simplezipworldrestorer.Util.FileUtil;
import fr.arsleust.simplezipworldrestorer.Util.ZipFileUtil;

public class WorldRestorer extends WorldOperator {

	private final String worldName;
	private boolean kick = false;
	
	public WorldRestorer(Plugin plugin, CommandSender sender, String [] args) throws UsageException {
		super(plugin, sender, args);
		
		int firstArg = 0;
		
		if (args.length > 0 && "kick".equals(args[0])) {
			kick = true;
			firstArg++;
		}
		
		worldName = plugin.buildWorldName(sender, args, firstArg);
	}

	public void execute() throws SendableException {
		if (kick) {
			Bukkit.getLogger().info("[SimpleZIPWorldRestorer] Restoring world " + worldName + " : Kicking players out of the world ...");
			kickPlayers();
		} else {
			Bukkit.getLogger().info("[SimpleZIPWorldRestorer] Restoring world " + worldName + " : Checking for players ...");
			checkForPlayers();
		}
		Bukkit.getLogger().info("[SimpleZIPWorldRestorer] Restoring world " + worldName + " : Unloading world ...");
		unloadWorld();
		Bukkit.getLogger().info("[SimpleZIPWorldRestorer] Restoring world " + worldName + " : Cleaning world ...");
		cleanData();
		Bukkit.getLogger().info("[SimpleZIPWorldRestorer] Restoring world " + worldName + " : Restoring data ...");
		restoreData();
		Bukkit.getLogger().info("[SimpleZIPWorldRestorer] Reloading world " + worldName + " : Reloading world ...");
		reloadWorld();
		Bukkit.getLogger().info("[SimpleZIPWorldRestorer] Reloading world " + worldName + " : Done !");
		jobDone = true;
	}
	
	private void checkForPlayers() throws NoSuchWorldException, SendableException {
		World world = Bukkit.getWorld(worldName);
		if(world == null) {
			throw new NoSuchWorldException(worldName);
		}
		if (!world.getPlayers().isEmpty()) {
			String playerNames = "";
			List<Player> players = world.getPlayers();
			for (int i = 0; i < players.size(); i++) {
				Player player = players.get(i);
				if (i > 0) {
					if (i == players.size() - 1) {
						playerNames += " and ";
					} else {
						playerNames += ", ";
					}
				}
				playerNames += player.getName();
			}
			throw new SendableException("[WorldRestorer] That world still has players in it.  Please wait for " + playerNames + " to leave.");
		}
	}

	private void kickPlayers() throws NoSuchWorldException {
		World world = Bukkit.getWorld(worldName);
		if(world == null) {
			throw new NoSuchWorldException(worldName);
		}
		Location teleportLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
		for(Player p : world.getPlayers()) {
			p.teleport(teleportLocation);
			p.sendMessage(ChatColor.BLUE + "[WorldRestorer] The world you were in was restored. You were kicked out of it.");
		}
	}

	private void unloadWorld() throws NoSuchWorldException {
		World world = Bukkit.getWorld(worldName);
		if(world == null) {
			throw new NoSuchWorldException(worldName);
		}
		// put false to ensure world isn't saved when unloaded
		Bukkit.unloadWorld(world, false);
	}

	private void cleanData() {
		FileUtil.delete(getWorldFolder());
	}
	
	private void restoreData() throws SendableException {
		try {
			ZipFileUtil.unzipFileIntoDirectory(getBackupFile(), getWorldFolder());
		} catch (IOException e) {
			e.printStackTrace();
			throw new SendableException("Error while unzipping, check console for more info.");
		}
	}
	
	private void reloadWorld() {
		WorldCreator worldCreator = new WorldCreator(worldName);
		worldCreator.createWorld();
	}
	
	private File getWorldFolder() {
		File worldContainer = Bukkit.getWorldContainer();
		File worldFolder = new File(worldContainer, worldName);
		return worldFolder;
	}
	
	private File getBackupFile() {
		File backupFolder = Plugin.getBackupFolder();
		File backupFile = new File(backupFolder, worldName + ".zip");
		return backupFile;
	}
	
	public Boolean isJobDone() {
		return this.jobDone;
	}
	
	public String getResultMessage() {
		if(isJobDone()) {
			return "Successfully restored world " + worldName;
		} else {
			return "World " + worldName + " isn't restored yet.";
		}
	}

}
