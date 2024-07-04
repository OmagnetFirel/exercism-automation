import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExercismDownloader {

    public static void main(String[] args) {
        String trackId = "java"; // Substitua pelo trackId desejado
        List<Exercise> completedExercises = getCompletedExercises(trackId);
        System.out.println("Downloading " + completedExercises.size() + " exercises...");
        for (Exercise exercise : completedExercises) {
            downloadExercise(exercise);
        }

        gitInitAndPush();
    }

    private static List<Exercise> getCompletedExercises(String trackId) {
        List<Exercise> exercises = new ArrayList<>();
        String command = String.format("exercism list --track-id=%s", trackId);

        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // Parse the output to get the exercise slugs
                if (line.startsWith("- ")) {
                    String exerciseSlug = line.substring(2);
                    exercises.add(new Exercise(trackId, exerciseSlug));
                }
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return exercises;
    }

    private static void downloadExercise(Exercise exercise) {
        String command = String.format("exercism download --exercise-slug=%s --track-id=%s",
                exercise.getExerciseSlug(), exercise.getTrackId());

        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void gitInitAndPush() {
        executeCommand("git add .");
        executeCommand("git commit -m \"Add all Exercism solutions\"");
        executeCommand("git push -u origin main");
    }

    private static void executeCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class Exercise {
        private final String trackId;
        private final String exerciseSlug;

        public Exercise(String trackId, String exerciseSlug) {
            this.trackId = trackId;
            this.exerciseSlug = exerciseSlug;
        }

        public String getTrackId() {
            return trackId;
        }

        public String getExerciseSlug() {
            return exerciseSlug;
        }
    }
}
