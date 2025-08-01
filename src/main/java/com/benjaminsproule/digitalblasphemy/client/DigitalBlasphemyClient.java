package com.benjaminsproule.digitalblasphemy.client;

import com.benjaminsproule.digitalblasphemy.client.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;

public class DigitalBlasphemyClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    private final String apiKey;
    private final URI accountInformationPath;
    private final URI wallpapersPath;
    private final URI wallpaperPath;
    private final URI downloadWallpaperPath;

    public DigitalBlasphemyClient(String apiKey) {
        this(apiKey, "https://api.digitalblasphemy.com");
    }

    DigitalBlasphemyClient(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.accountInformationPath = URI.create(baseUrl + "/v2/core/account");
        this.wallpapersPath = URI.create(baseUrl + "/v2/core/wallpapers");
        this.wallpaperPath = URI.create(baseUrl + "/v2/core/wallpaper/");
        this.downloadWallpaperPath = URI.create(baseUrl + "/v2/core/download/wallpaper/");
    }

    @NonNull
    public GetAccountInformationResponse getAccountInformation() throws IOException, ResponseException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(accountInformationPath)
                .GET();
        return executeRequest(request, GetAccountInformationResponse.class);
    }

    public GetWallpapersResponse getWallpapers(@NonNull GetWallpapersRequest getWallpapersRequest) throws IOException, ResponseException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(getWallpapersUrl(getWallpapersRequest))
                .GET();
        return executeRequest(request, GetWallpapersResponse.class);
    }

    @NonNull
    private URI getWallpapersUrl(@NonNull GetWallpapersRequest getWallpapersRequest) {
        List<Map.Entry<String, String>> queryParams = new ArrayList<>();

        if (getWallpapersRequest.getFilterDateDay() != 0) {
            queryParams.add(Map.entry("filter_date_day", String.valueOf(getWallpapersRequest.getFilterDateDay())));
        }
        if (getWallpapersRequest.getFilterDateMonth() != 0) {
            queryParams.add(Map.entry("filter_date_month", String.valueOf(getWallpapersRequest.getFilterDateMonth())));

        }
        if (getWallpapersRequest.getFilterDateYear() != 0) {
            queryParams.add(Map.entry("filter_date_year", String.valueOf(getWallpapersRequest.getFilterDateYear())));
        }
        queryParams.add(Map.entry("filter_date_operator", getWallpapersRequest.getFilterDateOperator().toString()));
        getWallpapersRequest.getFilterGallery().forEach(gallery -> queryParams.add(Map.entry("filter_gallery", gallery.toString())));
        if (getWallpapersRequest.getFilterRating() != 0) {
            queryParams.add(Map.entry("filter_rating", String.valueOf(getWallpapersRequest.getFilterRating())));
        }
        queryParams.add(Map.entry("filter_rating_operator", getWallpapersRequest.getFilterRatingOperator().toString()));
        if (getWallpapersRequest.getFilterResHeight() != 0) {
            queryParams.add(Map.entry("filter_res_height", String.valueOf(getWallpapersRequest.getFilterResHeight())));
        }
        queryParams.add(Map.entry("filter_res_operator", getWallpapersRequest.getFilterResOperator().toString()));
        queryParams.add(Map.entry("filter_res_operator_height", getWallpapersRequest.getFilterResOperatorHeight().toString()));
        queryParams.add(Map.entry("filter_res_operator_width", getWallpapersRequest.getFilterResOperatorWidth().toString()));
        if (getWallpapersRequest.getFilterResWidth() != 0) {
            queryParams.add(Map.entry("filter_res_width", String.valueOf(getWallpapersRequest.getFilterResWidth())));
        }
        getWallpapersRequest.getFilterTag().forEach(tag -> queryParams.add(Map.entry("filter_tag", tag.toString())));
        queryParams.add(Map.entry("limit", String.valueOf(getWallpapersRequest.getLimit())));
        queryParams.add(Map.entry("order", getWallpapersRequest.getOrder().toString()));
        if (!getWallpapersRequest.getOrderBy().equals(GetWallpapersOrderBy.DATE)) {
            queryParams.add(Map.entry("order_by", getWallpapersRequest.getOrderBy().toString()));
        }
        queryParams.add(Map.entry("page", String.valueOf(getWallpapersRequest.getPage())));
        if (!getWallpapersRequest.getS().isEmpty()) {
            queryParams.add(Map.entry("s", getWallpapersRequest.getS()));
        }
        queryParams.add(Map.entry("show_comments", String.valueOf(getWallpapersRequest.isShowComments())));
        queryParams.add(Map.entry("show_pickle_jar", String.valueOf(getWallpapersRequest.isShowPickleJar())));
        queryParams.add(Map.entry("show_resolutions", String.valueOf(getWallpapersRequest.isShowResolutions())));

        return createUri(wallpapersPath, emptyList(), queryParams);
    }

    @Nullable
    public Wallpaper getWallpaper(@NonNull GetWallpaperRequest getWallpaperRequest)
            throws IOException, ResponseException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(getWallpaperUrl(getWallpaperRequest))
                .GET();

        GetWallpaperResponse getWallpaperResponse = executeRequest(request, GetWallpaperResponse.class);
        return getWallpaperResponse.wallpaper();
    }

    @NonNull
    private URI getWallpaperUrl(@NonNull GetWallpaperRequest getWallpaperRequest) {
        List<String> paths = List.of(String.valueOf(getWallpaperRequest.getWallpaperId()));
        List<Map.Entry<String, String>> queryParams = new ArrayList<>();

        if (getWallpaperRequest.getFilterResHeight() > 0) {
            queryParams.add(Map.entry(
                    "filter_res_height",
                    String.valueOf(getWallpaperRequest.getFilterResHeight())
            ));
        }
        queryParams.add(Map.entry(
                "filter_res_operator",
                getWallpaperRequest.getFilterResOperator().toString()
        ));
        queryParams.add(Map.entry(
                "filter_res_operator_height",
                getWallpaperRequest.getFilterResOperatorHeight().toString()
        ));
        queryParams.add(Map.entry(
                "filter_res_operator_width",
                getWallpaperRequest.getFilterResOperatorWidth().toString()
        ));
        if (getWallpaperRequest.getFilterResWidth() > 0) {
            queryParams.add(Map.entry(
                    "filter_res_width",
                    String.valueOf(getWallpaperRequest.getFilterResWidth())
            ));
        }
        queryParams.add(Map.entry(
                "show_comments",
                String.valueOf(getWallpaperRequest.isShowComments())
        ));
        queryParams.add(Map.entry(
                "show_pickle_jar",
                String.valueOf(getWallpaperRequest.isShowPickleJar())
        ));
        queryParams.add(Map.entry(
                "show_resolutions",
                String.valueOf(getWallpaperRequest.isShowResolutions())
        ));

        return createUri(wallpaperPath, paths, queryParams);
    }

    public void downloadWallpaper(Path filename, DownloadWallpaperRequest downloadWallpaperRequest)
            throws IOException, ResponseException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(downloadUrl(downloadWallpaperRequest))
                .GET();
        DownloadWallpaperResponse downloadWallpaperResponse = executeRequest(request, DownloadWallpaperResponse.class);
        HttpRequest.Builder fileRequest = HttpRequest.newBuilder()
                .uri(URI.create(downloadWallpaperResponse.download().url()))
                .GET();
        byte[] body = executeRequest(fileRequest);
        Files.write(filename, body);
    }

    private <R> R executeRequest(HttpRequest.Builder requestBuilder, Class<R> clazz) throws IOException, ResponseException, InterruptedException {
        byte[] body = executeRequest(requestBuilder);
        return objectMapper.readValue(body, clazz);
    }

    private byte[] executeRequest(HttpRequest.Builder requestBuilder) throws IOException, ResponseException, InterruptedException {
        HttpRequest request = requestBuilder
                .header("Authorization", "Bearer " + apiKey)
                .build();
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() == 200) {
            return response.body();
        }
        String body = new String(response.body(), StandardCharsets.UTF_8);
        if (response.statusCode() == 404) {
            throw new ResponseException(404, "Not Found", List.of(body));
        }
        try {
            ResponseError responseError = objectMapper.readValue(body, ResponseError.class);
            throw new ResponseException(responseError);
        } catch (JsonProcessingException exception) {
            throw new ResponseException(0, "Unable to parse the body as JSON ErrorResponse. [" + body + "]");
        }
    }

    @NonNull
    private URI downloadUrl(DownloadWallpaperRequest downloadWallpaperRequest) {
        List<String> paths = List.of(downloadWallpaperRequest.getType().toString(),
                String.valueOf(downloadWallpaperRequest.getWidth()),
                String.valueOf(downloadWallpaperRequest.getHeight()),
                String.valueOf(downloadWallpaperRequest.getWallpaperId()));
        List<Map.Entry<String, String>> queryParams = List.of(Map.entry("show_watermark", String.valueOf(downloadWallpaperRequest.isShowWatermark())));

        return createUri(downloadWallpaperPath, paths, queryParams);
    }

    @NonNull
    private URI createUri(@NonNull URI baseUri, @NonNull List<String> paths, @NonNull List<Map.Entry<String, String>> queryParams) {
        String path = paths.stream()
                .filter(Objects::nonNull)
                .collect(joining("/"));
        if (!path.isEmpty()) {
            path = baseUri.getPath() + path;
        } else {
            path = baseUri.getPath();
        }

        String query = "?" + queryParams
                .stream()
                .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(joining("&"));

        return URI.create(baseUri.getScheme() + "://" + baseUri.getAuthority() + path + query);
    }
}
