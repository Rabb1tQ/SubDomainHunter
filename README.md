# SubDomainBrute
Java子域名爆破工具
## 描述

### 爆破：

现经过优化后，预计31297个域名调整好参数后大概在16秒左右能够跑完，效果大概如下图：
![img.png](img/img.png)

### 已添加子域获取接口：

* AlienVault
* Anubis
* Rapiddns
* SiteDossier

## 使用方法：

将PortService放到Jar包所在目录下，后执行`java -jar JarName`，参数项：

```
Usage: <main class> [options]
  Options:
  * --domain, -d
      目标域名
    help, --help
      查看帮助信息
    --nameserver, -n
      超时时间
    --threads, -t
      线程数
      Default: 100
```

## 子域获取接口添加：

1. `com.rabbitq.models.impl`包下添加你的子域获取接口类，
2. 继承`SubDomainInterface`接口并添加`@SubDomainInterfaceImplementation`注解
3. 重写`getSubDomain`方法，接收参数为`jcommander`的实体类，返回值为`Set<String>`，通过`targetOptionsEntity.getDomain()`可以获取到目标域名
4. 自定义异常捕捉，失败在终端中打印失败原因，成功在终端打印获取条数（非必须）
5. 直接运行即可，无需其他操作