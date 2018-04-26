package ru.anc.work.authS2.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class that keeps user information / for SUDIR-2 integration
 *
 * @author chizhikova_aa
 * @since 2016
 */


@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement
public class Sudir2UserModel {

	/**
	 * User identifier
	 */
	private String guid;

	/**
	 * User firstname
	 */
	private String firstName;

	/**
	 * User lastname
	 */
	private String lastName;

	/**
	 * User middlename
	 */
	private String middleName;

	/**
	 * User e-mail
	 */
	private String mail;

	/**
	 * User phone
	 */
	private String phone;

	/**
	 * User password
	 */
	private String password;


	/**
	 * Get user identifier
	 * @return user identifier
	 */
	public String getGuid() {
		return guid;
	}

	/**
	 * Set user identifier
	 * @param guid user identifier
	 */
	@XmlElement
	public void setGuid(String guid) {
		this.guid = guid;
	}

	/**
	 * Get user firstname
	 * @return user firstname
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Set user firstname
	 * @param firstName user firstname
	 */
	@XmlElement
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Get user lastname
	 * @return user lastname
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Set user lastname
	 * @param lastName user lastname
	 */
	@XmlElement
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Get user middlename
	 * @return user middlename
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * Set user middlename
	 * @param middleName user middlename
	 */
	@XmlElement
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * Get user e-mail
	 * @return user e-mail
	 */
	public String getMail() {
		return mail;
	}

	/**
	 * Set user e-mail
	 * @param mail user e-mail
	 */
	@XmlElement
	public void setMail(String mail) {
		this.mail = mail;
	}

	/**
	 * Get user phone
	 * @return телефон
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * Set user phone
	 * @param phone телефон
	 */
	@XmlElement
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * Get user password
	 * @return пароль
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Set user password
	 * @param password пароль
	 */
	@XmlElement
	public void setPassword(String password) {
		this.password = password;
	}
	

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Sudir2UserModel{");
		sb.append("guid='").append(guid).append('\'');
		sb.append(", firstName='").append(firstName).append('\'');
		sb.append(", lastName='").append(lastName).append('\'');
		sb.append(", middleName='").append(middleName).append('\'');
		sb.append(", mail='").append(mail).append('\'');
		sb.append(", phone='").append(phone).append('\'');
		sb.append(", password='").append(password).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
