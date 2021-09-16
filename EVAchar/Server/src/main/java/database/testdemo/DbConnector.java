package database.testdemo;

import config.Config;

import java.sql.*;


public class DbConnector {
    private Connection conn = null;

    private PreparedStatement psql = null;

    private ResultSet resultSet = null;

    public DbConnector() {
        /*
        5.x版本的驱动文件jar包对应的是：
        Class.forName("com.mysql.jdbc.Driver");语句来加载数据库驱动
        而我使用的是8.0x版本的数据库驱动文件，对此，需要将加载数据库驱动的语句更改为：
        Class.forName("com.mysql.cj.jdbc.Driver");
        url:连接地址+ssl连接关闭+字符集为utf-8+时区设置
         */
        // 数据库驱动名
        String dbDriver = "com.mysql.cj.jdbc.Driver";
        // 数据库所在域
        String dbUrl = "jdbc:mysql://" + Config.MYSQL_IP + ":" + Config.MYSQL_PORT + "/" + Config.DB_NAME
                + "?useUnicode=true&characterEncoding=UTF-8&userSSL=false&serverTimezone=GMT%2B8";
        try {
            // 加载驱动
            Class.forName(dbDriver);
            // 获取连接对象
            conn = DriverManager.getConnection(dbUrl, Config.DB_USER_NAME, Config.DB_USER_PASSWORD);
            System.out.println("成功连接数据库");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("无法连接到数据库：" + e.getMessage());
        }
    }

    public void close() {
        try {
            if (resultSet != null && !resultSet.isClosed())
                resultSet.close();
            System.out.println("结果集成功关闭");
        } catch (SQLException e) {
            System.out.println("结果集关闭异常：" + e.getMessage());
        }
        try {
            if (psql != null && !psql.isClosed())
                psql.close();
            System.out.println("更新集成功关闭");
        } catch (SQLException e) {
            System.out.println("更新集关闭异常：" + e.getMessage());
        }
        try {
            if (conn != null && !conn.isClosed())
                conn.close();
            System.out.println("数据库连接成功关闭");
        } catch (SQLException e) {
            System.out.println("数据库连接关闭异常：" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new DbConnector().close();
    }

    /**
     * @Title: getFromDatabase
     * @Description: 利用当前的数据连接查询，通过一条SQL语句
     * @param: sql
     *             SQL语句
     * @return: ResultSet 查询结果集
     * @throws: SQLException
     *              查询失败或参数化sql失败抛出该异常
     */
    public ResultSet getFromDatabase(String sql) {
        try {
            // 创建对象参数化 sql
            psql = conn.prepareStatement(sql);

            // 开始查询
            resultSet = psql.executeQuery();
        } catch (SQLException e) {
            System.out.println("数据库查询失败：" + e.getMessage());
        }
        return resultSet;
    }

    /**
     * @Title: putToDatabase
     * @Description: 通过SQL语句更新数据库
     * @param: sql
     *             一条更新SQL语句
     * @return: void
     * @throws: SQLException
     *              执行update失败抛出该异常
     */
    public void putToDatabase(String sql) {
        try {
            psql = conn.prepareStatement(sql);
            psql.executeUpdate();
        } catch (SQLException e) {
            System.out.println("更新数据库异常：" + e.getMessage());
        }
    }

}
