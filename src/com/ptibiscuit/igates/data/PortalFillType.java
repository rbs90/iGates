/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptibiscuit.igates.data;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 *
 * @author ANNA
 */
public class PortalFillType extends FillType {

	public PortalFillType(String name) {
		super(name, new int[]{90});
	}

	@Override
	public void fillBlocks(ArrayList<Location> blocks) {
		ArrayList<Location> changed = new ArrayList<Location>();
		for (Location l : blocks)
		{
			if (l.getBlock().getType() == Material.AIR)
			{
				l.getBlock().setType(Material.IRON_BLOCK);
				changed.add(l);
			}
		}
		
		for (Location b : changed)
		{
			b.getBlock().setType(Material.PORTAL);
		}
	}
	
}
