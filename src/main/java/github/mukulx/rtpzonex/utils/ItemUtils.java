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
package github.mukulx.rtpzonex.utils;

import github.mukulx.rtpzonex.RtpZoneX;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

    private static final String WAND_NBT_KEY = "RtpZoneX_zone_wand";

    public static ItemStack createZoneWand(RtpZoneX plugin) {
        Material material = plugin.getConfigManager().getWandMaterial();
        ItemStack wand = new ItemStack(material);
        ItemMeta meta = wand.getItemMeta();

        if (meta != null) {
            String name = plugin.getConfigManager().getWandName();
            meta.displayName(plugin.getConfigManager().colorize(name));

            List<Component> lore = new ArrayList<>();
            for (String line : plugin.getConfigManager().getWandLore()) {
                lore.add(plugin.getConfigManager().colorize(line));
            }
            meta.lore(lore);

            if (plugin.getConfigManager().isWandGlowing()) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            meta.setCustomModelData(9999);
            wand.setItemMeta(meta);
        }

        return wand;
    }

    public static boolean isZoneWand(RtpZoneX plugin, ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        if (item.getType() != plugin.getConfigManager().getWandMaterial()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        return meta.hasCustomModelData() && meta.getCustomModelData() == 9999;
    }
}
