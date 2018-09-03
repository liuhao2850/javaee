package com.liuhao.index;

import com.liuhao.dao.Impl.BookDaoImpl;
import com.liuhao.po.Book;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IndexManager {
    private static final String path = "//Users//liuhao//Downloads//index";

    @Test
    public void createIndex() throws IOException {

        BookDaoImpl dao = new BookDaoImpl();
        List<Book> allBooks = dao.findAllBooks();

        ArrayList<Document> documents = new ArrayList<Document>();
        for (Book book : allBooks) {
            Document document = new Document();
            document.add(new TextField("bookId", book.getId() + "", Field.Store.YES));
            document.add(new TextField("bookName", book.getBookname(), Field.Store.YES));
            document.add(new TextField("bookPrice", book.getPrice() + "", Field.Store.YES));
            document.add(new TextField("bookPic", book.getPic(), Field.Store.YES));
            document.add(new TextField("bookDesc", book.getBookdesc(), Field.Store.YES));
            documents.add(document);
        }
//        3.建立分析器（分词器）对象(Analyzer)
        //StandardAnalyzer analyzer = new StandardAnalyzer();
        IKAnalyzer analyzer = new IKAnalyzer();
        //        4.建立索引库配置对象（IndexWriterConfig），配置索引库
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
        //        5.建立索引库目录对象（Directory），指定索引库的位置
        File file = new File("//Users//liuhao//Downloads//index");
        FSDirectory directory = FSDirectory.open(file);
        //        6.建立索引库操作对象（IndexWriter），操作索引库
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
//        7.使用IndexWriter，把文档对象写入索引库
        for (Document document : documents) {
            indexWriter.addDocument(document);
        }
        //        8.释放资源
        indexWriter.close();
    }

    @Test
    public void readIndex() throws Exception {
        //        1.建立分析器对象（Analyzer），用于分词
        IKAnalyzer analyzer = new IKAnalyzer();
        //        2.建立查询对象（Query）
        // 2.1.建立查询解析器对象
        QueryParser parser = new QueryParser("book", analyzer);
        // 2.2.使用查询解析器对象，解析表达式，实例化Query对象
        Query query = parser.parse("bookName:java");
        //        3.建立索引库目录对象（Directory），指定索引库的位置
        FSDirectory directory = FSDirectory.open(new File(path));
        //        4.建立索引读取对象（IndexReader），把索引数据读取内存中
        DirectoryReader reader = DirectoryReader.open(directory);
        //        5.建立索引搜索对象（IndexSearcher），执行搜索，返回搜索的结果集（TopDocs）
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        //        6.处理结果集
        // 6.1.实际搜索到的结果数量
        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println(topDocs.totalHits);
        // 6.2.获取结果数据 只有ID和分数
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc sd : scoreDocs) {
            int id = sd.doc;
            float score1 = sd.score;
            System.out.println("当前文档的Id：" + id + ",当前文档的分值：" + score1);
            //根据ID查出每一条数据
            Document doc = indexSearcher.doc(id);
            System.out.println("图书ID" + doc.get("bookId"));
            System.out.println("图书名" + doc.get("bookName"));
            System.out.println("图书价格" + doc.get("bookPrice"));
            System.out.println("图书图片" + doc.get("bookPic"));
            System.out.println("图书介绍" + doc.get("bookDesc"));
        }
        reader.close();
    }
}
