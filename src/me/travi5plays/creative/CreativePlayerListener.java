package me.travi5plays.creative;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.Overridden;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class CreativePlayerListener
extends JavaPlugin
implements Listener
{
	private String[] creativeWorlds;
	private String[] bannedIDs;
	private String returnWorld;
	private boolean allAccess = true;
	private boolean closedtoAll = false;
	private boolean weatherAllow = true;
	private boolean witherAllow = false;
	private boolean respawnSpawnCords = true;
	private boolean dispencerNerf = true;
	private boolean messagePlayer = true;
	private boolean dispenserTicker = true;
	private boolean debugMode = false;
	private boolean modOverride = false;

	public CreativePlayerListener()
	{
		this.allAccess = getConfig().getBoolean("allAccess");
		this.closedtoAll = getConfig().getBoolean("closedtoAll");
		this.weatherAllow = getConfig().getBoolean("weatherAllow");
		this.witherAllow = getConfig().getBoolean("witherAllow");
		this.respawnSpawnCords = getConfig().getBoolean("respawnSpawnCords");
		this.dispencerNerf = getConfig().getBoolean("dispencerNerf");
		this.messagePlayer = getConfig().getBoolean("messagePlayer");

		this.returnWorld = getConfig().getString("returnWorld");
		String worldsString = getConfig().getString("creativeWorlds");
		if (worldsString != null) {
			this.creativeWorlds = worldsString.split(",");
		}
		String BannedIDString = getConfig().getString("bannedIDs");
		if (BannedIDString != null) {
			this.bannedIDs = BannedIDString.split(",");
		}
	}

	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(this, this);
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Creative] by Travi5 Sucessfully Enabled]");
	}

	public void reloadAll()
	{
		reloadConfig();
		this.returnWorld = getConfig().getString("returnWorld");
		String worldsString = getConfig().getString("creativeWorlds");
		if (worldsString != null) {
			this.creativeWorlds = worldsString.split(",");
		}
		String BannedIDString = getConfig().getString("bannedIDs");
		if (BannedIDString != null) {
			this.bannedIDs = BannedIDString.split(",");
		}
	}

	@Overridden
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player p = (Player)sender;
		if ((label.equalsIgnoreCase("Creative")) || (label.equalsIgnoreCase("C")))
		{
			if ((args.length == 0) || (args[0].equalsIgnoreCase("help"))) {
				UserHelp(p);
			}
			if (args.length > 0)
			{
				if (checkWorlds(p.getWorld().getName()))
				{
					if ((this.debugMode) && (p.hasPermission("Creative.Handle.mod"))) {
						p.sendMessage("Command ran while in creative");
					}
					if ((args[0].equalsIgnoreCase("survival")) || (args[0].equalsIgnoreCase("gm0")))
					{
						p.setGameMode(GameMode.SURVIVAL);
						p.sendMessage(ChatColor.GOLD + "Your gamemode has been changed");
					}
					if ((args[0].equalsIgnoreCase("creative")) || (args[0].equalsIgnoreCase("gm1")))
					{
						p.setGameMode(GameMode.CREATIVE);
						p.sendMessage(ChatColor.GOLD + "Your gamemode has been changed");
					}
					if ((args[0].equalsIgnoreCase("spectator")) || (args[0].equalsIgnoreCase("gm3")) || (args[0].equalsIgnoreCase("adventure")) || (args[0].equalsIgnoreCase("gm4"))) {
						p.sendMessage(ChatColor.RED + "This gamemode is not allowed");
					}
					if ((args[0].equalsIgnoreCase("clear")) || (args[0].equalsIgnoreCase("cl")))
					{
						p.getInventory().clear();
						p.getInventory().setChestplate(new ItemStack(Material.AIR));
						p.getInventory().setLeggings(new ItemStack(Material.AIR));
						p.getInventory().setBoots(new ItemStack(Material.AIR));
						p.sendMessage(ChatColor.GREEN + "Your inventory has been cleared!");
					}
					if ((args[0].equalsIgnoreCase("up")) || (args[0].equalsIgnoreCase("up1")))
					{
						Block block = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
						if (block.getType() == Material.AIR) {
							//Block is air
							block.setType(Material.DIAMOND_BLOCK);							
						}
						else {
							p.sendMessage(ChatColor.RED + "The block below is not air");
						}
					}
					if (args[0].equalsIgnoreCase("Hat")) {
						if (p.getItemInHand().getType() != Material.AIR)
						{
							ItemStack itemHand = p.getItemInHand();
							ItemStack helmet = p.getInventory().getHelmet();
							p.getInventory().setHelmet(itemHand);
							p.getInventory().setItemInHand(helmet);
							p.sendMessage(ChatColor.GOLD + "Your hat has been updated!");
						}
						else if ((p.getItemInHand().getType() == Material.AIR) && (p.getInventory().getHelmet() != null))
						{
							ItemStack helmet = p.getInventory().getHelmet();
							p.getInventory().setItemInHand(helmet);
							p.getInventory().setHelmet(null);
							p.sendMessage(ChatColor.GOLD + "Your hat has been cleared!");
						}
						else
						{
							p.sendMessage("Please have an item in your hand that you wish to wear");
						}
					}
				}
				if (p.hasPermission("Creative.Handle.mod"))
				{
					if (args[0].equalsIgnoreCase("Setspawn")) {
						setSpawn(p);
					}
					if (args[0].equalsIgnoreCase("Return")) {
						setReturn(p);
					}
					if (args[0].equalsIgnoreCase("reload"))
					{
						reloadAll();
						p.sendMessage(ChatColor.GOLD + "Config reloaded");
					}
					if (args[0].equalsIgnoreCase("open"))
					{
						getConfig().set("closedtoAll", Boolean.valueOf(false));
						this.closedtoAll = false;
						saveConfig();
						reloadConfig();
						p.sendMessage(ChatColor.GREEN + "Creative is now OPEN");
						autoSignUpdater();
					}
					if (args[0].equalsIgnoreCase("close"))
					{
						getConfig().set("closedtoAll", Boolean.valueOf(true));
						this.closedtoAll = true;
						saveConfig();
						reloadConfig();

						teleportAllPlayers();
						p.sendMessage(ChatColor.RED + "Creative is now CLOSED");
						autoSignUpdater();
					}
					if (args[0].equalsIgnoreCase("allAccess")) {
						if (!getConfig().getBoolean("allAccess"))
						{
							getConfig().set("allAccess", Boolean.valueOf(true));
							this.allAccess = true;
							saveConfig();
							reloadConfig();
							p.sendMessage(ChatColor.GREEN + "Creative is now OPEN to all");
							autoSignUpdater();
						}
						else
						{
							getConfig().set("allAccess", Boolean.valueOf(false));
							this.allAccess = false;
							saveConfig();
							reloadConfig();
							p.sendMessage(ChatColor.GREEN + "Creative is now" + ChatColor.RED + " CLOSED " + ChatColor.GREEN + "to non members");
							teleportAllPlayers();
							autoSignUpdater();
						}
					}
					if (args[0].equalsIgnoreCase("weather")) {
						if (this.weatherAllow)
						{
							this.weatherAllow = false;
							getConfig().set("weatherAllow", Boolean.valueOf(false));
							saveConfig();
							reloadConfig();
							p.sendMessage("weather disabled");
						}
						else
						{
							this.weatherAllow = true;
							getConfig().set("weatherAllow", Boolean.valueOf(false));
							saveConfig();
							reloadConfig();
							p.sendMessage("weather enabled");
						}
					}
					if (args[0].equalsIgnoreCase("wither")) {
						if (this.witherAllow)
						{
							this.witherAllow = false;
							getConfig().set("witherAllow", Boolean.valueOf(false));
							saveConfig();
							reloadConfig();
							p.sendMessage("Wither disabled");
						}
						else
						{
							this.witherAllow = true;
							getConfig().set("witherAllow", Boolean.valueOf(false));
							saveConfig();
							reloadConfig();
							p.sendMessage("Wither ALLOWED");
						}
					}
					if (args[0].equalsIgnoreCase("dispenserNerf")) {
						if (this.dispencerNerf)
						{
							this.dispencerNerf = false;
							getConfig().set("dispencerNerf", Boolean.valueOf(false));
							saveConfig();
							reloadConfig();
							p.sendMessage("Dispencers no longer nerfed");
						}
						else
						{
							this.dispencerNerf = true;
							getConfig().set("dispencerNerf", Boolean.valueOf(true));
							saveConfig();
							reloadConfig();
							p.sendMessage("Dispencers nerfed!");
						}
					}
					if (args[0].equalsIgnoreCase("messagePlayer")) {
						if (this.messagePlayer)
						{
							this.messagePlayer = false;
							getConfig().set("messagePlayer", Boolean.valueOf(false));
							saveConfig();
							reloadConfig();
							p.sendMessage("messagePlayer disabled");
						}
						else
						{
							this.messagePlayer = true;
							getConfig().set("messagePlayer", Boolean.valueOf(true));
							saveConfig();
							reloadConfig();
							p.sendMessage("messagePlayer enabled");
						}
					}
					if (args[0].equalsIgnoreCase("debug")) {
						if (this.debugMode)
						{
							this.debugMode = false;
							p.sendMessage("Debug Mode disabled");
						}
						else
						{
							this.debugMode = true;
							p.sendMessage("Debug mode enabled");
						}
					}
					if (args[0].equalsIgnoreCase("Mod")) {
						if (this.modOverride)
						{
							this.modOverride = false;
							p.sendMessage("Mod override disabled");
						}
						else
						{
							this.modOverride = true;
							p.sendMessage("Mod override enabled");
						}
					}
					if (args[0].equalsIgnoreCase("add")) {
						if (getConfig().get("whitelisted." + args[1]) == null)
						{
							getConfig().set("whitelisted." + args[1] + ".Added by", p.getName());
							saveConfig();
							reloadConfig();
							p.sendMessage(ChatColor.GREEN + "Added player " + args[1]);
							Player target = Bukkit.getPlayer(args[1]);
							if (target != null) {
								target.sendMessage(ChatColor.GOLD + "You are now a member of Creative");
							}
						}
						else if (getConfig().get("whitelisted." + args[1]) != null)
						{
							p.sendMessage(ChatColor.RED + "Player " + args[1] + " was already added to players");
						}
					}
					if ((args[0].equalsIgnoreCase("remove")) || (args[0].equalsIgnoreCase("rem"))) {
						if (getConfig().getString("whitelisted." + args[1]) != null)
						{
							getConfig().set("whitelisted." + args[1], null);
							saveConfig();
							reloadConfig();

							p.sendMessage(ChatColor.GREEN + "Player " + args[1] + " Removed from config");

							Player target = Bukkit.getPlayer(args[1]);
							if (target != null) {
								if (!checkAccess(target)) {
									returnPlayer(target);
								}
							}
						}
						else if (getConfig().getString("whitelisted." + args[1]) == null)
						{
							p.sendMessage(ChatColor.RED + "Player " + args[1] + " not found in config");
						}
					}
				}
			}
		}
		return true;
	}

	@EventHandler
	public void signPlace(SignChangeEvent e)
	{
		if (e.getPlayer().hasPermission("Creative.Handle.mod"))
		{

			if (e.getLine(1).contains("[CreativeTP]")) {
				e.setLine(0, "");
				e.setLine(1, ChatColor.DARK_BLUE + "[Teleport]");
				e.setLine(2, ChatColor.GREEN + "Creative");

				getConfig().set("Teleport-Sign..World", e.getBlock().getLocation().getWorld().getName());
				getConfig().set("Teleport-Sign..X", Integer.valueOf(e.getBlock().getLocation().getBlockX()));
				getConfig().set("Teleport-Sign..Y", Integer.valueOf(e.getBlock().getLocation().getBlockY()));
				getConfig().set("Teleport-Sign..Z", Integer.valueOf(e.getBlock().getLocation().getBlockZ()));
				saveConfig();
				reloadConfig();
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
				{
					public void run()
					{
						CreativePlayerListener.this.autoSignUpdater();
					}
				}, 20L);
			}
			if (e.getLine(1).equalsIgnoreCase("[Creative]") && checkWorlds(e.getPlayer().getWorld().getName())) {

				e.setLine(1,ChatColor.GREEN + "Welcome");
				e.setLine(2,"Press4 commands");


			}
		}
		//Gamemode signs
		if (checkWorlds(e.getPlayer().getWorld().getName())){


			if (e.getLine(1).equalsIgnoreCase("gamemode")) { //if world = creative
				if (e.getLine(2).equalsIgnoreCase(""))//line is empty make it gamemode 1
				{
					e.setLine(0, "");
					e.setLine(1, ChatColor.GREEN + "Gamemode");
					e.setLine(2, "Creative");
				}

				if (e.getLine(2) != null) {
					if (e.getLine(2).equalsIgnoreCase("1") ||e.getLine(2).equalsIgnoreCase("gm1") || e.getLine(2).equalsIgnoreCase("creative") || e.getLine(2).equalsIgnoreCase("c"))//line is empty make it gamemode 1
					{
						e.setLine(0, "");
						e.setLine(1, ChatColor.GREEN + "Gamemode");
						e.setLine(2, "Creative");
					}
					if (e.getLine(2).equalsIgnoreCase("0") ||e.getLine(2).equalsIgnoreCase("gm0") || e.getLine(2).equalsIgnoreCase("survival") || e.getLine(2).equalsIgnoreCase("s"))//line is empty make it gamemode 1
					{
						e.setLine(0, "");
						e.setLine(1, ChatColor.GREEN + "Gamemode");
						e.setLine(2, "Survial");
					}
				}
			}
		}
	}

	@EventHandler
	public void signInteract(PlayerInteractEvent e)
	{
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			Block block = e.getClickedBlock();
			Player p = e.getPlayer();

			if ((block.getType() == Material.SIGN_POST) || (block.getType() == Material.WALL_SIGN) || (block.getType() == Material.SIGN))
			{
				Sign sign = (Sign)block.getState();
				if ((sign.getLine(1).contains(ChatColor.DARK_BLUE + "[Teleport]")) && (sign.getLine(2).contains(ChatColor.GREEN + "Creative")))
				{
					if ((this.debugMode) && (p.hasPermission("Creative.Handle.mod"))) {
						p.sendMessage("Sign found");
					}
					if (checkAccess(p)) {
						teleportPlayerCreative(p);
					}
				}
				//Gamemode signs
				if (checkWorlds(e.getPlayer().getWorld().getName())) {
					if (sign.getLine(1).contains(ChatColor.GREEN + "Gamemode")) { //gamemode change signs
						if (sign.getLine(2).equalsIgnoreCase("Survial")) {
							p.setGameMode(GameMode.SURVIVAL);
						}
						if (sign.getLine(2).equalsIgnoreCase("Creative")) {
							p.setGameMode(GameMode.CREATIVE);
						}
					}
					if (sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Welcome") && sign.getLine(2).equalsIgnoreCase("Press4 commands")) {
						UserHelp(e.getPlayer());

					}

				}//if in world
			}//if sign
		}
	}

	public void autoSignUpdater()
	{
		World w = Bukkit.getServer().getWorld(getConfig().getString("Teleport-Sign..World"));
		int x = getConfig().getInt("Teleport-Sign..X");
		int y = getConfig().getInt("Teleport-Sign..Y");
		int z = getConfig().getInt("Teleport-Sign..Z");
		Block bloc = w.getBlockAt(x, y, z);
		if ((bloc.getType() == Material.SIGN_POST) || (bloc.getType() == Material.WALL_SIGN) || (bloc.getType() == Material.SIGN))
		{
			Sign sign = (Sign)bloc.getState();

			String status = "";
			if ((this.allAccess) && (!this.closedtoAll)) {
				status = ChatColor.GREEN + "OPEN";
			}
			if ((!this.allAccess) && (!this.closedtoAll)) {
				status = ChatColor.RED + "MEMBERS ONLY";
			}
			if (this.closedtoAll) {
				status = ChatColor.RED + "CLOSED";
			}
			sign.setLine(3, status);
			sign.update(true);
			if (this.debugMode) {
				Bukkit.broadcast("Sign Status changed to " + status, "Creative.Handle.mod");
			}
		}
	}

	public void UserHelp(Player p)
	{
		p.sendMessage(ChatColor.GREEN + "---- CREATIVE v1.2 by Travi5 ----");
		p.sendMessage(ChatColor.GOLD + "Welcome to Creative!");
		p.sendMessage(ChatColor.GOLD + "Here is a list your available commands!");
		p.sendMessage(ChatColor.GOLD + "Use /Creative " + ChatColor.GRAY + "or" + ChatColor.GOLD + " /C " + ChatColor.WHITE + "Followed by the following");
		p.sendMessage(ChatColor.WHITE + "Survival " + ChatColor.GRAY + "or " + ChatColor.WHITE + " gm0" + ChatColor.GOLD + " To change your GAMEMODE");
		p.sendMessage(ChatColor.WHITE + "Clear " + ChatColor.GOLD + "Clear your entire inventory");
		p.sendMessage(ChatColor.WHITE + "Hat " + ChatColor.GOLD + "Use item in hand as a cool hat!");
		p.sendMessage(ChatColor.WHITE + "up " + ChatColor.GOLD + "Spawns a diamond block in the air");
		p.sendMessage(ChatColor.RED + "---Lag machines and hacking are NOT allowed---");
		if (p.hasPermission("Creative.Handle.mod"))
		{
			p.sendMessage("");
			p.sendMessage(ChatColor.GOLD + "---- MOD COMMANDS ----");
			p.sendMessage(ChatColor.WHITE + "Add " + ChatColor.GRAY + "or " + ChatColor.WHITE + "Remove " + ChatColor.GOLD + "Players from the whitelist");
			p.sendMessage(ChatColor.WHITE + "Open " + ChatColor.GRAY + "or " + ChatColor.WHITE + "Close " + ChatColor.GOLD + "The world");
			p.sendMessage(ChatColor.WHITE + "AllAccess " + ChatColor.GOLD + "All players allowed to enter reguardless of whitelist");
			p.sendMessage(ChatColor.WHITE + "Setspawn " + ChatColor.GOLD + "Set world spawn (used by the sign)");
			p.sendMessage(ChatColor.WHITE + "Return " + ChatColor.GOLD + "Set return world spawn");
			p.sendMessage(ChatColor.WHITE + "Weather " + ChatColor.GOLD + "Toggle is weather lock");
			p.sendMessage(ChatColor.WHITE + "WitherAllow " + ChatColor.GOLD + "Allow summon of wither");
			p.sendMessage(ChatColor.WHITE + "DispenserNerf " + ChatColor.GOLD + "Nerf items from dispenser FIREWORKS & EGG");
			p.sendMessage(ChatColor.WHITE + "MessagePlayer " + ChatColor.GOLD + "Turn on/off messages when using banned items");
			p.sendMessage(ChatColor.WHITE + "Reload " + ChatColor.GOLD + "Reload the config files");
			p.sendMessage(ChatColor.WHITE + "Debug " + ChatColor.GOLD + "Debuging can be fun!");
			p.sendMessage(ChatColor.WHITE + "Mod " + ChatColor.GOLD + "Moderator Override");
		}
	}

	public void setSpawn(Player p)
	{
		reloadConfig();
		getConfig().set("Spawn-Coordinates..World", p.getWorld().getName());
		getConfig().set("Spawn-Coordinates..X", Integer.valueOf(p.getLocation().getBlockX()));
		getConfig().set("Spawn-Coordinates..Y", Integer.valueOf(p.getLocation().getBlockY()));
		getConfig().set("Spawn-Coordinates..Z", Integer.valueOf(p.getLocation().getBlockZ()));
		getConfig().set("Spawn-Coordinates..Pitch", Float.valueOf(p.getLocation().getPitch()));
		getConfig().set("Spawn-Coordinates..Yaw", Float.valueOf(p.getLocation().getYaw()));
		saveConfig();
		reloadConfig();
		p.sendMessage(ChatColor.GOLD + "The Spawn has been set");
	}

	public void setReturn(Player p)
	{
		getConfig().set("returnWorld", p.getWorld().getName());
		p.sendMessage("Return world has been set to: " + p.getWorld().getName());
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e)
	{
		if (this.debugMode) {
			Bukkit.broadcast("weather request", "Creative.Handle.mod");
		}
		if ((checkWorlds(e.getWorld().getName())) && (!this.weatherAllow))
		{
			e.setCancelled(true);
			if (this.debugMode) {
				Bukkit.broadcast("weather stopped " + e.getWorld().getName(), "Creative.Handle.mod");
			}
		}
		else if (this.debugMode)
		{
			Bukkit.broadcast("weather allowed " + e.getWorld().getName(), "Creative.Handle.mod");
		}
	}

	public void teleportPlayerCreative(Player p)
	{
		if (getConfig().get("Spawn-Coordinates") != null)
		{
			World w = Bukkit.getServer().getWorld(getConfig().getString("Spawn-Coordinates..World"));
			if (w != null)
			{
				double x = getConfig().getDouble("Spawn-Coordinates..X");
				double y = getConfig().getDouble("Spawn-Coordinates..Y");
				double z = getConfig().getDouble("Spawn-Coordinates..Z");
				float pitch = getConfig().getInt("Spawn-Coordinates..Pitch");
				float yaw = getConfig().getInt("Spawn-Coordinates..Yaw");
				Location Spawn = new Location(w, x, y, z, yaw, pitch).add(new Vector(0.5D, 0.0D, 0.5D));

				p.teleport(Spawn);
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERPEARL_THROW, 1.0F, 1.0F);
				if (this.debugMode) {
					Bukkit.broadcast("player has teleported", "Creative.Handle.mod");
				}
			}
			else
			{
				p.sendMessage(ChatColor.RED + "Oh no. Looks like the world is missing");
				if (this.debugMode) {
					Bukkit.broadcast("The creative world is missing", "Creative.Handle.mod");
				}
			}
		}
		else
		{
			p.sendMessage(ChatColor.RED + "No spawn location found. Please contact server admin");
			if (this.debugMode) {
				Bukkit.broadcast("The creative spawn is missing", "Creative.Handle.mod");
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		if (checkWorlds(p.getWorld().getName()))
		{
			if (this.debugMode) {
				Bukkit.broadcast("Player joined creative", "Creative.Handle.mod");
			}
			if (!checkAccess(p)) {
				returnPlayer(p);
			}
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e)
	{
		final Player p = e.getPlayer();
		if (checkWorlds(p.getWorld().getName()))
		{
			if (this.debugMode) {
				Bukkit.broadcast("Player joined creative", "Creative.Handle.mod");
			}
			if (checkAccess(p))
			{
				if (this.respawnSpawnCords) {
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
					{
						public void run()
						{
							CreativePlayerListener.this.teleportPlayerCreative(p);
						}
					}, 1L);
				}
			}
			else {
				returnPlayer(p);
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void onInteractEvent(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		ItemStack item = p.getItemInHand();

		int id = item.getType().getId();
		if (checkWorlds(p.getWorld().getName()))
		{
			if ((this.debugMode) && (p.hasPermission("Creative.Handle.mod"))) {
				p.sendMessage("item Interact in creative: ID " + id);
			}
			if (checkBannedID(p, id))
			{
				e.getPlayer().setItemInHand(new ItemStack(Material.AIR, 1));
				e.setCancelled(true);
				bannedMessage(p, id);
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGH)
	private void onClick(InventoryClickEvent e)
	{
		Player p = (Player)e.getWhoClicked();
		ItemStack item = e.getCurrentItem();
		if (item == null) {
			return;
		}
		int id = item.getType().getId();
		if (checkWorlds(p.getWorld().getName()))
		{
			if ((this.debugMode) && (p.hasPermission("Creative.Handle.mod"))) {
				p.sendMessage("item clicked in creative: ID " + id);
			}
			if (checkBannedID(p, id))
			{
				e.setCurrentItem(new ItemStack(Material.AIR, 1));
				e.setCancelled(true);
				bannedMessage(p, id);
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGH)
	private void onPickup(PlayerPickupItemEvent e)
	{
		Player p = e.getPlayer();
		if (checkWorlds(p.getWorld().getName()))
		{
			int id = e.getItem().getItemStack().getType().getId();
			if ((this.debugMode) && (p.hasPermission("Creative.Handle.mod"))) {
				p.sendMessage("item picked up in creative: ID " + id);
			}
			if (checkBannedID(p, id))
			{
				e.setCancelled(true);
				bannedMessage(p, id);
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		Player p = e.getPlayer();
		if (checkWorlds(p.getWorld().getName()))
		{
			if (checkWorlds(p.getWorld().getName()))
			{
				int id = e.getBlock().getType().getId();
				if ((this.debugMode) && (p.hasPermission("Creative.Handle.mod"))) {
					p.sendMessage("item picked up in creative: ID " + id);
				}
				if (e.getBlock().getLocation().getBlockY() == 0)
				{
					e.setCancelled(true);
					p.sendMessage("The void is too cold. you might freeze to death");
				}
				if (checkBannedID(p, id))
				{
					e.setCancelled(true);
					bannedMessage(p, id);
				}
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e)
	{
		Player p = e.getPlayer();
		if (checkWorlds(p.getWorld().getName()))
		{
			int id = e.getBlock().getType().getId();
			if ((this.debugMode) && (p.hasPermission("Creative.Handle.mod"))) {
				p.sendMessage("item picked up in creative: ID " + id);
			}
			if (checkBannedID(p, id))
			{
				e.setCancelled(true);
				bannedMessage(p, id);
			}
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e)
	{
		Player p = e.getPlayer();
		if (checkWorlds(p.getWorld().getName()))
		{
			int id = e.getItemDrop().getItemStack().getTypeId();
			if ((this.debugMode) && (p.hasPermission("Creative.Handle.mod"))) {
				p.sendMessage("item dropped in creative: ID " + id);
			}
			if (checkBannedID(p, id))
			{
				e.setCancelled(true);
				bannedMessage(p, id);
			}
		}
	}

	@EventHandler
	public void WitherSpawn(CreatureSpawnEvent e)
	{
		if ((e.getEntityType() != null) && (e.getEntityType() == EntityType.WITHER) && 
				(checkWorlds(e.getEntity().getWorld().getName())) && (!this.witherAllow))
		{
			e.setCancelled(true);
			if (this.debugMode) {
				Bukkit.broadcast("someone tried to summon the wither", "Creative.Handle.mod");
			}
			for (Player p : Bukkit.getOnlinePlayers())
			{
				double x = e.getLocation().getX();
				double y = e.getLocation().getY();
				double z = e.getLocation().getZ();
				World w = e.getLocation().getWorld();
				Location EntityLoc = new Location(w, x, y, z);
				if (p.getLocation().distance(EntityLoc) < 5.0D) {
					p.sendMessage(ChatColor.RED + "You are unable to summon this");
				}
			}
		}
	}

	@EventHandler
	public void dispenserSpam(BlockDispenseEvent e)
	{
		Material mat = e.getItem().getType();
		World w = e.getBlock().getWorld();
		if ((checkWorlds(w.getName())) && (
				(mat == Material.FIREWORK) || (mat == Material.EGG)))
		{
			Location loc = e.getBlock().getLocation();
			if (this.dispencerNerf) {
				if (this.dispenserTicker)
				{
					dispenceTimer();
					if (this.debugMode)
					{
						double x = loc.getX();
						double y = loc.getY();
						double z = loc.getZ();
						Bukkit.broadcast("player spamming " + mat + " at " + x + " " + y + " " + z + " ", "Creative.Handle.mod");
					}
				}
				else
				{
					e.setCancelled(true);
				}
			}
		}
	}

	public void dispenceTimer()
	{
		this.dispenserTicker = false;

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
		{
			public void run()
			{
				CreativePlayerListener.this.dispenserTicker = true;
			}
		}, 20L);
	}

	public void teleportAllPlayers()
	{
		for (Player p : Bukkit.getOnlinePlayers() ) {
			if (checkWorlds(p.getWorld().getName())) {
				if (!checkAccess(p)) {
					returnPlayer(p);
				}
			}
		}
	}

	public void returnPlayer(final Player p)
	{
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
		{
			public void run()
			{
				if (CreativePlayerListener.this.returnWorld != null)
				{
					p.teleport(CreativePlayerListener.this.getServer().getWorld(CreativePlayerListener.this.getConfig().getString("returnWorld")).getSpawnLocation());
					if (CreativePlayerListener.this.debugMode) {
						Bukkit.broadcast("player teleported out of creative", "Creative.Handle.mod");
					}
				}
				else
				{
					p.sendMessage("No return world has been found. Please tell an admin");
					if (CreativePlayerListener.this.debugMode) {
						Bukkit.broadcast("No return world has been defined", "Creative.Handle.mod");
					}
				}
			}
		}, 1L);
	}

	public boolean checkAccess(Player p)
	{
		boolean access = false;
		if ((this.allAccess) && (!this.closedtoAll)) {
			access = true;
		}
		if ((!this.allAccess) && (!this.closedtoAll)) {
			if (getConfig().getString("whitelisted." + p.getName()) != null) {
				access = true;
			} else {
				p.sendMessage(ChatColor.RED + "Sorry, members only");
			}
		}
		if (this.closedtoAll)
		{
			access = false;
			p.sendMessage(ChatColor.RED + "Sorry but creative is closed");
		}
		if ((this.modOverride) && (p.hasPermission("Creative.Handle.mod"))) {
			access = true;
		}
		return access;
	}

	public boolean checkBannedID(Player p, int id)
	{
		boolean b = false;
		String[] arrayOfString;
		int j = (arrayOfString = this.bannedIDs).length;
		for (int i = 0; i < j; i++)
		{
			String IDChecking = arrayOfString[i];
			if (IDChecking.equals(Integer.toString(id))) {
				if ((this.modOverride) && (p.hasPermission("Creative.Handle.mod")))
				{
					b = false;
					p.sendMessage("MOD");
				}
				else
				{
					b = true;
				}
			}
		}
		return b;
	}

	public void bannedMessage(Player p, int item)
	{
		if (this.messagePlayer) {
			p.sendMessage(ChatColor.RED + "ID: " + item + " Is not allowed");
		}
	}

	public boolean checkWorlds(String w)
	{
		boolean b = false;
		String[] arrayOfString;
		int j = (arrayOfString = this.creativeWorlds).length;
		for (int i = 0; i < j; i++)
		{
			String world = arrayOfString[i];
			if (world.equalsIgnoreCase(w))
			{
				b = true;

				break;
			}
			b = false;
		}
		return b;
	}
}
