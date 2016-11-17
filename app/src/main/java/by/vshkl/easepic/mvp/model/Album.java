package by.vshkl.easepic.mvp.model;

public class Album {

    public enum StorageType {
        INTERNAL,
        EXTERNAL
    }

    private String bucketId;
    private String bucketDate;
    private String bucketName;
    private String bucketThumbnail;
    private StorageType bucketStorageType;

    public Album() {
    }

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public String getBucketDate() {
        return bucketDate;
    }

    public void setBucketDate(String bucketDate) {
        this.bucketDate = bucketDate;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketThumbnail() {
        return bucketThumbnail;
    }

    public void setBucketThumbnail(String bucketThumbnail) {
        this.bucketThumbnail = bucketThumbnail;
    }

    public StorageType getBucketStorageType() {
        return bucketStorageType;
    }

    public void setBucketStorageType(StorageType bucketStorageType) {
        this.bucketStorageType = bucketStorageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Album album = (Album) o;

        return bucketId.equals(album.bucketId);

    }

    @Override
    public int hashCode() {
        return bucketId.hashCode();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Album{");
        sb.append("bucketId='").append(bucketId).append('\'');
        sb.append(", bucketName='").append(bucketName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
