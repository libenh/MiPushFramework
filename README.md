# MiPushFramework

[![Test CI](https://github.com/libenh/MiPushFramework/actions/workflows/test_ci.yml/badge.svg)](https://github.com/libenh/MiPushFramework/actions/workflows/test_ci.yml)
[![License GPL-3.0](https://img.shields.io/badge/license-GPLv3.0-blue.svg)](LICENSE)

在非 MIUI 系统上体验小米系统级推送。

> 本仓库分支重点兼容 **ColorOS / OxygenOS / OnePlus（O系）**，含 ColorOS 17。

## ColorOS 17 / O系（必读）

| 现象 | 建议 |
|------|------|
| 大部分应用注册不上 / 注册后没反应 | 安装 **vc105** 版本（versionCode=105，startService） |
| 消息无法回传 | 同上；并关闭电池「高级优化」、给 xmsf 自启动/后台 |
| 仍失败 | 设置里对目标 App 「强制注册」；有 Root 会清理 mipush*.xml |

CI 每次构建会同时产出：

- `*-normal.apk` — 默认 / MIUI 更友好
- `*-vc105-O系.apk` — **O系推荐**

下载：[Actions](https://github.com/libenh/MiPushFramework/actions) 成功记录的 Artifacts，或 Releases。

## 功能

- 基本推送能力（类似 MIUI 上 `com.xiaomi.xmsf`）
- 通知改写与配置
- 注册/推送事件观测
- **强制注册**：startService + 广播双通道，多路径清理 mipush 缓存（适合 O系）

### 注意

* 给 `com.xiaomi.xmsf` 最大权限，勿用黑域/绿色守护等限制
* 建议配合伪装模块，便于应用自动向 xmsf 注册；不伪装时请用「强制注册」
* 推荐安装为**系统应用**（包可见性），O系上尤其重要

## 开始使用

1. 从 [Actions](https://github.com/libenh/MiPushFramework/actions) / Releases 下载 APK  
   - **ColorOS / 一加 / realme → 选 vc105**
2. 安装并按向导设置
3. 可选：高级配置里开「推送服务保活」

### vc105 与 normal

| | normal | vc105 |
|---|--------|-------|
| versionCode | 1003003000 | 105 |
| 应用→xmsf | bindService | **startService** |
| 适用 | MIUI / 一般 ROM | **ColorOS 等 O系** |

## 反馈

Issues 请附：ROM、Android 版本、用的 APK 变体（normal/vc105）、设置-获取日志。

## License

GPL-3.0
