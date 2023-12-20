package com.github.hanielcota.reports.usecases.impl;

import java.util.List;

public class ReportUsecaseImpl {
    public List<String> getReportOptions() {
        return List.of(
                "Uso de Hacks/Mods Proibidos",
                "Griefing",
                "Comportamento Inadequado",
                "Assédio/Discurso de Ódio",
                "Uso Excessivo de Caps Lock",
                "Uso de Bugs/Exploits",
                "Ofensa no Chat",
                "Publicidade Não Autorizada",
                "Spam no Chat",
                "Violação de Política de Construção",
                "Violação de Política de PvP",
                "Invasão de Contas",
                "Trapaça em Minigames",
                "Abuso de Poder (por membros da equipe)",
                "Roubo de Itens",
                "Violação de Política de Nome de Usuário");
    }
}
