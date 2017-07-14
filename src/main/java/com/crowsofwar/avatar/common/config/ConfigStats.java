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
package com.crowsofwar.avatar.common.config;

import static net.minecraft.init.Blocks.*;

import java.util.ArrayList;
import java.util.List;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarLog.WarningType;
import com.crowsofwar.gorecore.config.ConfigLoader;
import com.crowsofwar.gorecore.config.Load;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ConfigStats {
	
	public static final ConfigStats STATS_CONFIG = new ConfigStats();
	
	@Load
	public AttackSettings floatingBlockSettings = new AttackSettings(0.45f, 1),
			ravineSettings = new AttackSettings(7, 0.25), //
			waveSettings = new AttackSettings(6, 6), //
			airbladeSettings = new AttackSettings(4, .03), //
			fireArcSettings = new AttackSettings(4, 1);
	
	@Load
	public List<String> bendableBlocksNames;
	
	@Load
	public double wallWaitTime = 10, wallWaitTime2 = 60, wallMomentum = 10;
	
	@Load
	public int wallJumpDelay = 10;
	
	@Load
	public FireballSettings fireballSettings = new FireballSettings();
	
	@Load
	public float chiAirblade = 2f, chiAirGust = 1f, chiAirJump = .8f, chiAirBubble = 3f,
			chiAirBubbleTakeDamage = 0.5f, chiAirBubbleOneSecond = 0.25f, chiRavine = 3f, chiWall = 5f,
			chiPickUpBlock = 2.5f, chiMining = 4f, chiMiningMaster = 1f, chiFireArc = 2f, chiFireball = 4f,
			chiFlamethrowerSecond = 5f, chiLightFire = 3.5f, chiWave = 4f, chiWaterArc = 2f,
			chiWaterBubble = 2.5f, chiWaterSkateSecond = 1f, chiWallOneSecond = 0.25f;
	
	@Load
	public float sleepChiRegen = 99999;
	
	@Load
	public boolean allowAirBubbleElytra = false;
	
	@Load
	public double waterArcSearchRadius = 4, waterArcAngles = 8;
	
	@Load
	public boolean addDungeonLoot = true;
	
	@Load
	public boolean preventPickupBlockGriefing = false;
	
	public List<Block> bendableBlocks;
	
	private ConfigStats() {
		bendableBlocksNames = new ArrayList<>();
		addBendableBlock(STONE, SAND, SANDSTONE, COBBLESTONE, DIRT, GRAVEL, BRICK_BLOCK, MOSSY_COBBLESTONE,
				STONEBRICK, CLAY, HARDENED_CLAY, STAINED_HARDENED_CLAY, COAL_ORE, IRON_ORE, EMERALD_ORE,
				GOLD_ORE, LAPIS_ORE, REDSTONE_ORE, RED_SANDSTONE, GRASS, GRASS_PATH);
		
	}
	
	private void addBendableBlock(Block... blocks) {
		for (Block block : blocks)
			bendableBlocksNames.add(Block.REGISTRY.getNameForObject(block).toString());
	}
	
	private void loadBendableBlocks() {
		bendableBlocks = new ArrayList<>();
		
		for (String blockName : bendableBlocksNames) {
			Block b = Block.REGISTRY.getObject(new ResourceLocation(blockName));
			if (b == null) {
				AvatarLog.warn(WarningType.CONFIGURATION,
						"Invalid bendable blocks entry: " + blockName + "; does not exist");
			} else {
				bendableBlocks.add(b);
			}
		}
		
	}
	
	public static void load() {
		ConfigLoader.load(STATS_CONFIG, "avatar/stats.yml");
	}
	
	public void loadBlocks() {
		STATS_CONFIG.loadBendableBlocks();
	}
	
	public static class AttackSettings {
		
		@Load
		public float damage;
		
		@Load
		public double push;
		
		public AttackSettings() {}
		
		private AttackSettings(float damage, double push) {
			this.damage = damage;
			this.push = push;
		}
		
	}
	
	public static class FireballSettings {
		
		@Load
		public float damage = 6;
		
		@Load
		public int fireTime = 6;
		
		@Load
		public float explosionSize = 1.5f;
		
		@Load
		public boolean damageBlocks = false;
		
		@Load
		public double push = .75;
		
	}
	
}
