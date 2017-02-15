package ee.nipt.util;

import ee.nipt.domain.AssessmentDecision;
import ee.nipt.domain.Examination;
import ee.nipt.dto.ExaminationDTO;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;

public class ExaminationUtilsTest {


    @Test
    public void convertExaminationDTO() throws Exception {

        ExaminationDTO dto = new ExaminationDTO(
                TestUtil.nextString(),
                TestUtil.nextString(),
                TestUtil.nextString(),
                RandomUtils.nextInt(),
                false,
                RandomUtils.nextDouble(),
                RandomUtils.nextBoolean(),
                RandomUtils.nextBoolean(),
                RandomUtils.nextBoolean(),
                TestUtil.nextString(),
                TestUtil.nextString() + "@gmail.com",
                TestUtil.nextString(),
                TestUtil.nextDate(),
                TestUtil.nextDate(),
                RandomUtils.nextInt(),
                TestUtil.nextDate(),
                TestUtil.nextDate(),
                RandomUtils.nextBoolean(),
                TestUtil.nextString(),
                AssessmentDecision.NORMAL
        );

        Examination examination = ExaminationUtils.convert(dto);

        Assert.assertEquals(dto.getPatientId(), examination.getPatientId());
        Assert.assertEquals(dto.getSampleNumber(), examination.getSampleNumber());
        Assert.assertEquals(dto.getPhysicianId(), examination.getPhysicianId());
        Assert.assertEquals(dto.getPregnancyLength(), examination.getPregnancyLength());
        Assert.assertEquals(dto.getHasOneFetus(), examination.getHasOneFetus());
        Assert.assertEquals(dto.getBmi(), examination.getBmi());
        Assert.assertEquals(dto.getHasTumor(), examination.getHasTumor());
        Assert.assertEquals(dto.getHasIvf(), examination.getHasIvf());
        Assert.assertEquals(dto.getHasHsct(), examination.getHasHsct());
        Assert.assertEquals(dto.getNationality(), examination.getNationality());
        Assert.assertEquals(dto.getEmail(), examination.getEmail());
        Assert.assertEquals(dto.getExpertId(), examination.getExpertId());
        Assert.assertEquals(dto.getPatientDateOfBirth(), examination.getPatientDateOfBirth());
        Assert.assertEquals(dto.getDateOfExamination(), examination.getDateOfExamination());
        Assert.assertEquals(dto.getNumberOfFetuses(), examination.getNumberOfFetuses());
        Assert.assertEquals(dto.getDateOfSampling(), examination.getDateOfSampling());
        Assert.assertEquals(dto.getSampleArrivalDate(), examination.getSampleArrivalDate());
        Assert.assertEquals(dto.getWasPrognosisTrue(), examination.getWasPrognosisTrue());
        Assert.assertEquals(dto.getExpertAssessment(), examination.getExpertAssessment());
        Assert.assertEquals(dto.getDecision(), examination.getDecision());
    }

    @Test
    public void countryMapContainsEstonia() throws Exception {
        Assert.assertTrue(ExaminationUtils.getCountryMap().containsKey("Estonia"));
    }

}