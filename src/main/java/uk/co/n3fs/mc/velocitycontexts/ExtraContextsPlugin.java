package uk.co.n3fs.mc.velocitycontexts;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.context.ContextCalculator;
import me.lucko.luckperms.api.context.MutableContextSet;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;

import java.util.Optional;

@Plugin(
    id = "extra-contexts",
    name = "ExtraContexts-Velocity",
    version = "VERSION",
    authors = {"md678685"},
    dependencies = {
        @Dependency(id = "luckperms")
    }
)
public class ExtraContextsPlugin {

    @Inject private ProxyServer proxy;
    @Inject private Logger logger;

    private LuckPermsApi lpApi;

    @Subscribe
    public void onProxyInit(ProxyInitializeEvent event) {
        logger.info("Starting ExtraContextsVelocity v" + getVersion());

        Optional<LuckPermsApi> lpOpt = LuckPerms.getApiSafe();
        if (!lpOpt.isPresent()) {
            throw new RuntimeException("LuckPerms is missing!");
        }

        lpApi = lpOpt.get();
        lpApi.registerContextCalculator(new ContextCalculator<Player>() {
            @Override
            public @NonNull MutableContextSet giveApplicableContext(@NonNull Player subject, @NonNull MutableContextSet accumulator) {
                final Optional<ServerConnection> optServer = subject.getCurrentServer();
                optServer.ifPresent(serverConnection -> accumulator.add("connected-to", serverConnection.getServerInfo().getName()));

                return accumulator;
            }
        });
    }

    private PluginDescription getDescription() {
        return proxy.getPluginManager().getPlugin("extra-contexts").get().getDescription();
    }

    private String getVersion() {
        return getDescription().getVersion().get();
    }
}
