package com.nihility.service;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.P;
import static top.trumeet.common.Constants.TAG_CONDOM;

import android.content.Context;
import android.os.Build;

import com.elvishew.xlog.XLog;
import com.nihility.Global;
import com.nihility.utils.OPlusRom;
import com.oasisfeng.condom.CondomContext;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import com.xiaomi.push.revival.NotificationsRevivalForSelfUpdated;
import com.xiaomi.push.service.BackgroundActivityStartEnabler;
import com.xiaomi.push.service.PullAllApplicationDataFromServerJob;
import com.xiaomi.push.service.XMPushService;
import com.xiaomi.push.service.XMPushServiceMessenger;
import com.xiaomi.xmsf.push.control.XMOutbound;

public class XMPushServiceAbility extends XMPushServiceListenerNotifier {
    public static XMPushService xmPushService;

    public XMPushServiceAbility(XMPushService pushService) {
        xmPushService = pushService;
        Global.RegistrationRecorder().initContext(pushService);
        if (OPlusRom.isOPlus()) {
            XLog.tag("XMPushService").i("OPlus/C17 mode colorOs=" + OPlusRom.colorOsVersion()
                    + " versionCode=105 startService path");
        }
        condomContext(pushService);
        initListeners(pushService);
    }

    private void initListeners(XMPushService pushService) {
        addListener(new RegisterRecordAbility(new RegisterRecorder(pushService)));
        addListener(new ForegroundAbility(new ForegroundHelper(pushService)));
        addListener(new MessengerAbility(new XMPushServiceMessenger(pushService)));
        if (SDK_INT > P) {
            addListener(new XMPushServiceListener() {
                @Override
                public void created() {
                    BackgroundActivityStartEnabler.initialize(pushService);
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            addListener(new NotificationsRevivalAbility(
                    new NotificationsRevivalForSelfUpdated(pushService, sbn -> sbn.getTag() == null)));
        }
        addListener(new XMPushServiceListener() {
            @Override
            public void connectionStatusChanged(ConnectionStatus connectionStatus) {
                if (connectionStatus == ConnectionStatus.connected) {
                    pushService.executeJob(new PullAllApplicationDataFromServerJob(pushService));
                }
            }
        });
    }

    private static void condomContext(XMPushService pushService) {
        Context mBase = pushService.getBaseContext();
        JavaCalls.setField(pushService, "mBase",
                CondomContext.wrap(mBase, TAG_CONDOM,
                        XMOutbound.create(mBase, XMPushServiceAbility.class.getSimpleName())));
    }
}
