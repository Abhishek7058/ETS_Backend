package com.example.demo.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import com.example.demo.DTO.ScheduleDateBookingDTO;
import com.example.demo.DTO.SchedulingBookingDTO;
import com.example.demo.DTO.UserDTO;
import com.example.demo.DTO.VendorDTO;
import com.example.demo.DTO.VendorDriverDTO;
import com.example.demo.Model.CarRentalUser;
import com.example.demo.Model.ScheduledDate;
import com.example.demo.Model.SchedulingBooking;
import com.example.demo.Model.Vendor;
import com.example.demo.Model.VendorDriver;
import com.example.demo.Repository.ScheduleBookingRepository;
import com.example.demo.Repository.ScheduleDates;
// import com.example.demo.Repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class ScheduleBookingService {
    

    // @Autowired
    // private UserRepository userRepository;

    @Autowired
    private ScheduleBookingRepository scheduleBookingRepository;

    @Autowired
    private ScheduleDates scheduleDates;

    @Autowired
    private RestTemplate restTemplate;

        private static final Logger logger = LoggerFactory.getLogger(ScheduleBookingService.class);


    private final String apiKey = "AIzaSyCelDo4I5cPQ72TfCTQW-arhPZ7ALNcp8w"; // Replace with your Google API key


   

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
        String userServiceUrl = "http://localhost:8080/auth/getCarRentalUserById/" + userId;
        CarRentalUser user = restTemplate.getForObject(userServiceUrl, CarRentalUser.class);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

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


    // ------------------------------------------------------------------- sahil

  
    
    
    public boolean canBook(int bookingId, int vendorDriverId) {
        SchedulingBooking newBooking = scheduleBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));
    
        List<SchedulingBooking> existingBookings = scheduleBookingRepository.findByVendorDriverId(vendorDriverId);
    
        logger.debug("Checking booking possibility for bookingId: {}, vendorDriverId: {}", bookingId, vendorDriverId);
        logger.debug("Found {} existing bookings for driver", existingBookings.size());
        logger.debug("New booking details - cabType: {}, partnerSharing: {}, sittingExcepatation: {}", 
                     newBooking.getCabType(), newBooking.getPartnerSharing(), newBooking.getSittingExcepatation());
    
        if (newBooking.getCabType() == null) {
            logger.error("Booking has null cabType");
            return false;
        }
    
        if (!existingBookings.isEmpty()) {
            String existingCabType = existingBookings.get(0).getCabType();
            String requestingCabType = newBooking.getCabType();
    
            if (existingCabType == null || requestingCabType == null) {
                logger.error("Null cabType found during comparison");
                return false;
            }
    
            if (!existingCabType.equalsIgnoreCase(requestingCabType)) {
                logger.debug("Cab type mismatch - returning false");
                return false;
            }
        }
    
        int usableCapacity = getSeatingCapacityByCabType(newBooking.getCabType());
        logger.debug("Usable capacity for {} is {}", newBooking.getCabType(), usableCapacity);
    
        if (usableCapacity == -1) {
            logger.error("Unknown cab type: {}", newBooking.getCabType());
            throw new RuntimeException("Unknown cab type: " + newBooking.getCabType());
        }
    
        int effectiveSharing = newBooking.getPartnerSharing() == 0 ? usableCapacity : newBooking.getPartnerSharing();
        logger.debug("Effective partnerSharing used: {}", effectiveSharing);
    
        if (!existingBookings.isEmpty()) {
            int existingSharing = existingBookings.get(0).getPartnerSharing() == 0 ? 
                                  usableCapacity : 
                                  existingBookings.get(0).getPartnerSharing();
            logger.debug("Existing partnerSharing used: {}", existingSharing);
    
            if (effectiveSharing != existingSharing) {
                logger.debug("Partner sharing mismatch - returning false");
                return false;
            }
        }
    
        int usedSeats = 0;
        for (SchedulingBooking booking : existingBookings) {
            int seats = booking.getSittingExcepatation() <= 0 ? 1 : booking.getSittingExcepatation();
            usedSeats += seats;
        }
    
        int requestedSeats = newBooking.getSittingExcepatation() <= 0 ? 1 : newBooking.getSittingExcepatation();
        int remainingSeats = usableCapacity - usedSeats;
    
        logger.debug("Used seats: {}, Remaining seats: {}, Requested seats: {}, Allowed sharing: {}", 
                   usedSeats, remainingSeats, requestedSeats, effectiveSharing);
    
        return requestedSeats <= remainingSeats && (usedSeats + requestedSeats) <= effectiveSharing;
    }

    private int getSeatingCapacityByCabType(String cabType) {
        if (cabType == null) return -1;
    
        switch (cabType.toLowerCase()) {
            case "suv":
                return 5;
            case "sedan":
            case "sedan premium":
            case "hatchback":
                return 3;
            default:
                return -1;
        }
    }
    
    public SchedulingBooking assignDriverBooking(int bookingId, int vendorDriverId) {
        SchedulingBooking newBooking = scheduleBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));
    
        logger.info("Assigning driver {} to booking {}", vendorDriverId, bookingId);
    
        if (newBooking.getCabType() == null) {
            throw new RuntimeException("CabType is required for booking assignment.");
        }
                
        // Get driver details
        String driverUrl = "http://localhost:8080/vendorDriver/" + vendorDriverId;
        VendorDriver driver;
        try {
            driver = restTemplate.getForObject(driverUrl, VendorDriver.class);
            if (driver == null) {
                throw new RuntimeException("Driver not found with ID: " + vendorDriverId);
            }
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Driver not found with ID: " + vendorDriverId);
        }
        
        // 1. Cab Type Match: Check if the cab type of the booking matches the cab type of the driver's vehicle
        if (driver.getCabType() != null && !driver.getCabType().equalsIgnoreCase(newBooking.getCabType())) {
            throw new RuntimeException("Cab type mismatch. Booking requires " + newBooking.getCabType() + 
                    " but driver has " + driver.getCabType());
        }
        
        // Get all existing bookings for this driver
        List<SchedulingBooking> existingBookings = scheduleBookingRepository.findByVendorDriverId(vendorDriverId);
        
        // Check for route overlaps
        for (SchedulingBooking existingBooking : existingBookings) {
            if (isRouteOverlapping(
                    existingBooking.getPickUpLocation(),
                    existingBooking.getDropLocation(),
                    newBooking.getPickUpLocation(),
                    newBooking.getDropLocation())) {
                throw new RuntimeException("Cannot assign driver - route overlaps with existing booking ID: " + existingBooking.getId());
            }
        }
        
        // Get the cab capacity based on cab type
        int cabCapacity = getSeatingCapacityByCabType(newBooking.getCabType());
        if (cabCapacity == -1) {
            throw new RuntimeException("Unknown cab type: " + newBooking.getCabType());
        }
        
        // Create a map to track bookings by date and time slot
        Map<LocalDate, Map<String, List<SchedulingBooking>>> bookingsByDateAndSlot = new HashMap<>();
        Map<String, Integer> slotIdCounter = new HashMap<>(); // To generate unique slot IDs
        
        // Process each existing booking and organize by date and time slot
        for (SchedulingBooking booking : existingBookings) {
            // Skip if the booking doesn't have scheduled dates
            if (booking.getScheduledDates() == null || booking.getScheduledDates().isEmpty()) {
                continue;
            }
            
            // For each scheduled date in the booking
            for (ScheduledDate scheduledDate : booking.getScheduledDates()) {
                LocalDate bookingDate = scheduledDate.getDate();
                String timeSlot = getTimeSlot(booking.getTime());
                
                // Initialize the date map if it doesn't exist
                bookingsByDateAndSlot.putIfAbsent(bookingDate, new HashMap<>());
                
                // Initialize the time slot list if it doesn't exist
                Map<String, List<SchedulingBooking>> timeSlots = bookingsByDateAndSlot.get(bookingDate);
                timeSlots.putIfAbsent(timeSlot, new ArrayList<>());
                
                // Add the booking to the appropriate slot
                timeSlots.get(timeSlot).add(booking);
            }
        }
        
        // Check each date in the new booking
        if (newBooking.getScheduledDates() != null) {
            for (ScheduledDate scheduledDate : newBooking.getScheduledDates()) {
                LocalDate bookingDate = scheduledDate.getDate();
                String timeSlot = getTimeSlot(newBooking.getTime());
                
                // 2. Date Match: Check if the driver has bookings for this date
                if (bookingsByDateAndSlot.containsKey(bookingDate)) {
                    Map<String, List<SchedulingBooking>> timeSlots = bookingsByDateAndSlot.get(bookingDate);
                    
                    // 3. Time Slot Grouping: Check the specific time slot
                    if (timeSlots.containsKey(timeSlot)) {
                        List<SchedulingBooking> bookingsInSlot = timeSlots.get(timeSlot);
                        
                        // 4. Cab Capacity Limit: Check if adding this booking would exceed capacity
                        int currentBookingsCount = bookingsInSlot.size();
                        int requestedSeats = newBooking.getSittingExcepatation() <= 0 ? 1 : newBooking.getSittingExcepatation();
                        
                        if (currentBookingsCount + 1 > cabCapacity || currentBookingsCount + requestedSeats > cabCapacity) {
                            throw new RuntimeException("Cannot assign driver - cab capacity exceeded for date " + 
                                    bookingDate + " and time slot " + timeSlot);
                        }
                        
                        // 5. Slot ID Assignment: Get or create a slot ID for this time window
                        String slotKey = bookingDate + "-" + timeSlot;
                        if (!slotIdCounter.containsKey(slotKey)) {
                            slotIdCounter.put(slotKey, generateSlotId());
                        }
                        int slotId = slotIdCounter.get(slotKey);
                        
                        // Set the slot ID for the new booking
                        newBooking.setSlotId(slotId);
                    } else {
                        // New time slot for this date, create a new slot ID
                        String slotKey = bookingDate + "-" + timeSlot;
                        slotIdCounter.put(slotKey, generateSlotId());
                        newBooking.setSlotId(slotIdCounter.get(slotKey));
                    }
                } else {
                    // New date, create a new slot ID
                    String slotKey = bookingDate + "-" + timeSlot;
                    slotIdCounter.put(slotKey, generateSlotId());
                    newBooking.setSlotId(slotIdCounter.get(slotKey));
                }
            }
        }
        
        // Assign the driver and save the booking
        newBooking.setVendorDriverId(vendorDriverId);
        return scheduleBookingRepository.save(newBooking);
    }
    



    private boolean isRouteOverlapping(String existingPickup, String existingDrop, String newPickup, String newDrop) {
        try {
            // Using URI.create instead of deprecated fromHttpUrl
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("https://maps.googleapis.com/maps/api/directions/json")
                .queryParam("origin", UriUtils.encode(existingPickup, "UTF-8"))
                .queryParam("destination", UriUtils.encode(existingDrop, "UTF-8"))
                .queryParam("waypoints", "via:" + UriUtils.encode(newPickup, "UTF-8") + "|via:" + UriUtils.encode(newDrop, "UTF-8"))
                .queryParam("key", apiKey);
            
            String url = builder.build().toString();

            // Use proper generic type for the response
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && "OK".equals(response.get("status"))) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
                
                if (routes != null && !routes.isEmpty()) {
                    Map<String, Object> route = routes.get(0);
                    // We're not using the legs variable, so no need to cast it
                    // Just check if the legs exist to confirm routing
                    if (route.containsKey("legs")) {
                        return true;
                    }
                }
            }
            
            return false;
        } catch (Exception e) {
            logger.error("Error checking route overlap: {}", e.getMessage());
            return false;
        }
    }
    
    // Helper method to determine the time slot for a given time string
    private String getTimeSlot(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            return "UNKNOWN";
        }
        
        try {
            // Parse the time string to a LocalTime object
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime time = LocalTime.parse(timeString, formatter);
            
            // Define time slots - each slot is a 3-hour window
            if (time.isAfter(LocalTime.of(0, 0)) && time.isBefore(LocalTime.of(3, 0))) {
                return "00:00-03:00";
            } else if (time.isAfter(LocalTime.of(3, 0)) && time.isBefore(LocalTime.of(6, 0))) {
                return "03:00-06:00";
            } else if (time.isAfter(LocalTime.of(6, 0)) && time.isBefore(LocalTime.of(9, 0))) {
                return "06:00-09:00";
            } else if (time.isAfter(LocalTime.of(9, 0)) && time.isBefore(LocalTime.of(12, 0))) {
                return "09:00-12:00";
            } else if (time.isAfter(LocalTime.of(12, 0)) && time.isBefore(LocalTime.of(15, 0))) {
                return "12:00-15:00";
            } else if (time.isAfter(LocalTime.of(15, 0)) && time.isBefore(LocalTime.of(18, 0))) {
                return "15:00-18:00";
            } else if (time.isAfter(LocalTime.of(18, 0)) && time.isBefore(LocalTime.of(21, 0))) {
                return "18:00-21:00";
            } else {
                return "21:00-00:00";
            }
        } catch (Exception e) {
            logger.error("Error parsing time: {}", e.getMessage());
            return "UNKNOWN";
        }
    }
    
    // Helper method to generate a unique slot ID
    private static final AtomicInteger slotCounter = new AtomicInteger(1000);
    
    private int generateSlotId() {
        return slotCounter.incrementAndGet();
    }

    public SchedulingBookingDTO getBookingWithVendorDTO(int bookingId) {
        SchedulingBooking booking = scheduleBookingRepository.findById(bookingId)
                        .orElseThrow(() -> new RuntimeException("Booking not found"));

        String vendorServiceUrl = "http://localhost:8080/vendors/" + booking.getVendorId();
        String vendorDriverServiceUrl = "http://localhost:8080/vendorDriver/" + booking.getVendorDriverId();
        String userServiceUrl = "http://localhost:8080/auth/getCarRentalUserById/" + booking.getUser().getId();

        Vendor vendor = restTemplate.getForObject(vendorServiceUrl, Vendor.class);
        VendorDriver vendorDriver = restTemplate.getForObject(vendorDriverServiceUrl, VendorDriver.class);
        CarRentalUser user = restTemplate.getForObject(userServiceUrl, CarRentalUser.class);

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

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUserName(user.getUserName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setGender(user.getGender());
        userDTO.setPhone(user.getPhone());
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
    return this.scheduleBookingRepository.findByCarRentalUserId(userId);
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

try {
    String userServiceUrl = "http://localhost:8080/auth/getCarRentalUserById/" + schedulingBooking.getUser().getId();
    CarRentalUser user = restTemplate.getForObject(userServiceUrl, CarRentalUser.class);
    if (user != null) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUserName(user.getUserName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setGender(user.getGender());
        userDTO.setPhone(user.getPhone());
        dtoS.setUser(userDTO);
    }
} catch (Exception e) {
    System.out.println("User not found for ID: " + schedulingBooking.getUser().getId());
    dtoS.setUser(null);
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
    List<SchedulingBooking> bookings = this.scheduleBookingRepository.findByCarRentalUserId(userId);
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

        try {
            String userServiceUrl = "http://localhost:8080/auth/getCarRentalUserById/" + userId;
            CarRentalUser user = restTemplate.getForObject(userServiceUrl, CarRentalUser.class);
            if (user != null) {
                UserDTO userDTO = new UserDTO();
                userDTO.setId(user.getId());
                userDTO.setUserName(user.getUserName());
                userDTO.setLastName(user.getLastName());
                userDTO.setEmail(user.getEmail());
                userDTO.setGender(user.getGender());
                userDTO.setPhone(user.getPhone());
                schedulingBookingDTO.setUser(userDTO);
            }
        } catch (Exception e) {
            System.out.println("User not found for ID: " + userId);
            schedulingBookingDTO.setUser(null);
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

        try {
            String userServiceUrl = "http://localhost:8080/auth/getCarRentalUserById/" + schedulingBooking.getUser().getId();
            CarRentalUser user = restTemplate.getForObject(userServiceUrl, CarRentalUser.class);
            if (user != null) {
                UserDTO userDTO = new UserDTO();
                userDTO.setId(user.getId());
                userDTO.setUserName(user.getUserName());
                userDTO.setLastName(user.getLastName());
                userDTO.setEmail(user.getEmail());
                userDTO.setGender(user.getGender());
                userDTO.setPhone(user.getPhone());
                schedulingBookingDTO.setUser(userDTO);
            }
        } catch (Exception e) {
            System.out.println("User not found for ID: " + schedulingBooking.getUser().getId());
            schedulingBookingDTO.setUser(null);
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

    List<SchedulingBooking> bookings = scheduleBookingRepository.findByCarRentalUserId(userId);

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


