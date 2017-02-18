package tc.oc.api.minecraft.listing;

import tc.oc.commons.core.inject.HybridManifest;

public class ListingManifest extends HybridManifest {

    @Override
    protected void configure() {
        bind(ListingConfiguration.class);
        bindAndExpose(ListingService.class).to(ListingServiceImpl.class);
    }
}
