
package com.neha.job_portal_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neha.job_portal_api.entity.SecurityReportDelivery;

public interface SecurityReportDeliveryRepository
        extends JpaRepository<SecurityReportDelivery, Long> {

    List<SecurityReportDelivery>
        findTop50ByOrderByCreatedAtDesc();
}
