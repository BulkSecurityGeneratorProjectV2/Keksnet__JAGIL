package de.neo.jagil.handler;

import org.bukkit.OfflinePlayer;

public interface ConfirmationHandler {

	/**
	 * Called when the user clicks yes in a {@link de.neo.jagil.gui.ConfirmGUI}.
	 * @param p clicking user
	 */
	public void handleYes(OfflinePlayer p);

	/**
	 * Called when the user clicks no in a {@link de.neo.jagil.gui.ConfirmGUI}.
	 * @param p clicking user
	 */
	public void handleNo(OfflinePlayer p);
}
