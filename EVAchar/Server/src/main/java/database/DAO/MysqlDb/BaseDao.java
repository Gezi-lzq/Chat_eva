package database.DAO.MysqlDb;

import com.sun.rowset.CachedRowSetImpl;
import config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class BaseDao {

    /*
    使用的是8.0x版本的数据库驱动文件，对此，需要将加载数据库驱动的语句更改为：
    Class.forName("com.mysql.cj.jdbc.Driver");
    url:连接地址+ssl连接关闭+字符集为utf-8+时区设置
     */
    private static final String dbDriver = "com.mysql.cj.jdbc.Driver";
    // 数据库所在域
    private static final String dbUrl = "jdbc:mysql://" + Config.MYSQL_IP + ":" + Config.MYSQL_PORT + "/" + Config.DB_NAME
            + "?useUnicode=true&characterEncoding=UTF-8&userSSL=false&serverTimezone=GMT%2B8";

    public static Logger logger = LoggerFactory.getLogger(BaseDao.class);

    static {
        try {
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
            // 获取连接对象
            Connection conn = DriverManager.getConnection(dbUrl, Config.DB_USER_NAME, Config.DB_USER_PASSWORD);
//            System.out.println("已连接数据库");
            return conn;
    }


    public static void closeAll(Connection conn,PreparedStatement psql,ResultSet resultSet) throws SQLException {
        if (resultSet != null && !resultSet.isClosed())
            resultSet.close();
        if (psql != null && !psql.isClosed())
            psql.close();
        if (conn != null && !conn.isClosed())
            conn.close();
    }

    public CachedRowSetImpl executeQuerySQL(String preparedSql, Object[] param){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet num = null;

//   根据这个博客的方法     https://blog.csdn.net/Krishnna/article/details/52973388?utm_medium=distribute.pc_relevant.none-task-blog-OPENSEARCH-3.baidujs&depth_1-utm_source=distribute.pc_relevant.none-task-blog-OPENSEARCH-3.baidujs
        CachedRowSetImpl crt=null;

        /* 处理SQL,执行SQL */
        try {
            conn = getConnection(); // 得到数据库连接
            pstmt = conn.prepareStatement(preparedSql); // 得到PreparedStatement对象
            if (param != null) {
                for (int i = 0; i < param.length; i++) {
                    pstmt.setObject(i + 1, param[i]); // 为预编译sql设置参数
                }
            }
            num = pstmt.executeQuery(); // 执行SQL语句
            crt=new CachedRowSetImpl();
            crt.populate(num);
        } catch (SQLException e) {
            e.printStackTrace(); // 处理SQLException异常
        }finally {
            try {
                BaseDao.closeAll(conn, pstmt, num);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
//                BaseDao.closeAll(conn, pstmt, null);
//                当产生它的 Statement 关闭、重新执行或用于从多结果序列中获取下一个结果时，该 ResultSet 将被 Statement 自动关闭

        return crt;
    }
    //执行 DML，增删改的操作，返回影响的行数。
    public int executeUpdate(String preparedSql, Object[] param) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int num= 0;
        /* 处理SQL,执行SQL */
        try {
            conn = getConnection(); // 得到数据库连接
            pstmt = conn.prepareStatement(preparedSql); // 得到PreparedStatement对象

            if (param != null) {
                for (int i = 0; i < param.length; i++) {
                    pstmt.setObject(i + 1, param[i]); // 为预编译sql设置参数
                }
            }

            num = pstmt.executeUpdate(); // 执行SQL语句
        } catch (SQLException e) {
            e.printStackTrace(); // 处理SQLException异常
        } finally {
            try {
                BaseDao.closeAll(conn, pstmt, null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return num;
    }

    public static int getRow(String schName){
        int n=0;
        ResultSet resultSet=new BaseDao().executeQuerySQL("SELECT COUNT(*) FROM "+schName,null);
        try {
            if (resultSet.next())
                n=resultSet.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return n;
    }


}
