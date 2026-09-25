package com.devflow.repository;
import com.devflow.model.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface TaskRepository extends JpaRepository<Task,Long>{
  long countByStatus(TaskStatus status);
  List<Task> findAllByOrderByCreatedAtDesc();
  @Query("""
    select t from Task t
    where (:search is null or lower(t.title) like concat('%', :search, '%') or lower(coalesce(t.description, '')) like concat('%', :search, '%'))
      and (:status is null or t.status = :status)
      and (:priority is null or t.priority = :priority)
      and (:projectId is null or t.project.id = :projectId)
    order by t.createdAt desc
    """)
  List<Task> search(@Param("search") String search, @Param("status") TaskStatus status,
                    @Param("priority") Priority priority, @Param("projectId") Long projectId);
}
