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
package com.crowsofwar.avatar.client;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarLog.WarningType;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.client.gui.AvatarUiRenderer;
import com.crowsofwar.avatar.client.gui.GuiBisonChest;
import com.crowsofwar.avatar.client.gui.PreviewWarningGui;
import com.crowsofwar.avatar.client.gui.skills.GetBendingGui;
import com.crowsofwar.avatar.client.gui.skills.SkillsGui;
import com.crowsofwar.avatar.client.particles.AvatarParticleAir;
import com.crowsofwar.avatar.client.particles.AvatarParticleFlames;
import com.crowsofwar.avatar.client.render.*;
import com.crowsofwar.avatar.common.AvatarCommonProxy;
import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.controls.IControlsHandler;
import com.crowsofwar.avatar.common.controls.KeybindingWrapper;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.*;
import com.crowsofwar.avatar.common.entity.mob.*;
import com.crowsofwar.avatar.common.gui.AvatarGui;
import com.crowsofwar.avatar.common.gui.AvatarGuiHandler;
import com.crowsofwar.avatar.common.network.IPacketHandler;
import com.crowsofwar.avatar.common.network.packets.PacketSRequestData;
import com.crowsofwar.avatar.common.particle.ClientParticleSpawner;
import com.crowsofwar.gorecore.data.PlayerDataFetcher;
import com.crowsofwar.gorecore.data.PlayerDataFetcherClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.crowsofwar.avatar.common.config.ConfigClient.CLIENT_CONFIG;
import static net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler;

@SideOnly(Side.CLIENT)
public class AvatarClientProxy implements AvatarCommonProxy {
	
	private Minecraft mc;
	private PacketHandlerClient packetHandler;
	private ClientInput inputHandler;
	private PlayerDataFetcher<AvatarPlayerData> clientFetcher;
	private boolean displayedMainMenu;
	private List<KeyBinding> allKeybindings;
	
	@Override
	public void preInit() {
		mc = Minecraft.getMinecraft();
		
		displayedMainMenu = false;
		
		packetHandler = new PacketHandlerClient();
		AvatarUiRenderer.instance = new AvatarUiRenderer();
		
		inputHandler = new ClientInput();
		MinecraftForge.EVENT_BUS.register(inputHandler);
		MinecraftForge.EVENT_BUS.register(AvatarUiRenderer.instance);
		MinecraftForge.EVENT_BUS.register(this);
		AvatarInventoryOverride.register();
		AvatarFovChanger.register();
		
		clientFetcher = new PlayerDataFetcherClient<>(AvatarPlayerData.class, (data) -> {
			AvatarMod.network.sendToServer(new PacketSRequestData(data.getPlayerID()));
			AvatarLog.debug("Client: Requesting data for " + data.getPlayerEntity() + "");
		});
		
		registerEntityRenderingHandler(EntityFloatingBlock.class, RenderFloatingBlock::new);
		registerEntityRenderingHandler(EntityFireArc.class, RenderFireArc::new);
		registerEntityRenderingHandler(EntityWaterArc.class, RenderWaterArc::new);
		registerEntityRenderingHandler(EntityAirGust.class, RenderAirGust::new);
		registerEntityRenderingHandler(EntityRavine.class, RenderRavine::new);
		registerEntityRenderingHandler(EntityFlames.class,
				rm -> new RenderFlames(rm, new ClientParticleSpawner()));
		registerEntityRenderingHandler(EntityWave.class, RenderWave::new);
		registerEntityRenderingHandler(EntityWaterBubble.class, RenderWaterBubble::new);
		registerEntityRenderingHandler(EntityWallSegment.class, RenderWallSegment::new);
		registerEntityRenderingHandler(EntityFireball.class, RenderFireball::new);
		registerEntityRenderingHandler(EntityAirblade.class, RenderAirblade::new);
		registerEntityRenderingHandler(EntityAirBubble.class, RenderAirBubble::new);
		registerEntityRenderingHandler(EntitySkyBison.class, RenderSkyBison::new);
		registerEntityRenderingHandler(EntityOtterPenguin.class, RenderOtterPenguin::new);
		
		registerEntityRenderingHandler(EntityAirbender.class,
				rm -> new RenderHumanBender(rm, "airbender", 7));
		registerEntityRenderingHandler(EntityFirebender.class,
				rm -> new RenderHumanBender(rm, "firebender", 1));
		registerEntityRenderingHandler(EntityWaterbender.class,
				rm -> new RenderHumanBender(rm, "airbender", 1));
		
	}

	@Override
	public IControlsHandler getKeyHandler() {
		return inputHandler;
	}
	
	@Override
	public IPacketHandler getClientPacketHandler() {
		return packetHandler;
	}
	
	@Override
	public double getPlayerReach() {
		PlayerControllerMP pc = mc.playerController;
		double reach = pc.getBlockReachDistance();
		if (pc.extendedReach()) reach = 6;
		return reach;
	}
	
	@Override
	public void init() {
		
		ParticleManager pm = mc.effectRenderer;
		
		if (CLIENT_CONFIG.useCustomParticles) {
			pm.registerParticle(AvatarParticles.getParticleFlames().getParticleID(),
					AvatarParticleFlames::new);
			pm.registerParticle(AvatarParticles.getParticleAir().getParticleID(), AvatarParticleAir::new);
		}
		
	}
	
	@Override
	public AvatarGui createClientGui(int id, EntityPlayer player, World world, int x, int y, int z) {
		
		if (id == AvatarGuiHandler.GUI_ID_SKILLS_EARTH) return new SkillsGui(BendingType.EARTHBENDING);
		if (id == AvatarGuiHandler.GUI_ID_SKILLS_FIRE) return new SkillsGui(BendingType.FIREBENDING);
		if (id == AvatarGuiHandler.GUI_ID_SKILLS_WATER) return new SkillsGui(BendingType.WATERBENDING);
		if (id == AvatarGuiHandler.GUI_ID_SKILLS_AIR) return new SkillsGui(BendingType.AIRBENDING);
		if (id == AvatarGuiHandler.GUI_ID_BISON_CHEST) {
			// x-coordinate represents ID of sky bison
			int bisonId = x;
			EntitySkyBison bison = EntitySkyBison.findBison(world, bisonId);
			if (bison != null) {
				
				return new GuiBisonChest(player.inventory, bison);
				
			} else {
				AvatarLog.warn(WarningType.WEIRD_PACKET, player.getName()
						+ " tried to open skybison inventory, was not found. BisonId: " + bisonId);
			}
		}
		if (id == AvatarGuiHandler.GUI_ID_GET_BENDING) {
			return new GetBendingGui(player);
		}
		
		return null;
	}
	
	@Override
	public PlayerDataFetcher<AvatarPlayerData> getClientDataFetcher() {
		return clientFetcher;
	}
	
	@Override
	public IThreadListener getClientThreadListener() {
		return mc;
	}
	
	@Override
	public int getParticleAmount() {
		return mc.gameSettings.particleSetting;
	}
	
	@SubscribeEvent
	public void onMainMenu(GuiOpenEvent e) {
		if (AvatarInfo.IS_PREVIEW && e.getGui() instanceof GuiMainMenu && !displayedMainMenu) {
			GuiScreen screen = new PreviewWarningGui();
			mc.displayGuiScreen(screen);
			e.setGui(screen);
			displayedMainMenu = true;
		}
	}
	
	@Override
	public KeybindingWrapper createKeybindWrapper(String keybindName) {
		
		if (allKeybindings == null) {
			initAllKeybindings();
		}
		
		KeyBinding kb = null;
		for (KeyBinding candidate : allKeybindings) {
			if (candidate.getKeyDescription().equals(keybindName)) {
				kb = candidate;
				break;
			}
		}
		
		return kb == null ? new KeybindingWrapper() : new ClientKeybindWrapper(kb);
		
	}

	@Override
	public void registerItemModels() {
		AvatarItemRenderRegister.register();
	}

	/**
	 * Finds all keybindings list via reflection. Performance-wise this is ok
	 * since only supposed to be called once, after keybindings are registered
	 */
	private void initAllKeybindings() {
		try {
			
			Field field = KeyBinding.class.getDeclaredFields()[0];
			field.setAccessible(true);
			Map<String, KeyBinding> kbMap = (Map<String, KeyBinding>) field.get(null);
			this.allKeybindings = kbMap.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
			
		} catch (Exception ex) {
			AvatarLog.error(
					"Could not load all keybindings list by using reflection. Will probably have serious problems",
					ex);
		}
	}
	
}
