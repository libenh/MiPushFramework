package com.xiaomi.xmsf.push.control;

import android.content.Context;

import com.oasisfeng.condom.CondomContext;

import top.trumeet.common.Constants;

/**
 * Creates the restricted Context used for outbound MiPush operations.
 */
final class PushContextFactory {
    private PushContextFactory() {
    }

    static Context wrap(Context context) {
        return CondomContext.wrap(context, Constants.TAG_CONDOM,
                XMOutbound.create(context, Constants.TAG_CONDOM));
    }
}
