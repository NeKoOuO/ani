package me.him188.ani.app.data.source.media.cache

import io.ktor.util.collections.ConcurrentMap
import kotlinx.atomicfu.atomic
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import me.him188.ani.app.data.source.media.cache.engine.sum
import me.him188.ani.app.platform.notification.Notif
import me.him188.ani.app.platform.notification.NotifManager
import me.him188.ani.app.platform.notification.NotifPriority
import me.him188.ani.app.tools.toPercentageOrZero
import me.him188.ani.datasources.api.topic.sum
import me.him188.ani.utils.coroutines.sampleWithInitial
import me.him188.ani.utils.platform.currentTimeMillis

class MediaCacheNotificationTask(
    private val cacheManager: MediaCacheManager,
    private val notificationManager: NotifManager,
) {
    // does not return
    suspend fun run() {
        val startTime = currentTimeMillis()
        cacheManager.enabledStorages.collectLatest { list ->
            supervisorScope {
                val channel = notificationManager.downloadChannel

                val stats = list.map { it.stats }.sum()

                val summaryNotif = channel.newNotif().apply {
                    silent = true
                    ongoing = true
                    setGroup("media-cache")
                    setAsGroupSummary(true)
                    contentTitle = "正在缓存"
                    priority = NotifPriority.MIN
                }

                val visibleCount = atomic(0)
                for (storage in list) {
                    launch {
                        if (currentTimeMillis() - startTime < 5000) {
                            delay(5000)
                        }

                        storage.listFlow.debounce(1000).collectLatest { caches ->
                            @Suppress("NAME_SHADOWING")
                            var caches = caches.toPersistentList()
                            val cacheIds = caches.mapTo(mutableSetOf()) { it.cacheId }
                            val visibleNotifications = ConcurrentMap<String, Notif>()
                            try {
                                while (currentCoroutineContext().isActive) {
                                    for (cache in caches) {
                                        val progress = cache.fileStats.map { it.downloadProgress }.first()
                                        if (progress.isFinished) {
                                            caches = caches.remove(cache)
                                            visibleNotifications.remove(cache.cacheId)?.let {
                                                visibleCount.decrementAndGet()
                                                it.release()
                                            }
                                        } else {
                                            visibleNotifications.getOrPut(cache.cacheId) {
                                                visibleCount.incrementAndGet()
                                                channel.newNotif().apply {
                                                    setGroup("media-cache")
                                                    contentTitle = cache.previewText
                                                    silent = true
                                                    ongoing = true
                                                    priority = NotifPriority.MIN
                                                }
                                            }.run {
                                                val download = cache.downloadSpeed.first()
                                                contentText = "$download/s"
                                                setProgress(100, progress.toPercentageOrZero().toInt())
                                                show()
                                            }
                                        }
                                    }

                                    // 清除已经完成的通知
                                    // toList is required here to avoid ConcurrentModificationException
                                    visibleNotifications.keys.toList().forEach { cacheId ->
                                        if (!cacheIds.contains(cacheId)) {
                                            visibleNotifications.remove(cacheId)?.release()
                                        }
                                    }

                                    delay(3000)
                                }
                            } catch (e: Throwable) {
                                visibleNotifications.values.forEach { it.release() }
                                throw e
                            }
                            visibleNotifications.values.forEach { it.release() }
                        }
                    }
                }

                launch {
                    if (currentTimeMillis() - startTime < 5000) {
                        delay(5000)
                    }

                    combine(stats.map { it.downloadSpeed }.sampleWithInitial(3000)) { downloadRate ->
                        if (visibleCount.value == 0) {
                            summaryNotif.cancel()
                        } else {
                            summaryNotif.run {
                                contentText = "下载 ${downloadRate.sum()}/s"
                                show()
                            }
                        }
                    }.collect()
                }
            }
        }
    }

    private val MediaCache.previewText: String
        get() {
            metadata.subjectNames.firstOrNull()?.let { name ->
                return "$name ${metadata.episodeSort}"
            }
            return origin.originalTitle
        }

}