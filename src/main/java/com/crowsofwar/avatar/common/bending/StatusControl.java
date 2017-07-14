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
package com.crowsofwar.avatar.common.bending;

import java.util.ArrayList;
import java.util.List;

import com.crowsofwar.avatar.common.bending.air.StatCtrlAirJump;
import com.crowsofwar.avatar.common.bending.air.StatCtrlBubbleContract;
import com.crowsofwar.avatar.common.bending.air.StatCtrlBubbleExpand;
import com.crowsofwar.avatar.common.bending.earth.StatCtrlPlaceBlock;
import com.crowsofwar.avatar.common.bending.earth.StatCtrlThrowBlock;
import com.crowsofwar.avatar.common.bending.fire.StatCtrlSetFlamethrowing;
import com.crowsofwar.avatar.common.bending.fire.StatCtrlThrowFire;
import com.crowsofwar.avatar.common.bending.fire.StatCtrlThrowFireball;
import com.crowsofwar.avatar.common.bending.water.StatCtrlSkateJump;
import com.crowsofwar.avatar.common.bending.water.StatCtrlSkateStart;
import com.crowsofwar.avatar.common.bending.water.StatCtrlThrowBubble;
import com.crowsofwar.avatar.common.bending.water.StatCtrlThrowWater;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.util.Raytrace;

/**
 * Describes a temporary effect where a callback listener is added to a control
 * event. The listener then will perform certain actions associated with that
 * control.
 * <p>
 * For example, the player receives a place-block Status Control, which
 * subscribes to right-click. The status control receives a callback whenever
 * the player uses the right-click control. Then, the status control is removed.
 * <p>
 * Status controls are stored in player-data, but are also sent to the client
 * via packets, which render over the crosshair.
 * 
 * @author CrowsOfWar
 */
public abstract class StatusControl {
	
	// @formatter:off
	public static final StatusControl
			AIR_JUMP = new StatCtrlAirJump(),
			PLACE_BLOCK = new StatCtrlPlaceBlock(),
			THROW_BLOCK = new StatCtrlThrowBlock(),
			THROW_WATER = new StatCtrlThrowWater(),
			START_FLAMETHROW = new StatCtrlSetFlamethrowing(true),
			STOP_FLAMETHROW = new StatCtrlSetFlamethrowing(false),
			THROW_FIRE = new StatCtrlThrowFire(),
			THROW_BUBBLE = new StatCtrlThrowBubble(),
			SKATING_JUMP = new StatCtrlSkateJump(),
			SKATING_START = new StatCtrlSkateStart(),
			THROW_FIREBALL = new StatCtrlThrowFireball(),
			BUBBLE_EXPAND = new StatCtrlBubbleExpand(),
			BUBBLE_CONTRACT = new StatCtrlBubbleContract();
	// @formatter:on
	
	private static int nextId = 0;
	private static List<StatusControl> allControls;
	
	private final int texture;
	private final AvatarControl control;
	private Raytrace.Info raytrace;
	private final CrosshairPosition position;
	private final int id;
	
	public StatusControl(int texture, AvatarControl subscribeTo, CrosshairPosition position) {
		
		if (allControls == null) allControls = new ArrayList<>();
		
		this.texture = texture;
		this.control = subscribeTo;
		this.raytrace = new Raytrace.Info();
		this.position = position;
		this.id = ++nextId;
		allControls.add(this);
		
	}
	
	/**
	 * Require that a raytrace be cast client-side, which is sent to the server.
	 * It is then accessible in {@link #execute(BendingContext)}.
	 * 
	 * @param range
	 *            Range to raytrace. -1 for player reach
	 * @param raycastLiquids
	 *            Whether to keep going when hit liquids
	 */
	protected void requireRaytrace(int range, boolean raycastLiquids) {
		this.raytrace = new Raytrace.Info(range, raycastLiquids);
	}
	
	/**
	 * Execute this status control in the given context. Only called
	 * server-side.
	 * 
	 * @param ctx
	 *            Information for status control
	 * @return Whether to remove it
	 */
	public abstract boolean execute(BendingContext ctx);
	
	public int id() {
		return id;
	}
	
	public AvatarControl getSubscribedControl() {
		return control;
	}
	
	public Raytrace.Info getRaytrace() {
		return raytrace;
	}
	
	public int getTextureU() {
		return (texture * 16) % 256;
	}
	
	public int getTextureV() {
		return (texture / 16) * 16;
	}
	
	public CrosshairPosition getPosition() {
		return position;
	}
	
	public static StatusControl lookup(int id) {
		id--;
		return id >= 0 && id < allControls.size() ? allControls.get(id) : null;
	}
	
	public enum CrosshairPosition {
		
		ABOVE_CROSSHAIR(4, 14),
		LEFT_OF_CROSSHAIR(14, 3),
		RIGHT_OF_CROSSHAIR(-6, 3),
		BELOW_CROSSHAIR(4, -8);
		
		private final int x, y;
		
		/**
		 * Some notes on coordinates:<br />
		 * +y = up<br />
		 * +x = left
		 * 
		 * @param x
		 * @param y
		 */
		private CrosshairPosition(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public int xOffset() {
			return x;
		}
		
		public int yOffset() {
			return y;
		}
		
	}
	
}
