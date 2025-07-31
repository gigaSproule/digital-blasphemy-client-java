package com.benjaminsproule.digitalblasphemy.client;

import com.benjaminsproule.digitalblasphemy.client.model.*;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.http.Fault;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static com.benjaminsproule.digitalblasphemy.client.util.FileUtils.readFile;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class DigitalBlasphemyClientTest {

    private WireMockServer wireMockServer;

    private DigitalBlasphemyClient underTest;

    @BeforeEach
    void setup() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        underTest = new DigitalBlasphemyClient("apiKey", wireMockServer.baseUrl());
    }

    @AfterEach
    void teardown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    void usesDigitalBlasphemyApiHost() throws NoSuchFieldException, IllegalAccessException {
        DigitalBlasphemyClient digitalBlasphemyClient = new DigitalBlasphemyClient("apiKey");
        assertThat(getField(digitalBlasphemyClient, "accountInformationPath")).isEqualTo("https://api.digitalblasphemy.com/v2/core/account");
        assertThat(getField(digitalBlasphemyClient, "wallpapersPath")).isEqualTo("https://api.digitalblasphemy.com/v2/core/wallpapers");
        assertThat(getField(digitalBlasphemyClient, "wallpaperPath")).isEqualTo("https://api.digitalblasphemy.com/v2/core/wallpaper/");
        assertThat(getField(digitalBlasphemyClient, "downloadWallpaperPath")).isEqualTo("https://api.digitalblasphemy.com/v2/core/download/wallpaper/");
    }

    private static Object getField(DigitalBlasphemyClient digitalBlasphemyClient, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = DigitalBlasphemyClient.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        Object fieldValue = field.get(digitalBlasphemyClient);
        field.setAccessible(false);
        return fieldValue;
    }

    @Nested
    class GetAccountInformation {
        @Test
        void getAccountInformationCanMapSuccessfulResponse() throws IOException, URISyntaxException, ResponseException {
            stubFor(get("/v2/core/account")
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("getAccountInformationSuccess.json")
                            ))));

            GetAccountInformationResponse getAccountInformationResponse = underTest.getAccountInformation();

            GetAccountInformationResponse expectedGetAccountInformationResponse = new GetAccountInformationResponse(
                    new GetAccountInformationResponse.DBCore(1),
                    new GetAccountInformationResponse.User(true, "username", 2, true, true)
            );
            assertThat(getAccountInformationResponse).isEqualTo(expectedGetAccountInformationResponse);

            verify(1, getRequestedFor(urlEqualTo("/v2/core/account")));
        }

        @Test
        void getAccountInformationCanMapUnauthorisedResponse() throws IOException, URISyntaxException {
            stubFor(get("/v2/core/account")
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(unauthorized()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("unauthorisedResponse.json")
                            ))));

            assertThatThrownBy(() -> underTest.getAccountInformation())
                    .isInstanceOf(ResponseException.class)
                    .hasFieldOrPropertyWithValue("code", 401)
                    .hasFieldOrPropertyWithValue("description", "Unauthorized")
                    .extracting("errors").isNull();

            verify(1, getRequestedFor(urlEqualTo("/v2/core/account")));
        }

        @Test
        void getAccountInformationCanMapUnknownErrorResponse() {
            stubFor(get("/v2/core/account")
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(aResponse().withStatus(405)));

            assertThatThrownBy(() -> underTest.getAccountInformation())
                    .isInstanceOf(ResponseException.class)
                    .hasFieldOrPropertyWithValue("code", 0)
                    .hasFieldOrPropertyWithValue(
                            "description",
                            "Unable to parse the body as JSON ErrorResponse. []")
                    .extracting("errors").isNull();

            verify(1, getRequestedFor(urlEqualTo("/v2/core/account")));
        }

        @Test
        void getAccountInformationCanMapNoResponse() {
            stubFor(get("/v2/core/account")
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE)));

            assertThatThrownBy(() -> underTest.getAccountInformation())
                    .isInstanceOf(IOException.class)
                    .hasMessageStartingWith("unexpected end of stream on %s".formatted(wireMockServer.baseUrl()));

            verify(moreThanOrExactly(2), getRequestedFor(urlEqualTo("/v2/core/account")));
        }
    }

    @Nested
    class GetWallpapers {
        @ParameterizedTest
        @MethodSource("notSentQueryParamsWhenNotProvided")
        void getWallpapersDoesNotSendQueryParamIfNotProvided(String queryParam) throws IOException, URISyntaxException, ResponseException {
            GetWallpapersRequest getWallpapersRequest = GetWallpapersRequest.builder().build();

            stubFor(get(urlMatching("/v2/core/wallpapers\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("getWallpapersSuccessFullyPopulated.json")
                            ))));

            underTest.getWallpapers(getWallpapersRequest);

            verify(1, getRequestedFor(urlMatching("/v2/core/wallpapers\\?.*"))
                    .withoutQueryParam(queryParam));
        }

        public static Stream<Arguments> notSentQueryParamsWhenNotProvided() {
            return Stream.of(
                    arguments("filter_date_day"),
                    arguments("filter_date_month"),
                    arguments("filter_date_year"),
                    arguments("filter_date_gallery"),
                    arguments("filter_rating"),
                    arguments("filter_res_height"),
                    arguments("filter_tag"),
                    arguments("order_by"),
                    arguments("s")
            );
        }

        @ParameterizedTest
        @MethodSource("sentQueryParamsWhenNotProvided")
        void getWallpapersDoesSendQueryParamIfNotProvided(String queryParam, String expectedValue) throws IOException, URISyntaxException, ResponseException {
            GetWallpapersRequest getWallpapersRequest = GetWallpapersRequest.builder().build();

            stubFor(get(urlMatching("/v2/core/wallpapers\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("getWallpapersSuccessFullyPopulated.json")
                            ))));

            underTest.getWallpapers(getWallpapersRequest);

            verify(1, getRequestedFor(
                    urlMatching("/v2/core/wallpapers\\?.*" + queryParam + "=" + expectedValue + ".*"))
            );
        }

        public static Stream<Arguments> sentQueryParamsWhenNotProvided() {
            return Stream.of(
                    arguments("filter_date_operator", "%3E%3D"),
                    arguments("filter_rating_operator", "%3E%3D"),
                    arguments("filter_res_operator", "%3E%3D"),
                    arguments("filter_res_operator_height", "%3E%3D"),
                    arguments("filter_res_operator_width", "%3E%3D"),
                    arguments("limit", "10"),
                    arguments("page", "1"),
                    arguments("show_comments", "false"),
                    arguments("show_pickle_jar", "false"),
                    arguments("show_resolutions", "true")
            );
        }

        @ParameterizedTest
        @MethodSource("queryParamsProvided")
        void getWallpaperDoesSendQueryParamIfProvided(String field, String queryParam, Object value, String expectedValue)
                throws IOException, URISyntaxException, ResponseException, IllegalAccessException, NoSuchFieldException {
            GetWallpapersRequest getWallpapersRequest = GetWallpapersRequest.builder().build();

            stubFor(get(urlMatching("/v2/core/wallpapers\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("getWallpapersSuccessFullyPopulated.json")
                            ))));

            Field declaredField = GetWallpapersRequest.class.getDeclaredField(field);
            declaredField.setAccessible(true);
            declaredField.set(getWallpapersRequest, value);
            declaredField.setAccessible(false);

            underTest.getWallpapers(getWallpapersRequest);

            if (value instanceof List<?>) {
                String[] expectedValues = expectedValue.split(",");
                String expectedQueryParams = Arrays.stream(expectedValues).map((expected) -> queryParam + "=" + expected).collect(joining("&"));
                verify(1, getRequestedFor(
                        urlMatching("/v2/core/wallpapers\\?.*" + expectedQueryParams + ".*"))
                );
            } else {
                verify(1, getRequestedFor(
                        urlMatching("/v2/core/wallpapers\\?.*" + queryParam + "=" + expectedValue + ".*"))
                );
            }
        }

        public static Stream<Arguments> queryParamsProvided() {
            return Stream.of(
                    arguments("filterDateDay", "filter_date_day", 2, "2"),
                    arguments("filterDateMonth", "filter_date_month", 2, "2"),
                    arguments("filterDateYear", "filter_date_year", 2000, "2000"),
                    arguments("filterDateOperator", "filter_date_operator", Operator.EQUAL, "%3D"),
                    arguments("filterGallery", "filter_gallery", List.of(1, 2), "1,2"),
                    arguments("filterRating", "filter_rating", 3f, "3"),
                    arguments("filterRatingOperator", "filter_rating_operator", Operator.EQUAL, "%3D"),
                    arguments("filterResHeight", "filter_res_height", 1080, "1080"),
                    arguments("filterResOperator", "filter_res_operator", Operator.EQUAL, "%3D"),
                    arguments("filterResOperatorHeight", "filter_res_operator_height", Operator.EQUAL, "%3D"),
                    arguments("filterResOperatorWidth", "filter_res_operator_width", Operator.EQUAL, "%3D"),
                    arguments("filterResWidth", "filter_res_width", 1920, "1920"),
                    arguments("filterTag", "filter_tag", List.of(1, 2), "1,2"),
                    arguments("limit", "limit", 20, "20"),
                    arguments("order", "order", Order.DESCENDING, "desc"),
                    arguments("orderBy", "order_by", GetWallpapersOrderBy.NAME, "name"),
                    arguments("page", "page", 2, "2"),
                    arguments("s", "s", "search", "search"),
                    arguments("showComments", "show_comments", true, "true"),
                    arguments("showPickleJar", "show_pickle_jar", true, "true"),
                    arguments("showResolutions", "show_resolutions", false, "false")
            );
        }

        @ParameterizedTest
        @MethodSource("successfulResponse")
        void getWallpapersCanMapResponse(String response, GetWallpapersResponse expectedWallpapersResponse) throws IOException, ResponseException {
            GetWallpapersRequest getWallpapersRequest = GetWallpapersRequest.builder().build();

            stubFor(get(urlMatching("/v2/core/wallpapers\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(response))
                    ));

            GetWallpapersResponse getWallpapersResponse = underTest.getWallpapers(getWallpapersRequest);

            assertThat(getWallpapersResponse).isEqualTo(expectedWallpapersResponse);

            verify(1, getRequestedFor(
                    urlMatching("/v2/core/wallpapers\\?.*")));
        }

        public static Stream<Arguments> successfulResponse() throws IOException, URISyntaxException {
            return Stream.of(
                    arguments(readFile("getWallpaperSuccessFullyPopulated.json"), new GetWallpapersResponse()),
                    arguments(readFile("getWallpapersSuccessMinimalPopulated.json"), new GetWallpapersResponse())
            );
        }

        @Test
        void getWallpaperCanMapUnauthorisedResponse() throws IOException, URISyntaxException {
            GetWallpapersRequest getWallpapersRequest = GetWallpapersRequest.builder().build();

            stubFor(get(urlMatching("/v2/core/wallpapers\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(unauthorized()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("unauthorisedResponse.json")
                            ))));

            assertThatThrownBy(() -> underTest.getWallpapers(getWallpapersRequest))
                    .isInstanceOf(ResponseException.class)
                    .hasFieldOrPropertyWithValue("code", 401)
                    .hasFieldOrPropertyWithValue("description", "Unauthorized")
                    .extracting("errors").isNull();

            verify(1, getRequestedFor(
                    urlMatching("/v2/core/wallpapers\\?.*")));
        }

        @Test
        void getWallpaperCanMapBadRequestResponse() throws IOException, URISyntaxException {
            GetWallpapersRequest getWallpapersRequest = GetWallpapersRequest.builder().build();

            stubFor(get(urlMatching("/v2/core/wallpapers\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(badRequest()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("getWallpapersBadRequest.json")
                            ))));

            assertThatThrownBy(() -> underTest.getWallpapers(getWallpapersRequest))
                    .isInstanceOf(ResponseException.class)
                    .hasFieldOrPropertyWithValue("code", 400)
                    .hasFieldOrPropertyWithValue("description", "Bad Request")
                    .hasFieldOrPropertyWithValue("errors", List.of(
                            "\"filter_date_day\" must be less than or equal to 31",
                            "\"limit\" must be greater than or equal to 1"
                    ));

            verify(1, getRequestedFor(
                    urlMatching("/v2/core/wallpapers\\?.*")));
        }

        @Test
        void getWallpaperCanMapUnknownErrorResponse() {
            GetWallpapersRequest getWallpapersRequest = GetWallpapersRequest.builder().build();

            stubFor(get(urlMatching("/v2/core/wallpapers\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(aResponse().withStatus(405)));

            assertThatThrownBy(() -> underTest.getWallpapers(getWallpapersRequest))
                    .isInstanceOf(ResponseException.class)
                    .hasFieldOrPropertyWithValue("code", 0)
                    .hasFieldOrPropertyWithValue(
                            "description",
                            "Unable to parse the body as JSON ErrorResponse. []")
                    .extracting("errors").isNull();

            verify(1, getRequestedFor(
                    urlMatching("/v2/core/wallpapers\\?.*")));
        }

        @Test
        void getWallpaperCanMapNoResponse() {
            GetWallpapersRequest getWallpapersRequest = GetWallpapersRequest.builder().build();

            stubFor(get(urlMatching("/v2/core/wallpapers\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE)));

            assertThatThrownBy(() -> underTest.getWallpapers(getWallpapersRequest))
                    .isInstanceOf(IOException.class)
                    .hasMessageStartingWith("unexpected end of stream on %s".formatted(wireMockServer.baseUrl()));

            verify(moreThanOrExactly(2), getRequestedFor(
                    urlMatching("/v2/core/wallpapers\\?.*")));
        }
    }

    @Nested
    class GetWallpaper {
        @ParameterizedTest
        @MethodSource("notSentQueryParamsWhenNotProvided")
        void getWallpaperDoesNotSendQueryParamIfNotProvided(String queryParam) throws IOException, URISyntaxException, ResponseException {
            GetWallpaperRequest getWallpaperRequest = GetWallpaperRequest.builder().wallpaperId(1).build();

            stubFor(get(urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("getWallpaperSuccessFullyPopulated.json")
                            ))));

            underTest.getWallpaper(getWallpaperRequest);

            verify(1, getRequestedFor(urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*"))
                    .withoutQueryParam(queryParam));
        }

        public static Stream<Arguments> notSentQueryParamsWhenNotProvided() {
            return Stream.of(
                    arguments("filter_res_height"),
                    arguments("filter_res_width")
            );
        }

        @ParameterizedTest
        @MethodSource("sentQueryParamsWhenNotProvided")
        void getWallpaperDoesSendQueryParamIfNotProvided(String queryParam, String expectedValue) throws IOException, URISyntaxException, ResponseException {
            GetWallpaperRequest getWallpaperRequest = GetWallpaperRequest.builder().wallpaperId(1).build();

            stubFor(get(urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("getWallpaperSuccessFullyPopulated.json")
                            ))));

            underTest.getWallpaper(getWallpaperRequest);

            verify(1, getRequestedFor(
                    urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*" + queryParam + "=" + expectedValue + ".*"))
            );
        }

        public static Stream<Arguments> sentQueryParamsWhenNotProvided() {
            return Stream.of(
                    arguments("filter_res_operator", "%3E%3D"),
                    arguments("filter_res_operator_height", "%3E%3D"),
                    arguments("filter_res_operator_width", "%3E%3D"),
                    arguments("show_comments", "false"),
                    arguments("show_pickle_jar", "false"),
                    arguments("show_resolutions", "true")
            );
        }

        @ParameterizedTest
        @MethodSource("queryParamsProvided")
        void getWallpaperDoesSendQueryParamIfProvided(String field, String queryParam, Object value, String expectedValue)
                throws IOException, URISyntaxException, ResponseException, IllegalAccessException, NoSuchFieldException {
            GetWallpaperRequest getWallpaperRequest = GetWallpaperRequest.builder().wallpaperId(1).build();

            stubFor(get(urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("getWallpaperSuccessFullyPopulated.json")
                            ))));

            Field declaredField = GetWallpaperRequest.class.getDeclaredField(field);
            declaredField.setAccessible(true);
            declaredField.set(getWallpaperRequest, value);
            declaredField.setAccessible(false);

            underTest.getWallpaper(getWallpaperRequest);

            verify(1, getRequestedFor(
                    urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*" + queryParam + "=" + expectedValue + ".*"))
            );
        }

        public static Stream<Arguments> queryParamsProvided() {
            return Stream.of(
                    arguments("filterResHeight", "filter_res_height", 1, "1"),
                    arguments("filterResWidth", "filter_res_width", 1, "1"),
                    arguments("filterResOperator", "filter_res_operator", Operator.EQUAL, "%3D"),
                    arguments("filterResOperatorHeight", "filter_res_operator_height", Operator.EQUAL, "%3D"),
                    arguments("filterResOperatorWidth", "filter_res_operator_width", Operator.EQUAL, "%3D"),
                    arguments("showComments", "show_comments", true, "true"),
                    arguments("showPickleJar", "show_pickle_jar", true, "true"),
                    arguments("showResolutions", "show_resolutions", false, "false")
            );
        }

        @ParameterizedTest
        @MethodSource("successfulResponse")
        void getWallpaperCanMapResponse(String response, Wallpaper expectedWallpaper) throws IOException, ResponseException {
            GetWallpaperRequest getWallpaperRequest = GetWallpaperRequest.builder().wallpaperId(1).build();

            stubFor(get(urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(response))
                    ));

            Wallpaper wallpaper = underTest.getWallpaper(getWallpaperRequest);

            assertThat(wallpaper).isEqualTo(expectedWallpaper);

            verify(1, getRequestedFor(
                    urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*")));
        }

        public static Stream<Arguments> successfulResponse() throws IOException, URISyntaxException {
            return Stream.of(
                    arguments(
                            readFile("getWallpaperSuccessFullyPopulated.json"),
                            new Wallpaper(
                                    2,
                                    true,
                                    new Wallpaper.Comments(List.of(
                                            new Wallpaper.Comments.Comment(
                                                    "5",
                                                    "author ID 1",
                                                    "author display 1",
                                                    "Content 1",
                                                    "6",
                                                    7),
                                            new Wallpaper.Comments.Comment(
                                                    "8",
                                                    "author ID 2",
                                                    "author display 2",
                                                    "Content 2",
                                                    "9",
                                                    10)
                                    )),
                                    "Content 3",
                                    true,
                                    "Vulcan",
                                    new Wallpaper.Paths(
                                            "/wallpaper/2",
                                            "/thumbnail/12x13/vulcan_thumbnail_12x13.jpg",
                                            "/sec/vulcan/"),
                                    new Wallpaper.PickleJar(
                                            "parent 1",
                                            List.of("sibling 1", "sibling 2")),
                                    "11",
                                    new Wallpaper.Resolutions(
                                            List.of(
                                                    new Wallpaper.Resolutions.Resolution(
                                                            "12x13",
                                                            "12",
                                                            "13",
                                                            "/single/12x13/vulcan_single_12x13.jpg"),
                                                    new Wallpaper.Resolutions.Resolution(
                                                            "14x15",
                                                            "14",
                                                            "15",
                                                            "/single/14x15/vulcan_single_14x15.jpg")
                                            ),
                                            List.of(
                                                    new Wallpaper.Resolutions.Resolution(
                                                            "16x17",
                                                            "16",
                                                            "17",
                                                            "/dual/16x17/vulcan_dual_16x17.jpg"),
                                                    new Wallpaper.Resolutions.Resolution(
                                                            "18x19",
                                                            "18",
                                                            "19",
                                                            "/dual/18x19/vulcan_dual_18x19.jpg")
                                            ),
                                            List.of(
                                                    new Wallpaper.Resolutions.Resolution(
                                                            "20x21",
                                                            "20",
                                                            "21",
                                                            "/triple/20x21/vulcan_triple_20x21.jpg"),
                                                    new Wallpaper.Resolutions.Resolution(
                                                            "22x23",
                                                            "22",
                                                            "23",
                                                            "/triple/22x23/vulcan_triple_22x23.jpg")
                                            ),
                                            List.of(
                                                    new Wallpaper.Resolutions.Resolution(
                                                            "24x25",
                                                            "24",
                                                            "25",
                                                            "/mobile/24x25/vulcan_mobile_24x25.jpg"),
                                                    new Wallpaper.Resolutions.Resolution(
                                                            "26x27",
                                                            "26",
                                                            "27",
                                                            "/mobile/26x27/vulcan_mobile_26x27.jpg")
                                            )
                                    ),
                                    "vulcan",
                                    Map.of(
                                            "28", new Wallpaper.Tag(28, "Tag 1"),
                                            "29", new Wallpaper.Tag(29, "Tag 2")
                                    ),
                                    30L
                            )),
                    arguments(readFile("getWallpaperSuccessMinimalPopulated.json"), null)
            );
        }

        @Test
        void getWallpaperCanMapUnauthorisedResponse() throws IOException, URISyntaxException {
            GetWallpaperRequest getWallpaperRequest = GetWallpaperRequest.builder().wallpaperId(1).build();

            stubFor(get(urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(unauthorized()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("unauthorisedResponse.json")
                            ))));

            assertThatThrownBy(() -> underTest.getWallpaper(getWallpaperRequest))
                    .isInstanceOf(ResponseException.class)
                    .hasFieldOrPropertyWithValue("code", 401)
                    .hasFieldOrPropertyWithValue("description", "Unauthorized")
                    .extracting("errors").isNull();

            verify(1, getRequestedFor(
                    urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*")));
        }

        @Test
        void getWallpaperCanMapBadRequestResponse() throws IOException, URISyntaxException {
            GetWallpaperRequest getWallpaperRequest = GetWallpaperRequest.builder().wallpaperId(1).build();

            stubFor(get(urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(badRequest()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("getWallpaperBadRequest.json")
                            ))));

            assertThatThrownBy(() -> underTest.getWallpaper(getWallpaperRequest))
                    .isInstanceOf(ResponseException.class)
                    .hasFieldOrPropertyWithValue("code", 400)
                    .hasFieldOrPropertyWithValue("description", "Bad Request")
                    .hasFieldOrPropertyWithValue("errors", List.of(
                            "\"filter_res_height\" must be greater than or equal to 1",
                            "\"filter_res_width\" must be greater than or equal to 1"
                    ));

            verify(1, getRequestedFor(
                    urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*")));
        }

        @Test
        void getWallpaperCanMapUnknownErrorResponse() {
            GetWallpaperRequest getWallpaperRequest = GetWallpaperRequest.builder().wallpaperId(1).build();

            stubFor(get(urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(aResponse().withStatus(405)));

            assertThatThrownBy(() -> underTest.getWallpaper(getWallpaperRequest))
                    .isInstanceOf(ResponseException.class)
                    .hasFieldOrPropertyWithValue("code", 0)
                    .hasFieldOrPropertyWithValue(
                            "description",
                            "Unable to parse the body as JSON ErrorResponse. []")
                    .extracting("errors").isNull();

            verify(1, getRequestedFor(
                    urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*")));
        }

        @Test
        void getWallpaperCanMapNoResponse() {
            GetWallpaperRequest getWallpaperRequest = GetWallpaperRequest.builder().wallpaperId(1).build();

            stubFor(get(urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE)));

            assertThatThrownBy(() -> underTest.getWallpaper(getWallpaperRequest))
                    .isInstanceOf(IOException.class)
                    .hasMessageStartingWith("unexpected end of stream on %s".formatted(wireMockServer.baseUrl()));

            verify(moreThanOrExactly(2), getRequestedFor(
                    urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*")));
        }
    }

    @Nested
    class DownloadWallpaper {
        @Test
        void downloadWallpaperDoesSendShowWatermarkIfNotProvided() throws IOException, URISyntaxException, ResponseException {
            DownloadWallpaperRequest downloadWallpaperRequest = DownloadWallpaperRequest.builder()
                    .type(WallpaperType.SINGLE)
                    .width(1)
                    .height(1)
                    .wallpaperId(1)
                    .build();

            stubFor(get(urlMatching(
                    "/v2/core/download/wallpaper/%s/%s/%s/%s\\?.*".formatted(
                            downloadWallpaperRequest.getType(),
                            downloadWallpaperRequest.getWidth(),
                            downloadWallpaperRequest.getHeight(),
                            downloadWallpaperRequest.getWallpaperId()
                    )))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("downloadWallpaperSuccessFullyPopulated.json")
                                            .replace("{{host}}", wireMockServer.baseUrl())
                            ))));

            stubFor(get(urlMatching("/test.jpg"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "image/jpg")
                            .withResponseBody(new Body("image-content"))));

            String filename = UUID.randomUUID().toString();

            underTest.downloadWallpaper(filename, downloadWallpaperRequest);

            verify(1, getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=true".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(1, getRequestedFor(urlMatching("/test.jpg")));

            Files.deleteIfExists(Path.of(filename));
        }

        @Test
        void downloadWallpaperDoesSendShowWatermarkIfProvided() throws IOException, URISyntaxException, ResponseException {
            DownloadWallpaperRequest downloadWallpaperRequest = DownloadWallpaperRequest.builder()
                    .type(WallpaperType.SINGLE)
                    .width(1)
                    .height(1)
                    .wallpaperId(1)
                    .showWatermark(false)
                    .build();

            stubFor(get(urlMatching(
                    "/v2/core/download/wallpaper/%s/%s/%s/%s\\?.*".formatted(
                            downloadWallpaperRequest.getType(),
                            downloadWallpaperRequest.getWidth(),
                            downloadWallpaperRequest.getHeight(),
                            downloadWallpaperRequest.getWallpaperId()
                    )))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("downloadWallpaperSuccessFullyPopulated.json")
                                            .replace("{{host}}", wireMockServer.baseUrl())
                            ))));

            stubFor(get(urlMatching("/test.jpg"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "image/jpg")
                            .withResponseBody(new Body("image-content"))));

            String filename = UUID.randomUUID().toString();

            underTest.downloadWallpaper(filename, downloadWallpaperRequest);

            verify(1, getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=false".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(1, getRequestedFor(urlMatching("/test.jpg")));

            Files.deleteIfExists(Path.of(filename));
        }

        @Test
        void downloadWallpaperSendsPathParameters() throws IOException, URISyntaxException, ResponseException {
            DownloadWallpaperRequest downloadWallpaperRequest = DownloadWallpaperRequest.builder()
                    .type(WallpaperType.DUAL)
                    .width(2)
                    .height(3)
                    .wallpaperId(4)
                    .showWatermark(false)
                    .build();

            stubFor(get(urlMatching(
                    "/v2/core/download/wallpaper/%s/%s/%s/%s\\?.*".formatted(
                            downloadWallpaperRequest.getType(),
                            downloadWallpaperRequest.getWidth(),
                            downloadWallpaperRequest.getHeight(),
                            downloadWallpaperRequest.getWallpaperId()
                    )))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("downloadWallpaperSuccessFullyPopulated.json")
                                            .replace("{{host}}", wireMockServer.baseUrl())
                            ))));

            stubFor(get(urlMatching("/test.jpg"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "image/jpg")
                            .withResponseBody(new Body("image-content"))));

            String filename = UUID.randomUUID().toString();

            underTest.downloadWallpaper(filename, downloadWallpaperRequest);

            verify(1, getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=false".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(1, getRequestedFor(urlMatching("/test.jpg")));

            Files.deleteIfExists(Path.of(filename));
        }

        @Test
        void downloadWallpaperCanMapSuccessfulResponseFullyPopulated() throws IOException, URISyntaxException, ResponseException {
            DownloadWallpaperRequest downloadWallpaperRequest = DownloadWallpaperRequest.builder()
                    .type(WallpaperType.DUAL)
                    .width(2)
                    .height(3)
                    .wallpaperId(4)
                    .showWatermark(false)
                    .build();

            stubFor(get(urlMatching(
                    "/v2/core/download/wallpaper/%s/%s/%s/%s\\?.*".formatted(
                            downloadWallpaperRequest.getType(),
                            downloadWallpaperRequest.getWidth(),
                            downloadWallpaperRequest.getHeight(),
                            downloadWallpaperRequest.getWallpaperId()
                    )))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("downloadWallpaperSuccessFullyPopulated.json")
                                            .replace("{{host}}", wireMockServer.baseUrl())
                            ))));

            stubFor(get(urlMatching("/test.jpg"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "image/jpg")
                            .withResponseBody(new Body("image-content"))));

            String filename = UUID.randomUUID().toString();

            underTest.downloadWallpaper(filename, downloadWallpaperRequest);

            verify(1, getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=false".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(1, getRequestedFor(urlMatching("/test.jpg")));

            Files.deleteIfExists(Path.of(filename));
        }

        @Test
        void downloadWallpaperCanMapSuccessfulResponseMinimalPopulated() throws IOException, URISyntaxException, ResponseException {
            DownloadWallpaperRequest downloadWallpaperRequest = DownloadWallpaperRequest.builder()
                    .type(WallpaperType.DUAL)
                    .width(2)
                    .height(3)
                    .wallpaperId(4)
                    .showWatermark(false)
                    .build();

            stubFor(get(urlMatching(
                    "/v2/core/download/wallpaper/%s/%s/%s/%s\\?.*".formatted(
                            downloadWallpaperRequest.getType(),
                            downloadWallpaperRequest.getWidth(),
                            downloadWallpaperRequest.getHeight(),
                            downloadWallpaperRequest.getWallpaperId()
                    )))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("downloadWallpaperSuccessMinimalPopulated.json")
                                            .replace("{{host}}", wireMockServer.baseUrl())
                            ))));

            stubFor(get(urlMatching("/test.jpg"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "image/jpg")
                            .withResponseBody(new Body("image-content"))));

            String filename = UUID.randomUUID().toString();

            underTest.downloadWallpaper(filename, downloadWallpaperRequest);

            verify(1, getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=false".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(1, getRequestedFor(urlMatching("/test.jpg")));

            Files.deleteIfExists(Path.of(filename));
        }

        @Test
        void downloadWallpaperCanMapUnauthorisedResponseWhenGettingDownloadWallpaperResponse() throws IOException, URISyntaxException {
            DownloadWallpaperRequest downloadWallpaperRequest = DownloadWallpaperRequest.builder()
                    .type(WallpaperType.DUAL)
                    .width(2)
                    .height(3)
                    .wallpaperId(4)
                    .showWatermark(false)
                    .build();

            stubFor(get(urlMatching(
                    "/v2/core/download/wallpaper/%s/%s/%s/%s\\?.*".formatted(
                            downloadWallpaperRequest.getType(),
                            downloadWallpaperRequest.getWidth(),
                            downloadWallpaperRequest.getHeight(),
                            downloadWallpaperRequest.getWallpaperId()
                    )))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(unauthorized()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("unauthorisedResponse.json")
                            ))));

            String filename = UUID.randomUUID().toString();

            assertThatThrownBy(() -> underTest.downloadWallpaper(filename, downloadWallpaperRequest))
                    .isInstanceOf(ResponseException.class)
                    .hasFieldOrPropertyWithValue("code", 401)
                    .hasFieldOrPropertyWithValue(
                            "description",
                            "Unauthorized")
                    .extracting("errors").isNull();

            assertThat(Files.notExists(Path.of(filename))).isTrue();
            verify(1, getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=false.*".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(0, getRequestedFor(urlMatching("/test.jpg")));
        }

        @Test
        void downloadWallpaperCanMapUnauthorisedResponseWhenDownloadingFile() throws IOException, URISyntaxException {
            DownloadWallpaperRequest downloadWallpaperRequest = DownloadWallpaperRequest.builder()
                    .type(WallpaperType.DUAL)
                    .width(2)
                    .height(3)
                    .wallpaperId(4)
                    .showWatermark(false)
                    .build();

            stubFor(get(urlMatching(
                    "/v2/core/download/wallpaper/%s/%s/%s/%s\\?.*".formatted(
                            downloadWallpaperRequest.getType(),
                            downloadWallpaperRequest.getWidth(),
                            downloadWallpaperRequest.getHeight(),
                            downloadWallpaperRequest.getWallpaperId()
                    )))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("downloadWallpaperSuccessFullyPopulated.json")
                                            .replace("{{host}}", wireMockServer.baseUrl())
                            ))));

            stubFor(get(urlMatching("/test.jpg"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(unauthorized()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("unauthorisedResponse.json")
                            ))));

            String filename = UUID.randomUUID().toString();

            assertThatThrownBy(() -> underTest.downloadWallpaper(filename, downloadWallpaperRequest))
                    .isInstanceOf(ResponseException.class)
                    .hasFieldOrPropertyWithValue("code", 401)
                    .hasFieldOrPropertyWithValue(
                            "description",
                            "Unauthorized")
                    .extracting("errors").isNull();

            verify(1, getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=false.*".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(1, getRequestedFor(urlMatching("/test.jpg")));

            Files.deleteIfExists(Path.of(filename));
        }

        @Test
        void downloadWallpaperCanMapBadRequestResponseWhenGettingDownloadWallpaperResponse() throws IOException, URISyntaxException {
            DownloadWallpaperRequest downloadWallpaperRequest = DownloadWallpaperRequest.builder()
                    .type(WallpaperType.DUAL)
                    .width(2)
                    .height(3)
                    .wallpaperId(4)
                    .showWatermark(false)
                    .build();

            stubFor(get(urlMatching(
                    "/v2/core/download/wallpaper/%s/%s/%s/%s\\?.*".formatted(
                            downloadWallpaperRequest.getType(),
                            downloadWallpaperRequest.getWidth(),
                            downloadWallpaperRequest.getHeight(),
                            downloadWallpaperRequest.getWallpaperId()
                    )))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(badRequest()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("downloadWallpaperBadRequest.json")
                            ))));

            String filename = UUID.randomUUID().toString();

            assertThatThrownBy(() -> underTest.downloadWallpaper(filename, downloadWallpaperRequest))
                    .isInstanceOf(ResponseException.class)
                    .hasFieldOrPropertyWithValue("code", 400)
                    .hasFieldOrPropertyWithValue(
                            "description",
                            "Bad Request")
                    .hasFieldOrPropertyWithValue("errors", List.of(
                            "\"type\" must be one of [single, dual, triple, mobile]",
                            "\"width\" must be a number"
                    ));

            assertThat(Files.notExists(Path.of(filename))).isTrue();
            verify(1, getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=false.*".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(0, getRequestedFor(urlMatching("/test.jpg")));
        }

        @Test
        void downloadWallpaperCanMapNotFoundResponseWhenDownloadingFile() throws IOException, URISyntaxException {
            DownloadWallpaperRequest downloadWallpaperRequest = DownloadWallpaperRequest.builder()
                    .type(WallpaperType.DUAL)
                    .width(2)
                    .height(3)
                    .wallpaperId(4)
                    .showWatermark(false)
                    .build();

            stubFor(get(urlMatching(
                    "/v2/core/download/wallpaper/%s/%s/%s/%s\\?.*".formatted(
                            downloadWallpaperRequest.getType(),
                            downloadWallpaperRequest.getWidth(),
                            downloadWallpaperRequest.getHeight(),
                            downloadWallpaperRequest.getWallpaperId()
                    )))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("downloadWallpaperSuccessFullyPopulated.json")
                                            .replace("{{host}}", wireMockServer.baseUrl())
                            ))));

            stubFor(get(urlMatching("/test.jpg"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(notFound()
                            .withHeader("Content-Type", "text/plain")
                            .withResponseBody(new Body("Object Not Found"))));

            String filename = UUID.randomUUID().toString();

            assertThatThrownBy(() -> underTest.downloadWallpaper(filename, downloadWallpaperRequest))
                    .isInstanceOf(ResponseException.class)
                    .hasFieldOrPropertyWithValue("code", 404)
                    .hasFieldOrPropertyWithValue(
                            "description",
                            "Not Found")
                    .hasFieldOrPropertyWithValue("errors", List.of(
                            "Object Not Found"
                    ));

            assertThat(Files.notExists(Path.of(filename))).isTrue();
            verify(1, getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=false.*".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(1, getRequestedFor(urlMatching("/test.jpg")));
        }

        @Test
        void downloadWallpaperCanMapUnknownErrorResponseWhenDownloadingFile() throws IOException, URISyntaxException {
            DownloadWallpaperRequest downloadWallpaperRequest = DownloadWallpaperRequest.builder()
                    .type(WallpaperType.DUAL)
                    .width(2)
                    .height(3)
                    .wallpaperId(4)
                    .showWatermark(false)
                    .build();

            stubFor(get(urlMatching(
                    "/v2/core/download/wallpaper/%s/%s/%s/%s\\?.*".formatted(
                            downloadWallpaperRequest.getType(),
                            downloadWallpaperRequest.getWidth(),
                            downloadWallpaperRequest.getHeight(),
                            downloadWallpaperRequest.getWallpaperId()
                    )))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("downloadWallpaperSuccessFullyPopulated.json")
                                            .replace("{{host}}", wireMockServer.baseUrl())
                            ))));

            stubFor(get(urlMatching("/test.jpg"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(aResponse().withStatus(405)));

            String filename = UUID.randomUUID().toString();

            assertThatThrownBy(() -> underTest.downloadWallpaper(filename, downloadWallpaperRequest))
                    .isInstanceOf(ResponseException.class)
                    .hasFieldOrPropertyWithValue("code", 0)
                    .hasFieldOrPropertyWithValue(
                            "description",
                            "Unable to parse the body as JSON ErrorResponse. []")
                    .extracting("errors").isNull();

            assertThat(Files.notExists(Path.of(filename))).isTrue();
            verify(1, getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=false.*".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(1, getRequestedFor(urlMatching("/test.jpg")));
        }

        @Test
        void downloadWallpaperNoResponseWhenGettingDownloadWallpaperResponse() {
            DownloadWallpaperRequest downloadWallpaperRequest = DownloadWallpaperRequest.builder()
                    .type(WallpaperType.DUAL)
                    .width(2)
                    .height(3)
                    .wallpaperId(4)
                    .showWatermark(false)
                    .build();

            stubFor(get(urlMatching(
                    "/v2/core/download/wallpaper/%s/%s/%s/%s\\?.*".formatted(
                            downloadWallpaperRequest.getType(),
                            downloadWallpaperRequest.getWidth(),
                            downloadWallpaperRequest.getHeight(),
                            downloadWallpaperRequest.getWallpaperId()
                    )))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE)));

            String filename = UUID.randomUUID().toString();

            assertThatThrownBy(() -> underTest.downloadWallpaper(filename, downloadWallpaperRequest))
                    .isInstanceOf(IOException.class)
                    .hasMessageStartingWith("unexpected end of stream on %s".formatted(wireMockServer.baseUrl()));
            ;

            assertThat(Files.notExists(Path.of(filename))).isTrue();
            verify(moreThanOrExactly(2), getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=false.*".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(0, getRequestedFor(urlMatching("/test.jpg")));
        }

        @Test
        void downloadWallpaperNoResponseWhenDownloadingFile() throws IOException, URISyntaxException {
            DownloadWallpaperRequest downloadWallpaperRequest = DownloadWallpaperRequest.builder()
                    .type(WallpaperType.DUAL)
                    .width(2)
                    .height(3)
                    .wallpaperId(4)
                    .showWatermark(false)
                    .build();

            stubFor(get(urlMatching(
                    "/v2/core/download/wallpaper/%s/%s/%s/%s\\?.*".formatted(
                            downloadWallpaperRequest.getType(),
                            downloadWallpaperRequest.getWidth(),
                            downloadWallpaperRequest.getHeight(),
                            downloadWallpaperRequest.getWallpaperId()
                    )))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("downloadWallpaperSuccessFullyPopulated.json")
                                            .replace("{{host}}", wireMockServer.baseUrl())
                            ))));

            stubFor(get(urlMatching("/test.jpg"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE)));

            String filename = UUID.randomUUID().toString();

            assertThatThrownBy(() -> underTest.downloadWallpaper(filename, downloadWallpaperRequest))
                    .isInstanceOf(IOException.class)
                    .hasMessageStartingWith("unexpected end of stream on %s".formatted(wireMockServer.baseUrl()));
            ;

            assertThat(Files.notExists(Path.of(filename))).isTrue();
            verify(1, getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=false.*".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(moreThanOrExactly(2), getRequestedFor(urlMatching("/test.jpg")));
        }
    }

}
