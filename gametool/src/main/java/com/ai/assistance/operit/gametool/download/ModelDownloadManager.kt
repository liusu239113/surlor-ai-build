package com.ai.assistance.operit.gametool.download

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.ai.assistance.operit.gametool.models.GameModelConfig
import com.ai.assistance.operit.gametool.models.ModelInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.security.MessageDigest
import kotlin.coroutines.coroutineContext

/**
 * 本地模型下载管理器
 *
 * 首次启动时从国内镜像（ModelScope）下载 GGUF 模型，支持断点续传和进度回调。
 */
object ModelDownloadManager {

    private const val TAG = "ModelDownloadManager"
    private const val CONNECT_TIMEOUT_MS = 30_000L
    private const val READ_TIMEOUT_MS = 60_000L
    private const val BUFFER_SIZE = 8192

    data class DownloadProgress(
        val downloadedBytes: Long,
        val totalBytes: Long,
        val status: Status
    ) {
        val percent: Int
            get() = if (totalBytes > 0) ((downloadedBytes * 100) / totalBytes).toInt() else 0

        enum class Status {
            IDLE, PENDING, DOWNLOADING, PAUSED, COMPLETED, FAILED
        }
    }

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_MS, java.util.concurrent.TimeUnit.MILLISECONDS)
            .readTimeout(READ_TIMEOUT_MS, java.util.concurrent.TimeUnit.MILLISECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
    }

    private const val PREFS_NAME = "model_download_prefs"
    private const val KEY_MODEL_SELECTION_COMPLETED = "model_selection_completed"

    /**
     * 检查模型选择流程是否已完成（下载成功或用户跳过）
     */
    fun isModelSelectionCompleted(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_MODEL_SELECTION_COMPLETED, false)
    }

    /**
     * 标记模型选择流程已完成
     */
    fun setModelSelectionCompleted(context: Context, completed: Boolean = true) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_MODEL_SELECTION_COMPLETED, completed)
            .apply()
    }

    /**
     * 获取模型存放目录
     */
    fun getModelDir(context: Context): File {
        return File(context.getExternalFilesDir(null) ?: context.filesDir, "models").apply {
            mkdirs()
        }
    }

    /**
     * 获取模型文件
     */
    fun getModelFile(context: Context, model: ModelInfo): File {
        return File(getModelDir(context), "${model.id}.gguf")
    }

    /**
     * 检查模型是否已下载完成（文件存在且大小匹配）
     */
    fun isModelDownloaded(context: Context, model: ModelInfo = GameModelConfig.getRecommendedModel()): Boolean {
        val file = getModelFile(context, model)
        return file.exists() && file.length() >= model.fileSizeMb * 1024 * 1024 * 0.95
    }

    /**
     * 获取推荐模型，如果不存在则返回 null
     */
    fun getDownloadedModel(context: Context): ModelInfo? {
        return GameModelConfig.ALL_MODELS.firstOrNull { isModelDownloaded(context, it) }
            ?: GameModelConfig.getRecommendedModel().takeIf { isModelDownloaded(context, it) }
    }

    /**
     * 下载推荐模型。默认优先使用国内镜像。
     *
     * @param useMirror 是否使用 ModelScope 国内镜像，默认 true
     * @param onProgress 进度回调
     * @return 下载完成后的模型文件
     */
    suspend fun downloadRecommendedModel(
        context: Context,
        useMirror: Boolean = true,
        onProgress: (DownloadProgress) -> Unit = {}
    ): Result<File> {
        return downloadModel(context, GameModelConfig.getRecommendedModel(), useMirror, onProgress)
    }

    /**
     * 下载指定模型
     */
    suspend fun downloadModel(
        context: Context,
        model: ModelInfo,
        useMirror: Boolean = true,
        onProgress: (DownloadProgress) -> Unit = {}
    ): Result<File> {
        return withContext(Dispatchers.IO) {
            try {
                onProgress(DownloadProgress(0, model.fileSizeMb * 1024 * 1024, DownloadProgress.Status.PENDING))

                val file = getModelFile(context, model)
                val tempFile = File(file.parentFile, "${file.name}.download")

                val url = if (useMirror) model.mirrorUrl else model.url
                Log.i(TAG, "开始下载模型: ${model.displayName}, URL=$url")

                val existingLength = if (tempFile.exists()) tempFile.length() else 0L

                val requestBuilder = Request.Builder()
                    .url(url)
                    .header("User-Agent", "SurlorAI-GameKit/1.0")

                if (existingLength > 0) {
                    requestBuilder.header("Range", "bytes=$existingLength-")
                    Log.i(TAG, "断点续传，从 $existingLength 字节继续")
                }

                val request = requestBuilder.build()
                val response = client.newCall(request).execute()

                if (!response.isSuccessful && response.code != 206) {
                    throw IOException("下载失败: HTTP ${response.code}")
                }

                val contentLength = response.body?.contentLength() ?: -1
                val totalBytes = if (response.code == 206 && existingLength > 0) {
                    existingLength + contentLength
                } else {
                    if (tempFile.exists()) tempFile.delete()
                    contentLength
                }

                val effectiveTotal = if (totalBytes > 0) totalBytes else model.fileSizeMb * 1024 * 1024

                response.body?.byteStream()?.use { input ->
                    RandomAccessFile(tempFile, "rwd").use { raf ->
                        if (response.code == 206) {
                            raf.seek(existingLength)
                        } else {
                            raf.setLength(0)
                        }

                        val buffer = ByteArray(BUFFER_SIZE)
                        var downloaded = if (response.code == 206) existingLength else 0L
                        var read: Int

                        onProgress(
                            DownloadProgress(
                                downloaded,
                                effectiveTotal,
                                DownloadProgress.Status.DOWNLOADING
                            )
                        )

                        while (input.read(buffer).also { read = it } != -1) {
                            if (!coroutineContext.isActive) {
                                onProgress(
                                    DownloadProgress(
                                        downloaded,
                                        effectiveTotal,
                                        DownloadProgress.Status.PAUSED
                                    )
                                )
                                return@withContext Result.failure(IOException("下载已取消"))
                            }

                            raf.write(buffer, 0, read)
                            downloaded += read

                            onProgress(
                                DownloadProgress(
                                    downloaded,
                                    effectiveTotal,
                                    DownloadProgress.Status.DOWNLOADING
                                )
                            )
                        }
                    }
                } ?: throw IOException("响应体为空")

                if (!tempFile.renameTo(file)) {
                    throw IOException("无法重命名临时文件")
                }

                onProgress(
                    DownloadProgress(
                        file.length(),
                        effectiveTotal,
                        DownloadProgress.Status.COMPLETED
                    )
                )

                Log.i(TAG, "模型下载完成: ${file.absolutePath}, 大小=${file.length()}")
                Result.success(file)
            } catch (e: Exception) {
                Log.e(TAG, "模型下载失败", e)
                onProgress(
                    DownloadProgress(
                        0,
                        model.fileSizeMb * 1024 * 1024,
                        DownloadProgress.Status.FAILED
                    )
                )
                Result.failure(e)
            }
        }
    }

    /**
     * 计算文件 SHA256（用于校验，大文件较慢）
     */
    suspend fun sha256(file: File): String {
        return withContext(Dispatchers.IO) {
            val digest = MessageDigest.getInstance("SHA-256")
            file.inputStream().use { input ->
                val buffer = ByteArray(BUFFER_SIZE)
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    digest.update(buffer, 0, read)
                }
            }
            digest.digest().joinToString("") { "%02x".format(it) }
        }
    }

    /**
     * 当前是否使用 WiFi 网络
     */
    fun isOnWifi(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    /**
     * 当前是否有可用网络
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
