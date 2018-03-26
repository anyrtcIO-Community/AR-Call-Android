### anyRTC-P2P-Android SDK for Android
### 简介
anyRTC-P2P-Android点对点呼叫，支持视频、语音、优先视频等多种呼叫模式，基于RTCP2PEngine SDK，适用于网络电话、活动、教育等多种呼叫场景。。

### 项目展示
![image](https://github.com/AnyRTC/anyRTC-P2P-Android/blob/master/images/p2p1.png)
![image](https://github.com/AnyRTC/anyRTC-P2P-Android/blob/master/images/p2p2.jpg)
![image](https://github.com/AnyRTC/anyRTC-P2P-Android/blob/master/images/p2p3.jpg)
![image](https://github.com/AnyRTC/anyRTC-P2P-Android/blob/master/images/p2p4.jpg)
![image](https://github.com/AnyRTC/anyRTC-P2P-Android/blob/master/images/p2p5.jpg)
![image](https://github.com/AnyRTC/anyRTC-P2P-Android/blob/master/images/p2p6.jpg)


### app体验

##### 扫码下载
![image](https://github.com/AnyRTC/anyRTC-P2P-Android/blob/master/images/demo_qrcode.png)
##### [点击下载](https://www.pgyer.com/anyrtc_p2p_android)


### SDK集成
# > 方式一（推荐）

添加Jcenter仓库 Gradle依赖：

```
dependencies {
   compile 'org.anyrtc:rtp2pcall_kit:2.3.1'
}
```

或者 Maven
```
<dependency>
  <groupId>org.anyrtc</groupId>
  <artifactId>rtp2pcall_kit</artifactId>
  <version>2.3.1</version>
  <type>pom</type>
</dependency>
```

>方式二

 [下载aar SDK](https://www.anyrtc.io/resoure)

>1. 将下载好的rtp2pcall_kit-release.aar文件放入项目的libs目录中
>2. 在Model下的build.gradle文件添加如下代码依赖P2P SDK

```
android
{

 repositories {
        flatDir {dirs 'libs'}
    }
    
 }
    
```
```
dependencies {
    compile(name: 'rtp2pcall_kit-release.aar', ext: 'aar')
}
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
**P2PApplication**类中的开发者信息即可

### 操作步骤

1、两台手机分别登录两个不同的账号；

2、一台手机点击发起通话进入呼叫页面，输入对方手机号；

3、选择呼叫模式开始呼叫，呼叫接通开始会话。

### 完整文档
SDK集成，API介绍，详见官方完整文档：[点击查看](https://www.anyrtc.io/resoure)

### Ios版anyRTC-P2P点对点呼叫

[anyRTC-P2P-Ios](https://github.com/AnyRTC/anyRTC-P2P-iOS)


### 支持的系统平台
**Android** 4.0及以上

### 支持的CPU架构
**Android** arm64-v8a  armeabi armeabi-v7a


### 注意事项
1. P2P SDK所有回调均在子线程中，所以在回调中操作UI等，应切换主线程。
2. 注意安卓6.0+动态权限处理。
3. 常见错误代码请参考[错误码查询](https://www.anyrtc.io/resoure)

### 商业授权
程序发布需商用授权，业务咨询请联系 QQ:984630262 

QQ交流群:580477436

联系电话:021-65650071

Email:zhangjianqiang@dync.cc

### 技术支持 
- anyRTC官方网址：[https://www.anyrtc.io](https://www.anyrtc.io/resoure)
- QQ技术咨询群：580477436
- 

### 关于直播

本公司有一整套完整直播解决方案。本公司开发者平台www.anyrtc.io。除了基于RTMP协议的直播系统外，我公司还有基于WebRTC的时时交互直播系统、P2P呼叫系统、会议系统等。快捷集成SDK，便可让你的应用拥有时时通话功能。欢迎您的来电~

### License

- P2PEngine is available under the MIT license. See the LICENSE file for more info.





   



 
