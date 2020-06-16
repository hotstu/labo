[![author](https://img.shields.io/badge/author-hglf-blue.svg)](https://github.com/hotstu)
[![Download](https://api.bintray.com/packages/hglf/maven/labo_hglf/images/download.svg) ](https://bintray.com/hglf/maven/labo_hglf/_latestVersion)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# labo-tool
  基础工具类

# labo-rxfetch
   http请求核心库，对RxJava和OkHttp的封装，返回Observable，支持GET、POST，文件上传下载，支持断点续传、支持json自动反序列化到实例对象
   
   ```java
           OkHttpClient build = new OkHttpClient.Builder().build();
           fetch = new RxFetch(build, new GsonTypeAdapter(), new JsoupTypeAdapter());
   ```

# labo-jsBridge
  JsBridge,实现了java到js、js到java的相互调取，对调用进行了工程化的封装，Webview的安全处理，
  支持同步调用、异步调用，支持跨window调用。

```
  @: optimization
  +: new feature
  #: bug fix
  -: removed feature
  *: Note
```

## changelog
rxfetch:
* 1.0.2
  * + Moshi json parser in the box

* 1.2.0
  * +labo_sec 
  * +labo_noob
* 1.1.0
    1. #fix groupid typo
* 1.0.0

    1. migrate to androidx
    2. rxfetch rename `LaboSchedulers` to `Transformers`


* 0.0.1
   init

## more

|Github|简书| 掘金|JCenter | dockerHub|
| -------------| ------------- |------------- |------------- |------------- |
| [Github](https://github.com/hotstu)| [简书](https://www.jianshu.com/u/ca2207af2001) | [掘金](https://juejin.im/user/5bee320651882516be2ebbfe) |[JCenter ](https://bintray.com/hglf/maven)      | [dockerHub](https://hub.docker.com/u/hglf)|

