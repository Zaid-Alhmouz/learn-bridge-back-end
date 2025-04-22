package com.learnbridge.learn_bridge_back_end.repository;

import com.learnbridge.learn_bridge_back_end.dto.InstructorDTO;
import com.learnbridge.learn_bridge_back_end.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InstructorStatsRepository extends JpaRepository<Instructor, Long> {



    @Query("""
    SELECT new com.learnbridge.learn_bridge_back_end.dto.InstructorDTO(
      i.instructorId,
      i.firstName,
      i.lastName,
      i.universityInfo,
      i.instructorBio,
      i.avgPrice,
      i.favouriteCategory,
      (SELECT COUNT(s2)
         FROM Session s2
        WHERE s2.instructor.userId    = i.instructorId
          AND s2.sessionStatus         = com.learnbridge.learn_bridge_back_end.entity.SessionStatus.FINISHED
      ),
      (SELECT COUNT(r2)
         FROM Rating r2
        WHERE r2.instructor.user.userId = i.instructorId
      ),
      (SELECT COALESCE(AVG(r3.stars), 0)
         FROM Rating r3
        WHERE r3.instructor.user.userId = i.instructorId
      )
    )
    FROM Instructor i
    WHERE i.favouriteCategory = :category
  """)
    List<InstructorDTO> findStatsByCategory(@Param("category") String category);

    @Query("""
    SELECT new com.learnbridge.learn_bridge_back_end.dto.InstructorDTO(
      i.instructorId,
      i.firstName,
      i.lastName,
      i.universityInfo,
      i.instructorBio,
      i.avgPrice,
      i.favouriteCategory,
      (SELECT COUNT(s2)
         FROM Session s2
        WHERE s2.instructor.userId    = i.instructorId
          AND s2.sessionStatus         = com.learnbridge.learn_bridge_back_end.entity.SessionStatus.FINISHED
      ),
      (SELECT COUNT(r2)
         FROM Rating r2
        WHERE r2.instructor.user.userId = i.instructorId
      ),
      (SELECT COALESCE(AVG(r3.stars), 0)
         FROM Rating r3
        WHERE r3.instructor.user.userId = i.instructorId
      )
    )
    FROM Instructor i
    WHERE i.instructorId = :id
  """)
    Optional<InstructorDTO> findStatsById(@Param("id") Long id);

}
