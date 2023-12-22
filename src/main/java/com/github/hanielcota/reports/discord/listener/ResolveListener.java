package com.github.hanielcota.reports.discord.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class ResolveListener extends ListenerAdapter {

    private final Set<Long> clickedUsers = new HashSet<>();

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String[] buttonIdParts = event.getComponentId().split(":");
        if (buttonIdParts.length != 2 || !buttonIdParts[0].equals("resolveButton")) {
            return;
        }

        User user = event.getUser();
        String reportedPlayer = buttonIdParts[1];

        if (!clickedUsers.isEmpty()) {
            event.reply("Já existe um staff encarregado(a) de lidar com esta denúncia.")
                    .queue(message -> message.deleteOriginal().queueAfter(10, java.util.concurrent.TimeUnit.SECONDS));
            return;
        }

        clickedUsers.add(user.getIdLong());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("DENÚNCIA RESOLVIDA")
                .setDescription("O Staff " + user.getName().toUpperCase() + " **resolveu** a denúncia do jogador "
                        + reportedPlayer + ".")
                .setColor(Color.GREEN)
                .setFooter("Denúncia resolvida!", null);

        event.replyEmbeds(embedBuilder.build())
                .queue(message -> message.deleteOriginal().queueAfter(10, java.util.concurrent.TimeUnit.SECONDS));
    }
}
