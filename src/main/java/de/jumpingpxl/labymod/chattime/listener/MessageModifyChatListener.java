package de.jumpingpxl.labymod.chattime.listener;

import de.jumpingpxl.labymod.chattime.util.Settings;
import net.labymod.api.events.MessageModifyChatEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class MessageModifyChatListener implements MessageModifyChatEvent {

	private final Settings settings;

	public MessageModifyChatListener(Settings settings) {
		this.settings = settings;
	}

	@Override
	public Object onModifyChatMessage(Object object) {
		if (!settings.isEnabledChatTime()) {
			return object;
		}

		ChatComponentText textComponent = settings.getStyle().createCopy();
		for (int i = 0; i < textComponent.getSiblings().size(); i++) {
			IChatComponent sibling = textComponent.getSiblings().get(i);
			if (sibling.getUnformattedText().contains("%time%")) {
				ChatComponentText newSibling = new ChatComponentText(
						sibling.getUnformattedText().replace("%time%", getTime()));
				newSibling.setChatStyle(sibling.getChatStyle());
				textComponent.getSiblings().remove(sibling);
				textComponent.getSiblings().add(i, newSibling);
			}
		}

		if (settings.isBeforeMessage()) {
			return textComponent.appendSibling((IChatComponent) object);
		} else {
			return new ChatComponentText("").appendSibling((IChatComponent) object).appendSibling(
					textComponent);
		}
	}

	private String getTime() {
		try {
			return settings.getDateFormat().format(System.currentTimeMillis());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return "§cERROR";
		}
	}
}
