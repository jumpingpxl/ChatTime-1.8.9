package de.jumpingpxl.labymod.chattime.util.elements;

import net.labymod.main.LabyMod;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;

import java.awt.*;
import java.util.Objects;
import java.util.function.Function;

public class TextField {

	private static final DrawUtils DRAW_UTILS = LabyMod.getInstance().getDrawUtils();

	private final FontRenderer fontRenderer;
	private final Consumer<String> callback;
	private final int x;
	private final int y;
	private final int maxX;
	private final int maxY;
	private final int width;
	private final int height;
	private Function<Checker, Boolean> checker;
	private String text;
	private boolean mouseOver;
	private boolean focused;
	private int textX;

	private boolean cursorVisible;
	private int cursorIndex;
	private int markerStartIndex;

	private int tick;

	public TextField(FontRenderer fontRenderer, int x, int y, int width, int height,
	                 Consumer<String> callback) {
		this.fontRenderer = fontRenderer;
		this.callback = callback;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		maxX = x + width;
		maxY = y + height;
		cursorVisible = true;
		cursorIndex = -1;
		markerStartIndex = -1;
	}

	public void render(int mouseX, int mouseY) {
		mouseOver = mouseX > x && mouseX < maxX && mouseY > y && mouseY < maxY;
		Gui.drawRect(x, y, maxX, maxY, Color.BLACK.getRGB());
		DRAW_UTILS.drawRectBorder(x, y, maxX, maxY,
				(focused ? Color.WHITE : Color.LIGHT_GRAY).getRGB(),
				1);

		textX = (x + width / 2) - (fontRenderer.getStringWidth(text) / 2);
		int textY = y + height / 2 - 4;
		DRAW_UTILS.drawString(text, textX, textY);

		if (cursorIndex != -1 && cursorVisible && focused) {
			int cursorX = getXByIndex(cursorIndex);
			DRAW_UTILS.drawRect(cursorX, y + 4D, cursorX + 1D, maxY - 4D, Color.WHITE.getRGB());
		}

		drawMarker();
	}

	public void mouseClicked(double mouseX, int button) {
		if (markerStartIndex != -1) {
			markerStartIndex = -1;
		}

		if (mouseOver && button == 0) {
			if (!focused) {
				focused = true;
			}

			boolean cursorSet = false;
			int currentX = textX;
			for (int i = 0; i < text.length(); i++) {
				if (!cursorSet) {
					int charWidth = fontRenderer.getStringWidth(String.valueOf(text.charAt(i)));
					if (currentX + charWidth / 2D > mouseX) {
						cursorSet = true;
						setCursorIndex(i);
					} else {
						currentX += charWidth;
					}
				}
			}

			if (!cursorSet) {
				setCursorIndex(text.length());
			}
		} else if (focused) {
			focused = false;
		}
	}

	public void mouseScrolled(int delta) {
		if (focused && delta != 0) {
			delta = delta < 0 ? 1 : -1;
			if (GuiScreen.isCtrlKeyDown()) {
				setCursorIndex(delta > 0 ? text.length() : 0);
			} else {
				moveCursorBy(delta);
			}

			moveMarkerBy(delta);
		}
	}

	public void keyPressed(char typedChar, int keyCode) {
		if (GuiScreen.isKeyComboCtrlA(keyCode)) {
			setCursorIndex(text.length());
			markerStartIndex = 0;
		} else if (GuiScreen.isKeyComboCtrlC(keyCode)) {
			if (markerStartIndex != -1) {
				GuiScreen.setClipboardString(getMarkedText());
			}
		} else if (GuiScreen.isKeyComboCtrlV(keyCode)) {
			writeText(GuiScreen.getClipboardString());
		} else if (GuiScreen.isKeyComboCtrlX(keyCode)) {
			if (markerStartIndex != -1) {
				GuiScreen.setClipboardString(getMarkedText());
				delMarkedChars();
			}
		} else {
			switch (keyCode) {
				case 14:
					if (markerStartIndex != -1) {
						delMarkedChars();
					} else {
						delChar(-1);
					}

					break;
				case 211:
					if (markerStartIndex != -1) {
						delMarkedChars();
					} else {
						delChar(1);
					}

					break;
				case 205:
					if (GuiScreen.isCtrlKeyDown()) {
						setCursorIndex(text.length());
					} else {
						moveCursorBy(1);
					}

					moveMarkerBy(1);
					break;
				case 203:
					if (GuiScreen.isCtrlKeyDown()) {
						setCursorIndex(0);
					} else {
						moveCursorBy(-1);
					}

					moveMarkerBy(-1);
					break;
				default:
					charTyped(typedChar);
			}
		}
	}

	public void charTyped(char codePoint) {
		if (focused && ChatAllowedCharacters.isAllowedCharacter(codePoint)) {
			writeChar(codePoint);
		}
	}

	public void tick() {
		if (tick != 15) {
			tick++;
		} else {
			tick = 1;
			cursorVisible = !cursorVisible;
		}
	}

	public boolean isFocused() {
		return focused;
	}

	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	public void setChecker(Function<Checker, Boolean> checker) {
		this.checker = checker;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;

		if (cursorIndex == -1 || cursorIndex > text.length()) {
			cursorIndex = text.length();
		}
	}

	public int getCursorIndex() {
		return cursorIndex;
	}

	public void setCursorIndex(int index) {
		if (index < 0) {
			this.cursorIndex = 0;
		} else {
			this.cursorIndex = Math.min(index, text.length());
		}

		tick = 5;
		cursorVisible = true;
	}

	public void writeText(String text) {
		for (char character : text.toCharArray()) {
			if (!writeChar(character)) {
				break;
			}
		}
	}

	private void moveCursorBy(int moveBy) {
		setCursorIndex(this.cursorIndex + moveBy);
	}

	private boolean writeChar(char character) {
		StringBuilder stringBuilder = new StringBuilder();
		if (text.length() == 0) {
			stringBuilder.append(character);
		} else {
			if (markerStartIndex != -1) {
				delMarkedChars();
			}

			stringBuilder.append(text, 0, cursorIndex);
			stringBuilder.append(character);
			stringBuilder.append(text.substring(cursorIndex));
		}

		if (fontRenderer.getStringWidth(stringBuilder.toString()) < width - 10 && (Objects.isNull(
				checker) || checker.apply(new Checker(stringBuilder.toString(), character)))) {
			text = stringBuilder.toString();
			callback.accept(text);
			moveCursorBy(1);
			return true;
		}

		return false;
	}

	private int getXByIndex(int index) {
		if (index == text.length()) {
			return textX + fontRenderer.getStringWidth(text);
		}

		int currentX = textX;
		for (int i = 0; i < text.length(); i++) {
			if (i == index) {
				return currentX - 1;
			}

			String character = String.valueOf(text.charAt(i));
			int charWidth = fontRenderer.getStringWidth(character);
			currentX += charWidth;
		}

		return textX;
	}

	private String getMarkedText() {
		int start = Math.min(markerStartIndex, cursorIndex);
		int end = Math.max(markerStartIndex, cursorIndex);
		return text.substring(start, end);
	}

	private void moveMarkerBy(int pos) {
		if (GuiScreen.isShiftKeyDown()) {
			if (markerStartIndex == -1) {
				markerStartIndex = cursorIndex + (pos < 0 ? 1 : -1);
			}
		} else {
			if (markerStartIndex != -1) {
				markerStartIndex = -1;
			}
		}
	}

	private void delMarkedChars() {
		int start = Math.min(markerStartIndex, cursorIndex);
		int end = Math.max(markerStartIndex, cursorIndex);
		for (int i = start; i < end; i++) {
			delChar(markerStartIndex > cursorIndex ? 1 : -1);
		}

		markerStartIndex = -1;
	}

	private void delChar(int pos) {
		if (text.length() == 0) {
			return;
		}

		try {
			text = text.substring(0, cursorIndex + Math.min(pos, 0)) + text.substring(
					cursorIndex + Math.max(pos, 0));
			callback.accept(text);
			moveCursorBy(pos >= 0 ? 0 : -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void drawMarker() {
		if (markerStartIndex == -1) {
			return;
		}

		int markerX = getXByIndex(Math.min(markerStartIndex, cursorIndex));
		int markerY = y + 4;
		int markerMaxX = getXByIndex(Math.max(markerStartIndex, cursorIndex));
		int markerMaxY = maxY - 4;

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer bufferbuilder = tessellator.getWorldRenderer();
		GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
		GlStateManager.disableTexture2D();
		GlStateManager.enableColorLogic();
		GlStateManager.colorLogicOp(5387);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
		bufferbuilder.pos(markerX, markerMaxY, 0.0D).endVertex();
		bufferbuilder.pos(markerMaxX, markerMaxY, 0.0D).endVertex();
		bufferbuilder.pos(markerMaxX, markerY, 0.0D).endVertex();
		bufferbuilder.pos(markerX, markerY, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.disableColorLogic();
		GlStateManager.enableTexture2D();
	}

	public static class Checker {

		private final String text;
		private final char character;

		public Checker(String text, char character) {
			this.text = text;
			this.character = character;
		}

		public String getText() {
			return text;
		}

		public char getCharacter() {
			return character;
		}
	}
}
