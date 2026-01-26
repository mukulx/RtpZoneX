/*
 * RtpZoneX - https://github.com/mukulx/RtpZoneX
 * Copyright (C) 2025 Mukulx
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package github.mukulx.rtpzonex.gui;

import github.mukulx.rtpzonex.RtpZoneX;
import github.mukulx.rtpzonex.zone.RtpZone;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ZoneListGUI {

    private final RtpZoneX plugin;
    private final Player player;
    public ZoneListGUI(RtpZoneX plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }
    public void open() {
        Collection<RtpZone> zones = plugin.getZoneManager().getAllZones();
        int configSize = plugin.getConfigManager().getGuiInt("zone-list.size", 27);
        int size = Math.min(configSize, 54);

        Component title = plugin.getConfigManager().colorize(
                plugin.getConfigManager().getGuiString("zone-list.title", "&6RTP Zones"));

        Inventory inv = Bukkit.createInventory(null, size, title);

        int slot = 0;
        int maxSlots = size - 9;

        for (RtpZone zone : zones) {
            if (slot >= maxSlots) {
                break;
            }
            inv.setItem(slot++, createZoneItem(zone));
        }

        inv.setItem(size - 5, createInfoItem(zones.size()));

        player.openInventory(inv);
    }
    private ItemStack createInfoItem(int totalZones) {
        String materialName = plugin.getConfigManager().getGuiString("zone-list.info-item.material", "BOOK");
        Material mat = Material.valueOf(materialName);
        String name = plugin.getConfigManager().getGuiString("zone-list.info-item.name", "&e&lZone Information");
        List<String> loreTemplate = plugin.getConfigManager().getGuiStringList("zone-list.info-item.lore");
        List<String> lore = new ArrayList<>();
        for (String line : loreTemplate) {
            lore.add(line.replace("%total%", String.valueOf(totalZones)));
        }
        return createItem(mat, name, lore.toArray(new String[0]));
    }
    private ItemStack createZoneItem(RtpZone zone) {
        String materialName = plugin.getConfigManager().getGuiString("zone-list.zone-item.material", "ENDER_PEARL");
        Material mat = Material.valueOf(materialName);

        String nameTemplate = plugin.getConfigManager().getGuiString("zone-list.zone-item.name", "&a&l%zone%");
        String name = nameTemplate.replace("%zone%", zone.getName());

        List<String> loreTemplate = plugin.getConfigManager().getGuiStringList("zone-list.zone-item.lore");
        List<String> lore = new ArrayList<>();

        String waitStatus = zone.getConfig().isWaitForPlayers() ?
                plugin.getConfigManager().getStatusEnabled() :
                plugin.getConfigManager().getStatusDisabled();

        for (String line : loreTemplate) {
            lore.add(line
                    .replace("%players%", String.valueOf(zone.getPlayerCount()))
                    .replace("%world%", zone.getConfig().getTargetWorld())
                    .replace("%wait_status%", waitStatus));
        }

        return createItem(mat, name, lore.toArray(new String[0]));
    }
    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(plugin.getConfigManager().colorize(name));

        if (lore.length > 0) {
            List<Component> loreComponents = new ArrayList<>();
            for (String line : lore) {
                loreComponents.add(plugin.getConfigManager().colorize(line));
            }
            meta.lore(loreComponents);
        }

        item.setItemMeta(meta);
        return item;
    }
}
