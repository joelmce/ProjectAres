package tc.oc.pgm.listing;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.command.CommandSender;
import tc.oc.api.minecraft.listing.ListingService;
import tc.oc.commons.core.commands.CommandFutureCallback;
import tc.oc.commons.core.concurrent.Flexecutor;
import tc.oc.minecraft.scheduler.Sync;

@Singleton
public class ListingCommands {

    private final ListingService listingService;
    private final Flexecutor executor;

    @Inject ListingCommands(ListingService listingService, @Sync Flexecutor executor) {
        this.listingService = listingService;
        this.executor = executor;
    }

    @Command(
        aliases = "announce",
        desc = "Announce the server to the listing service"
    )
    @CommandPermissions("pgm.listing.announce")
    public void announce(CommandContext args, CommandSender sender) {
        executor.callback(
            listingService.update(true),
            CommandFutureCallback.onSuccess(sender, args, reply -> {
                sender.sendMessage("Announced server to listing service");
            })
        );
    }
}
