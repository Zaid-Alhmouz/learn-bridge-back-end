package service;

import com.learnbridge.learn_bridge_back_end.dao.LearnerDAO;
import com.learnbridge.learn_bridge_back_end.dto.InstructorDTO;
import com.learnbridge.learn_bridge_back_end.entity.Learner;
import com.learnbridge.learn_bridge_back_end.entity.User;
import com.learnbridge.learn_bridge_back_end.repository.InstructorStatsRepository;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.service.FindInstructorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FindInstructorServiceTest {
    @Mock
    private LearnerDAO learnerDAO;

    @Mock
    private InstructorStatsRepository repo;

    @InjectMocks
    private FindInstructorService findInstructorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindFavouriteInstructors() {
        Long learnerId = Long.valueOf(1L);
        String favouriteCategory = "Software Engineering";

        SecurityUser mockUser = mock(SecurityUser.class);
        User mockInnerUser = mock(User.class);

        when(mockUser.getUser()).thenReturn(mockInnerUser);
        when(mockInnerUser.getId()).thenReturn(learnerId);

        Learner learner = new Learner();
        learner.setFavouriteCategory(favouriteCategory);
        when(learnerDAO.findLearnerById(learnerId)).thenReturn(learner);

        List<InstructorDTO> expectedInstructors = List.of(new InstructorDTO(), new InstructorDTO());
        when(repo.findStatsByCategory(favouriteCategory)).thenReturn(expectedInstructors);

        List<InstructorDTO> result = findInstructorService.findFavouriteInstructors(mockUser);

        assertEquals(expectedInstructors, result);
        verify(learnerDAO, times(1)).findLearnerById(learnerId);
        verify(repo, times(1)).findStatsByCategory(favouriteCategory);
    }

    @Test
    void testFindSelectedCategoryInstructors() {
        when(repo.findStatsByCategory("Science")).thenReturn(List.of(new InstructorDTO()));

        List<InstructorDTO> result = findInstructorService.findSelectedCategoryInstructors("Science");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testViewProfileFound() {
        InstructorDTO instructorDTO = new InstructorDTO();
        when(repo.findStatsById(Long.valueOf(1L))).thenReturn(Optional.of(instructorDTO));

        InstructorDTO result = findInstructorService.viewProfile(Long.valueOf(1L));

        assertNotNull(result);
    }

    @Test
    void testViewProfileNotFound() {
        when(repo.findStatsById(Long.valueOf(99L))).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            findInstructorService.viewProfile(Long.valueOf(99L));
        });

        assertTrue(exception.getMessage().contains("Instructor not found"));
    }

}
