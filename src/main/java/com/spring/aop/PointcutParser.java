package com.spring.aop;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * 切点表达式解析器
 * 支持简单的切点表达式解析
 */
public class PointcutParser {

    /**
     * 解析切点表达式
     * 支持格式：
     * - execution(* com.example.*.*(..))
     * - execution(public * com.example.UserService.*(..))
     */
    public static boolean matches(String pointcutExpression, Method method) {
        // 移除 execution() 部分
        String pattern = pointcutExpression.substring(pointcutExpression.indexOf("(") + 1,
                pointcutExpression.length() - 1);

        // 分解表达式部分
        String[] parts = pattern.split("\\s+");

        // 检查方法修饰符（如果指定）
        if (!parts[0].equals("*")) {
            if (!method.toString().contains(parts[0])) {
                return false;
            }
        }

        // 获取类和方法部分
        String classAndMethod = parts[parts.length - 1];
        String[] methodParts = classAndMethod.split("\\.");

        // 构建正则表达式
        String regex = methodParts[methodParts.length - 1]
                .replace("*", ".*")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("..", ".*");

        // 检查方法名是否匹配
        return Pattern.compile(regex).matcher(method.getName()).matches();
    }
}