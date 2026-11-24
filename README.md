# MiPushFramework (ColorOS 17)

[![Test CI](https://github.com/libenh/MiPushFramework/actions/workflows/test_ci.yml/badge.svg)](https://github.com/libenh/MiPushFramework/actions/workflows/test_ci.yml)

**仅针对 ColorOS 17 / O系（OPPO / 一加 / realme）** 的小米系统级推送服务。

## 下载

1. [Actions](https://github.com/libenh/MiPushFramework/actions) → 最新成功 **Test CI**
2. Artifacts 下载 **`MiPush-C17`**
3. 或从 Releases 下载 `MiPush-C17-*.apk`

产物固定 **versionCode = 105**（startService），不再提供 normal/bindService 变体。

## 安装

1. 安装 APK（建议系统应用）
2. 向导授权；电池里允许 **自启动 / 后台**
3. 注册不上：设置 → 对应 App → **强制注册**

## 主要改动（相对原版）

- 默认 versionCode **105**，适配 C17 bind 异常
- 强制注册：startService + 广播；多路径清理 `mipush*.xml`
- CI **只编译 c17Release**

## 本地构建

```bash
./gradlew :push:assembleC17Release
```

## License

GPL-3.0
