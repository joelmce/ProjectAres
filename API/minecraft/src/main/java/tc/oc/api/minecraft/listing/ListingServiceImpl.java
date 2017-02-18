package tc.oc.api.minecraft.listing;

import java.net.URISyntaxException;
import java.security.SecureRandom;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.api.client.util.Throwables;
import com.google.common.util.concurrent.ListenableFuture;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import tc.oc.api.http.HttpClient;
import tc.oc.api.http.HttpOption;
import tc.oc.api.message.types.Reply;
import tc.oc.minecraft.api.server.LocalServer;

@Singleton
class ListingServiceImpl implements ListingService {

    private final HttpClient http;
    private final ListingConfiguration config;
    private final LocalServer localServer;
    private final SecureRandom random = new SecureRandom();

    private @Nullable String sessionId;
    private @Nullable String sessionDigest;

    @Inject ListingServiceImpl(HttpClient http, ListingConfiguration config, LocalServer localServer) {
        this.http = http;
        this.config = config;
        this.localServer = localServer;
    }

    @Override
    public @Nullable String sessionDigest() {
        return sessionDigest;
    }

    private String url(String path) {
        try {
            return config.listingUrl()
                         .toURI()
                         .resolve(path)
                         .toString();
        } catch(URISyntaxException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public ListenableFuture<Reply> update(boolean online) {
        if(sessionId == null) {
            final byte[] bytes = new byte[20];
            random.nextBytes(bytes);
            sessionId = Hex.encodeHexString(bytes);
            sessionDigest = DigestUtils.sha1Hex(sessionId);
        }

        final String sid = sessionId;

        if(!online) {
            sessionId = sessionDigest = null;
        }

        return http.post(url("/announce"), new ListingUpdate() {
            @Override
            public @Nullable String host() {
                return config.serverHost();
            }

            @Override
            public int port() {
                return config.serverPort().orElseGet(localServer::getPort);
            }

            @Override
            public boolean online() {
                return online;
            }

            @Override
            public String session() {
                return sid;
            }
        }, Reply.class, HttpOption.INFINITE_RETRY);
    }
}
