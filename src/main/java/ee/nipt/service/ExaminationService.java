package ee.nipt.service;

import ee.nipt.dto.ExaminationDTO;
import ee.nipt.exception.DuplicateSampleNumberException;
import ee.nipt.exception.EmailNotPresentExcpetion;

import java.security.Principal;

public interface ExaminationService {
    /**
     * Save examination in to the database
     *
     * @param dto      to be saved
     * @param inserter is the creator of the dto
     */
    void saveExamination(ExaminationDTO dto, Principal inserter) throws EmailNotPresentExcpetion, DuplicateSampleNumberException;
}
