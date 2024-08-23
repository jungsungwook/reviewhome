package com.memeki.reviewhome.scheduler.service;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.memeki.reviewhome.geo.service.GeoFeaturesService;
import com.memeki.reviewhome.historyManager.entity.UpdateHistory;
import com.memeki.reviewhome.historyManager.service.HistoryManagerService;

@Service
public class SchedulerService {

    private final GeoFeaturesService geoFeaturesService;
    private final HistoryManagerService historyManagerService;

    public SchedulerService(
            GeoFeaturesService geoFeaturesService,
            HistoryManagerService historyManagerService) {
        this.geoFeaturesService = geoFeaturesService;
        this.historyManagerService = historyManagerService;
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void updateGeoFeaturesScheduler() {
        UpdateHistory updateHistory = historyManagerService.getRecentUpdateHistory("geoFeatures");
        if(updateHistory != null && updateHistory.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(1))) {
            return;
        }
        geoFeaturesService.updateGeoFeatures();
    }
}
