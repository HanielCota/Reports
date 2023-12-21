package com.github.hanielcota.reports.menus.view;

import com.github.hanielcota.reports.ReportsPlugin;
import com.github.hanielcota.reports.entities.PlayerReport;
import com.github.hanielcota.reports.menus.ReportMenu;
import com.github.hanielcota.reports.menus.items.ReportItems;
import org.bukkit.entity.Player;

import java.util.List;

public class ReportView {

    public void createReportMenu(Player player, Player target, ReportsPlugin plugin) {
        ReportMenu menu = new ReportMenu(plugin.getReportMenuUseCase(), plugin);

        menu.setItem(4, ReportItems.createPlayerSkull(target));
        menu.setItem(45, ReportItems.createBackItem(), click -> player.closeInventory());

        List<PlayerReport> topPlayerReports = plugin.getMySQLPlayerReportGateway().getLimitedPlayerReports(target.getName(), 14);

        menu.setReportItems(topPlayerReports);

        menu.open(player);
    }
}