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
package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.data.ctx.BenderInfo;
import com.crowsofwar.avatar.common.entity.data.OwnerAttribute;
import com.crowsofwar.avatar.common.util.AvatarDataSerializers;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityAirblade extends AvatarEntity {
	
	public static final DataParameter<BenderInfo> SYNC_OWNER = EntityDataManager
			.createKey(EntityAirblade.class, AvatarDataSerializers.SERIALIZER_BENDER);
	
	private final OwnerAttribute ownerAttr;
	private float damage;
	
	/**
	 * Hardness threshold to chop blocks. For example, setting to 1.5 will allow
	 * the airblade to chop stone.
	 * <p>
	 * Note: Threshold of 0 means that the airblade can chop grass and similar
	 * blocks. Set to > 0 to avoid chopping blocks at all.
	 */
	private float chopBlocksThreshold;
	private boolean chainAttack;
	private boolean pierceArmor;
	
	public EntityAirblade(World world) {
		super(world);
		setSize(1.5f, .2f);
		this.ownerAttr = new OwnerAttribute(this, SYNC_OWNER);
		this.chopBlocksThreshold = -1;
	}
	
	@Override
	public void onUpdate() {
		
		super.onUpdate();
		
		if (!world.isRemote && velocity().sqrMagnitude() <= .9) {
			setDead();
		}
		if (!world.isRemote && inWater) {
			setDead();
		}
		
		if (!world.isRemote && chopBlocksThreshold >= 0) {
			breakCollidingBlocks();
		}
		
		if (!isDead && !world.isRemote) {
			List<EntityLivingBase> collidedList = world.getEntitiesWithinAABB(EntityLivingBase.class,
					getEntityBoundingBox());
			
			if (!collidedList.isEmpty()) {
				
				EntityLivingBase collided = collidedList.get(0);
				
				DamageSource source = AvatarDamageSource.causeAirbladeDamage(collided, getOwner());
				if (pierceArmor) {
					source.setDamageBypassesArmor();
				}
				collided.attackEntityFrom(source, STATS_CONFIG.airbladeSettings.damage);
				
				Vector motion = velocity().copy();
				motion.mul(STATS_CONFIG.airbladeSettings.push);
				motion.setY(0.08);
				collided.addVelocity(motion.x(), motion.y(), motion.z());
				
				if (getOwner() != null) {
					BendingData data = getOwnerBender().getData();
					data.getAbilityData(BendingAbility.ABILITY_AIRBLADE).addXp(SKILLS_CONFIG.airbladeHit);
				}
				
				setDead();
				
			}
		}
		
	}
	
	/**
	 * When the airblade can break blocks, checks any blocks that the airblade
	 * collides with and tries to break them
	 */
	private void breakCollidingBlocks() {
		// Hitbox expansion (in each direction) to destroy blocks before the
		// airblade collides with them
		double expansion = 0.1;
		AxisAlignedBB hitbox = getEntityBoundingBox().expand(expansion, expansion, expansion);
		
		for (int ix = 0; ix <= 1; ix++) {
			for (int iz = 0; iz <= 1; iz++) {
				
				double x = ix == 0 ? hitbox.minX : hitbox.maxX;
				double y = hitbox.minY;
				double z = iz == 0 ? hitbox.minZ : hitbox.maxZ;
				BlockPos pos = new BlockPos(x, y, z);
				
				tryBreakBlock(world.getBlockState(pos), pos);
				
			}
		}
	}
	
	/**
	 * Assuming the airblade can break blocks, tries to break the block.
	 */
	private void tryBreakBlock(IBlockState state, BlockPos pos) {
		if (state.getBlock() == Blocks.AIR) {
			return;
		}
		
		float hardness = state.getBlockHardness(world, pos);
		if (hardness <= chopBlocksThreshold) {
			breakBlock(pos);
			velocity().mul(0.5);
		}
	}
	
	@Override
	public void setDead() {
		super.setDead();
	}
	
	@Override
	public EntityLivingBase getOwner() {
		return ownerAttr.getOwner();
	}
	
	public void setOwner(EntityLivingBase owner) {
		ownerAttr.setOwner(owner);
	}
	
	public Bender getOwnerBender() {
		return ownerAttr.getOwnerBender();
	}
	
	public void setDamage(float damage) {
		this.damage = damage;
	}
	
	public float getChopBlocksThreshold() {
		return chopBlocksThreshold;
	}
	
	public void setChopBlocksThreshold(float chopBlocksThreshold) {
		this.chopBlocksThreshold = chopBlocksThreshold;
	}
	
	public boolean getPierceArmor() {
		return pierceArmor;
	}
	
	public void setPierceArmor(boolean piercing) {
		this.pierceArmor = piercing;
	}
	
	public boolean isChainAttack() {
		return chainAttack;
	}
	
	public void setChainAttack(boolean chainAttack) {
		this.chainAttack = chainAttack;
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		ownerAttr.load(nbt);
		damage = nbt.getFloat("Damage");
		chopBlocksThreshold = nbt.getFloat("ChopBlocksThreshold");
		pierceArmor = nbt.getBoolean("Piercing");
		chainAttack = nbt.getBoolean("ChainAttack");
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		ownerAttr.save(nbt);
		nbt.setFloat("Damage", damage);
		nbt.setFloat("ChopBlocksThreshold", chopBlocksThreshold);
		nbt.setBoolean("Piercing", pierceArmor);
		nbt.setBoolean("ChainAttack", chainAttack);
	}
	
}
