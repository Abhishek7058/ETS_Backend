package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.DTO.ScheduleDateBookingDTO;
import com.example.demo.DTO.SchedulingBookingDTO;
import com.example.demo.Model.SchedulingBooking;


@Repository
public interface ScheduleBookingRepository extends JpaRepository<SchedulingBooking, Integer>{
  
List<SchedulingBooking> findByUserId(int userId);

List<SchedulingBookingDTO> getUserById(int userId);

List<SchedulingBooking> findByVendorDriverId(int vendorDriverId);
}
