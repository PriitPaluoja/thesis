package ee.nipt.util;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

public class IdUtilsTest {
    private static final String ESTONIAN_ID_FORMAT = "dd-MM-yyyy";
    private static final String NON_ESTONIAN_ID_FORMAT = "ddMMyyyy";


    @Test
    public void isValidEstonianID() throws Exception {
        //Checked against: http://no.nonsense.ee/isikukood.php?d=01&m=10&c=19&y=78&g=1
        Assert.assertTrue(IdUtils.isValid("37810010858"));
        Assert.assertTrue(IdUtils.isValid("37810013958"));
        Assert.assertTrue(IdUtils.isValid("37810019937"));
        Assert.assertTrue(IdUtils.isValid("37810010008"));
        Assert.assertTrue(IdUtils.isValid("47810010456"));
        Assert.assertTrue(IdUtils.isValid("47810010467"));
        Assert.assertTrue(IdUtils.isValid("47810019996"));
        Assert.assertTrue(IdUtils.isValid("60210010317"));
        Assert.assertTrue(IdUtils.isValid("60210019922"));
        Assert.assertTrue(IdUtils.isValid("60210016169"));
        Assert.assertTrue(IdUtils.isValid("50210010197"));
        Assert.assertTrue(IdUtils.isValid("50210019235"));
        Assert.assertTrue(IdUtils.isValid("62710010184"));
        Assert.assertTrue(IdUtils.isValid("62710019996"));
        Assert.assertTrue(IdUtils.isValid("42701319991"));


        Assert.assertFalse(IdUtils.isValid("37810010859"));
        Assert.assertFalse(IdUtils.isValid("37810013959"));
        Assert.assertFalse(IdUtils.isValid("37810019936"));
        Assert.assertFalse(IdUtils.isValid("37810010007"));
        Assert.assertFalse(IdUtils.isValid("4781001045"));
        Assert.assertFalse(IdUtils.isValid("07810010467"));
        Assert.assertFalse(IdUtils.isValid("6d710019996"));
        Assert.assertFalse(IdUtils.isValid("42y701319991"));
    }

    @Test
    public void isValidNonEstonianID() throws Exception {
        Assert.assertTrue(IdUtils.isValid("37810010858A02091995"));
        Assert.assertTrue(IdUtils.isValid("37810013958A12122016"));
        Assert.assertTrue(IdUtils.isValid("37810019937A14042020"));
        Assert.assertTrue(IdUtils.isValid("37810010008A01012016"));
        Assert.assertTrue(IdUtils.isValid("47810010456A06071991"));
        Assert.assertFalse(IdUtils.isValid("47810010456A99071991"));
        Assert.assertFalse(IdUtils.isValid("4781001045A06990000"));
        Assert.assertFalse(IdUtils.isValid("47810010456A06321991"));
        Assert.assertFalse(IdUtils.isValid("4781001045606321991"));
        Assert.assertFalse(IdUtils.isValid("A4781001045606321991"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ESTONIAN_ID_FORMAT);
        LocalDate start = LocalDate.parse("01-01-2016", formatter);
        LocalDate end = LocalDate.parse("31-12-2016", formatter);


        Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                .map(d -> "470456A" + d.format(DateTimeFormatter.ofPattern(NON_ESTONIAN_ID_FORMAT)))
                .forEach(d -> Assert.assertTrue(IdUtils.isValid(d)));
    }


    @Test
    public void areDatesValidNonEstonianID() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ESTONIAN_ID_FORMAT);
        LocalDate start = LocalDate.parse("01-01-2016", formatter);
        LocalDate end = LocalDate.parse("31-12-2016", formatter);

        DateTimeFormatter df = DateTimeFormatter.ofPattern(NON_ESTONIAN_ID_FORMAT);

        Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                .map(d -> d.format(DateTimeFormatter.ofPattern(NON_ESTONIAN_ID_FORMAT)))
                .forEach(d -> Assert.assertEquals(df.format(IdUtils.calculateDateOfBirth("470456A" + d)), d));

        Assert.assertNull(IdUtils.calculateDateOfBirth("213Afw"));
    }

    @Test
    public void areDatesValidEstonianID() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ESTONIAN_ID_FORMAT);
        Assert.assertEquals(LocalDate.parse("19-01-1927", formatter), IdUtils.calculateDateOfBirth("42701191840"));
        Assert.assertEquals(LocalDate.parse("31-01-1927", formatter), IdUtils.calculateDateOfBirth("42701310451"));
        Assert.assertEquals(LocalDate.parse("19-01-2027", formatter), IdUtils.calculateDateOfBirth("62701191287"));
        Assert.assertNull(IdUtils.calculateDateOfBirth("fw"));
    }
}
