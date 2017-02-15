package ee.nipt.util;

import ee.nipt.feedback.Feedback;
import ee.nipt.feedback.FeedbackType;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;

public class ControllerUtilTest {

    @Test
    public void feedbackTest() throws Exception {
        Model model = new ExtendedModelMap();

        for (int i = 0; i < 25; i++)
            ControllerUtil.setFeedback(model, i % 2 == 0 ? FeedbackType.ERROR : FeedbackType.SUCCESS, String.valueOf(i));

        List<Feedback> feedbackList = (List<Feedback>) model.asMap().get(Feedback.FEEDBACK_VARIABLE_NAME);

        Assert.assertEquals(25, feedbackList.size());

        for (int i = 0; i < 25; i++) {
            Feedback feedback = feedbackList.get(i);
            Assert.assertEquals(feedback.getCssClass(), i % 2 == 0 ? "alert-danger" : "alert-success");
            Assert.assertEquals(feedback.getMessage(), String.valueOf(i));
        }

        Assert.assertEquals(new Feedback("s", FeedbackType.INFO).getCssClass(), "alert-info");
        Assert.assertEquals(new Feedback("s", FeedbackType.WARNING).getCssClass(), "alert-warning");
    }
}