package com.benjaminsproule.digitalblasphemy.client.model;

import java.util.List;

import static java.util.Collections.emptyList;

public class GetWallpapersRequest {
    private int filterDateDay;
    private int filterDateMonth;
    private int filterDateYear;
    private Operator filterDateOperator;
    private List<Integer> filterGallery;
    private float filterRating;
    private Operator filterRatingOperator;
    private long filterResHeight;
    private Operator filterResOperator;
    private Operator filterResOperatorHeight;
    private Operator filterResOperatorWidth;
    private long filterResWidth;
    private List<Integer> filterTag;
    private int limit;
    private Order order;
    private GetWallpapersOrderBy orderBy;
    private int page;
    private String s;
    private boolean showComments;
    private boolean showPickleJar;
    private boolean showResolutions;

    public GetWallpapersRequest(int filterDateDay, int filterDateMonth, int filterDateYear, Operator filterDateOperator, List<Integer> filterGallery, float filterRating, Operator filterRatingOperator, long filterResHeight, Operator filterResOperator, Operator filterResOperatorHeight, Operator filterResOperatorWidth, long filterResWidth, List<Integer> filterTag, int limit, Order order, GetWallpapersOrderBy orderBy, int page, String s, boolean showComments, boolean showPickleJar, boolean showResolutions) {
        this.filterDateDay = filterDateDay;
        this.filterDateMonth = filterDateMonth;
        this.filterDateYear = filterDateYear;
        this.filterDateOperator = filterDateOperator;
        this.filterGallery = filterGallery;
        this.filterRating = filterRating;
        this.filterRatingOperator = filterRatingOperator;
        this.filterResHeight = filterResHeight;
        this.filterResOperator = filterResOperator;
        this.filterResOperatorHeight = filterResOperatorHeight;
        this.filterResOperatorWidth = filterResOperatorWidth;
        this.filterResWidth = filterResWidth;
        this.filterTag = filterTag;
        this.limit = limit;
        this.order = order;
        this.orderBy = orderBy;
        this.page = page;
        this.s = s;
        this.showComments = showComments;
        this.showPickleJar = showPickleJar;
        this.showResolutions = showResolutions;
    }

    public int getFilterDateDay() {
        return this.filterDateDay;
    }

    public int getFilterDateMonth() {
        return this.filterDateMonth;
    }

    public int getFilterDateYear() {
        return this.filterDateYear;
    }

    public Operator getFilterDateOperator() {
        return this.filterDateOperator;
    }

    public List<Integer> getFilterGallery() {
        return this.filterGallery;
    }

    public float getFilterRating() {
        return this.filterRating;
    }

    public Operator getFilterRatingOperator() {
        return this.filterRatingOperator;
    }

    public long getFilterResHeight() {
        return this.filterResHeight;
    }

    public Operator getFilterResOperator() {
        return this.filterResOperator;
    }

    public Operator getFilterResOperatorHeight() {
        return this.filterResOperatorHeight;
    }

    public Operator getFilterResOperatorWidth() {
        return this.filterResOperatorWidth;
    }

    public long getFilterResWidth() {
        return this.filterResWidth;
    }

    public List<Integer> getFilterTag() {
        return this.filterTag;
    }

    public int getLimit() {
        return this.limit;
    }

    public Order getOrder() {
        return this.order;
    }

    public GetWallpapersOrderBy getOrderBy() {
        return this.orderBy;
    }

    public int getPage() {
        return this.page;
    }

    public String getS() {
        return this.s;
    }

    public boolean isShowComments() {
        return this.showComments;
    }

    public boolean isShowPickleJar() {
        return this.showPickleJar;
    }

    public boolean isShowResolutions() {
        return this.showResolutions;
    }

    public static GetWallpapersRequest.Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final GetWallpapersRequest getWallPapersRequest;

        private Builder() {
            this.getWallPapersRequest = new GetWallpapersRequest(
                    0, 0, 0, Operator.GreaterThanOrEqual, emptyList(),
                    0f, Operator.GreaterThanOrEqual, 0, Operator.GreaterThanOrEqual,
                    Operator.GreaterThanOrEqual, Operator.GreaterThanOrEqual, 0, emptyList(), 10,
                    Order.ASCENDING, GetWallpapersOrderBy.DATE, 1, "", false, false,
                    true
            );
        }

        public int filterDateDay(int filterDateDay) {
            return getWallPapersRequest.filterDateDay = filterDateDay;
        }

        public int filterDateMonth(int filterDateMont) {
            return getWallPapersRequest.filterDateMonth = filterDateMont;
        }

        public int filterDateYear(int filterDateYear) {
            return getWallPapersRequest.filterDateYear = filterDateYear;
        }

        public Operator filterDateOperator(Operator filterDateOperator) {
            return getWallPapersRequest.filterDateOperator = filterDateOperator;
        }

        public List<Integer> filterGallery(List<Integer> filterGallery) {
            return getWallPapersRequest.filterGallery = filterGallery;
        }

        public float filterRating(float filterRating) {
            return getWallPapersRequest.filterRating = filterRating;
        }

        public Operator filterRatingOperator(Operator filterRatingOperator) {
            return getWallPapersRequest.filterRatingOperator = filterRatingOperator;
        }

        public long filterResHeight(long filterResHeight) {
            return getWallPapersRequest.filterResHeight = filterResHeight;
        }

        public Operator filterResOperator(Operator filterResOperator) {
            return getWallPapersRequest.filterResOperator = filterResOperator;
        }

        public Operator filterResOperatorHeight(Operator filterResOperatorHeight) {
            return getWallPapersRequest.filterResOperatorHeight = filterResOperatorHeight;
        }

        public Operator filterResOperatorWidth(Operator filterResOperatorWidth) {
            return getWallPapersRequest.filterResOperatorWidth = filterResOperatorWidth;
        }

        public long filterResWidth(long filterResWidth) {
            return getWallPapersRequest.filterResWidth = filterResWidth;
        }

        public List<Integer> filterTag(List<Integer> filterTag) {
            return getWallPapersRequest.filterTag = filterTag;
        }

        public int limit(int limit) {
            return getWallPapersRequest.limit = limit;
        }

        public Order order(Order order) {
            return getWallPapersRequest.order = order;
        }

        public GetWallpapersOrderBy orderBy(GetWallpapersOrderBy orderBy) {
            return getWallPapersRequest.orderBy = orderBy;
        }

        public int page(int page) {
            return getWallPapersRequest.page = page;
        }

        public String s(String s) {
            return getWallPapersRequest.s = s;
        }

        public boolean showComments(boolean showComments) {
            return getWallPapersRequest.showComments = showComments;
        }

        public boolean showPickleJar(boolean showPickleJar) {
            return getWallPapersRequest.showPickleJar = showPickleJar;
        }

        public boolean showResolutions(boolean showResolutions) {
            return getWallPapersRequest.showResolutions = showResolutions;
        }

        public GetWallpapersRequest build() {
            return getWallPapersRequest;
        }
    }
}
