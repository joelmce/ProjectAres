package tc.oc.api.minecraft.listing;

import javax.annotation.Nullable;

import com.google.common.util.concurrent.ListenableFuture;
import tc.oc.api.message.types.Reply;

public interface ListingService {

    ListenableFuture<Reply> update(boolean online);

    @Nullable String sessionDigest();
}
