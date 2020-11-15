# LiveDataBus[![version](https://jitpack.io/v/fmtjava/LiveDataBus.svg)](https://jitpack.io/#fmtjava/LiveDataBus)
基于LiveData实现的一款不用发注册，不会内存泄露的轻量级消息总线框架，支持订阅普通事件消息和粘性事件

# How to
## Step 1. Add the JitPack repository to your build file

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}

```
##  Step 2. Add the dependency
```
implementation 'com.github.fmtjava:LiveDataBus:1.0.0'
```

# 订阅普通消息事件
```
  LiveDataBus.with<User>("login").observe(this, {
            tv_text.text = it.name
        })

```

# 发送普通消息事件
```
 LiveDataBus.with<User>("login").postData(user)
```

# 订阅粘性消息事件
```
 LiveDataBus.with<String>("name").observeStick(this, {
            tv_text.text = it
        })
```

# 发送性消息事件
```
  LiveDataBus.with<String>("name").postStickData("fmt")
```
