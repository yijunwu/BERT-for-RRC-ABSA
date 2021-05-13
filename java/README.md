# 1. 概述
WilsonTracker 是一个数据因果关系追踪框架。

# 2. 使用方法

## 2.1 依赖wilson-tracker

```xml
<groupId>com.alibaba.intl</groupId>
<artifactId>wilson-tracker</artifactId>
<version>1.5.2-DFT-SNAPSHOT</version>
```

## 2.2 注解标注需要追踪的操作入口，数据出入口，以及数据条目

1\.  使用 @TrackedEntrance 标明需要追踪的操作（方法）

```java
@Override
@TrackedEntrance(name = "放款", autoIncludeUnits = true, flushOnExit = true)
public Long createLoad(NyseCreateLoanRequest nyseCreateLoanRequest) {
    return nysePPLoanDelegate.createLoad(nyseCreateLoanRequest);
}
```

2\. 使用 @TrackedSource 标明操作入口之外的数据入口（如从数据库读需要跟踪的数据等）

```java
@TrackedSource(name = "NyseFundLoanRepository.findByOutEntityIdAndRequestId", dataClass = "com.alibaba.intl.nyse.dal.nyse.entity.NyseFundLoan")
NyseFundLoan findByOutEntityIdAndRequestId(Long outEntityId, String request);
```

3\. 使用 @TrackedDestination 标明数据出口（如将被跟踪的数据写入数据库等）

```java
@TrackedDestination(name = "NyseFundLoanAdaptor.saveNyseFund", autoIncludeUnits = false, dataClass = "com.alibaba.intl.nyse.dal.nyse.entity.NyseFundLoan")
public NyseFundLoan saveNyseFundLoan(NyseFundLoan fundLoan) {
    Long amount = fundLoan.getAmount();
    String amountCur = fundLoan.getAmountCur();
    Long outEntityId = fundLoan.getOutEntityId();
    System.out.println(("" + amount + amountCur + outEntityId));
    return nyseFundLoanRepository.save(fundLoan);
}

```

4\. 使用 @TrackedUnit 操作标明需要跟踪的数据条目

```java
@TrackedDestination(name = "NyseFundLoanAdaptor.saveNyseFund", autoIncludeUnits = false, dataClass = "com.alibaba.intl.nyse.dal.nyse.entity.NyseFundLoan")
@TrackedUnit(name = "amount",
    propagationType = EXPLICIT,
    group = "amountMoney",
    expression = "${_.amount}",
    dataType = NUMBER)
public NyseFundLoan saveNyseFundLoan(NyseFundLoan fundLoan) {
    Long amount = fundLoan.getAmount();
    String amountCur = fundLoan.getAmountCur();
    Long outEntityId = fundLoan.getOutEntityId();
    System.out.println(("" + amount + amountCur + outEntityId));
    return nyseFundLoanRepository.save(fundLoan);
}

```

## 2.3 引入wilson-tracker-maven-plugin

对于需要进行数据追踪的模块，在pom.xml中添加对wilson-tracker-maven-plugin的引用，有父子module结构的，也可以加在父module的pom.xml中。
示例代码如下

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.alibaba.intl</groupId>
            <artifactId>wilson-tracker-maven-plugin</artifactId>
            <version>1.5.2-DFT-SNAPSHOT</version>
            <executions>
                <execution>
                    <goals>
                        <goal>soot</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <enabled>true</enabled>
                <debug>true</debug>
                <fork>true</fork>
                <prependClasspath>true</prependClasspath>
                <outputFormat>CLASS</outputFormat>
                <!--modulePatterns>nyse/dal,nyse/biz,nyse/biz.compatible.api,nyse/entry,nyse/export,nyse/api</modulePatterns-->
                <!-- put your configurations here -->
            </configuration>
        </plugin>
    </plugins>
</build>

```

