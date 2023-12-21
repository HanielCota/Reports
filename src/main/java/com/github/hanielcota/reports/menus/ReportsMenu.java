package com.github.hanielcota.reports.menus;

import com.github.hanielcota.reports.ReportsPlugin;
import com.github.hanielcota.reports.entities.PlayerReport;
import com.github.hanielcota.reports.menus.items.ReportItems;
import com.github.hanielcota.reports.utils.FastInv;
import com.github.hanielcota.reports.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ReportsMenu extends FastInv {

    private final ReportsPlugin plugin;

    public ReportsMenu(Player player, ReportsPlugin plugin) {
        super(6 * 9, "Reportes globais");
        this.plugin = plugin;

        setReportItems(player, plugin.getMySQLPlayerReportGateway().getAllPlayerReports());
    }

    public void setReportItems(Player player, List<PlayerReport> reports) {
        int slot = 19;

        setItem(45, ReportItems.createBackItem(), click -> player.closeInventory());
        ItemStack barrier = new ItemBuilder(Material.BARRIER).setName("§cNão há reports.").build();

        if (reports.isEmpty()) {
            setItem(22, barrier);
            return;
        }

        for (PlayerReport report : reports) {
            if (slot >= 26 && slot <= 27) {
                slot += 2;
            }

            if (slot > 34) {
                return;
            }

            ItemStack reportItem = ReportItems.createGlobalReports(report);
            if (reportItem.getType() == Material.AIR) {
                continue;
            }

            setItem(slot, reportItem, click -> {
                boolean isShiftClick = click.isShiftClick();

                if (isShiftClick) {
                    removeReport(click.getSlot(), report.getId(), barrier);
                    player.sendMessage("§aRelatório removido com sucesso.");
                    return;
                }

                handleTeleportClick(player, report);
            });

            ++slot;
        }
    }



    private void handleTeleportClick(Player player, PlayerReport report) {
        Player targetPlayer = Bukkit.getPlayerExact(report.getNick());

        if (targetPlayer == null || !targetPlayer.isOnline()) {
            player.sendMessage("§cO jogador " + report.getNick() + " não está online.");
            return;
        }

        player.teleport(targetPlayer.getLocation());
        player.sendMessage("§aTeleportado para " + report.getNick() + ".");
    }

    public void removeReport(int slot, int reportId, ItemStack item) {
        if (!isValidSlot(slot)) {
            return;
        }

        setItem(slot, item);
        plugin.getMySQLPlayerReportGateway().removeReport(reportId);
    }

    private boolean isValidSlot(int slot) {
        return slot >= 0 && slot < getInventory().getSize();
    }
}
