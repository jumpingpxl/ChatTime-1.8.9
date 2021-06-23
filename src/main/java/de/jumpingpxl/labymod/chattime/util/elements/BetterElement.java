package de.jumpingpxl.labymod.chattime.util.elements;

import net.labymod.main.LabyMod;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.Material;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public abstract class BetterElement<T> extends ControlElement {

	private static final DrawUtils DRAW_UTILS = LabyMod.getInstance().getDrawUtils();

	private final Consumer<T> toggleListener;
	private final int previewWidth;
	private T value;
	private boolean mouseOverPreview;

	protected BetterElement(String displayName, Material material, int previewWidth, T startValue,
	                        Consumer<T> toggleListener) {
		super(displayName, new IconData(material));

		this.previewWidth = previewWidth;
		this.toggleListener = toggleListener;

		setValue(startValue);
	}

	@Override
	public void draw(int x, int y, int maxX, int maxY, int mouseX, int mouseY) {
		x -= 20;
		maxX += 20;

		super.draw(x, y, maxX, maxY, mouseX, mouseY);
		drawPreview(maxX - previewWidth - 1, y + 1, maxX - 1, maxY - 1, mouseX, mouseY);
	}

	@Override
	public int getEntryHeight() {
		return 22;
	}

	public void drawPreview(int x, int y, int maxX, int maxY, int mouseX, int mouseY) {
		mouseOverPreview = mouseX > x && mouseX < maxX && mouseY > y && mouseY < maxY;

		Gui.drawRect(x, y, maxX, maxY, Color.BLACK.getRGB());
		getDrawUtils().drawRectBorder(x, y, maxX, maxY, Color.LIGHT_GRAY.getRGB(), 1);
	}

	public final T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public final Consumer<T> getToggleListener() {
		return toggleListener;
	}

	public boolean isMouseOverPreview() {
		return mouseOverPreview;
	}

	protected final DrawUtils getDrawUtils() {
		return DRAW_UTILS;
	}
}
