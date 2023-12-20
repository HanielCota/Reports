package com.github.hanielcota.reports.menus.items;

import com.github.hanielcota.reports.entities.PlayerReport;
import com.github.hanielcota.reports.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportItems {

    public ItemStack createPlayerSkull(Player player) {
        return new ItemBuilder(Material.PLAYER_HEAD)
                .setName("§aPainel de Relatórios")
                .setLore(
                        "§7Examine cuidadosamente",
                        "§7os relatórios dos jogadores e",
                        "§7aplique as punições necessárias",
                        "§7de acordo com as infrações cometidas.")
                .setSkullOwner(player.getName())
                .build();
    }

    public ItemStack createBackItem() {
        return new ItemBuilder(Material.SPECTRAL_ARROW)
                .setName("§cFechar")
                .setLore("§7Clique aqui para fechar este menu.")
                .build();
    }

    public static ItemStack createReportsItem(PlayerReport report) {
        String onlineStatus = report.getOnline();
        Player target = Bukkit.getPlayerExact(onlineStatus);

        return new ItemBuilder(Material.MOJANG_BANNER_PATTERN)
                .setName("§aReportado: " + report.getNick())
                .setLore(
                        "§7ID: §7" + report.getId(),
                        "§7Timestamp: " + formatTimestamp(report.getTimestamp()),
                        "§7Role: " + report.getRole(),
                        "§7Online: " + (target != null && target.isOnline() ? "§aOnline" : "§cOffline"),
                        "§7Reportado por: " + report.getReportedBy(),
                        "§7Motivo: " + report.getReason(),
                        "",
                        "§7(Clique com Shift e Click esquerdo para remover o report)")
                .addItemFlag(ItemFlag.HIDE_ITEM_SPECIFICS)
                .build();
    }

    private static String formatTimestamp(LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter);
    }
}