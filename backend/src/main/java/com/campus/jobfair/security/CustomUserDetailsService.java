package com.campus.jobfair.security;

import com.campus.jobfair.entity.Student;
import com.campus.jobfair.entity.UserAccount;
import com.campus.jobfair.repository.StudentRepository;
import com.campus.jobfair.repository.UserAccountRepository;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;
    private final StudentRepository studentRepository;

    public CustomUserDetailsService(UserAccountRepository userAccountRepository,
                                    StudentRepository studentRepository) {
        this.userAccountRepository = userAccountRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 先按用户名查找
        Optional<UserAccount> accountOpt = userAccountRepository.findByUsername(username);
        if (accountOpt.isPresent()) {
            return new CustomUserDetails(accountOpt.get());
        }

        // 支持学生通过手机号登录：根据手机号反查学生，再取其账号
        Student student = studentRepository.findByPhone(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        UserAccount account = student.getAccount();
        if (account == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return new CustomUserDetails(account);
    }
}
