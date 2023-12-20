package com.github.hanielcota.reports.menus;

import com.github.hanielcota.reports.ReportsPlugin;
import com.github.hanielcota.reports.entities.PlayerReport;
import com.github.hanielcota.reports.usecases.impl.ReportMenuUseCaseImpl;
import com.github.hanielcota.reports.utils.FastInv;
import com.github.hanielcota.reports.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ReportMenu extends FastInv {
    private final ReportMenuUseCaseImpl reportMenuUseCase;
    private final ReportsPlugin plugin;

    public ReportMenu(ReportMenuUseCaseImpl reportMenuUseCase, ReportsPlugin plugin) {
        super(6 * 9, "Reportes");
        this.reportMenuUseCase = reportMenuUseCase;
        this.plugin = plugin;
    }

    public void setReportItems(List<PlayerReport> reports) {
        int slot = 19;

        ItemStack barrier = new ItemBuilder(Material.BARRIER).setName("§cNão há reports.").build();

        for (PlayerReport report : reports) {
            if (slot >= 26 && slot <= 27) {
                slot += 2;
            }

            if (slot > 34) {
                break;
            }

            ItemStack reportItemStack = reportMenuUseCase.createReportItem(report);

            int finalSlot = slot;

            setItem(slot, reportItemStack, click -> {
                if (!click.isShiftClick()) {
                    return;
                }

                removeReport(finalSlot, report.getId(), barrier);
            });

            ++slot;
        }

        while (slot < 35) {
            if (slot != 26 && slot != 27) {
                setItem(slot, barrier);
            }
            ++slot;
        }
    }

    public void removeReport(int slot, int reportId, ItemStack item) {
        if (slot >= 0 && slot < getInventory().getSize()) {
            setItem(slot, item);
            plugin.getMySQLPlayerReportGateway().removeReport(reportId);
        }
    }
}
