package ee.nipt.db;

import ee.nipt.domain.Examination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExaminationRepository extends JpaRepository<Examination, String> {

    List<Examination> findAllByPatientId(String patientId);
}
