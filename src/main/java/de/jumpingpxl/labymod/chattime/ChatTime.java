package de.jumpingpxl.labymod.chattime;

import de.jumpingpxl.labymod.chattime.listener.MessageModifyChatListener;
import de.jumpingpxl.labymod.chattime.util.Settings;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;

import java.util.List;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 21.12.2018
 * @project LabyMod-Addon: ChatTime-1.8.9
 */

public class ChatTime extends LabyModAddon {

	private static final String VERSION = "4";

	private Settings settings;

	@Override
	public void onEnable() {
		settings = new Settings(this);

		getApi().getEventManager().register(new MessageModifyChatListener(settings));
	}

	@Override
	public void loadConfig() {
		settings.loadConfig();
	}

	@Override
	protected void fillSettings(List<SettingsElement> settingsElements) {
		settings.fillSettings(settingsElements);
	}

	public String getVersion() {
		return VERSION;
	}
}