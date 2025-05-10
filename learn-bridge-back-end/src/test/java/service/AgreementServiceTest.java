package service;

import com.learnbridge.learn_bridge_back_end.dao.AgreementDAO;
import com.learnbridge.learn_bridge_back_end.dao.InstructorDAO;
import com.learnbridge.learn_bridge_back_end.dao.LearnerDAO;
import com.learnbridge.learn_bridge_back_end.dao.PostDAO;
import com.learnbridge.learn_bridge_back_end.dto.AgreementResponseDTO;
import com.learnbridge.learn_bridge_back_end.entity.*;
import com.learnbridge.learn_bridge_back_end.service.AgreementService;
import com.learnbridge.learn_bridge_back_end.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AgreementServiceTest {

    @Mock
    private AgreementDAO agreementDAO;

    @Mock
    private InstructorDAO instructorDAO;

    @Mock
    private LearnerDAO learnerDAO;

    @Mock
    private PostDAO postDAO;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AgreementService agreementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAgreement_Success() {
        Long instructorId = 1L;
        Long learnerId = 2L;
        Long postId = 3L;

        // Mock Instructor
        Instructor instructor = new Instructor();
        instructor.setFavouriteCategory("Math");

        // Mock Learner
        Learner learner = new Learner();

        // Mock Post
        Post post = new Post();
        post.setCategory("Math");
        post.setPrice(new BigDecimal("100"));
        post.setPostStatus(PostStatus.ACCEPTED); // Set postStatus to avoid NullPointerException
        PostId compositePostId = new PostId(learnerId, postId);

        // Mock Agreement
        Agreement agreement = new Agreement();
        agreement.setInstructor(instructor);
        agreement.setLearner(learner);
        agreement.setPost(post);
        agreement.setPrice(new BigDecimal("100"));

        Agreement savedAgreement = new Agreement();
        savedAgreement.setAgreementId(1L);
        savedAgreement.setInstructor(instructor);
        savedAgreement.setLearner(learner);
        savedAgreement.setPost(post);
        savedAgreement.setPrice(new BigDecimal("100"));

        // Mock DAO calls
        when(instructorDAO.findInstructorById(instructorId)).thenReturn(instructor);
        when(learnerDAO.findLearnerById(learnerId)).thenReturn(learner);
        when(postDAO.findPostById(compositePostId)).thenReturn(post);
        when(agreementDAO.saveAgreement(any(Agreement.class))).thenReturn(savedAgreement);

        // Invoke the service method
        AgreementResponseDTO response = agreementService.createAgreement(instructorId, learnerId, postId);

        // Assertions
        assertNotNull(response);
        assertEquals("Math", post.getCategory());
        assertEquals(PostStatus.ACCEPTED.toString(), response.getPostStatus());
        verify(notificationService, times(1)).createAgreementRequestNotification(savedAgreement);
    }

    @Test
    void testCreateAgreement_CategoryMismatch() {
        Long instructorId = 1L;
        Long learnerId = 2L;
        Long postId = 3L;

        Instructor instructor = new Instructor();
        instructor.setFavouriteCategory("Science");

        Learner learner = new Learner();

        Post post = new Post();
        post.setCategory("Math");

        PostId compositePostId = new PostId(learnerId, postId);

        when(instructorDAO.findInstructorById(instructorId)).thenReturn(instructor);
        when(learnerDAO.findLearnerById(learnerId)).thenReturn(learner);
        when(postDAO.findPostById(compositePostId)).thenReturn(post);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> agreementService.createAgreement(instructorId, learnerId, postId));

        assertEquals("Instructor's favourite category (“Science”) does not match post category (“Math”).", exception.getMessage());
    }

    @Test
    void testGetAgreementByLearner_Success() {
        Long learnerId = 1L;

        Learner learner = new Learner();
        learner.setFirstName("John");
        learner.setLastName("Doe");

        Instructor instructor = new Instructor();
        instructor.setFirstName("Jane");
        instructor.setLastName("Smith");

        Agreement agreement = new Agreement();
        agreement.setLearner(learner);
        agreement.setInstructor(instructor);
        agreement.setPrice(new BigDecimal("100"));

        when(agreementDAO.findAgreementByLearnerId(learnerId)).thenReturn(Collections.singletonList(agreement));

        List<AgreementResponseDTO> response = agreementService.getAgreementByLearner(learnerId);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("John Doe", response.get(0).getLearnerName());
        assertEquals("Jane Smith", response.get(0).getInstructorName());
    }

    @Test
    void testCreateLearnerInitiatedAgreement_Success() {
        Long learnerId = 1L;
        Long instructorId = 2L;

        Learner learner = new Learner();
        learner.setFirstName("John");
        learner.setLastName("Doe");

        Instructor instructor = new Instructor();
        instructor.setFirstName("Jane");
        instructor.setLastName("Smith");
        instructor.setAvgPrice(new BigDecimal("200"));

        Agreement agreement = new Agreement();
        agreement.setLearner(learner);
        agreement.setInstructor(instructor);
        agreement.setPrice(new BigDecimal("200"));

        Agreement savedAgreement = new Agreement();
        savedAgreement.setAgreementId(1L);
        savedAgreement.setLearner(learner);
        savedAgreement.setInstructor(instructor);
        savedAgreement.setPrice(new BigDecimal("200"));

        when(learnerDAO.findLearnerById(learnerId)).thenReturn(learner);
        when(instructorDAO.findInstructorById(instructorId)).thenReturn(instructor);
        when(agreementDAO.saveAgreement(any(Agreement.class))).thenReturn(savedAgreement);

        AgreementResponseDTO response = agreementService.createLearnerInitiatedAgreement(learnerId, instructorId);

        assertNotNull(response);
        assertEquals("John Doe", response.getLearnerName());
        assertEquals("Jane Smith", response.getInstructorName());
        verify(notificationService, times(1)).createLearnerRequestNotification(savedAgreement);
    }

    @Test
    void testGetAgreementById_Success() {
        Long agreementId = 1L;

        Learner learner = new Learner();
        learner.setFirstName("John");
        learner.setLastName("Doe");

        Instructor instructor = new Instructor();
        instructor.setFirstName("Jane");
        instructor.setLastName("Smith");

        Agreement agreement = new Agreement();
        agreement.setAgreementId(agreementId);
        agreement.setLearner(learner);
        agreement.setInstructor(instructor);

        when(agreementDAO.findAgreementById(agreementId)).thenReturn(agreement);

        AgreementResponseDTO response = agreementService.getAgreementById(agreementId);

        assertNotNull(response);
        assertEquals("John Doe", response.getLearnerName());
        assertEquals("Jane Smith", response.getInstructorName());
    }

    @Test
    void testGetAgreementById_NotFound() {
        Long agreementId = 1L;

        when(agreementDAO.findAgreementById(agreementId)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> agreementService.getAgreementById(agreementId));

        assertEquals("Agreement not found with id: 1", exception.getMessage());
    }
}
