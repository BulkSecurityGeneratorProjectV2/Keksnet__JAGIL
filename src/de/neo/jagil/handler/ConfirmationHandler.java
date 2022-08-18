package de.neo.jagil.handler;

import de.neo.jagil.gui.prebuild.ConfirmGUI;
import org.bukkit.OfflinePlayer;

public interface ConfirmationHandler {

	/**
	 * Called when the user clicks yes in a {@link ConfirmGUI}.
	 * @param p clicking user
	 */
	public void handleYes(OfflinePlayer p);

	/**
	 * Called when the user clicks no in a {@link ConfirmGUI}.
	 * @param p clicking user
	 */
	public void handleNo(OfflinePlayer p);
}
