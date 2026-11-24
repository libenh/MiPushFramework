package top.trumeet.mipushframework;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.xiaomi.xmsf.BuildConfig;
import com.xiaomi.xmsf.R;

import top.trumeet.mipushframework.main.HelpPage;

/**
 * Legacy helpers still used by non-Compose entry points.
 * Settings UI "About" is primarily composed in SettingsPage.kt.
 */
public class MainPageOperation {
    private static final String REPO_URL = "https://github.com/libenh/MiPushFramework";
    private static final String RELEASES_URL = "https://github.com/libenh/MiPushFramework/releases";

    private final Context context;

    public MainPageOperation(Context context) {
        this.context = context;
    }

    public void gotoHelpActivity() {
        Intent intent = new Intent();
        intent.setClass(context, HelpPage.class);
        context.startActivity(intent);
    }

    public void showAboutDialog() {
        String versionInfo = String.format(
                "name: %s\ncode: %d\nflavor: %s\ntype: %s\nsdk: %d\ndevice: %s %s",
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE,
                BuildConfig.FLAVOR,
                BuildConfig.BUILD_TYPE,
                Build.VERSION.SDK_INT,
                Build.MANUFACTURER,
                Build.MODEL);
        AlertDialog.Builder build = new AlertDialog.Builder(context)
                .setView(R.layout.dialog_about)
                .setPositiveButton("复制", (dialogInterface, i) -> {
                    ClipboardManager clipboardManager = (ClipboardManager)
                            context.getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboardManager != null) {
                        clipboardManager.setPrimaryClip(ClipData.newPlainText("version", versionInfo));
                        Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("GitHub", (d, i) -> gotoGitHubReleasePage());
        TextView content = build.show().findViewById(R.id.text_version);
        if (content != null) {
            content.setText(versionInfo);
        }
    }

    public void gotoGitHubReleasePage() {
        context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(RELEASES_URL)));
    }

    public void gotoRepoPage() {
        context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(REPO_URL)));
    }
}
