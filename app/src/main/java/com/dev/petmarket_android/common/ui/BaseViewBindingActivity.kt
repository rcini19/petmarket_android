package com.dev.petmarket_android.common.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * Base activity class providing ViewBinding support and coroutine scope management.
 * All activities should extend this class for consistent lifecycle handling.
 *
 * Usage:
 * ```
 * class MyActivity : BaseViewBindingActivity<ActivityMyBinding>() {
 *     override fun createBinding() = ActivityMyBinding.inflate(layoutInflater)
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         setContentView(binding.root)
 *         // Use binding.viewId to access views
 *     }
 * }
 * ```
 */
abstract class BaseViewBindingActivity<VB : ViewBinding> :
    AppCompatActivity(),
    CoroutineScope {

    protected abstract fun createBinding(): VB

    protected lateinit var binding: VB

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = createBinding()
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel() // Cancel all coroutines when activity is destroyed
    }
}
