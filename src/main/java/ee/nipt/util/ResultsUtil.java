package ee.nipt.util;

import ee.nipt.domain.*;
import ee.nipt.dto.*;

import java.util.List;
import java.util.stream.Collectors;

public class ResultsUtil {
    private static final String SPLITTER = "::";

    /**
     * @param examination whose fields will be used to create {@link ReportDTO}
     * @param dataId      will determine which {@link DataDTO} will be inserted into the {@link ReportDTO}
     * @param runId       will determine which {@link RunDTO} will be inserted into the {@link ReportDTO}
     * @return {@link ReportDTO}
     */
    public static ReportDTO createReportDto(Examination examination, Integer dataId, Integer runId) {
        DbData data = getDataById(examination, dataId);
        Run run = getRunById(data, runId);
        return convertToReportDTO(examination, data, run);
    }

    /**
     * Create object {@link ChoiceDTO} with given parameters.
     *
     * @param patientId    as national identification code. Does not have to be valid
     * @param examinations list
     * @return new instance of {@link ChoiceDTO} were examinations have been converted to special ID's
     */
    public static ChoiceDTO createChoiceDTO(String patientId, List<Examination> examinations) {
        return new ChoiceDTO(patientId, examinationsToIds(examinations));
    }

    /**
     * Append assessment information to {@link Examination}
     *
     * @param examination to be updated
     * @param assessment  as freely formatted text describing decision
     * @param isNormal    is fetus normal or not (if true then 0 else 1 will be appended)
     * @param expert      who is the author of the assessment
     * @return inserted parameter {@link Examination}
     */
    public static Examination appendAssessment(Examination examination, String assessment, boolean isNormal, Person expert) {
        examination.setExpertAssessment(assessment);
        examination.setDecision(isNormal ? AssessmentDecision.NORMAL : AssessmentDecision.ABNORMAL);
        examination.setExpertId(expert.getIdentificationCode());
        return examination;
    }

    /**
     * Calculate {@link Examination} Sample number, {@link DbData} ID and {@link Run} ID by selected ID
     *
     * @param choiceDTO whose parameters will be calculated
     * @return {@link ChoiceDTO} if selected ID is present and correct else null
     */
    public static ChoiceDTO populateChoiceDtoIdFields(ChoiceDTO choiceDTO) {
        if (choiceDTO.getSelectedId() == null) return null;
        String[] rawId = choiceDTO.getSelectedId().split(SPLITTER);
        if (rawId.length != 3) return null;
        choiceDTO.setExaminationId(rawId[0]);
        choiceDTO.setDataId(Integer.valueOf(rawId[1]));
        choiceDTO.setRunId(Integer.valueOf(rawId[2]));
        return choiceDTO;
    }


    /**
     * Create new instance of {@link AssessmentDTO} with ID fields taken from {@link ReportDTO}
     */
    public static AssessmentDTO createAssessmentWithIdFieldsFromReport(ReportDTO report) {
        AssessmentDTO assessmentDTO = new AssessmentDTO();
        assessmentDTO.setSampleNumber(String.valueOf(report.getSampleNumber()));
        assessmentDTO.setRunId(report.getRun().getRunId());
        assessmentDTO.setDataId(report.getData().getDataId());
        return assessmentDTO;
    }


    private static List<String> examinationsToIds(List<Examination> examinations) {
        return examinations.stream()
                .flatMap(e -> e.getDatas().stream().flatMap(d -> d.getRuns().stream().map(r -> createId(e, d, r))))
                .collect(Collectors.toList());
    }

    private static String createId(Examination examination, DbData data, Run run) {
        String sampleNumber = examination.getSampleNumber();
        String dataId = String.valueOf(data.getDataId());
        String runId = String.valueOf(run.getRunId());
        return sampleNumber + SPLITTER + dataId + SPLITTER + runId;
    }

    private static ReportDTO convertToReportDTO(Examination examination, DbData data, Run run) {
        DataDTO dataDTO = new DataDTO(
                data.getDataId(),
                data.getRawData(),
                data.getBarcode(),
                data.getSnp(),
                data.getInformativeSnp(),
                data.getRawReads(),
                data.getPcrRedundancy(),
                data.getUniqueReadsPerSnp(),
                data.getLot(),
                data.getDnaSeparation()
        );

        RunDTO runDTO = new RunDTO(
                run.getRunId(),
                run.getTestType(),
                run.getDateOfRun(),
                run.getQScore(),
                run.getGender(),
                run.getFetalFraction(),
                run.getChromosomes().stream()
                        .map(c ->
                                new ChromosomeDTO(
                                        c.getChNumber(),
                                        c.getScore_1(),
                                        c.getScore_2(),
                                        c.getScore_3(),
                                        c.getScore_4(),
                                        c.getPlot())).collect(
                        Collectors.toList()
                ));


        return new ReportDTO(
                examination.getPatientId(),
                examination.getSampleNumber(),
                examination.getCreator(),
                examination.getPhysicianId(),
                examination.getPregnancyLength(),
                examination.getHasOneFetus(),
                examination.getBmi(),
                examination.getHasTumor(),
                examination.getHasIvf(),
                examination.getHasHsct(),
                examination.getNationality(),
                examination.getEmail(),
                examination.getExpertId(),
                examination.getPatientDateOfBirth(),
                examination.getDateOfExamination(),
                examination.getNumberOfFetuses(),
                examination.getDateOfSampling(),
                examination.getSampleArrivalDate(),
                examination.getWasPrognosisTrue(),
                examination.getExpertAssessment(),
                examination.getDecision(),
                dataDTO,
                runDTO
        );

    }

    private static DbData getDataById(Examination examination, Integer dataId) {
        List<DbData> datas = examination.getDatas().stream()
                .filter(data -> data.getDataId().equals(dataId))
                .collect(Collectors.toList());
        if (datas.size() > 1)
            throw new RuntimeException();
        else return datas.isEmpty() ? new DbData() : datas.get(0);
    }

    private static Run getRunById(DbData data, Integer runId) {
        List<Run> runs = data.getRuns().stream().filter(run -> run.getRunId().equals(runId)).collect(Collectors.toList());
        if (runs.size() > 1)
            throw new RuntimeException();
        else return runs.isEmpty() ? new Run() : runs.get(0);
    }
}