package com.benjaminsproule.digitalblasphemy.client.model;

import edu.umd.cs.findbugs.annotations.NonNull;

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

    public GetWallpapersRequest(int filterDateDay, int filterDateMonth, int filterDateYear,
                                Operator filterDateOperator, List<Integer> filterGallery, float filterRating,
                                Operator filterRatingOperator, long filterResHeight, Operator filterResOperator,
                                Operator filterResOperatorHeight, Operator filterResOperatorWidth, long filterResWidth,
                                List<Integer> filterTag, int limit, Order order, GetWallpapersOrderBy orderBy,
                                int page, String s, boolean showComments, boolean showPickleJar,
                                boolean showResolutions) {
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
                    0, 0, 0, Operator.GREATER_THAN_OR_EQUAL, emptyList(),
                    0f, Operator.GREATER_THAN_OR_EQUAL, 0, Operator.GREATER_THAN_OR_EQUAL,
                    Operator.GREATER_THAN_OR_EQUAL, Operator.GREATER_THAN_OR_EQUAL, 0, emptyList(), 10,
                    Order.ASCENDING, GetWallpapersOrderBy.DATE, 1, "", false, false,
                    true
            );
        }

        public GetWallpapersRequest.Builder filterDateDay(int filterDateDay) {
            if (filterDateDay < 1 || filterDateDay > 31) {
                throw new IllegalArgumentException("Filter date day must be between 1 and 31.");
            }
            this.getWallPapersRequest.filterDateDay = filterDateDay;
            return this;
        }

        public GetWallpapersRequest.Builder filterDateMonth(int filterDateMonth) {
            if (filterDateMonth < 1 || filterDateMonth > 12) {
                throw new IllegalArgumentException("Filter date month must be between 1 and 12.");
            }
            this.getWallPapersRequest.filterDateMonth = filterDateMonth;
            return this;
        }

        public GetWallpapersRequest.Builder filterDateYear(int filterDateYear) {
            if (filterDateYear < 1997) {
                throw new IllegalArgumentException("Filter date year must be from 1997 inclusive.");
            }
            this.getWallPapersRequest.filterDateYear = filterDateYear;
            return this;
        }

        public GetWallpapersRequest.Builder filterDateOperator(@NonNull Operator filterDateOperator) {
            this.getWallPapersRequest.filterDateOperator = filterDateOperator;
            return this;
        }

        public GetWallpapersRequest.Builder filterGallery(@NonNull List<Integer> filterGallery) {
            this.getWallPapersRequest.filterGallery = filterGallery;
            return this;
        }

        public GetWallpapersRequest.Builder filterRating(float filterRating) {
            if (filterRating < 1 || filterRating > 5) {
                throw new IllegalArgumentException("Filter rating must be between 1 and 5.");
            }
            this.getWallPapersRequest.filterRating = filterRating;
            return this;
        }

        public GetWallpapersRequest.Builder filterRatingOperator(@NonNull Operator filterRatingOperator) {
            this.getWallPapersRequest.filterRatingOperator = filterRatingOperator;
            return this;
        }

        public GetWallpapersRequest.Builder filterResHeight(long filterResHeight) {
            this.getWallPapersRequest.filterResHeight = filterResHeight;
            return this;
        }

        public GetWallpapersRequest.Builder filterResOperator(@NonNull Operator filterResOperator) {
            this.getWallPapersRequest.filterResOperator = filterResOperator;
            return this;
        }

        public GetWallpapersRequest.Builder filterResOperatorHeight(@NonNull Operator filterResOperatorHeight) {
            this.getWallPapersRequest.filterResOperatorHeight = filterResOperatorHeight;
            return this;
        }

        public GetWallpapersRequest.Builder filterResOperatorWidth(@NonNull Operator filterResOperatorWidth) {
            this.getWallPapersRequest.filterResOperatorWidth = filterResOperatorWidth;
            return this;
        }

        public GetWallpapersRequest.Builder filterResWidth(long filterResWidth) {
            this.getWallPapersRequest.filterResWidth = filterResWidth;
            return this;
        }

        public GetWallpapersRequest.Builder filterTag(@NonNull List<Integer> filterTag) {
            this.getWallPapersRequest.filterTag = filterTag;
            return this;
        }

        public GetWallpapersRequest.Builder limit(int limit) {
            if (limit < 1 || limit > 50) {
                throw new IllegalArgumentException("Limit must be between 1 and 50.");
            }
            this.getWallPapersRequest.limit = limit;
            return this;
        }

        public GetWallpapersRequest.Builder order(@NonNull Order order) {
            this.getWallPapersRequest.order = order;
            return this;
        }

        public GetWallpapersRequest.Builder orderBy(@NonNull GetWallpapersOrderBy orderBy) {
            this.getWallPapersRequest.orderBy = orderBy;
            return this;
        }

        public GetWallpapersRequest.Builder page(int page) {
            if (page < 1) {
                throw new IllegalArgumentException("Page must be greater than 0.");
            }
            this.getWallPapersRequest.page = page;
            return this;
        }

        public GetWallpapersRequest.Builder s(@NonNull String s) {
            if (s.isBlank()) {
                throw new IllegalArgumentException("S must not be an empty or blank string.");
            }
            this.getWallPapersRequest.s = s;
            return this;
        }

        public GetWallpapersRequest.Builder showComments(boolean showComments) {
            this.getWallPapersRequest.showComments = showComments;
            return this;
        }

        public GetWallpapersRequest.Builder showPickleJar(boolean showPickleJar) {
            this.getWallPapersRequest.showPickleJar = showPickleJar;
            return this;
        }

        public GetWallpapersRequest.Builder showResolutions(boolean showResolutions) {
            this.getWallPapersRequest.showResolutions = showResolutions;
            return this;
        }

        public GetWallpapersRequest build() {
            return this.getWallPapersRequest;
        }
    }
}
