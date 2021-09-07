# 重要提醒
anyRTC 对该版本已经不再维护，如需音视频呼叫，请前往:https://github.com/anyRTC-UseCase/ARCall

**功能如下：**
- 一对一音视频呼叫
- 一对多音视频呼叫
- 视频通话转音频通话
- 静音开关/视频开关
- AI降噪，极致降噪，不留噪声
- 大小屏切换
- 悬浮窗功能

新版本一行代码，30分钟即可使应用有音视频能力。

更多示列请前往**公司网址： [www.anyrtc.io](https://www.anyrtc.io)**


### AR-Call-Android SDK for Android
### 简介
AR-Call-Android 呼叫，支持视频、语音、优先视频等多种呼叫模式，基于ARCallEngine SDK，适用于网络电话、活动、教育等多种呼叫场景。。


### app体验

##### [点击下载](http://download.anyrtc.io/gyjh)


### SDK集成
# > 方式一（推荐）[ ![Download](https://api.bintray.com/packages/dyncanyrtc/ar_dev/call/images/download.svg) ](https://bintray.com/dyncanyrtc/ar_dev/call/_latestVersion)


添加Jcenter仓库 Gradle依赖：

```
dependencies {
    compile 'org.ar:arcall_kit:3.1.8'
}
```

或者 Maven
```
<dependency>
  <groupId>org.ar</groupId>
  <artifactId>arcall_kit</artifactId>
  <version>3.1.8</version>
  <type>pom</type>
</dependency>
```

###更新日志

V3.1.0

增加onRTCJoinRoomOk回调
去除onRTCMakeCall回调中第一个MeetId参数
turnOn方法增加userData参数
ARUserOption类中去除userData参数

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
在[anyRTC官网](http://www.anyrtc.io)获取了应用 ID，应用 Token 后，替换 DEMO 中
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





   



 
