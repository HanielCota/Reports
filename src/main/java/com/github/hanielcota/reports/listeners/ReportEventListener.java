package com.github.hanielcota.reports.listeners;

import com.github.hanielcota.reports.events.ReportEvent;
import com.github.hanielcota.reports.usecases.impl.ReportService;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class ReportEventListener implements Listener {

    private final ReportService reportService;

    @EventHandler
    public void onReport(ReportEvent event) {
        reportService.processReport(event.getReportedPlayer(), event.getReason());

        List<String> onlineAdmins = Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("report.admin"))
                .map(Player::getName)
                .toList();

        if (onlineAdmins.isEmpty()) {
            return;
        }

        String reportMessage = ChatColor.GREEN + "[Relatório] " + ChatColor.YELLOW
                + "Jogador: " + event.getReportedPlayer() + ChatColor.YELLOW
                + ", Motivo: " + event.getReason();

        sendReportMessageToAdmins(onlineAdmins, reportMessage);
    }

    private void sendReportMessageToAdmins(List<String> onlineAdmins, String reportMessage) {
        onlineAdmins.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player -> player.sendMessage(reportMessage));
    }
}
