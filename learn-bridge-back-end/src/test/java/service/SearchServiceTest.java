package service;

import com.learnbridge.learn_bridge_back_end.dao.InstructorDAO;
import com.learnbridge.learn_bridge_back_end.dao.PostDAO;
import com.learnbridge.learn_bridge_back_end.dto.PostDTO;
import com.learnbridge.learn_bridge_back_end.dto.SearchDTO;
import com.learnbridge.learn_bridge_back_end.entity.Instructor;
import com.learnbridge.learn_bridge_back_end.entity.Post;
import com.learnbridge.learn_bridge_back_end.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SearchServiceTest {

    @Mock
    private InstructorDAO instructorDAO;

    @Mock
    private PostDAO postDAO;

    @InjectMocks
    private SearchService searchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchInstructors_withKeyword() {
        // Arrange
        String keyword = "bridge";
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyword(keyword);

        List<Instructor> mockInstructors = Arrays.asList(new Instructor(), new Instructor());
        when(instructorDAO.searchInstructors(keyword)).thenReturn(mockInstructors);

        // Act
        List<Instructor> result = searchService.searchInstructors(searchDTO);

        // Assert
        assertEquals(mockInstructors, result);
        verify(instructorDAO, times(1)).searchInstructors(keyword);
        verify(instructorDAO, never()).findAllInstructors();
    }

    @Test
    void testSearchInstructors_withoutKeyword() {
        // Arrange
        SearchDTO searchDTO = new SearchDTO(); // No keyword set
        List<Instructor> mockInstructors = Arrays.asList(new Instructor(), new Instructor());
        when(instructorDAO.findAllInstructors()).thenReturn(mockInstructors);

        // Act
        List<Instructor> result = searchService.searchInstructors(searchDTO);

        // Assert
        assertEquals(mockInstructors, result);
        verify(instructorDAO, times(1)).findAllInstructors();
        verify(instructorDAO, never()).searchInstructors(anyString());
    }

    @Test
    void testSearchPosts_withKeyword() {
        // Arrange
        String keyword = "lesson";
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyword(keyword);

        Post post1 = new Post();
        Post post2 = new Post();
        List<Post> mockPosts = Arrays.asList(post1, post2);
        when(postDAO.searchPosts(keyword)).thenReturn(mockPosts);

        // Act
        List<PostDTO> result = searchService.searchPosts(searchDTO);

        // Assert
        assertEquals(2, result.size());
        verify(postDAO, times(1)).searchPosts(keyword);
        verify(postDAO, never()).findApprovedPosts();
    }

    @Test
    void testSearchPosts_withoutKeyword() {
        // Arrange
        SearchDTO searchDTO = new SearchDTO(); // No keyword set

        Post post1 = new Post();
        Post post2 = new Post();
        List<Post> mockPosts = Arrays.asList(post1, post2);
        when(postDAO.findApprovedPosts()).thenReturn(mockPosts);

        // Act
        List<PostDTO> result = searchService.searchPosts(searchDTO);

        // Assert
        assertEquals(2, result.size());
        verify(postDAO, times(1)).findApprovedPosts();
        verify(postDAO, never()).searchPosts(anyString());
    }

    @Test
    void testSearchInstructors_withEmptyKeyword() {
        // Arrange
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyword("   "); // Empty keyword with spaces

        List<Instructor> mockInstructors = Collections.singletonList(new Instructor());
        when(instructorDAO.findAllInstructors()).thenReturn(mockInstructors);

        // Act
        List<Instructor> result = searchService.searchInstructors(searchDTO);

        // Assert
        assertEquals(mockInstructors, result);
        verify(instructorDAO, times(1)).findAllInstructors();
        verify(instructorDAO, never()).searchInstructors(anyString());
    }

    @Test
    void testSearchPosts_withEmptyKeyword() {
        // Arrange
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyword("   "); // Empty keyword with spaces

        Post post = new Post();
        List<Post> mockPosts = Collections.singletonList(post);
        when(postDAO.findApprovedPosts()).thenReturn(mockPosts);

        // Act
        List<PostDTO> result = searchService.searchPosts(searchDTO);

        // Assert
        assertEquals(1, result.size());
        verify(postDAO, times(1)).findApprovedPosts();
        verify(postDAO, never()).searchPosts(anyString());
    }
}
