FROM centos:7
RUN curl -SL https://tdmq-1255429582.cos.ap-beijing.myqcloud.com/jdk-17_linux-x64_bin.rpm -o java-17-openjdk.rpm
RUN yum update -y && yum install -y java-17-openjdk.rpm

# 设置时区。这对于日志、调用链等功能能否在 TSF 控制台被检索到非常重要。
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo "Asia/Shanghai" > /etc/timezone
ENV workdir /app/

# 下面的 jar 包可替换为您的 Spring Cloud 应用 jar包，注意这个 jar 包要和您的 dockerfile 位于同一级目录
ENV jar websocket-gateway-1.0-SNAPSHOT-jar-with-dependencies.jar
COPY target/${jar} ${workdir}
WORKDIR ${workdir}

# JAVA_OPTS 环境变量的值为部署组的 JVM 启动参数，在运行时 bash 替换。使用 exec 以使 Java 程序可以接收 SIGTERM 信号。
CMD ["sh", "-ec", "exec java ${JAVA_OPTS} -jar ${jar}"]