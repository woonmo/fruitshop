package common.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 모든 URL 을 받아주는 컨트롤러(in 호텔프런트)

@WebServlet(
		description = "사용자가 웹에서 *.up 을 했을 경우 이 서블릿이 응답을 해주도록 한다.",
		urlPatterns = {"*.ddg"},
		initParams = { 
				@WebInitParam(name = "propertyConfig", value = "C:/NCS/workspace_jsp/fruitshop/src/main/webapp/WEB-INF/Command.properties", description = "*.ddg 에 대한 클래스의 매핑파일") 
	      }
		)
public class FrontController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private Map<String, Object> cmdMap = new HashMap<>();
	
	// Servlet 환경설정을 해주는 메소드
	// 구동 시 딱 한 번만 호출 됨
	public void init(ServletConfig config) throws ServletException {
		/*
			웹브라우저 주소창에서  *.up 을 하면 FrontController 서블릿이 응대를 해오는데 
			맨 처음에 자동적으로 실행되어지는 메소드가 init(ServletConfig config) 이다.
			여기서 중요한 것은 init(ServletConfig config) 메소드는 WAS(톰캣)가 구동되어진 후
			딱 1번만 init(ServletConfig config) 메소드가 실행되어지고, 그 이후에는 실행이 되지 않는다. 
			그러므로 init(ServletConfig config) 메소드에는 FrontController 서블릿이 동작해야할 환경설정을 잡아주는데 사용된다.
		 */   
		/* *** 확인용 *** */ 
//		System.out.println("~~~ 확인용 => 서블릿 FrontController 의 init(ServletConfig config) 메소드 실행 됨");
		
		FileInputStream fis = null;		// 특정 파일에 있는 내용을 읽어들이기 위한 객체
		
		String props = config.getInitParameter("propertyConfig");	// key: propertyConfig value: /Users/lee/Documents/NCS/workspace_jsp/MyMVC/src/main/webapp/WEB-INF/Command.properties
//		System.out.println("~~~ 확인용 props => "+ props);
		// ~~~ 확인용 props => /Users/lee/Documents/NCS/workspace_jsp/MyMVC/src/main/webapp/WEB-INF/Command.properties
		
		try {
			// fis 는 /Users/lee/Documents/NCS/workspace_jsp/MyMVC/src/main/webapp/WEB-INF/Command.properties 파일의 내용을 읽어들이기 위한 객체
			fis = new FileInputStream(props);
			
			
			// Properties 는 Collection 중 HashMap 계열중의 하나로써
			// "key","value"으로 이루어져 있는것이다.
			// 그런데 중요한 것은 Properties 는 key도 String 타입이고, value도 String 타입만 가능하다는 것이다.
			// key는 중복을 허락하지 않는다. value 값을 얻어오기 위해서는 key값 만 알면 된다.
			Properties pr = new Properties();
			
			pr.load(fis);
			/*
		         pr.load(fis); 은 fis 객체를 사용하여 C:/NCS/workspace_jsp/MyMVC/src/main/webapp/WEB-INF/Command.properties 파일의 내용을 읽어다가   
		         Properties 클래스의 객체인 pr 에 로드시킨다.
		         그러면 pr 은 읽어온 파일(Command.properties)의 내용에서 
		         = 을 기준으로 왼쪽은 key로 보고, 오른쪽은 value 로 인식한다.
	        */
			
			Enumeration<Object> en = pr.keys();		// properties 에 등록된 key 들을 읽어옴
			/*
	          pr.keys(); 은
	          C:/NCS/workspace_jsp/MyMVC/src/main/webapp/WEB-INF/Command.properties 파일의 내용물에서 
	          = 을 기준으로 왼쪽에 있는 모든 key 들만 가져오는 것이다.   
	        */
			
			while(en.hasMoreElements()) {		// en.hasMoreElements(): en 에 요소가 더 있는지 판별. return type boolean
				String key = (String)en.nextElement();
//				System.out.println("~~~ 확인용 key => "+ key);
//				System.out.println("~~~ 확인용 value => "+ pr.getProperty(key) +"\n");
				/*
				 	~~~ 확인용 key => /test3.up
					~~~ 확인용 value => test.controller.Test3Controller
					
					~~~ 확인용 key => /test1/test2.up
					~~~ 확인용 value => test.controller.Test2Controller
					
					~~~ 확인용 key => /test1.up
					~~~ 확인용 value => test.controller.Test1Controller
				 */
				String className = pr.getProperty(key);
				
				if (className != null) {
					className = className.trim();	// 혹시 모를 공백입력에 대한 대비
					
					
					// <?> 은 generic 인데 어떤 클래스 타입인지는 모르지만 하여튼 클래스 타입이 들어온다는 뜻이다.
	                // String 타입으로 되어진 className 을 클래스화(객체화?) 시켜주는 것이다.
	                // 주의할 점은 실제로 String 으로 되어져 있는 문자열이 클래스로 존재해야만 한다는 것이다.
					Class<?> cls = Class.forName(className);	// 클래스 명을 가지고 실제로 클래스화를 시켜줌
					
					Constructor<?> constrt = cls.getDeclaredConstructor();		// 해당 클래스에 대한 생성자 만들기
					
					Object obj = constrt.newInstance();		// 생성자로부터 실제 객체(인스턴스)를 생성해주는 것
					/*
					 	$$$ 확인용 Test3Controller 클래스 생성자 호출함 $$$
						@@@ 확인용 Test2Controller 클래스 생성자 호출함 @@@
						### 확인용 Test1Controller 클래스 생성자 호출함 ###
					 */
					
					cmdMap.put(key, obj);
					
				}// end of if (className != null) ---------------
				
			}// end of while(en.hasMoreElements()) -----------------
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e ) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println(">> 문자열로 명명되어진 클래스가 존재하지 않습니다. properties 확인 <<");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}// end of public void init(ServletConfig config) throws ServletException -----------------------
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		System.out.println("~~~~ 확인용 => 서블릿 FrontController 의 doGet 메소드가 실행 됨.");
		
		// 웹브라우저의 주소 입력창에서 
	    // http://localhost:9090/MyMVC/member/idDuplicateCheck.up?userid=leess 와 같이 입력되었더라면
//		String url = request.getRequestURL().toString();
		
//		System.out.println("~~~~ 확인용 url => "+ url);
		// ~~~~ 확인용 url => http://localhost:9090/MyMVC/member/idDuplicateCheck.up
		
		
		// 웹브라우저의 주소 입력창에서 
	    // http://localhost:9090/MyMVC/member/idDuplicateCheck.up?userid=leess 와 같이 입력되었더라면		
		String uri = request.getRequestURI();
//		System.out.println("~~~~ 확인용 uri => "+ uri);	// 더 자주 씀
		// ~~~~ 확인용 uri => /MyMVC/member/idDuplicateCheck.up
		
		/*
		 	~~~~ 확인용 uri => /MyMVC/test1.up
		 	~~~~ 확인용 uri => /MyMVC/test1/test2.up
		 	~~~~ 확인용 uri => /MyMVC/test3.up
		 */
		
		String key = uri.substring(request.getContextPath().length());		//  /MyMVC/test3.up 에서	/MyMVC 만 잘라냄 -> /test3.up
		
		AbstractController action = (AbstractController) cmdMap.get(key);	// 객체를 가져옴
		// 여기 만약 AbstractController 를 쓰지 않고 Test1Controller 를 가져온다면 서블릿 매핑 된 클래스를 전부 만들어야? -> 내일 질문****
		// 그게 아니라 AbstractController 만 사용한다면 프로젝트 내 모든 클래스(회원, 상품, 장바구니 등) 에서 AbstractController 를 상속받아야?
		// AbstractController 에 InterCommand 를 implement 하는 이유?(interface 를 사용하는 이유)
		// 프로젝트 내 모든 클래스가 InterCommand 의 execute 를 overriding 해야 하는지?
	
		
		if (action == null) {
			System.out.println(">>> "+ key +" 은 URI 패턴에 매핑된 클래스가 없습니다. <<<");
			// >>> /member/idDuplicateCheck.up 은 URI 패턴에 매핑된 클래스가 없습니다. <<<
		}
		else {
			try {
				action.execute(request, response);
				
				boolean bool 	= action.isRedirect();
				String viewPage = action.getViewPage();
				
				if (!bool) {
					// viewPage 에 명기된 view 단 페이지로 forward(dispatcher)를 하겠다는 말이다.
	                // forward 되어지면 웹브라우저의 URL주소 변경되지 않고 그대로 이면서 화면에 보여지는 내용은 forward 되어지는 jsp 파일이다.
	                // 또한 forward 방식은 forward 되어지는 페이지로 데이터를 전달할 수 있다는 것이다.
					if (viewPage != null) {
						request.getRequestDispatcher(viewPage).forward(request, response);
					}
				}
				else {
					// viewPage 에 명기된 주소로 sendRedirect(웹브라우저의 URL주소 변경됨)를 하겠다는 말이다.
	                // 즉, 단순히 페이지이동을 하겠다는 말이다. 
	                // 암기할 내용은 sendRedirect 방식은 sendRedirect 되어지는 페이지로 데이터를 전달할 수가 없다는 것이다.
					if (viewPage != null) {
						response.sendRedirect(viewPage);
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response); 
	}
}
