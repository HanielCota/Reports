package com.github.hanielcota.reports.menus.items;

import com.github.hanielcota.reports.entities.PlayerReport;
import com.github.hanielcota.reports.utils.ItemBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportItems {

    public static ItemStack createPlayerSkull(Player player) {
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

    public static ItemStack createBackItem() {
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

    public static ItemStack createGlobalReports(PlayerReport report) {
        if (report == null || report.getNick() == null) {
            return new ItemStack(Material.AIR);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        boolean isTargetOnline = isTargetOnline(report.getNick());
        String onlineStatus = isTargetOnline ? "§aOnline" : "§cOffline";

        return new ItemBuilder(Material.PLAYER_HEAD)
                .setSkullOwner(report.getNick())
                .setName((isTargetOnline ? "§a" : "§c") + report.getNick())
                .setLore(
                        "",
                        "§7ID: §f" + report.getId(),
                        "§7Data e Hora: §f" + report.getTimestamp().format(formatter),
                        "§7Atividade Atual: §fDesenvolver",
                        "§7Motivo: §f" + report.getReason(),
                        "§7Status: " + onlineStatus,
                        "",
                        "§aClique para teletransportar para o jogador.",
                        "§bOu",
                        "§e(Clique com Shift e Click esquerdo para remover o report)")
                .build();
    }

    public static boolean isTargetOnline(String nick) {
        Player target = Bukkit.getPlayerExact(nick);
        return target != null && target.isOnline();
    }

    private static String formatTimestamp(LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter);
    }
}