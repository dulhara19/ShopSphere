package com.shopsphere.analytics.repository;

import com.shopsphere.analytics.model.Export;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExportRepository extends JpaRepository<Export, Long> {

    Optional<Export> findByExportId(String exportId);

    List<Export> findByUserId(String userId);

    List<Export> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Export> findByStatus(String status);

    @Query("SELECT e FROM Export e WHERE e.userId = :userId AND e.status = :status ORDER BY e.createdAt DESC")
    List<Export> findUserExportsByStatus(@Param("userId") String userId, @Param("status") String status);

    List<Export> findByType(String type);
}
