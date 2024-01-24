# SubDomainBrute
Java子域名爆破工具

Usage：
将PortService放到Jar包所在目录下，后执行java -jar JarName，参数项：

```
Usage: <main class> [options]
  Options:
    help, --help
      查看帮助信息
  * host, -h
      目标IP
    numOfThreads, -t
      线程数
      Default: 100
  * ports, -p
      目标端口，示例：1,2-30,80
    timeOut, -time
      超时时间
      Default: 200
```