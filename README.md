
### AR-Call-Android SDK for Android
### 简介
AR-Call-Android 呼叫，支持视频、语音、优先视频等多种呼叫模式，基于ARCallEngine SDK，适用于网络电话、活动、教育等多种呼叫场景。。


### app体验

##### 扫码下载
![image](https://www.pgyer.com/app/qrcode/3blO)
##### [点击下载](https://www.pgyer.com/3blO)


### SDK集成
# > 方式一（推荐）[ ![Download](https://api.bintray.com/packages/dyncanyrtc/ar_dev/call/images/download.svg) ](https://bintray.com/dyncanyrtc/ar_dev/call/_latestVersion)


添加Jcenter仓库 Gradle依赖：

```
dependencies {
    compile 'org.ar:arcall_kit:3.0.4'
}
```

或者 Maven
```
<dependency>
  <groupId>org.ar</groupId>
  <artifactId>arcall_kit</artifactId>
  <version>3.0.3</version>
  <type>pom</type>
</dependency>
```

### 安装

##### 编译环境

AndroidStudio

##### 运行环境

Android API 15+
真机运行

### 如何使用

##### 注册开发者信息

>如果您还未注册anyRTC开发者账号，请登录[anyRTC官网](http://www.anyrtc.io)注册及获取更多的帮助。

##### 替换开发者账号
在[anyRTC官网](http://www.anyrtc.io)获取了开发者账号，AppID等信息后，替换DEMO中
**DeveloperInfo**类中的开发者信息即可

### 操作步骤

1、两台手机分别登录两个不同的账号；

2、一台手机点击发起通话进入呼叫页面，输入对方手机号；

3、选择呼叫模式开始呼叫，呼叫接通开始会话。

### 完整文档
SDK集成，API介绍，详见官方完整文档：[点击查看](https://docs.anyrtc.io/v1/P2P/android.html)

### iOS版 P2P点对点呼叫

[AR-P2P-iOS](https://github.com/AnyRTC/anyRTC-P2P-iOS)


### 支持的系统平台
**Android** 4.0及以上

### 支持的CPU架构
**Android** arm64-v8a  armeabi armeabi-v7a


### 注意事项
1. P2P SDK所有回调均在子线程中，所以在回调中操作UI等，应切换主线程。
2. 注意安卓6.0+动态权限处理。
3. 常见错误代码请参考[错误码查询](https://www.anyrtc.io/resoure)

### 技术支持
- anyRTC官方网址：[https://www.anyrtc.io](https://www.anyrtc.io/resoure)
- QQ技术咨询群：554714720
- 联系电话:021-65650071-816
- Email:hi@dync.cc

### 关于直播

本公司有一整套完整直播解决方案。本公司开发者平台www.anyrtc.io。除了基于RTMP协议的直播系统外，我公司还有基于WebRTC的时时交互直播系统、P2P呼叫系统、会议系统等。快捷集成SDK，便可让你的应用拥有时时通话功能。欢迎您的来电~

### License

- P2PEngine is available under the MIT license. See the LICENSE file for more info.





   



 
