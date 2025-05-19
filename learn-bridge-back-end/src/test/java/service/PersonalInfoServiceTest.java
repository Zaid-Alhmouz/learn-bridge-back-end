package service;

import com.learnbridge.learn_bridge_back_end.dao.InstructorDAO;
import com.learnbridge.learn_bridge_back_end.dao.LearnerDAO;
import com.learnbridge.learn_bridge_back_end.dao.UserDAO;
import com.learnbridge.learn_bridge_back_end.dto.PersonalInfoDTO;
import com.learnbridge.learn_bridge_back_end.entity.Instructor;
import com.learnbridge.learn_bridge_back_end.entity.Learner;
import com.learnbridge.learn_bridge_back_end.entity.User;
import com.learnbridge.learn_bridge_back_end.entity.UserRole;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.service.PersonalInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PersonalInfoServiceTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private InstructorDAO instructorDAO;

    @Mock
    private LearnerDAO learnerDAO;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PersonalInfoService personalInfoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEditPersonalInfo_UserNotFound() {
        SecurityUser loggedUser = mock(SecurityUser.class);
        User user = mock(User.class);
        when(loggedUser.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(userDAO.findUserById(1L)).thenReturn(null);

        PersonalInfoDTO dto = new PersonalInfoDTO();

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                personalInfoService.editPersonalInfo(dto, loggedUser)
        );
        assertEquals("User not found for id: 1", exception.getMessage());
    }

    @Test
    void testEditPersonalInfo_EmailAlreadyInUse() {
        SecurityUser loggedUser = mock(SecurityUser.class);
        User user = new User();
        setPrivateField(user, "userId", 1L);
        user.setEmail("old@example.com");

        when(loggedUser.getUser()).thenReturn(user);
        when(userDAO.findUserById(1L)).thenReturn(user);
        when(userDAO.findUserByEmail("new@example.com")).thenReturn(new User());

        PersonalInfoDTO dto = new PersonalInfoDTO();
        dto.setEmail("new@example.com");

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                personalInfoService.editPersonalInfo(dto, loggedUser)
        );
        assertEquals("The email is already in use by another account.", exception.getMessage());
    }

    @Test
    void testEditPersonalInfo_UpdatePassword() {
        SecurityUser loggedUser = mock(SecurityUser.class);
        User user = new User();
        setPrivateField(user, "userId", 1L);
        user.setEmail("old@example.com");

        when(loggedUser.getUser()).thenReturn(user);
        when(userDAO.findUserById(1L)).thenReturn(user);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        PersonalInfoDTO dto = new PersonalInfoDTO();
        dto.setPassword("newPassword");

        personalInfoService.editPersonalInfo(dto, loggedUser);

        assertEquals("encodedPassword", user.getPassword());
        verify(userDAO, times(1)).updateUser(user);
    }

    @Test
    void testEditPersonalInfo_UpdateLearnerDetails() {
        SecurityUser loggedUser = mock(SecurityUser.class);
        User user = new User();
        setPrivateField(user, "userId", 1L);
        user.setUserRole(UserRole.LEARNER);

        when(loggedUser.getUser()).thenReturn(user);
        when(userDAO.findUserById(1L)).thenReturn(user);

        Learner learner = new Learner();
        when(learnerDAO.findLearnerById(1L)).thenReturn(learner);

        // Prepare Base64 string for bytes {1,2,3,4}
        byte[] imgBytes = {1, 2, 3, 4};
        String b64 = Base64.getEncoder().encodeToString(imgBytes);
        PersonalInfoDTO dto = new PersonalInfoDTO();
        dto.setFirstName("NewFirstName");
        dto.setLastName("NewLastName");
        dto.setFavouriteCategory("NewCategory");
        dto.setPersonalImage("data:image/jpeg;base64," + b64);

        personalInfoService.editPersonalInfo(dto, loggedUser);

        assertEquals("NewFirstName", learner.getFirstName());
        assertEquals("NewLastName", learner.getLastName());
        assertEquals("NewCategory", learner.getFavouriteCategory());
        assertArrayEquals(imgBytes, learner.getPersonalImage());

        verify(learnerDAO, times(1)).updateLearner(learner);
    }

    @Test
    void testEditPersonalInfo_UpdateInstructorDetails() {
        SecurityUser loggedUser = mock(SecurityUser.class);
        User user = new User();
        setPrivateField(user, "userId", 1L);
        user.setUserRole(UserRole.INSTRUCTOR);

        when(loggedUser.getUser()).thenReturn(user);
        when(userDAO.findUserById(1L)).thenReturn(user);

        Instructor instructor = new Instructor();
        when(instructorDAO.findInstructorById(1L)).thenReturn(instructor);

        byte[] imgBytes = {5, 6, 7, 8};
        String b64 = Base64.getEncoder().encodeToString(imgBytes);
        PersonalInfoDTO dto = new PersonalInfoDTO();
        dto.setFirstName("NewFirstName");
        dto.setLastName("NewLastName");
        dto.setFavouriteCategory("NewCategory");
        dto.setUniversityInfo("NewUniversity");
        dto.setBio("NewBio");
        dto.setAvgPrice(new BigDecimal("100"));
        dto.setPersonalImage("data:image/jpeg;base64," + b64);

        personalInfoService.editPersonalInfo(dto, loggedUser);

        assertEquals("NewFirstName", instructor.getFirstName());
        assertEquals("NewLastName", instructor.getLastName());
        assertEquals("NewCategory", instructor.getFavouriteCategory());
        assertEquals("NewUniversity", instructor.getUniversityInfo());
        assertEquals("NewBio", instructor.getInstructorBio());
        assertEquals(new BigDecimal("100"), instructor.getAvgPrice());
        assertArrayEquals(imgBytes, instructor.getInstructorImage());

        verify(instructorDAO, times(1)).updateInstructor(instructor);
    }

    // Helper method to set private fields via reflection
    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
