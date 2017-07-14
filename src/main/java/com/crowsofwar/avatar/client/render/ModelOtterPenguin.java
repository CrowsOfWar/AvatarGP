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
package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.mob.EntityOtterPenguin;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * TheOtterpenguin - talhanation<br />
 * Created using Tabula 5.1.0
 * 
 * @author talhanation
 */
public class ModelOtterPenguin extends ModelBase {
	
	public ModelRenderer leftarm2;
	public ModelRenderer leftarm1;
	public ModelRenderer rightarm2;
	public ModelRenderer rightarm1;
	public ModelRenderer tail2;
	public ModelRenderer tail1;
	public ModelRenderer body;
	public ModelRenderer leftleg;
	public ModelRenderer head;
	public ModelRenderer rightleg;
	public ModelRenderer leftfoot;
	public ModelRenderer leftshin;
	public ModelRenderer nose;
	public ModelRenderer beard1;
	public ModelRenderer beard2;
	public ModelRenderer rightshin;
	public ModelRenderer rightfoot;
	
	public ModelOtterPenguin() {
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.leftarm2 = new ModelRenderer(this, 73, 0);
		this.leftarm2.mirror = true;
		this.leftarm2.setRotationPoint(3.0F, 6.0F, 1.0F);
		this.leftarm2.addBox(0.5F, 4.0F, -3.0F, 1, 10, 4, 0.0F);
		this.setRotateAngle(leftarm2, 0.0F, -0.0F, -0.03490658476948738F);
		this.tail1 = new ModelRenderer(this, 54, 33);
		this.tail1.setRotationPoint(0.0F, 18.0F, 4.0F);
		this.tail1.addBox(-3.0F, -3.0F, -1.0F, 6, 7, 2, 0.0F);
		this.setRotateAngle(tail1, 0.3839724361896515F, -0.0F, 0.0F);
		this.rightshin = new ModelRenderer(this, 58, 16);
		this.rightshin.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.rightshin.addBox(-1.0F, 2.0F, -1.0F, 2, 2, 2, 0.0F);
		this.beard2 = new ModelRenderer(this, 120, 4);
		this.beard2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.beard2.addBox(-2.0F, -1.6F, -3.6F, 1, 3, 0, 0.0F);
		this.head = new ModelRenderer(this, 0, 0);
		this.head.setRotationPoint(0.0F, 6.999999999999996F, 0.0F);
		this.head.addBox(-4.0F, -8.0F, -3.5F, 8, 8, 8, 0.0F);
		this.rightarm1 = new ModelRenderer(this, 73, 0);
		this.rightarm1.setRotationPoint(-3.0F, 6.0F, 1.0F);
		this.rightarm1.addBox(-1.5F, 4.0F, -3.0F, 1, 10, 4, 0.0F);
		this.setRotateAngle(rightarm1, 0.0F, -0.0F, 0.03490658476948738F);
		this.leftarm1 = new ModelRenderer(this, 85, 0);
		this.leftarm1.setRotationPoint(3.0F, 7.0F, 1.0F);
		this.leftarm1.addBox(0.800000011920929F, 0.0F, -3.0F, 1, 10, 4, 0.0F);
		this.setRotateAngle(leftarm1, 0.0F, -0.0F, -0.15707963705062866F);
		this.rightarm2 = new ModelRenderer(this, 73, 0);
		this.rightarm2.setRotationPoint(-3.0F, 7.0F, 1.0F);
		this.rightarm2.addBox(-1.7999999523162842F, 0.0F, -3.0F, 1, 10, 4, 0.0F);
		this.setRotateAngle(rightarm2, 0.0F, -0.0F, 0.15707963705062866F);
		this.beard1 = new ModelRenderer(this, 120, 4);
		this.beard1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.beard1.addBox(1.0F, -1.6F, -3.6F, 1, 3, 0, 0.0F);
		this.rightleg = new ModelRenderer(this, 0, 16);
		this.rightleg.setRotationPoint(-2.0000000000000004F, 18.999999999999996F, 0.0F);
		this.rightleg.addBox(-2.0F, 0.0F, -2.0F, 4, 2, 4, 0.0F);
		this.leftshin = new ModelRenderer(this, 58, 16);
		this.leftshin.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.leftshin.addBox(-1.0F, 2.0F, -1.0F, 2, 2, 2, 0.0F);
		this.leftleg = new ModelRenderer(this, 0, 16);
		this.leftleg.setRotationPoint(2.1000000000000005F, 18.999999999999975F, 0.0F);
		this.leftleg.addBox(-2.0F, 0.0F, -2.0F, 4, 2, 4, 0.0F);
		this.body = new ModelRenderer(this, 1, 29);
		this.body.setRotationPoint(0.0F, 7.0F, 0.0F);
		this.body.addBox(-4.0F, 0.0F, -3.0F, 8, 12, 7, 0.0F);
		this.tail2 = new ModelRenderer(this, 40, 34);
		this.tail2.setRotationPoint(0.0F, 18.0F, 4.0F);
		this.tail2.addBox(-2.5F, 3.0F, -2.799999952316284F, 5, 6, 1, 0.0F);
		this.setRotateAngle(tail2, 1.0995573997497559F, -0.0F, 0.0F);
		this.rightfoot = new ModelRenderer(this, 43, 16);
		this.rightfoot.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.rightfoot.addBox(-1.5F, 4.0F, -3.0F, 3, 1, 4, 0.0F);
		this.leftfoot = new ModelRenderer(this, 43, 16);
		this.leftfoot.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.leftfoot.addBox(-1.5F, 4.0F, -3.0F, 3, 1, 4, 0.0F);
		this.nose = new ModelRenderer(this, 0, 0);
		this.nose.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.nose.addBox(-1.0F, -3.0F, -4.0F, 2, 1, 1, 0.0F);
		this.rightleg.addChild(this.rightshin);
		this.head.addChild(this.beard2);
		this.head.addChild(this.beard1);
		this.leftleg.addChild(this.leftshin);
		this.rightleg.addChild(this.rightfoot);
		this.leftleg.addChild(this.leftfoot);
		this.head.addChild(this.nose);
		
	}
	
	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		// this part authored by CrowsOfWar
		
		EntityOtterPenguin penguin = (EntityOtterPenguin) entity;
		if (penguin.isChild()) {
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.7f, 0.7f, 0.7f);
			GlStateManager.translate(0, 0.62f, 0);
		}
		
		// end CrowsOfWar
		this.leftarm2.render(f5);
		this.tail1.render(f5);
		this.head.render(f5);
		this.rightarm1.render(f5);
		this.leftarm1.render(f5);
		this.rightarm2.render(f5);
		this.rightleg.render(f5);
		this.leftleg.render(f5);
		this.body.render(f5);
		this.tail2.render(f5);
		// this part authored by CrowsOfWar
		if (penguin.isChild()) {
			GlStateManager.popMatrix();
		}
		// end CrowsOfWar
		
	}
	
	/**
	 * This method made by CrowsOfWar
	 */
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scaleFactor, Entity entity) {
		
		float pi = (float) Math.PI;
		
		head.rotateAngleY = (float) Math.toRadians(netHeadYaw);
		head.rotateAngleX = (float) Math.toRadians(headPitch);
		
		rightleg.rotateAngleX = MathHelper.cos(limbSwing * 2) * limbSwingAmount;
		leftleg.rotateAngleX = MathHelper.cos(limbSwing * 2 + pi) * limbSwingAmount;
		
	}
	
	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
