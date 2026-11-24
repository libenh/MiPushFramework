package com.nihility.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;
import com.nihility.Global;
import com.nihility.utils.OPlusRom;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.xmsf.R;

import top.trumeet.common.Constants;
import top.trumeet.common.utils.Utils;
import top.trumeet.mipush.provider.db.EventDb;
import top.trumeet.mipush.provider.db.RegisteredApplicationDb;
import top.trumeet.mipush.provider.entities.Event;
import top.trumeet.mipush.provider.event.type.RegistrationType;
import top.trumeet.mipush.provider.entities.RegisteredApplication;

/**
 * 记录应用注册请求；C17 下加强日志便于排查「注册没反应」。
 */
public class RegisterRecorder {
    private final String TAG = RegisterRecorder.class.getSimpleName();
    private final Logger logger = XLog.tag(TAG).build();
    private final Context context;

    public RegisterRecorder(Context context) {
        this.context = context;
    }

    public void recordRegisterRequest(Intent intent) {
        try {
            if (!isRegisterAppRequest(intent)) {
                return;
            }

            String pkg = intent.getStringExtra(Constants.EXTRA_MI_PUSH_PACKAGE);
            if (pkg == null) {
                // 兼容其他 extra 键
                pkg = intent.getStringExtra("mipush_app_package");
            }
            if (pkg == null) {
                logger.e("Package name is NULL! extras=" + intent.getExtras());
                return;
            }

            logger.i("REGISTER_REQUEST pkg=" + pkg
                    + " action=" + intent.getAction()
                    + " oplus=" + OPlusRom.isOPlus());
            showRegisterToastIfUserAllow(
                    RegisteredApplicationDb.registerApplication(pkg));
            saveRegisterAppRecord(pkg);
        } catch (RuntimeException e) {
            logger.e("XMPushService::onHandleIntent: ", e);
            toastErrorMessage(e);
        }
    }

    void toastErrorMessage(RuntimeException e) {
        Utils.makeText(context, context.getString(R.string.common_err, e.getMessage()), Toast.LENGTH_LONG);
    }

    void saveRegisterAppRecord(String pkg) {
        EventDb.insertEvent(Event.ResultType.OK,
                new RegistrationType(null, pkg, null)
        );
    }

    boolean isRegisterAppRequest(Intent intent) {
        return intent != null && PushConstants.MIPUSH_ACTION_REGISTER_APP.equals(intent.getAction());
    }

    void showRegisterToastIfUserAllow(RegisteredApplication application) {
        if (application == null) {
            logger.w("RegisteredApplication is null after registerApplication");
            return;
        }
        if (canShowRegisterNotification(application)) {
            showRegisterNotification(application);
        } else {
            Log.e("XMPushService Bridge", "Notification disabled");
        }
    }

    void showRegisterNotification(RegisteredApplication application) {
        CharSequence appName = Global.ApplicationNameCache()
                .getAppName(context, application.getPackageName());
        CharSequence usedString = context.getString(R.string.notification_registerAllowed, appName);
        Utils.makeText(context, usedString, Toast.LENGTH_SHORT);
    }

    boolean canShowRegisterNotification(RegisteredApplication application) {
        boolean notificationOnRegister = Global.ConfigCenter().isNotificationOnRegister(context);
        notificationOnRegister = notificationOnRegister && application.isNotificationOnRegister();
        return notificationOnRegister;
    }
}
