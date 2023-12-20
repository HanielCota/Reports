package com.github.hanielcota.reports.adapters;

import com.github.hanielcota.reports.events.ReportEvent;
import com.github.hanielcota.reports.usecases.impl.ReportService;
import org.bukkit.Bukkit;
public class BukkitReportService implements ReportService {

    @Override
    public void processReport(String reportedPlayer, String reason) {
        // Adicione logs de depuração
        Bukkit.getLogger().info("Processando relatório para o jogador: " + reportedPlayer + " Razão: " + reason);

        ReportEvent reportEvent = new ReportEvent(reportedPlayer, reason);

        // Aciona o evento
        Bukkit.getPluginManager().callEvent(reportEvent);

        // Adicione logs de depuração
        Bukkit.getLogger().info("Evento de relatório chamado com sucesso");
    }
}