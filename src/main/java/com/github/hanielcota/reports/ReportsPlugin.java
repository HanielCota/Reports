package com.github.hanielcota.reports;

import co.aikar.commands.PaperCommandManager;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.hanielcota.reports.adapters.MySQLPlayerReportGateway;
import com.github.hanielcota.reports.commands.ReportCommand;
import com.github.hanielcota.reports.commands.ReportsCommand;
import com.github.hanielcota.reports.discord.DiscordReportHandler;
import com.github.hanielcota.reports.discord.DiscordToken;
import com.github.hanielcota.reports.discord.listener.DenyButtonListener;
import com.github.hanielcota.reports.discord.listener.TakeButtonListener;
import com.github.hanielcota.reports.discord.listener.ResolveListener;
import com.github.hanielcota.reports.entities.PlayerReport;
import com.github.hanielcota.reports.usecases.impl.ReportMenuUseCaseImpl;
import com.github.hanielcota.reports.usecases.impl.ReportUsecaseImpl;
import com.github.hanielcota.reports.utils.FastInvManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

@Getter
@Slf4j
public final class ReportsPlugin extends JavaPlugin {

    private DiscordReportHandler reportHandler;
    private TextChannel staffChannel;
    private DiscordToken discordToken;
    private JDA jda;
    private String jdbcUrl;
    private String username;
    private String password;
    private MySQLPlayerReportGateway mySQLPlayerReportGateway;
    private ReportUsecaseImpl reportUsecase;
    private ReportMenuUseCaseImpl reportMenuUseCase;
    private PlayerReport playerReport;

    @Override
    public void onEnable() {
        setupConfig();
        loadDatabaseConfig();
        validateConfig();
        initializeDatabaseGateway();
        initDiscordBot();
        initializePlayerReports();
        registerCommands();
        registerListeners();
    }

    private void setupConfig() {
        saveDefaultConfig();
    }

    private void loadDatabaseConfig() {
        FileConfiguration config = getConfig();
        jdbcUrl = config.getString("database.jdbcUrl");
        username = config.getString("database.username");
        password = config.getString("database.password");
    }

    private void validateConfig() {
        if (isConfigInvalid()) {
            handleInvalidConfig();
        }
    }

    private boolean isConfigInvalid() {
        return jdbcUrl == null || username == null || password == null;
    }

    private void handleInvalidConfig() {
        getLogger().severe("Invalid configuration. Please check your config.yml file.");
        getServer().getPluginManager().disablePlugin(this);
        throw new IllegalStateException("Invalid configuration");
    }

    private void initializeDatabaseGateway() {
        mySQLPlayerReportGateway = new MySQLPlayerReportGateway(jdbcUrl, username, password);
        reportUsecase = new ReportUsecaseImpl();

        reportMenuUseCase = new ReportMenuUseCaseImpl(
                Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS)
                .build());
    }

    private void initializePlayerReports() {
        playerReport = new PlayerReport();
    }

    private void registerListeners() {
        FastInvManager.register(this);
    }

    private void registerCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new ReportsCommand(this));
        commandManager.registerCommand(new ReportCommand(this, reportUsecase));
    }

    private void initDiscordBot() {
        discordToken = new DiscordToken();

        try {
            jda = JDABuilder.createDefault(discordToken.getTokenDiscord()).build();
            jda.awaitReady();
            jda.addEventListener(new ResolveListener());
            jda.addEventListener(new TakeButtonListener());
            jda.addEventListener(new DenyButtonListener());

            staffChannel = jda.getTextChannelById("1187528705308893284");
            reportHandler = new DiscordReportHandler(staffChannel);

            getLogger().info("Discord bot initialized successfully!");
        } catch (Exception e) {
            log.error("Failed to initialize Discord bot!", e);
        }
    }
}
