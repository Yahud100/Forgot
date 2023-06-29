package controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import DAO.ForgotPasswordDAOImpl;
import models.Employee;
import models.MailOtpModel;
import service.EmployeeLoginService;
import service_interfaces.MailService;

@Controller
public class LoginController {

	private final Logger logger = LoggerFactory.getLogger(LoginController.class);

	private EmployeeLoginService empservice;
	private MailService mailService;
	private ForgotPasswordDAOImpl forgotPassword;

	@Autowired
	public LoginController(EmployeeLoginService empservice, Employee empauto, MailService mailService,
			ForgotPasswordDAOImpl forgotPassword) {
		this.empservice = empservice;
		this.mailService = mailService;
		this.forgotPassword = forgotPassword;

	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String employeeLogin(Model model) {

		logger.info("Login Page Requested");
		logger.warn("testing...");
		logger.error("heloo");
		return "login";
	}

	@RequestMapping(value = "/employee", method = RequestMethod.POST)
	public String enterIntoMenu_employee(@RequestParam("empl_email") String email,
			@RequestParam("empl_password") String password, HttpServletRequest request) {

		HttpSession session = request.getSession(true);
		System.out.println("this is employee side ");

		if (empservice.authenticateUser(email, password)) {

			// Set employee ID in session
			Employee empdetails = empservice.getByEmail(email);
			int employeeId = empdetails.getEmplId();
			session.setAttribute("employeeId", employeeId);
			return "index2"; // Redirect to the dashboard page
		} else {
			return "login";
		}
	}

	@RequestMapping(value = "/admin", method = RequestMethod.POST)
	public String enterIntoMenu_admin(@RequestParam("admin_email") String email,
			@RequestParam("admin_password") String password, HttpServletRequest request) {
		System.out.println("this is admin side ");

		HttpSession session = request.getSession(true);
		if (empservice.authenticateUser_admin(email, password)) {

			// Set employee ID in session
			Employee empdetails = empservice.getByEmail(email);
			int adminId = empdetails.getEmplId();
			System.out.println(adminId);

			session.setAttribute("adminId", adminId);

			return "Index_admin"; // Redirect to the dashboard page
		} else {
			return "login";
		}
	}

	@RequestMapping(value = "/checkingSession", method = RequestMethod.GET)
	public void getAllDetailsEmploye(HttpSession session) {

		if (session.getAttribute("adminId") == null)
			System.out.println("no admin login");
		else {
			System.out.println("admin login");
			int employeeid = (int) session.getAttribute("adminId");
			System.out.println(employeeid);
		}
		if (session.getAttribute("employeeId") == null)
			System.out.println("no employee login");
		else {
			System.out.println("employee login");
			int adminid = (int) session.getAttribute("employeeId");
			System.out.println(adminid);
		}

	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ResponseEntity<String> logout(HttpSession session) {
		session.invalidate();
		System.out.println("logout");

		return ResponseEntity.ok("success");
	}

	@RequestMapping(value = "/forgot", method = RequestMethod.GET)
	public String myControllerMethod() {
		// Controller logic
		return "forgot";
	}

	@RequestMapping(value = "/sendmail", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@ResponseBody
	public ResponseEntity<String> handleEmailAjaxRequest(Model model, MailOtpModel mail) {

		System.out.println(mail.getEmail().trim());
		System.out.println(mail);

		String email = mail.getEmail().trim();
		// Check whether the email exists
		boolean emailExists = forgotPassword.checkEmailExists(email);

		if (!emailExists) {
			// Email does not exist, return an error response
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email does not exist.");
		}

		// Email exists, continue with generating OTP and sending the email
		int otp = (int) (Math.random() * 9000) + 1000;

		System.out.println(otp);
		boolean flag = mailService.sendOtpMail(email, String.valueOf(otp));

		if (flag)
			return ResponseEntity.ok("Email Successfully sent!");

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");

	}

	@RequestMapping(value = "/otpvalidate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@ResponseBody
	public ResponseEntity<String> handleOTPAjaxRequest(Model mod, MailOtpModel mail) {
		System.out.println("In here");
		return ResponseEntity.ok("otp verification ");
	}

	@RequestMapping(value = "/changepassword", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@ResponseBody
	public ResponseEntity<String> changePasswordAjaxRequest(Model mod) {
		System.out.println("In here");

		return ResponseEntity.ok("Password change");
	}

}