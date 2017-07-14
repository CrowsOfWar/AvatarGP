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
package com.crowsofwar.avatar.common;

import static com.crowsofwar.avatar.common.AvatarChatMessages.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

/**
 * Manages information and handling of current bison transfers
 * 
 * @author CrowsOfWar
 */
public class TransferConfirmHandler {
	
	private static final Map<EntityPlayer, TransferData> inProgressTransfers = new HashMap<>();
	
	private TransferConfirmHandler() {}
	
	public static void registerEventHandler() {
		MinecraftForge.EVENT_BUS.register(new TransferConfirmHandler.TickHandler());
	}
	
	/**
	 * Initiates the transfer process and intializes data about the transfer.
	 * Also sends messages to parties involved.
	 */
	public static void startTransfer(EntityPlayer from, EntityPlayer to, EntitySkyBison bison) {
		inProgressTransfers.put(from, new TransferData(from, to, bison));
		MSG_BISON_TRANSFER_OLD_START.send(from, bison.getName(), to.getName());
		MSG_BISON_TRANSFER_NEW_START.send(to, bison.getName(), from.getName());
	}
	
	/**
	 * Tries to transfer the player's bison to whoever was requested. Handles
	 * all transferring and messaging logic.
	 */
	public static void confirmTransfer(EntityPlayer oldOwner) {
		TransferData transfer = inProgressTransfers.get(oldOwner);
		if (transfer != null) {
			
			EntitySkyBison bison = transfer.bison;
			EntityPlayer newOwner = transfer.to;
			bison.setOwner(newOwner);
			
			MSG_BISON_TRANSFER_OLD.send(oldOwner, bison.getName(), newOwner.getName());
			MSG_BISON_TRANSFER_NEW.send(newOwner, bison.getName(), oldOwner.getName());
			
			inProgressTransfers.remove(oldOwner);
			
		} else {
			
			MSG_BISON_TRANSFER_NONE.send(oldOwner);
			
		}
	}
	
	private static class TransferData {
		
		private final EntityPlayer from, to;
		private final EntitySkyBison bison;
		private int ticksLeft;
		
		public TransferData(EntityPlayer from, EntityPlayer to, EntitySkyBison bison) {
			this.from = from;
			this.to = to;
			this.bison = bison;
			this.ticksLeft = 100;
		}
		
	}
	
	private static class TickHandler {
		
		@SubscribeEvent
		public void onTick(TickEvent.ServerTickEvent e) {
			if (e.phase == Phase.START) {
				
				Set<Map.Entry<EntityPlayer, TransferData>> entries = inProgressTransfers.entrySet();
				Iterator<Map.Entry<EntityPlayer, TransferData>> iterator = entries.iterator();
				
				while (iterator.hasNext()) {
					Map.Entry<EntityPlayer, TransferData> entry = iterator.next();
					TransferData data = entry.getValue();
					data.ticksLeft--;
					
					if (data.ticksLeft <= 0 || data.bison.isDead || data.from.isDead || data.to.isDead) {
						MSG_BISON_TRANSFER_OLD_IGNORE.send(data.from, data.to.getName());
						MSG_BISON_TRANSFER_NEW_IGNORE.send(data.to, data.from.getName());
						iterator.remove();
					}
					
				}
				
			}
		}
		
	}
	
}
