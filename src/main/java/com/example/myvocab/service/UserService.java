package com.example.myvocab.service;

import com.example.myvocab.dto.UserCourseInfo;
import com.example.myvocab.dto.UserPersonalDto;
import com.example.myvocab.exception.BadRequestException;
import com.example.myvocab.exception.NotFoundException;
import com.example.myvocab.model.*;
import com.example.myvocab.model.Package;
import com.example.myvocab.model.enummodel.OrderStatus;
import com.example.myvocab.model.enummodel.RoleUser;
import com.example.myvocab.repo.*;
import com.example.myvocab.request.SignUpRequest;
import com.example.myvocab.request.UpdatePasswordRequest;
import com.example.myvocab.security.UserDetailsCustom;
import com.example.myvocab.util.Validation;
import net.bytebuddy.utility.RandomString;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private RolesRepo rolesRepo;

    @Autowired
    private UserRoleRepo userRoleRepo;

    @Autowired
    private UserTopicRepo userTopicRepo;

    @Autowired
    private UserCourseRepo userCourseRepo;

    @Autowired
    private PackageRepo packageRepo;
    @Autowired
    private OrdersRepo ordersRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private FileService fileService;

    @Autowired
    private JavaMailSender mailSender;

    public UserPersonalDto getUserInfoFromUserCustom(UserDetailsCustom userDetailsCustom) {
        UserPersonalDto usersInfo = modelMapper.map(userDetailsCustom.getUser(), UserPersonalDto.class);
        return usersInfo;
    }

    public UserPersonalDto getUserInfoFromId(String userId) {
        Optional<Users> o_user = usersRepo.findById(userId);
        if (o_user.isEmpty()) {
            throw new NotFoundException("Không tìm thấy user có id =" + userId);
        }
        return modelMapper.map(o_user.get(), UserPersonalDto.class);
    }

    public void register(SignUpRequest request, String siteURL) throws UnsupportedEncodingException, MessagingException {
        handleUserSignUpException(request);
        String encodedPass = passwordEncoder.encode(request.getPassword());
        String randomCode = RandomString.make(64);
        Users user = Users.builder()
                .avatar("/asset/img/home/header/default_avt.png")
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(encodedPass)
                .userRoles(new ArrayList<>())
                .verificationCode(randomCode)
                .enabled(false)
                .build();
        Users savedUser = usersRepo.save(user);

        //create user role normal
        Roles role = rolesRepo.findByName("USER_NORMAL").get();
        UserRole userRole = UserRole.builder()
                .role(role)
                .user(savedUser)
                .build();

        UserRole savedUserRole = userRoleRepo.save(userRole);

        savedUser.addUserRole(savedUserRole);
        usersRepo.save(savedUser);

        //Gửi mail
        sendVerificationEmail(savedUser, siteURL);

    }

    public void handleUserSignUpException(SignUpRequest request) {
        Optional<Users> o_user = usersRepo.findByEmail(request.getEmail());
        if (o_user.isPresent()) {
            throw new BadRequestException("Email đã được sử dụng.");
        }

        if (!Validation.isValidEmail(request.getEmail())) {
            throw new BadRequestException("Email không hợp lệ.");
        }

        if (!Validation.isValidPassword(request.getPassword())) {
            throw new BadRequestException("Password cần dài từ 8 đến 16 ký tự.");
        }

        if (request.getFullName().length() == 0 || request.getPassword().length() == 0 || request.getEmail().length() == 0) {
            throw new BadRequestException("Bạn chưa nhập đủ thông tin");
        }
    }

    public void sendVerificationEmail(Users user, String siteURL) throws MessagingException, UnsupportedEncodingException {

        String toAddress = user.getEmail();
        String fromAddress = "vocabulary.learned@gmail.com";
        String senderName = "VOCABULARY.COM";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Vocabulary.com";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getFullName());
        String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);
        helper.setText(content, true);
        mailSender.send(message);
    }

    public boolean verifySignUp(String verificationCode) {
        Users user = usersRepo.findByVerificationCode(verificationCode);

        if (user == null || user.isEnabled()) {
            return false;
        } else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            usersRepo.save(user);
            return true;
        }
    }


    public List<UserTopic> getUserTopicsByUser(String idUser) {
        return userTopicRepo.findByUserCourse_User_Id(idUser);

    }

    public List<UserCourseInfo> getUserCourseByUser(String userId) {
        return userCourseRepo.findByUser_IdOrderByStudiedAtDesc(userId);
    }

    public List<UserCourseInfo> getUserCourseByUserAndCategory(String userId, Long categoryId) {
        return userCourseRepo.findByUser_IdAndCourse_Category_IdOrderByStudiedAtDesc(userId, categoryId);
    }

    public boolean checkIfValidOldPassword(Users user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    public void changeUserPassword(Users user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        usersRepo.save(user);
    }

    public void handleChangeUserPassword(Users user, UpdatePasswordRequest request) {
        if (!checkIfValidOldPassword(user, request.getOldPassword())) {
            throw new BadRequestException("Mật khẩu cũ không chính xác");
        }
        changeUserPassword(user, request.getNewPassword());
    }


    public void handleUploadUserAvatar(Users user, MultipartFile file) {
        String uploadDir = "upload/img/user/";
        // String uploadDir = "src/main/resources/static/asset/img/user/";// Lưu vào static
        String fileName = user.getId() + file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);

        fileService.uploadFile(uploadDir, fileName, file);
        user.setAvatar("/upload/img/user/" + fileName);
        //user.setAvatar("/asset/img/user/" + fileName);
        usersRepo.save(user);
    }

    public void updateUserInfo(String phone, String fullName, Users user) {
        user.setFullName(fullName);
        user.setPhone(phone);
        usersRepo.save(user);
    }

    public Orders createPendingOrder(Users user, Long idPackage) {
        Optional<Package> o_package = packageRepo.findById(idPackage);
        if (o_package.isEmpty()) {
            throw new NotFoundException("Không tìm thấy package có id= " + idPackage);
        }
        Orders order = Orders.builder()
                .status(OrderStatus.PENDING)
                .user(user)
                .aPackage(o_package.get())
                .build();
        Orders savedOrder = ordersRepo.save(order);
        return savedOrder;

    }

    public boolean isNormalUser() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .filter(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER_NORMAL"))
                .findFirst()
                .isPresent();
    }

    public boolean isVIPUser() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .filter(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER_VIP"))
                .findFirst()
                .isPresent();
    }

    public Page<Users> listAllUsersByPage(int pageNum) {
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        return usersRepo.findAll(pageable);

    }

    public void checkExpiredUserRole(String userEmail) {
        Optional<Users> o_user = usersRepo.findByEmail(userEmail);
        if (o_user.isPresent()) {
            Users user = o_user.get();
            Roles role = rolesRepo.findByName("USER_VIP").get();
            Optional<UserRole> o_userRole = userRoleRepo.findByRole_IdAndUser_Id(role.getId(), user.getId());

            if (o_userRole.isPresent()) {
                UserRole userRole = o_userRole.get();
                List<Orders> orders = ordersRepo.findByUser_IdAndStatus(user.getId(), OrderStatus.ACTIVATED, Orders.class);
                Orders order = orders.get(0);

                LocalDate expiredDate = order.getActiveDate().plusDays(30 * order.getAPackage().getDuration());
                LocalDate today = LocalDate.now();

                // check if order is already expired
                if (expiredDate.isBefore(today)) {
                    Roles normalRole = rolesRepo.findByName("USER_NORMAL").get(); //change role of user
                    userRole.setRole(normalRole);
                    order.setStatus(OrderStatus.EXPIRED);// change status of order
                }
            }
        }

    }


    public void resetUserPassword(String email) throws MessagingException, UnsupportedEncodingException {
        if (!Validation.isValidEmail(email)) {
            return;
        }
        Optional<Users> o_user = usersRepo.findByEmail(email);
        if (o_user.isEmpty()) {
            return;
        }
        Users user = o_user.get();
        String newPassword = RandomString.make(10);
        user.setPassword(passwordEncoder.encode(newPassword));
        Users saveUser = usersRepo.save(user);

        sendResetPasswordEmail(saveUser, newPassword);
    }

    public void sendResetPasswordEmail(Users user, String newPassword) throws MessagingException, UnsupportedEncodingException {

        String toAddress = user.getEmail();
        String fromAddress = "vocabulary.learned@gmail.com";
        String senderName = "VOCABULARY.COM";
        String subject = "Reset password";
        String content = "Dear [[name]],<br>"
                + "Mật khẩu mới của bạn là: [[password]]<br>"
                + "Trân trọng,<br>"
                + "Vocabulary.com";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getFullName());
        content = content.replace("[[password]]", newPassword);
        helper.setText(content, true);
        mailSender.send(message);


    }

    public boolean isUserOrderPendingExist(String userId){
        return ordersRepo.existsByUser_IdAndStatus(userId,OrderStatus.PENDING);
    }


}
