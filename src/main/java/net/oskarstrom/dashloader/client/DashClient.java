package net.oskarstrom.dashloader.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.oskarstrom.dashloader.DashLoader;
import net.oskarstrom.dashloader.util.DashReport;

import java.time.Instant;

@Environment(EnvType.CLIENT)
public class DashClient implements PreLaunchEntrypoint {


    @Override
    public void onPreLaunch() {
        DashReport.addTime(Instant.now(), "From beginning");
        new DashLoader(Thread.currentThread().getContextClassLoader());
    }
}
