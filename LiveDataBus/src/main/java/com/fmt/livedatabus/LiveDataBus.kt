package com.fmt.livedatabus

import androidx.lifecycle.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

object LiveDataBus {

    private val eventMap = ConcurrentHashMap<String, StickLiveData<*>>()//一个事件对应一个LiveData
    private val lifecycleMap =
        ConcurrentHashMap<String, CopyOnWriteArrayList<LifecycleOwner>>()//一个事件对应多个页面
    private val eventTypeMap =
        ConcurrentHashMap<LifecycleOwner, CopyOnWriteArrayList<String>>()//一个页面对应多个事件

    fun <T> with(eventName: String): StickLiveData<T> {
        var liveData = eventMap[eventName]

        if (liveData == null) {
            liveData = StickLiveData<T>(eventName)
            eventMap[eventName] = liveData
        }
        return liveData as StickLiveData<T>
    }

    private fun getLifecycleOwners(eventName: String): CopyOnWriteArrayList<LifecycleOwner> {
        var lifecycleOwners = lifecycleMap[eventName]
        if (lifecycleOwners == null) {
            lifecycleOwners = CopyOnWriteArrayList<LifecycleOwner>()
            lifecycleMap[eventName] = lifecycleOwners
        }
        return lifecycleOwners
    }

    private fun addLifecycleOwner(eventName: String, lifecycleOwner: LifecycleOwner) {
        val lifecycleOwners = getLifecycleOwners(eventName)
        if (!lifecycleOwners.contains(lifecycleOwner)) {
            lifecycleOwners.add(lifecycleOwner)
        }
    }

    private fun getEventTypeList(lifecycleOwner: LifecycleOwner): CopyOnWriteArrayList<String> {
        var eventTypeList = eventTypeMap[lifecycleOwner]
        if (eventTypeList == null) {
            eventTypeList = CopyOnWriteArrayList<String>()
            eventTypeMap[lifecycleOwner] = eventTypeList
        }
        return eventTypeList
    }

    private fun addEventType(eventName: String, lifecycleOwner: LifecycleOwner) {
        val eventTypeList = getEventTypeList(lifecycleOwner)
        if (!eventTypeList.contains(eventName)) {
            eventTypeList.add(eventName)
        }
    }

    class StickLiveData<T>(private val eventName: String) : LiveData<T>() {

        internal var mStickData: T? = null
        internal var mVersion = 0

        fun setData(data: T) {
            setValue(data)
        }

        fun postData(data: T) {
            postValue(data)
        }

        fun setStickData(data: T) {
            mStickData = data
            setValue(data)
        }

        fun postStickData(data: T) {
            mStickData = data
            postValue(data)
        }

        override fun setValue(value: T) {
            mVersion++
            super.setValue(value)
        }

        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            observeStick(owner, observer, false)
        }

        fun observeStick(owner: LifecycleOwner, observer: Observer<in T>, stick: Boolean = true) {
            super.observe(owner, StickWarpObserver(this, observer, stick))
            addLifecycleOwner(eventName, owner)
            addEventType(eventName, owner)
            //监听页面的生命周期
            owner.lifecycle.addObserver(LifecycleEventObserver { source, event ->
                if (event == Lifecycle.Event.ON_DESTROY) {
                    val lifecycleOwners = getLifecycleOwners(eventName)
                    lifecycleOwners.remove(source)

                    val eventTypeList = getEventTypeList(owner)

                    eventTypeList.forEach { eventType ->
                        if (getLifecycleOwners(eventType).isEmpty()) {
                            eventMap.remove(eventType)
                        }
                    }
                }
            })
        }
    }

    //装饰者模式对原先的Observer进行包装
    class StickWarpObserver<T>(
        private val stickLiveData: StickLiveData<T>,
        private val observer: Observer<in T>,
        private val stick: Boolean
    ) : Observer<T> {

        //创建Observer时默认赋值为LiveData的Version,防止黏性事件
        private var mLastVersion = stickLiveData.mVersion

        override fun onChanged(t: T) {
            if (mLastVersion >= stickLiveData.mVersion) {
                if (stick && stickLiveData.mStickData != null) {
                    observer.onChanged(t)
                }
                return
            }
            mLastVersion = stickLiveData.mVersion
            observer.onChanged(t)
        }
    }
}