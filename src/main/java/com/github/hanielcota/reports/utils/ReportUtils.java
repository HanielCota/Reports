package com.github.hanielcota.reports.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportUtils {

    public static boolean isOnCooldown(String playerName, Map<String, Long> cooldowns, long cooldownSeconds) {
        if (cooldowns.containsKey(playerName)) {
            long lastReportTime = cooldowns.get(playerName);
            long currentTime = System.currentTimeMillis();
            long elapsedTime = (currentTime - lastReportTime) / 1000; 

            return elapsedTime < cooldownSeconds;
        }
        return false;
    }
}
