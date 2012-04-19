/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptibiscuit.igates.listeners;

import com.ptibiscuit.igates.Plugin;
import com.ptibiscuit.igates.data.Portal;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;

/**
 *
 * @author ANNA
 */
public class SpreadBlockListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockFromTo(BlockFromToEvent e)
	{
		if (Plugin.instance.getConfig().getBoolean("config.retain_liquid"))
		{
			for (Portal p : Plugin.instance.getData().getPortals())
			{
				if (p.isIn(e.getBlock().getLocation()) && !p.isIn(e.getToBlock().getLocation()))
				{
					e.setCancelled(true);
					return;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPhysics(BlockPhysicsEvent e)
	{
		for (Portal p : Plugin.instance.getData().getPortals())
		{
			if (p.isIn(e.getBlock().getLocation()))
			{
				e.setCancelled(true);
				return;
			}
		}
	}
}
