package com.techpedia.usermanagement.helper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;







import javax.sql.DataSource;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;


import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;






import com.techpedia.chiper.ChiperEncryptException;
import com.techpedia.chiper.ChiperUtils;
import com.techpedia.logger.TechPediaLogger;

import sun.misc.BASE64Decoder;

import com.techpedia.usermanagement.dao.UserManagementDAOImpl;
import com.techpedia.usermanagement.dataobject.CityListDO;
import com.techpedia.usermanagement.dataobject.CollegeListDO;
import com.techpedia.usermanagement.dataobject.Mentor1n2Details;
import com.techpedia.usermanagement.dataobject.MentorsOfProject;
import com.techpedia.usermanagement.dataobject.PasswordResetVo;
import com.techpedia.usermanagement.dataobject.PopularMentorsDO;
import com.techpedia.usermanagement.dataobject.RecentNewsDO;
import com.techpedia.usermanagement.dataobject.SaveUserPhoto;
import com.techpedia.usermanagement.dataobject.SearchCityDO;
import com.techpedia.usermanagement.dataobject.SearchCollegeDO;
import com.techpedia.usermanagement.dataobject.SearchCriteriaDO;
import com.techpedia.usermanagement.dataobject.SearchForMentorListDO;
import com.techpedia.usermanagement.dataobject.SignInVo;
import com.techpedia.usermanagement.dataobject.StateListDO;
import com.techpedia.usermanagement.dataobject.UniversityListDO;
import com.techpedia.usermanagement.dataobject.UpdateUserPhotoDO;
import com.techpedia.usermanagement.dataobject.UserProfileDO;
import com.techpedia.usermanagement.dataobject.UserRecentComments;
import com.techpedia.usermanagement.dataobject.UserTeamListDO;
import com.techpedia.usermanagement.dataobject.UsrAccessDetails;
import com.techpedia.usermanagement.entity.BranchMaster;
import com.techpedia.usermanagement.entity.MentorDetails;
import com.techpedia.usermanagement.entity.MentorsAssignedToProject;
import com.techpedia.usermanagement.entity.ProjectTeamMaster;
import com.techpedia.usermanagement.entity.ProjectTeamTxn;
import com.techpedia.usermanagement.entity.RecentNewsTxn;
import com.techpedia.usermanagement.entity.UsrAssignAuthortnRolesToUsrs;
import com.techpedia.usermanagement.entity.UsrAuthortnRoles;
import com.techpedia.usermanagement.entity.UsrMngtAddress;
import com.techpedia.usermanagement.entity.UsrMngtCollege;
import com.techpedia.usermanagement.entity.UsrMngtContactInfo;
import com.techpedia.usermanagement.entity.UsrMngtFaculty;
import com.techpedia.usermanagement.entity.UsrMngtIndustry;
import com.techpedia.usermanagement.entity.UsrMngtMaster;
import com.techpedia.usermanagement.entity.UsrMngtMentor;
import com.techpedia.usermanagement.entity.UsrMngtPassword;
import com.techpedia.usermanagement.entity.UsrMngtStudent;
import com.techpedia.usermanagement.exception.CityFetchException;
import com.techpedia.usermanagement.exception.CollegesFetchException;
import com.techpedia.usermanagement.exception.CreateProfileException;
import com.techpedia.usermanagement.exception.CurrentPasswordFetchException;
import com.techpedia.usermanagement.exception.EmailExistException;
import com.techpedia.usermanagement.exception.GetRecentNewsException;
import com.techpedia.usermanagement.exception.LoginException;
import com.techpedia.usermanagement.exception.MentorSearchException;
import com.techpedia.usermanagement.exception.PasswordExpiryException;
import com.techpedia.usermanagement.exception.PasswordMismatchException;
import com.techpedia.usermanagement.exception.PasswordResetException;
import com.techpedia.usermanagement.exception.PopularMentorsFetchException;
import com.techpedia.usermanagement.exception.ProfileFetchException;
import com.techpedia.usermanagement.exception.ProfileNotFoundException;
import com.techpedia.usermanagement.exception.ProfileSearchException;
import com.techpedia.usermanagement.exception.ProfileUpdateException;
import com.techpedia.usermanagement.exception.ProjectNotFoundException;
import com.techpedia.usermanagement.exception.SaveUserPhotoException;
import com.techpedia.usermanagement.exception.StateFetchException;
import com.techpedia.usermanagement.exception.UniversitiesFetchException;
import com.techpedia.usermanagement.exception.UserExistException;
import com.techpedia.usermanagement.exception.UserFunctionsNotDefinedException;
import com.techpedia.usermanagement.exception.UserInactiveException;
import com.techpedia.usermanagement.exception.UserNotFoundException;
import com.techpedia.usermanagement.exception.UserRecentCommentsFetchException;
import com.techpedia.usermanagement.exception.UserRoleNotDefinedException;
import com.techpedia.usermanagement.exception.UserRoleNotMappedWithFunctionIdsException;
import com.techpedia.usermanagement.exception.UserTeamListFetchException;
import com.techpedia.usermanagement.util.FileUploadDownload;
import com.techpedia.usermanagement.util.PasswordUtil;
import com.techpedia.util.HibernateUtil;

//import com.techpedia.usermanagement.util.Image2Base64;

@SuppressWarnings("restriction")
@Component
public class UserManagementDAOHelper {

	@Autowired
	private DataSource dataSource;
	
	private static final Logger log = LoggerFactory.getLogger(UserManagementDAOHelper.class.getName());
	
	
	public static String UploadUserPhoto(SaveUserPhoto SaveUserPhoto)
			throws SaveUserPhotoException {
		log.info("UserManagementDAOImpl UploadUserPhoto :START");
		String returnVal = "N";
		String fileSize = "";
		ResourceBundle rbundle = ResourceBundle.getBundle("uploadImage");
		String SERVER_UPLOAD_PROFILEIMAGE_FOLDER_LOCATION = rbundle.getString("SERVER_UPLOAD_PROFILEIMAGE_FOLDER_LOCATION");		
		String regstrId = String.valueOf(SaveUserPhoto.getRegisterId());
		String docName = SaveUserPhoto.getImgName();	
		String docPath = SERVER_UPLOAD_PROFILEIMAGE_FOLDER_LOCATION+"/"+regstrId+"/"+docName;
		Calendar now = Calendar.getInstance(); 
	    Date imgUploadDate = now.getTime();
	    Transaction tx = null;
		Session session = null;
		try {			
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] decodedBytes = decoder.decodeBuffer(SaveUserPhoto.getImgByteArray());
			InputStream inputStream = new ByteArrayInputStream(decodedBytes);			
			fileSize = FileUploadDownload.saveFile(inputStream, SERVER_UPLOAD_PROFILEIMAGE_FOLDER_LOCATION,regstrId, docName);
			
			/*Start Adding into TB_TECH001_MAST_PROJECTS_DETAIL here*/	
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query query = session
					.createSQLQuery("update techpedia.usr_mngt_contact_info set PHOTO_PATH = :photoPath where RGSTR_ID = :rgstrId");
			query.setParameter("photoPath", docPath);
			query.setParameter("rgstrId", SaveUserPhoto.getRegisterId());
			query.executeUpdate();
			//Criteria criteria = session.createCriteria(UsrMngtContactInfo.class);
			//criteria.add(Restrictions.eq("RGSTR_ID",SaveUserPhoto.getRegisterId() ));
	        //criteria.add(Restrictions.eq("PHOTO_PATH", docPath));	        
	        //UsrMngtContactInfo imgPathTxn = (UsrMngtContactInfo) criteria.uniqueResult();
	        //if(imgPathTxn == null){				
	        	//UsrMngtContactInfo userImgPathTxn = new UsrMngtContactInfo(SaveUserPhoto.getRegisterId(),docPath);		
				//session.save(userImgPathTxn);						
	       // }else{
	        	//imgPathTxn.setProjDocSize(fileSize);
	        	//session.update(docPathTxn);	        	
	        	//throw new SaveUserPhotoException("Error while uploading document :"+ e.getMessage());
	       // }
	        tx.commit();
			returnVal = "Y";
		} catch (Exception e) {		
			throw new SaveUserPhotoException("UM-EXC03",
					"SaveUserPhotoException",
					e.getMessage());
		}finally{
			if(tx!=null)
				tx=null;
			if(session!=null)
				session.close();
		}	
		log.info("UserManagementDAOImpl UploadUserPhoto :END");
		return returnVal;
	}

	public static boolean createProfileHelperNew(UserProfileDO userprofile)
			throws ChiperEncryptException, CreateProfileException,
			UserExistException, EmailExistException {
		log.info("UserManagementDAOImpl createProfile :START");
		boolean retval = false;
		//String isActive = "N";
		String cVerification = "1";
		Date activated_date = new Date();
		String createdDate = getCurrentTimeStamp();
		String encryPassword = ChiperUtils.encrypt2(userprofile.getPassword());
		Session ss = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		UsrMngtMaster uMaster = null;
		try {

			tx = ss.beginTransaction();
			Query query = ss
					.createSQLQuery("select RGSTR_ID from usr_mngt_master where USR_ID = :userid");
			query.setParameter("userid", userprofile.getUserName());
			Query emailquery = ss
					.createSQLQuery("select RGSTR_ID from usr_mngt_master where EMAIL_ID = :emailId");
			emailquery.setParameter("emailId", userprofile.getEmail());
			if (query.uniqueResult() != null) {
				throw new UserExistException("UM-EX011",
						"UserAlreadyExistedException",
						" User Is already Existed");
			} else if (emailquery.uniqueResult() != null) {
				throw new EmailExistException("UM-EX014",
						"EmailAlreadyExistedException",
						" User Is already Existing with the given email Id");
			} else {

				uMaster = new UsrMngtMaster(userprofile.getFirstName(),
						userprofile.getMidName(), userprofile.getLastName(),
						userprofile.getDob(), userprofile.getiSactive(), cVerification,
						userprofile.getUserType(), userprofile.getUserName(),
						userprofile.getFaceBookId(), createdDate,
						activated_date, userprofile.getEmail(),userprofile.getTwitterId(),
						userprofile.getLinkedinId());

				ss.save(uMaster);
				userprofile.setRgstrId(uMaster.getRgstrId());
				UsrMngtAddress uAddress = new UsrMngtAddress(
						uMaster.getRgstrId(), userprofile.getAddrLn1(),
						userprofile.getAddrLn2(), userprofile.getCity(),
						userprofile.getDistrict(), userprofile.getState(),
						userprofile.getCountry(), userprofile.getPincode());
				// System.out.println("Photo Insert " + userprofile.getPhoto());
				UsrMngtContactInfo uContactInfo = new UsrMngtContactInfo(
						uMaster.getRgstrId(), userprofile.getMobile(),
						userprofile.getHomePhoneNo(), userprofile.getPhoto(),userprofile.getPhotopath());

				UsrMngtPassword uPassword = new UsrMngtPassword(
						uMaster.getRgstrId(), userprofile.getUserName(),
						encryPassword, createdDate);

				ss.save(uAddress);
				ss.save(uContactInfo);
				ss.save(uPassword);

				if (userprofile.getUserType().equalsIgnoreCase("Student")) {

					UsrMngtStudent uStudent = new UsrMngtStudent(
							uMaster.getRgstrId(),
							userprofile.getDegreeOfStudent(),
							userprofile.getCollge(),
							userprofile.getUniversity(),
							// userprofile.getAlumni(),
							userprofile.getStudentID(),
							userprofile.getCompletionYear(),
							userprofile.getBranchIdOfStudent());
					ss.save(uStudent);

				} else if (userprofile.getUserType()
						.equalsIgnoreCase("college")) {
					// System.out.println("LOGO " + userprofile.getLogo());
					UsrMngtCollege uCollege = new UsrMngtCollege(
							uMaster.getRgstrId(), userprofile.getWebpage(),
							userprofile.getLogo(),
							userprofile.getCollegeName(),
							userprofile.getCollegeDesc(),
							userprofile.getPrinicipalName(),
							userprofile.getPrinicipalEmail(),
							userprofile.getFacilitiesOffrdToStudents(),
							userprofile.getCntctInfoForNatnlInnovnClub(),
							userprofile.getAffltUniversityOfCollege(),
							userprofile.getTechpdaFactlyCoordtr());

					ss.save(uCollege);

					// UserMngtCollegeMaster userMngtCollegeMaster=new
					// UserMngtCollegeMaster(userprofile.getCollegeName(),userprofile.getCollegeDesc());
					// ss.save(userMngtCollegeMaster);

				} else if (userprofile.getUserType()
						.equalsIgnoreCase("faculty")) {

					UsrMngtFaculty uFaculty = new UsrMngtFaculty(
							uMaster.getRgstrId(),
							userprofile.getDegreeOfFaculty(),
							userprofile.getCollgeOfFaculty(),
							userprofile.getSpecializationOfFaculty(),
							userprofile.getUniversityOfFaculty(),
							userprofile.getAlumni(),
							userprofile.getMemshipInAssocns(),
							userprofile.getPsnlWebpgLink(),
							userprofile.getProffesionalExpOfFaculty(),
							userprofile.getAffltUniversityOfFaculty(),
							userprofile.getBranchIdOfFaculty());
					ss.save(uFaculty);

				} else if (userprofile.getUserType().equalsIgnoreCase("mentor")) {

					UsrMngtMentor uMentor = new UsrMngtMentor(
							uMaster.getRgstrId(),
							userprofile.getDegreeOfMentor(),
							userprofile.getDesignationOfMentor(),
							userprofile.getBranchIdOfMentor(),
							userprofile.getInstitutionalAssctnInfo(),
							userprofile.getProfessionalExperience(),
							userprofile.getTimeUspaceForMentoringPerMnth(),
							userprofile.getMentorProfile(),
							userprofile.getExpectationFromMentor(),
							userprofile.getCommitmentUBringIn(),
							userprofile.getWebpage(),
							userprofile.getIntOnGrassrtInnovators(),
							userprofile.getPopularity());
					ss.save(uMentor);

				} else if (userprofile.getUserType().equalsIgnoreCase(
						"industry")) {

					UsrMngtIndustry uIndustry = new UsrMngtIndustry(
							uMaster.getRgstrId(), userprofile.getFax(),
							userprofile.getContactNameOfIndustry(),
							userprofile.getContactEmailOfIndustry(),
							userprofile.getOperatnSectr(),
							userprofile.getKindOfSprtUProvideInnovtr(),
							userprofile.getPrdRng(),
							userprofile.getAssociateIndustry(),
							userprofile.getTechngyExprtizOffrToOthers(),
							userprofile.getSolnReqrdForTechnlgicalChlngs(),
							userprofile.getIntrstToPoseInnovtnChlngAwrds());
					ss.save(uIndustry);

				}
				tx.commit();
				// UserManagementEmailHelper.sendEmailFromDAO(userprofile);
				// //Sending mail
				retval = true;
			}

		} catch (UserExistException e) {
			throw e;

		} catch (EmailExistException e) {
			throw e;

		} catch (Exception e) {
			e.printStackTrace();
			log.error((new StringBuilder(
					"Error while commiting the transaction :")).append(
					e.getMessage()).toString());
			try {
				tx.rollback();
				// deleting the record which is inserted in the usrmngtmaster
				// table.
				ss.createQuery(
						"delete from UsrMngtMaster where RGSTR_ID = :rgstrid")
						.setParameter("rgstrid", uMaster.getRgstrId())
						.executeUpdate();

			} catch (Exception ee) {
				log.error((new StringBuilder(
						"Error while doing rollback to the failed transaction :"))
						.append(e.getMessage()).toString());
			}
			throw new CreateProfileException("UM-EX001",
					"CreateProfileException", e.getMessage());
		}

		finally {
			tx = null;
			ss.close();
		}
		log.info("UserManagementDAOImpl createProfile :END");
		return retval;
	}
	
	public static boolean createProfileHelper(UserProfileDO userprofile)
			throws ChiperEncryptException, CreateProfileException,
			UserExistException, EmailExistException {
		log.info("UserManagementDAOImpl createProfile :START");
		boolean retval = false;
		//String isActive = "N";
		String cVerification = "1";
		Date activated_date = new Date();
		String createdDate = getCurrentTimeStamp();
		String encryPassword = ChiperUtils.encrypt2(userprofile.getPassword());
		Session ss = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		UsrMngtMaster uMaster = null;
		try {

			tx = ss.beginTransaction();
			Query query = ss
					.createSQLQuery("select RGSTR_ID from usr_mngt_master where USR_ID = :userid");
			query.setParameter("userid", userprofile.getUserName());
			Query emailquery = ss
					.createSQLQuery("select RGSTR_ID from usr_mngt_master where EMAIL_ID = :emailId");
			emailquery.setParameter("emailId", userprofile.getEmail());
			if (query.uniqueResult() != null) {
				throw new UserExistException("UM-EX011",
						"UserAlreadyExistedException",
						" User Is already Existed");
			} else if (emailquery.uniqueResult() != null) {
				throw new EmailExistException("UM-EX014",
						"EmailAlreadyExistedException",
						" User Is already Existing with the given email Id");
			} else {

				uMaster = new UsrMngtMaster(userprofile.getFirstName(),
						userprofile.getMidName(), userprofile.getLastName(),
						userprofile.getDob(), userprofile.getiSactive(), cVerification,
						userprofile.getUserType(), userprofile.getUserName(),
						userprofile.getFaceBookId(), createdDate,
						activated_date, userprofile.getEmail(),userprofile.getTwitterId(),
						userprofile.getLinkedinId());

				ss.save(uMaster);

				UsrMngtAddress uAddress = new UsrMngtAddress(
						uMaster.getRgstrId(), userprofile.getAddrLn1(),
						userprofile.getAddrLn2(), userprofile.getCity(),
						userprofile.getDistrict(), userprofile.getState(),
						userprofile.getCountry(), userprofile.getPincode());
				// System.out.println("Photo Insert " + userprofile.getPhoto());
				UsrMngtContactInfo uContactInfo = new UsrMngtContactInfo(
						uMaster.getRgstrId(), userprofile.getMobile(),
						userprofile.getHomePhoneNo(), userprofile.getPhoto(),userprofile.getPhotopath());

				UsrMngtPassword uPassword = new UsrMngtPassword(
						uMaster.getRgstrId(), userprofile.getUserName(),
						encryPassword, createdDate);

				ss.save(uAddress);
				ss.save(uContactInfo);
				ss.save(uPassword);

				if (userprofile.getUserType().equalsIgnoreCase("Student")) {

					UsrMngtStudent uStudent = new UsrMngtStudent(
							uMaster.getRgstrId(),
							userprofile.getDegreeOfStudent(),
							userprofile.getCollge(),
							userprofile.getUniversity(),
							// userprofile.getAlumni(),
							userprofile.getStudentID(),
							userprofile.getCompletionYear(),
							userprofile.getBranchIdOfStudent());
					ss.save(uStudent);

				} else if (userprofile.getUserType()
						.equalsIgnoreCase("college")) {
					// System.out.println("LOGO " + userprofile.getLogo());
					UsrMngtCollege uCollege = new UsrMngtCollege(
							uMaster.getRgstrId(), userprofile.getWebpage(),
							userprofile.getLogo(),
							userprofile.getCollegeName(),
							userprofile.getCollegeDesc(),
							userprofile.getPrinicipalName(),
							userprofile.getPrinicipalEmail(),
							userprofile.getFacilitiesOffrdToStudents(),
							userprofile.getCntctInfoForNatnlInnovnClub(),
							userprofile.getAffltUniversityOfCollege(),
							userprofile.getTechpdaFactlyCoordtr());

					ss.save(uCollege);

					// UserMngtCollegeMaster userMngtCollegeMaster=new
					// UserMngtCollegeMaster(userprofile.getCollegeName(),userprofile.getCollegeDesc());
					// ss.save(userMngtCollegeMaster);

				} else if (userprofile.getUserType()
						.equalsIgnoreCase("faculty")) {

					UsrMngtFaculty uFaculty = new UsrMngtFaculty(
							uMaster.getRgstrId(),
							userprofile.getDegreeOfFaculty(),
							userprofile.getCollgeOfFaculty(),
							userprofile.getSpecializationOfFaculty(),
							userprofile.getUniversityOfFaculty(),
							userprofile.getAlumni(),
							userprofile.getMemshipInAssocns(),
							userprofile.getPsnlWebpgLink(),
							userprofile.getProffesionalExpOfFaculty(),
							userprofile.getAffltUniversityOfFaculty(),
							userprofile.getBranchIdOfFaculty());
					ss.save(uFaculty);

				} else if (userprofile.getUserType().equalsIgnoreCase("mentor")) {

					UsrMngtMentor uMentor = new UsrMngtMentor(
							uMaster.getRgstrId(),
							userprofile.getDegreeOfMentor(),
							userprofile.getDesignationOfMentor(),
							userprofile.getBranchIdOfMentor(),
							userprofile.getInstitutionalAssctnInfo(),
							userprofile.getProfessionalExperience(),
							userprofile.getTimeUspaceForMentoringPerMnth(),
							userprofile.getMentorProfile(),
							userprofile.getExpectationFromMentor(),
							userprofile.getCommitmentUBringIn(),
							userprofile.getWebpage(),
							userprofile.getIntOnGrassrtInnovators(),
							userprofile.getPopularity());
					ss.save(uMentor);

				} else if (userprofile.getUserType().equalsIgnoreCase(
						"industry")) {

					UsrMngtIndustry uIndustry = new UsrMngtIndustry(
							uMaster.getRgstrId(), userprofile.getFax(),
							userprofile.getContactNameOfIndustry(),
							userprofile.getContactEmailOfIndustry(),
							userprofile.getOperatnSectr(),
							userprofile.getKindOfSprtUProvideInnovtr(),
							userprofile.getPrdRng(),
							userprofile.getAssociateIndustry(),
							userprofile.getTechngyExprtizOffrToOthers(),
							userprofile.getSolnReqrdForTechnlgicalChlngs(),
							userprofile.getIntrstToPoseInnovtnChlngAwrds());
					ss.save(uIndustry);

				}
				tx.commit();
				// UserManagementEmailHelper.sendEmailFromDAO(userprofile);
				// //Sending mail
				retval = true;
			}

		} catch (UserExistException e) {
			throw e;

		} catch (EmailExistException e) {
			throw e;

		} catch (Exception e) {
			e.printStackTrace();
			log.error((new StringBuilder(
					"Error while commiting the transaction :")).append(
					e.getMessage()).toString());
			try {
				tx.rollback();
				// deleting the record which is inserted in the usrmngtmaster
				// table.
				ss.createQuery(
						"delete from UsrMngtMaster where RGSTR_ID = :rgstrid")
						.setParameter("rgstrid", uMaster.getRgstrId())
						.executeUpdate();

			} catch (Exception ee) {
				log.error((new StringBuilder(
						"Error while doing rollback to the failed transaction :"))
						.append(e.getMessage()).toString());
			}
			throw new CreateProfileException("UM-EX001",
					"CreateProfileException", e.getMessage());
		}

		finally {
			tx = null;
			ss.close();
		}
		log.info("UserManagementDAOImpl createProfile :END");
		return retval;
	}

	public static boolean updateProfileHelper(UserProfileDO userprofile)
			throws ProfileNotFoundException, ProfileUpdateException,
			ChiperEncryptException {
		log.info("UserManagementDAOImpl updateProfile :START");
		boolean retval = false;
		String isActive = "Y";
		String cVerification = "1";
		Date activated_date = new Date();
		String createdDate = getCurrentTimeStamp();
		// String encryPassword =
		// ChiperUtils.encrypt2(userprofile.getPassword());
		Session ss = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		UsrMngtMaster uMaster = null;
		try {
			tx = ss.beginTransaction();

			uMaster = (UsrMngtMaster) ss.get(UsrMngtMaster.class,
					userprofile.getRgstrId());
			if (uMaster == null) {
				log.error("Record NOt found Exception for the Given Register Id "
						+ userprofile.getRgstrId());
				throw new ProfileNotFoundException("UM-EX002",
						"ProfileNotFoundException",
						"Profile Not found for the given Register Id");
			} else {
				uMaster.setpFname(userprofile.getFirstName());
				uMaster.setmName(userprofile.getMidName());
				uMaster.setlName(userprofile.getLastName());
				uMaster.setdOb(userprofile.getDob());
				uMaster.setiSactive(isActive);
				uMaster.setcVerify(cVerification);
				uMaster.setType(userprofile.getUserType());
				uMaster.setUserId(userprofile.getUserName());
				uMaster.setFbId(userprofile.getFaceBookId());
				uMaster.setRgstrDate(createdDate);
				uMaster.setActivatedDate(activated_date);
				uMaster.setEmail(userprofile.getEmail());
				uMaster.setTwitterId(userprofile.getTwitterId());
				uMaster.setLinkedinId(userprofile.getLinkedinId());

				UsrMngtAddress uAddress = new UsrMngtAddress(
						userprofile.getRgstrId(), userprofile.getAddrLn1(),
						userprofile.getAddrLn2(), userprofile.getCity(),
						userprofile.getDistrict(), userprofile.getState(),
						userprofile.getCountry(), userprofile.getPincode());

				// UsrMngtContactInfo uContactInfo = new
				// UsrMngtContactInfo(userprofile.getRgstrId(),
				// userprofile.getMobile(), userprofile.getHomePhoneNo(),
				// userprofile.getPhoto());
				// UsrMngtPassword uPassword = new
				// UsrMngtPassword(userprofile.getRgstrId(),
				// userprofile.getUserName(), encryPassword, createdDate);
				ss.update("0", uMaster);
				ss.update("1", uAddress);
				// ss.update("2", uContactInfo);
				// ss.update("3", uPassword);

				Query query = ss
						.createSQLQuery("update techpedia.usr_mngt_contact_info set MOBILE_NO = :mobile,HOME_PHONE_NO = :homeNumber where RGSTR_ID = :rgstrId");
				query.setParameter("homeNumber", userprofile.getHomePhoneNo());
				query.setParameter("mobile", userprofile.getMobile());
				query.setParameter("rgstrId", userprofile.getRgstrId());
				query.executeUpdate();

				if (userprofile.getUserType().equalsIgnoreCase("Student")) {

					UsrMngtStudent uStudent = new UsrMngtStudent(
							uMaster.getRgstrId(),
							userprofile.getDegreeOfStudent(), // Modified by
																// Anil
							userprofile.getCollge(),
							userprofile.getUniversity(),// added by Anil
							// userprofile.getAlumni(),
							userprofile.getStudentID(),
							userprofile.getCompletionYear(),
							userprofile.getBranchIdOfStudent());
					ss.update("4", uStudent);

				} else if (userprofile.getUserType()
						.equalsIgnoreCase("college")) {
					UsrMngtCollege uCollege = new UsrMngtCollege(
							userprofile.getRgstrId(), userprofile.getWebpage(),
							userprofile.getLogo(),
							userprofile.getCollegeName(),
							userprofile.getCollegeDesc(),
							userprofile.getPrinicipalName(),
							userprofile.getPrinicipalEmail(),
							userprofile.getFacilitiesOffrdToStudents(),
							userprofile.getCntctInfoForNatnlInnovnClub(),
							userprofile.getAffltUniversityOfCollege(),
							userprofile.getTechpdaFactlyCoordtr());
					ss.update("5", uCollege);

				} else if (userprofile.getUserType()
						.equalsIgnoreCase("faculty")) {

					UsrMngtFaculty uFaculty = new UsrMngtFaculty(
							userprofile.getRgstrId(),
							userprofile.getDegreeOfFaculty(),
							userprofile.getCollgeOfFaculty(),
							userprofile.getSpecializationOfFaculty(),
							userprofile.getUniversityOfFaculty(),
							userprofile.getAlumni(),
							userprofile.getMemshipInAssocns(),
							userprofile.getPsnlWebpgLink(),
							userprofile.getProffesionalExpOfFaculty(),
							userprofile.getAffltUniversityOfFaculty(),
							userprofile.getBranchIdOfFaculty());
					ss.update("6", uFaculty);

				} else if (userprofile.getUserType().equalsIgnoreCase("mentor")) {
					UsrMngtMentor uMentor = new UsrMngtMentor(
							userprofile.getRgstrId(),
							userprofile.getDegreeOfMentor(),
							userprofile.getDesignationOfMentor(),
							userprofile.getBranchIdOfMentor(),
							userprofile.getInstitutionalAssctnInfo(),
							userprofile.getProfessionalExperience(),
							userprofile.getTimeUspaceForMentoringPerMnth(),
							userprofile.getMentorProfile(),
							userprofile.getExpectationFromMentor(),
							userprofile.getCommitmentUBringIn(),
							userprofile.getWebpage(),
							userprofile.getIntOnGrassrtInnovators(),
							userprofile.getPopularity());
					System.out.println("Branch id="
							+ userprofile.getBranchIdOfMentor());
					ss.update("7", uMentor);

				} else if (userprofile.getUserType().equalsIgnoreCase(
						"industry")) {

					UsrMngtIndustry uIndustry = new UsrMngtIndustry(
							userprofile.getRgstrId(), userprofile.getFax(),
							userprofile.getContactNameOfIndustry(),
							userprofile.getContactEmailOfIndustry(),
							userprofile.getOperatnSectr(),
							userprofile.getKindOfSprtUProvideInnovtr(),
							userprofile.getPrdRng(),
							userprofile.getAssociateIndustry(),
							userprofile.getTechngyExprtizOffrToOthers(),
							userprofile.getSolnReqrdForTechnlgicalChlngs(),
							userprofile.getIntrstToPoseInnovtnChlngAwrds());
					ss.update("8", uIndustry);

				}
				tx.commit();
				retval = true;
			}

		} catch (ProfileNotFoundException e) {
			throw e;
		} catch (Exception e) {

			log.error((new StringBuilder(
					"Error while updating the transaction :")).append(
					e.getMessage()).toString());
			try {
				tx.rollback();
			} catch (Exception ee) {
				log.error((new StringBuilder(
						"Error while doing rollback to the failed transaction :"))
						.append(e.getMessage()).toString());
			}
			throw new ProfileUpdateException("UM-EX003",
					"ProfileUpdateException", e.getMessage());

		}

		finally {
			tx = null;
			ss.close();
		}
		log.info("UserManagementDAOImpl updateProfile :END");
		return retval;
	}

	public static boolean deactivateProfileHelper(String userId)
			throws ProfileNotFoundException, ProfileUpdateException {
		Session ss = HibernateUtil.getSessionFactory().openSession();
		int res = 0;
		try {
			res = ss.createQuery(
					"update UsrMngtMaster set IS_ACTIVE ='N' where USR_ID = :userid")
					.setParameter("userid", userId).executeUpdate();
			if (res == 0) {
				log.error("Record NOt found Exception for the User Id "
						+ userId);
				throw new ProfileNotFoundException("UM-EX002",
						"ProfileNotFoundException",
						"Profile Not found for the given User Id");
			}
		} catch (Exception e) {
			if (res == 0) {
				throw new ProfileNotFoundException("UM-EX002",
						"ProfileNotFoundException",
						"Profile Not found for the given User Id");
			} else {
				log.error((new StringBuilder(
						"Error while Deactivating the profile :")).append(
						e.getMessage()).toString());
				throw new ProfileUpdateException("UM-EX003",
						"ProfileUpdateException", e.getMessage());
			}
		} finally {
			ss.close();
		}
		if (res == 1) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean activateProfileHelper(String userId)
			throws ProfileNotFoundException, ProfileUpdateException {
		Session ss = HibernateUtil.getSessionFactory().openSession();
		int res = 0;
		try {
			res = ss.createQuery(
					"update UsrMngtMaster set IS_ACTIVE ='Y' where USR_ID = :userid")
					.setParameter("userid", userId).executeUpdate();
			if (res == 0) {
				log.error("Record NOt found Exception for the User Id "
						+ userId);
				throw new ProfileNotFoundException("UM-EX002",
						"ProfileNotFoundException",
						"Profile Not found for the given User Id");
			}
		} catch (Exception e) {
			if (res == 0) {
				throw new ProfileNotFoundException("UM-EX002",
						"ProfileNotFoundException",
						"Profile Not found for the given User Id");
			} else {
				log.error((new StringBuilder(
						"Error while Deactivating the profile :")).append(
						e.getMessage()).toString());
				throw new ProfileUpdateException("UM-EX003",
						"ProfileUpdateException", e.getMessage());
			}
		} finally {
			ss.close();
		}
		if (res == 1) {
			return true;
		} else {
			return false;
		}
	}
	public static UserProfileDO getUserProfileHelperNew(Long RegisterID)
			throws ProfileNotFoundException, ProfileFetchException {

		UserProfileDO uprofileDo = new UserProfileDO();
		Session ss = HibernateUtil.getSessionFactory().openSession();
		UsrMngtMaster uMaster = null;

		try {

			uMaster = (UsrMngtMaster) ss.get(UsrMngtMaster.class, RegisterID);
			if (uMaster == null) {
				log.error("Record NOt found Exception for the Register Id "
						+ RegisterID);
				throw new ProfileNotFoundException("UM-EX002",
						"ProfileNotFoundException",
						"Profile Not found for the given Register Id");
			} else {
				uprofileDo.setRgstrId(uMaster.getRgstrId());
				uprofileDo.setFirstName(uMaster.getpFname());
				uprofileDo.setMidName(uMaster.getmName());
				uprofileDo.setLastName(uMaster.getlName());
				uprofileDo.setUserName(uMaster.getUserId());
				uprofileDo.setFaceBookId(uMaster.getFbId());
				uprofileDo.setDob(uMaster.getdOb());
				uprofileDo.setEmail(uMaster.getEmail());
				uprofileDo.setUserType(uMaster.getType());
				uprofileDo.setTwitterId(uMaster.getTwitterId());
				uprofileDo.setLinkedinId(uMaster.getLinkedinId());
				UsrMngtAddress uAddress = (UsrMngtAddress) ss.get(
						UsrMngtAddress.class, RegisterID);
				uprofileDo.setAddrLn1(uAddress.getAddrLn1());
				uprofileDo.setAddrLn2(uAddress.getAddrLn2());
				uprofileDo.setCity(uAddress.getCity());
				uprofileDo.setDistrict(uAddress.getDistrict());
				uprofileDo.setState(uAddress.getState());
				uprofileDo.setCountry(uAddress.getCountry());
				uprofileDo.setPincode(uAddress.getPin());
				UsrMngtContactInfo uContact = (UsrMngtContactInfo) ss.get(
						UsrMngtContactInfo.class, RegisterID);
				uprofileDo.setMobile(uContact.getMobileNo());
				uprofileDo.setHomePhoneNo(uContact.getHomePhoneNo());
				uprofileDo.setPhotopath(uContact.getPhotoPath());
				// System.out.println("class : " +
				// uContact.getPhoto().getClass());
				if (uContact.getPhoto() != null) {
					uprofileDo.setPhoto(uContact.getPhoto());
				}

				if (uprofileDo.getUserType().equals("student")) {

					UsrMngtStudent uStudent = (UsrMngtStudent) ss.get(
							UsrMngtStudent.class, RegisterID);
					uprofileDo.setDegreeOfStudent(uStudent.getDegree());
					uprofileDo.setCollge(uStudent.getCollege());
					uprofileDo.setUniversity(uStudent.getUniversity());
					uprofileDo.setStudentID(uStudent.getEnrollmentNo());
					uprofileDo.setCompletionYear(uStudent.getYearOfPass());

					if (uStudent != null && !"null".equals(uStudent)
							&& uStudent.getBranchId() != null) {
						BranchMaster bMaster = (BranchMaster) ss.get(
								BranchMaster.class, uStudent.getBranchId());

						if (bMaster != null
								&& bMaster.getProBrancDesc() != null) {
							uprofileDo.setBranchIdOfStudent(uStudent
									.getBranchId());
							uprofileDo.setProjectBranchDescOfStudent(bMaster
									.getProBrancDesc());
						}
					}
					// uprofileDo.setBranchIdOfStudent(uStudent.getBranchId());

				} else if (uprofileDo.getUserType().equals("college")) {
					UsrMngtCollege ucollege = (UsrMngtCollege) ss.get(
							UsrMngtCollege.class, RegisterID);
					uprofileDo.setWebpage(ucollege.getWebpgLnk());
					uprofileDo.setLogo(ucollege.getLogo());
					/*
					 * uprofileDo.setCntctPerNameofCollege(ucollege
					 * .getCntctPerNameofCollege()); name of the person from the
					 * usr_mngt_master
					 * 
					 * uprofileDo.setCollgeContactEmail(ucollege
					 * .getCntctPerEmailId());
					 */

					uprofileDo.setCollegeName(ucollege.getCollegeName());
					uprofileDo.setCollegeDesc(ucollege.getCollegeDescription());
					uprofileDo.setPrinicipalName(ucollege.getPrincipalName());
					uprofileDo
							.setPrinicipalEmail(ucollege.getPrincipalMailId());
					uprofileDo.setFacilitiesOffrdToStudents(ucollege
							.getFacilitiesOffrdToStudents());
					uprofileDo.setCntctInfoForNatnlInnovnClub(ucollege
							.getCntctInfoForNatnlInnovnClub());
					uprofileDo.setAffltUniversityOfCollege(ucollege
							.getAffltUniversity());
					uprofileDo.setTechpdaFactlyCoordtr(ucollege
							.getTechpdaFactlyCoordtr());

				} else if (uprofileDo.getUserType().equals("mentor")) {
					UsrMngtMentor umentor = (UsrMngtMentor) ss.get(
							UsrMngtMentor.class, RegisterID);
					uprofileDo.setDegreeOfMentor(umentor.getDegree()); // Added
																		// by
																		// Anil
					uprofileDo.setDesignationOfMentor(umentor.getDesignation());
					uprofileDo.setInstitutionalAssctnInfo(umentor
							.getInstitutionalAssctnInfo());
					uprofileDo.setProfessionalExperience(umentor
							.getProExperience());
					uprofileDo.setTimeUspaceForMentoringPerMnth(umentor
							.getTimeUspaceForMentoringPerMnth());
					uprofileDo.setMentorProfile(umentor.getMentorProfile());
					uprofileDo.setExpectationFromMentor(umentor
							.getExpectationFromMentor());
					uprofileDo.setCommitmentUBringIn(umentor
							.getCommitmentUBringIn());
					uprofileDo.setWebpage(umentor.getWebLink());
					uprofileDo.setIntOnGrassrtInnovators(umentor
							.getIntOnGrassrtInnovators());
					uprofileDo.setPopularity(umentor.getPopularity());

					if (umentor != null && !"null".equals(umentor)
							&& umentor.getBranchId() != null) {
						BranchMaster bMaster = (BranchMaster) ss.get(
								BranchMaster.class, umentor.getBranchId());

						if (bMaster != null
								&& bMaster.getProBrancDesc() != null) {
							uprofileDo.setBranchIdOfMentor(umentor
									.getBranchId());
							uprofileDo.setProjectBranchDescOfMentor(bMaster
									.getProBrancDesc());
						}
					}

					// uprofileDo.setBranchIdOfMentor(umentor.getBranchId()); //
					// Modified
					// by
					// Anil
					// System.out.println("Popularity " +
					// umentor.getPopularity());
					// System.out.println("ExpectationFromMentor "+
					// umentor.getExpectationFromMentor());
					// System.out.println("getBranchId " +
					// umentor.getBranchId());

				} else if (uprofileDo.getUserType().equals("faculty")) {
					UsrMngtFaculty ufaculty = (UsrMngtFaculty) ss.get(
							UsrMngtFaculty.class, RegisterID);

					if (ufaculty != null && !"null".equals(ufaculty)
							&& ufaculty.getSpecification() != null) {
						BranchMaster bMaster = (BranchMaster) ss
								.get(BranchMaster.class,
										ufaculty.getSpecification());
						if (bMaster != null
								&& bMaster.getProBrancDesc() != null) {
							uprofileDo.setSpecializationOfFaculty(ufaculty
									.getSpecification());
							uprofileDo.setProjectBranchDescOfFaculty(bMaster
									.getProBrancDesc());

						}
					}

					/*
					 * BranchMaster bMaster = (BranchMaster) ss.get(
					 * BranchMaster.class,ufaculty.getSpecification());
					 * 
					 * if (bMaster!=null && bMaster.getBranchId() != null) {
					 * uprofileDo
					 * .setSpecializationOfFaculty(bMaster.getProBrancDesc()); }
					 */

					// uprofileDo.setSpecializationOfFaculty(ufaculty.getSpecification());
					// // Modified by Anil
					uprofileDo.setAlumni(ufaculty.getAlumni());
					uprofileDo.setMemshipInAssocns(ufaculty
							.getMemshipInAssocns());
					uprofileDo.setPsnlWebpgLink(ufaculty.getPsnlWebpgLink());
					uprofileDo
							.setProffesionalExpOfFaculty(ufaculty.getProExp());
					uprofileDo.setCollgeOfFaculty(ufaculty.getCollege());
					uprofileDo.setDegreeOfFaculty(ufaculty.getDegree());
					uprofileDo.setUniversityOfFaculty(ufaculty.getUniversity());
					uprofileDo.setAffltUniversityOfFaculty(ufaculty
							.getAffltUniversity());

				} else if (uprofileDo.getUserType().equals("industry")) {
					UsrMngtIndustry uindustry = (UsrMngtIndustry) ss.get(
							UsrMngtIndustry.class, RegisterID);
					uprofileDo.setFax(uindustry.getFax());
					uprofileDo.setContactNameOfIndustry(uindustry
							.getCntctprsnName());
					uprofileDo.setContactEmailOfIndustry(uindustry
							.getCntctPrsnEmailId());
					uprofileDo.setOperatnSectr(uindustry.getOperatnSectr());
					uprofileDo.setKindOfSprtUProvideInnovtr(uindustry
							.getKindOfSprtUProvideInnovtr());
					uprofileDo.setPrdRng(uindustry.getPrdRng());
					uprofileDo.setAssociateIndustry(uindustry
							.getAssociateIndustry());
					uprofileDo.setTechngyExprtizOffrToOthers(uindustry
							.getTechngyExprtizOffrToOthers());
					uprofileDo.setSolnReqrdForTechnlgicalChlngs(uindustry
							.getSolnReqrdForTechnlgicalChlngs());
					uprofileDo.setIntrstToPoseInnovtnChlngAwrds(uindustry
							.getIntrstToPoseInnovtnChlngAwrds());
				}
			}

		} catch (ProfileNotFoundException e) {
			throw e;
		} catch (Exception e) {

			log.error((new StringBuilder("Error while fetching the profile :"))
					.append(e.getMessage()).toString());
			throw new ProfileFetchException("UM-EX004",
					"ProfileFetchException", e.getMessage());

		} finally {
			ss.close();
		}

		return uprofileDo;

	}
	
	public static UserProfileDO getUserProfileHelper(Long RegisterID)
			throws ProfileNotFoundException, ProfileFetchException {

		UserProfileDO uprofileDo = new UserProfileDO();
		Session ss = HibernateUtil.getSessionFactory().openSession();
		UsrMngtMaster uMaster = null;

		try {

			uMaster = (UsrMngtMaster) ss.get(UsrMngtMaster.class, RegisterID);
			if (uMaster == null) {
				log.error("Record NOt found Exception for the Register Id "
						+ RegisterID);
				throw new ProfileNotFoundException("UM-EX002",
						"ProfileNotFoundException",
						"Profile Not found for the given Register Id");
			} else {
				uprofileDo.setRgstrId(uMaster.getRgstrId());
				uprofileDo.setFirstName(uMaster.getpFname());
				uprofileDo.setMidName(uMaster.getmName());
				uprofileDo.setLastName(uMaster.getlName());
				uprofileDo.setUserName(uMaster.getUserId());
				uprofileDo.setFaceBookId(uMaster.getFbId());
				uprofileDo.setDob(uMaster.getdOb());
				uprofileDo.setEmail(uMaster.getEmail());
				uprofileDo.setUserType(uMaster.getType());
				uprofileDo.setTwitterId(uMaster.getTwitterId());
				uprofileDo.setLinkedinId(uMaster.getLinkedinId());
				UsrMngtAddress uAddress = (UsrMngtAddress) ss.get(
						UsrMngtAddress.class, RegisterID);
				uprofileDo.setAddrLn1(uAddress.getAddrLn1());
				uprofileDo.setAddrLn2(uAddress.getAddrLn2());
				uprofileDo.setCity(uAddress.getCity());
				uprofileDo.setDistrict(uAddress.getDistrict());
				uprofileDo.setState(uAddress.getState());
				uprofileDo.setCountry(uAddress.getCountry());
				uprofileDo.setPincode(uAddress.getPin());
				UsrMngtContactInfo uContact = (UsrMngtContactInfo) ss.get(
						UsrMngtContactInfo.class, RegisterID);
				uprofileDo.setMobile(uContact.getMobileNo());
				uprofileDo.setHomePhoneNo(uContact.getHomePhoneNo());
				// System.out.println("class : " +
				// uContact.getPhoto().getClass());
				if (uContact.getPhoto() != null) {
					uprofileDo.setPhoto(uContact.getPhoto());
				}

				if (uprofileDo.getUserType().equals("student")) {

					UsrMngtStudent uStudent = (UsrMngtStudent) ss.get(
							UsrMngtStudent.class, RegisterID);
					uprofileDo.setDegreeOfStudent(uStudent.getDegree());
					uprofileDo.setCollge(uStudent.getCollege());
					uprofileDo.setUniversity(uStudent.getUniversity());
					uprofileDo.setStudentID(uStudent.getEnrollmentNo());
					uprofileDo.setCompletionYear(uStudent.getYearOfPass());

					if (uStudent != null && !"null".equals(uStudent)
							&& uStudent.getBranchId() != null) {
						BranchMaster bMaster = (BranchMaster) ss.get(
								BranchMaster.class, uStudent.getBranchId());

						if (bMaster != null
								&& bMaster.getProBrancDesc() != null) {
							uprofileDo.setBranchIdOfStudent(uStudent
									.getBranchId());
							uprofileDo.setProjectBranchDescOfStudent(bMaster
									.getProBrancDesc());
						}
					}
					// uprofileDo.setBranchIdOfStudent(uStudent.getBranchId());

				} else if (uprofileDo.getUserType().equals("college")) {
					UsrMngtCollege ucollege = (UsrMngtCollege) ss.get(
							UsrMngtCollege.class, RegisterID);
					uprofileDo.setWebpage(ucollege.getWebpgLnk());
					uprofileDo.setLogo(ucollege.getLogo());
					/*
					 * uprofileDo.setCntctPerNameofCollege(ucollege
					 * .getCntctPerNameofCollege()); name of the person from the
					 * usr_mngt_master
					 * 
					 * uprofileDo.setCollgeContactEmail(ucollege
					 * .getCntctPerEmailId());
					 */

					uprofileDo.setCollegeName(ucollege.getCollegeName());
					uprofileDo.setCollegeDesc(ucollege.getCollegeDescription());
					uprofileDo.setPrinicipalName(ucollege.getPrincipalName());
					uprofileDo
							.setPrinicipalEmail(ucollege.getPrincipalMailId());
					uprofileDo.setFacilitiesOffrdToStudents(ucollege
							.getFacilitiesOffrdToStudents());
					uprofileDo.setCntctInfoForNatnlInnovnClub(ucollege
							.getCntctInfoForNatnlInnovnClub());
					uprofileDo.setAffltUniversityOfCollege(ucollege
							.getAffltUniversity());
					uprofileDo.setTechpdaFactlyCoordtr(ucollege
							.getTechpdaFactlyCoordtr());

				} else if (uprofileDo.getUserType().equals("mentor")) {
					UsrMngtMentor umentor = (UsrMngtMentor) ss.get(
							UsrMngtMentor.class, RegisterID);
					uprofileDo.setDegreeOfMentor(umentor.getDegree()); // Added
																		// by
																		// Anil
					uprofileDo.setDesignationOfMentor(umentor.getDesignation());
					uprofileDo.setInstitutionalAssctnInfo(umentor
							.getInstitutionalAssctnInfo());
					uprofileDo.setProfessionalExperience(umentor
							.getProExperience());
					uprofileDo.setTimeUspaceForMentoringPerMnth(umentor
							.getTimeUspaceForMentoringPerMnth());
					uprofileDo.setMentorProfile(umentor.getMentorProfile());
					uprofileDo.setExpectationFromMentor(umentor
							.getExpectationFromMentor());
					uprofileDo.setCommitmentUBringIn(umentor
							.getCommitmentUBringIn());
					uprofileDo.setWebpage(umentor.getWebLink());
					uprofileDo.setIntOnGrassrtInnovators(umentor
							.getIntOnGrassrtInnovators());
					uprofileDo.setPopularity(umentor.getPopularity());

					if (umentor != null && !"null".equals(umentor)
							&& umentor.getBranchId() != null) {
						BranchMaster bMaster = (BranchMaster) ss.get(
								BranchMaster.class, umentor.getBranchId());

						if (bMaster != null
								&& bMaster.getProBrancDesc() != null) {
							uprofileDo.setBranchIdOfMentor(umentor
									.getBranchId());
							uprofileDo.setProjectBranchDescOfMentor(bMaster
									.getProBrancDesc());
						}
					}

					// uprofileDo.setBranchIdOfMentor(umentor.getBranchId()); //
					// Modified
					// by
					// Anil
					// System.out.println("Popularity " +
					// umentor.getPopularity());
					// System.out.println("ExpectationFromMentor "+
					// umentor.getExpectationFromMentor());
					// System.out.println("getBranchId " +
					// umentor.getBranchId());

				} else if (uprofileDo.getUserType().equals("faculty")) {
					UsrMngtFaculty ufaculty = (UsrMngtFaculty) ss.get(
							UsrMngtFaculty.class, RegisterID);

					if (ufaculty != null && !"null".equals(ufaculty)
							&& ufaculty.getSpecification() != null) {
						BranchMaster bMaster = (BranchMaster) ss
								.get(BranchMaster.class,
										ufaculty.getSpecification());
						if (bMaster != null
								&& bMaster.getProBrancDesc() != null) {
							uprofileDo.setSpecializationOfFaculty(ufaculty
									.getSpecification());
							uprofileDo.setProjectBranchDescOfFaculty(bMaster
									.getProBrancDesc());

						}
					}

					/*
					 * BranchMaster bMaster = (BranchMaster) ss.get(
					 * BranchMaster.class,ufaculty.getSpecification());
					 * 
					 * if (bMaster!=null && bMaster.getBranchId() != null) {
					 * uprofileDo
					 * .setSpecializationOfFaculty(bMaster.getProBrancDesc()); }
					 */

					// uprofileDo.setSpecializationOfFaculty(ufaculty.getSpecification());
					// // Modified by Anil
					uprofileDo.setAlumni(ufaculty.getAlumni());
					uprofileDo.setMemshipInAssocns(ufaculty
							.getMemshipInAssocns());
					uprofileDo.setPsnlWebpgLink(ufaculty.getPsnlWebpgLink());
					uprofileDo
							.setProffesionalExpOfFaculty(ufaculty.getProExp());
					uprofileDo.setCollgeOfFaculty(ufaculty.getCollege());
					uprofileDo.setDegreeOfFaculty(ufaculty.getDegree());
					uprofileDo.setUniversityOfFaculty(ufaculty.getUniversity());
					uprofileDo.setAffltUniversityOfFaculty(ufaculty
							.getAffltUniversity());

				} else if (uprofileDo.getUserType().equals("industry")) {
					UsrMngtIndustry uindustry = (UsrMngtIndustry) ss.get(
							UsrMngtIndustry.class, RegisterID);
					uprofileDo.setFax(uindustry.getFax());
					uprofileDo.setContactNameOfIndustry(uindustry
							.getCntctprsnName());
					uprofileDo.setContactEmailOfIndustry(uindustry
							.getCntctPrsnEmailId());
					uprofileDo.setOperatnSectr(uindustry.getOperatnSectr());
					uprofileDo.setKindOfSprtUProvideInnovtr(uindustry
							.getKindOfSprtUProvideInnovtr());
					uprofileDo.setPrdRng(uindustry.getPrdRng());
					uprofileDo.setAssociateIndustry(uindustry
							.getAssociateIndustry());
					uprofileDo.setTechngyExprtizOffrToOthers(uindustry
							.getTechngyExprtizOffrToOthers());
					uprofileDo.setSolnReqrdForTechnlgicalChlngs(uindustry
							.getSolnReqrdForTechnlgicalChlngs());
					uprofileDo.setIntrstToPoseInnovtnChlngAwrds(uindustry
							.getIntrstToPoseInnovtnChlngAwrds());
				}
			}

		} catch (ProfileNotFoundException e) {
			throw e;
		} catch (Exception e) {

			log.error((new StringBuilder("Error while fetching the profile :"))
					.append(e.getMessage()).toString());
			throw new ProfileFetchException("UM-EX004",
					"ProfileFetchException", e.getMessage());

		} finally {
			ss.close();
		}

		return uprofileDo;

	}
	
	
/*	public static boolean authenticateHelper(String userid, String pwd)
			throws UserNotFoundException, LoginException,
			PasswordMismatchException, PasswordExpiryException,
			PasswordResetException {

		PasswordUtil pwdUtil = new PasswordUtil();
		boolean authenticateFlag = false;
		try {
			String currentPwd = pwdUtil.getCurrentPassword(userid);
			log.info("Current Password " + currentPwd);
			if (!currentPwd.equals(pwd)) {
				log.error("Entered Wrong Password");
				throw new PasswordMismatchException("UM-Ex007",
						"PasswordMismatchException", "Entered Wrong Password");
			} else {
				authenticateFlag = true;
			}

		} catch (PasswordMismatchException e) {
			throw e;
		} catch (Exception e) {
			log.error("Password Mismatch Exception" + e.getMessage());
			throw new PasswordMismatchException("UM-Ex007",
					"PasswordMismatchException", e.getMessage());
		}

		return authenticateFlag;
	}*/

	public static boolean authenticateHelper(String emailId, String pwd)
			throws UserNotFoundException, LoginException,
			PasswordMismatchException, PasswordExpiryException,
			PasswordResetException {

		PasswordUtil pwdUtil = new PasswordUtil();
		boolean authenticateFlag = false;
		try {
			String currentPwd = pwdUtil.getCurrentPassword(emailId);
			log.info("Current Password " + currentPwd);
			if (!currentPwd.equals(pwd)) {
				log.error("Entered Wrong Password");
				throw new PasswordMismatchException("UM-Ex007",
						"PasswordMismatchException", "Entered Wrong Password");
			} else {
				authenticateFlag = true;
			}

		} catch (PasswordMismatchException e) {
			throw e;
		}catch (UserNotFoundException e) {
			log.error("User Not Found Exception"+ e.getMessage());
			throw new UserNotFoundException("UM-Ex006","UserNotFoundException","Entered Email ID does not exist in Techpedia database");
		} catch (Exception e) {
			log.error("Password Mismatch Exception" + e.getMessage());
			throw new PasswordMismatchException("UM-Ex007",	"PasswordMismatchException", e.getMessage());
		}

		return authenticateFlag;
	}

	public static boolean passwordResetHelper(PasswordResetVo pwdResetVo)
			throws PasswordResetException, UserNotFoundException {
		boolean flag = false;
		Transaction tx = null;
		Session ss = HibernateUtil.getSessionFactory().openSession();
		String createdDate = getCurrentTimeStamp();
		UsrMngtPassword pwdObj = null;
		String currentPassword = null;
		try {
			log.error("user id:" + pwdResetVo.getUserName() + "oldPwd :"
					+ pwdResetVo.getOldpassword() + "newPwd:"
					+ pwdResetVo.getNewpassword());
			currentPassword = getCurrentPassword(pwdResetVo.getUserName());

			if (!currentPassword.equals(pwdResetVo.getOldpassword())) {
				log.error("The current password entered was wrong");
				throw new PasswordResetException("UM-Ex005",
						"PasswordResetException",
						"The current password entered was wrong");

			}
			if (pwdResetVo.getOldpassword().equals(pwdResetVo.getNewpassword())) {
				log.error("old password and new passwords are same");
				throw new PasswordResetException("UM-Ex005",
						"PasswordResetException",
						"old password and new passwords are same");

			} else if (pwdResetVo.getUserName().equals(
					pwdResetVo.getNewpassword())) {
				log.error("new password and user id are same");
				throw new PasswordResetException("UM-Ex005",
						"PasswordResetException",
						"new password and user id are same");

			} else {
				tx = ss.beginTransaction();
				log.info("Password Reset : Start");
				Query queryPassword = ss
						.createQuery("from UsrMngtPassword where USR_ID = :userid order by CREATED_DATE desc");
				queryPassword.setParameter("userid", pwdResetVo.getUserName());
				queryPassword.setMaxResults(5);
				@SuppressWarnings("unchecked")
				List<UsrMngtPassword> passwords = queryPassword.list();

				for (int i = 0; i < passwords.size(); i++) {
					pwdObj = (UsrMngtPassword) passwords.get(i);
					String lastPassword = ChiperUtils.decrypt2(pwdObj
							.getUsrPasswd());
					if (lastPassword.equals(pwdResetVo.getNewpassword())) {
						log.error("New password entered is one of the last 5 passwords");
						throw new PasswordResetException("UM-Ex005",
								"PasswordResetException",
								"New password entered is one of the last 5 passwords");
					}

				}

				String encryPassword = ChiperUtils.encrypt2(pwdResetVo
						.getNewpassword());
				if (passwords.size() >= 5) {

					Date lastcreateddate = (Date) ss
							.createSQLQuery(
									"select min(CREATED_DATE) from usr_mngt_passwd where USR_ID=:userid")
							.setParameter("userid", pwdResetVo.getUserName())
							.uniqueResult();

					Query query = ss
							.createQuery("update UsrMngtPassword u set u.usrPasswd = :newpwd, u.createdDate= :createddate where u.usrId = :userid and u.createdDate=:lastCreated");
					query.setParameter("userid", pwdResetVo.getUserName());
					query.setParameter("newpwd", encryPassword);
					query.setParameter("createddate", createdDate);
					query.setParameter("lastCreated",
							lastcreateddate.toString());
					query.executeUpdate();

				} else {

					UsrMngtPassword uPassword = new UsrMngtPassword(
							pwdObj.getRgstrId(), pwdResetVo.getUserName(),
							encryPassword, createdDate);
					ss.save(uPassword);
				}
				tx.commit();
				flag = true;

			}

		} catch (UserNotFoundException | PasswordResetException e) {

			throw e;

		} catch (Exception e) {

			try {
				tx.rollback();
			} catch (Exception e1) {
				log.error((new StringBuilder(
						"Error while Roll back the transaction :")).append(
						e.getMessage()).toString());
				throw new PasswordResetException("UM-Ex005",
						"PasswordResetException", e.getMessage());
			}
		} finally {
			tx = null;
			ss.close();
		}
		log.info("Password Reset : End");
		return flag;

	}

	public static String getCurrentPassword(String userId)
			throws UserNotFoundException, PasswordResetException {
		String currentPassword = null;
		Session ss = HibernateUtil.getSessionFactory().openSession();
		UsrMngtPassword passwords = null;
		try {
			Query queryPassword = ss
					.createQuery("from UsrMngtPassword where USR_ID = :userid order by CREATED_DATE desc");
			queryPassword.setParameter("userid", userId);
			queryPassword.setMaxResults(1);
			passwords = (UsrMngtPassword) queryPassword.uniqueResult();
			if (passwords == null) {
				log.error("Entered User Id is not Existed in the database");
				throw new UserNotFoundException("UM-Ex006",
						"UserNotFoundException",
						"Entered User Id is not Existed in the database");
			}
			currentPassword = ChiperUtils.decrypt2(passwords.getUsrPasswd());
		} catch (Exception e) {
			if (passwords == null) {
				log.error("Entered User Id is not Existed in the database");
				throw new UserNotFoundException("UM-Ex006",
						"UserNotFoundException",
						"Entered User Id is not Existed in the database");
			} else {
				log.error("Password Reset Exception" + e.getMessage());
				throw new PasswordResetException("UM-Ex-005",
						"PasswordResetException", e.getMessage());
			}
		} finally {
			ss.close();
		}
		return currentPassword;
	}

	public static boolean emailIdVerificationHelper(String emailId)
			throws UserNotFoundException, ProfileFetchException {
		Session ss = HibernateUtil.getSessionFactory().openSession();
		//UserProfileDO uprofileDo = null;
		//String rgstrId = null;
		try {
			Query query = ss
					.createSQLQuery("select RGSTR_ID from usr_mngt_master where EMAIL_ID = :emailId");
			query.setParameter("emailId", emailId);
			//rgstrId = query.uniqueResult().toString();
			if (query.uniqueResult() == null) {
				log.error("Entered Email Id is not Existed in the database");
				throw new UserNotFoundException("UM-Ex006",
						"UserNotFoundException",
						"Entered Email Id is not Existed in the database");
			}
			//uprofileDo = getUserProfileHelper(Long.parseLong(rgstrId));
		} catch (UserNotFoundException e) {
			log.error("Entered User Id is not Existed in the database");
			throw new UserNotFoundException("UM-Ex006",
					"UserNotFoundException",
					"Entered Email Id is not Existed in the database");
		} finally {
			ss.close();
		}

		return true;
	}
    
/*	public static UserProfileDO signInHelper(SignInVo signInVo)
			throws UserNotFoundException, LoginException,
			PasswordMismatchException, PasswordExpiryException,
			PasswordResetException, ProfileFetchException,
			UserInactiveException {
		List result = null;
		String res;
		if (!authenticateHelper(signInVo.getUsername(), signInVo.getPassword())) {
			log.error("Password Mismatch Exception");
			throw new PasswordMismatchException("UM-Ex007",
					"PasswordMismatchException", "Entered the Wrong Password");
		}

		Session ss = HibernateUtil.getSessionFactory().openSession();
		SQLQuery userActiveQuery = ss
				.createSQLQuery("select IS_ACTIVE from techpedia.usr_mngt_master where usr_id=:userId");
		userActiveQuery.setParameter("userId", signInVo.getUsername());
		result = userActiveQuery.list();
		res = result.get(0).toString();
		if (!res.equalsIgnoreCase("y")) {
			log.error("User is inactive: " + signInVo.getUsername());
			throw new UserInactiveException("UM-Ex025",
					"UserInactiveException",
					"User is inactive for given user Id");
		}

		UserProfileDO uprofileDo = null;
		long rgstrId = 0;
		try {

			Query query = ss
					.createQuery("from UsrMngtPassword where USR_ID = :userid order by CREATED_DATE desc");
			query.setParameter("userid", signInVo.getUsername());
			query.setMaxResults(1);
			UsrMngtPassword record = (UsrMngtPassword) query.uniqueResult();
			if (record == null) {
				log.error("Entered User Id is not Existed in the database");
				throw new UserNotFoundException("UM-Ex-006",
						"UserNotFoundException",
						"Entered User Id is not Existed in the database");
			}

			rgstrId = record.getRgstrId();
			System.out.println("rgstrId :" + rgstrId);
			uprofileDo = getUserProfileHelper(rgstrId);

			// uprofileDo.setUsrAccessDetails(getUsrRolePermissions(rgstrId));
		} catch (UserNotFoundException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			log.error((new StringBuilder("Error while fetching the profile :"))
					.append(e.getMessage()).toString());
			throw new ProfileFetchException("UM-EX004",
					"ProfileFetchException", e.getMessage());
		} finally {
			ss.close();
		}

		return uprofileDo;

	}*/
	
	public static UserProfileDO signInHelper(SignInVo signInVo)
			throws UserNotFoundException, LoginException,
			PasswordMismatchException, PasswordExpiryException,
			PasswordResetException, ProfileFetchException,
			UserInactiveException {
		List result = null;
		String res;
		if (!authenticateHelper(signInVo.getEmailId(), signInVo.getPassword())) {
			log.error("Password Mismatch Exception");
			throw new PasswordMismatchException("UM-Ex007",
					"PasswordMismatchException", "Entered the Wrong Password");
		}

		Session ss = HibernateUtil.getSessionFactory().openSession();
		SQLQuery userActiveQuery = ss
				.createSQLQuery("select IS_ACTIVE from techpedia.usr_mngt_master where EMAIL_ID=:emailId");
		userActiveQuery.setParameter("emailId", signInVo.getEmailId());
		result = userActiveQuery.list();
		res = result.get(0).toString();
		if (!res.equalsIgnoreCase("y")) {
			log.error("User is inactive: " + signInVo.getEmailId());
			throw new UserInactiveException("UM-Ex025",
					"UserInactiveException",
					"User is inactive for given email id");
		}

		UserProfileDO uprofileDo = null;
		long rgstrId = 0;
		try {

			//Query query = ss.createQuery("from UsrMngtPassword where USR_ID = :userid order by CREATED_DATE desc");
			Query query = ss.createQuery("from UsrMngtPassword where USR_ID in(select userId from UsrMngtMaster where EMAIL_ID=:emailId) order by CREATED_DATE desc");			
			query.setParameter("emailId", signInVo.getEmailId());
			query.setMaxResults(1);
			UsrMngtPassword record = (UsrMngtPassword) query.uniqueResult();
			if (record == null) {
				log.error("Entered Email id is not Existed in the database");
				throw new UserNotFoundException("UM-Ex-006",
						"UserNotFoundException",
						"Entered Email ID does not exist in Techpedia database");
			}

			rgstrId = record.getRgstrId();
			System.out.println("rgstrId :" + rgstrId);
			uprofileDo = getUserProfileHelper(rgstrId);

			// uprofileDo.setUsrAccessDetails(getUsrRolePermissions(rgstrId));
		} catch (UserNotFoundException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			log.error((new StringBuilder("Error while fetching the profile :"))
					.append(e.getMessage()).toString());
			throw new ProfileFetchException("UM-EX004",
					"ProfileFetchException", e.getMessage());
		} finally {
			ss.close();
		}

		return uprofileDo;

	}
	
	public static UserProfileDO socialSignInHelper(String emailId)
			throws UserNotFoundException, LoginException,
			ProfileFetchException,UserInactiveException {
		List result = null;
		String res;
		Session ss = HibernateUtil.getSessionFactory().openSession();
		SQLQuery userActiveQuery = ss
				.createSQLQuery("select IS_ACTIVE from techpedia.usr_mngt_master where EMAIL_ID=:emailId");
		userActiveQuery.setParameter("emailId", emailId);
		result = userActiveQuery.list();
		res = result.get(0).toString();
		if (!res.equalsIgnoreCase("y")) {
			log.error("User is inactive: " + emailId);
			throw new UserInactiveException("UM-Ex025",
					"UserInactiveException",
					"User is inactive for given email id");
		}

		UserProfileDO uprofileDo = null;
		long rgstrId = 0;
		try {

			//Query query = ss.createQuery("from UsrMngtPassword where USR_ID = :userid order by CREATED_DATE desc");
			Query query = ss.createQuery("select rgstrId from UsrMngtMaster where EMAIL_ID=:emailId");			
			query.setParameter("emailId", emailId);
			rgstrId = (long) query.uniqueResult();
			System.out.println("rgstrId :" + rgstrId);
			uprofileDo = getUserProfileHelper(rgstrId);

			// uprofileDo.setUsrAccessDetails(getUsrRolePermissions(rgstrId));
		}  catch (Exception e) {
			e.printStackTrace();
			log.error((new StringBuilder("Error while fetching the profile :"))
					.append(e.getMessage()).toString());
			throw new ProfileFetchException("UM-EX004",
					"ProfileFetchException", e.getMessage());
		} finally {
			ss.close();
		}

		return uprofileDo;

	}


	public static boolean updatePhotoHelper(UpdateUserPhotoDO updateUserPhotoDO)
			throws ProfileUpdateException {

		boolean flag = false;
		Transaction tx = null;
		Session ss = HibernateUtil.getSessionFactory().openSession();
		try {
			tx = ss.beginTransaction();

			Query query = ss
					.createSQLQuery("update techpedia.usr_mngt_contact_info set PHOTO = :newphoto where rgstr_id = :rgstrId");
			query.setParameter("newphoto",
					updateUserPhotoDO.getPhotoByteStream());
			query.setParameter("rgstrId",
					new Long(updateUserPhotoDO.getRegisterID()));
			query.executeUpdate();

			tx.commit();
			flag = true;
		} catch (Exception e) {
			// e.getStackTrace();
			e.printStackTrace();
			log.error((new StringBuilder("Error while updating the Photo: "))
					.append(e.getMessage()).toString());
			throw new ProfileUpdateException("UM-EX004",
					"ProfileUpdateException", e.getMessage());
		} finally {
			tx = null;
			ss.close();
		}
		return flag;

	}

	public static List<UserProfileDO> searchUserProfilesHelper(
			SearchCriteriaDO scDo) throws ProfileSearchException {
		Session ss = HibernateUtil.getSessionFactory().openSession();
		String fname = scDo.getFirstName();
		String lname = scDo.getLastName();
		String dob = scDo.getDob();
		String email = scDo.getEmail();
		String userName = scDo.getUserName();
		String userType = scDo.getUserType();
		UsrMngtMaster uMaster = null;
		List<UserProfileDO> uprofile = new ArrayList<UserProfileDO>();
		try {
			Query query = ss
					.createQuery("from UsrMngtMaster where "
							+ "(pFname like :fname OR lName like :lname OR dOb like :dob OR email like :email OR userId like :userName OR type like :usertype) OR ("
							+ "(pFname like :fname AND lName like :lname) OR "
							+ "(pFname like :fname AND dOb like :dob)) order by RGSTR_DATE desc");
			query.setParameter("fname", fname + "%");
			query.setParameter("lname", lname + "%");
			query.setParameter("dob", dob + "%");
			query.setParameter("email", email + "%");
			query.setParameter("userName", userName + "%");
			query.setParameter("usertype", userType + "%");
			@SuppressWarnings("unchecked")
			List<UsrMngtMaster> uMasterlist = query.list();
			for (int i = 0; i < uMasterlist.size(); i++) {
				UserProfileDO uprofileDo = new UserProfileDO();
				uMaster = (UsrMngtMaster) uMasterlist.get(i);
				uprofileDo.setRgstrId(uMaster.getRgstrId());
				uprofileDo.setFirstName(uMaster.getpFname());
				uprofileDo.setMidName(uMaster.getmName());
				uprofileDo.setLastName(uMaster.getlName());
				uprofileDo.setUserName(uMaster.getUserId());
				uprofileDo.setFaceBookId(uMaster.getFbId());
				uprofileDo.setDob(uMaster.getdOb());
				uprofileDo.setEmail(uMaster.getEmail());
				uprofileDo.setUserType(uMaster.getType());
				uprofile.add(uprofileDo);
				uprofileDo = null;
			}
		} catch (Exception e) {
			log.error((new StringBuilder("Error while fetching the Records"))
					.append(e.getMessage()).toString());
			throw new ProfileSearchException("EX012", "ProfileSearchException",
					e.getMessage());
		}

		finally {
			ss.close();
		}

		return uprofile;
	}

	public static UsrAccessDetails getUsrRolePermissions(Long rgstrId)
			throws UserNotFoundException,
			com.techpedia.usermanagement.exception.UserRoleNotMappedException,
			UserRoleNotMappedWithFunctionIdsException,
			UserRoleNotDefinedException, UserFunctionsNotDefinedException {

		UsrAccessDetails uad = null;

		Map<String, List<String>> rf = null;

		Session ss = HibernateUtil.getSessionFactory().openSession();
		List<String> rolesList = null;

		UsrAssignAuthortnRolesToUsrs role = null;
		try {

			Query roles = ss
					.createQuery("from UsrAssignAuthortnRolesToUsrs where RGSTR_ID = :rgstrId");
			roles.setParameter("rgstrId", rgstrId);
			@SuppressWarnings("unchecked")
			List<UsrAssignAuthortnRolesToUsrs> noOfRoles = roles.list();

			if (noOfRoles != null && noOfRoles.isEmpty()) {
				log.error("UM-EX011 - UserRoleNotMappedException -> The registered id = "
						+ rgstrId + " is not mapped with any roles :");
				// throw new
				// UserRoleNotMappedException("UM-EX011","UserRoleNotMappedException","The registered id = "+
				// rgstrId + " is not mapped with any roles :");
				return new UsrAccessDetails();
			} else {
				uad = new UsrAccessDetails();
				rolesList = new ArrayList<String>();
				rf = new HashMap<String, List<String>>();
				String roleName = null;
				String fIds = null;
				for (int i = 0; i < noOfRoles.size(); i++) {
					role = (UsrAssignAuthortnRolesToUsrs) noOfRoles.get(i);
					rolesList.add(role.getRoleName());
					Query roleTbl = ss
							.createQuery("FROM UsrAuthortnRoles WHERE ROLE_ID = :roleId");

					roleTbl.setParameter("roleId", role.getRoleId());

					UsrAuthortnRoles uar = (UsrAuthortnRoles) roleTbl
							.uniqueResult();
					if (uar == null) {
						log.error("UM-EX012 - UserRoleNotDefinedException -> The Role id = "
								+ role.getRoleId()
								+ " is not defined in UsrAuthortnRoles table:");
						// throw new
						// UserRoleNotDefinedException("UM-EX012","UserRoleNotDefinedException","The Role id = "+
						// role.getRoleId() +
						// " is not defined in UsrAuthortnRoles table:");
					} else {

						roleName = uar.getRoleName();

						log.info(" RoleNmae = " + roleName);
						fIds = uar.getFunIds();
						log.info("Function ids mapped to role is -------------> "
								+ fIds);

						if (fIds == null) {
							log.error("UM-EX014 - UserRoleNotMappedWithFunctionIdsException-> The Role id = "
									+ role.getRoleId()
									+ " is defined, but FUN_IDS column of UsrAuthortnRoles is not defined or Emppty :");
							// throw new
							// UserRoleNotMappedWithFunctionIdsException("UM-EX014","UserRoleNotMappedWithFunctionIdsException","The Role id = "+
							// role.getRoleId() +
							// " is defined, but FUN_IDS column of UsrAuthortnRoles is not defined or Emppty :");
						} else {

							String fIdsArray[] = fIds.split(",", 0);

							Query fNames = ss
									.createSQLQuery("select fun_name from usr_authortn_functions where fun_id in (:funIds)");

							@SuppressWarnings("unchecked")
							List<String> funNameList = (List<String>) fNames
									.setParameterList("funIds", fIdsArray)
									.list();
							if (funNameList.isEmpty()) {
								log.error("UM-EX013 - UserFunctionsNotDefinedException -> The Functions ids = "
										+ fIds
										+ " are not defined in usr_authortn_functions table:");
								List<String> fNameList = new ArrayList<String>();
								rf.put(roleName, fNameList);
								// throw new
								// UserFunctionsNotDefinedException("UM-EX013","UserFunctionsNotDefinedException","The Function ids = "+
								// fIds +
								// " are not defined in usr_authortn_functions table:");
							} else {
								rf.put(roleName, funNameList);
							}
						}

					}

				}
				uad.setNoOfRoles(rolesList);
				uad.setRoleVsFunctions(rf);

			}
		}
		/*
		 * catch(UserRoleNotDefinedException e) { //throw e; return uad; }
		 */
		/*
		 * catch(UserRoleNotMappedWithFunctionIdsException e) { throw e; }
		 */
		/*
		 * catch(UserFunctionsNotDefinedException e) { throw e; }
		 */
		catch (Exception e) {
			e.printStackTrace();
			log.error((new StringBuilder(
					"Error while fetching Authorizations :")).append(
					e.getMessage()).toString());
			// throw new
			// UserRoleNotMappedException("UM-EX011","UserRoleNotMappedException",e.getMessage());
			return uad;
		} finally {
			ss.close();
		}

		return uad;
	}

	public static String getCurrentTimeStamp() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate;
	}

	public static List<UserTeamListDO> getUserTeamList(Long registerID)
			throws UserTeamListFetchException {
		log.info("UserManagementDAOImpl getUserTeamList :START");
		List<UserTeamListDO> userTeamList = null;
		UserTeamListDO userTeamDO = null;
		List<Long> teamIDS = null;
		Session ss = HibernateUtil.getSessionFactory().openSession();
		try {

			Query query = ss
					.createQuery("from ProjectTeamTxn where REGSTR_ID = :rgstrId");
			query.setParameter("rgstrId", registerID);
			@SuppressWarnings("unchecked")
			List<ProjectTeamTxn> teamIdsList = query.list();
			if (teamIdsList != null && teamIdsList.isEmpty()) {
				log.error("UM-EX018 - UserTeamListFetchException -> The registered id = "
						+ registerID + " is not belongs to any team :");
				throw new UserTeamListFetchException("UM-EX018",
						"UserTeamListFetchException", "The registered id = "
								+ registerID + " is not belongs to any team.");
			} else {
				userTeamList = new ArrayList<UserTeamListDO>();

				teamIDS = new ArrayList<Long>();
				for (ProjectTeamTxn projectTeamTxn : teamIdsList) {
					teamIDS.add(projectTeamTxn.getTeamId());
				}
				Query fNames = ss
						.createQuery("from ProjectTeamMaster where TEAM_ID in (:teamIds)");
				@SuppressWarnings("unchecked")
				List<ProjectTeamMaster> teamsList = fNames.setParameterList(
						"teamIds", teamIDS).list();

				for (ProjectTeamMaster projectTeamMaster : teamsList) {
					userTeamDO = new UserTeamListDO();
					userTeamDO
							.setTeamID(new Long(projectTeamMaster.getTeamId())
									.toString());
					userTeamDO.setTeamName(projectTeamMaster.getProjTeamDesc());
					userTeamList.add(userTeamDO);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error while fetching user team.");
			throw new UserTeamListFetchException("UM-EX018",
					"UserTeamListFetchException", e.getMessage());
		} finally {
			ss.close();
		}
		return userTeamList;
	}

	/*
	 * public static void main(String args[]) throws UserNotFoundException,
	 * UserRoleNotMappedException, UserRoleNotMappedWithFunctionIdsException,
	 * UserRoleNotDefinedException, UserFunctionsNotDefinedException,
	 * LoginException, PasswordMismatchException, PasswordExpiryException,
	 * PasswordResetException, ProfileFetchException,
	 * PopularMentorsFetchException, CurrentPasswordFetchException,
	 * ProfileUpdateException{
	 * 
	 * //getUsrRolePermissions(new Long(89)); //signInHelper("admin","admin");
	 * 
	 * //getUserRecentComments(new Long(200914)); //getPopularMentorList();
	 * //forgotPassword("admjhjhin@admin.com"); UpdateUserPhotoDO
	 * updateUserPhotoDO = new UpdateUserPhotoDO();
	 * updateUserPhotoDO.setRegisterID("1"); String byte1=null; try { byte1 =
	 * Image2Base64.getbyteArray(); System.out.println("byte1 : " + byte1);
	 * //System.out.println("byte1 bytes: " + byte1.getBytes()); } catch
	 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace();
	 * } updateUserPhotoDO.setPhotoByteStream(byte1);
	 * updatePhotoHelper(updateUserPhotoDO); //signInHelper("admin", "admin");
	 * 
	 * }
	 */

	public static List<UserRecentComments> getUserRecentComments(Long registerID)
			throws UserRecentCommentsFetchException {
		log.info("UserManagementDAOImpl getUserRecentComments :START");

		List<UserRecentComments> userCommentsList = null;
		UserRecentComments usrrecentcmnts = null;
		Session ss = HibernateUtil.getSessionFactory().openSession();

		try {
			Query query = ss
					.createSQLQuery("select a.PROJ_ID, a.PROJ_COMMENT,a.COMMENT_ID, b.PROJ_TITLE from tb_tech001_txn_project_comment a, tb_tech001_mast_projects_detail b where (a.REGSTR_ID = :rgstrId && a.PROJ_ID = b.PROJ_ID)");
			query.setParameter("rgstrId", registerID);
			@SuppressWarnings("unchecked")
			List<Object[]> rows = query.list();

			if (rows != null) {
				userCommentsList = new ArrayList<UserRecentComments>();
				for (Object[] row : rows) {
					// System.out.println(row[2].toString());
					usrrecentcmnts = new UserRecentComments();
					usrrecentcmnts.setProjectID(row[0].toString());
					if (row[1] != null && row[1].toString() != null) {
						usrrecentcmnts.setProjectComment(row[1].toString());
					}
					if (row[2] != null && row[2].toString() != null) {
						usrrecentcmnts.setProjectCommentID(row[2].toString());
					}
					if (row[3] != null && row[3].toString() != null) {
						usrrecentcmnts.setProjectTitle(row[3].toString());
					}
					userCommentsList.add(usrrecentcmnts);
					System.out.println("project ID:" + row[0].toString()
							+ "Project Comment:" + row[1].toString()
							+ "Comment ID:" + row[2].toString()
							+ "Project Title:" + row[3].toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error while fetching getPopularMentorList.");
			throw new UserRecentCommentsFetchException("UM-EX020",
					"UserRecentCommentsFetchException", e.getMessage());
		} finally {
			ss.close();
		}
		log.info("UserManagementDAOImpl getUserRecentComments :END");
		return userCommentsList;
	}

	public static List<PopularMentorsDO> getPopularMentorList()
			throws PopularMentorsFetchException {
		log.info("UserManagementDAOImpl getPopularMentorList :START");
		List<PopularMentorsDO> popularMentorList = null;
		PopularMentorsDO popularMentor = null;
		Query query1 = null;
		 Query query2 = null;
		Session ss = HibernateUtil.getSessionFactory().openSession();
		// UserProfileDO uprofileDo = new UserProfileDO();

		try {
			Query query = ss
					.createSQLQuery("SELECT RGSTR_ID,BRANCH_ID,POPULARITY FROM techpedia.usr_mngt_mentor ORDER BY POPULARITY DESC LIMIT 6");
			@SuppressWarnings("unchecked")
			List<Object[]> rows = query.list();

			System.out.println("rows : " + rows.size());

			if (rows != null && !rows.isEmpty()) {

				popularMentorList = new ArrayList<PopularMentorsDO>();

				for (Object[] row : rows) {

					popularMentor = new PopularMentorsDO();
					popularMentor.setMentorId(row[0].toString());
					if (row[1] != null && row[1].toString() != null) {
						popularMentor.setMentorBranch(row[1].toString());
					}
					if (row[2] != null && row[2].toString() != null) {
						popularMentor.setPopualrity(row[2].toString());
					}

					query1 = ss
							.createSQLQuery("select FNAME,MNAME,LNAME from  techpedia.usr_mngt_master where RGSTR_ID = :rgstrId");
					query1.setParameter("rgstrId", row[0].toString());
					@SuppressWarnings("unchecked")
					List<Object[]> masterrows = query1.list();

					UsrMngtContactInfo uContact = (UsrMngtContactInfo) ss.get(
							UsrMngtContactInfo.class,
							new Long(row[0].toString()));

					// System.out.println("Mentor Photo :"+uContact.getPhoto()
					// );

					if (uContact.getPhoto() != null) {
						popularMentor.setMentorImage(uContact.getPhoto()
								.toString());
					}

					/*
					 * query2 = ss .createSQLQuery(
					 * "select PHOTO from  techpedia.usr_mngt_contact_info where RGSTR_ID = :rgstrId"
					 * ); query2.setParameter("rgstrId", row[0].toString());
					 * Object photo = query2.uniqueResult();
					 * System.out.println("photo : " + photo); //if (photo !=
					 * null) { if (photo != null) {
					 * popularMentor.setMentorImage(photo.toString()); }
					 * System.out.println("photo image : " +
					 * popularMentor.getMentorImage()); //}
					 */

					if (masterrows != null && !masterrows.isEmpty()) {
						for (Object[] masterrow : masterrows) {
							if (masterrow[0] != null
									&& masterrow[0].toString() != null) {
								popularMentor.setMentorFirstName(masterrow[0]
										.toString());
							}
							if (masterrow[1] != null
									&& masterrow[1].toString() != null) {
								popularMentor.setMentorMiddleName(masterrow[1]
										.toString());
							}
							if (masterrow[2] != null
									&& masterrow[2].toString() != null) {
								popularMentor.setMentorLastName(masterrow[2]
										.toString());
							}

						}
					}
					query2 = ss
							.createSQLQuery("SELECT PROJ_BRANCH_DESC FROM techpedia.tb_tech001_mast_branch where BRANCH_ID = :branchid");
					query2.setParameter("branchid", popularMentor.getMentorBranch());
					popularMentor.setMentorBranch((String) query2.uniqueResult());
					popularMentorList.add(popularMentor);

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error while fetching getPopularMentorList.");
			throw new PopularMentorsFetchException("UM-EX019",
					"PopularMentorsFetchException", e.getMessage());
		} finally {
			ss.close();
		}
		log.info("UserManagementDAOImpl getPopularMentorList :END");
		return popularMentorList;
	}

	public static String forgotPassword(String email)
			throws UserNotFoundException, CurrentPasswordFetchException {
		log.info("UserManagementDAOImpl forgotPassword :START");
		String currentPassword = null;
		Session ss = HibernateUtil.getSessionFactory().openSession();
		try {
			Query emailquery = ss
					.createSQLQuery("select USR_ID from usr_mngt_master where EMAIL_ID = :emailId");
			emailquery.setParameter("emailId", email);
			if (emailquery.uniqueResult() == null) {
				throw new UserNotFoundException("UM-EX006",
						"UserNotFoundException",
						"No User Found with the given " + email + " Id");
			} else {
				String userID = (String) emailquery.uniqueResult();
				currentPassword = getCurrentPassword(userID);
			}
		} catch (UserNotFoundException e) {
			throw e;
		} catch (PasswordResetException e) {
			throw new CurrentPasswordFetchException("UM-EX021",
					"CurrentPasswordFetchException",
					"Error while fetching current password.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new CurrentPasswordFetchException("UM-EX021",
					"CurrentPasswordFetchException",
					"Error while fetching current password.");
		}
		log.info("UserManagementDAOImpl forgotPassword :END");

		// Sending the Current Password by taking the Email id of user.
		/*
		 * try { UserManagementEmailHelper.sendPasswordFromDAO(email,
		 * currentPassword); } catch (EmailServiceException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */

		return currentPassword;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<SearchForMentorListDO> getSearchListOfMentors(
			Long projId, Long registerID) throws MentorSearchException {
		log.info("UserManagementDAOImpl getSearchListOfMentors :START");

		ArrayList<SearchForMentorListDO> mentorsList = new ArrayList<SearchForMentorListDO>();
		Session ss = HibernateUtil.getSessionFactory().openSession();

		/*
		 * String qryStr =
		 * "select umm.RGSTR_ID, LNAME,MNAME,FNAME, PHOTO,POPULARITY,DEGREE,DESIGNATION,PRO_EXPERIENCE,BRANCH_ID,INSTITUTIONAL_ASSCTN_INFO, "
		 * +
		 * "INT_ON_GRASSRT_INNOVATORS,COMMITMENT_U_BRING_IN from usr_mngt_mentor as umm, "
		 * +
		 * "usr_mngt_contact_info as umci,	usr_mngt_master as master, tb_tech001_mast_projects_brnch as tmpb where umm.BRANCH_ID"
		 * +
		 * " in (select PROJ_BRANCH_ID from tb_tech001_mast_projects_brnch where tmpb.PROJ_ID = :PROJ_ID)  "
		 * +
		 * "and umm.rgstr_id = master.rgstr_id and umm.rgstr_id = umci.rgstr_id order by BRANCH_ID;"
		 * ;
		 */

		String qryStr = "select umm.RGSTR_ID, master.LNAME,master.MNAME,master.FNAME,umci.PHOTO,umm.POPULARITY,umm.DEGREE,umm.DESIGNATION,umm.PRO_EXPERIENCE,umm.BRANCH_ID,umm.INSTITUTIONAL_ASSCTN_INFO, "
				+ "umm.INT_ON_GRASSRT_INNOVATORS,umm.COMMITMENT_U_BRING_IN from usr_mngt_mentor as umm, "
				+ "usr_mngt_contact_info as umci,usr_mngt_master as master where umm.BRANCH_ID"
				+ " in (select PROJ_BRANCH_ID from tb_tech001_mast_projects_brnch where PROJ_ID = :PROJ_ID)"
				+ "and umm.rgstr_id = master.rgstr_id and umm.rgstr_id = umci.rgstr_id order by umm.BRANCH_ID;"; // modified
																													// by
		// Anil

		try {
			SQLQuery qry = ss.createSQLQuery(qryStr);
			qry.setParameter("PROJ_ID", projId);
			// qry.setParameter("registerID", registerID);
			// qry.setFirstResult(1);
			// qry.setMaxResults(100);
			qry.addEntity(MentorDetails.class);
			mentorsList = (ArrayList<SearchForMentorListDO>) qry.list();

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error while fetching Mentor with matching Branch Id.");
			throw new MentorSearchException("UM-EX019",
					"MentorSearchException", e.getMessage());
		} finally {
			if (ss != null)
				ss.close();
		}
		log.info("UserManagementDAOImpl getSearchListOfMentors :END");

		return mentorsList;

	}

	@SuppressWarnings("unchecked")
	public static List<Mentor1n2Details> getMentorsOfProject(Long projId)
			throws ProjectNotFoundException {
		log.info("UserManagementDAOImpl getMentorsOfProject :START");

		MentorsOfProject mop = null;
		MentorsAssignedToProject matp = null;

		List<Mentor1n2Details> mentor1n2Details = new ArrayList<Mentor1n2Details>();

		Session ss = HibernateUtil.getSessionFactory().openSession();

		String strQry = "Select proj_id, PROJ_MENTOR1_ID, PROJ_MENTOR2_ID from tb_tech001_mast_projects_detail where proj_id = :PROJ_ID ";

		String secQry = null;

		try {
			SQLQuery qry = ss.createSQLQuery(strQry);

			qry.setParameter("PROJ_ID", projId);
			qry.setMaxResults(1);
			qry.addEntity(MentorsAssignedToProject.class);

			matp = (MentorsAssignedToProject) qry.uniqueResult();
			if (matp != null) {
				// mop = new MentorsOfProject();

				secQry = "select umm.RGSTR_ID, LNAME,MNAME,FNAME, PHOTO,POPULARITY,DEGREE,DESIGNATION,PRO_EXPERIENCE,BRANCH_ID,INSTITUTIONAL_ASSCTN_INFO, "
						+ "INT_ON_GRASSRT_INNOVATORS,COMMITMENT_U_BRING_IN from usr_mngt_mentor as umm, usr_mngt_master as master, "
						+ "usr_mngt_contact_info as umci where umm.RGSTR_ID in (:mentorId1, :mentorId2)and umm.RGSTR_ID = master.RGSTR_ID and "
						+ "umm.RGSTR_ID = umci.RGSTR_ID;";
				SQLQuery query = ss.createSQLQuery(secQry);
				query.setParameter("mentorId1", matp.getMentorId1());
				query.setParameter("mentorId2", matp.getMentorId2());
				query.addEntity(MentorDetails.class);

				mentor1n2Details = (List<Mentor1n2Details>) query.list();

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("No record for given project id.");
			throw new ProjectNotFoundException("UM-EX020",
					"ProjectNotFoundException", e.getMessage());
		} finally {
			if (ss != null)
				ss.close();
		}
		log.info("UserManagementDAOImpl getMentorsOfProject :END");
		return mentor1n2Details;

	}
	@SuppressWarnings("unchecked")
	public static List<StateListDO> getStateList(String sName)
			throws StateFetchException {
		log.info("UserManagementDAOImpl getStateList :START");
		Session ss = HibernateUtil.getSessionFactory().openSession();
		List<StateListDO> stateList = new ArrayList<StateListDO>();
		try {
			SQLQuery query = ss
					.createSQLQuery("select STATE_NAME from usr_mast_state where STATE_NAME like :sName ");

			query.setParameter("sName", "%" + sName + "%");
			// query.addEntity(UsrCollegeMaster.class);
			stateList = (List<StateListDO>) query.list();

		} catch (Exception e) {
			log.error((new StringBuilder("Error while fetching the Records"))
					.append(e.getMessage()).toString());
			throw new StateFetchException("UMC-EX001",
					"StateFetchException", e.getMessage());
		}

		finally {
			ss.close();
		}

		return stateList;

	}
	
	@SuppressWarnings("unchecked")
	public static List<CityListDO> getCityList(SearchCityDO SearchCityDO)
			throws CityFetchException {
		log.info("UserManagementDAOImpl getCityList :START");
		Session ss = HibernateUtil.getSessionFactory().openSession();
		List<CityListDO> cityList = new ArrayList<CityListDO>();
		try {
			SQLQuery query = ss
					.createSQLQuery("select CITY_NAME from usr_mast_city where STATE_ID IN "
							+ "(select STATE_ID from usr_mast_state where STATE_NAME = :sId ) AND CITY_NAME like :sName  ");

			query.setParameter("sName", "%" + SearchCityDO.getTerm() + "%");
			query.setParameter("sId",  SearchCityDO.getStateName() );
			// query.addEntity(UsrCollegeMaster.class);
			cityList = (List<CityListDO>) query.list();

		} catch (Exception e) {
			log.error((new StringBuilder("Error while fetching the Records"))
					.append(e.getMessage()).toString());
			throw new CityFetchException("UMC-EX002",
					"CityFetchException", e.getMessage());
		}

		finally {
			ss.close();
		}

		return cityList;

	}
	
	@SuppressWarnings("unchecked")
	public static List<CollegeListDO> getCollegesListUser(String cName)
			throws CollegesFetchException {
		log.info("UserManagementDAOImpl getCollegesList :START");
		Session ss = HibernateUtil.getSessionFactory().openSession();
		List<CollegeListDO> ucollegeList = new ArrayList<CollegeListDO>();
		try {
			SQLQuery query = ss
					.createSQLQuery("select College_Name from usr_mngt_college_master where College_Name like :cName ");
				
			query.setParameter("cName", "%" + cName + "%");
			// query.addEntity(UsrCollegeMaster.class);
			ucollegeList = (List<CollegeListDO>) query.list();

		} catch (Exception e) {
			log.error((new StringBuilder("Error while fetching the Records"))
					.append(e.getMessage()).toString());
			throw new CollegesFetchException("UM-EX021",
					"CollegesFetchException", e.getMessage());
		}

		finally {
			ss.close();
		}

		return ucollegeList;

	}
	
	@SuppressWarnings("unchecked")
	public static List<CollegeListDO> getCollegesList(SearchCollegeDO searchCollege)
			throws CollegesFetchException {
		log.info("UserManagementDAOImpl getCollegesList :START");
		Session ss = HibernateUtil.getSessionFactory().openSession();
		List<CollegeListDO> ucollegeList = new ArrayList<CollegeListDO>();
		try {
			SQLQuery query = ss
					.createSQLQuery("select College_Name from usr_mngt_college_master where STATE_ID IN "
							+ "(select STATE_ID from usr_mast_state where STATE_NAME = :sName ) AND College_Name like :cName  ");

			query.setParameter("cName", "%" + searchCollege.getTerm() + "%");
			query.setParameter("sName",  searchCollege.getStateName() );
			ucollegeList = (List<CollegeListDO>) query.list();

		} catch (Exception e) {
			log.error((new StringBuilder("Error while fetching the Records"))
					.append(e.getMessage()).toString());
			throw new CollegesFetchException("UM-EX021",
					"CollegesFetchException", e.getMessage());
		}

		finally {
			ss.close();
		}

		return ucollegeList;

	}

	@SuppressWarnings("unchecked")
	public static List<UniversityListDO> getUniversitiesList(String uName)
			throws UniversitiesFetchException {
		log.info("UserManagementDAOImpl getUniversitiesList :START");
		Session ss = HibernateUtil.getSessionFactory().openSession();
		List<UniversityListDO> universityList = new ArrayList<UniversityListDO>();
		try {
			System.out.println("Uname " + uName);
			SQLQuery query = ss
					.createSQLQuery("select University_Name from USR_UNIVERSITY_MASTER  where University_Name like :uName");
			query.setParameter("uName", "%" + uName + "%");
			System.out.println("query: " + query.getQueryString() + " Query1: "
					+ query.getNamedParameters());

			// query.list()
			universityList = (List<UniversityListDO>) query.list();

		} catch (Exception e) {
			log.error((new StringBuilder("Error while fetching the Records"))
					.append(e.getMessage()).toString());
			throw new UniversitiesFetchException("UM-EX022",
					"UniversitiesFetchException", e.getMessage());
		}

		finally {
			ss.close();
		}

		return universityList;
	}
	
	public static boolean validateAdmin(Long registerID)
			throws UserNotFoundException {
		log.info("UserManagementDAOImpl validateAdmin :START");
		List result = null;
		String user = "";
		boolean flag = false;
		try {
			Session ss = HibernateUtil.getSessionFactory().openSession();
			SQLQuery query = ss
					.createSQLQuery("select type from techpedia.usr_mngt_master where rgstr_ID = :registerID");
			query.setParameter("registerID", registerID);
			result = query.list();
			user = result.get(0).toString();
			if ("Admin".equalsIgnoreCase(user)) {
				flag = true;
				log.debug("Admin User");
			}
		} catch (Exception e) {
			log.error((new StringBuilder("Error while fetching the Records"))
					.append(e.getMessage()).toString());
			throw new UserNotFoundException("UM-EX023",
					"UserNotFoundException", e.getMessage());
		}
		return flag;
	}

	public static boolean updateAddFacultyProfileHelper(
			UserProfileDO userprofile) throws ProfileNotFoundException,
			ProfileUpdateException, ChiperEncryptException {
		log.info("UserManagementDAOImpl updateAddFacultyProfileHelper :START");
		boolean retval = false;
		String isActive = "Y";
		String cVerification = "1";
		Date activated_date = new Date();
		String createdDate = getCurrentTimeStamp();
		// String encryPassword
		// =ChiperUtils.encrypt2(userprofile.getPassword());
		Session ss = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		UsrMngtMaster uMaster = null;
		try {
			tx = ss.beginTransaction();

			uMaster = (UsrMngtMaster) ss.get(UsrMngtMaster.class,
					userprofile.getRgstrId());
			if (uMaster == null) {
				log.error("Record NOt found Exception for the Given Register Id "
						+ userprofile.getRgstrId());
				throw new ProfileNotFoundException("UM-EX002",
						"ProfileNotFoundException",
						"Profile Not found for the given Register Id");
			} else {
				uMaster.setpFname(userprofile.getFirstName());
				uMaster.setmName(userprofile.getMidName());
				uMaster.setlName(userprofile.getLastName());
				uMaster.setdOb(userprofile.getDob());
				uMaster.setiSactive(isActive);
				uMaster.setcVerify(cVerification);
				uMaster.setType(userprofile.getUserType());
				uMaster.setUserId(userprofile.getUserName());
				uMaster.setFbId(userprofile.getFaceBookId());
				uMaster.setRgstrDate(createdDate);
				uMaster.setActivatedDate(activated_date);
				uMaster.setEmail(userprofile.getEmail());

				UsrMngtAddress uAddress = new UsrMngtAddress(
						userprofile.getRgstrId(), userprofile.getAddrLn1(),
						userprofile.getAddrLn2(), userprofile.getCity(),
						userprofile.getDistrict(), userprofile.getState(),
						userprofile.getCountry(), userprofile.getPincode());

				// UsrMngtContactInfo uContactInfo = new
				// UsrMngtContactInfo(userprofile.getRgstrId(),
				// userprofile.getMobile(), userprofile.getHomePhoneNo(),
				// userprofile.getPhoto());
				/*
				 * int rstrId=(int)userprofile.getRgstrId(); UsrMngtPassword
				 * uPassword = (UsrMngtPassword)
				 * ss.get(UsrMngtPassword.class,rstrId);
				 * System.out.println("User Name: "+userprofile.getUserName());
				 * uPassword.setUsrId(userprofile.getUserName());
				 * uPassword.setCreatedDate(createdDate);
				 */
				ss.update("0", uMaster);
				ss.update("1", uAddress);
				// ss.update("2", uContactInfo);
				// ss.update("3", uPassword);

				Query pwdQuery = ss
						.createSQLQuery("UPDATE techpedia.usr_mngt_passwd  SET USR_ID=:username WHERE RGSTR_ID=:rgstrId");
				pwdQuery.setParameter("username", userprofile.getUserName());
				pwdQuery.setParameter("rgstrId", userprofile.getRgstrId());
				pwdQuery.executeUpdate();

				Query query = ss
						.createSQLQuery("update techpedia.usr_mngt_contact_info set MOBILE_NO = :mobile,HOME_PHONE_NO = :homeNumber where RGSTR_ID = :rgstrId");
				query.setParameter("homeNumber", userprofile.getHomePhoneNo());
				query.setParameter("mobile", userprofile.getMobile());
				query.setParameter("rgstrId", userprofile.getRgstrId());
				query.executeUpdate();

				if (userprofile.getUserType().equalsIgnoreCase("Student")) {

					UsrMngtStudent uStudent = new UsrMngtStudent(
							uMaster.getRgstrId(),
							userprofile.getDegreeOfStudent(), // Modified by
																// Anil
							userprofile.getCollge(),
							userprofile.getUniversity(),// added by Anil
							// userprofile.getAlumni(),
							userprofile.getStudentID(),
							userprofile.getCompletionYear(),
							userprofile.getBranchIdOfStudent());
					ss.update("4", uStudent);

				} else if (userprofile.getUserType()
						.equalsIgnoreCase("college")) {
					UsrMngtCollege uCollege = new UsrMngtCollege(
							userprofile.getRgstrId(), userprofile.getWebpage(),
							userprofile.getLogo(),
							userprofile.getCollegeName(),
							userprofile.getCollegeDesc(),
							userprofile.getPrinicipalName(),
							userprofile.getPrinicipalEmail(),
							userprofile.getFacilitiesOffrdToStudents(),
							userprofile.getCntctInfoForNatnlInnovnClub(),
							userprofile.getAffltUniversityOfCollege(),
							userprofile.getTechpdaFactlyCoordtr());
					ss.update("5", uCollege);

				} else if (userprofile.getUserType()
						.equalsIgnoreCase("faculty")) {

					UsrMngtFaculty uFaculty = new UsrMngtFaculty(
							userprofile.getRgstrId(),
							userprofile.getDegreeOfFaculty(),
							userprofile.getCollgeOfFaculty(),
							userprofile.getSpecializationOfFaculty(),
							userprofile.getUniversityOfFaculty(),
							userprofile.getAlumni(),
							userprofile.getMemshipInAssocns(),
							userprofile.getPsnlWebpgLink(),
							userprofile.getProffesionalExpOfFaculty(),
							userprofile.getAffltUniversityOfFaculty(),
							userprofile.getBranchIdOfFaculty());
					ss.update("6", uFaculty);

				} else if (userprofile.getUserType().equalsIgnoreCase("mentor")) {
					UsrMngtMentor uMentor = new UsrMngtMentor(
							userprofile.getRgstrId(),
							userprofile.getDegreeOfMentor(),
							userprofile.getDesignationOfMentor(),
							userprofile.getBranchIdOfMentor(),
							userprofile.getInstitutionalAssctnInfo(),
							userprofile.getProfessionalExperience(),
							userprofile.getTimeUspaceForMentoringPerMnth(),
							userprofile.getMentorProfile(),
							userprofile.getExpectationFromMentor(),
							userprofile.getCommitmentUBringIn(),
							userprofile.getWebpage(),
							userprofile.getIntOnGrassrtInnovators(),
							userprofile.getPopularity());
					System.out.println("Branch id="
							+ userprofile.getBranchIdOfMentor());
					ss.update("7", uMentor);

				} else if (userprofile.getUserType().equalsIgnoreCase(
						"industry")) {

					UsrMngtIndustry uIndustry = new UsrMngtIndustry(
							userprofile.getRgstrId(), userprofile.getFax(),
							userprofile.getContactNameOfIndustry(),
							userprofile.getContactEmailOfIndustry(),
							userprofile.getOperatnSectr(),
							userprofile.getKindOfSprtUProvideInnovtr(),
							userprofile.getPrdRng(),
							userprofile.getAssociateIndustry(),
							userprofile.getTechngyExprtizOffrToOthers(),
							userprofile.getSolnReqrdForTechnlgicalChlngs(),
							userprofile.getIntrstToPoseInnovtnChlngAwrds());
					ss.update("8", uIndustry);

				}
				tx.commit();
				retval = true;
			}

		} catch (ProfileNotFoundException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			log.error((new StringBuilder(
					"Error while updating the transaction :")).append(
					e.getMessage()).toString());
			try {
				tx.rollback();
			} catch (Exception ee) {
				ee.printStackTrace();
				log.error((new StringBuilder(
						"Error while doing rollback to the failed transaction :"))
						.append(e.getMessage()).toString());
			}
			throw new ProfileUpdateException("UM-EX003",
					"ProfileUpdateException", e.getMessage());

		}

		finally {
			tx = null;
			ss.close();
		}
		log.info("UserManagementDAOImpl updateAddFacultyProfileHelper :END");
		return retval;
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList<RecentNewsDO> getRecentNews()
			throws GetRecentNewsException {
		//log.debug("ProjectDaoImpl.getAllProject :Start");
		
		String sql = "SELECT * FROM COLLEGE_RECENT_NEWS ORDER BY NEWS_DATE DESC LIMIT 5";
		ArrayList<RecentNewsDO> recentNewsList =new ArrayList<RecentNewsDO>();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
	
		try {
			recentNewsList = (ArrayList<RecentNewsDO>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(RecentNewsDO.class));
			
			if(recentNewsList.size()==0)
				throw new GetRecentNewsException("No News available for this criteria");
		} catch (Exception e) {
			//log.debug("Error while deleting project : "+ e.getMessage());
			throw new GetRecentNewsException("Error while getting Recent News : "+ e.getMessage());
		}
		//log.debug("ProjectDaoImpl.getAllProject :End");
		return recentNewsList;
	}
	
}
