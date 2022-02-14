package gitlet;
import java.io.File;
import java.io.Serializable;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Haoqing Xuan
 */
/** define the blob class.
 * @author Haoqing Xuan. */
public class Blobs implements Serializable {
    /** keep the content of the file. */
    private byte[] content;
    /** keep the contentSHA1 of the file.*/
    private String contentID;

    /** initialize blobs.
     * @param file */
    public Blobs(File file) {
        content = Utils.readContents(file);
        contentID = Utils.sha1(content);
    }
    /** get the content.
     * @return content. */
    public byte[] getContent() {
        return content;
    }
    /** get the contentID.
     * @return contentID. */
    public String getContentID() {
        return contentID;
    }
}

