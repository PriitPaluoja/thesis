package ee.nipt.util;

import ee.nipt.domain.*;
import ee.nipt.dto.*;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ResultsUtilTest {

    @Test
    public void createReportDto() throws Exception {
        String sampleNumber = "TLDER";
        Examination examination = createExamination(sampleNumber, 1, 1);

        ReportDTO dto = ResultsUtil.createReportDto(examination, 1, 1);
        // Assert examination part
        Assert.assertEquals(examination.getPatientId(), dto.getPatientId());
        Assert.assertEquals(examination.getSampleNumber(), dto.getSampleNumber());
        Assert.assertEquals(examination.getPhysicianId(), dto.getPhysicianId());
        Assert.assertEquals(examination.getPregnancyLength(), dto.getPregnancyLength());
        Assert.assertEquals(examination.getHasOneFetus(), dto.getHasOneFetus());
        Assert.assertEquals(examination.getBmi(), dto.getBmi());
        Assert.assertEquals(examination.getHasTumor(), dto.getHasTumor());
        Assert.assertEquals(examination.getHasIvf(), dto.getHasIvf());
        Assert.assertEquals(examination.getHasHsct(), dto.getHasHsct());
        Assert.assertEquals(examination.getCreator(), dto.getCreator());
        Assert.assertEquals(examination.getNationality(), dto.getNationality());
        Assert.assertEquals(examination.getEmail(), dto.getEmail());
        Assert.assertEquals(examination.getExpertId(), dto.getExpertId());
        Assert.assertEquals(examination.getPatientDateOfBirth(), dto.getPatientDateOfBirth());
        Assert.assertEquals(examination.getDateOfExamination(), dto.getDateOfExamination());
        Assert.assertEquals(examination.getNumberOfFetuses(), dto.getNumberOfFetuses());
        Assert.assertEquals(examination.getDateOfSampling(), dto.getDateOfSampling());
        Assert.assertEquals(examination.getSampleArrivalDate(), dto.getSampleArrivalDate());
        Assert.assertEquals(examination.getWasPrognosisTrue(), dto.getWasPrognosisTrue());
        Assert.assertEquals(examination.getExpertAssessment(), dto.getExpertAssessment());
        Assert.assertEquals(examination.getDecision(), dto.getDecision());
        // Assert data
        DbData data = examination.getDatas().iterator().next();
        DataDTO dataDTO = dto.getData();
        Assert.assertEquals(data.getDataId(), dataDTO.getDataId());
        Assert.assertEquals(data.getRawData(), dataDTO.getRawData());
        Assert.assertEquals(data.getBarcode(), dataDTO.getBarcode());
        Assert.assertEquals(data.getSnp(), dataDTO.getSnp());
        Assert.assertEquals(data.getInformativeSnp(), dataDTO.getInformativeSnp());
        Assert.assertEquals(data.getRawReads(), dataDTO.getRawReads());
        Assert.assertEquals(data.getPcrRedundancy(), dataDTO.getPcrRedundancy());
        Assert.assertEquals(data.getUniqueReadsPerSnp(), dataDTO.getUniqueReadsPerSNP());
        Assert.assertEquals(data.getLot(), dataDTO.getLot());
        Assert.assertEquals(data.getDnaSeparation(), dataDTO.getDnaSeparation());
        // Assert Run
        Run run = data.getRuns().iterator().next();
        RunDTO runDTO = dto.getRun();
        Assert.assertEquals(run.getRunId(), runDTO.getRunId());
        Assert.assertEquals(run.getTestType(), runDTO.getTestType());
        Assert.assertEquals(run.getQScore(), runDTO.getQScore());
        Assert.assertEquals(run.getGender(), runDTO.getGender());
        Assert.assertEquals(run.getFetalFraction(), runDTO.getFetalFraction());
        // Assert Chromosomes
        List<Chromosome> chromosomes = run.getChromosomes();
        List<ChromosomeDTO> chromosomeDTOS = runDTO.getChromosomes();

        for (int i = 0; i < chromosomes.size(); i++) {
            Chromosome chromosome = chromosomes.get(i);
            ChromosomeDTO chromosomeDTO = chromosomeDTOS.get(i);
            Assert.assertEquals(chromosome.getChNumber(), chromosomeDTO.getChromosomeNumber());
            Assert.assertEquals(chromosome.getScore_1(), chromosomeDTO.getScore1());
            Assert.assertEquals(chromosome.getScore_2(), chromosomeDTO.getScore2());
            Assert.assertEquals(chromosome.getScore_3(), chromosomeDTO.getScore3());
            Assert.assertEquals(chromosome.getScore_4(), chromosomeDTO.getScore4());
            Assert.assertEquals(chromosome.getPlot(), chromosomeDTO.getPlot());
        }
    }


    @Test
    public void checkIdCreation() throws Exception {
        final String sampleNumber1 = "a";
        final String sampleNumber2 = "b";
        final String sampleNumber3 = "c";

        String patientId = "11111111111";


        List<Examination> examinations = Arrays.asList(
                createExamination(sampleNumber1, 1, 1),
                createExamination(sampleNumber2, 2, 2),
                createExamination(sampleNumber3, 3, 3)
        );

        ChoiceDTO dto = ResultsUtil.createChoiceDTO(patientId, examinations);

        Assert.assertTrue(dto.getPatientId().equals(patientId));

        for (String id : dto.getIds()) {
            dto.setSelectedId(id);
            dto = ResultsUtil.populateChoiceDtoIdFields(dto);

            int runId = dto.getRunId();
            int dataId = dto.getDataId();

            switch (dto.getExaminationId()) {
                case sampleNumber1:
                    Assert.assertTrue(dataId == 1);
                    Assert.assertTrue(runId == 1);
                    break;
                case sampleNumber2:
                    Assert.assertTrue(dataId == 2);
                    Assert.assertTrue(runId == 2);
                    break;
                case sampleNumber3:
                    Assert.assertTrue(dataId == 3);
                    Assert.assertTrue(runId == 3);
                    break;
                default:
                    throw new RuntimeException();
            }
        }

        dto.setSelectedId("a");
        dto = ResultsUtil.populateChoiceDtoIdFields(dto);
        Assert.assertNull(dto);
    }

    @Test
    public void appendAssessment() throws Exception {
        Examination examination = new Examination();

        String assessment = "Healthy";
        String id = "11111111111";
        Person expert = new Person();
        expert.setIdentificationCode(id);
        examination = ResultsUtil.appendAssessment(examination, assessment, true, expert);

        Assert.assertTrue(examination.getDecision() == AssessmentDecision.NORMAL);
        Assert.assertTrue(examination.getExpertId().equals(id));
        Assert.assertTrue(examination.getExpertAssessment().equals(assessment));

        examination = ResultsUtil.appendAssessment(examination, assessment, false, expert);

        Assert.assertTrue(examination.getDecision() == AssessmentDecision.ABNORMAL);
    }


    @Test
    public void reportIdFieldsToAssessmentDTO() throws Exception {
        ReportDTO report = new ReportDTO();
        report.setSampleNumber("ASDJAIJEF");

        DataDTO dataDTO = new DataDTO();
        dataDTO.setDataId(1);
        report.setData(dataDTO);

        RunDTO runDTO = new RunDTO();
        runDTO.setRunId(2);
        report.setRun(runDTO);

        AssessmentDTO dto = ResultsUtil.createAssessmentWithIdFieldsFromReport(report);

        Assert.assertEquals(report.getSampleNumber(), dto.getSampleNumber());
        Assert.assertEquals(report.getData().getDataId(), dto.getDataId());
        Assert.assertEquals(report.getRun().getRunId(), dto.getRunId());
    }


    private Examination createExamination(String sampleNumber, int dataId, int runId) throws IllegalAccessException {
        Examination examination = new Examination(
                TestUtil.nextString(),
                sampleNumber,
                TestUtil.nextString(),
                TestUtil.nextString(),
                RandomUtils.nextInt(),

                RandomUtils.nextBoolean(),
                RandomUtils.nextDouble(),
                RandomUtils.nextBoolean(),
                RandomUtils.nextBoolean(),
                RandomUtils.nextBoolean(),
                "EE",
                TestUtil.nextString() + "@gmail.com",
                TestUtil.nextString(),
                TestUtil.nextDate(),
                TestUtil.nextDate(),
                RandomUtils.nextInt(),
                TestUtil.nextDate(),
                TestUtil.nextDate(),
                RandomUtils.nextBoolean(),
                TestUtil.nextString(),
                AssessmentDecision.NORMAL,
                new HashSet<>()
        );

        DbData data = new DbData(
                dataId,
                TestUtil.nextString(),
                TestUtil.nextString(),
                RandomUtils.nextInt(),
                RandomUtils.nextInt(),
                RandomUtils.nextDouble(),
                RandomUtils.nextInt(),
                RandomUtils.nextDouble(),
                TestUtil.nextString(),
                TestUtil.nextDate(),
                new HashSet<>()
        );

        Run run = new Run(
                runId,
                "test1",
                TestUtil.nextDate(),
                RandomUtils.nextDouble(),
                "M",
                RandomUtils.nextDouble(),
                new ArrayList<>()
        );

        for (int i = 0; i < 10; i++) {
            run.getChromosomes().add(
                    new Chromosome(RandomUtils.nextInt(),
                            i,
                            RandomUtils.nextDouble(),
                            RandomUtils.nextDouble(),
                            RandomUtils.nextDouble(),
                            RandomUtils.nextDouble(),
                            TestUtil.nextString()
                    )
            );
        }

        data.getRuns().add(run);
        examination.getDatas().add(data);
        return examination;
    }
}