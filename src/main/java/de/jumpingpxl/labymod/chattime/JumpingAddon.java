package de.jumpingpxl.labymod.chattime;

import de.jumpingpxl.labymod.chattime.listener.MessageModifyChatListener;
import de.jumpingpxl.labymod.chattime.util.Settings;
import lombok.Getter;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;

import java.util.List;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 21.12.2018
 * @project LabyMod-Addon: ChatTime-1.8.9
 */

@Getter
public class JumpingAddon extends LabyModAddon {

	private Settings settings;
	private String version = "1.0";

	@Override
	public void onEnable() {
		settings = new Settings(this);
		getApi().getEventManager().register(new MessageModifyChatListener(this));
	}

	@Override
	public void onDisable() {

	}

	@Override
	public void loadConfig() {
		settings.loadConfig();
	}

	@Override
	protected void fillSettings(List<SettingsElement> settingsElements) {
		settings.fillSettings(settingsElements);
	}
}
