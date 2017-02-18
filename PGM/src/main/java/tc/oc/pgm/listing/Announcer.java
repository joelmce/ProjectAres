package tc.oc.pgm.listing;

import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import tc.oc.api.minecraft.listing.ListingService;
import tc.oc.commons.core.concurrent.Flexecutor;
import tc.oc.commons.core.logging.Loggers;
import tc.oc.commons.core.util.SystemFutureCallback;
import tc.oc.minecraft.api.event.Enableable;
import tc.oc.minecraft.scheduler.Sync;
import tc.oc.pgm.match.Match;

@Singleton
public class Announcer implements Listener, Enableable {

    private final Logger logger;
    private final Provider<Match> matchProvider;
    private final ListingService listingService;
    private final Flexecutor syncExecutor;

    @Inject Announcer(Loggers loggers, Provider<Match> matchProvider, ListingService listingService, @Sync(defer = true) Flexecutor syncExecutor) {
        this.logger = loggers.get(getClass());
        this.matchProvider = matchProvider;
        this.listingService = listingService;
        this.syncExecutor = syncExecutor;
    }

    @Override
    public void enable() {
        // Don't announce until we are ready to receive the ping
        syncExecutor.execute(this::announce);
    }

    @Override
    public void disable() {
        listingService.update(false);
    }

    void announce() {
        syncExecutor.callback(
            listingService.update(true),
            SystemFutureCallback.onSuccess(
                x -> logger.info("Announced server to listing service")
            )
        );
    }

    @EventHandler
    private void onPing(ServerListPingEvent event) {
        event.getExtra().put("pgm", new Info());
    }

    private class Info {
        private class Map {
            final String name;
            final @Nullable String icon;

            private Map(String name, String icon) {
                this.name = name;
                this.icon = icon;
            }
        }

        final @Nullable String session = listingService.sessionDigest();
        final Map map;
        final int participants;
        final int observers;

        Info() {
            final Match match = matchProvider.get();
            this.map = new Map(match.getMap().getName(),
                               match.getMap().getThumbnailUri().orElse(null));
            this.participants = match.getParticipatingPlayers().size();
            this.observers = match.getObservingPlayers().size();
        }
    }
}
