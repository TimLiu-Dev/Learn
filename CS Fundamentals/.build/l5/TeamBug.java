import java.util.ArrayList;
import java.util.List;

public class TeamBug {
    static class Team {
        private final List<String> players;
        Team(List<String> players) { this.players = players; }   // stores the caller's list as-is
        List<String> players() { return players; }                 // hands out the real list
    }

    public static void main(String[] args) {
        List<String> names = new ArrayList<>(List.of("Ada", "Linus"));
        Team team = new Team(names);
        names.add("Mallory");
        System.out.println(team.players());
        team.players().clear();
        System.out.println(names);
    }
}
