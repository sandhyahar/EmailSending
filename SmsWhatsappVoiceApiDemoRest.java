package com.Calling.Rest;

import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Calling.Common.Common;
import com.Calling.Entity.CallingMstEntity;
import com.Calling.Entity.EmployeeMstEntity;
import com.Calling.Pojo.clientDetailsPojo;
import com.Calling.Repository.CallingMstRepo;
import com.Calling.Repository.EmployeeMstRepo;
import com.Calling.Service.EmailService;

@RestController
public class SmsWhatsappVoiceApiDemoRest {

	@Autowired
	EmailService emailService;

	@Autowired
	CallingMstRepo callingmstrepo;

	@Autowired
	EmployeeMstRepo employeemstrepo;

	@RequestMapping("/rest/smsWhatsappVoiceSendApiDemo")
	public String SmsWhatsappVoiceSendApi(@RequestBody String data) {
		JSONObject res = new JSONObject();
		try {

			if (data != null && !data.equalsIgnoreCase("")) {

				JSONObject json = new JSONObject(data);
				String userSrno = json.getString("userSrno");
				String mobileno = json.getString("mobileno");
				String demoType = json.getString("demoType");
				String emailId = json.getString("emailId");
				String customerName = json.getString("customerName");

				if (userSrno == null || userSrno.equalsIgnoreCase("")) {
					res.put("message", "userSrno must be required.");
					res.put("status", "false");
					return res.toString();
				}

				if (!userSrno.matches("\\d+")) {
					res.put("message", "Invalie userSrno");
					res.put("status", "false");
					return res.toString();
				}

				if (mobileno == null || mobileno.equalsIgnoreCase("")) {
					res.put("message", "clientMobileno must be required.");
					res.put("status", "false");
					return res.toString();
				}

				if (!Pattern.matches("\\+?\\d{10}", mobileno)) {
					res.put("message", "Invalid clientMobileno.");
					res.put("status", "false");
					return res.toString();
				}

				if (demoType == null || demoType.equalsIgnoreCase("")) {
					res.put("message", "demoType must be required.");
					res.put("status", "false");
					return res.toString();
				}

				if (demoType.equalsIgnoreCase("sms")) {
					smsApi(mobileno);
					res.put("message", "SMS Successfully Send in mobileno: " + mobileno);
					res.put("status", "true");
				} else if (demoType.equalsIgnoreCase("voice")) {
					voiceApi(mobileno);
					res.put("message", "Voice Successfully Send in mobileno: " + mobileno);
					res.put("status", "true");
				} else if (demoType.equalsIgnoreCase("whatsapp")) {
					whatsappApi(userSrno, mobileno, customerName);
					res.put("message", "Whatsapp Successfully Send in mobileno: " + mobileno);
					res.put("status", "true");
				} else if (demoType.equalsIgnoreCase("email")) {
				    emailService.sendEmail("This is Test Email", "hello this testing email", emailId);
					res.put("message", "Email Successfully Send in " + emailId);
					res.put("status", "true");
				} else if (demoType.equalsIgnoreCase("all")) {
					smsApi(mobileno);
					voiceApi(mobileno);
					whatsappApi(userSrno, mobileno, customerName);
					emailService.sendEmail("This is Test Email", "hello this testing email", emailId);
					res.put("message", "Successfully Send in mobileno: " + mobileno);
					res.put("status", "true");

				} else {
					res.put("message", "Invalid demoType.");
					res.put("status", "false");
					return res.toString();
				}

			}
		} catch (Exception e) {

			System.out.println(e);
		}

		return res.toString();
	}

	@RequestMapping("/rest/addClient")
	public String addClientApi(@RequestBody clientDetailsPojo clientDetails) {
		JSONObject json = new JSONObject();
		try {

			String mobileno = clientDetails.getClientMobileNo();

			String userSrno = clientDetails.getUserSrno();

			if (userSrno == null || userSrno.equalsIgnoreCase("")) {
				json.put("message", "userSrno must be required.");
				json.put("status", "false");
				return json.toString();
			}

			if (!userSrno.matches("\\d+")) {
				json.put("message", "Invalie userSrno");
				json.put("status", "false");
				return json.toString();
			}

			if (mobileno == null || mobileno.equalsIgnoreCase("")) {
				json.put("message", "clientMobileno must be required.");
				json.put("status", "false");
				return json.toString();
			}

			if (!Pattern.matches("\\+?\\d{10}", mobileno)) {
				json.put("message", "Invalid clientMobileno.");
				json.put("status", "false");
				return json.toString();
			}

			List<CallingMstEntity> clientExists = callingmstrepo.findByClientMobileNo(mobileno);
			if (clientExists.size() == 0) {
				CallingMstEntity cme = new CallingMstEntity();
				cme.setClientMobileNo(mobileno);
				cme.setClientName(clientDetails.getClientName());
				cme.setCompanyName(clientDetails.getCompanyName());
				int user = Integer.parseInt(userSrno);
				cme.setEmpSrno(user);

				EmployeeMstEntity eme = employeemstrepo.findBySrno(user);

				if (eme != null) {
					String empName = eme.getEmpName();
					String empMobileno = eme.getEmpMobileNo();

					callingmstrepo.save(cme);

					Common.SendWhatsappApi(mobileno, clientDetails.getClientName(), empName, empMobileno);

					json.put("message", "Client added successfully.");
					json.put("status", "true");
				} else {

					json.put("message", "Invalid UserSrno");
					json.put("status", "false");
				}
			} else {
				json.put("message", "Client is already in contacted.");
				json.put("status", "false");
			}

		} catch (Exception e) {
			System.out.println(e);
		}

		return json.toString();
	}

	public String voiceApi(String mobileno) {
		String result, url, param;
		url = "http://voice.dove-soft.com/ttsvoice.jsp";
		param = "user=jaimin&key=8a157ae4bdXX&voiceType=1&voiceText=Hello Sir,This is test voice demo using text to speech.&mobileNo="
				+ mobileno;

		result = Common.excutePostHTTP(url, param);

		return result;
	}

	public String smsApi(String mobileno) {
		String result, url, param;
		url = "http://mobicomm.dove-sms.com//submitsms.jsp";
		param = "user=jaimin27&key=9683be38b8XX&mobile=" + mobileno
				+ "&message=OTP : 123567 Regards, way2share Dove Soft Limited&senderid=wayshr&accusage=1";

		result = Common.excutePostHTTP(url, param);

		return result;

	}

	public String whatsappApi(String userSrno, String mobileno, String customerName) {
		int user = Integer.parseInt(userSrno);
		JSONObject json = new JSONObject();
		String result = "";

		EmployeeMstEntity eme = employeemstrepo.findBySrno(user);

		if (eme != null) {
			String empName = eme.getEmpName();
			String empMobileno = eme.getEmpMobileNo();
			result = Common.SendWhatsappApi(mobileno, customerName, empName, empMobileno);
			return result;

		} else {
			json.put("message", "Invalid UserSrno");
			json.put("status", "false");
		}

		return json.toString();
	}

}
