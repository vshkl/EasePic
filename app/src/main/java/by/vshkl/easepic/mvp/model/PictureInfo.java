package by.vshkl.easepic.mvp.model;

public class PictureInfo {

    private String path;
    private String mimeType;
    private String size;
    private String width;
    private String height;
    private String date;
//    private String camera;
//    private String EXIF;
//    private String dateTaken;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

//    public String getCamera() {
//        return camera;
//    }
//
//    public void setCamera(String camera) {
//        this.camera = camera;
//    }
//
//    public String getEXIF() {
//        return EXIF;
//    }
//
//    public void setEXIF(String EXIF) {
//        this.EXIF = EXIF;
//    }
//
//    public String getDateTaken() {
//        return dateTaken;
//    }
//
//    public void setDateTaken(String dateTaken) {
//        this.dateTaken = dateTaken;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PictureInfo that = (PictureInfo) o;

        return path.equals(that.path);

    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PictureInfo{");
        sb.append("path='").append(path).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
