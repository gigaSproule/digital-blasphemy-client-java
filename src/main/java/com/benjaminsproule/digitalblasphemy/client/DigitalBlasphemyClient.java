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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class DigitalBlasphemyClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient client = new OkHttpClient();

    private final String apiKey;
    private final String accountInformationPath;
    private final String wallpapersPath;
    private final String wallpaperPath;
    private final String downloadWallpaperPath;

    public DigitalBlasphemyClient(String apiKey) {
        this(apiKey, "https://api.digitalblasphemy.com");
    }

    DigitalBlasphemyClient(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.accountInformationPath = baseUrl + "/v2/core/account";
        this.wallpapersPath = baseUrl + "/v2/core/wallpapers";
        this.wallpaperPath = baseUrl + "/v2/core/wallpaper/";
        this.downloadWallpaperPath = baseUrl + "/v2/core/download/wallpaper/";
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

    public GetWallpapersResponse getWallpapers(@NonNull GetWallpapersRequest getWallpapersRequest) throws IOException, ResponseException {
        Request request = new Request.Builder()
                .url(getWallpapersUrl(getWallpapersRequest))
                .header("Authorization", "Bearer " + apiKey)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            if (response.isSuccessful()) {
                return objectMapper.readValue(body, GetWallpapersResponse.class);
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
    private HttpUrl getWallpapersUrl(@NonNull GetWallpapersRequest getWallpapersRequest) {
        HttpUrl.Builder builder = requireNonNull(HttpUrl.parse(wallpapersPath))
                .newBuilder();

        if (getWallpapersRequest.getFilterDateDay() != 0) {
            builder.addQueryParameter("filter_date_day", String.valueOf(getWallpapersRequest.getFilterDateDay()));
        }
        if (getWallpapersRequest.getFilterDateMonth() != 0) {
            builder.addQueryParameter("filter_date_month", String.valueOf(getWallpapersRequest.getFilterDateMonth()));

        }
        if (getWallpapersRequest.getFilterDateYear() != 0) {
            builder.addQueryParameter("filter_date_year", String.valueOf(getWallpapersRequest.getFilterDateYear()));
        }
        builder.addQueryParameter("filter_date_operator", getWallpapersRequest.getFilterDateOperator().toString());
        getWallpapersRequest.getFilterGallery().forEach(gallery -> builder.addQueryParameter("filter_gallery", gallery.toString()));
        if (getWallpapersRequest.getFilterRating() != 0) {
            builder.addQueryParameter("filter_rating", String.valueOf(getWallpapersRequest.getFilterRating()));
        }
        builder.addQueryParameter("filter_rating_operator", getWallpapersRequest.getFilterRatingOperator().toString());
        if (getWallpapersRequest.getFilterResHeight() != 0) {
            builder.addQueryParameter("filter_res_height", String.valueOf(getWallpapersRequest.getFilterResHeight()));
        }
        builder.addQueryParameter("filter_res_operator", getWallpapersRequest.getFilterResOperator().toString());
        builder.addQueryParameter("filter_res_operator_height", getWallpapersRequest.getFilterResOperatorHeight().toString());
        builder.addQueryParameter("filter_res_operator_width", getWallpapersRequest.getFilterResOperatorWidth().toString());
        if (getWallpapersRequest.getFilterResWidth() != 0) {
            builder.addQueryParameter("filter_res_width", String.valueOf(getWallpapersRequest.getFilterResWidth()));
        }
        getWallpapersRequest.getFilterTag().forEach(tag -> builder.addQueryParameter("filter_tag", tag.toString()));
        builder.addQueryParameter("limit", String.valueOf(getWallpapersRequest.getLimit()));
        builder.addQueryParameter("order", getWallpapersRequest.getOrder().toString());
        if (!getWallpapersRequest.getOrderBy().equals(GetWallpapersOrderBy.DATE)) {
            builder.addQueryParameter("order_by", getWallpapersRequest.getOrderBy().toString());
        }
        builder.addQueryParameter("page", String.valueOf(getWallpapersRequest.getPage()));
        if (!getWallpapersRequest.getS().isEmpty()) {
            builder.addQueryParameter("s", getWallpapersRequest.getS());
        }
        builder.addQueryParameter("show_comments", String.valueOf(getWallpapersRequest.isShowComments()));
        builder.addQueryParameter("show_pickle_jar", String.valueOf(getWallpapersRequest.isShowPickleJar()));
        builder.addQueryParameter("show_resolutions", String.valueOf(getWallpapersRequest.isShowResolutions()));

        return builder.build();
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
                GetWallpaperResponse getWallpaperResponse = objectMapper.readValue(body, GetWallpaperResponse.class);
                return getWallpaperResponse.wallpaper();
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

    public void downloadWallpaper(String filename, DownloadWallpaperRequest downloadWallpaperRequest) throws IOException, ResponseException {
        Request request = new Request.Builder()
                .url(downloadUrl(downloadWallpaperRequest))
                .header("Authorization", "Bearer " + apiKey)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            if (response.isSuccessful()) {
                DownloadWallpaperResponse downloadWallpaperResponse = objectMapper.readValue(body, DownloadWallpaperResponse.class);
                Request fileRequest = new Request.Builder()
                        .url(downloadWallpaperResponse.download().url())
                        .header("Authorization", "Bearer " + apiKey)
                        .get()
                        .build();
                try (Response fileResponse = client.newCall(fileRequest).execute()) {
                    if (fileResponse.isSuccessful()) {
                        Files.write(Path.of(filename), fileResponse.body().bytes());
                        return;
                    }
                    String fileResponseBody = fileResponse.body().string();
                    if (fileResponse.code() == 404) {
                        throw new ResponseException(404, "Not Found", List.of(fileResponseBody));
                    }
                    try {
                        ResponseError responseError = objectMapper.readValue(fileResponseBody, ResponseError.class);
                        throw new ResponseException(responseError);
                    } catch (JsonProcessingException exception) {
                        throw new ResponseException(0, "Unable to parse the body as JSON ErrorResponse. [" + fileResponseBody + "]");
                    }
                }
            }
            try {
                ResponseError responseError = objectMapper.readValue(body, ResponseError.class);
                throw new ResponseException(responseError);
            } catch (JsonProcessingException exception) {
                throw new ResponseException(0, "Unable to parse the body as JSON ErrorResponse. [" + body + "]");
            }
        }
    }

    private HttpUrl downloadUrl(DownloadWallpaperRequest downloadWallpaperRequest) {
        HttpUrl.Builder builder = requireNonNull(HttpUrl.parse(downloadWallpaperPath))
                .newBuilder()
                .addEncodedPathSegment(downloadWallpaperRequest.getType().toString())
                .addEncodedPathSegment(String.valueOf(downloadWallpaperRequest.getWidth()))
                .addEncodedPathSegment(String.valueOf(downloadWallpaperRequest.getHeight()))
                .addEncodedPathSegment(String.valueOf(downloadWallpaperRequest.getWallpaperId()))
                .addQueryParameter("show_watermark", String.valueOf(downloadWallpaperRequest.isShowWatermark()));
        return builder.build();
    }
}
