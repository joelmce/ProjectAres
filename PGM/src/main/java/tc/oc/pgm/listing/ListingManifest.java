package tc.oc.pgm.listing;

import tc.oc.commons.core.commands.CommandBinder;
import tc.oc.commons.core.inject.HybridManifest;
import tc.oc.minecraft.api.event.ListenerBinder;

public class ListingManifest extends HybridManifest {

    @Override
    protected void configure() {
        new ListenerBinder(binder())
            .bindListener().to(Announcer.class);
        new CommandBinder(binder())
            .register(ListingCommands.class);
    }
}
