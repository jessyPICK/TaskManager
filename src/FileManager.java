import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.*;

public class FileManager {
    private String fileName;

    public FileManager(String fileName) {
        this.fileName = fileName;
    }

    public List<Task> readTasksFromFile() {
        List<Task> tasks = new ArrayList<>();
        try {
            String jsonContent = readFileContent();
            JSONArray jsonArray = new JSONArray(jsonContent);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.getInt("id");
                boolean isCompleted = jsonObject.getString("status").equalsIgnoreCase("T");
                String taskName = jsonObject.getString("titre");
                String taskCategory = jsonObject.optString("category", ""); // Utilisation de optString pour gérer les clés manquantes
                String description = jsonObject.optString("description", ""); // Utilisation de optString pour gérer les clés manquantes
                int difficulty = jsonObject.optInt("difficulty", 0); // Utilisation de optInt pour gérer les clés manquantes
                String dateString = jsonObject.getString("date");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = dateFormat.parse(dateString);
                List<SubTask> subTasks = new ArrayList<>();
                if (jsonObject.has("subTasks")) { // Vérifier si la clé "subTasks" existe
                    JSONArray subTasksArray = jsonObject.getJSONArray("subTasks");
                    for (int j = 0; j < subTasksArray.length(); j++) {
                        JSONObject subTaskObject = subTasksArray.getJSONObject(j);
                        int subTaskId = subTaskObject.getInt("id");
                        String subTaskName = subTaskObject.getString("titre"); // Correction de la clé "taskName" à "titre"
                        String subTaskDescription = subTaskObject.optString("description", ""); // Utilisation de optString pour gérer les clés manquantes
                        boolean subTaskCompleted = subTaskObject.getString("status").equalsIgnoreCase("T");
                        String subTaskDateString = subTaskObject.getString("date");
                        Date subTaskDate = dateFormat.parse(subTaskDateString);
                        int subTaskDifficulty = subTaskObject.optInt("difficulty", 0); // Utilisation de optInt pour gérer les clés manquantes
                        SubTask subTask = new SubTask(subTaskId, subTaskName, subTaskDescription, subTaskCompleted, subTaskDate, subTaskDifficulty);
                        subTasks.add(subTask);
                    }
                }
                Task task = new Task(id, taskName, description, isCompleted, date, taskCategory, difficulty, subTasks);
                tasks.add(task);
            }
        } catch (IOException | JSONException | ParseException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public void writeTasksToFile(List<Task> tasks) {
        JSONArray jsonArray = new JSONArray();
        for (Task task : tasks) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", task.getId());
                jsonObject.put("status", task.isCompleted() ? "T" : "F");
                jsonObject.put("category", task.getCategory());
                jsonObject.put("titre", task.getName());
                jsonObject.put("description", task.getDescript()); 
                jsonObject.put("difficulty", task.getDificult()); 

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = dateFormat.format(task.getDate());
                jsonObject.put("date", dateString);

                JSONArray subTasksArray = new JSONArray();
                for (SubTask subTask : task.getSubTasks()) {
                    JSONObject subTaskObject = new JSONObject();
                    subTaskObject.put("id", subTask.getId());
                    subTaskObject.put("status", subTask.isCompleted() ? "T" : "F");
                    subTaskObject.put("titre", subTask.getTaskName()); // Correction de la clé "taskName" à "titre"
                    subTaskObject.put("description", subTask.getDescription());
                    String subTaskDateString = dateFormat.format(subTask.getTimeToDo());
                    subTaskObject.put("date", subTaskDateString);
                    subTaskObject.put("difficulty", subTask.getDifficulty());
                    subTasksArray.put(subTaskObject);
                }
                jsonObject.put("subTasks", subTasksArray);

                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            writeFileContent(jsonArray.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFileContent() throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        }
        return content.toString();
    }

    private void writeFileContent(String content) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            bw.write(content);
        }
    }
}