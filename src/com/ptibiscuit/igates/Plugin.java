/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptibiscuit.igates;

import com.ptibiscuit.framework.JavaPluginEnhancer;
import com.ptibiscuit.framework.PermissionHelper;
import com.ptibiscuit.igates.data.FillType;
import com.ptibiscuit.igates.data.Portal;
import com.ptibiscuit.igates.data.Volume;
import com.ptibiscuit.igates.data.models.IData;
import com.ptibiscuit.igates.data.models.YamlData;
import com.ptibiscuit.igates.listeners.PlayerListener;
import com.ptibiscuit.igates.listeners.SpreadBlockListener;
import com.ptibiscuit.igates.listeners.VolumeSelectionManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.PluginManager;

public class Plugin extends JavaPluginEnhancer implements Listener {
	public static Plugin instance;
	
	private IData data;
	private VolumeSelectionManager vsm = new VolumeSelectionManager();
	private SpreadBlockListener sbl = new SpreadBlockListener();
	private PlayerListener pm = new PlayerListener();
	
	
	@Override
	public void onConfigurationDefault(FileConfiguration c) {
		c.set("portals", new HashMap<String, Object>());
		c.set("config.retain_liquid", false);
		c.set("config.display_message_selection", true);
	}

	@Override
	public void onEnable() {
		this.setup(ChatColor.AQUA + "[iGates]", "igates", true);
		this.instance = this;
		this.myLog.startFrame();
		this.myLog.addInFrame("iGates by Ptibiscuit");
		this.myLog.addCompleteLineInFrame();
		
		this.data = new YamlData();
		// On fait attention à Multiverse, au cas où.
		if (!this.multiVerseExists())
			data.loadPortals();
		this.myLog.addInFrame(data.getPortals().size() + " portals loaded !");
		
		
		PluginManager pgm = this.getServer().getPluginManager();
		pgm.registerEvents(vsm, this);
		pgm.registerEvents(pm, this);
		pgm.registerEvents(sbl, this);
		pgm.registerEvents(this, this);
		
		this.myLog.displayFrame(false);
	}
	
	@Override
	public void onLangDefault(Properties p) {
		p.setProperty("cant_do", "You're not able to do that.");
		p.setProperty("need_be_player", "You need to be a player to do that.");
		p.setProperty("more_args", "Bad using of the command (Need argument ?).");
		p.setProperty("tag_taken", "This portal's tag ios already taken.");
		p.setProperty("ft_dont_exist", "This FillType doesn't exist (water, portal, lava, web)");
		p.setProperty("tag_dont_exist", "This portal's tag doesn't exist.");
		p.setProperty("set_active", "This portal has been turned {ACTIVE}.");
		p.setProperty("set_filltype", "This portal's filltype is now {FILLTYPE}.");
		p.setProperty("portal_deleted", "Portal deleted. :'(");
		p.setProperty("first_point_set", "The first point of your selection is set !");
		p.setProperty("second_point_set", "The second point of your selection is set !");
		p.setProperty("froms_added", "\"From area\" added to the portal !");
		p.setProperty("need_volume", "Before do that, you need to select an area with the woodaxe, like with WorldEdit. =)");
		p.setProperty("top_list", "List of all portals:");
		p.setProperty("elem_list", "{ACTIVE} " + ChatColor.GOLD + "{TAG}" + ChatColor.WHITE + ": {CNT_FROMS} \"Froms\" areas.");
		p.setProperty("portail_created", "Portal \"{TAG}\" created !");
		p.setProperty("weird_arg", "You used weird arg, only on, off, to and filltype are available.");
	}

	@Override
	public void onDisable() {
		
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try
		{
			if (!(sender instanceof Player))
			{
				this.sendPreMessage(sender, "need_be_player");
				return true;
			}
			Player p = (Player) sender;
			
			if (label.equalsIgnoreCase("igcreate"))
			{
				if (!this.permissionHandler.has(sender, "portal.edit.create", true))
				{
					this.sendPreMessage(sender, "cant_do");
					return true;
				}
				
				if (this.getPortal(args[0]) == null)
				{
					FillType ft = FillType.getFillType(args[1]);
					if (ft != null)
					{
						data.createPortal(args[0], p.getLocation(), new ArrayList<Volume>(), ft);
						this.sendMessage(sender, getSentence("portail_created").replace("{TAG}", args[0]));
					}
					else
					{
						this.sendPreMessage(sender, "ft_dont_exist");
					}
				}
				else
				{
					this.sendPreMessage(sender, "tag_taken");
				}
			}
			else if (label.equalsIgnoreCase("igset"))
			{
				if (!this.permissionHandler.has(sender, "portal.edit.set." + args[0], true))
				{
					this.sendPreMessage(sender, "cant_do");
					return true;
				}
				
				Portal portal = this.getPortal(args[1]);
				if (portal == null)
				{
					this.sendPreMessage(sender, "tag_dont_exist");
					return true;
				}
				
				if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off"))
				{
					boolean active = (args[0].equalsIgnoreCase("on")) ? true : false; 
					this.data.setActive(portal, active);
					this.sendMessage(sender, this.getSentence("set_active").replace("{ACTIVE}", args[0]));
				}
				else if (args[0].equalsIgnoreCase("to"))
				{
					this.data.setSpawn(portal, p.getLocation());
				}
				else if (args[0].equalsIgnoreCase("filltype"))
				{
					FillType ft = FillType.getFillType(args[2]);
					if (ft != null)
					{
						this.data.setFillType(portal, ft);
						this.sendMessage(sender, this.getSentence("set_filltype").replace("{FILLTYPE}", args[2]));
					}
					else
					{
						this.sendPreMessage(sender, "ft_dont_exist");
					}
				}
				else
				{
					this.sendPreMessage(sender, "weird_arg");
				}
			}
			else if (label.equalsIgnoreCase("igdelete"))
			{
				if (!this.permissionHandler.has(sender, "portal.edit.delete", true))
				{
					this.sendPreMessage(sender, "cant_do");
					return true;
				}
				
				Portal portal = this.getPortal(args[0]);
				if (portal == null)
				{
					this.sendPreMessage(sender, "tag_dont_exist");
					return true;
				}
				
				portal.beforeDelete();
				this.data.deletePortal(portal);
				this.sendPreMessage(sender, "portal_deleted");
			}
			else if (label.equalsIgnoreCase("iglist"))
			{
				if (!this.permissionHandler.has(sender, "portal.list", false))
				{
					this.sendPreMessage(sender, "cant_do");
					return true;
				}
				
				this.sendPreMessage(sender, "top_list");
				for (Portal portal : this.data.getPortals())
				{
					String enable = (portal.isActive()) ? ChatColor.GREEN + "[V]" : ChatColor.RED + "[X]" ;
					enable += ChatColor.WHITE;
					int cntFroms = portal.getFromPoints().size();
					this.sendMessage(sender, getSentence("elem_list").replace("{TAG}", portal.getTag()).replace("{ACTIVE}", enable).replace("{CNT_FROMS}", String.valueOf(cntFroms)));
				}
			}
			else if (label.equalsIgnoreCase("igaddfrom"))
			{
				if (!this.permissionHandler.has(sender, "portal.edit.addfrom", true))
				{
					this.sendPreMessage(sender, "cant_do");
					return true;
				}
				
				Portal portal = this.getPortal(args[0]);
				if (portal == null)
				{
					this.sendPreMessage(sender, "tag_dont_exist");
					return true;
				}
				Volume v = this.vsm.getSelection(p.getName());
				if (v != null)
				{
					portal.getFromPoints().add(v);
					this.data.setFromsAreas(portal, portal.getFromPoints());
					this.vsm.removeSelection(p.getName());
					this.sendPreMessage(sender, "froms_added");
				}
				else
				{
					this.sendPreMessage(sender, "need_volume");
				}
			}
			else if (label.equalsIgnoreCase("igclearfroms"))
			{
				if (!this.permissionHandler.has(sender, "portal.edit.clearfroms", true))
				{
					this.sendPreMessage(sender, "cant_do");
					return true;
				}
				
				Portal portal = this.getPortal(args[0]);
				if (portal == null)
				{
					this.sendPreMessage(sender, "tag_dont_exist");
					return true;
				}
			}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			this.sendPreMessage(sender, "more_args");
		}
		return true;
	}
	
	public Portal getPortalByPosition(Location l)
	{
		for (Portal p : this.data.getPortals())
		{
			if (p.isIn(l) && p.isActive())
				return p;
		}
		return null;
	}
	
	public Portal getPortal(String tag)
	{
		for (Portal p : this.data.getPortals())
		{
			if (p.getTag().equalsIgnoreCase(tag))
				return p;
		}
		return null;
	}

	public IData getData() {
		return data;
	}
	
	/*
	 * All the thing about the MultiVerse thing.
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginEnable(PluginEnableEvent e)
	{
		if (e.getPlugin().getName().equalsIgnoreCase("Multiverse-Core"))
			this.data.loadPortals();
	}
	
	public boolean multiVerseExists()
	{
		if (this.getServer().getPluginManager().getPlugin("Multiverse-Core") != null)
			return true;
		return false;
	}
}