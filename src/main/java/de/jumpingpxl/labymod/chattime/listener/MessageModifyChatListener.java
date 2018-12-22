package de.jumpingpxl.labymod.chattime.listener;

import de.jumpingpxl.labymod.chattime.JumpingAddon;
import net.labymod.api.events.MessageModifyChatEvent;
import net.minecraft.util.ChatComponentText;
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
		if(!jumpingAddon.getSettings().isEnabledChatTime())
			return object;
		String chatTimePrefix = "";
		try {
			chatTimePrefix = jumpingAddon.getSettings().translateAlternateColorCodes('&', jumpingAddon.getSettings().
					getChatTimePrefix()).replace("%time%", new SimpleDateFormat(jumpingAddon.getSettings().stripColor('&',
					jumpingAddon.getSettings().getChatTimeFormat())).format(new Date(System.currentTimeMillis())));
		} catch (IllegalArgumentException e){
			e.printStackTrace();
		}
		return new ChatComponentText(chatTimePrefix).appendSibling((IChatComponent) object);
	}
}
