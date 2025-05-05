package com.example.demo.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.demo.DTO.ScheduleDateBookingDTO;
import com.example.demo.DTO.SchedulingBookingDTO;
import com.example.demo.DTO.UserDTO;
import com.example.demo.DTO.VendorDTO;
import com.example.demo.DTO.VendorDriverDTO;
import com.example.demo.Model.Booking;
import com.example.demo.Model.ScheduledDate;
import com.example.demo.Model.SchedulingBooking;
import com.example.demo.Model.User;
import com.example.demo.Model.Vendor;
import com.example.demo.Model.VendorDriver;
import com.example.demo.Repository.ScheduleBookingRepository;
import com.example.demo.Repository.ScheduleDates;
import com.example.demo.Repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class ScheduleBookingService {
    

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScheduleBookingRepository scheduleBookingRepository;

    @Autowired
    private ScheduleDates scheduleDates;

    @Autowired
    private RestTemplate restTemplate;

     @Transactional
    public SchedulingBooking createSchedule(
            int userId,
            String pickUpLocation,
            String dropLocation,
            String time,
            String returnTime,
            String shiftTime,
            List<LocalDate> dates
    ) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        SchedulingBooking booking = new SchedulingBooking();
        booking.setPickUpLocation(pickUpLocation);
        booking.setDropLocation(dropLocation);
        booking.setTime(time);
        booking.setReturnTime(returnTime);
        booking.setShiftTime(shiftTime);
        booking.setUser(user);

        for (LocalDate date : dates) {
            ScheduledDate sd = new ScheduledDate();
            sd.setDate(date);
            sd.setSchedulingBooking(booking);
            booking.getScheduledDates().add(sd);
        }

        return scheduleBookingRepository.save(booking);
    }


     

    public SchedulingBooking assignVendorToBooking(int bookingId, Long vendorId) {
        SchedulingBooking booking = scheduleBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

        String vendorUrl = "http://localhost:8080/vendors/" + vendorId;
        try {
            restTemplate.getForObject(vendorUrl, Vendor.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Vendor not found with ID: " + vendorId);
        }

        booking.setVendorId(vendorId);
        return scheduleBookingRepository.save(booking);
    }

    public SchedulingBooking assignDriverBooking(int bookingId, int vendorDriverId ){
        SchedulingBooking booking = scheduleBookingRepository.findById(bookingId)
        .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));  
        String driverUrl = "http://localhost:8080/vendorDriver/" + vendorDriverId;
        try {
            restTemplate.getForObject(driverUrl, Vendor.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Vendor not found with ID: " + vendorDriverId);
        }
        booking.setVendorDriverId(vendorDriverId);
        return scheduleBookingRepository.save(booking);

    
    }


    public SchedulingBookingDTO getBookingWithVendorDTO(int bookingId) {
    SchedulingBooking booking = scheduleBookingRepository.findById(bookingId)
                        .orElseThrow(() -> new RuntimeException("Booking not found"));

    String vendorServiceUrl = "http://localhost:8080/vendors/" + booking.getVendorId();
    String vendorDriverServiceUrl = "http://localhost:8080/vendorDriver/" + booking.getVendorDriverId();

    Vendor vendor = restTemplate.getForObject(vendorServiceUrl, Vendor.class);
    VendorDriver vendorDriver = restTemplate.getForObject(vendorDriverServiceUrl, VendorDriver.class);

    booking.setVendor(vendor);
    booking.setVendorDriver(vendorDriver);

    SchedulingBookingDTO dto = new SchedulingBookingDTO();
    dto.setId(booking.getId());
    dto.setPickUpLocation(booking.getPickUpLocation());
    dto.setDropLocation(booking.getDropLocation());
    dto.setTime(booking.getTime());
    dto.setReturnTime(booking.getReturnTime());
    dto.setShiftTime(booking.getShiftTime());
    dto.setBookingType(booking.getBookingType());
    dto.setDateOfList(booking.getDateOfList());

    VendorDTO vendorDTO = new VendorDTO();
    vendorDTO.setId(vendor.getId());
    vendorDTO.setVendorCompanyName(vendor.getVendorCompanyName());
    vendorDTO.setContactNo(vendor.getContactNo());
    vendorDTO.setAlternateMobileNo(vendor.getAlternateMobileNo());
    vendorDTO.setCity(vendor.getCity());
    vendorDTO.setVendorEmail(vendor.getVendorEmail());
    dto.setVendor(vendorDTO);

    VendorDriverDTO driverDTO = new VendorDriverDTO();
    driverDTO.setVendorDriverId(vendorDriver.getVendorDriverId());
    driverDTO.setDriverName(vendorDriver.getDriverName());
    driverDTO.setContactNo(vendorDriver.getContactNo());
    driverDTO.setAltContactNo(vendorDriver.getAltContactNo());
    dto.setVendorDriver(driverDTO);

    User user = booking.getUser();
    UserDTO userDTO = new UserDTO();
    userDTO.setId(user.getId());
    userDTO.setFirstName(user.getFirstName());
    userDTO.setLastName(user.getLastName());
    userDTO.setEmail(user.getEmail());
    userDTO.setMobileNo(user.getMobileNo());
    userDTO.setPickupLocation(user.getPickupLocation());
    userDTO.setDropLocation(user.getDropLocation());
    userDTO.setShiftTime(user.getShiftTime());
    userDTO.setStatus(user.getStatus());
    
    
    userDTO.setEmail(user.getEmail());
    dto.setUser(userDTO);

    List<ScheduleDateBookingDTO> scheduledDateDTOs = booking.getScheduledDates().stream().map(sd -> {
        ScheduleDateBookingDTO sdDTO = new ScheduleDateBookingDTO();
        sdDTO.setId(sd.getId());
        sdDTO.setDate(sd.getDate());
        return sdDTO;
    }).toList();
    dto.setScheduledDates(scheduledDateDTOs);

    return dto;
}



public List<SchedulingBooking> getBookingByUserId(int userId){
    return this.scheduleBookingRepository.findByUserId(userId);
}

public SchedulingBookingDTO getByScheduleBookingId(int id){
    SchedulingBooking schedulingBooking = this.scheduleBookingRepository.findById(id).get();
   SchedulingBookingDTO dtoS = new SchedulingBookingDTO();
   dtoS.setId(schedulingBooking.getId());
   dtoS.setBookingId(schedulingBooking.getBookId());
   dtoS.setPickUpLocation(schedulingBooking.getPickUpLocation());
   dtoS.setDropLocation(schedulingBooking.getDropLocation());
   dtoS.setTime(schedulingBooking.getTime());
   dtoS.setReturnTime(schedulingBooking.getReturnTime());
   dtoS.setShiftTime(schedulingBooking.getShiftTime());
   dtoS.setBookingType(schedulingBooking.getBookingType());
   try {
    String vendorServiceUrl = "http://localhost:8080/vendors/" + schedulingBooking.getVendorId();
    Vendor vendor = restTemplate.getForObject(vendorServiceUrl, Vendor.class);
    if (vendor != null) {
        VendorDTO vendorDTO = new VendorDTO();
        vendorDTO.setId(vendor.getId());
        vendorDTO.setVendorCompanyName(vendor.getVendorCompanyName());
        vendorDTO.setContactNo(vendor.getContactNo());
        vendorDTO.setAlternateMobileNo(vendor.getAlternateMobileNo());
        vendorDTO.setCity(vendor.getCity());
        vendorDTO.setVendorEmail(vendor.getVendorEmail());
        dtoS.setVendor(vendorDTO);
    }
} catch (Exception e) {
    System.out.println("Vendor not found for ID: " + schedulingBooking.getVendorId());
    dtoS.setVendor(null); 
}

try {
    String vendorDriverServiceUrl = "http://localhost:8080/vendorDriver/" + schedulingBooking.getVendorDriverId();
    VendorDriver vendorDriver = restTemplate.getForObject(vendorDriverServiceUrl, VendorDriver.class);
    if (vendorDriver != null) {
        VendorDriverDTO driverDTO = new VendorDriverDTO();
        driverDTO.setVendorDriverId(vendorDriver.getVendorDriverId());
        driverDTO.setDriverName(vendorDriver.getDriverName());
        driverDTO.setContactNo(vendorDriver.getContactNo());
        driverDTO.setAltContactNo(vendorDriver.getAltContactNo());
        dtoS.setVendorDriver(driverDTO);
    }
} catch (Exception e) {
    System.out.println("VendorDriver not found for ID: " + schedulingBooking.getVendorDriverId());
    dtoS.setVendorDriver(null); 
}

User user = this.userRepository.findById(schedulingBooking.getUser().getId()).orElse(null);
if (user != null) {
    UserDTO userDTO = new UserDTO();
    userDTO.setId(user.getId());
    userDTO.setFirstName(user.getFirstName());
    userDTO.setLastName(user.getLastName());
    userDTO.setEmail(user.getEmail());
    userDTO.setMobileNo(user.getMobileNo());
    userDTO.setPickupLocation(user.getPickupLocation());
    userDTO.setDropLocation(user.getDropLocation());
    userDTO.setShiftTime(user.getShiftTime());
    userDTO.setStatus(user.getStatus());
    dtoS.setUser(userDTO);
}

List<ScheduleDateBookingDTO> scheduledDateDTOs = schedulingBooking.getScheduledDates().stream().map(sd -> {
    ScheduleDateBookingDTO sdDTO = new ScheduleDateBookingDTO();
    sdDTO.setId(sd.getId());
    sdDTO.setDate(sd.getDate());
    return sdDTO;
}).toList();
dtoS.setScheduledDates(scheduledDateDTOs);

// dtoList.add(dtoS);




   

   return dtoS;



}

// public List<SchedulingBookingDTO> getBooking(int userId){
//     return this.scheduleBookingRepository.getUserById(userId);
// }

public List<SchedulingBookingDTO> findByUserId(int userId) {
    List<SchedulingBooking> bookings = this.scheduleBookingRepository.findByUserId(userId);
    List<SchedulingBookingDTO> dtoList = new ArrayList<>();

    for (SchedulingBooking schedulingBooking : bookings) {
        SchedulingBookingDTO schedulingBookingDTO = new SchedulingBookingDTO();
        schedulingBookingDTO.setId(schedulingBooking.getId());
        schedulingBookingDTO.setPickUpLocation(schedulingBooking.getPickUpLocation());
        schedulingBookingDTO.setDropLocation(schedulingBooking.getDropLocation());
        schedulingBookingDTO.setTime(schedulingBooking.getTime());
        schedulingBookingDTO.setReturnTime(schedulingBooking.getReturnTime());
        schedulingBookingDTO.setShiftTime(schedulingBooking.getShiftTime());
        schedulingBookingDTO.setBookingType(schedulingBooking.getBookingType());

        try {
            String vendorServiceUrl = "http://localhost:8080/vendors/" + schedulingBooking.getVendorId();
            Vendor vendor = restTemplate.getForObject(vendorServiceUrl, Vendor.class);
            if (vendor != null) {
                VendorDTO vendorDTO = new VendorDTO();
                vendorDTO.setId(vendor.getId());
                vendorDTO.setVendorCompanyName(vendor.getVendorCompanyName());
                vendorDTO.setContactNo(vendor.getContactNo());
                vendorDTO.setAlternateMobileNo(vendor.getAlternateMobileNo());
                vendorDTO.setCity(vendor.getCity());
                vendorDTO.setVendorEmail(vendor.getVendorEmail());
                schedulingBookingDTO.setVendor(vendorDTO);
            }
        } catch (Exception e) {
            System.out.println("Vendor not found for ID: " + schedulingBooking.getVendorId());
            schedulingBookingDTO.setVendor(null); 
        }

        try {
            String vendorDriverServiceUrl = "http://localhost:8080/vendorDriver/" + schedulingBooking.getVendorDriverId();
            VendorDriver vendorDriver = restTemplate.getForObject(vendorDriverServiceUrl, VendorDriver.class);
            if (vendorDriver != null) {
                VendorDriverDTO driverDTO = new VendorDriverDTO();
                driverDTO.setVendorDriverId(vendorDriver.getVendorDriverId());
                driverDTO.setDriverName(vendorDriver.getDriverName());
                driverDTO.setContactNo(vendorDriver.getContactNo());
                driverDTO.setAltContactNo(vendorDriver.getAltContactNo());
                schedulingBookingDTO.setVendorDriver(driverDTO);
            }
        } catch (Exception e) {
            System.out.println("VendorDriver not found for ID: " + schedulingBooking.getVendorDriverId());
            schedulingBookingDTO.setVendorDriver(null); 
        }

        User user = this.userRepository.findById(userId).orElse(null);
        if (user != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setEmail(user.getEmail());
            userDTO.setMobileNo(user.getMobileNo());
            userDTO.setPickupLocation(user.getPickupLocation());
            userDTO.setDropLocation(user.getDropLocation());
            userDTO.setShiftTime(user.getShiftTime());
            userDTO.setStatus(user.getStatus());
            schedulingBookingDTO.setUser(userDTO);
        }

        List<ScheduleDateBookingDTO> scheduledDateDTOs = schedulingBooking.getScheduledDates().stream().map(sd -> {
            ScheduleDateBookingDTO sdDTO = new ScheduleDateBookingDTO();
            sdDTO.setId(sd.getId());
            sdDTO.setDate(sd.getDate());
            return sdDTO;
        }).toList();
        schedulingBookingDTO.setScheduledDates(scheduledDateDTOs);

        dtoList.add(schedulingBookingDTO);
    }

    return dtoList;
}

public List<SchedulingBookingDTO> getBookingByVendorDriverId(int vendorDriverId){

    List<SchedulingBooking> bookings = this.scheduleBookingRepository.findByVendorDriverId(vendorDriverId);
    List<SchedulingBookingDTO> dtoList = new ArrayList<>();

    for (SchedulingBooking schedulingBooking : bookings) {
        SchedulingBookingDTO schedulingBookingDTO = new SchedulingBookingDTO();
        schedulingBookingDTO.setId(schedulingBooking.getId());
        schedulingBookingDTO.setPickUpLocation(schedulingBooking.getPickUpLocation());
        schedulingBookingDTO.setDropLocation(schedulingBooking.getDropLocation());
        schedulingBookingDTO.setTime(schedulingBooking.getTime());
        schedulingBookingDTO.setReturnTime(schedulingBooking.getReturnTime());
        schedulingBookingDTO.setShiftTime(schedulingBooking.getShiftTime());
        schedulingBookingDTO.setBookingType(schedulingBooking.getBookingType());

        try {
            String vendorServiceUrl = "http://localhost:8080/vendors/" + schedulingBooking.getVendorId();
            Vendor vendor = restTemplate.getForObject(vendorServiceUrl, Vendor.class);
            if (vendor != null) {
                VendorDTO vendorDTO = new VendorDTO();
                vendorDTO.setId(vendor.getId());
                vendorDTO.setVendorCompanyName(vendor.getVendorCompanyName());
                vendorDTO.setContactNo(vendor.getContactNo());
                vendorDTO.setAlternateMobileNo(vendor.getAlternateMobileNo());
                vendorDTO.setCity(vendor.getCity());
                vendorDTO.setVendorEmail(vendor.getVendorEmail());
                schedulingBookingDTO.setVendor(vendorDTO);
            }
        } catch (Exception e) {
            System.out.println("Vendor not found for ID: " + schedulingBooking.getVendorId());
            schedulingBookingDTO.setVendor(null); 
        }

        try {
            String vendorDriverServiceUrl = "http://localhost:8080/vendorDriver/" + schedulingBooking.getVendorDriverId();
            VendorDriver vendorDriver = restTemplate.getForObject(vendorDriverServiceUrl, VendorDriver.class);
            if (vendorDriver != null) {
                VendorDriverDTO driverDTO = new VendorDriverDTO();
                driverDTO.setVendorDriverId(vendorDriver.getVendorDriverId());
                driverDTO.setDriverName(vendorDriver.getDriverName());
                driverDTO.setContactNo(vendorDriver.getContactNo());
                driverDTO.setAltContactNo(vendorDriver.getAltContactNo());
                schedulingBookingDTO.setVendorDriver(driverDTO);
            }
        } catch (Exception e) {
            System.out.println("VendorDriver not found for ID: " + schedulingBooking.getVendorDriverId());
            schedulingBookingDTO.setVendorDriver(null); 
        }

        User user = this.userRepository.findById(vendorDriverId).orElse(null);
        if (user != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setEmail(user.getEmail());
            userDTO.setMobileNo(user.getMobileNo());
            userDTO.setPickupLocation(user.getPickupLocation());
            userDTO.setDropLocation(user.getDropLocation());
            userDTO.setShiftTime(user.getShiftTime());
            userDTO.setStatus(user.getStatus());
            schedulingBookingDTO.setUser(userDTO);
        }

        List<ScheduleDateBookingDTO> scheduledDateDTOs = schedulingBooking.getScheduledDates().stream().map(sd -> {
            ScheduleDateBookingDTO sdDTO = new ScheduleDateBookingDTO();
            sdDTO.setId(sd.getId());
            sdDTO.setDate(sd.getDate());
            return sdDTO;
        }).toList();
        schedulingBookingDTO.setScheduledDates(scheduledDateDTOs);

        dtoList.add(schedulingBookingDTO);
    }

    return dtoList;


}

public ScheduledDate updateStatusByUserIdAndDate(int userId, LocalDate backendDate) {
    LocalDate currentDate = LocalDate.now(); 

    if (!currentDate.isEqual(backendDate)) {
        return null;
    }

    List<SchedulingBooking> bookings = scheduleBookingRepository.findByUserId(userId);

    for (SchedulingBooking booking : bookings) {
        for (ScheduledDate schedulingDate : booking.getScheduledDates()) {
            if (schedulingDate.getDate().isEqual(currentDate)) {
                schedulingDate.setStatus("COMPLETED");
                return scheduleDates.save(schedulingDate);
            }
        }
    }

    return null;
}




   
}


