package com.github.hanielcota.reports.adapters;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.hanielcota.reports.entities.PlayerReport;
import com.github.hanielcota.reports.gateways.PlayerReportGateway;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MySQLPlayerReportGateway implements PlayerReportGateway {

    private final HikariDataSource dataSource;
    private final Cache<String, List<PlayerReport>> playerReportsCache;

    public MySQLPlayerReportGateway(String jdbcUrl, String username, String password) {
        this.dataSource = createDataSource(jdbcUrl, username, password);
        this.playerReportsCache =
                Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();
        createPlayerReportsTable();
    }

    private HikariDataSource createDataSource(String jdbcUrl, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(config);
    }

    @Override
    public List<PlayerReport> getAllPlayerReports() {
        String cacheKey = "getAllPlayerReports";
        List<PlayerReport> cachedResult = playerReportsCache.getIfPresent(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }

        List<PlayerReport> result = getPlayerReports("SELECT * FROM player_reports");
        playerReportsCache.put(cacheKey, result);
        return result;
    }

    @Override
    public List<PlayerReport> getLimitedPlayerReports(String nick, int limit) {
        String query =
                "SELECT id, nick, timestamp, role, reported, reason, online FROM player_reports WHERE nick = ? LIMIT ?";
        return limitedPlayerReports(query, nick, limit);
    }

    @Override
    public List<PlayerReport> getPlayerReportsByNick(String nick) {
        String cacheKey = "getPlayerReportsByNick" + nick;
        List<PlayerReport> cachedResult = playerReportsCache.getIfPresent(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }

        List<PlayerReport> result = getPlayerReports("SELECT nick, timestamp, role, online FROM player_reports WHERE nick = ?", nick);
        playerReportsCache.put(cacheKey, result);
        return result;
    }

    @Override
    public void removeReport(int reportId) {
        playerReportsCache.invalidateAll();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("DELETE FROM player_reports WHERE id = ?")) {

            statement.setInt(1, reportId);
            statement.executeUpdate();

        } catch (SQLException e) {
            log.error("Error removing report", e);
        }
    }

    @Override
    public void reportPlayer(String reporter, String reportedPlayer, String reason) {
        playerReportsCache.invalidateAll();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO player_reports (nick, timestamp, role, online, reported, reason) VALUES (?, ?, ?, ?, ?, ?)")) {

            statement.setString(1, reportedPlayer);
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(3, "Cargo");
            statement.setString(4, reportedPlayer);
            statement.setString(5, reporter);
            statement.setString(6, reason);

            statement.executeUpdate();

        } catch (SQLException e) {
            log.error("Error reporting player", e);
        }
    }

    private List<PlayerReport> limitedPlayerReports(String query, String nick, int limit) {
        String cacheKey = "limitedPlayerReports:" + query + ":" + nick + ":" + limit;

        List<PlayerReport> cachedResult = playerReportsCache.getIfPresent(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }

        List<PlayerReport> playerReports = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, nick);
            statement.setInt(2, limit);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                PlayerReport playerReport = mapResultSetToPlayerReport(resultSet);
                playerReports.add(playerReport);
            }
        } catch (SQLException e) {
            log.error("Error retrieving limited player reports", e);
        }

        playerReportsCache.put(cacheKey, playerReports);
        return playerReports;
    }

    private List<PlayerReport> getPlayerReports(String query, String... parameters) {
        String cacheKey = query + Arrays.toString(parameters);
        List<PlayerReport> cachedResult = playerReportsCache.getIfPresent(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }

        List<PlayerReport> result = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            for (int i = 0; i < parameters.length; i++) {
                statement.setString(i + 1, parameters[i]);
            }

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                PlayerReport playerReport = mapResultSetToPlayerReport(resultSet);
                result.add(playerReport);
            }
        } catch (SQLException e) {
            log.error("Error retrieving player reports", e);
        }

        playerReportsCache.put(cacheKey, result);
        return result;
    }

    private PlayerReport mapResultSetToPlayerReport(ResultSet resultSet) throws SQLException {
        PlayerReport playerReport = new PlayerReport();
        playerReport.setId(resultSet.getInt("id"));
        playerReport.setNick(resultSet.getString("nick"));
        playerReport.setTimestamp(resultSet.getTimestamp("timestamp").toLocalDateTime());
        playerReport.setRole(resultSet.getString("role"));
        playerReport.setOnline(resultSet.getString("online"));

        String reportedBy = resultSet.getString("reported");
        String reason = resultSet.getString("reason");

        playerReport.setReportedBy(reportedBy != null ? reportedBy : "Sem informação");
        playerReport.setReason(reason != null ? reason : "Sem informação");

        return playerReport;
    }

    public void createPlayerReportsTable() {
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {

            String createTableQuery =
                    "CREATE TABLE IF NOT EXISTS player_reports (" + "id INT AUTO_INCREMENT PRIMARY KEY,"
                            + "nick VARCHAR(255) NOT NULL,"
                            + "timestamp TIMESTAMP NOT NULL,"
                            + "role VARCHAR(255) NOT NULL,"
                            + "online VARCHAR(255) NOT NULL,"
                            + "reported VARCHAR(255) NOT NULL,"
                            + "reason VARCHAR(255) NOT NULL"
                            + ")";

            statement.executeUpdate(createTableQuery);

        } catch (SQLException e) {
            log.error("Error creating player_reports table", e);
        }
    }
}