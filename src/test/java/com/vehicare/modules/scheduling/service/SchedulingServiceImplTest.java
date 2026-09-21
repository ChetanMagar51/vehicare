package com.vehicare.modules.scheduling.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.vehicare.modules.scheduling.entity.ServiceAdvisorAvailability;
import com.vehicare.modules.scheduling.entity.ServiceCenterWorkingHours;
import com.vehicare.modules.scheduling.entity.ServiceSlot;
import com.vehicare.modules.scheduling.entity.ServiceSlotStatus;
import com.vehicare.modules.scheduling.exception.InvalidSchedulingConfigurationException;
import com.vehicare.modules.scheduling.exception.InvalidSlotException;
import com.vehicare.modules.scheduling.exception.ServiceAdvisorNotFoundException;
import com.vehicare.modules.scheduling.exception.WorkingHoursNotConfiguredException;
import com.vehicare.modules.scheduling.repository.ServiceAdvisorAvailabilityRepository;
import com.vehicare.modules.scheduling.repository.ServiceCenterWorkingHoursRepository;
import com.vehicare.modules.scheduling.repository.ServiceSlotRepository;
import com.vehicare.modules.user.dto.UserDto;
import com.vehicare.modules.user.entity.Role;
import com.vehicare.modules.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class SchedulingServiceImplTest {

	@Mock
	private ServiceSlotRepository serviceSlotRepository;

	@Mock
	private ServiceCenterWorkingHoursRepository workingHoursRepository;

	@Mock
	private ServiceAdvisorAvailabilityRepository advisorAvailabilityRepository;

	@Mock
	private UserService userService;

	@InjectMocks
	private SchedulingServiceImpl schedulingService;

	private static final Long ADVISOR_ID = 101L;

	private static final LocalDate START_DATE = LocalDate.now().plusMonths(1).withDayOfMonth(1);

	private static final LocalDate END_DATE = START_DATE.withDayOfMonth(START_DATE.lengthOfMonth());

	private static final YearMonth MONTH_ = YearMonth.now().plusMonths(1);

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(schedulingService, "slotDurationMinutes", 30);
	}

	// ============================================================
	// Helper methods
	// ============================================================

	private UserDto serviceAdvisor() {

		return UserDto.builder().id(ADVISOR_ID).role(Role.Service_Adviser).build();
	}

	private ServiceCenterWorkingHours workingHours(DayOfWeek day, LocalTime opening, LocalTime closing) {

		return ServiceCenterWorkingHours.builder().dayOfWeek(day).openingTime(opening).closingTime(closing)
				.breakStart(null).breakEnd(null).closed(false).build();
	}

	private ServiceAdvisorAvailability availability(DayOfWeek day, LocalTime from, LocalTime to) {

		return ServiceAdvisorAvailability.builder().serviceAdvisorId(ADVISOR_ID).dayOfWeek(day).availableFrom(from)
				.availableTo(to).available(true).build();
	}

	private List<ServiceCenterWorkingHours> completeWorkingHours() {

		return new ArrayList<>(List.of(workingHours(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(18, 0)),

				workingHours(DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(18, 0)),

				workingHours(DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(18, 0)),

				workingHours(DayOfWeek.THURSDAY, LocalTime.of(9, 0), LocalTime.of(18, 0)),

				workingHours(DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(18, 0)),

				workingHours(DayOfWeek.SATURDAY, LocalTime.of(9, 0), LocalTime.of(18, 0)),

				workingHours(DayOfWeek.SUNDAY, LocalTime.of(9, 0), LocalTime.of(18, 0))));
	}

	private void mockValidAdvisor() {

		when(userService.getUserById(ADVISOR_ID)).thenReturn(serviceAdvisor());
	}

	private void mockCompleteWorkingHours() {

		when(workingHoursRepository.findAll()).thenReturn(completeWorkingHours());
	}

	private void mockNoExistingSlots() {

		when(serviceSlotRepository.findByServiceAdvisorIdAndServiceDateBetween(eq(ADVISOR_ID), any(LocalDate.class),
				any(LocalDate.class))).thenReturn(List.of());
	}

	// ============================================================
	// generateSlotsForAdvisor()
	// ============================================================

	@Test
	void generateSlotsForAdvisor_shouldRejectNullStartDate() {

		assertThrows(InvalidSchedulingConfigurationException.class,
				() -> schedulingService.generateSlotsForAdvisor(ADVISOR_ID, null, END_DATE));
	}

	@Test
	void generateSlotsForAdvisor_shouldRejectNullEndDate() {

		assertThrows(InvalidSchedulingConfigurationException.class,
				() -> schedulingService.generateSlotsForAdvisor(ADVISOR_ID, START_DATE, null));
	}

	@Test
	void generateSlotsForAdvisor_shouldRejectInvalidDateRange() {

		LocalDate startDate = LocalDate.of(2026, 10, 10);
		LocalDate endDate = LocalDate.of(2026, 10, 1);

		assertThrows(InvalidSchedulingConfigurationException.class,
				() -> schedulingService.generateSlotsForAdvisor(ADVISOR_ID, startDate, endDate));
	}

	@Test
	void generateSlotsForAdvisor_shouldGenerateSlotsSuccessfully() {

		LocalDate startDate = LocalDate.of(2026, 9, 1);
		LocalDate endDate = LocalDate.of(2026, 9, 30);

		mockValidAdvisor();
		mockCompleteWorkingHours();

		when(advisorAvailabilityRepository.findByServiceAdvisorId(ADVISOR_ID))
				.thenReturn(List.of(availability(DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))));

		mockNoExistingSlots();

		schedulingService.generateSlotsForAdvisor(ADVISOR_ID, startDate, endDate);

		ArgumentCaptor<List<ServiceSlot>> captor = ArgumentCaptor.forClass(List.class);

		verify(serviceSlotRepository).saveAll(captor.capture());

		List<ServiceSlot> slots = captor.getValue();

		assertEquals(10, slots.size());

		assertTrue(slots.stream().allMatch(slot -> slot.getServiceAdvisorId().equals(ADVISOR_ID)));

		assertTrue(slots.stream().allMatch(slot -> slot.getStatus() == ServiceSlotStatus.AVAILABLE));
	}

	@Test
	void generateSlotsForAdvisor_shouldRejectNullAdvisorId() {

		assertThrows(InvalidSchedulingConfigurationException.class,
				() -> schedulingService.generateSlotsForAdvisor(null, START_DATE, END_DATE));
	}

	@Test
	void generateSlotsForAdvisor_shouldRejectInvalidSlotDuration() {

		ReflectionTestUtils.setField(schedulingService, "slotDurationMinutes", 0);

		assertThrows(InvalidSchedulingConfigurationException.class,
				() -> schedulingService.generateSlotsForAdvisor(ADVISOR_ID, START_DATE, END_DATE));
	}

	@Test
	void generateSlotsForAdvisor_shouldRejectNonServiceAdvisor() {

		when(userService.getUserById(ADVISOR_ID)).thenReturn(UserDto.builder().id(ADVISOR_ID).role(Role.Owner).build());

		assertThrows(ServiceAdvisorNotFoundException.class,
				() -> schedulingService.generateSlotsForAdvisor(ADVISOR_ID, START_DATE, END_DATE));
	}

	@Test
	void generateSlotsForAdvisor_shouldRejectMissingWorkingHours() {

		mockValidAdvisor();

		List<ServiceCenterWorkingHours> hours = completeWorkingHours();

		hours.removeIf(h -> h.getDayOfWeek() == DayOfWeek.MONDAY);

		when(workingHoursRepository.findAll()).thenReturn(hours);

		assertThrows(WorkingHoursNotConfiguredException.class,
				() -> schedulingService.generateSlotsForAdvisor(ADVISOR_ID, START_DATE, END_DATE));
	}

	@Test
	void generateSlotsForAdvisor_shouldSkipClosedCenterDay() {

		mockValidAdvisor();

		List<ServiceCenterWorkingHours> hours = completeWorkingHours();

		hours.set(0, ServiceCenterWorkingHours.builder().dayOfWeek(DayOfWeek.MONDAY).openingTime(LocalTime.of(9, 0))
				.closingTime(LocalTime.of(18, 0)).breakStart(null).breakEnd(null).closed(true).build());

		when(workingHoursRepository.findAll()).thenReturn(hours);

		when(advisorAvailabilityRepository.findByServiceAdvisorId(ADVISOR_ID))
				.thenReturn(List.of(availability(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))));

		mockNoExistingSlots();

		schedulingService.generateSlotsForAdvisor(ADVISOR_ID, START_DATE, END_DATE);

		verify(serviceSlotRepository, never()).saveAll(anyList());
	}

	@Test
	void generateSlotsForAdvisor_shouldRejectInvalidWorkingHours() {

		mockValidAdvisor();

		List<ServiceCenterWorkingHours> hours = completeWorkingHours();

		hours.set(0, ServiceCenterWorkingHours.builder().dayOfWeek(DayOfWeek.MONDAY).openingTime(LocalTime.of(18, 0))
				.closingTime(LocalTime.of(9, 0)).breakStart(null).breakEnd(null).closed(false).build());

		when(workingHoursRepository.findAll()).thenReturn(hours);

		assertThrows(InvalidSlotException.class,
				() -> schedulingService.generateSlotsForAdvisor(ADVISOR_ID, START_DATE, END_DATE));
	}

	@Test
	void generateSlotsForAdvisor_shouldRejectOverlappingAdvisorAvailability() {

		mockValidAdvisor();
		mockCompleteWorkingHours();

		when(advisorAvailabilityRepository.findByServiceAdvisorId(ADVISOR_ID))
				.thenReturn(List.of(availability(DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(11, 0)),
						availability(DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(12, 0))));

		assertThrows(InvalidSlotException.class,
				() -> schedulingService.generateSlotsForAdvisor(ADVISOR_ID, START_DATE, END_DATE));
	}

	@Test
	void generateSlotsForAdvisor_shouldIgnoreUnavailableAdvisorPeriod() {

		mockValidAdvisor();
		mockCompleteWorkingHours();

		ServiceAdvisorAvailability unavailable = availability(DayOfWeek.TUESDAY, LocalTime.of(9, 0),
				LocalTime.of(10, 0));

		unavailable.setAvailable(false);

		when(advisorAvailabilityRepository.findByServiceAdvisorId(ADVISOR_ID)).thenReturn(List.of(unavailable));

		mockNoExistingSlots();

		schedulingService.generateSlotsForAdvisor(ADVISOR_ID, START_DATE, END_DATE);

		verify(serviceSlotRepository, never()).saveAll(anyList());
	}

	@Test
	void generateSlotsForAdvisor_shouldNotCreateDuplicateSlots() {

		LocalDate startDate = LocalDate.of(2026, 9, 1);
		LocalDate endDate = LocalDate.of(2026, 9, 30);

		mockValidAdvisor();
		mockCompleteWorkingHours();

		when(advisorAvailabilityRepository.findByServiceAdvisorId(ADVISOR_ID))
				.thenReturn(List.of(availability(DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))));

		ServiceSlot existingSlot = ServiceSlot.builder().serviceAdvisorId(ADVISOR_ID)
				.serviceDate(LocalDate.of(2026, 9, 1)).startTime(LocalTime.of(9, 0)).endTime(LocalTime.of(9, 30))
				.status(ServiceSlotStatus.AVAILABLE).build();

		when(serviceSlotRepository.findByServiceAdvisorIdAndServiceDateBetween(eq(ADVISOR_ID), eq(startDate),
				eq(endDate))).thenReturn(List.of(existingSlot));

		schedulingService.generateSlotsForAdvisor(ADVISOR_ID, startDate, endDate);

		ArgumentCaptor<List<ServiceSlot>> captor = ArgumentCaptor.forClass(List.class);

		verify(serviceSlotRepository).saveAll(captor.capture());

		List<ServiceSlot> newSlots = captor.getValue();

		assertEquals(9, newSlots.size());

		boolean duplicateExists = newSlots.stream()
				.anyMatch(slot -> slot.getServiceDate().equals(LocalDate.of(2026, 9, 1))
						&& slot.getStartTime().equals(LocalTime.of(9, 0))
						&& slot.getEndTime().equals(LocalTime.of(9, 30)));

		assertTrue(!duplicateExists);
	}

	@Test
	void generateSlotsForAdvisor_shouldSkipBreakTime() {

		LocalDate startDate = LocalDate.of(2026, 9, 1);
		LocalDate endDate = LocalDate.of(2026, 9, 30);

		mockValidAdvisor();

		List<ServiceCenterWorkingHours> hours = completeWorkingHours();

		hours.set(0,
				ServiceCenterWorkingHours.builder().dayOfWeek(DayOfWeek.MONDAY).openingTime(LocalTime.of(9, 0))
						.closingTime(LocalTime.of(18, 0)).breakStart(LocalTime.of(9, 30)).breakEnd(LocalTime.of(10, 0))
						.closed(false).build());

		when(workingHoursRepository.findAll()).thenReturn(hours);

		when(advisorAvailabilityRepository.findByServiceAdvisorId(ADVISOR_ID))
				.thenReturn(List.of(availability(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(11, 0))));

		mockNoExistingSlots();

		schedulingService.generateSlotsForAdvisor(ADVISOR_ID, startDate, endDate);

		ArgumentCaptor<List<ServiceSlot>> captor = ArgumentCaptor.forClass(List.class);

		verify(serviceSlotRepository).saveAll(captor.capture());

		List<ServiceSlot> slots = captor.getValue();

		// September 2026 has 4 Mondays.
		//
		// Per Monday:
		// 09:00-09:30
		// 09:30-10:00 -> break, skipped
		// 10:00-10:30
		// 10:30-11:00
		//
		// 3 slots × 4 Mondays = 12.
		assertEquals(12, slots.size());

		boolean breakSlotExists = slots.stream().anyMatch(slot -> slot.getStartTime().equals(LocalTime.of(9, 30))
				&& slot.getEndTime().equals(LocalTime.of(10, 0)));

		assertTrue(!breakSlotExists);
	}

	@Test
	void generateSlotsForAdvisor_shouldNotGenerateIncompleteSlot() {

		LocalDate startDate = LocalDate.of(2026, 9, 1);
		LocalDate endDate = LocalDate.of(2026, 9, 30);

		mockValidAdvisor();
		mockCompleteWorkingHours();

		when(advisorAvailabilityRepository.findByServiceAdvisorId(ADVISOR_ID))
				.thenReturn(List.of(availability(DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(9, 40))));

		mockNoExistingSlots();

		schedulingService.generateSlotsForAdvisor(ADVISOR_ID, startDate, endDate);

		ArgumentCaptor<List<ServiceSlot>> captor = ArgumentCaptor.forClass(List.class);

		verify(serviceSlotRepository).saveAll(captor.capture());

		List<ServiceSlot> slots = captor.getValue();

		// September 2026 has 5 Tuesdays.
		//
		// Per Tuesday:
		// 09:00-09:30 -> complete
		// 09:30-10:00 -> would exceed advisor availability (09:40)
		//
		// Therefore: 1 slot × 5 Tuesdays = 5.
		assertEquals(5, slots.size());

		assertTrue(slots.stream().allMatch(slot -> slot.getStartTime().equals(LocalTime.of(9, 0))
				&& slot.getEndTime().equals(LocalTime.of(9, 30))));
	}

	@Test
	void generateMonthlySlots_shouldContinueWhenOneAdvisorFails() {

		UserDto advisor1 = UserDto.builder().id(101L).role(Role.Service_Adviser).build();

		UserDto advisor2 = UserDto.builder().id(102L).role(Role.Service_Adviser).build();

		when(userService.getUsersByRole(Role.Service_Adviser)).thenReturn(List.of(advisor1, advisor2));

		when(userService.getUserById(101L)).thenReturn(advisor1);

		when(userService.getUserById(102L)).thenReturn(advisor2);

		// Advisor 101 has no availability configured.
		when(advisorAvailabilityRepository.findByServiceAdvisorId(101L)).thenReturn(List.of());

		// Advisor 102 has valid availability.
		when(advisorAvailabilityRepository.findByServiceAdvisorId(102L)).thenReturn(
				List.of(ServiceAdvisorAvailability.builder().serviceAdvisorId(102L).dayOfWeek(DayOfWeek.TUESDAY)
						.availableFrom(LocalTime.of(9, 0)).availableTo(LocalTime.of(10, 0)).available(true).build()));

		when(workingHoursRepository.findAll()).thenReturn(completeWorkingHours());

		when(serviceSlotRepository.findByServiceAdvisorIdAndServiceDateBetween(eq(102L), any(LocalDate.class),
				any(LocalDate.class))).thenReturn(List.of());

		schedulingService.generateMonthlySlots(MONTH_);

		// Both advisors were attempted.
		verify(userService).getUserById(101L);

		verify(userService).getUserById(102L);

		// Advisor 102 continued and generated slots.
		verify(serviceSlotRepository).saveAll(anyList());
	}

	@Test
	void generateSlotsForAdvisor_shouldNotGenerateWhenThereIsNoCommonTime() {

		mockValidAdvisor();
		mockCompleteWorkingHours();

		when(advisorAvailabilityRepository.findByServiceAdvisorId(ADVISOR_ID))
				.thenReturn(List.of(availability(DayOfWeek.TUESDAY, LocalTime.of(7, 0), LocalTime.of(8, 0))));

		mockNoExistingSlots();

		schedulingService.generateSlotsForAdvisor(ADVISOR_ID, START_DATE, END_DATE);

		verify(serviceSlotRepository, never()).saveAll(anyList());
	}

	@Test
	void generateSlotsForAdvisor_shouldRejectInvalidAdvisorAvailabilityTime() {

		mockValidAdvisor();
		mockCompleteWorkingHours();

		when(advisorAvailabilityRepository.findByServiceAdvisorId(ADVISOR_ID))
				.thenReturn(List.of(availability(DayOfWeek.TUESDAY, LocalTime.of(11, 0), LocalTime.of(9, 0))));

		assertThrows(InvalidSlotException.class,
				() -> schedulingService.generateSlotsForAdvisor(ADVISOR_ID, START_DATE, END_DATE));
	}

	@Test
	void generateSlotsForAdvisor_shouldRejectNullAdvisorAvailabilityTime() {

		mockValidAdvisor();
		mockCompleteWorkingHours();

		ServiceAdvisorAvailability invalid = ServiceAdvisorAvailability.builder().serviceAdvisorId(ADVISOR_ID)
				.dayOfWeek(DayOfWeek.TUESDAY).availableFrom(null).availableTo(LocalTime.of(10, 0)).available(true)
				.build();

		when(advisorAvailabilityRepository.findByServiceAdvisorId(ADVISOR_ID)).thenReturn(List.of(invalid));

		assertThrows(InvalidSlotException.class,
				() -> schedulingService.generateSlotsForAdvisor(ADVISOR_ID, START_DATE, END_DATE));
	}

	@Test
	void generateSlotsForAdvisor_shouldRejectInvalidBreakTime() {

		mockValidAdvisor();

		List<ServiceCenterWorkingHours> hours = completeWorkingHours();

		hours.set(0,
				ServiceCenterWorkingHours.builder().dayOfWeek(DayOfWeek.MONDAY).openingTime(LocalTime.of(9, 0))
						.closingTime(LocalTime.of(18, 0)).breakStart(LocalTime.of(11, 0)).breakEnd(LocalTime.of(10, 0))
						.closed(false).build());

		when(workingHoursRepository.findAll()).thenReturn(hours);

		assertThrows(InvalidSlotException.class,
				() -> schedulingService.generateSlotsForAdvisor(ADVISOR_ID, START_DATE, END_DATE));
	}

	@Test
	void generateSlotsForAdvisor_shouldRejectWhenOnlyOneBreakTimeIsProvided() {

		mockValidAdvisor();

		List<ServiceCenterWorkingHours> hours = completeWorkingHours();

		hours.set(0, ServiceCenterWorkingHours.builder().dayOfWeek(DayOfWeek.MONDAY).openingTime(LocalTime.of(9, 0))
				.closingTime(LocalTime.of(18, 0)).breakStart(LocalTime.of(12, 0)).breakEnd(null).closed(false).build());

		when(workingHoursRepository.findAll()).thenReturn(hours);

		assertThrows(InvalidSlotException.class,
				() -> schedulingService.generateSlotsForAdvisor(ADVISOR_ID, START_DATE, END_DATE));
	}

	@Test
	void generateSlotsForAdvisor_shouldRejectBreakOutsideWorkingHours() {

		mockValidAdvisor();

		List<ServiceCenterWorkingHours> workingHours = completeWorkingHours();

		workingHours.stream().filter(w -> w.getDayOfWeek() == DayOfWeek.MONDAY).findFirst().ifPresent(w -> {
			w.setBreakStart(LocalTime.of(8, 0));
			w.setBreakEnd(LocalTime.of(10, 0));
		});

		when(workingHoursRepository.findAll()).thenReturn(workingHours);

		when(advisorAvailabilityRepository.findByServiceAdvisorId(ADVISOR_ID))
				.thenReturn(List.of(availability(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(18, 0))));

		InvalidSlotException exception = assertThrows(InvalidSlotException.class,
				() -> schedulingService.generateSlotsForAdvisor(ADVISOR_ID, START_DATE, END_DATE));

		assertEquals("Break must be within center working hours", exception.getMessage());
	}

	@Test
	void generateSlotsForAdvisor_shouldNotSaveWhenAllSlotsAlreadyExist() {

		LocalDate startDate = LocalDate.of(2026, 9, 1);
		LocalDate endDate = LocalDate.of(2026, 9, 30);

		mockValidAdvisor();
		mockCompleteWorkingHours();

		when(advisorAvailabilityRepository.findByServiceAdvisorId(ADVISOR_ID))
				.thenReturn(List.of(availability(DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))));

		List<ServiceSlot> existingSlots = new ArrayList<>();

		List<LocalDate> tuesdays = List.of(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 8),
				LocalDate.of(2026, 9, 15), LocalDate.of(2026, 9, 22), LocalDate.of(2026, 9, 29));

		long id = 1L;

		for (LocalDate date : tuesdays) {

			existingSlots.add(ServiceSlot.builder().id(id++).serviceAdvisorId(ADVISOR_ID).serviceDate(date)
					.startTime(LocalTime.of(9, 0)).endTime(LocalTime.of(9, 30)).status(ServiceSlotStatus.AVAILABLE)
					.build());

			existingSlots.add(ServiceSlot.builder().id(id++).serviceAdvisorId(ADVISOR_ID).serviceDate(date)
					.startTime(LocalTime.of(9, 30)).endTime(LocalTime.of(10, 0)).status(ServiceSlotStatus.BOOKED)
					.build());
		}

		when(serviceSlotRepository.findByServiceAdvisorIdAndServiceDateBetween(eq(ADVISOR_ID), eq(startDate),
				eq(endDate))).thenReturn(existingSlots);

		schedulingService.generateSlotsForAdvisor(ADVISOR_ID, startDate, endDate);

		verify(serviceSlotRepository, never()).saveAll(anyList());
	}

	// ============================================================
	// getAvailableSlots()
	// ============================================================

	@Test
	void getAvailableSlots_shouldReturnAvailableSlots() {

		LocalDate date = LocalDate.of(2026, 9, 1);

		ServiceSlot slot1 = ServiceSlot.builder().id(1L).serviceAdvisorId(ADVISOR_ID).serviceDate(date)
				.startTime(LocalTime.of(9, 0)).endTime(LocalTime.of(9, 30)).status(ServiceSlotStatus.AVAILABLE).build();

		ServiceSlot slot2 = ServiceSlot.builder().id(2L).serviceAdvisorId(ADVISOR_ID).serviceDate(date)
				.startTime(LocalTime.of(9, 30)).endTime(LocalTime.of(10, 0)).status(ServiceSlotStatus.AVAILABLE)
				.build();

		when(serviceSlotRepository.findByServiceAdvisorIdAndServiceDateAndStatus(ADVISOR_ID, date,
				ServiceSlotStatus.AVAILABLE)).thenReturn(List.of(slot1, slot2));

		var result = schedulingService.getAvailableSlots(ADVISOR_ID, date);

		assertEquals(2, result.size());

		assertEquals(1L, result.get(0).getSlotId());

		assertEquals(LocalTime.of(9, 0), result.get(0).getStartTime());

		assertEquals(LocalTime.of(9, 30), result.get(0).getEndTime());
	}

	@Test
	void getAvailableSlots_shouldReturnEmptyListWhenNoSlotsAvailable() {

		LocalDate date = LocalDate.of(2026, 9, 1);

		when(serviceSlotRepository.findByServiceAdvisorIdAndServiceDateAndStatus(ADVISOR_ID, date,
				ServiceSlotStatus.AVAILABLE)).thenReturn(List.of());

		var result = schedulingService.getAvailableSlots(ADVISOR_ID, date);

		assertTrue(result.isEmpty());
	}

	// ============================================================
	// generateMonthlySlots()
	// ============================================================

	@Test
	void generateMonthlySlots_shouldGenerateSlotsForAllServiceAdvisors() {

		UserDto advisor1 = UserDto.builder().id(101L).role(Role.Service_Adviser).build();

		UserDto advisor2 = UserDto.builder().id(102L).role(Role.Service_Adviser).build();

		when(userService.getUsersByRole(Role.Service_Adviser)).thenReturn(List.of(advisor1, advisor2));

		when(userService.getUserById(101L)).thenReturn(advisor1);

		when(userService.getUserById(102L)).thenReturn(advisor2);

		when(workingHoursRepository.findAll()).thenReturn(completeWorkingHours());

		when(advisorAvailabilityRepository.findByServiceAdvisorId(101L)).thenReturn(
				List.of(ServiceAdvisorAvailability.builder().serviceAdvisorId(101L).dayOfWeek(DayOfWeek.TUESDAY)
						.availableFrom(LocalTime.of(9, 0)).availableTo(LocalTime.of(10, 0)).available(true).build()));

		when(advisorAvailabilityRepository.findByServiceAdvisorId(102L)).thenReturn(
				List.of(ServiceAdvisorAvailability.builder().serviceAdvisorId(102L).dayOfWeek(DayOfWeek.TUESDAY)
						.availableFrom(LocalTime.of(9, 0)).availableTo(LocalTime.of(10, 0)).available(true).build()));

		when(serviceSlotRepository.findByServiceAdvisorIdAndServiceDateBetween(anyLong(), any(LocalDate.class),
				any(LocalDate.class))).thenReturn(List.of());

		schedulingService.generateMonthlySlots(MONTH_);

		verify(userService).getUsersByRole(Role.Service_Adviser);

		verify(userService).getUserById(101L);

		verify(userService).getUserById(102L);

		verify(serviceSlotRepository, times(2)).saveAll(anyList());
	}

	@Test
	void generateMonthlySlots_shouldNotGenerateWhenNoServiceAdvisorsExist() {

		when(userService.getUsersByRole(Role.Service_Adviser)).thenReturn(List.of());

		schedulingService.generateMonthlySlots(MONTH_);

		verify(userService).getUsersByRole(Role.Service_Adviser);

		verify(workingHoursRepository, never()).findAll();

		verify(advisorAvailabilityRepository, never()).findByServiceAdvisorId(any(Long.class));

		verify(serviceSlotRepository, never()).saveAll(anyList());
	}

	@Test
	void generateSlotsForAdvisor_shouldThrowExceptionWhenAdvisorAvailabilityNotConfigured() {

		mockValidAdvisor();
		mockCompleteWorkingHours();

		when(advisorAvailabilityRepository.findByServiceAdvisorId(ADVISOR_ID)).thenReturn(List.of());

		 assertThrows(
				 InvalidSlotException.class,
				() -> schedulingService.generateSlotsForAdvisor(ADVISOR_ID, START_DATE, END_DATE));

		
		verify(advisorAvailabilityRepository).findByServiceAdvisorId(ADVISOR_ID);

		verify(serviceSlotRepository, never()).findByServiceAdvisorIdAndServiceDateBetween(anyLong(),
				any(LocalDate.class), any(LocalDate.class));

		verify(serviceSlotRepository, never()).saveAll(anyList());
	}
}