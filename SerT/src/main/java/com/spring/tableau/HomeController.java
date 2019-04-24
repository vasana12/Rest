package com.spring.tableau;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.spring.tableau.Entity.MultiLaunchWbEntity;
import com.spring.tableau.util.RestApiUtils;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger s_logger = LoggerFactory.getLogger(HomeController.class);
	GetWorkbooksList getWbListClassObj = new GetWorkbooksList();
	GetProjectList getPjListClassObj = new GetProjectList();
	GetViewList getViewListClassObj = new GetViewList();
	
	private static Properties s_properties = new Properties();

	static {
		// Configures the logger to log to stdout
		BasicConfigurator.configure();

		// Loads the values from configuration file into the Properties instance
		try {
			s_properties.load(new ClassPathResource("config.properties").getInputStream());
		} catch (IOException e) {
			s_logger.error("Failed to load configuration files.");
		}
	}
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		
		
		s_logger.info("Welcome home! The client locale is {}.", locale);
		return "home";
	}
	
	@RequestMapping(value = "index.do", method = RequestMethod.GET)
	public String index(Locale locale, Model model) {
		
		System.out.println("test_ViewName");
		
		List<MultiLaunchWbEntity> getViewListClassObj = getViewListClassObj.g
		s_logger.info("Welcome home! The client locale is {}.", locale);
		return "index";
	}

	
	@RequestMapping(value = "table.do", method = RequestMethod.GET)
	public ModelAndView table(Locale locale, Model model) {
		s_logger.info("Welcome home! The client locale is {}.", locale);
		ModelAndView mv = new ModelAndView();
		List<MultiLaunchWbEntity> getPjList = getPjListClassObj
				.getPjList();
		mv.addObject("tableList", getPjList);
		
		mv.setViewName("table");
		return mv;
	}
	@RequestMapping(value = "tableauurl.re", method = RequestMethod.POST, produces="application/json")  //1
	public @ResponseBody ModelAndView  getTicket(HttpServletRequest request) {
		
		System.out.println("tableauurl.do");
		
		String userId = "admin";
		String site = "";
		
		 Map<String,Object> returnModel = Util.getTicket(userId, site, Util.s_properties.getProperty("server.host"));
		ModelAndView mv = new ModelAndView();
		mv.addObject("MAP",returnModel);
		mv.setViewName("getTicket");
		 System.out.println("returnModel : " + returnModel); // ws
		 
		return mv;
	}
	
	@RequestMapping(value = "getTicket.do", method = RequestMethod.GET)  //1
	public ModelAndView makeTicket(HttpServletRequest request) {
		
		String workBookName =request.getParameter("workBookName");
		System.out.println("workBookName="+workBookName);
		ModelAndView mv = new ModelAndView();	
		mv.addObject("workBookName",workBookName);
		String userId = "admin";
		String site = "";
		
		 Map<String,Object> returnModel = Util.getTicket(userId, site, Util.s_properties.getProperty("server.host"));
		 System.out.println("returnModel : " + returnModel); // ws
		 System.out.println(returnModel.get("data"));
		 
		mv.addObject("ticket",returnModel.get("data"));
		mv.setViewName("getTicket");
		return mv;
	}
	
	
	
}
