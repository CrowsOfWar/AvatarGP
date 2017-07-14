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
package com.crowsofwar.avatar.common.entity.mob;

import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import java.util.Set;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityOtterPenguin extends EntityAnimal {
	
	public static final ResourceLocation LOOT_TABLE = LootTableList
			.register(new ResourceLocation("avatarmod", "otterpenguin"));
	
	/**
	 * @param world
	 */
	public EntityOtterPenguin(World world) {
		super(world);
		setSize(0.7f, 1.6f);
	}
	
	@Override
	protected void initEntityAI() {
		Set<Item> temptItems = Sets.newHashSet(Items.FISH);
		
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIPanic(this, 3D));
		this.tasks.addTask(3, new EntityAIMate(this, 1.0D));
		this.tasks.addTask(4, new EntityAITempt(this, 1.2D, false, temptItems));
		this.tasks.addTask(5, new EntityAIFollowParent(this, 1.1D));
		this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 3));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
	}
	
	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		return new EntityOtterPenguin(world);
	}
	
	@Override
	protected ResourceLocation getLootTable() {
		return LOOT_TABLE;
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		
		if (!super.processInteract(player, hand) && !world.isRemote) {
			
			if (!isBreedingItem(player.getHeldItemMainhand())
					&& !isBreedingItem(player.getHeldItemOffhand())) {
				
				player.startRiding(this);
				return true;
				
			}
			
		}
		
		return false;
	}
	
	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack.getItem() == Items.FISH;
	}
	
	@Override
	public Entity getControllingPassenger() {
		return getPassengers().isEmpty() ? null : getPassengers().get(0);
	}
	
	@Override
	public boolean canBeSteered() {
		return getControllingPassenger() instanceof EntityLivingBase;
	}
	
	@Override
	public boolean canPassengerSteer() {
		return super.canPassengerSteer();
	}

	// moveWithHeading
	@Override
	public void travel(float strafe, float forward, float unknown) {
		EntityLivingBase driver = (EntityLivingBase) getControllingPassenger();
		
		if (this.isBeingRidden() && this.canBeSteered()) {
			this.rotationYaw = driver.rotationYaw;
			this.prevRotationYaw = this.rotationYaw;
			this.rotationPitch = driver.rotationPitch * 0.5F;
			this.setRotation(this.rotationYaw, this.rotationPitch);
			this.renderYawOffset = this.rotationYaw;
			this.rotationYawHead = this.rotationYaw;
			this.stepHeight = 1.0F;
			this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;
			
			if (this.canPassengerSteer()) {
				
				forward = driver.moveForward;
				strafe = driver.moveStrafing;
				
				setAIMoveSpeed((float) getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
						.getAttributeValue());
				super.travel(strafe, forward, unknown);
				
			} else {
				this.motionX = 0.0D;
				this.motionY = 0.0D;
				this.motionZ = 0.0D;
			}
			
			this.prevLimbSwingAmount = this.limbSwingAmount;
			double d1 = this.posX - this.prevPosX;
			double d0 = this.posZ - this.prevPosZ;
			float f1 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;
			
			if (f1 > 1.0F) {
				f1 = 1.0F;
			}
			
			this.limbSwingAmount += (f1 - this.limbSwingAmount) * 0.4F;
			this.limbSwing += this.limbSwingAmount;
		} else {
			this.stepHeight = 0.5F;
			this.jumpMovementFactor = 0.02F;
			super.travel(strafe, forward, unknown);
		}
	}
	
}
