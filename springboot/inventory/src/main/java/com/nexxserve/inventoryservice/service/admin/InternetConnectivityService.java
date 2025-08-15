package com.nexxserve.inventoryservice.service.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class InternetConnectivityService {

    @Value("${app.connectivity.check.urls:https://www.google.com,https://www.cloudflare.com}")
    private String[] checkUrls;

    @Value("${app.connectivity.check.timeout:5000}")
    private int connectionTimeout;

    private final AtomicBoolean internetAvailable = new AtomicBoolean(true);
    private final AtomicBoolean checking = new AtomicBoolean(false);

    public boolean isInternetAvailable() {
        return internetAvailable.get();
    }

    public void checkInternetConnectivity() {
        if (checking.compareAndSet(false, true)) {
            try {
                boolean connected = performConnectivityCheck();
                internetAvailable.set(connected);

                if (connected) {
                    log.debug("Internet connectivity confirmed");
                } else {
                    log.warn("No internet connectivity detected");
                }
            } finally {
                checking.set(false);
            }
        }
    }

    private boolean performConnectivityCheck() {
        for (String urlString : checkUrls) {
            if (checkSingleUrl(urlString.trim())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkSingleUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(connectionTimeout);
            connection.setInstanceFollowRedirects(false);

            int responseCode = connection.getResponseCode();
            connection.disconnect();

            return responseCode >= 200 && responseCode < 400;
        } catch (IOException e) {
            log.debug("Connectivity check failed for {}: {}", urlString, e.getMessage());
            return false;
        }
    }

    public void markInternetUnavailable() {
        internetAvailable.set(false);
    }

    public void markInternetAvailable() {
        internetAvailable.set(true);
    }
}