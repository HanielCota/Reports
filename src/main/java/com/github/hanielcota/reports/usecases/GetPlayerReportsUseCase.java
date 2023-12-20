package com.github.hanielcota.reports.usecases;

import com.github.hanielcota.reports.entities.PlayerReport;

import java.util.List;

public interface GetPlayerReportsUseCase {

    List<PlayerReport> getPlayerReports();
}