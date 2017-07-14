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
package com.crowsofwar.avatar.common.entity.ai;

import java.util.Random;

import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityAiBisonSit extends EntityAIBase {
	
	private final EntitySkyBison bison;
	
	public EntityAiBisonSit(EntitySkyBison bison) {
		this.bison = bison;
		setMutexBits(1);
	}
	
	@Override
	public boolean shouldExecute() {
		return bison.isSitting();
	}
	
	@Override
	public void startExecuting() {
		World world = bison.world;
		Vector bisonPos = Vector.getEntityPos(bison);
		
		int y;
		for (y = (int) bisonPos.y(); y > 0; y--) {
			BlockPos pos = new BlockPos(bisonPos.x(), y, bisonPos.z());
			if (world.isSideSolid(pos, EnumFacing.UP)) {
				break;
			}
		}
		
		Random random = bison.getRNG();
		Vector randomized = new Vector((random.nextDouble() * 2 - 1) * 2, 0,
				(random.nextDouble() * 2 - 1) * 2);
		
		Vector targetPos = bisonPos.copy().setY(y - 1).plus(randomized);
		bison.getMoveHelper().setMoveTo(targetPos.x(), targetPos.y(), targetPos.z(), 1);
		
	}
	
}
