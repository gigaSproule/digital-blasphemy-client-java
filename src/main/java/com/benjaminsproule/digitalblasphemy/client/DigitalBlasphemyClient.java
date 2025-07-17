package com.benjaminsproule.digitalblasphemy.client;

import com.benjaminsproule.digitalblasphemy.client.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class DigitalBlasphemyClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient client = new OkHttpClient();

    private final String apiKey;
    private final String accountInformationPath;
    private final String wallpaperPath;

    public DigitalBlasphemyClient(String apiKey) {
        this(apiKey, "https://api.digitalblasphemy.com");
    }

    DigitalBlasphemyClient(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.accountInformationPath = baseUrl + "/v2/core/account";
        this.wallpaperPath = baseUrl + "/v2/core/wallpaper";
    }

    @NotNull
    public GetAccountInformationResponse getAccountInformation() throws IOException, ResponseException {
        Request request = new Request.Builder()
                .url(accountInformationPath)
                .header("Authorization", "Bearer " + apiKey)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            if (response.isSuccessful()) {
                return objectMapper.readValue(body, GetAccountInformationResponse.class);
            }
            try {
                ResponseError responseError = objectMapper.readValue(body, ResponseError.class);
                throw new ResponseException(responseError);
            } catch (JsonProcessingException exception) {
                throw new ResponseException(0, "Unable to parse the body as JSON ErrorResponse. [" + body + "]");
            }
        }
    }

    public GetWallpapersResponse getWallpapers() {
        return null;
    }

    @Nullable
    public Wallpaper getWallpaper(GetWallpaperRequest getWallpaperRequest) throws IOException, ResponseException {
        Request request = new Request.Builder()
                .url(wallpaperPath)
                .header("Authorization", "Bearer " + apiKey)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            if (response.isSuccessful()) {
                GetWallpaperResponse wallpaperResponse = objectMapper.readValue(body, GetWallpaperResponse.class);
                return wallpaperResponse.wallpaper();
            }
            try {
                ResponseError responseError = objectMapper.readValue(body, ResponseError.class);
                throw new ResponseException(responseError);
            } catch (JsonProcessingException exception) {
                throw new ResponseException(0, "Unable to parse the body as JSON ErrorResponse. [" + body + "]");
            }
        }
    }

    public void downloadWallpaper() {
    }
}
