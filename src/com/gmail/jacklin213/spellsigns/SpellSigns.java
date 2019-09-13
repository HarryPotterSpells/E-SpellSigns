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
import com.hpspells.core.spell.Spell;
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
		if (b.getType() == Material.SIGN || b.getType() == Material.WALL_SIGN|| b.getType() == Material.LEGACY_SIGN_POST) {
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
			if (spellManager.isSpell(event.getLine(1)))
                event.setLine(1, ChatColor.YELLOW + event.getLine(1));
		}
	}
	
	@EventHandler
	public void onSignClick(PlayerInteractEvent event) {
		Action action = event.getAction();
		if (action != Action.RIGHT_CLICK_BLOCK && action != Action.LEFT_CLICK_BLOCK) {
            return;
        }
		Material material = event.getClickedBlock().getType();
		Player player = event.getPlayer();
		if (material == Material.SIGN || material == Material.WALL_SIGN || material == Material.LEGACY_SIGN_POST) {
		    Sign sign = (Sign) event.getClickedBlock().getState();
		    if (ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase("[SpellSigns]")) {
//                log.info("Line 2: " + sign.getLine(1).isEmpty()); See if spell line is empty
                if (sign.getLine(1).trim().isEmpty()) 
                    player.sendMessage(ChatColor.RED + "This sign is not assigned to any spells");
                else {
                    String spellName = ChatColor.stripColor(sign.getLine(1));
                    if (spellManager.isSpell(spellName)) {
                        Spell spell = spellManager.getSpell(spellName);
                        if (spell.playerKnows(player)) {
                           player.sendMessage(ChatColor.RED + "You already know this spell!");
                        } else {
                            spell.teach(player);
                            player.sendMessage(ChatColor.GREEN + "You have successfully learnt " + spellName);
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "This is not a valid spell");
                    }
                }
            }
		}
	}
}
