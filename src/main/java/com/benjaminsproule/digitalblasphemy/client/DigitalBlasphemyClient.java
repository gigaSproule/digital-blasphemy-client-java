package com.benjaminsproule.digitalblasphemy.client;

import com.benjaminsproule.digitalblasphemy.client.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

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
        this.wallpaperPath = baseUrl + "/v2/core/wallpaper/";
    }

    @NonNull
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
    public Wallpaper getWallpaper(@NonNull GetWallpaperRequest getWallpaperRequest)
            throws IOException, ResponseException {
        Request request = new Request.Builder()
                .url(getWallpaperUrl(getWallpaperRequest))
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

    @NonNull
    private HttpUrl getWallpaperUrl(@NonNull GetWallpaperRequest getWallpaperRequest) {
        HttpUrl.Builder builder = requireNonNull(HttpUrl.parse(wallpaperPath))
                .newBuilder()
                .addEncodedPathSegment(String.valueOf(getWallpaperRequest.getWallpaperId()));

        if (getWallpaperRequest.getFilterResHeight() > 0) {
            builder.addQueryParameter(
                    "filter_res_height",
                    String.valueOf(getWallpaperRequest.getFilterResHeight())
            );
        }
        builder.addQueryParameter(
                "filter_res_operator",
                getWallpaperRequest.getFilterResOperator().toString()
        );
        builder.addQueryParameter(
                "filter_res_operator_height",
                getWallpaperRequest.getFilterResOperatorHeight().toString()
        );
        builder.addQueryParameter(
                "filter_res_operator_width",
                getWallpaperRequest.getFilterResOperatorWidth().toString()
        );
        if (getWallpaperRequest.getFilterResWidth() > 0) {
            builder.addQueryParameter(
                    "filter_res_width",
                    String.valueOf(getWallpaperRequest.getFilterResWidth())
            );
        }
        builder.addQueryParameter(
                "show_comments",
                String.valueOf(getWallpaperRequest.isShowComments())
        );
        builder.addQueryParameter(
                "show_pickle_jar",
                String.valueOf(getWallpaperRequest.isShowPickleJar())
        );
        builder.addQueryParameter(
                "show_resolutions",
                String.valueOf(getWallpaperRequest.isShowResolutions())
        );

        return builder.build();
    }

    public void downloadWallpaper() {
    }
}
