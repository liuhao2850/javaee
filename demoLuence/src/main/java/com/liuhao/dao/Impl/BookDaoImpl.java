package com.liuhao.dao.Impl;

import com.liuhao.dao.BookDao;
import com.liuhao.po.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDaoImpl implements BookDao {

    public List<Book> findAllBooks() {
        ArrayList<Book> list = new ArrayList<Book>();
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
             connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/lucene?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC&useSSL=false","root","rootroot");
             preparedStatement =connection.prepareStatement("select * from book");
             rs = preparedStatement.executeQuery();
            while(rs.next()){
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setBookname(rs.getString("bookname"));
                book.setPic(rs.getString("pic"));
                book.setPrice(rs.getFloat("price"));
                book.setBookdesc(rs.getString("bookdesc"));
                list.add(book);
                System.out.println("1");


            }



        } catch (Exception e) {
            e.printStackTrace();
        }finally {

                try {
                    if(rs!=null) rs.close();
                    if(preparedStatement!=null) preparedStatement.close();
                    if(connection!=null) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

        }


        return list;
    }
}
