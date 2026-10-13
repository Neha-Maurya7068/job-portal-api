
package com.neha.job_portal_api.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neha.job_portal_api.entity.SecurityReportDelivery;
import com.neha.job_portal_api.entity.SecurityReportDeliveryStatus;

public interface SecurityReportDeliveryRepository
        extends JpaRepository<SecurityReportDelivery, Long> {

    @Query("""
        SELECT d FROM SecurityReportDelivery d
        WHERE (:status IS NULL OR d.status = :status)
          AND (:startDate IS NULL OR d.reportDate >= :startDate)
          AND (:endDate IS NULL OR d.reportDate <= :endDate)
        ORDER BY d.createdAt DESC
        """)
    Page<SecurityReportDelivery> searchDeliveries(
            @Param("status") SecurityReportDeliveryStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query("""
        SELECT COUNT(d) FROM SecurityReportDelivery d
        WHERE (:startDate IS NULL OR d.reportDate >= :startDate)
          AND (:endDate IS NULL OR d.reportDate <= :endDate)
        """)
    long countInDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
        SELECT COUNT(d) FROM SecurityReportDelivery d
        WHERE d.status = :status
          AND (:startDate IS NULL OR d.reportDate >= :startDate)
          AND (:endDate IS NULL OR d.reportDate <= :endDate)
        """)
    long countByStatusInDateRange(
            @Param("status") SecurityReportDeliveryStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
