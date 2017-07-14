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
package com.crowsofwar.avatar.common.item;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.AvatarEntityItem;
import com.crowsofwar.avatar.common.gui.AvatarGuiHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ItemScroll extends Item implements AvatarItem {
	
	public ItemScroll() {
		setUnlocalizedName("scroll");
		setMaxStackSize(1);
		setCreativeTab(AvatarItems.tabItems);
		setMaxDamage(0);
		setHasSubtypes(true);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		BendingData data = AvatarPlayerData.fetcher().fetch(player);
		if (data.getAllBending().isEmpty()) {
			player.openGui(AvatarMod.instance, AvatarGuiHandler.GUI_ID_GET_BENDING, world, 0, 0, 0);
		} else {
			BendingController controller = data.getAllBending().get(0);
			player.openGui(AvatarMod.instance, controller.getType().id(), world, 0, 0, 0);
		}
		
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int metadata = stack.getMetadata() >= ScrollType.values().length ? 0 : stack.getMetadata();
		return super.getUnlocalizedName(stack) + "." + ScrollType.fromId(metadata).displayName();
	}
	
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}
	
	@Override
	public boolean hasCustomEntity(ItemStack stack) {
		return getScrollType(stack) == ScrollType.FIRE;
	}
	
	@Override
	public Entity createEntity(World world, Entity old, ItemStack stack) {
		AvatarEntityItem custom = new AvatarEntityItem(world, old.posX, old.posY, old.posZ, stack);
		custom.setResistFire(true);
		custom.motionX = old.motionX;
		custom.motionY = old.motionY;
		custom.motionZ = old.motionZ;
		custom.setDefaultPickupDelay();
		return custom;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltips,
							   ITooltipFlag advanced) {
		
		BendingType bendingType = getScrollType(stack).getBendingType();
		String bendingTypeName = bendingType == null ? "all" : bendingType.name().toLowerCase();
		
		String tooltip = I18n.format("avatar." + bendingTypeName);
		tooltips.add(tooltip);
		
	}
	
	@Override
	public Item item() {
		return this;
	}
	
	@Override
	public String getModelName(int meta) {
		return "scroll_" + ScrollType.fromId(meta).displayName();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		
		for (int meta = 0; meta < ScrollType.values().length; meta++) {
			subItems.add(new ItemStack(this, 1, meta));
		}
		
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}
	
	public static ScrollType getScrollType(ItemStack stack) {
		int meta = stack.getMetadata();
		if (meta < 0 || meta >= ScrollType.values().length) return ScrollType.ALL;
		return ScrollType.fromId(meta);
	}
	
	public static void setScrollType(ItemStack stack, ScrollType type) {
		stack.setItemDamage(type.id());
	}
	
	public enum ScrollType {
		ALL(null),
		EARTH(BendingType.EARTHBENDING),
		FIRE(BendingType.FIREBENDING),
		WATER(BendingType.WATERBENDING),
		AIR(BendingType.AIRBENDING);
		
		private final BendingType type;
		
		private ScrollType(BendingType type) {
			this.type = type;
		}
		
		public boolean isCompatibleWith(ScrollType other) {
			return other == this || this == ALL || other == ALL;
		}
		
		public String displayName() {
			return name().toLowerCase();
		}
		
		public int id() {
			return ordinal();
		}
		
		public boolean accepts(BendingType type) {
			return this.type == null || this.type == type;
		}
		
		/**
		 * Gets the corresponding bending type from this scroll. Returns null if
		 * there isn't an exact corresponding type (ie, in the case of
		 * {@link #ALL}).
		 */
		@Nullable
		public BendingType getBendingType() {
			return type;
		}
		
		public static ScrollType fromId(int id) {
			if (id < 0 || id >= values().length) throw new IllegalArgumentException("Invalid id: " + id);
			return values()[id];
		}
		
		public static int amount() {
			return values().length;
		}
		
	}
	
}
