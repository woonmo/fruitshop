package common.controller;

import java.sql.SQLException;
import java.util.List;

import index.domain.MainVO;
import index.model.MainDAO;
import index.model.MainDAO_imple;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class IndexController extends AbstractController {

	private MainDAO maindao;	// 초기 선언
	
	public IndexController() {
		maindao = new MainDAO_imple();	// WAS 가 구동 될 때 (기본생성자가 생성될 때) 객체를 생성하여 넣어 줌
	}
	
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		try {
			List<MainVO> imgList = maindao.imgaeSelectAll();
			
			if (imgList.size() > 0) {
				request.setAttribute("imgList", imgList);
			}
			
			super.setRedirect(false);
			super.setViewPage("/WEB-INF/index/index.jsp");
			
		} catch (SQLException e) {	// 쿼리문 오류 발생 시
			e.printStackTrace();
			super.setRedirect(true);	// redirect 시킴
			super.setViewPage(request.getContextPath()+"/error.ddg");
		}
	}// end of public void execute() ------------------

}
