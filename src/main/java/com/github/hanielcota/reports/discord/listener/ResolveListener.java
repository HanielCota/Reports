package com.github.hanielcota.reports.discord.listener;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class ResolveListener extends ListenerAdapter {

    private final Cache<String, Boolean> clickedUsers = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build();

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String[] buttonIdParts = event.getComponentId().split(":");
        if (buttonIdParts.length != 2 || !buttonIdParts[0].equals("resolveButton")) {
            return;
        }

        User user = event.getUser();
        String reportedPlayer = buttonIdParts[1];

        if (clickedUsers.getIfPresent(user.getId()) != null) {
            event.reply("Já existe um staff encarregado(a) de lidar com esta denúncia.")
                    .setEphemeral(true)
                    .queue(message -> message.deleteOriginal().queueAfter(10, TimeUnit.SECONDS));
            return;
        }

        clickedUsers.put(user.getId(), true);

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("DENÚNCIA RESOLVIDA")
                .setDescription("O Staff " + user.getName().toUpperCase() + " **resolveu** a denúncia do jogador "
                        + reportedPlayer + ".")
                .setColor(Color.GREEN)
                .setFooter("Denúncia resolvida!", null);

        event.replyEmbeds(embedBuilder.build())
                .queue(message -> message.deleteOriginal().queueAfter(10, TimeUnit.SECONDS));
    }
}
