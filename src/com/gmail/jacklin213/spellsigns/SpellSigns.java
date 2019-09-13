package com.gmail.jacklin213.spellsigns;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.hpspells.core.api.APIHandler;
import com.hpspells.core.extension.Extension;
import com.hpspells.core.spell.SpellManager;

public class SpellSigns extends Extension {
	
	Logger log;
	APIHandler API;
	SpellManager spellManager;
	
	@Override
	public void onEnable() {
		log = getLogger();
		API = APIHandler.getInstance();
		spellManager = APIHandler.getSpellManager();
	
		log.info("Version: " + this.getVersion() + " has been enabled!");
	}
	
	@EventHandler
	public void onSignDestroy(BlockBreakEvent event) {
		BlockFace[] bf = new BlockFace[] {
			    BlockFace.EAST,
			    BlockFace.SOUTH,
			    BlockFace.WEST,
			    BlockFace.NORTH
		};
		Block b;
		for (int i = 3; i > 0; i--) {
			b = event.getBlock().getRelative(bf[(i)]);
			if (b.getType() == Material.WALL_SIGN) {
				//block has a sign on it, don't break!
				event.setCancelled(true);
			}
		}
		b = event.getBlock();
		if (b.getType() == Material.SIGN || b.getType() == Material.LEGACY_SIGN_POST) {
			if (event.getPlayer().hasPermission("spellsigns.destroy")) {
				event.getPlayer().sendMessage(ChatColor.YELLOW + "SpellSign is destroyed");
			} else {
				event.getPlayer().sendMessage(ChatColor.RED + "You do not have the permission to destory this sign");
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onSignCreate(SignChangeEvent event) {
		if (event.getLine(0).equalsIgnoreCase("spellsigns") 
				|| event.getLine(0).equalsIgnoreCase("[spellsigns]")) {
			event.setLine(0, ChatColor.BLUE + "[SpellSigns]");
			if (event.getLines().length > 1) {
				if (spellManager.getSpell(event.getLine(1)) != null)
					event.setLine(1, ChatColor.YELLOW + event.getLine(1));
			}
		}
	}
	
	@EventHandler
	public void onSignClick(PlayerInteractEvent event) {
		Material material = event.getMaterial();
		Action action = event.getAction();
		Player player = event.getPlayer();
		if ((action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK)
				&& (material == Material.SIGN || material == Material.LEGACY_SIGN_POST || material == Material.WALL_SIGN) 
				&& event.getClickedBlock().getState() instanceof Sign) {
			Sign sign = (Sign) event.getClickedBlock().getState();
			if (sign.getLine(0).equalsIgnoreCase("[SpellSigns]")) {
				if (sign.getLines().length == 1) 
					player.sendMessage(ChatColor.RED + "This sign is not assigned to any spells");
				else if (sign.getLines().length == 2) {
					if (spellManager.getSpell(sign.getLine(1)) != null) {
						player.sendMessage("Yay this spell exists");
					} else {
						player.sendMessage(ChatColor.RED + "This is not a valid spell");
					}
				}
			}
		}
	}
}
