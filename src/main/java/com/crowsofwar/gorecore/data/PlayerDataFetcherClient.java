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
package com.crowsofwar.gorecore.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import com.crowsofwar.gorecore.GoreCore;
import com.crowsofwar.gorecore.util.AccountUUIDs;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Manages player data fetching on a client. Caches player data, creating as
 * necessary using reflection. Supports a callback for player data creation.
 * 
 * @param <T>
 * 
 * @author CrowsOfWar
 */
@SideOnly(Side.CLIENT)
public class PlayerDataFetcherClient<T extends PlayerData> implements PlayerDataFetcher<T> {
	
	private final Minecraft mc;
	
	/**
	 * Keeps track of client-side player data by mapping player UUID to player
	 * data.
	 */
	private Map<UUID, T> playerData = new HashMap<UUID, T>();
	private Class<T> dataClass;
	private Consumer<T> onCreate;
	
	/**
	 * Amount of ticks until check if player data can unload.
	 */
	private int ticksUntilCheck;
	
	/**
	 * Create a client-side player data fetcher.
	 * 
	 * @param dataClass
	 *            The class of your player data
	 */
	public PlayerDataFetcherClient(Class<T> dataClass) {
		this(dataClass, t -> {
		});
	}
	
	/**
	 * Create a client-side player data fetcher.
	 * 
	 * @param dataClass
	 *            The class of your player data
	 * @param callback
	 *            A handler which will be invoked when a new instance of your
	 *            player data was created
	 */
	public PlayerDataFetcherClient(Class<T> dataClass, Consumer<T> callback) {
		this.mc = Minecraft.getMinecraft();
		this.dataClass = dataClass;
		this.onCreate = callback;
		this.ticksUntilCheck = 20;
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	private T createPlayerData(Class<T> dataClass, UUID playerID) {
		try {
			
			EntityPlayer player = AccountUUIDs.findEntityFromUUID(mc.world, playerID);
			return dataClass.getConstructor(DataSaver.class, UUID.class, EntityPlayer.class)
					.newInstance(new DataSaverDontSave(), playerID, player);
			
		} catch (Exception e) {
			GoreCore.LOGGER.warn("Found an error when trying to make new client-side player data!");
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public T fetch(World world, UUID playerID) {
		if (world == null) throw new IllegalArgumentException("Cannot get client player data for null world");
		if (playerID == null)
			throw new IllegalArgumentException("Cannot get client player data for null player ID");
		
		T data = playerData.get(playerID);
		if (data == null) {
			data = createPlayerData(dataClass, playerID);
			playerData.put(playerID, data);
			if (onCreate != null) onCreate.accept(data);
		}
		
		data.setPlayerEntity(AccountUUIDs.findEntityFromUUID(world, playerID));
		return data;
	}
	
	/**
	 * Player data cache is cleared when world is unloaded.
	 */
	@SubscribeEvent
	public void onUnloadWorld(WorldEvent.Unload e) {
		playerData.clear();
		GoreCore.LOGGER.info("Client fetcher- all player data decached");
	}
	
	/**
	 * Every second, check player data if it needs to unload.
	 */
	@SubscribeEvent
	public void onTick(TickEvent.WorldTickEvent e) {
		if (e.side == Side.CLIENT && e.phase == Phase.START && --ticksUntilCheck == 0) {
			ticksUntilCheck = 20;
			Iterator<UUID> iterator = playerData.keySet().iterator();
			while (iterator.hasNext()) {
				UUID playerId = iterator.next();
				T data = playerData.get(playerId);
				if (data.shouldBeDecached()) {
					GoreCore.LOGGER.info("Client fetcher- decaching some player data");
					iterator.remove();
				}
			}
		}
	}
	
}
