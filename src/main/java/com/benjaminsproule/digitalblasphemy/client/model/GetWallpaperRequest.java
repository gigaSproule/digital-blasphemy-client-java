package com.benjaminsproule.digitalblasphemy.client.model;

public class GetWallpaperRequest {
    private int wallpaperId;
    private long filterResHeight;
    private Operator filterResOperator;
    private Operator filterResOperatorHeight;
    private Operator filterResOperatorWidth;
    private long filterResWidth;
    private boolean showComments;
    private boolean showPickleJar;
    private boolean showResolutions;

    private GetWallpaperRequest(
            int wallpaperId,
            long filterResHeight,
            Operator filterResOperator,
            Operator filterResOperatorHeight,
            Operator filterResOperatorWidth,
            long filterResWidth,
            boolean showComments,
            boolean showPickleJar,
            boolean showResolutions
    ) {
        this.wallpaperId = wallpaperId;
        this.filterResHeight = filterResHeight;
        this.filterResOperator = filterResOperator;
        this.filterResOperatorHeight = filterResOperatorHeight;
        this.filterResOperatorWidth = filterResOperatorWidth;
        this.filterResWidth = filterResWidth;
        this.showComments = showComments;
        this.showPickleJar = showPickleJar;
        this.showResolutions = showResolutions;
    }

    public int getWallpaperId() {
        return this.wallpaperId;
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

    public boolean isShowComments() {
        return this.showComments;
    }

    public boolean isShowPickleJar() {
        return this.showPickleJar;
    }

    public boolean isShowResolutions() {
        return this.showResolutions;
    }

    public static GetWallpaperRequest.Builder builder() {
        return new GetWallpaperRequest.Builder();
    }

    public static class Builder {
        private final GetWallpaperRequest getWallPaperRequest;

        private Builder() {
            this.getWallPaperRequest = new GetWallpaperRequest(
                    0, 0, Operator.GREATER_THAN_OR_EQUAL, Operator.GREATER_THAN_OR_EQUAL, Operator.GREATER_THAN_OR_EQUAL,
                    0, false, false, true
            );
        }

        public GetWallpaperRequest.Builder wallpaperId(int wallpaperId) {
            if (wallpaperId <= 0) {
                throw new IllegalArgumentException("Wallpaper ID must be greater than 0.");
            }
            this.getWallPaperRequest.wallpaperId = wallpaperId;
            return this;
        }

        public GetWallpaperRequest.Builder filterResHeight(int filterResHeight) {
            this.getWallPaperRequest.filterResHeight = filterResHeight;
            return this;
        }

        public GetWallpaperRequest.Builder filterResOperator(Operator filterResOperator) {
            this.getWallPaperRequest.filterResOperator = filterResOperator;
            return this;
        }

        public GetWallpaperRequest.Builder filterResOperatorHeight(Operator filterResOperatorHeight) {
            this.getWallPaperRequest.filterResOperatorHeight = filterResOperatorHeight;
            return this;
        }

        public GetWallpaperRequest.Builder filterResOperatorWidth(Operator filterResOperatorWidth) {
            this.getWallPaperRequest.filterResOperatorWidth = filterResOperatorWidth;
            return this;
        }

        public GetWallpaperRequest.Builder filterResWidth(int filterResWidth) {
            this.getWallPaperRequest.filterResWidth = filterResWidth;
            return this;
        }

        public GetWallpaperRequest.Builder showComments(boolean showComments) {
            this.getWallPaperRequest.showComments = showComments;
            return this;
        }

        public GetWallpaperRequest.Builder showPickleJar(boolean showPickleJar) {
            this.getWallPaperRequest.showPickleJar = showPickleJar;
            return this;
        }

        public GetWallpaperRequest.Builder showResolutions(boolean showResolutions) {
            this.getWallPaperRequest.showResolutions = showResolutions;
            return this;
        }

        public GetWallpaperRequest build() {
            if (this.getWallPaperRequest.wallpaperId == 0) {
                throw new IllegalStateException("Wallpaper ID must be provided.");
            }
            return this.getWallPaperRequest;
        }
    }
}
