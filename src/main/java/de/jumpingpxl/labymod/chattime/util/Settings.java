package de.jumpingpxl.labymod.chattime.util;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import de.jumpingpxl.labymod.chattime.ChatTime;
import de.jumpingpxl.labymod.chattime.util.elements.BetterBooleanElement;
import de.jumpingpxl.labymod.chattime.util.elements.SpacerElement;
import de.jumpingpxl.labymod.chattime.util.elements.formatting.Format;
import de.jumpingpxl.labymod.chattime.util.elements.formatting.FormatElement;
import de.jumpingpxl.labymod.chattime.util.elements.style.StyleElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.HeaderElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Material;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Settings {

	private static final Pattern COLOR_PATTERN = Pattern.compile("[0123456789abcdefr]");
	private static final Pattern FORMAT_PATTERN = Pattern.compile("[lmno]");

	private final ChatTime chatTime;
	private SimpleDateFormat dateFormat;
	private Format format;
	private String customFormat;
	private String rawStyle;
	private ChatComponentText style;
	private boolean enabledChatTime;
	private boolean beforeMessage;

	public Settings(ChatTime chatTime) {
		this.chatTime = chatTime;
	}

	public void loadConfig() {
		enabledChatTime = !getConfig().has("enabled") || getConfig().get("enabled").getAsBoolean();
		beforeMessage = !getConfig().has("before") || getConfig().get("before").getAsBoolean();

		rawStyle = getConfig().has("prefix") ? getConfig().get("prefix").getAsString()
				: "&e┃ &6%time% &7» &r";
		style = fromString(rawStyle);

		String formatName = getConfig().has("format") ? getConfig().get("format").getAsString()
				: Format.HOUR_MINUTE_SECOND.name();
		Optional<Format> formatByName = Format.getFormatByName(formatName);
		if (formatByName.isPresent()) {
			format = formatByName.get();
			customFormat = getConfig().has("customFormat") ?
					getConfig().get("customFormat").getAsString()
					: format.getFormatting();
		} else {
			Optional<Format> formatByValue = Format.getFormatByFormatting(formatName);
			if (formatByValue.isPresent()) {
				format = formatByValue.get();
				customFormat = getConfig().has("customFormat") ? getConfig().get("customFormat")
						.getAsString() : format.getFormatting();
			} else {
				format = Format.CUSTOM;
				customFormat = formatName;
			}
		}

		dateFormat = new SimpleDateFormat(
				format == Format.CUSTOM ? customFormat : format.getFormatting());
	}

	public void fillSettings(List<SettingsElement> settingsElements) {
		settingsElements.add(new HeaderElement("§eChatTime v" + chatTime.getVersion()));
		settingsElements.add(
				new BetterBooleanElement("§6Enabled", new ControlElement.IconData(Material.LEVER),
						enabled -> {
							enabledChatTime = enabled;
							getConfig().addProperty("enabled", enabled);
							saveConfig();
						}, enabledChatTime));

		settingsElements.add(new SpacerElement());
		settingsElements.add(new StyleElement(this, "§6Style", Material.MAP, rawStyle, string -> {
			this.rawStyle = string;
			this.style = fromString(string);
			getConfig().addProperty("prefix", string);
			saveConfig();
		}));

		settingsElements.add(
				new FormatElement("§6Formatting", Material.MAP, new String[]{format.name(), customFormat},
						array -> {
							this.format = Format.getFormatByName(array[0]).orElse(Format.CUSTOM);
							this.customFormat = array[1];
							this.dateFormat = new SimpleDateFormat(
									format == Format.CUSTOM ? customFormat : format.getFormatting());
							getConfig().addProperty("format", format.name());
							getConfig().addProperty("customFormat", customFormat);
							saveConfig();
						}));

		settingsElements.add(
				new BetterBooleanElement("§6Time as Prefix", new ControlElement.IconData(Material.LEVER),
						enabled -> {
							beforeMessage = enabled;
							getConfig().addProperty("before", enabled);
							saveConfig();
						}, beforeMessage));
	}

	public String getFormattedString(String string) {
		char[] array = string.toCharArray();
		for (int i = 0; i < array.length - 1; i++) {
			if (array[i] == '&' && "0123456789AaBbCcDdEeFfLlMmNnOoRr".indexOf(array[i + 1]) > -1) {
				array[i] = '§';
				array[i + 1] = Character.toLowerCase(array[i + 1]);
			}
		}
		return new String(array);
	}

	public Format getFormat() {
		return format;
	}

	public String getCustomFormat() {
		return customFormat;
	}

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	public ChatComponentText getStyle() {
		return style;
	}

	public boolean isEnabledChatTime() {
		return enabledChatTime;
	}

	public boolean isBeforeMessage() {
		return beforeMessage;
	}

	private JsonObject getConfig() {
		return chatTime.getConfig();
	}

	private void saveConfig() {
		chatTime.saveConfig();
	}

	private ChatComponentText fromString(String string) {
		ChatComponentText textComponent = new ChatComponentText("");
		try {
			StringBuilder stringBuilder = new StringBuilder();
			Set<EnumChatFormatting> tempFormats = Sets.newHashSet();
			EnumChatFormatting tempColor = null;
			for (int i = 0; i < string.length(); i++) {
				String character = String.valueOf(string.charAt(i));
				if (!character.equals("&")) {
					String prevCharacter = i == 0 ? null : String.valueOf(string.charAt(i - 1));
					if (Objects.nonNull(prevCharacter) && prevCharacter.equals("&")) {
						Matcher colorMatcher = COLOR_PATTERN.matcher(character);
						if (colorMatcher.find()) {
							if (Objects.nonNull(tempColor)) {
								buildSibling(textComponent, stringBuilder, tempFormats, tempColor);

								stringBuilder = new StringBuilder();
								tempFormats.clear();
							}

							char colorCode = string.charAt(i);
							tempColor = getFormattingByCode(colorCode);
						} else {
							Matcher formatMatcher = FORMAT_PATTERN.matcher(character);
							if (formatMatcher.find()) {
								tempFormats.add(getFormattingByCode(string.charAt(i)));
							} else {
								stringBuilder.append(prevCharacter);
							}
						}
					} else {
						stringBuilder.append(character);
					}
				}

				if (i == string.length() - 1) {
					buildSibling(textComponent, stringBuilder, tempFormats, tempColor);
					break;
				}
			}
		} catch (Exception e) {
			textComponent = new ChatComponentText(string);
			e.printStackTrace();
		}

		return textComponent;
	}

	private void buildSibling(ChatComponentText parent, StringBuilder stringBuilder,
	                          Set<EnumChatFormatting> formatting, EnumChatFormatting color) {
		ChatComponentText sibling = new ChatComponentText(stringBuilder.toString());
		ChatStyle style = sibling.getChatStyle();
		style.setColor(color);
		for (EnumChatFormatting tempFormat : formatting) {
			switch (tempFormat) {
				case OBFUSCATED:
					style.setObfuscated(true);
					break;
				case BOLD:
					style.setBold(true);
					break;
				case STRIKETHROUGH:
					style.setStrikethrough(true);
					break;
				case UNDERLINE:
					style.setUnderlined(true);
					break;
				case ITALIC:
					style.setItalic(true);
					break;
			}
		}

		sibling.setChatStyle(style);
		parent.appendSibling(sibling);
	}

	private EnumChatFormatting getFormattingByCode(char code) {
		switch (code) {
			case '0':
				return EnumChatFormatting.BLACK;
			case '1':
				return EnumChatFormatting.DARK_BLUE;
			case '2':
				return EnumChatFormatting.DARK_GREEN;
			case '3':
				return EnumChatFormatting.DARK_AQUA;
			case '4':
				return EnumChatFormatting.DARK_RED;
			case '5':
				return EnumChatFormatting.DARK_PURPLE;
			case '6':
				return EnumChatFormatting.GOLD;
			case '7':
				return EnumChatFormatting.GRAY;
			case '8':
				return EnumChatFormatting.DARK_GRAY;
			case '9':
				return EnumChatFormatting.BLUE;
			case 'a':
				return EnumChatFormatting.GREEN;
			case 'b':
				return EnumChatFormatting.AQUA;
			case 'c':
				return EnumChatFormatting.RED;
			case 'd':
				return EnumChatFormatting.LIGHT_PURPLE;
			case 'e':
				return EnumChatFormatting.YELLOW;
			case 'k':
				return EnumChatFormatting.OBFUSCATED;
			case 'l':
				return EnumChatFormatting.BOLD;
			case 'm':
				return EnumChatFormatting.STRIKETHROUGH;
			case 'n':
				return EnumChatFormatting.UNDERLINE;
			case 'o':
				return EnumChatFormatting.ITALIC;
			default:
				return EnumChatFormatting.WHITE;
		}
	}
}
