package com.github.hanielcota.reports.discord;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

@AllArgsConstructor
public class DiscordReportHandler extends ListenerAdapter {

    private final TextChannel staffChannel;

    public void sendReport(String reporter, String reportedPlayer, String reason) {
        validateInputs(reporter, reportedPlayer, reason);

        String playerHeadUrl = "https://minotar.net/helm/" + reportedPlayer + "/100.png";

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("**JOGADOR DENÚNCIADO**")
                .setDescription("Um novo jogador foi denunciado, pegue a denúncia e faça o possível para solucioná-la. "
                        + "Você só poderá pegar uma nova denúncia após resolver essa. "
                        + "A denúncia só pode ser pega se você estiver online no servidor.")
                .addField("Delator:", reporter, false)
                .addField("Jogador denunciado:", reportedPlayer, false)
                .addField("Motivo", reason, false)
                .setColor(0xFF0000)
                .setImage(playerHeadUrl);

        // Adicionar botão de interação para pegar a denúncia
        Button takeButton = Button.primary("takeButton:" + reportedPlayer, "Pegar Denúncia")
                .withDisabled(false)
                .withEmoji(Emoji.fromUnicode("➡️"));

        // Adicionar botão de interação para negar a denúncia
        Button denyButton = Button.primary("denyButton:" + reportedPlayer, "Negar Denúncia")
                .withDisabled(false)
                .withEmoji(Emoji.fromUnicode("❌"));

        // Adicionar botão de interação para marcar a denúncia como resolvida
        Button resolveButton = Button.primary("resolveButton:" + reportedPlayer, "Resolver Denúncia")
                .withDisabled(false)
                .withEmoji(Emoji.fromUnicode("✅"));

        // Enviar a mensagem com os botões de interação
        staffChannel.sendMessageEmbeds(embedBuilder.build())
                .setComponents(ActionRow.of(takeButton.asEnabled(), resolveButton.asEnabled(), denyButton.asEnabled()))
                .queue();
    }

    private void validateInputs(String reporter, String reportedPlayer, String reason) {
        assert reporter != null && reportedPlayer != null && reason != null
                : "Reporter, Reported Player, and Reason cannot be null";
    }
}
