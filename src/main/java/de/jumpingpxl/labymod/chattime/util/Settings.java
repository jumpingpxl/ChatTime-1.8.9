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
	private String chatTimeStyle = "&7[&6%time%&7] &r";
	private String hoverStyle = "&7[&6%time%&7]";
	private boolean enabledChatTime = true;
	private boolean enabledHover = false;
	private boolean beforeMessage = true;

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
		if (getConfig().has("chatData") || getConfig().has("chatData2") || getConfig().has("hover")) {
			if (getConfig().has("chatData")) {
				getConfig().addProperty("format", getConfig().get("chatData").getAsString());
				getConfig().remove("chatData");
			}
			if (getConfig().has("chatData2")) {
				getConfig().addProperty("prefix", getConfig().get("chatData2").getAsString());
				getConfig().remove("chatData2");
			}
			if (getConfig().has("hover")) {
				getConfig().addProperty("hoverStyle", getConfig().get("hover").getAsString());
				getConfig().remove("hover");
			}
			saveConfig();
		}
		chatTimeFormat = getConfig().has("format") ? getConfig().get("format").getAsString() : chatTimeFormat;
		chatTimeStyle = getConfig().has("prefix") ? getConfig().get("prefix").getAsString() : chatTimeStyle;
		hoverStyle = getConfig().has("hoverStyle") ? getConfig().get("hoverStyle").getAsString() : hoverStyle;
		enabledChatTime = !getConfig().has("enabled") || getConfig().get("enabled").getAsBoolean();
		enabledHover = getConfig().has("enabledHover") && getConfig().get("enabledHover").getAsBoolean();
		beforeMessage = !getConfig().has("before") || getConfig().get("before").getAsBoolean();
	}


	public void fillSettings(List<SettingsElement> list) {
		list.add(new HeaderElement("§eChatTime v" + jumpingAddon.getVersion()));
		list.add(new HeaderElement("§6General"));
		list.add(new BooleanElement("§6Enabled", new ControlElement.IconData(Material.LEVER), enabled -> {
			enabledChatTime = enabled;
			getConfig().addProperty("enabled", enabled);
			saveConfig();
		}, enabledChatTime));
		list.add(new StringElement("§6Style", new ControlElement.IconData(Material.EMPTY_MAP), chatTimeStyle,
				string -> {
					chatTimeStyle = string;
					getConfig().addProperty("prefix", string);
					saveConfig();
				}));
		list.add(new StringElement("§6Time-Formatting", new ControlElement.IconData(Material.EMPTY_MAP), chatTimeFormat,
				string -> {
					chatTimeFormat = string;
					getConfig().addProperty("format", string);
					saveConfig();
				}));
		list.add(new BooleanElement("§6Before Message", new ControlElement.IconData(Material.LEVER), enabled -> {
			beforeMessage = enabled;
			getConfig().addProperty("before", enabled);
			saveConfig();
		}, beforeMessage));
		list.add(new BooleanElement("§6Hover", new ControlElement.IconData(Material.LEVER), enabled -> {
			enabledHover = enabled;
			getConfig().addProperty("enabledHover", enabled);
			saveConfig();
		}, enabledHover));
		list.add(new StringElement("§6Hover-Style", new ControlElement.IconData(Material.EMPTY_MAP), hoverStyle,
				string -> {
					hoverStyle = string;
					getConfig().addProperty("hoverStyle", string);
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
