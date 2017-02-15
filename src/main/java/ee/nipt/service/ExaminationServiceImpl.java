package ee.nipt.service;


import ee.nipt.db.ExaminationRepository;
import ee.nipt.db.PersonRepository;
import ee.nipt.domain.Examination;
import ee.nipt.dto.ExaminationDTO;
import ee.nipt.exception.DuplicateSampleNumberException;
import ee.nipt.exception.EmailNotPresentExcpetion;
import ee.nipt.util.ExaminationUtils;
import ee.nipt.util.IdUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ExaminationServiceImpl implements ExaminationService {
    private final ExaminationRepository examinationRepository;
    private final PersonRepository personRepository;


    @Override
    public void saveExamination(ExaminationDTO dto, Principal inserter) throws EmailNotPresentExcpetion, DuplicateSampleNumberException {
        Examination examination = ExaminationUtils.convert(dto);
        examination.setDateOfExamination(ExaminationUtils.getCurrentDateFromNetwork().toLocalDate());
        examination.setPatientDateOfBirth(IdUtils.calculateDateOfBirth(dto.getPatientId()));

        // Check that the person's, who later should receive report, e-mail address is present in the database.
        if (!personRepository.findOneByEmail(examination.getEmail()).isPresent()) {
            log.error("Report receiver e-mail {} not found. Inserter: {}", examination.getEmail(), inserter.getName());
            throw new EmailNotPresentExcpetion();
        }

        // Forbid duplicate sample numbers
        if (examinationRepository.exists(dto.getSampleNumber())) {
            log.warn("Sample number {} not unique! Inserter: {}", examination.getSampleNumber(), inserter.getName());
            throw new DuplicateSampleNumberException();
        }

        // Set the physician ID, who later receives notification, with the examination entity
        examination.setPhysicianId(personRepository.findOneByEmail(examination.getEmail())
                .get()
                .getIdentificationCode()
        );

        // Save the exmination creator id with examination entity
        examination.setCreator(
                personRepository.findOneByEmail(inserter.getName()).get().getIdentificationCode()
        );

        // Save
        examinationRepository.save(examination);
        log.info("{} saved examination with sample number {}", inserter.getName(), examination.getSampleNumber());
    }
}
