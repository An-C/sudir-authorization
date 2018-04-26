package ru.anc.work.authS2.dao;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.anc.work.authS2.model.Sudir2UserModel;
import ru.anc.work.authentication.utils.PortalPasswordEncoder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.soap.SOAPException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

/**
 * Class for Sudir2UserDAO testing
 */
@ContextConfiguration(locations = {"/test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class Sudir2UserTest {


	/**
	 * DAO to deal with user
	 */
	@Autowired
	private Sudir2UserDAO sudir2UserDAO;

	/**
	 * Test user creation in userinfo
	 */
	@Test
	@Ignore
	public void testCreateUserInfo(){

		//Create user model as it received from SUDIR-2
		Sudir2UserModel userModel = new Sudir2UserModel();
		userModel.setGuid("testCreateUserInfo2");
		userModel.setLastName("Create");
		userModel.setFirstName("User");
		userModel.setMiddleName("Info");
		userModel.setPhone("12345678");
		userModel.setMail("12345678@mail.ru");
		PortalPasswordEncoder encoder = new PortalPasswordEncoder("MD5");
		userModel.setPassword(encoder.encode("testCreateUserInfo"+PortalPasswordEncoder.MD5_SALT+"123456"));

		//Save
		try {
			Assert.assertNotNull(sudir2UserDAO.createUserInfo(userModel));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Test user updating in userinfo
	 */
	@Test
	@Ignore
	public void testUpdateUserInfo(){

		//Create user model as it received from SUDIR-2
		Sudir2UserModel userModel = new Sudir2UserModel();
		userModel.setGuid("testCreateUserInfo2");
		userModel.setLastName("TestUpdate");
		userModel.setFirstName("User");
		userModel.setMiddleName("MIDDLENAME");
		userModel.setPhone("12345678");
		userModel.setMail("12345678@mail.ru");
		userModel.setPassword("123456");

		//Update
		try {
			sudir2UserDAO.updateUserInfo(userModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Test user json transformation (frm ESIA) to Sudir2UserModel
	 */
	@Test
	@Ignore
	public void testConvertUserJSONtoSudir2UserModel(){

		String userJson = " {\n" +
				"\"guid\":\"9b3d0147-66f3-4ae5-bf97-9ecce4d3caa4\",\n" +
				"\"firstName\":\"User One\",\n" +
				"\"lastName\":\"Tset\",\n" +
				"\"middleName\":\"First\",\n" +
				"\"phone\":\"9161111111\",\n" +
				"\"mail\":\"first@test.local\",\n" +
				"\"legalPersonVO\":{\n" +
				"\"corpId\":\"d39e5bee-98fe-49ab-9861-c168e6116f62\",\n" +
				"\"certificateID\":\"707372110000000138D0\",\n" +
				"\"corpInn\":\"7702222222\",\n" +
				"\"corpOgrn\":\"1137702222228\"\n" +
				" }}";

		//Save
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Sudir2UserModel userModel = objectMapper.readValue(userJson, Sudir2UserModel.class);
			//System.out.println(printObjectToXML(userModel));

			JAXBContext jaxbContext = JAXBContext.newInstance(Sudir2UserModel.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			//output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			//jaxbMarshaller.marshal(userModel, System.out);
			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(userModel, sw);
			String xmlString = sw.toString();
			System.out.println(xmlString);

			Assert.assertNotNull(userModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test url encoding
	 * @throws UnsupportedEncodingException exception
	 */
	@Test
	@Ignore
	public void encodeUrl() throws UnsupportedEncodingException {
		String url = java.net.URLEncoder.encode("https://expertiza-test.mos.ru/portal/submit/simpleSubmit.action?action=DL_GET_CARD_LINK_LOADER&guid=bad7153a811b46dcb1de8548eec3192c&objectType=P", "UTF-8");
		System.out.println(url);
	}
}
