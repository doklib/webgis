package crawler;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class CrawlerTestMain {
	
	
	static String sc1 = "";
	static Date time1 = new Date();
	static ArrayList<Object> searchMapArray = new ArrayList<Object>();

	public static void getSearch(String sc, int pg, int index) throws Exception {
		// TODO Auto-generated method stub

		CrawlerTest c = new CrawlerTest();
		
		String url = "https://search.naver.com/search.naver?date_from=&date_option=0&date_to=&dup_remove=1&nso=&post_blogurl=&post_blogurl_without=&query="+sc+"&sm=tab_pge&srchby=all&st=sim&where=post&start="+pg;
		System.out.println(url);
		System.out.println(pg+"번호");
		
		List<Object> multiList = c.blog(url);
		
	
		//int totalNum = Integer.parseInt(c.blogTotal(url));
		
		ArrayList<Object> myStringArrays = new ArrayList<Object>();
		Map<String, Object> multiMap = new HashMap<>();

		int i = 0;
		for(Object ss : multiList) {
			String s = (String) ((HashMap)ss).get("url");
			String sb = c.getHtml(s);	
			
			String titleFin =(String) ((HashMap)ss).get("title");
			titleFin = titleFin.replace(",", "");
			// 블로그 내용 
			System.out.println("블로그 목록 URL : " + s);
			System.out.println(sb);
			

			// 링크 대상 페이지에 접근하기
			Document nextDoc = Jsoup.connect(s).get();
			// 상세 내용 추출하기
			String htmlList = nextDoc.select("div.se-component span").text();
			if(htmlList.equals("")) {
				htmlList = nextDoc.select("div.se_component span").text();
			}
			/*else if(htmlList.equals("")){
				htmlList = nextDoc.select("div. span").text();
		
			}*/
			
			if(!htmlList.equals("")&&htmlList.length()<30000) {
				
			
			String dateTime = nextDoc.select("span.se_publishDate").text();
			
			SimpleDateFormat orginFormat = new SimpleDateFormat ( "yyyy. M. dd. HH:mm");
			SimpleDateFormat format = new SimpleDateFormat ( "yyyyMMdd");	
		
			if(dateTime.contains("전")) {	
				Date time = new Date();
				dateTime = orginFormat.format(time);
			}
			
			
			Date orginDate = orginFormat.parse(dateTime);
			dateTime = format.format(orginDate);
			//System.out.println(newDate);
			System.out.println(dateTime);	
			
			// 블로그 내용 가져오기 테스트
			System.out.println(s);
			
			
			
			/*if(htmlList.length()>10000) {
				int leng = htmlList.length();
			} else {
				
			}*/
			
			String htmlList2 = htmlList.replace(",", "");
			System.out.println(htmlList2);
			
			
			Map<String, Object> contextMap = new HashMap<>();
			contextMap.put("url", s);
			contextMap.put("date", dateTime);
			contextMap.put("text", htmlList2);
			contextMap.put("title", titleFin);
			
			myStringArrays.add(contextMap);
			
			}
			i++;
		}
		 
		
		

		// sc 이름으로 저장하기
		createCSV(myStringArrays, index);

		
	}
	
	public static int createCSV(ArrayList<Object> myStringArrays, int index){
	       
		   int resultCount =0; 
		    
		   try{
			   
			   HashMap<String,Object> searched = (HashMap<String, Object>) searchMapArray.get(index);
			   String AddrOut =  (String) searched.get("Addr");
			   String AgeOut = (String) searched.get("Age");
    		   String SexOut = (String) searched.get("Sex");
    		   String PurposeOut = (String) searched.get("Purpose");
    		   String CompanionOut = (String) searched.get("Companion");
    		   String SeasonOut = (String) searched.get("Season");
			   
			   
		       BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:\\workspace\\workspace2\\Crawler\\"+"결과"+".csv",true),"MS949"));
		    
		       for(Object dom : myStringArrays) {	
	    		   String a = AddrOut+","+AgeOut+","+SexOut+","+PurposeOut+","+CompanionOut+","+SeasonOut+","+
	    		   ((HashMap)dom).get("url")+","+((HashMap)dom).get("title")+","+((HashMap)dom).get("date")+","+((HashMap)dom).get("text");
	    		   fw.write(a);
	    		   fw.newLine();
	    		   resultCount++;  
			       }
		       
		       fw.flush();
		       fw.close();
		      
		   }catch (Exception e) {
		     e.printStackTrace();
		   }
		       return resultCount;
		   }
	



	public static void main(String[] args) throws Exception{
		
		String value="";
		
		FileInputStream fis=new FileInputStream("D:\\workspace\\workspace2\\Crawler\\검색어.xlsx");
		XSSFWorkbook workbook=new XSSFWorkbook(fis);
         
         
		int rowindex=0;
		//시트 수 (첫번째에만 존재하므로 0을 준다)
		//만약 각 시트를 읽기위해서는 FOR문을 한번더 돌려준다
		XSSFSheet sheet=workbook.getSheetAt(0);
		//행의 수
		int rows=sheet.getPhysicalNumberOfRows();
		for(rowindex=1;rowindex<rows;rowindex++){
			//행을읽는다
			XSSFRow row=sheet.getRow(rowindex);
			
			Map<String, Object> searchMap = new HashMap<>();
			searchMap.put("Addr", String.valueOf(row.getCell(0)));
			searchMap.put("Age", String.valueOf(row.getCell(1)));
			searchMap.put("Sex", String.valueOf(row.getCell(2)));
			searchMap.put("Purpose", String.valueOf(row.getCell(3)));
			searchMap.put("Companion", String.valueOf(row.getCell(4)));
			searchMap.put("Season", String.valueOf(row.getCell(5)));
			searchMap.put("rownum", rowindex);
			searchMapArray.add(searchMap);

		System.out.println(searchMapArray);
		}	
		
		for(Object arr : searchMapArray) {	
	 		   value = ((HashMap)arr).get("Addr")+"+"+((HashMap)arr).get("Age")+"+"+((HashMap)arr).get("Sex")+"+"+((HashMap)arr).get("Purpose")+"+"+((HashMap)arr).get("Companion")+"+"+((HashMap)arr).get("Season");
	 		   int row = (int) ((HashMap)arr).get("rownum");
	 		  int index = searchMapArray.indexOf(arr);

		
			String encodeResult = URLEncoder.encode(value, "UTF-8");
			String sc = encodeResult;
					//value;
					//"테스트+경주";
			//"%EA%B2%BD%EA%B8%B0%EB%8F%84%2B20%EB%8C%80%20%20%20%20%EB%82%A8%EC%9E%90%2B%2B%ED%9C%B4%EC%96%91%2B%EA%B0%80%EC%A1%B1%2B%EB%B4%84%2B%EA%B5%AD%EB%82%B4%EC%97%AC%ED%96%89%2B";
			sc1 = value;
			
			
			CrawlerTest c = new CrawlerTest();
			String url = "https://search.naver.com/search.naver?date_from=&date_option=0&date_to=&dup_remove=1&nso=&post_blogurl=&post_blogurl_without=&query="+sc+"&sm=tab_pge&srchby=all&st=sim&where=post&start=1";
			int totalNum = Integer.parseInt(c.blogTotal(url));
			
			for(int i = 0; i <totalNum/1000; i++) {
	//		for(int i = 0; i <5; i++) {	
				getSearch(sc,i*10+1,index);
			
			}
		}
			
	}
		
}
