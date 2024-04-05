# axin-rpc-framework

**模拟简易版RPC框架**

**技术选型**: Java+Netty+kryo+Zookeeper

### 设计思路
1. **注册中心**: 选用 ZooKeeper(当然也可以 Nacos、Consul等)。 注册中心主要用来保存相关的信息比如说远程方法的地址(套接字)
2. **网络传输**: 选用基于 NIO 的 Netty 框架。 调用 RPC 需要发网络请求, 请求中至少要包含调用的类名、方法名以及相关参数
3. **序列化**:  选用 Kryo(当然也可以是 Protobuf、Hession2)。涉及到网络传输就一定涉及到序列化, 不推荐直接使用 JDK 自带的序列化
4. **动态代理**: RPC 的主要目的就是让我们调用远程方法像调用本地方法一样简单, 使用动态代理屏蔽远程接口调用的细节比如网络传输
5. **负载均衡**: 为了避免单个服务器响应同一请求, 容易造成服务器宕机、崩溃等问题, 因此需要负载均衡
6. ......

### 运行项目

**1.导入项目**

克隆项目到自己的本地：`git clone git@github.com:Axin668/axin-rpc-framework.git`

然后使用 IDEA 打开，等待项目初始化完成。

**2.初始化git hooks**

执行以下命令:

```shell
➜  axin-rpc-framework git:(master) ✗ chmod +x ./init.sh
➜  axin-rpc-framework git:(master) ✗ ./init.sh 
```

`init.sh` 这个脚本的主要作用是将 git commit 钩子拷贝到项目下的 `.git/hooks/` 目录, 这样你每次commit的时候就会执行了。

```shell
cp config/git-hooks/pre-commit .git/hooks/
chmod +x .git/hooks/pre-commit
```

`pre-commit` 的内容如下, 主要作用是在提交代码前运行 `Check Style`检查代码格式问题。

```shell
#!/bin/sh
#set -x

echo "begin to execute hook"
mvn checkstyle:check

RESULT=$?

exit $RESULT
```

**3.CheckStyle 插件下载和配置**

TODO

**4.下载运行 ZooKeeper**

这里使用Docker下载安装
下载:
```shell
docker pull zookeeper:3.5.8
```
运行:
```shell
docker run -d --name zookeeper -p 2181:2181 zookeeper:3.5.8
```