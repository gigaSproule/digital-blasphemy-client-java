package com.benjaminsproule.digitalblasphemy.client.model;

import edu.umd.cs.findbugs.annotations.NonNull;

public class DownloadWallpaperRequest {
    private WallpaperType type;
    private int width;
    private int height;
    private int wallpaperId;
    private boolean showWatermark;

    private DownloadWallpaperRequest(
            WallpaperType type,
            int width,
            int height,
            int wallpaperId,
            boolean showWatermark
    ) {
        this.type = type;
        this.width = width;
        this.height = height;
        this.wallpaperId = wallpaperId;
        this.showWatermark = showWatermark;
    }

    public WallpaperType getType() {
        return this.type;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWallpaperId() {
        return this.wallpaperId;
    }

    public boolean isShowWatermark() {
        return this.showWatermark;
    }

    public static DownloadWallpaperRequest.Builder builder() {
        return new DownloadWallpaperRequest.Builder();
    }

    public static class Builder {
        private final DownloadWallpaperRequest downloadWallpaperRequest;

        private Builder() {
            this.downloadWallpaperRequest = new DownloadWallpaperRequest(
                    WallpaperType.Single, 0, 0, 0, true
            );
        }

        public DownloadWallpaperRequest.Builder type(@NonNull WallpaperType type) {
            this.downloadWallpaperRequest.type = type;
            return this;
        }

        public DownloadWallpaperRequest.Builder wallpaperId(int wallpaperId) {
            if (wallpaperId <= 0) {
                throw new IllegalArgumentException("Wallpaper ID must be greater than 0.");
            }
            this.downloadWallpaperRequest.wallpaperId = wallpaperId;
            return this;
        }

        public DownloadWallpaperRequest.Builder width(int width) {
            if (width <= 0) {
                throw new IllegalArgumentException("Width must be greater than 0.");
            }
            this.downloadWallpaperRequest.width = width;
            return this;
        }

        public DownloadWallpaperRequest.Builder height(int height) {
            if (height <= 0) {
                throw new IllegalArgumentException("Height must be greater than 0.");
            }
            this.downloadWallpaperRequest.height = height;
            return this;
        }

        public DownloadWallpaperRequest.Builder showWatermark(boolean showWatermark) {
            this.downloadWallpaperRequest.showWatermark = showWatermark;
            return this;
        }

        public DownloadWallpaperRequest build() {
            if (this.downloadWallpaperRequest.width == 0) {
                throw new IllegalStateException("Width must be provided.");
            }
            if (this.downloadWallpaperRequest.height == 0) {
                throw new IllegalStateException("Height must be provided.");
            }
            if (this.downloadWallpaperRequest.wallpaperId == 0) {
                throw new IllegalStateException("Wallpaper ID must be provided.");
            }
            return this.downloadWallpaperRequest;
        }
    }
}
