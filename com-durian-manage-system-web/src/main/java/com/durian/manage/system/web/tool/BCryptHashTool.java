package com.durian.manage.system.web.tool;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 一次性工具：生成 BCrypt 哈希，用于手动重置数据库里的密码。
 *
 * 用法（项目根目录）：
 *   mvn compile -pl com-durian-manage-system-web -am -DskipTests
 *   mvn exec:java -pl com-durian-manage-system-web -Dexec.mainClass=com.durian.manage.system.web.tool.BCryptHashTool -Dexec.args="你的新密码"
 *
 * 把打印出来的哈希复制到 SQL 里：
 *   UPDATE `user` SET password='<打印的哈希>' WHERE username='你的用户名';
 */
public class BCryptHashTool {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("用法: BCryptHashTool <要哈希的密码>");
            return;
        }
        String plain = args[0];
        String hash = new BCryptPasswordEncoder(10).encode(plain);
        System.out.println("====== BCrypt 哈希结果 ======");
        System.out.println("明文密码: " + plain);
        System.out.println("BCrypt:   " + hash);
        System.out.println();
        System.out.println("SQL 重置语句示例：");
        System.out.println("UPDATE `user` SET password='" + hash + "' WHERE username='你的用户名';");
    }
}
