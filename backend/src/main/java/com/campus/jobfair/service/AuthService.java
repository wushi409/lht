package com.campus.jobfair.service;

import com.campus.jobfair.dto.AuthDtos.AuthResponse;
import com.campus.jobfair.dto.AuthDtos.CodeLoginRequest;
import com.campus.jobfair.dto.AuthDtos.CompanyRegisterRequest;
import com.campus.jobfair.dto.AuthDtos.LoginRequest;
import com.campus.jobfair.dto.AuthDtos.SendCodeRequest;
import com.campus.jobfair.dto.AuthDtos.StudentRegisterRequest;
import com.campus.jobfair.dto.ResetPasswordRequest;

import com.campus.jobfair.entity.Company;
import com.campus.jobfair.entity.Student;
import com.campus.jobfair.entity.UserAccount;
import com.campus.jobfair.entity.enums.CompanyStatus;
import com.campus.jobfair.entity.enums.UserRole;
import com.campus.jobfair.entity.enums.VerificationScene;

import com.campus.jobfair.repository.CompanyRepository;
import com.campus.jobfair.repository.StudentRepository;
import com.campus.jobfair.repository.UserAccountRepository;
import com.campus.jobfair.security.CustomUserDetails;
import com.campus.jobfair.security.JwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final VerificationCodeService verificationCodeService;

    public AuthService(UserAccountRepository userAccountRepository,
                       StudentRepository studentRepository,
                       CompanyRepository companyRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       VerificationCodeService verificationCodeService) {
        this.userAccountRepository = userAccountRepository;
        this.studentRepository = studentRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.verificationCodeService = verificationCodeService;
    }

    @Transactional
    public AuthResponse registerStudent(StudentRegisterRequest req) {
        if (userAccountRepository.existsByUsername(req.getStudentNo()) || studentRepository.existsByStudentNo(req.getStudentNo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "学号已存在");
        }
        Student student = new Student();
        student.setStudentNo(req.getStudentNo());
        student.setName(req.getName());
        student.setCollege(req.getCollege());
        student.setPhone(req.getPhone());
        student.setEmail(req.getEmail());
        studentRepository.save(student);

        UserAccount account = new UserAccount();
        account.setUsername(req.getStudentNo());
        account.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        account.setRole(UserRole.STUDENT);
        account.setStudent(student);
        userAccountRepository.save(account);

        String token = jwtService.generateToken(new CustomUserDetails(account));
        return new AuthResponse(token, account.getRole().name());
    }

    @Transactional
    public AuthResponse registerCompany(CompanyRegisterRequest req) {
        if (companyRepository.existsByCreditCode(req.getCreditCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "统一社会信用代码已存在");
        }
        Company company = new Company();
        company.setName(req.getName());
        company.setCreditCode(req.getCreditCode());
        company.setScale(req.getScale());
        company.setIndustry(req.getIndustry());
        company.setDescription(req.getDescription());
        company.setContactName(req.getContactName());
        company.setContactPhone(req.getContactPhone());
        company.setContactEmail(req.getContactEmail());
        company.setStatus(CompanyStatus.PENDING);
        companyRepository.save(company);

        UserAccount account = new UserAccount();
        account.setUsername(req.getCreditCode());
        account.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        account.setRole(UserRole.COMPANY);
        account.setCompany(company);
        // 企业审核通过前不可用
        account.setActive(false);
        userAccountRepository.save(account);

        String token = jwtService.generateToken(new CustomUserDetails(account));
        return new AuthResponse(token, account.getRole().name());
    }

    public AuthResponse login(LoginRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(principal);
        return new AuthResponse(token, principal.getRole().name());
    }

    private UserAccount findAccountByPrincipal(String username) {
        // 先按用户名查找账号（学号、统一社会信用代码、管理员账号等）
        return userAccountRepository.findByUsername(username)
                .orElseGet(() -> {
                    // 再尝试按学生手机号查找
                    Student student = studentRepository.findByPhone(username)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "账号不存在"));
                    UserAccount account = student.getAccount();
                    if (account == null) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "账号不存在");
                    }
                    return account;
                });
    }

    @Transactional
    public String sendCode(SendCodeRequest req) {
        VerificationScene scene;
        try {
            scene = VerificationScene.valueOf(req.getScene().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不支持的验证码场景");
        }
        // 这里只生成验证码并记录，实际项目中应通过短信/邮件发送
        return verificationCodeService.generate(req.getUsername(), scene);
    }

    @Transactional
    public AuthResponse codeLogin(CodeLoginRequest req) {
        // 校验验证码
        verificationCodeService.validate(req.getUsername(), VerificationScene.LOGIN, req.getCode());

        UserAccount account = findAccountByPrincipal(req.getUsername());
        if (!account.isActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "账号未激活或已被禁用");
        }
        String token = jwtService.generateToken(new CustomUserDetails(account));
        return new AuthResponse(token, account.getRole().name());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest req) {
        // 校验重置密码场景验证码
        verificationCodeService.validate(req.getUsername(), VerificationScene.RESET_PASSWORD, req.getCode());

        UserAccount account = findAccountByPrincipal(req.getUsername());
        account.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        userAccountRepository.save(account);
    }
}