package tc.oc.net;

import java.util.Base64;

public interface UriUtils {

    static String dataUri(String mime, byte[] data) {
        return "data:" + mime + ";base64," + Base64.getEncoder().encodeToString(data);
    }
}
