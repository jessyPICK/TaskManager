import java.util.Date;
import java.util.List;


public class Task {
    private int id; // Ajout de l'attribut ID
    private String taskName;
    private String description;
    private boolean isCompleted;
    private Date timeToDo;
    private String taskCategory;
    private int difficulty;
    private List<SubTask> subTasks;
    

    public Task(int id, String taskName,  String description, boolean isCompleted, Date timeToDo, String taskCategory, int difficulty, List<SubTask> subTasks) {
        this.id = id;
        this.taskName = taskName;
        this.isCompleted = isCompleted;
        this.timeToDo = timeToDo;
        this.taskCategory = taskCategory;
        this.description = description;
        this.difficulty = difficulty;
        this.subTasks = subTasks;
    }
    

    // Ajoutez les getters et setters pour l'ID
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return taskName;
    } 
    public void setName(String taskName) {
        this.taskName = taskName;
    }
    public String getDescript() {
        return description;
    } 
    public void setDescript(String description) {
        this.description = description;
    }
    public Date getDate() {
        return timeToDo;
    } 
    public void setDate(Date timeToDo) {
        this.timeToDo = timeToDo;
    }
    public String getCategory() {
        return taskCategory;
    }
    public void setCategory(String taskCategory) {
        this.taskCategory = taskCategory;
    }
    public int getDificult() {
        return difficulty;
    }
    public void setDificult(int difficulty) {
        this.difficulty = difficulty;
    }
    public boolean isCompleted() {
        return isCompleted;
    }
    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
    public List<SubTask> getSubTasks() {
        return subTasks;
    }
    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }
}
class SubTask {
    private int id;
    private String taskName;
    private String description;
    private boolean isCompleted;
    private Date timeToDo;
    private int difficulty;

    public SubTask(int id, String taskName, String description, boolean isCompleted, Date timeToDo, int difficulty) {
        this.id = id;
        this.taskName = taskName;
        this.description = description;
        this.isCompleted = isCompleted;
        this.timeToDo = timeToDo;
        this.difficulty = difficulty;
    }

    // Getters and setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public boolean isCompleted() {
        return isCompleted;
    }
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
    public Date getTimeToDo() {
        return timeToDo;
    }
    public void setTimeToDo(Date timeToDo) {
        this.timeToDo = timeToDo;
    }
    public int getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}