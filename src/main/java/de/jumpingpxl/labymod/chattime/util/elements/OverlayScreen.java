package de.jumpingpxl.labymod.chattime.util.elements;

import net.labymod.main.LabyMod;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public abstract class OverlayScreen<T> extends GuiScreen {

	protected static final int BACKGROUND_COLOR = ModColor.toRGB(0, 0, 0, 128);
	private static final DrawUtils DRAW_UTILS = LabyMod.getInstance().getDrawUtils();
	private static final String DONE_BUTTON = "§aDone";
	private static final String CANCEL_BUTTON = "§cCancel";

	private final String title;
	private final GuiScreen backgroundScreen;
	private final Consumer<T> callback;
	private final int defaultX;
	private final int defaultY;
	private final int defaultMaxX;
	private final int defaultMaxY;
	protected int centerX;
	protected int centerY;
	protected int x;
	protected int y;
	protected int maxX;
	protected int maxY;
	private T currentValue;
	private GuiButton cancelButton;

	protected OverlayScreen(String title, GuiScreen backgroundScreen, T currentValue,
	                        Consumer<T> callback, int x, int y, int maxX, int maxY) {
		this.title = title;
		this.backgroundScreen = backgroundScreen;
		this.currentValue = currentValue;
		this.callback = callback;

		defaultX = x;
		defaultY = y;
		defaultMaxX = maxX;
		defaultMaxY = maxY;
	}

	@Override
	public void initGui() {
		super.initGui();
		backgroundScreen.width = this.width;
		backgroundScreen.height = this.height;
		backgroundScreen.initGui();

		int tempCenterY = this.height / 3;
		centerX = this.width / 2;
		centerY = tempCenterY + (defaultY + defaultMaxY) / 3;
		x = centerX + defaultX;
		y = tempCenterY + defaultY;
		maxX = centerX + defaultMaxX;
		maxY = tempCenterY + defaultMaxY;

		int buttonLength = defaultMaxX - 7;
		cancelButton = new GuiButton(1, centerX + 2, maxY - 25, buttonLength, 20, CANCEL_BUTTON);

		GuiButton doneButton = new GuiButton(0, x + 5, maxY - 25, buttonLength, 20, DONE_BUTTON);

		buttonList.add(cancelButton);
		buttonList.add(doneButton);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		backgroundScreen.drawScreen(0, 0, partialTicks);

		drawRect(0, 0, this.width, this.height, BACKGROUND_COLOR);
		drawRect(x, y, maxX, maxY, BACKGROUND_COLOR);
		DRAW_UTILS.drawCenteredString(title, centerX, y + 5D);

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1) {
			actionPerformed(cancelButton);
		} else {
			super.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 0) {
			onDoneClick();
		}

		Minecraft.getMinecraft().displayGuiScreen(backgroundScreen);
	}

	public void onDoneClick() {
		callback.accept(currentValue);
	}

	protected final DrawUtils getDrawUtils() {
		return DRAW_UTILS;
	}

	protected final String getOverlayTitle() {
		return title;
	}

	protected final Consumer<T> getCallback() {
		return callback;
	}

	protected final T getValue() {
		return currentValue;
	}

	protected final void setValue(T value) {
		currentValue = value;
	}
}
