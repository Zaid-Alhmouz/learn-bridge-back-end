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
        // Arrange
        SecurityUser loggedUser = mock(SecurityUser.class);
        User user = mock(User.class);
        when(loggedUser.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(userDAO.findUserById(1L)).thenReturn(null);

        PersonalInfoDTO dto = new PersonalInfoDTO();

        // Act and Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                personalInfoService.editPersonalInfo(dto, loggedUser)
        );
        assertEquals("User not found for id: 1", exception.getMessage());
    }

    @Test
    void testEditPersonalInfo_EmailAlreadyInUse() {
        // Arrange
        SecurityUser loggedUser = mock(SecurityUser.class);
        User user = new User();
        setPrivateField(user, "userId", 1L); // Set the ID
        user.setEmail("old@example.com");

        when(loggedUser.getUser()).thenReturn(user);
        when(userDAO.findUserById(1L)).thenReturn(user);

        User userWithNewEmail = new User(); // Simulates another user with the same email
        when(userDAO.findUserByEmail("new@example.com")).thenReturn(userWithNewEmail);

        PersonalInfoDTO dto = new PersonalInfoDTO();
        dto.setEmail("new@example.com");

        // Act and Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                personalInfoService.editPersonalInfo(dto, loggedUser)
        );
        assertEquals("The email is already in use by another account.", exception.getMessage());
    }

    @Test
    void testEditPersonalInfo_UpdatePassword() {
        // Arrange
        SecurityUser loggedUser = mock(SecurityUser.class);
        User user = new User();
        setPrivateField(user, "userId", 1L); // Set the ID
        user.setEmail("old@example.com");

        when(loggedUser.getUser()).thenReturn(user);
        when(userDAO.findUserById(1L)).thenReturn(user);

        PersonalInfoDTO dto = new PersonalInfoDTO();
        dto.setPassword("newPassword");

        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        // Act
        personalInfoService.editPersonalInfo(dto, loggedUser);

        // Assert
        assertEquals("encodedPassword", user.getPassword());
        verify(userDAO, times(1)).updateUser(user);
    }

    @Test
    void testEditPersonalInfo_UpdateLearnerDetails() {
        // Arrange
        SecurityUser loggedUser = mock(SecurityUser.class);
        User user = new User();
        setPrivateField(user, "userId", 1L); // Set the ID
        user.setUserRole(UserRole.LEARNER);

        when(loggedUser.getUser()).thenReturn(user);
        when(userDAO.findUserById(1L)).thenReturn(user);

        Learner learner = new Learner();
        when(learnerDAO.findLearnerById(1L)).thenReturn(learner);

        PersonalInfoDTO dto = new PersonalInfoDTO();
        dto.setFirstName("NewFirstName");
        dto.setLastName("NewLastName");
        dto.setFavouriteCategory("NewCategory");
        dto.setPersonalImage(new byte[]{1, 2, 3, 4}); // Updated to byte[]
        user.setEmail("new@example.com");

        // Act
        personalInfoService.editPersonalInfo(dto, loggedUser);

        // Assert
        assertEquals("NewFirstName", learner.getFirstName());
        assertEquals("NewLastName", learner.getLastName());
        assertEquals("NewCategory", learner.getFavouriteCategory());
        assertArrayEquals(new byte[]{1, 2, 3, 4}, learner.getPersonalImage()); // Updated assertion for byte[]

        verify(learnerDAO, times(1)).updateLearner(learner);
    }

    @Test
    void testEditPersonalInfo_UpdateInstructorDetails() {
        // Arrange
        SecurityUser loggedUser = mock(SecurityUser.class);
        User user = new User();
        setPrivateField(user, "userId", 1L); // Set the ID
        user.setUserRole(UserRole.INSTRUCTOR);

        when(loggedUser.getUser()).thenReturn(user);
        when(userDAO.findUserById(1L)).thenReturn(user);

        Instructor instructor = new Instructor();
        when(instructorDAO.findInstructorById(1L)).thenReturn(instructor);

        PersonalInfoDTO dto = new PersonalInfoDTO();
        dto.setFirstName("NewFirstName");
        dto.setLastName("NewLastName");
        dto.setFavouriteCategory("NewCategory");
        dto.setUniversityInfo("NewUniversity");
        dto.setBio("NewBio");
        dto.setAvgPrice(new BigDecimal("100"));
        dto.setPersonalImage(new byte[]{5, 6, 7, 8}); // Updated to byte[]
        user.setEmail("new@example.com");

        // Act
        personalInfoService.editPersonalInfo(dto, loggedUser);

        // Assert
        assertEquals("NewFirstName", instructor.getFirstName());
        assertEquals("NewLastName", instructor.getLastName());
        assertEquals("NewCategory", instructor.getFavouriteCategory());
        assertEquals("NewUniversity", instructor.getUniversityInfo());
        assertEquals("NewBio", instructor.getInstructorBio());
        assertEquals(new BigDecimal("100"), instructor.getAvgPrice());
        assertArrayEquals(new byte[]{5, 6, 7, 8}, instructor.getInstructorImage()); // Updated assertion for byte[]

        verify(instructorDAO, times(1)).updateInstructor(instructor);
    }

    // Helper method to set private fields using reflection
    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}