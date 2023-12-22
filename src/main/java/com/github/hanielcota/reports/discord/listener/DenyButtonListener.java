package com.github.hanielcota.reports.discord.listener;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class DenyButtonListener extends ListenerAdapter {

    private final Cache<String, Boolean> clickedDenyButtons = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build();

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String[] buttonIdParts = event.getComponentId().split(":");
        if (buttonIdParts.length != 2 || !buttonIdParts[0].equals("denyButton")) {
            return;
        }

        User user = event.getUser();
        String reportedPlayer = buttonIdParts[1];

        if (clickedDenyButtons.getIfPresent(user.getId() + ":" + reportedPlayer) != null) {
            event.reply("Já existe um staff encarregado(a) de lidar com esta denúncia.")
                    .setEphemeral(true)
                    .queue(message -> message.deleteOriginal().queueAfter(10, TimeUnit.SECONDS));
            return;
        }

        clickedDenyButtons.put(user.getId() + ":" + reportedPlayer, true);

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("DENÚNCIA NEGADA")
                .setDescription("O Staff " + user.getName().toUpperCase() + " **recusou** a denúncia do jogador "
                        + reportedPlayer + ".")
                .setColor(Color.RED)
                .setFooter("Denúncia recusada!", null);

        // Envia a mensagem formatada
        event.replyEmbeds(embedBuilder.build())
                .queue(message -> message.deleteOriginal().queueAfter(10, TimeUnit.SECONDS));
    }
}
