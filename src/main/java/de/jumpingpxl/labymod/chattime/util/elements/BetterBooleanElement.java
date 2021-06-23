package de.jumpingpxl.labymod.chattime.util.elements;

import net.labymod.settings.elements.BooleanElement;
import net.labymod.utils.Consumer;

public class BetterBooleanElement extends BooleanElement {

	public BetterBooleanElement(String displayName, IconData iconData,
	                            Consumer<Boolean> toggleListener, boolean currentValue) {
		super(displayName, iconData, toggleListener, currentValue);
	}

	@Override
	public void draw(int x, int y, int maxX, int maxY, int mouseX, int mouseY) {
		x -= 20;
		maxX += 20;

		super.draw(x, y, maxX, maxY, mouseX, mouseY);
	}
}
