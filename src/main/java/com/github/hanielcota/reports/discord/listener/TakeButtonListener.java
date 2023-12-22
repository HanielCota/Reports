package com.github.hanielcota.reports.discord.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class TakeButtonListener extends ListenerAdapter {

    private final Set<String> clickedTakeButtons = new HashSet<>();

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String[] buttonIdParts = event.getComponentId().split(":");
        if (buttonIdParts.length != 2 || !buttonIdParts[0].equals("takeButton")) {
            return;
        }

        User user = event.getUser();
        String reportedPlayer = buttonIdParts[1];

        if (!clickedTakeButtons.isEmpty()) {
            event.reply("Já existe um staff encarregado(a) de lidar com esta denúncia.")
                    .queue(message -> message.deleteOriginal().queueAfter(10, java.util.concurrent.TimeUnit.SECONDS));
            return;
        }

        clickedTakeButtons.add(user.getId() + ":" + reportedPlayer);

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("DENÚNCIA PEGA")
                .setDescription("O Staff " + user.getName().toUpperCase() + " **pegou** a denúncia do jogador "
                        + reportedPlayer + ".")
                .setColor(Color.YELLOW)
                .setFooter("Denúncia em análise!", null);

        event.replyEmbeds(embedBuilder.build())
                .queue(message -> message.deleteOriginal().queueAfter(10, java.util.concurrent.TimeUnit.SECONDS));
    }
}
