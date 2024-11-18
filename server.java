import io.javalin.Javalin;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

public class App {
    public static void main(String[] args) {
        //create a factory with 4 workers
        factory f = new factory(4);
        

        // Create and start the Javalin server
        Javalin app = Javalin.create(config -> {
            config.defaultContentType = "application/json"; 
        }).start(7000); 

        app.get("/get_futures", ctx -> {
            ctx.result("Hello, world!");
        });

        // Define a POST endpoint
        app.post("/add_job", ctx -> {
            String requestBody = ctx.body();

            //convert body to a map
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> requestBodyMap = objectMapper.readValue(requestBody, Map.class);

            List<Integer> items = (List<Integer>) requestBodyMap.get("data");

            //create a new task
            FutureTask<?> task = new FutureTask<>(() -> {bubbleSort(items); return null;});

            // Add the task to the factory
            f.addTask(task);

            ctx.result("You submitted: " + requestBody);
        });
    }

    public List<Integer> bubbleSort(List<Integer> items) {
        for (int i = 0; i < items.size(); i++) {
            for (int j = 0; j < items.size() - 1; j++) {
                if (items.get(j).toString().compareTo(items.get(j + 1).toString()) > 0) {
                    Integer temp = items.get(j);
                    items.set(j, items.get(j + 1));
                    items.set(j + 1, temp);
                }
            }
        }
        return items;
    }
}
