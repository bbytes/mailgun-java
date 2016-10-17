package com.bbytes.mailgun;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.bbytes.mailgun.api.MailOperations;
import com.bbytes.mailgun.api.impl.MailgunClientException;
import com.bbytes.mailgun.client.MailgunClient;
import com.bbytes.mailgun.model.MailMessage;
import com.bbytes.mailgun.model.MailgunSendResponse;
import com.bbytes.mailgun.util.MailBuilder;

public class MailOperationTest extends MailgunJavaClientApplicationTests {

	@Autowired
	Environment environment;

	MailgunClient client;

	String domain;

	@Before
	public void setup() {
		client = MailgunClient.create(environment.getProperty("mailgun.apiKey"));
		domain = environment.getProperty("mailgun.domain");
	}

	@Test
	public void testSendSimpleMail() {
		MailOperations mailOperations = client.mailOperations(domain);
		MailgunSendResponse response = mailOperations.sendTextMail(environment.getProperty("from.email"),
				"test mail from mailgun client", "Hello message", environment.getProperty("to.email"));
		Assert.assertTrue(response.isOk());
		System.out.println(response.getMessage());

	}

	@Test
	public void testSendMailWithAttachments() {
		MailOperations mailOperations = client.mailOperations("recruizmail.com");
		File attachment1 = new File("src/test/resources/testfiles/attachment.txt");
		File attachment2 = new File("src/test/resources/testfiles/sample.jpg");
		File attachment3 = new File("src/test/resources/testfiles/image.png");
		Assert.assertTrue(attachment1.exists());
		Assert.assertTrue(attachment2.exists());
		Assert.assertTrue(attachment3.exists());

		MailMessage message = MailBuilder.create().from(environment.getProperty("from.email"))
				.to(environment.getProperty("to.email")).subject("Attachment Added").html("Check attachments")
				.addAttachments(attachment1, attachment2, attachment3).build();

		MailgunSendResponse response = mailOperations.sendMail(message);

		Assert.assertTrue(response.isOk());
		System.out.println(response.getMessage());

	}

	@Test
	public void testSendMailWithInline() {
		MailOperations mailOperations = client.mailOperations("recruizmail.com");
		File image = new File("src/test/resources/testfiles/image.png");
		Assert.assertTrue(image.exists());
		MailMessage message = MailBuilder.create().from(environment.getProperty("from.email"))
				.to(environment.getProperty("to.email")).subject("Inline image mail")
				.html("<html>Inline image here: <img src=\"cid:image.png\"></html>").addInline(image).build();

		MailgunSendResponse response = mailOperations.sendMail(message);

		Assert.assertTrue(response.isOk());
		System.out.println(response.getMessage());

	}
}
