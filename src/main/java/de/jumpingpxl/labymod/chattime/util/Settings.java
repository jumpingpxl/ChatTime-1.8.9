package de.jumpingpxl.labymod.chattime.util;

import com.google.gson.JsonObject;
import de.jumpingpxl.labymod.chattime.JumpingAddon;
import lombok.Getter;
import net.labymod.settings.elements.*;
import net.labymod.utils.Material;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 21.12.2018
 */

@Getter
public class Settings {

	private JumpingAddon jumpingAddon;
	private String chatTimeFormat = "HH:mm:ss";
	private String chatTimePrefix = "&7[&6%time%&7] &r";
	private boolean enabledChatTime = true;

	public Settings(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	private JsonObject getConfig() {
		return jumpingAddon.getConfig();
	}

	private void saveConfig() {
		jumpingAddon.saveConfig();
	}

	public void loadConfig() {
		chatTimeFormat = getConfig().has("format") ? getConfig().get("format").getAsString() : chatTimeFormat;
		chatTimePrefix = getConfig().has("prefix") ? getConfig().get("prefix").getAsString() : chatTimePrefix;
		enabledChatTime = !getConfig().has("enabled") || getConfig().get("enabled").getAsBoolean();
	}

	public void fillSettings(List<SettingsElement> list) {
		list.add(new HeaderElement("§eJumpingChatTime v" + jumpingAddon.getVersion()));
		list.add(new BooleanElement("Enabled", new ControlElement.IconData(Material.EMPTY_MAP), enabled -> {
			enabledChatTime = enabled;
			getConfig().addProperty("enabled", enabled);
			saveConfig();
		}, enabledChatTime));
		list.add(new StringElement("Prefix-Style", new ControlElement.IconData(Material.EMPTY_MAP), chatTimePrefix,
				string -> {
			chatTimePrefix = string;
			getConfig().addProperty("prefix", string);
			saveConfig();
		}));
		list.add(new StringElement("Time-Formatting", new ControlElement.IconData(Material.EMPTY_MAP), chatTimeFormat,
				string -> {
			chatTimeFormat = string;
			getConfig().addProperty("format", string);
			saveConfig();
		}));
	}

	public String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
		char[] array = textToTranslate.toCharArray();
		for (int i = 0; i < array.length - 1; i++)
			if (array[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(array[i + 1]) > -1) {
				array[i] = '§';
				array[i + 1] = Character.toLowerCase(array[i + 1]);
			}
		return new String(array);
	}

	public String stripColor(char colorChar, String input) {
		if (input == null)
			return null;
		return Pattern.compile("(?i)" + colorChar + "[0-9A-FK-OR]").matcher(input).replaceAll("");
	}
}
