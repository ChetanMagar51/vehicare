package com.vehicare.modules.appointment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.vehicare.modules.appointment.entity.Appointment;
import com.vehicare.modules.appointment.entity.AppointmentStatus;

@DataJpaTest
class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @BeforeEach
    void setUp() {
    	 appointmentRepository.deleteAll();

    	    Appointment appointment1 = new Appointment();
    	    appointment1.setServiceAdvisorId(101L);
    	    appointment1.setOwnerId(201L);
    	    appointment1.setVehicleId(301L);
    	    appointment1.setServiceDate(LocalDate.of(2026, 9, 21));
    	    appointment1.setServiceTime(LocalTime.of(10, 0));
    	    appointment1.setStatus(AppointmentStatus.SCHEDULED);

    	    Appointment appointment2 = new Appointment();
    	    appointment2.setServiceAdvisorId(101L);
    	    appointment2.setOwnerId(202L);
    	    appointment2.setVehicleId(302L);
    	    appointment2.setServiceDate(LocalDate.of(2026, 9, 22));
    	    appointment2.setServiceTime(LocalTime.of(11, 0));
    	    appointment2.setStatus(AppointmentStatus.SCHEDULED);

    	    Appointment appointment3 = new Appointment();
    	    appointment3.setServiceAdvisorId(102L);
    	    appointment3.setOwnerId(201L);
    	    appointment3.setVehicleId(303L);
    	    appointment3.setServiceDate(LocalDate.of(2026, 9, 23));
    	    appointment3.setServiceTime(LocalTime.of(12, 0));
    	    appointment3.setStatus(AppointmentStatus.SCHEDULED);

    	    appointmentRepository.saveAll(
    	            List.of(appointment1, appointment2, appointment3)
    	    );
    }

    // ---------------------------------------------------
    // findByServiceAdvisorId()
    // ---------------------------------------------------

    @Test
    void findByServiceAdvisorId_shouldReturnAppointments() {

        List<Appointment> appointments =
                appointmentRepository.findByServiceAdvisorId(101L);

        assertThat(appointments).hasSize(2);

        assertThat(appointments)
                .allMatch(appointment ->
                        appointment.getServiceAdvisorId().equals(101L));
    }

    @Test
    void findByServiceAdvisorId_shouldReturnEmptyList() {

        List<Appointment> appointments =
                appointmentRepository.findByServiceAdvisorId(999L);

        assertThat(appointments).isEmpty();
    }

    // ---------------------------------------------------
    // findByIdAndServiceAdvisorId()
    // ---------------------------------------------------

    @Test
    void findByIdAndServiceAdvisorId_shouldReturnAppointment() {

        Appointment savedAppointment =
                appointmentRepository.findByServiceAdvisorId(101L)
                        .get(0);

        Optional<Appointment> result =
                appointmentRepository.findByIdAndServiceAdvisorId(
                        savedAppointment.getId(),
                        101L
                );

        assertThat(result).isPresent();
        assertThat(result.get().getId())
                .isEqualTo(savedAppointment.getId());
    }

    @Test
    void findByIdAndServiceAdvisorId_shouldReturnEmptyForWrongAdvisor() {

        Appointment savedAppointment =
                appointmentRepository.findByServiceAdvisorId(101L)
                        .get(0);

        Optional<Appointment> result =
                appointmentRepository.findByIdAndServiceAdvisorId(
                        savedAppointment.getId(),
                        999L
                );

        assertThat(result).isEmpty();
    }

    // ---------------------------------------------------
    // findByOwnerId()
    // ---------------------------------------------------

    @Test
    void findByOwnerId_shouldReturnAppointments() {

        List<Appointment> appointments =
                appointmentRepository.findByOwnerId(201L);

        assertThat(appointments).hasSize(2);

        assertThat(appointments)
                .allMatch(appointment ->
                        appointment.getOwnerId().equals(201L));
    }

    @Test
    void findByOwnerId_shouldReturnEmptyList() {

        List<Appointment> appointments =
                appointmentRepository.findByOwnerId(999L);

        assertThat(appointments).isEmpty();
    }

    // ---------------------------------------------------
    // findByVehicleId()
    // ---------------------------------------------------

    @Test
    void findByVehicleId_shouldReturnAppointments() {

        List<Appointment> appointments =
                appointmentRepository.findByVehicleId(301L);

        assertThat(appointments).hasSize(1);

        assertThat(appointments.get(0).getVehicleId())
                .isEqualTo(301L);
    }

    @Test
    void findByVehicleId_shouldReturnEmptyList() {

        List<Appointment> appointments =
                appointmentRepository.findByVehicleId(999L);

        assertThat(appointments).isEmpty();
    }
}