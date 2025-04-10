//package com.learnbridge.learn_bridge_back_end.service;
//
//import com.learnbridge.learn_bridge_back_end.dao.UserDAO;
//import com.learnbridge.learn_bridge_back_end.entity.User;
//import com.learnbridge.learn_bridge_back_end.entity.UserRole;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.UUID;
//
//@Service
//public class CustomOAuth2UserService extends DefaultOAuth2UserService {
//
//    private final UserDAO userDAO;
//    private final PasswordEncoder passwordEncoder;
//
//    @Autowired
//    public CustomOAuth2UserService(UserDAO userDAO, @Lazy PasswordEncoder passwordEncoder) {
//        this.userDAO = userDAO;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException
//    {
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//
//        String provider = userRequest.getClientRegistration().getRegistrationId();
//        String email = oAuth2User.getAttribute("email");
//
//        User user = userDAO.findUserByEmail(email);
//
//        if (user == null) {
//
//            // Create a new user with a random password
//            user = new User();
//            user.setEmail(email);
//            user.setName(oAuth2User.getAttribute("name"));
//            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // We use a DUMMY password here just to keep the data storing in the table safe (password not null constraint) as we will not use it for authentication
//
//            user.setUserRole(UserRole.LEARNER);
//
//            userDAO.saveUser(user);
//        }
//
//        return createOAuth2User(user, oAuth2User);
//    }
//
//    private OAuth2User createOAuth2User(User user, OAuth2User oAuth2User) {
//
//        // Convert your UserRole to authorities
//        List<GrantedAuthority> authorities = Collections.singletonList(
//                new SimpleGrantedAuthority(user.getUserRole().name())
//        );
//
//        // Return a proper 0Auth2User implementation
//        return new DefaultOAuth2User(
//                authorities,
//                oAuth2User.getAttributes(),
//                "email"
//        );
//    }
//}
