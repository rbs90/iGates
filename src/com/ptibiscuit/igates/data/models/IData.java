package com.ptibiscuit.igates.data.models;

import com.ptibiscuit.igates.data.FillType;
import com.ptibiscuit.igates.data.Portal;
import com.ptibiscuit.igates.data.Volume;
import java.util.ArrayList;
import org.bukkit.Location;

public interface IData {
	public Portal createPortal(String tag, Location to, ArrayList<Volume> froms, FillType fillTypes);
	public void loadPortals();
	public ArrayList<Portal> getPortals();
	public void deletePortal(Portal portal);
	public void setActive(Portal portal, boolean active);
	public void setSpawn(Portal portal, Location l);
	public void setPrice(Portal p, int price);
    public void setCommand(Portal p, String command);
	public void setFillType(Portal portal, FillType filltype);
	public void setFromsAreas(Portal portal, ArrayList<Volume> froms);
}
