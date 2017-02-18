package tc.oc.api.minecraft.listing;

import java.net.URL;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import javax.inject.Inject;

import tc.oc.commons.core.configuration.ConfigUtils;
import tc.oc.minecraft.api.configuration.Configuration;
import tc.oc.minecraft.api.configuration.ConfigurationSection;

class ListingConfiguration {

    private final ConfigurationSection config;

    @Inject ListingConfiguration(Configuration root) {
        this.config = root.needSection("announce");
    }

    public boolean enabled() {
        return config.needBoolean("enabled");
    }

    public URL listingUrl() {
        return ConfigUtils.needUrl(config, "listing-url");
    }

    public @Nullable String serverHost() {
        return config.getString("server-host");
    }

    public OptionalInt serverPort() {
        final int port = config.getInt("server-port");
        return port != 0 ? OptionalInt.of(port) : OptionalInt.empty();
    }
}
