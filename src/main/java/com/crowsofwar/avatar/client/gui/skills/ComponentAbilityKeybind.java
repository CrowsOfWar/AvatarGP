/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.crowsofwar.avatar.client.gui.skills;

import static com.crowsofwar.avatar.common.config.ConfigClient.CLIENT_CONFIG;

import org.lwjgl.input.Keyboard;

import com.crowsofwar.avatar.client.uitools.UiComponent;
import com.crowsofwar.avatar.common.bending.BendingAbility;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ComponentAbilityKeybind extends UiComponent {
	
	private final BendingAbility ability;
	private String text1, text2;
	private int color;
	
	private boolean editing;
	private Conflictable conflict;
	private Integer editContents;
	
	public ComponentAbilityKeybind(BendingAbility ability) {
		this.ability = ability;
		this.text1 = this.text2 = "";
		this.color = 0xffffff;
		
		this.editing = false;
		this.conflict = null;
		
		updateText();
		
	}
	
	@Override
	protected float componentWidth() {
		int w1 = mc.fontRenderer.getStringWidth(text1);
		int w2 = mc.fontRenderer.getStringWidth(text2);
		return Math.max(w1, w2);
	}
	
	@Override
	protected float componentHeight() {
		return mc.fontRenderer.FONT_HEIGHT * 2;
	}
	
	@Override
	protected void componentDraw(float partialTicks, boolean mouseHover) {
		
		FontRenderer fr = mc.fontRenderer;
		fr.drawString(text1, 0, 0, color);
		fr.drawString(text2, 0, fr.FONT_HEIGHT, color);
		
	}
	
	/**
	 * Update the current text and color based on current keybind, whether
	 * editing, etc.
	 */
	private void updateText() {
		
		// Keycode mapped to this ability - may be null!
		Integer keymapping = editing ? editContents : currentKey();
		
		String key;
		
		if (hasConflict()) {
			color = 0xff0000;
			key = "conflict";
		} else if (editing) {
			color = 0xff5962;
			key = "editing";
		} else {
			color = 0xffffff;
			key = keymapping != null ? "set" : "none";
		}
		
		String keymappingStr = keymapping == null ? "no key" : GameSettings.getKeyDisplayString(keymapping);
		String conflictStr = conflict == null ? "no conflict" : conflict.getName();
		
		text1 = I18n.format("avatar.key." + key + "1", keymappingStr);
		text2 = I18n.format("avatar.key." + key + "2", conflictStr);
		
	}
	
	private boolean hasConflict() {
		return conflict != null;
	}
	
	@Override
	protected void click(int button) {
		
		if (editing) {
			// Stop editing
			
			if (button == 0) {
				// Store on LMB
				editing = false;
				storeKey(editContents);
			} else if (button == 1) {
				// Discard on RMB
				editing = false;
			} else {
				// Accept MMB and extra mouse buttons
				editContents = button - 100;
			}
			
		} else {
			// Start editing
			editing = true;
			editContents = hasKeybinding() ? currentKey() : null;
		}
		
		updateText();
		
	}
	
	@Override
	public void keyPressed(int keyCode) {
		
		if (keyCode == Keyboard.KEY_ESCAPE) {
			editing = false;
			storeKey(null);
			updateText();
		} else if (editing) {
			editContents = keyCode;
			updateText();
		}
	}
	
	private Integer currentKey() {
		return CLIENT_CONFIG.keymappings.get(ability);
	}
	
	private boolean hasKeybinding() {
		return currentKey() != null;
	}
	
	private void storeKey(Integer key) {
		CLIENT_CONFIG.keymappings.put(ability, key);
	}
	
	public boolean isEditing() {
		return editing;
	}
	
	interface Conflictable {
		String getName();
	}
	
	private static Conflictable conflictableKeybinding(KeyBinding keybind) {
		return () -> GameSettings.getKeyDisplayString(keybind.getKeyCode());
	}
	
	private static Conflictable conflictableAbility(BendingAbility ability) {
		return () -> GameSettings.getKeyDisplayString(CLIENT_CONFIG.keymappings.get(ability));
	}
	
}
