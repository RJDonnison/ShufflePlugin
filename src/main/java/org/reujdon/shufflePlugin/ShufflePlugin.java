package org.reujdon.shufflePlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class ShufflePlugin extends JavaPlugin implements Listener {

    private final Set<Player> activeShuffling = new HashSet<>();

    @Override
    public void onEnable() {
        getLogger().info("ShufflePlugin enabled!");
        Bukkit.getPluginManager().registerEvents(this, this);

        getCommand("shuffletoggle").setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }
            if (activeShuffling.contains(player)) {
                activeShuffling.remove(player);
                player.sendActionBar(Component.text("§aHotbar shuffle disabled!", NamedTextColor.RED));
            } else {
                activeShuffling.add(player);
                player.sendActionBar(Component.text("§aHotbar shuffle enabled!", NamedTextColor.GREEN));
            }
            return true;
        });
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!activeShuffling.contains(player)) return;

        List<Integer> blockSlots = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType().isBlock() && !item.getType().isAir()) {
                blockSlots.add(i);
            }
        }

        if (blockSlots.isEmpty()) {
            activeShuffling.remove(player);
            player.sendActionBar(Component.text("§cNo block items in your hotbar to switch to!\nShuffle disabled!", NamedTextColor.RED));
            return;
        }

        Random random = new Random();
        int randomIndex = random.nextInt(blockSlots.size());
        int chosenSlot = blockSlots.get(randomIndex);

        player.getInventory().setHeldItemSlot(chosenSlot);
    }
}
