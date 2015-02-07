package com.uestc.spider.www;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.uestc.ifeng.www.IFENGMil;

public class CRUT {

	private Mongo mg  = null;
	private DB db ;
	private DBCollection users;
	private GridFS gd;
	private String DBName ;  // = "TODAY"; //���ݿ����� �������б� �ɶ��̱���
	private String DBTable  ; //= "cg"; //���ݿ����
	public CRUT(String dbname,String dbtable){
		
		this.DBName = dbname ;
		this.DBTable = dbtable;
		try {
			
            mg = new Mongo();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MongoException e) {
            e.printStackTrace();
        }
		//��ȡcdsb DB�����Ĭ��û�д�����mongodb���Զ�����
		db = mg.getDB(DBName);
		//��ȡusers DBCollection�����Ĭ��û�д�����mongodb���Զ�����
		users = db.getCollection(DBTable);
//connect reset!!! ��Ҫ����һ��	 ��������Ҫ������Ҫ��
//		gd = new GridFS(db);
//		System.out.println("�ұ�ִ����");
	}
	//ɾ�����ݿ�
	public void destory() {
        if (mg != null)
            mg.close();
        mg = null;
        db = null;
        users = null;
//        System.gc();
    }
	
	/*
	 * �������,��������,����s�ĳ��ȱ���Ϊż��
	 * ����Ϊ��������Ŀ���������ݣ��������ƣ�����ʱ��
	 * k-v:(name ,time) (content , office)
	 */
	public void add(String title,String originalTitle,String titleContent,
			String time ,String content,
			String newSource,String originalSource,
			String category,String originalCategroy,
			String url ,String image){
		DBObject user = new BasicDBObject();
		//�������⣺���⣬���ݱ��⣬ԭʼ����
		user.put("Title", title);
		user.put("OriginalTitle", originalTitle);
		user.put("TitleContent", titleContent);
		
		//����ʱ��
		user.put("Time", time);
		//��������
		user.put("Content",content);
		//����������Դ ��������Դ������ԭʼ��Դ
		user.put("NewSource",newSource);
		user.put("OriginalSource", originalSource);
		//�������ŷ��� ����� ����ԭʼ���
		user.put("Category", category);
		user.put("OriginalCategroy", originalCategroy);
		//������ַ
		user.put("Url", url);
		//����ͼƬ
		user.put("image",image);

		users.insert(user);
		users.addOption(Bytes.QUERYOPTION_NOTIMEOUT);

		
	}
	//���� �����ȡʱ��
	public void add(String title,String originalTitle,String titleContent,
			String time ,String content,
			String newSource,String originalSource,
			String category,String originalCategroy,
			String url ,String image,String downloadtime){
		DBObject user = new BasicDBObject();
		//�������⣺���⣬���ݱ��⣬ԭʼ����
		user.put("Title", title);
		user.put("OriginalTitle", originalTitle);
		user.put("TitleContent", titleContent);
		
		//����ʱ��
		user.put("Time", time);
		//��������
		user.put("Content",content);
		//����������Դ ��������Դ������ԭʼ��Դ
		user.put("NewSource",newSource);
		user.put("OriginalSource", originalSource);
		//�������ŷ��� ����� ����ԭʼ���
		user.put("Category", category);
		user.put("OriginalCategroy", originalCategroy);
		//������ַ
		user.put("Url", url);
		//����ͼƬ
		user.put("image",image);
		user.put("downloadTime",downloadtime);

		users.insert(user);
		users.addOption(Bytes.QUERYOPTION_NOTIMEOUT);

		
	}
	//���� �����ȡʱ��
		public void add(String title,String originalTitle,String titleContent,
				String time ,String content,
				String newSource,String originalSource,
				String category,String originalCategroy,
				String url ,String image,String downloadtime,Date date){
			DBObject user = new BasicDBObject();
			//�������⣺���⣬���ݱ��⣬ԭʼ����
			user.put("Title", title);
			user.put("OriginalTitle", originalTitle);
			user.put("TitleContent", titleContent);
			
			//����ʱ��
			user.put("Time", time);
			//��������
			user.put("Content",content);
			//����������Դ ��������Դ������ԭʼ��Դ
			user.put("NewSource",newSource);
			user.put("OriginalSource", originalSource);
			//�������ŷ��� ����� ����ԭʼ���
			user.put("Category", category);
			user.put("OriginalCategroy", originalCategroy);
			//������ַ
			user.put("Url", url);
			//����ͼƬ
			user.put("image",image);
			user.put("downloadTime",downloadtime);
			user.put("Date", date);

			users.insert(user);
			users.addOption(Bytes.QUERYOPTION_NOTIMEOUT);

			
		}
	//���غ��� ���������� 
	public void add(String url,String comment,String commentUrl){
		
		DBObject user = new BasicDBObject();
		//���ж��Ƿ���ڸ�������
//		if(!query("Url",url)){ //���������
		//�������⣺���⣬���ݱ��⣬ԭʼ����
		user.put("Url", url);
		user.put("CommentUrl",commentUrl);
		if(comment == null || comment == "")
			user.put("CommentNumber", 0);
		else if(comment.contains("\n")){
			user.put("CommentNumber", comment.substring(0, comment.indexOf("\n")));
		}
		if(comment == null || comment == "")
			user.put("Comment", "�����ۣ�");
		else if(comment.contains("\n"))
			user.put("Comment", comment.substring(comment.indexOf("\n"),comment.length()));
		else
			user.put("Comment", comment);

	     users.insert(user);
	     users.addOption(Bytes.QUERYOPTION_NOTIMEOUT);

	}
	//����add ����download time
	public void add(String url,String commentUrl,Queue<String> comment,String downloadTime){
		
		DBObject user = new BasicDBObject();
		//���ж��Ƿ���ڸ�������
//		if(!query("Url",url)){ //���������
		//�������⣺���⣬���ݱ��⣬ԭʼ����
		user.put("Url", url);
		user.put("CommentUrl",commentUrl);
		if(comment == null)
			user.put("Comment", "�����ۣ�");
		else
			user.put("Comment", comment);
		user.put("DownloadTime", downloadTime);
	    users.insert(user);
	    users.addOption(Bytes.QUERYOPTION_NOTIMEOUT);

	}
	//����add ����download time
	public void add(String url,String commentUrl,Queue<String> comment,String downloadTime,Date date){
		
		DBObject user = new BasicDBObject();
		//���ж��Ƿ���ڸ�������
//		if(!query("Url",url)){ //���������
		//�������⣺���⣬���ݱ��⣬ԭʼ����
		user.put("Url", url);
		user.put("CommentUrl",commentUrl);
		if(comment == null)
			user.put("Comment", "�����ۣ�");
		else
			user.put("Comment", comment);
		user.put("DownloadTime", downloadTime);
		user.put("Date", date);
	    users.insert(user);
	    users.addOption(Bytes.QUERYOPTION_NOTIMEOUT);

	}
	//������������ ɾ��downloadTime
	public void add(String url , String commentUrl,Queue<String> comment , Date date){
		DBObject user = new BasicDBObject();
		user.put("Url", url);
		user.put("CommentUrl",commentUrl);
		if(comment == null)
			user.put("Comment", null);
		else
			user.put("Comment", comment);
		user.put("Date", date);
		if(url!=null&&url.contains("163.com")){
		
			System.out.println(url+"---"+comment+"----"+date);
		}
	    users.insert(user);
	    users.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
	}
	//��ѯ���Բ�������Ŀ���������ݣ����ŷ���ʱ�䣬�������Ƶ�
	public boolean query(String key,String value){
		boolean buf = false ;
		if(users.findOne(new BasicDBObject(key, value)) != null){
			buf = true ;
		}
		users.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
		return buf;
		
	}
	//�������� �е��鷳���������Ҫ�������Ƿ��ظ������ٴδ���
	@SuppressWarnings("unused")
	public void update(String url ,String commentUrl ,Queue<String> newComment,Date date){
		users.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
		DBObject buf  = users.findOne(new BasicDBObject("Url", url));
		BasicDBList commentQueue = null;
		Queue<String> bufQueue = new LinkedList<String>();
		Queue<String> bufQueue1 = new LinkedList<String>();
		if(buf.get("Comment") != null){
			commentQueue = (BasicDBList) buf.get("Comment");
			for(int i = 0 ; i < commentQueue.size();i ++){
				bufQueue.offer(commentQueue.get(i).toString());
				bufQueue1.offer(commentQueue.get(i).toString());
			}
		}else{
			bufQueue = null;
		}
		
		boolean flag = false ;
		if(bufQueue != null){
			
			if(newComment != null){
				while (!newComment.isEmpty()) {
					String bufComment = newComment.poll(); //��ȡ������
					String bufComment1 = bufComment ;       //��������
					bufComment = bufComment.substring(0, bufComment.lastIndexOf("--"));
					for(String i :bufQueue1){
						String t = i ;
						t = t.substring(0, t.lastIndexOf("--"));
						if(bufComment.equals(t)){
							flag = true ;
						}
					}
					if(flag == false)
						bufQueue.offer(bufComment1);
					flag = false ;
				}
			}
				
			
		}else{
			bufQueue = newComment ;
		}
		
		users.remove(buf);
		DBObject user = new BasicDBObject();
		user.put("Url", url);
		user.put("CommentUrl",commentUrl);
		if(bufQueue == null)
			user.put("Comment", null);
		else
			user.put("Comment", bufQueue);
		user.put("Date", date);
		if(url!=null&&url.contains("163.com")){
			
			System.out.println(url+"---"+bufQueue+"----"+date);
		}
	    users.insert(user);
//	    users.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
	}
	//�鿴���ݿ�����������
    @SuppressWarnings("unused")
	private void queryAll() {
    	DBCollection users = db.getCollection("users");
		System.out.println("��ѯusers���������ݣ�");
		//db�α�
		DBCursor cur = users.find();
		while (cur.hasNext()) {
			 System.out.println(cur.next());
			 
		}
		
    }
  //ɾ������
  	 public void remove(String key ,String value) {
  		    users.remove(new BasicDBObject(key, new BasicDBObject("$gte", value))).getN();
  		
  	 }
//���Ӵ�ӡ��־

    public static void main(String args[]){
    	
    	CRUT test = new CRUT("TODAY" ,"CG");
//		for(String name:mg.getDatabaseNames())
//			System.out.println(name);
//		String url1 = "http://e.chengdu.cn/html/2014-09/10/content_487767.htm";
//		CDSB test1 = new CDSB(url1);	
//		test.queryAll();
//    	test.destory();
//    	test.add("xixi", "2014.9.10", "��ʦ�ڿ���", "uestc");
//    	test.remove("xi","2014.9.10");
//    	test.add("xi","2014.9.10","jiaoshijiekuailfe","uestc");
//		System.out.println(test1.handleTitle(test1.text)+"      gfhfhfg");
//		test.add(test1.handleTitle(test1.text),test1.handleTime(test1.text),test1.handleContent(test1.text),test1.handleOfficeName(test1.text),test1.handlePage(test1.text),url1);
    	test.query("Url","http://e.chengdu.cn/html/2014-10/16/content_493017.htm");
//    	test.query(test1.handleTitle(test1.text),"2014");
//    	test.query("xixi","2014.9.10");
    }
}
