package com.github.hanielcota.reports.usecases.impl;

import java.util.List;

public class ReportUsecaseImpl {
    public List<String> getReportOptions() {
        return List.of(
                "Uso de Hacks",
                "Abuso de Bugs",
                "Publicidade não autorizada",
                "Divulgação Simples",
                "Divulgação de Comércio",
                "Comportamento Inadequado",
                "Divulgação de Comércio",
                "Ofensa à equipe/servidor",
                "Ofensa à jogador",
                "Anti-jogo",
                "Flood ou Spam",
                "Desinformação",
                "Construção Inadequada"
        );
    }
}
