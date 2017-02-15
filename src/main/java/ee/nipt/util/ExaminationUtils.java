package ee.nipt.util;

import ee.nipt.domain.Examination;
import ee.nipt.dto.ExaminationDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class ExaminationUtils {

    /**
     * Convert {@link ExaminationDTO} to {@link Examination}
     *
     * @param dto to be converted
     * @return new {@link Examination}
     */
    public static Examination convert(ExaminationDTO dto) {
        Examination examination = new Examination();

        examination.setPatientId(dto.getPatientId());
        examination.setSampleNumber(dto.getSampleNumber());
        examination.setPhysicianId(dto.getPhysicianId());
        examination.setPregnancyLength(dto.getPregnancyLength());
        examination.setHasOneFetus(dto.getHasOneFetus());
        examination.setBmi(dto.getBmi());
        examination.setHasTumor(dto.getHasTumor());
        examination.setHasIvf(dto.getHasIvf());
        examination.setHasHsct(dto.getHasHsct());
        examination.setNationality(dto.getNationality());
        examination.setEmail(dto.getEmail());
        examination.setExpertId(dto.getExpertId());
        examination.setPatientDateOfBirth(dto.getPatientDateOfBirth());
        examination.setDateOfExamination(dto.getDateOfExamination());
        examination.setNumberOfFetuses(dto.getNumberOfFetuses());
        examination.setDateOfSampling(dto.getDateOfSampling());
        examination.setSampleArrivalDate(dto.getSampleArrivalDate());
        examination.setWasPrognosisTrue(dto.getWasPrognosisTrue());
        examination.setExpertAssessment(dto.getExpertAssessment());
        examination.setDecision(dto.getDecision());

        if (dto.getHasOneFetus() != null && dto.getHasOneFetus())
            examination.setNumberOfFetuses(1);

        return examination;
    }


    /**
     * Get current time from pool.ntp.org. If lookup fails, returns local system time.
     *
     * @return {@link LocalDate} representing current time
     */
    public static LocalDateTime getCurrentDateFromNetwork() {
        String TIME_SERVER = "pool.ntp.org";

        try {
            NTPUDPClient timeClient = new NTPUDPClient();
            InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
            TimeInfo timeInfo = timeClient.getTime(inetAddress);

            return Instant.ofEpochMilli(timeInfo.getReturnTime()).atZone(ZoneId.of("Europe/Helsinki")).toLocalDateTime();
        } catch (IOException e) {
            log.error("Failed to query time info from pool.ntp.org: {}", e.toString());
            return Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.of("Europe/Helsinki")).toLocalDateTime();
        }
    }

    /**
     * @return map consisting of pairs such as Estonia=EE
     */
    public static Map<String, String> getCountryMap() {
        Map<String, String> map = new TreeMap<>();
        String[] countryCodes = Locale.getISOCountries();
        for (String cc : countryCodes) {
            // country name , country code map
            map.put(new Locale("", cc).getDisplayCountry(), cc.toUpperCase());
        }
        map.remove("");
        return map;
    }


}
