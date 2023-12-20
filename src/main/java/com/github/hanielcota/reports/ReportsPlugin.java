package com.github.hanielcota.reports;

import co.aikar.commands.PaperCommandManager;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.hanielcota.reports.adapters.BukkitReportService;
import com.github.hanielcota.reports.adapters.MySQLPlayerReportGateway;
import com.github.hanielcota.reports.commands.ReportCommand;
import com.github.hanielcota.reports.commands.ReportsCommand;
import com.github.hanielcota.reports.listeners.ReportEventListener;
import com.github.hanielcota.reports.usecases.impl.ReportMenuUseCaseImpl;
import com.github.hanielcota.reports.usecases.impl.ReportService;
import com.github.hanielcota.reports.usecases.impl.ReportUsecaseImpl;
import com.github.hanielcota.reports.utils.FastInvManager;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

@Getter
public final class ReportsPlugin extends JavaPlugin {

    private String jdbcUrl;
    private String username;
    private String password;
    private MySQLPlayerReportGateway mySQLPlayerReportGateway;
    private ReportUsecaseImpl reportUsecase;
    private ReportMenuUseCaseImpl reportMenuUseCase;
    private ReportService reportService;

    @Override
    public void onEnable() {
        setupConfig();
        loadDatabaseConfig();
        validateConfig();
        initializeDatabaseGateway();
        initializeReportService();
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

        reportMenuUseCase = new ReportMenuUseCaseImpl(Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build());
    }

    private void registerListeners() {
        FastInvManager.register(this);

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new ReportEventListener(reportService), this);

    }

    private void initializeReportService() {
        this.reportService = new BukkitReportService();
    }
    private void registerCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new ReportsCommand(this));
        commandManager.registerCommand(new ReportCommand(this, reportUsecase, reportService));
    }
}
