import java.util.ArrayList;
import java.util.List;

public class TeamFixed {
    static class Team {
        private final List<String> players;
        Team(List<String> players) { this.players = List.copyOf(players); }  // copy on the way in
        List<String> players() { return players; }    // already unmodifiable: safe to hand out
    }

    public static void main(String[] args) {
        List<String> names = new ArrayList<>(List.of("Ada", "Linus"));
        Team team = new Team(names);
        names.add("Mallory");
        System.out.println(team.players());
        team.players().add("Eve");                    // refused at runtime
    }
}
