package com.niran.psychoquiz.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

fun <T> LiveData<T>.safeObserveWithInit(
    owner: LifecycleOwner, observe: (any: T) -> Unit, init: (any: T) -> Unit
) {
    var isInit = false
    observe(owner) {
        it?.let {
            if (!isInit) {
                init(it)
                isInit = true
            }
            observe(it)
        }
    }
}