package com.github.hanielcota.reports.usecases.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.hanielcota.reports.entities.PlayerReport;
import com.github.hanielcota.reports.menus.items.ReportItems;
import lombok.AllArgsConstructor;
import org.bukkit.inventory.ItemStack;
@AllArgsConstructor
public class ReportMenuUseCaseImpl {

    private final Cache<PlayerReport, ItemStack> reportItemCache;

    public ItemStack createReportItem(PlayerReport report) {
        return reportItemCache.get(report, key -> ReportItems.createReportsItem(report));
    }

}
