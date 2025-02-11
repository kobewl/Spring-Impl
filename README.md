# Spring-Impl Project

这是一个简化版的 Spring Framework 实现项目，用于学习和理解 Spring 框架的核心原理。

## 核心功能实现

### 1. IoC 容器实现

- 实现了简化版的 Spring IoC 容器 (`ImplApplicationContext`)
- 支持基于注解的组件扫描
- 提供依赖注入的基础功能
- 实现三级缓存解决循环依赖问题：
  - 一级缓存：完整的单例 Bean
  - 二级缓存：提前暴露的单例对象（未完全初始化）
  - 三级缓存：单例工厂对象

### 2. Bean 生命周期管理

- 实现 `BeanPostProcessor` 接口，支持 Bean 的前置和后置处理
- 提供 `InitializingBean` 接口，支持 Bean 初始化时的自定义逻辑
- Bean 的完整生命周期：
  1. 实例化 Bean
  2. 属性注入
  3. 前置处理
  4. 初始化
  5. 后置处理

### 3. 依赖注入实现

- `@Autowired` 注解支持：
  - 字段注入
  - 构造方法注入
  - 普通方法注入
- 支持 `required` 属性配置是否必须注入
- 通过反射机制实现依赖注入

### 4. AOP 支持

- 实现了完整的 AOP 功能，支持多种通知类型：

  - `@Before`: 前置通知
  - `@After`: 后置通知
  - `@Around`: 环绕通知
  - `@AfterThrowing`: 异常通知

- 提供 `ProceedingJoinPoint` 支持，用于环绕通知：

  - 控制目标方法执行
  - 访问方法信息
  - 修改方法参数
  - 处理返回值

- 切点表达式解析器：
  - 支持方法级别的匹配
  - 支持类级别的匹配
  - 支持包级别的匹配

## 项目结构

```
src/main/java/
├── com.spring/           # 核心实现包
│   ├── Component.java        # @Component 注解定义
│   ├── ComponentScan.java    # @ComponentScan 注解定义
│   ├── Autowired.java        # @Autowired 注解定义
│   ├── InitializingBean.java # Bean 初始化接口
│   ├── BeanPostProcessor.java # Bean 处理器接口
│   ├── ImplApplicationContext.java  # Spring 容器实现
│   └── aop/                  # AOP 相关实现
│       ├── Aspect.java         # @Aspect 注解
│       ├── Before.java         # @Before 注解
│       ├── After.java          # @After 注解
│       ├── Around.java         # @Around 注解
│       ├── AfterThrowing.java  # @AfterThrowing 注解
│       ├── AopProxy.java       # AOP 代理实现
│       ├── PointcutParser.java # 切点解析器
│       └── ProceedingJoinPoint.java # 连接点实现
└── com.wangliang/       # 示例应用包
    ├── AppConfig.java       # 应用配置类
    ├── Test.java           # 测试启动类
    ├── aspect/             # 切面示例
    │   └── LoggingAspect.java # 日志切面
    └── service/            # 业务服务包
        ├── UserService.java    # 用户服务
        ├── OrderService.java   # 订单服务接口
        └── OrderServiceImpl.java # 订单服务实现
```

## 使用示例

### 1. 配置类

```java
@ComponentScan("com.wangliang.service")
public class AppConfig {
}
```

### 2. 服务定义

```java
@Component("userService")
public class UserService implements InitializingBean {
    @Autowired
    private OrderService orderService;

    // 服务方法实现...
}
```

### 3. AOP 切面

```java
@Aspect
@Component
public class LoggingAspect {
    @Before("execution(* com.wangliang.service.*.*(..))")
    public void logBefore() {
        System.out.println("=== Method execution start ===");
    }

    @Around("execution(* com.wangliang.service.UserService.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 环绕通知实现...
    }
}
```

### 4. 启动应用

```java
ImplApplicationContext applicationContext = new ImplApplicationContext(AppConfig.class);
UserService userService = (UserService) applicationContext.getBean("userService");
```

## 环境要求

- JDK 8 或以上
- Maven 3.6 或以上

## 依赖说明

- org.reflections:reflections:0.10.2 - 用于包扫描
- ch.qos.logback:logback-classic:1.2.11 - 日志输出
- junit:junit:4.13.2 - 单元测试
