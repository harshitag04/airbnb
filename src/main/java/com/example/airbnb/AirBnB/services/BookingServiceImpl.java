package com.example.airbnb.AirBnB.services;

import com.example.airbnb.AirBnB.DTOs.BookingDTO;
import com.example.airbnb.AirBnB.DTOs.BookingRequest;
import com.example.airbnb.AirBnB.DTOs.GuestDTO;
import com.example.airbnb.AirBnB.DTOs.HotelReportDTO;
import com.example.airbnb.AirBnB.entities.*;
import com.example.airbnb.AirBnB.enums.BookingStatus;
import com.example.airbnb.AirBnB.exceptions.ResourceNotFoundException;
import com.example.airbnb.AirBnB.exceptions.UnAuthorisedException;
import com.example.airbnb.AirBnB.repositories.*;
import com.example.airbnb.AirBnB.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.airbnb.utils.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService{

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final BookingRepository bookingsRepository;
    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;
    private final CheckOutService checkOutService;
    private final PricingService pricingService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    @Transactional
    public BookingDTO initialiseBooking(BookingRequest bookingRequest) {

        Hotels hotel = hotelRepository.findById(bookingRequest.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with Id {} " + bookingRequest.getHotelId()));

        Rooms rooms = roomRepository.findById(bookingRequest.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with Id {} " + bookingRequest.getRoomId()));

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableRepository(bookingRequest.getRoomId(),
                bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(),
                bookingRequest.getRoomsCount());

        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate()) + 1;

        if (inventoryList.size() != daysCount) {
            throw new IllegalStateException("Room is not available anymore");
        }

        // Reserve the room/ update the booked count of inventories
        inventoryRepository.initBooking(rooms.getId(), bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());

        BigDecimal priceForOneRoom = pricingService.calculateTotalPrice(inventoryList);
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));

        Bookings booking = Bookings.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotels(hotel)
                .rooms(rooms)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequest.getRoomsCount())
                .amount(totalPrice)
                .build();

        bookingsRepository.save(booking);
        return modelMapper.map(booking, BookingDTO.class);
    }

    @Override
    @Transactional
    public BookingDTO addGuest(Long Id, List<GuestDTO> guestDTOList) {

        Bookings bookings = bookingsRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with Id " + Id + " not found"));

        User user = getCurrentUser();
        if(!user.equals(bookings.getUser())){
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        }

        if(isBookingExpired(bookings)){
            throw new IllegalStateException("Booking with Id{} " + Id + "has already expired");
        }

        if(bookings.getBookingStatus() != BookingStatus.RESERVED){
            throw new IllegalStateException("Booking is not under reserved state, cannot add guests");
        }

        for(GuestDTO guestDTO1 : guestDTOList){
            Guest guest = modelMapper.map(guestDTO1, Guest.class);
            guest.setUser(getCurrentUser());
            guestRepository.save(guest);
            bookings.getGuest().add(guest);
        }

        bookings.setBookingStatus(BookingStatus.GUEST_ADDED);
        bookingsRepository.save(bookings);

        return modelMapper.map(bookings, BookingDTO.class);

    }

    @Override
    @Transactional
    public String initiatePayment(Long bookingId) {
        Bookings bookings = bookingsRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: "+bookingId));

        User user = getCurrentUser();

        if(!user.equals(bookings.getUser())){
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        }

        if(isBookingExpired(bookings)){
            throw new IllegalStateException("Booking has already expired");
        }

        String sessionUrl = checkOutService.getCheckOutSession(bookings,
                frontendUrl+"/payments" + bookingId + "/success",
                frontendUrl+"/payments" + bookingId + "/failure");

        bookings.setBookingStatus(BookingStatus.PAYMENTS_PENDING);
        bookingsRepository.save(bookings);

        return sessionUrl;
    }

    @Override
    @Transactional
    public void capturePayment(Event event) {
        if("checkout.session.completed".equals(event.getType())){
            //Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            Session session = retrieveSessionFromEvent(event);
            if(session == null || session.getId() == null)
                return;
            String sessionId = session.getId();
            Bookings bookings = bookingsRepository.findByPaymentSessionId(sessionId).orElseThrow(
                    ()-> new ResourceNotFoundException("Booking not found for session ID: "+sessionId));

            bookings.setBookingStatus(BookingStatus.CONFIRMED);
            bookingsRepository.save(bookings);

            inventoryRepository.findAndLockReservedInventory(bookings.getRooms().getId(),
                    bookings.getCheckInDate(), bookings.getCheckOutDate(), bookings.getRoomsCount());

            inventoryRepository.confirmBooking(bookings.getRooms().getId(),
                    bookings.getCheckInDate(), bookings.getCheckOutDate(), bookings.getRoomsCount());
            log.info("Successfully confirmed the booking for Booking ID: {}", bookings.getId());
        }else {
            log.warn("Unhandled event type: {}", event.getType());
        }
    }

    @Override
    @Transactional
    public void cancelPayment(Long bookingId) {

        Bookings bookings = bookingsRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: "+bookingId));

        User user = getCurrentUser();

        if(!user.equals(bookings.getUser())){
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        }

        if(bookings.getBookingStatus() != BookingStatus.CONFIRMED){
            throw new IllegalStateException("Only confirmed bookings can be cancelled");
        }
        bookings.setBookingStatus(BookingStatus.CANCELLED);
        bookingsRepository.save(bookings);

        inventoryRepository.findAndLockReservedInventory(bookings.getRooms().getId(),
                bookings.getCheckInDate(), bookings.getCheckOutDate(), bookings.getRoomsCount());

        inventoryRepository.cancelBooking(bookings.getRooms().getId(),
                bookings.getCheckInDate(), bookings.getCheckOutDate(), bookings.getRoomsCount());

        //handle refund

        try {
            Session session = Session.retrieve(bookings.getPaymentSessionId());
            RefundCreateParams refundCreateParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();
            Refund.create(refundCreateParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<BookingDTO> getAllBookingsByHotelId(Long hotelId) {

        Hotels hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with Id {} " + hotelId));

        User user = getCurrentUser();

        log.info("Getting all booking for the hotel with ID: {}", hotelId);

        if(!user.equals(hotel.getOwner())){
            throw new AccessDeniedException("You are not the owner of hotel with id: "+hotelId);
        }

        List<Bookings> bookings = bookingsRepository.findByHotels(hotel);

        return bookings.stream()
                .map((element) -> modelMapper.map(element, BookingDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public HotelReportDTO getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate) {

        Hotels hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with Id {} " + hotelId));

        User user = getCurrentUser();

        log.info("Generating report for hotel with ID: {}", hotelId);

        if(!user.equals(hotel.getOwner())){
            throw new AccessDeniedException("You are not the owner of hotel with id: "+hotelId);
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Bookings> bookings = bookingsRepository.findByHotelsAndCreatedAtBetween(hotel, startDateTime, endDateTime);

        long totalConfirmedBookings = bookings.stream()
                .filter((booking -> booking.getBookingStatus().equals(BookingStatus.CONFIRMED)))
                .count();

        BigDecimal totalRevenueOfConfirmedBookings = bookings.stream()
                .filter(booking -> booking.getBookingStatus().equals(BookingStatus.CONFIRMED))
                .map(Bookings::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgRevenue = totalConfirmedBookings == 0 ? BigDecimal.ZERO :
                totalRevenueOfConfirmedBookings.divide(BigDecimal.valueOf(totalConfirmedBookings), RoundingMode.HALF_UP);

        return new HotelReportDTO(totalConfirmedBookings, totalRevenueOfConfirmedBookings, avgRevenue);

    }

    @Override
    public List<BookingDTO> getMyBookings() {
        User user = getCurrentUser();

        return bookingsRepository.findByUser(user)
                .stream().
                map((element) -> modelMapper.map(element, BookingDTO.class))
                .collect(Collectors.toList());
    }

    private Session retrieveSessionFromEvent(Event event) {
        log.info("inside  retrieveSessionFromEvent");
        try {

            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
            if (deserializer.getObject().isPresent()) {
                return (Session) deserializer.getObject().get();
            } else {
                String rawJson = event.getData().getObject().toJson();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(rawJson);
                String sessionId = jsonNode.get("id").asText();

                return Session.retrieve(sessionId);
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to retrieve session data");
        }
    }


    public boolean isBookingExpired(Bookings bookings){
        return bookings.getCreatedAt().plusHours(1).isBefore(LocalDateTime.now());
    }

}
