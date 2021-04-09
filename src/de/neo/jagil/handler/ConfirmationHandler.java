package de.neo.jagil.handler;

import org.bukkit.OfflinePlayer;

public interface ConfirmationHandler {
	
	public void handleYes(OfflinePlayer p);
	
	public void handleNo(OfflinePlayer p);
}
