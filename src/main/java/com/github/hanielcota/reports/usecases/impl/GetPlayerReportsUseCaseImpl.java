package com.github.hanielcota.reports.usecases.impl;

import com.github.hanielcota.reports.entities.PlayerReport;
import com.github.hanielcota.reports.gateways.PlayerReportGateway;
import com.github.hanielcota.reports.usecases.GetPlayerReportsUseCase;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class GetPlayerReportsUseCaseImpl implements GetPlayerReportsUseCase {

    private final PlayerReportGateway playerReportGateway;

    @Override
    public List<PlayerReport> getPlayerReports() {
        return playerReportGateway.getAllPlayerReports();
    }
}