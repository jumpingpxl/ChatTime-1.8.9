package de.jumpingpxl.labymod.chattime.listener;

import de.jumpingpxl.labymod.chattime.JumpingAddon;
import net.labymod.api.events.MessageModifyChatEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 21.12.2018
 */

public class MessageModifyChatListener implements MessageModifyChatEvent {

	private JumpingAddon jumpingAddon;

	public MessageModifyChatListener(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	@Override
	public Object onModifyChatMessage(Object object) {
		if (!jumpingAddon.getSettings().isEnabledChatTime())
			return object;
		String time = "";
		try {
			time = new SimpleDateFormat(jumpingAddon.getSettings().stripColor('&',
					jumpingAddon.getSettings().getChatTimeFormat())).format(new Date(System.currentTimeMillis()));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		ChatComponentText chatComponent = new ChatComponentText(jumpingAddon.getSettings().
				translateAlternateColorCodes('&', jumpingAddon.getSettings().getChatTimeStyle()).replace("%time%", time));
		if (jumpingAddon.getSettings().isEnabledHover())
			chatComponent.setChatStyle(new ChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ChatComponentText(jumpingAddon.getSettings().translateAlternateColorCodes('&',
							jumpingAddon.getSettings().getHoverStyle()).replace("%time%", time)))));
		if (jumpingAddon.getSettings().isBeforeMessage())
			return new ChatComponentText("").setChatStyle(new ChatStyle()).appendSibling(chatComponent).
					appendSibling((IChatComponent) object);
		else
			return ((IChatComponent) object).appendSibling(chatComponent);
	}
}