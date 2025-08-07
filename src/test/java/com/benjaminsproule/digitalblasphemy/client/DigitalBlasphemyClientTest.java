package com.benjaminsproule.digitalblasphemy.client;

import com.benjaminsproule.digitalblasphemy.client.model.*;
import com.fasterxml.jackson.core.JsonParseException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import com.github.tomakehurst.wiremock.http.Fault;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.EOFException;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
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
    void usesDigitalBlasphemyApiHost() throws Exception {
        DigitalBlasphemyClient digitalBlasphemyClient = new DigitalBlasphemyClient("apiKey");
        assertThat(getField(digitalBlasphemyClient, "accountInformationPath"))
                .isEqualTo(URI.create("https://api.digitalblasphemy.com/v2/core/account"));
        assertThat(getField(digitalBlasphemyClient, "wallpapersPath"))
                .isEqualTo(URI.create("https://api.digitalblasphemy.com/v2/core/wallpapers"));
        assertThat(getField(digitalBlasphemyClient, "wallpaperPath"))
                .isEqualTo(URI.create("https://api.digitalblasphemy.com/v2/core/wallpaper/"));
        assertThat(getField(digitalBlasphemyClient, "downloadWallpaperPath"))
                .isEqualTo(URI.create("https://api.digitalblasphemy.com/v2/core/download/wallpaper/"));
    }

    private static Object getField(DigitalBlasphemyClient digitalBlasphemyClient, String fieldName) throws Exception {
        Field field = DigitalBlasphemyClient.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        Object fieldValue = field.get(digitalBlasphemyClient);
        field.setAccessible(false);
        return fieldValue;
    }

    @Nested
    class GetAccountInformation {
        @Test
        void getAccountInformationCanMapSuccessfulResponse() throws Exception {
            stubFor(get("/v2/core/account")
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("getAccountInformationSuccess.json")
                            ))));

            GetAccountInformationResponse getAccountInformationResponse = underTest.getAccountInformation().get();

            GetAccountInformationResponse expectedGetAccountInformationResponse = new GetAccountInformationResponse(
                    new GetAccountInformationResponse.DBCore(1),
                    new GetAccountInformationResponse.User(true, "username", 2, true, true)
            );
            assertThat(getAccountInformationResponse).isEqualTo(expectedGetAccountInformationResponse);

            verify(1, getRequestedFor(urlEqualTo("/v2/core/account")));
        }

        @Test
        void getAccountInformationCanMapUnauthorisedResponse() throws Exception {
            stubFor(get("/v2/core/account")
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(unauthorized()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("unauthorisedResponse.json")
                            ))));

            assertThatThrownBy(() -> underTest.getAccountInformation().get())
                    .isInstanceOf(ExecutionException.class)
                    .hasCause(new ResponseException(401, "Unauthorized"));

            verify(1, getRequestedFor(urlEqualTo("/v2/core/account")));
        }

        @Test
        void getAccountInformationCanMapUnknownErrorResponse() {
            stubFor(get("/v2/core/account")
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(aResponse().withStatus(405)));

            assertThatThrownBy(() -> underTest.getAccountInformation().get())
                    .isInstanceOf(ExecutionException.class)
                    .hasCause(new ResponseException(0, "Unable to parse the body as JSON ErrorResponse. []"));

            verify(1, getRequestedFor(urlEqualTo("/v2/core/account")));
        }

        @Test
        void getAccountInformationCanMapNoResponse() {
            stubFor(get("/v2/core/account")
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE)));

            assertThatThrownBy(() -> underTest.getAccountInformation().get())
                    .isInstanceOf(ExecutionException.class)
                    .hasCause(new EOFException("EOF reached while reading"));

            verify(getRequestedFor(urlEqualTo("/v2/core/account")));
        }

        @Test
        void getAccountInformationCanMapNonJsonResponse() {
            stubFor(get("/v2/core/account")
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(aResponse()
                            .withResponseBody(
                                    Body.ofBinaryOrText("<xml/>".getBytes(StandardCharsets.UTF_8),
                                            new ContentTypeHeader("application/xml"))
                            )));

            assertThatThrownBy(() -> underTest.getAccountInformation().get())
                    .isInstanceOf(ExecutionException.class)
                    .hasRootCauseInstanceOf(JsonParseException.class)
                    .rootCause().message().startsWith("Unexpected character ('<' (code 60))");

            verify(getRequestedFor(urlEqualTo("/v2/core/account")));
        }
    }

    @Nested
    class GetWallpapers {
        @ParameterizedTest
        @MethodSource("notSentQueryParamsWhenNotProvided")
        void getWallpapersDoesNotSendQueryParamIfNotProvided(String queryParam) throws Exception {
            GetWallpapersRequest getWallpapersRequest = GetWallpapersRequest.builder().build();

            stubFor(get(urlMatching("/v2/core/wallpapers\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("getWallpapersSuccessFullyPopulated.json")
                            ))));

            underTest.getWallpapers(getWallpapersRequest).get();

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
        void getWallpapersDoesSendQueryParamIfNotProvided(String queryParam, String expectedValue) throws Exception {
            GetWallpapersRequest getWallpapersRequest = GetWallpapersRequest.builder().build();

            stubFor(get(urlMatching("/v2/core/wallpapers\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("getWallpapersSuccessFullyPopulated.json")
                            ))));

            underTest.getWallpapers(getWallpapersRequest).get();

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
        void getWallpapersDoesSendQueryParamIfProvided(String field, String queryParam, Object value, String expectedValue) throws Exception {
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

            underTest.getWallpapers(getWallpapersRequest).get();

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
        void getWallpapersCanMapResponse(String response, GetWallpapersResponse expectedWallpapersResponse) throws Exception {
            GetWallpapersRequest getWallpapersRequest = GetWallpapersRequest.builder().build();

            stubFor(get(urlMatching("/v2/core/wallpapers\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(response))
                    ));

            GetWallpapersResponse getWallpapersResponse = underTest.getWallpapers(getWallpapersRequest).get();

            assertThat(getWallpapersResponse).isEqualTo(expectedWallpapersResponse);

            verify(1, getRequestedFor(
                    urlMatching("/v2/core/wallpapers\\?.*")));
        }

        public static Stream<Arguments> successfulResponse() throws Exception {
            return Stream.of(
                    arguments(readFile("getWallpapersSuccessFullyPopulated.json"), getWallpapersSuccessFullyPopulated()),
                    arguments(readFile("getWallpapersSuccessMinimalPopulated.json"), getWallpapersSuccessMinimalPopulated())
            );
        }

        private static GetWallpapersResponse getWallpapersSuccessFullyPopulated() {
            return new GetWallpapersResponse(
                    new GetWallpapersResponse.DBCore(
                            1,
                            new Endpoints(
                                    "https://api.digitalblasphemy.com/v2/core",
                                    "https://arcadia.digitalblasphemy.com",
                                    "https://cdn.digitalblasphemy.com",
                                    "https://digitalblasphemy.com"
                            ),
                            new GetWallpapersResponse.DBCore.Request(
                                    new GetWallpapersResponse.DBCore.Request.Query(
                                            2, 3, 4, Operator.EQUAL, List.of(5), 6,
                                            Operator.GREATER_THAN_OR_EQUAL, Operator.GREATER_THAN_OR_EQUAL, Operator.GREATER_THAN_OR_EQUAL,
                                            7, Operator.GREATER_THAN_OR_EQUAL, 8, List.of(9), 10,
                                            Order.ASCENDING, GetWallpapersOrderBy.NAME, 11, "search", true, true, true
                                    )
                            ),
                            12,
                            Map.of(
                                    "40", new Wallpaper(
                                            40,
                                            false,
                                            new Wallpaper.Comments(List.of(
                                                    new Wallpaper.Comments.Comment(
                                                            "41",
                                                            "author ID 3",
                                                            "author display 3",
                                                            "Content 4",
                                                            "42",
                                                            43
                                                    ),
                                                    new Wallpaper.Comments.Comment(
                                                            "44",
                                                            "author ID 4",
                                                            "author display 4",
                                                            "Content 5",
                                                            "45",
                                                            46
                                                    )
                                            )),
                                            "Content 6",
                                            false,
                                            "Valley I",
                                            new Wallpaper.Paths(
                                                    "/wallpaper/40",
                                                    "/thumbnail/48x49/valley_thumbnail_48x49.jpg",
                                                    "/sec/valley/"
                                            ),
                                            new Wallpaper.PickleJar(
                                                    "parent 2",
                                                    List.of("sibling 3", "sibling 4")
                                            ),
                                            "47",
                                            new Wallpaper.Resolutions(
                                                    List.of(
                                                            new Wallpaper.Resolutions.Resolution(
                                                                    "48x49",
                                                                    "48",
                                                                    "49",
                                                                    "/single/48x49/valley_single_48x49.jpg"
                                                            ),
                                                            new Wallpaper.Resolutions.Resolution(
                                                                    "50x51",
                                                                    "50",
                                                                    "51",
                                                                    "/single/50x51/valley_single_50x51.jpg"
                                                            )
                                                    ),
                                                    List.of(
                                                            new Wallpaper.Resolutions.Resolution(
                                                                    "52x53",
                                                                    "52",
                                                                    "53",
                                                                    "/dual/52x53/valley_dual_52x53.jpg"

                                                            ),

                                                            new Wallpaper.Resolutions
                                                                    .Resolution(

                                                                    "54x55",
                                                                    "54",

                                                                    "55",
                                                                    "/dual/54x55/valley_dual_54x55.jpg"
                                                            )
                                                    ),
                                                    List.of(
                                                            new Wallpaper.Resolutions.Resolution(
                                                                    "56x57",
                                                                    "56",
                                                                    "57",
                                                                    "/triple/56x57/valley_triple_56x57.jpg"
                                                            ),
                                                            new Wallpaper.Resolutions.Resolution(
                                                                    "58x59",
                                                                    "58",
                                                                    "59",
                                                                    "/triple/58x59/valley_triple_58x59.jpg"
                                                            )
                                                    ),
                                                    List.of(
                                                            new Wallpaper.Resolutions.Resolution(
                                                                    "60x61",
                                                                    "60",
                                                                    "61",
                                                                    "/mobile/60x61/valley_mobile_60x61.jpg"
                                                            ),
                                                            new Wallpaper.Resolutions.Resolution(
                                                                    "62x63",
                                                                    "62",
                                                                    "63",
                                                                    "/mobile/62x63/valley_mobile_62x63.jpg"
                                                            )
                                                    )
                                            ),
                                            "valley",
                                            Map.of(
                                                    "64", new Wallpaper.Tag(
                                                            64,
                                                            "Tag 3"
                                                    ),
                                                    "65", new Wallpaper.Tag(
                                                            65,
                                                            "Tag 4"
                                                    )
                                            ),
                                            66L
                                    ),
                                    "13", new Wallpaper(
                                            13,
                                            true,
                                            new Wallpaper.Comments(List.of(
                                                    new Wallpaper.Comments.Comment(
                                                            "14",
                                                            "author ID 1",
                                                            "author display 1",
                                                            "Content 1",
                                                            "15",
                                                            16
                                                    ),
                                                    new Wallpaper.Comments.Comment(
                                                            "17",
                                                            "author ID 2",
                                                            "author display 2",
                                                            "Content 2",
                                                            "18",
                                                            19
                                                    )
                                            )),
                                            "Content 3",
                                            true,
                                            "Vulcan",
                                            new Wallpaper.Paths(
                                                    "/wallpaper/13",
                                                    "/thumbnail/21x22/vulcan_thumbnail_21x22.jpg",
                                                    "/sec/vulcan/"
                                            ),
                                            new Wallpaper.PickleJar("parent 1", List.of("sibling 1", "sibling 2")),
                                            "20",
                                            new Wallpaper.Resolutions(
                                                    List.of(
                                                            new Wallpaper.Resolutions.Resolution(
                                                                    "21x22",
                                                                    "21",
                                                                    "22",
                                                                    "/single/21x22/vulcan_single_21x22.jpg"
                                                            ),
                                                            new Wallpaper.Resolutions.Resolution(
                                                                    "23x24",
                                                                    "23",
                                                                    "24",
                                                                    "/single/23x24/vulcan_single_23x24.jpg"
                                                            )
                                                    ),
                                                    List.of(
                                                            new Wallpaper.Resolutions.Resolution(
                                                                    "25x26",
                                                                    "25",
                                                                    "26",
                                                                    "/dual/25x26/vulcan_dual_25x26.jpg"
                                                            ),
                                                            new Wallpaper.Resolutions.Resolution(
                                                                    "27x28",
                                                                    "27",
                                                                    "28",
                                                                    "/dual/27x28/vulcan_dual_27x28.jpg"
                                                            )
                                                    ),
                                                    List.of(
                                                            new Wallpaper.Resolutions.Resolution(
                                                                    "29x30",
                                                                    "29",
                                                                    "30",
                                                                    "/triple/29x30/vulcan_triple_29x30.jpg"
                                                            ),
                                                            new Wallpaper.Resolutions.Resolution(
                                                                    "31x32",
                                                                    "31",
                                                                    "32",
                                                                    "/triple/31x32/vulcan_triple_31x32.jpg"
                                                            )
                                                    ),
                                                    List.of(
                                                            new Wallpaper.Resolutions.Resolution(
                                                                    "33x34",
                                                                    "33",
                                                                    "34",
                                                                    "/mobile/33x34/vulcan_mobile_33x34.jpg"
                                                            ),
                                                            new Wallpaper.Resolutions.Resolution(
                                                                    "35x36",
                                                                    "35",
                                                                    "36",
                                                                    "/mobile/35x36/vulcan_mobile_35x36.jpg"
                                                            )
                                                    )
                                            ),
                                            "vulcan",
                                            Map.of(
                                                    "37", new Wallpaper.Tag(
                                                            37,
                                                            "Tag 1"
                                                    ),
                                                    "38", new Wallpaper.Tag(
                                                            38,
                                                            "Tag 2"
                                                    )
                                            ),
                                            39L
                                    )
                            )
                    ),
                    List.of(13, 40)
            );
        }

        private static GetWallpapersResponse getWallpapersSuccessMinimalPopulated() {
            return new GetWallpapersResponse(
                    new GetWallpapersResponse.DBCore(
                            1,
                            new Endpoints(
                                    "https://api.digitalblasphemy.com/v2/core",
                                    "https://arcadia.digitalblasphemy.com",
                                    "https://cdn.digitalblasphemy.com",
                                    "https://digitalblasphemy.com"
                            ),
                            new GetWallpapersResponse.DBCore.Request(
                                    new GetWallpapersResponse.DBCore.Request.Query(
                                            null, null, null, Operator.EQUAL, null,
                                            null, null, null, null, 2,
                                            Operator.GREATER_THAN_OR_EQUAL, 3, null, 4, Order.ASCENDING, GetWallpapersOrderBy.NAME,
                                            5, null, false, false, false
                                    )
                            ),
                            6,
                            Map.of(
                                    "7", new Wallpaper(
                                            7,
                                            null,
                                            null,
                                            null,
                                            null,
                                            "Vulcan",
                                            new Wallpaper.Paths(
                                                    "/wallpaper/7",
                                                    "/thumbnail/8x9/vulcan_thumbnail_8x9.jpg",
                                                    "/sec/vulcan/"
                                            ),
                                            null,
                                            null,
                                            new Wallpaper.Resolutions(
                                                    List.of(
                                                            new Wallpaper.Resolutions.Resolution(
                                                                    "8x9",
                                                                    "8",
                                                                    "9",
                                                                    "/single/8x9/vulcan_single_8x9.jpg"
                                                            ),
                                                            new Wallpaper.Resolutions.Resolution(
                                                                    "10x11",
                                                                    "10",
                                                                    "11",
                                                                    "/single/10x11/vulcan_single_10x11.jpg"
                                                            )
                                                    ),
                                                    null,
                                                    null,
                                                    null
                                            ),
                                            null,
                                            null,
                                            null
                                    ),
                                    "12", new Wallpaper(
                                            12,
                                            null,
                                            null,
                                            null,
                                            null,
                                            "Valley I",
                                            new Wallpaper.Paths(
                                                    "/wallpaper/12",
                                                    "/thumbnail/13x14/valley_thumbnail_13x14.jpg",
                                                    "/sec/valley/"
                                            ),
                                            null,
                                            null,
                                            new Wallpaper.Resolutions(
                                                    List.of(
                                                            new Wallpaper.Resolutions.Resolution(
                                                                    "13x14",
                                                                    "13",
                                                                    "14",
                                                                    "/single/13x14/valley_single_13x14.jpg"
                                                            ),
                                                            new Wallpaper.Resolutions.Resolution(
                                                                    "15x16",
                                                                    "15",
                                                                    "16",
                                                                    "/single/15x16/valley_single_15x16.jpg"
                                                            )
                                                    ),
                                                    null,
                                                    null,
                                                    null
                                            ),
                                            null,
                                            null,
                                            null
                                    )
                            )
                    ),
                    List.of(7, 12)
            );
        }

        @Test
        void getWallpapersCanMapUnauthorisedResponse() throws Exception {
            GetWallpapersRequest getWallpapersRequest = GetWallpapersRequest.builder().build();

            stubFor(get(urlMatching("/v2/core/wallpapers\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(unauthorized()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("unauthorisedResponse.json")
                            ))));

            assertThatThrownBy(() -> underTest.getWallpapers(getWallpapersRequest).get())
                    .isInstanceOf(ExecutionException.class)
                    .hasCause(new ResponseException(401, "Unauthorized"));

            verify(1, getRequestedFor(
                    urlMatching("/v2/core/wallpapers\\?.*")));
        }

        @Test
        void getWallpapersCanMapBadRequestResponse() throws Exception {
            GetWallpapersRequest getWallpapersRequest = GetWallpapersRequest.builder().build();

            stubFor(get(urlMatching("/v2/core/wallpapers\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(badRequest()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("getWallpapersBadRequest.json")
                            ))));

            assertThatThrownBy(() -> underTest.getWallpapers(getWallpapersRequest).get())
                    .isInstanceOf(ExecutionException.class)
                    .hasCause(new ResponseException(400, "Bad Request", List.of(
                            "\"filter_date_day\" must be less than or equal to 31",
                            "\"limit\" must be greater than or equal to 1"
                    )));

            verify(1, getRequestedFor(
                    urlMatching("/v2/core/wallpapers\\?.*")));
        }

        @Test
        void getWallpapersCanMapUnknownErrorResponse() {
            GetWallpapersRequest getWallpapersRequest = GetWallpapersRequest.builder().build();

            stubFor(get(urlMatching("/v2/core/wallpapers\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(aResponse().withStatus(405)));

            assertThatThrownBy(() -> underTest.getWallpapers(getWallpapersRequest).get())
                    .isInstanceOf(ExecutionException.class)
                    .hasCause(new ResponseException(0, "Unable to parse the body as JSON ErrorResponse. []"));

            verify(1, getRequestedFor(
                    urlMatching("/v2/core/wallpapers\\?.*")));
        }

        @Test
        void getWallpapersCanMapNoResponse() {
            GetWallpapersRequest getWallpapersRequest = GetWallpapersRequest.builder().build();

            stubFor(get(urlMatching("/v2/core/wallpapers\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE)));

            assertThatThrownBy(() -> underTest.getWallpapers(getWallpapersRequest).get())
                    .isInstanceOf(ExecutionException.class)
                    .hasCause(new EOFException("EOF reached while reading"));

            verify(getRequestedFor(
                    urlMatching("/v2/core/wallpapers\\?.*")));
        }

        @Test
        void getWallpapersCanMapNonJsonResponse() {
            GetWallpapersRequest getWallpapersRequest = GetWallpapersRequest.builder().build();

            stubFor(get(urlMatching("/v2/core/wallpapers\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(aResponse()
                            .withResponseBody(
                                    Body.ofBinaryOrText("<xml/>".getBytes(StandardCharsets.UTF_8),
                                            new ContentTypeHeader("application/xml"))
                            )));

            assertThatThrownBy(() -> underTest.getWallpapers(getWallpapersRequest).get())
                    .isInstanceOf(ExecutionException.class)
                    .hasRootCauseInstanceOf(JsonParseException.class)
                    .rootCause().message().startsWith("Unexpected character ('<' (code 60))");

            verify(getRequestedFor(
                    urlMatching("/v2/core/wallpapers\\?.*")));
        }
    }

    @Nested
    class GetWallpaper {
        @ParameterizedTest
        @MethodSource("notSentQueryParamsWhenNotProvided")
        void getWallpaperDoesNotSendQueryParamIfNotProvided(String queryParam) throws Exception {
            GetWallpaperRequest getWallpaperRequest = GetWallpaperRequest.builder().wallpaperId(1).build();

            stubFor(get(urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("getWallpaperSuccessFullyPopulated.json")
                            ))));

            underTest.getWallpaper(getWallpaperRequest).get();

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
        void getWallpaperDoesSendQueryParamIfNotProvided(String queryParam, String expectedValue) throws Exception {
            GetWallpaperRequest getWallpaperRequest = GetWallpaperRequest.builder().wallpaperId(1).build();

            stubFor(get(urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("getWallpaperSuccessFullyPopulated.json")
                            ))));

            underTest.getWallpaper(getWallpaperRequest).get();

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
        void getWallpaperDoesSendQueryParamIfProvided(String field, String queryParam, Object value, String expectedValue) throws Exception {
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

            underTest.getWallpaper(getWallpaperRequest).get();

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
        void getWallpaperCanMapResponse(String response, Wallpaper expectedWallpaper) throws Exception {
            GetWallpaperRequest getWallpaperRequest = GetWallpaperRequest.builder().wallpaperId(1).build();

            stubFor(get(urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(response))
                    ));

            Wallpaper wallpaper = underTest.getWallpaper(getWallpaperRequest).get();

            assertThat(wallpaper).isEqualTo(expectedWallpaper);

            verify(1, getRequestedFor(
                    urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*")));
        }

        public static Stream<Arguments> successfulResponse() throws Exception {
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
        void getWallpaperCanMapUnauthorisedResponse() throws Exception {
            GetWallpaperRequest getWallpaperRequest = GetWallpaperRequest.builder().wallpaperId(1).build();

            stubFor(get(urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(unauthorized()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("unauthorisedResponse.json")
                            ))));

            assertThatThrownBy(() -> underTest.getWallpaper(getWallpaperRequest).get())
                    .isInstanceOf(ExecutionException.class)
                    .hasCause(new ResponseException(401, "Unauthorized"));

            verify(1, getRequestedFor(
                    urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*")));
        }

        @Test
        void getWallpaperCanMapBadRequestResponse() throws Exception {
            GetWallpaperRequest getWallpaperRequest = GetWallpaperRequest.builder().wallpaperId(1).build();

            stubFor(get(urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(badRequest()
                            .withHeader("Content-Type", "application/json")
                            .withResponseBody(new Body(
                                    readFile("getWallpaperBadRequest.json")
                            ))));

            assertThatThrownBy(() -> underTest.getWallpaper(getWallpaperRequest).get())
                    .isInstanceOf(ExecutionException.class)
                    .hasCause(new ResponseException(400, "Bad Request", List.of(
                            "\"filter_res_height\" must be greater than or equal to 1",
                            "\"filter_res_width\" must be greater than or equal to 1"
                    )));

            verify(1, getRequestedFor(
                    urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*")));
        }

        @Test
        void getWallpaperCanMapUnknownErrorResponse() {
            GetWallpaperRequest getWallpaperRequest = GetWallpaperRequest.builder().wallpaperId(1).build();

            stubFor(get(urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(aResponse().withStatus(405)));

            assertThatThrownBy(() -> underTest.getWallpaper(getWallpaperRequest).get())
                    .isInstanceOf(ExecutionException.class)
                    .hasCause(new ResponseException(0, "Unable to parse the body as JSON ErrorResponse. []"));

            verify(1, getRequestedFor(
                    urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*")));
        }

        @Test
        void getWallpaperCanMapNoResponse() {
            GetWallpaperRequest getWallpaperRequest = GetWallpaperRequest.builder().wallpaperId(1).build();

            stubFor(get(urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE)));

            assertThatThrownBy(() -> underTest.getWallpaper(getWallpaperRequest).get())
                    .isInstanceOf(ExecutionException.class)
                    .hasCause(new EOFException("EOF reached while reading"));

            verify(getRequestedFor(
                    urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*")));
        }

        @Test
        void getWallpaperCanMapNonJsonResponse() {
            GetWallpaperRequest getWallpaperRequest = GetWallpaperRequest.builder().wallpaperId(1).build();

            stubFor(get(urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*"))
                    .withHeader("Authorization", equalTo("Bearer apiKey"))
                    .willReturn(aResponse()
                            .withResponseBody(
                                    Body.ofBinaryOrText("<xml/>".getBytes(StandardCharsets.UTF_8),
                                            new ContentTypeHeader("application/xml"))
                            )));

            assertThatThrownBy(() -> underTest.getWallpaper(getWallpaperRequest).get())
                    .isInstanceOf(ExecutionException.class)
                    .hasRootCauseInstanceOf(JsonParseException.class)
                    .rootCause().message().startsWith("Unexpected character ('<' (code 60))");

            verify(getRequestedFor(
                    urlMatching("/v2/core/wallpaper/" + getWallpaperRequest.getWallpaperId() + "\\?.*")));
        }
    }

    @Nested
    class DownloadWallpaper {
        @Test
        void downloadWallpaperDoesSendShowWatermarkIfNotProvided() throws Exception {
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

            Path filename = Path.of(UUID.randomUUID().toString());

            underTest.downloadWallpaper(filename, downloadWallpaperRequest).get();

            verify(1, getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=true".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(1, getRequestedFor(urlMatching("/test.jpg")));

            Files.deleteIfExists(filename);
        }

        @Test
        void downloadWallpaperDoesSendShowWatermarkIfProvided() throws Exception {
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

            Path filename = Path.of(UUID.randomUUID().toString());

            underTest.downloadWallpaper(filename, downloadWallpaperRequest).get();

            verify(1, getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=false".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(1, getRequestedFor(urlMatching("/test.jpg")));

            Files.deleteIfExists(filename);
        }

        @Test
        void downloadWallpaperSendsPathParameters() throws Exception {
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

            Path filename = Path.of(UUID.randomUUID().toString());

            underTest.downloadWallpaper(filename, downloadWallpaperRequest).get();

            verify(1, getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=false".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(1, getRequestedFor(urlMatching("/test.jpg")));

            Files.deleteIfExists(filename);
        }

        @Test
        void downloadWallpaperCanMapSuccessfulResponseFullyPopulated() throws Exception {
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

            Path filename = Path.of(UUID.randomUUID().toString());

            underTest.downloadWallpaper(filename, downloadWallpaperRequest).get();

            verify(1, getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=false".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(1, getRequestedFor(urlMatching("/test.jpg")));

            Files.deleteIfExists(filename);
        }

        @Test
        void downloadWallpaperCanMapSuccessfulResponseMinimalPopulated() throws Exception {
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

            Path filename = Path.of(UUID.randomUUID().toString());

            underTest.downloadWallpaper(filename, downloadWallpaperRequest).get();

            verify(1, getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=false".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(1, getRequestedFor(urlMatching("/test.jpg")));

            Files.deleteIfExists(filename);
        }

        @Test
        void downloadWallpaperCanMapUnauthorisedResponseWhenGettingDownloadWallpaperResponse() throws Exception {
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

            Path filename = Path.of(UUID.randomUUID().toString());

            assertThatThrownBy(() -> underTest.downloadWallpaper(filename, downloadWallpaperRequest).get())
                    .isInstanceOf(ExecutionException.class)
                    .hasCause(new ResponseException(401, "Unauthorized"));

            assertThat(Files.notExists(filename)).isTrue();
            verify(1, getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=false.*".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(0, getRequestedFor(urlMatching("/test.jpg")));
        }

        @Test
        void downloadWallpaperCanMapUnauthorisedResponseWhenDownloadingFile() throws Exception {
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

            Path filename = Path.of(UUID.randomUUID().toString());

            assertThatThrownBy(() -> underTest.downloadWallpaper(filename, downloadWallpaperRequest).get())
                    .isInstanceOf(ExecutionException.class)
                    .hasCause(new ResponseException(401, "Unauthorized"));

            verify(1, getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=false.*".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(1, getRequestedFor(urlMatching("/test.jpg")));

            Files.deleteIfExists(filename);
        }

        @Test
        void downloadWallpaperCanMapBadRequestResponseWhenGettingDownloadWallpaperResponse() throws Exception {
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

            Path filename = Path.of(UUID.randomUUID().toString());

            assertThatThrownBy(() -> underTest.downloadWallpaper(filename, downloadWallpaperRequest).get())
                    .isInstanceOf(ExecutionException.class)
                    .hasCause(new ResponseException(400, "Bad Request", List.of(
                            "\"type\" must be one of [single, dual, triple, mobile]",
                            "\"width\" must be a number"
                    )));

            assertThat(Files.notExists(filename)).isTrue();
            verify(1, getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=false.*".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(0, getRequestedFor(urlMatching("/test.jpg")));
        }

        @Test
        void downloadWallpaperCanMapNotFoundResponseWhenDownloadingFile() throws Exception {
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

            Path filename = Path.of(UUID.randomUUID().toString());

            assertThatThrownBy(() -> underTest.downloadWallpaper(filename, downloadWallpaperRequest).get())
                    .isInstanceOf(ExecutionException.class)
                    .hasCause(new ResponseException(404, "Not Found", List.of(
                            "Object Not Found"
                    )));

            assertThat(Files.notExists(filename)).isTrue();
            verify(1, getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=false.*".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(1, getRequestedFor(urlMatching("/test.jpg")));
        }

        @Test
        void downloadWallpaperCanMapUnknownErrorResponseWhenDownloadingFile() throws Exception {
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

            Path filename = Path.of(UUID.randomUUID().toString());

            assertThatThrownBy(() -> underTest.downloadWallpaper(filename, downloadWallpaperRequest).get())
                    .isInstanceOf(ExecutionException.class)
                    .hasCause(new ResponseException(0, "Unable to parse the body as JSON ErrorResponse. []"));

            assertThat(Files.notExists(filename)).isTrue();
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

            Path filename = Path.of(UUID.randomUUID().toString());

            assertThatThrownBy(() -> underTest.downloadWallpaper(filename, downloadWallpaperRequest).get())
                    .isInstanceOf(ExecutionException.class)
                    .hasCause(new EOFException("EOF reached while reading"));

            assertThat(Files.notExists(filename)).isTrue();
            verify(getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=false.*".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(0, getRequestedFor(urlMatching("/test.jpg")));
        }

        @Test
        void downloadWallpaperNoResponseWhenDownloadingFile() throws Exception {
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

            Path filename = Path.of(UUID.randomUUID().toString());

            assertThatThrownBy(() -> underTest.downloadWallpaper(filename, downloadWallpaperRequest).get())
                    .isInstanceOf(ExecutionException.class)
                    .hasCause(new EOFException("EOF reached while reading"));

            assertThat(Files.notExists(filename)).isTrue();
            verify(getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=false.*".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(getRequestedFor(urlMatching("/test.jpg")));
        }

        @Test
        void downloadWallpaperCanMapNonJsonResponseWhenGettingDownloadWallpaperResponse() {
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
                    .willReturn(aResponse()
                            .withResponseBody(
                                    Body.ofBinaryOrText("<xml/>".getBytes(StandardCharsets.UTF_8),
                                            new ContentTypeHeader("application/xml"))
                            )));

            Path filename = Path.of(UUID.randomUUID().toString());

            assertThatThrownBy(() -> underTest.downloadWallpaper(filename, downloadWallpaperRequest).get())
                    .isInstanceOf(ExecutionException.class)
                    .hasRootCauseInstanceOf(JsonParseException.class)
                    .rootCause().message().startsWith("Unexpected character ('<' (code 60))");

            assertThat(Files.notExists(filename)).isTrue();
            verify(getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=false.*".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(0, getRequestedFor(urlMatching("/test.jpg")));
        }

        @Test
        void downloadWallpaperFileCannotBeCreatedWhenDownloadingFile() throws Exception {
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

            Path filename = Path.of("/does/not/exist/" + UUID.randomUUID());

            assertThatThrownBy(() -> underTest.downloadWallpaper(filename, downloadWallpaperRequest).get())
                    .isInstanceOf(ExecutionException.class)
                    .hasRootCause(new NoSuchFileException(filename.toString()));

            assertThat(Files.notExists(filename)).isTrue();
            verify(getRequestedFor(urlMatching("/v2/core/download/wallpaper/%s/%s/%s/%s\\?show_watermark=false.*".formatted(
                    downloadWallpaperRequest.getType(),
                    downloadWallpaperRequest.getWidth(),
                    downloadWallpaperRequest.getHeight(),
                    downloadWallpaperRequest.getWallpaperId()
            ))));
            verify(getRequestedFor(urlMatching("/test.jpg")));
        }
    }

}
