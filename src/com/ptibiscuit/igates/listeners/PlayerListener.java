/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptibiscuit.igates.listeners;

import com.ptibiscuit.framework.PermissionHelper;
import com.ptibiscuit.igates.Plugin;
import com.ptibiscuit.igates.data.Portal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent e) {
		if (e.isCancelled()) { return; }
		Player p = e.getPlayer();
		if (!PermissionHelper.has(e.getPlayer(), "igates.portal.use", true))
		{
			Plugin.instance.sendPreMessage(e.getPlayer(), "cant_do");
			return;
		}
		Portal portal = Plugin.instance.getPortalByPosition(p.getLocation());
		if (portal != null)
		{
			// Il se trouve effectivement dans un portal !
			portal.teleportPlayer(p);
		}
	}
}
