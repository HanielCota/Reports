package com.github.hanielcota.reports.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.github.hanielcota.reports.ReportsPlugin;
import com.github.hanielcota.reports.usecases.impl.ReportUsecaseImpl;
import com.github.hanielcota.reports.utils.ClickMessage;
import com.github.hanielcota.reports.utils.ReportUtils;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@CommandAlias("report")
@AllArgsConstructor
public class ReportCommand extends BaseCommand {

    private final ReportsPlugin plugin;
    private final ReportUsecaseImpl reportUsecase;
    private final Map<String, Long> reportCooldowns = new ConcurrentHashMap<>();

    @Default
    @CommandCompletion("@players")
    public void onCommand(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage("§cUso incorreto. Utilize: /report <nick>");
            return;
        }

        String targetNick = args[0];
        Player target = Bukkit.getPlayerExact(targetNick);

        if (target == null) {
            player.sendMessage("§cO jogador " + targetNick + " não está online ou não existe.");
            return;
        }

        if (targetNick.equalsIgnoreCase(player.getName())) {
            player.sendMessage("§cVocê não pode se reportar.");
            return;
        }

        if (isOnCooldown(player)) {
            player.sendMessage(
                    "§cVocê já reportou um jogador recentemente. Aguarde um pouco antes de reportar novamente.");
            return;
        }

        List<String> reportOptions = reportUsecase.getReportOptions();
        sendReportOptionsMessage(player, reportOptions, target);
    }

    @Subcommand("send")
    @CommandCompletion("@players")
    public void onSendCommand(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§cUso incorreto. Utilize: /report send <nick> <motivo>");
            return;
        }

        String reportedPlayerName = args[0];
        Player reportedPlayer = Bukkit.getPlayerExact(reportedPlayerName);

        if (reportedPlayer == null || !reportedPlayer.isOnline()) {
            player.sendMessage("§cO jogador " + reportedPlayerName + " não está online ou não existe.");
            return;
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (reason.isBlank()) {
            player.sendMessage("§cO motivo está vazio.");
            return;
        }

        String reporter = player.getName();

        if (reporter.equalsIgnoreCase(reportedPlayerName)) {
            player.sendMessage("§cVocê não pode se reportar.");
            return;
        }

        if (hasReportedRecently(player)) {
            player.sendMessage(
                    "",
                    "§cVocê já reportou um jogador recentemente.",
                    "§cAguarde um pouco antes de reportar novamente.",
                    "");
            return;
        }

        plugin.getMySQLPlayerReportGateway().reportPlayer(reporter, reportedPlayerName, reason);

        player.sendMessage("§aSeu relatório sobre o jogador " + reportedPlayerName + " foi enviado com sucesso.");

        reportCooldowns.put(player.getName(), System.currentTimeMillis());

        plugin.getReportHandler().sendReport(player.getName(), reportedPlayerName, reason);
    }

    private void sendReportOptionsMessage(Player player, List<String> reportOptions, Player target) {
        player.sendMessage("");

        for (String option : reportOptions) {
            ClickMessage clickMessage = new ClickMessage("").then("§c" + option);

            clickMessage.click(ClickEvent.Action.RUN_COMMAND, "/report send " + target.getName() + " " + option)
                    .tooltip("§7Clique aqui para reportar o jogador " + target.getName() + ".");

            clickMessage.send(player);
        }
        player.sendMessage("");
        player.sendMessage(
                "§cAlgumas infrações mais graves como §eRacismo/Discriminação",
                "§eComércio Externo §ce entre outras devem ser delatadas",
                "§ccom provas que seguem o padrão do servidor.");
        player.sendMessage("");

        new ClickMessage("§bClique aqui para entrar no Discord.")
                .click(ClickEvent.Action.OPEN_URL, "http://localhost")
                .send(player);

        player.sendMessage("");
    }

    private boolean isOnCooldown(Player player) {
        long cooldown = 300;
        return ReportUtils.isOnCooldown(player.getName(), reportCooldowns, cooldown);
    }

    private boolean hasReportedRecently(Player player) {
        long recentReportCooldown = 600;
        return ReportUtils.isOnCooldown(player.getName(), reportCooldowns, recentReportCooldown);
    }
}
