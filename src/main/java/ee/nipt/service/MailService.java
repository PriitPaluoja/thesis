package ee.nipt.service;

import ee.nipt.domain.Examination;
import ee.nipt.domain.Person;

import javax.mail.MessagingException;
import java.security.Principal;

public interface MailService {
    /**
     * Send account creation notification with his/her password to new user
     *
     * @param newPerson is the person who will receive the e-mail
     * @param password  is in plain text that will be used by new user to login
     * @param creator   is the person who created the account
     */
    void emailUserCreate(Person newPerson, String password, Person creator) throws MessagingException;

    /**
     * Notify corresponding user of creation of the examination report
     *
     * @param examination which report has been created
     * @param assessor    is the person who created report
     * @param doctor      is the person who will receive e-mail notification
     */
    void emailNotifyResults(Examination examination, Principal assessor, Person doctor) throws MessagingException;
}
