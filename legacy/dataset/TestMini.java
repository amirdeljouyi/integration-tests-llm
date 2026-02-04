import io.quarkus.qute.deployment.QuteProcessor;
import org.junit.Test;

public class TestMini {

    @Test
    public void smokeTestQuteProcessor() {
        // This MUST touch some real code in QuteProcessor
        QuteProcessor qp = new QuteProcessor();
        System.out.println("QuteProcessor instance: " + qp);

        // If there is a simple method you know is safe to call, even better:
        // qp.someSimplePublicMethod();
    }
}