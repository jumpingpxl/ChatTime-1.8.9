package de.jumpingpxl.labymod.chattime.util.elements.style;

import com.google.common.collect.Sets;
import de.jumpingpxl.labymod.chattime.util.Settings;
import de.jumpingpxl.labymod.chattime.util.elements.OverlayScreen;
import de.jumpingpxl.labymod.chattime.util.elements.PlaceholderButton;
import de.jumpingpxl.labymod.chattime.util.elements.TextField;
import net.labymod.utils.Consumer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

public class StyleScreen extends OverlayScreen<String> {

	private static final String[] PLACEHOLDERS = {"0123456789", "abcdef", "mnor"};
	private static final String TIME_PLACEHOLDER = "%time%";
	private static final String STYLE_TITLE = "§eChange Style";
	private static final String USEFUL_TITLE = "§eUseful";
	private static final String PREVIEW_TITLE = "§ePreview";

	private final Settings settings;
	private final Set<PlaceholderButton> placeholderButtons;
	private TextField textField;

	public StyleScreen(Settings settings, String title, GuiScreen backgroundScreen,
	                   String currentValue, Consumer<String> callback) {
		super(title, backgroundScreen, currentValue, callback, -105, -50, 105, 100);
		this.settings = settings;

		placeholderButtons = Sets.newHashSet();
	}

	@Override
	public void initGui() {
		super.initGui();
		String text = getValue();
		int cursorIndex = text.length();
		boolean focused = true;
		if (Objects.nonNull(textField)) {
			text = textField.getText();
			cursorIndex = textField.getCursorIndex();
			focused = textField.isFocused();
		}

		textField = new TextField(mc.fontRendererObj, centerX - 78, y + 25, 156, 20, this::setValue);
		textField.setText(text);
		textField.setCursorIndex(cursorIndex);
		textField.setFocused(focused);

		placeholderButtons.clear();
		int placeholderX = 0;
		int placeholderY = y + 43;
		for (String placeholder : PLACEHOLDERS) {
			placeholderY += 12;
			int rowWidth = -2;
			char[] charArray = placeholder.toCharArray();
			for (char character : charArray) {
				rowWidth += mc.fontRendererObj.getStringWidth("&" + character) + 2;
			}

			if (placeholder.equals(PLACEHOLDERS[PLACEHOLDERS.length - 1])) {
				rowWidth += mc.fontRendererObj.getStringWidth(TIME_PLACEHOLDER) + 2;
			}

			placeholderX = centerX - rowWidth / 2;
			for (char character : charArray) {
				PlaceholderButton placeholderButton = new PlaceholderButton(mc.fontRendererObj,
						"§" + character + "&" + character, placeholderX, placeholderY,
						s -> textField.writeText(s));
				placeholderButtons.add(placeholderButton);
				placeholderX += placeholderButton.getWidth() + 2;
			}
		}

		PlaceholderButton placeholderButton = new PlaceholderButton(mc.fontRendererObj,
				TIME_PLACEHOLDER, placeholderX, placeholderY, s -> textField.writeText(s));
		placeholderButtons.add(placeholderButton);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		getDrawUtils().drawCenteredString(STYLE_TITLE, centerX, y + 18D, 0.7D);
		textField.render(mouseX, mouseY);

		getDrawUtils().drawCenteredString(USEFUL_TITLE, centerX, y + 48D, 0.7D);
		for (PlaceholderButton placeholder : placeholderButtons) {
			placeholder.render(mouseX, mouseY);
		}

		getDrawUtils().drawCenteredString(PREVIEW_TITLE, centerX, maxY - 57D, 0.7D);
		drawRect(centerX - 98, maxY - 50, centerX + 98, maxY - 30, BACKGROUND_COLOR);
		getDrawUtils().drawRect(centerX - 98.5D, maxY - 50.5D, centerX + 98.5D, maxY - 50D,
				Color.LIGHT_GRAY.getRGB());
		getDrawUtils().drawRect(centerX - 98.5D, maxY - 31D, centerX + 98.5D, maxY - 30.5D,
				Color.LIGHT_GRAY.getRGB());
		String preview = settings.getFormattedString(getValue());
		try {
			preview = preview.replace(TIME_PLACEHOLDER,
					settings.getDateFormat().format(System.currentTimeMillis()));
		} catch (Exception e) {
			preview = preview.replace(TIME_PLACEHOLDER, "§cERROR");
			e.printStackTrace();
		}

		getDrawUtils().drawCenteredString(preview, centerX, maxY - 45D, 1.3D);
	}

	@Override
	public void handleMouseInput() throws IOException {
		textField.mouseScrolled(Mouse.getEventDWheel());
		super.handleMouseInput();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
		boolean clickedPlaceholder = false;
		for (PlaceholderButton placeholder : placeholderButtons) {
			if (placeholder.mouseClicked(button)) {
				clickedPlaceholder = true;
				break;
			}
		}

		if (!clickedPlaceholder) {
			textField.mouseClicked(mouseX, button);
		}

		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		textField.keyPressed(typedChar, keyCode);
		super.keyTyped(typedChar, keyCode);
	}

	@Override
	public void updateScreen() {
		textField.tick();
		super.updateScreen();
	}
}
