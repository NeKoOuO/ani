# CI 说明

## 各平台 CI 运行情况

由于 GitHub Runner 性能有限, 各平台不一定会全流程运行.

| 平台             | 编译 Kotlin | 运行测试             | 构建 Anitorrent <br/>for Desktop | 构建 Anitorrent <br/>for Android | 构建 Android arm64-v8a | 构建其他 Android 架构  | 编译 Kotlin iOS | 构建 iOS Framework |
|----------------|-----------|------------------|--------------------------------|--------------------------------|----------------------|------------------|---------------|------------------|
| Ubuntu x86_64  | ✅         | ❌<sup>\[1]</sup> | ✅                              | ✅                              | ✅                    | ✅                | ❌             | ❌                |
| Windows x86_64 | ✅         | ✅                | ✅                              | ✅                              | ✅                    | ✅                | ❌             | ❌                |
| macOS x86_64   | ✅         | ✅                | ✅                              | ✅                              | ✅                    | ✅                | ✅             | ❌(TODO)          |
| macOS aarch64  | ✅         | ✅                | ✅                              | ✅                              | ✅                    | ❌<sup>\[2]</sup> | ✅             | ❌<sup>\[2]</sup> |

> [!NOTE]
>
> \[1]: Ani 项目目前不支持 Ubuntu 平台
>
> \[2]: 构建其他架构需要下载更多依赖, 导致超出 GitHub Runner 限制, 无法构建
>
> 只有 macOS 平台支持 iOS 构建
