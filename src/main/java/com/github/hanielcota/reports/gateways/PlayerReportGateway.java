package com.github.hanielcota.reports.gateways;

import com.github.hanielcota.reports.entities.PlayerReport;

import java.util.List;

public interface PlayerReportGateway {
    List<PlayerReport> getAllPlayerReports();

    List<PlayerReport> getLimitedPlayerReports(String nick, int limit);

    List<PlayerReport> getPlayerReportsByNick(String nick);

    void removeReport(int reportId);

    void reportPlayer(String reporter, String reportedPlayer, String reason);
}