public class Person {
    private String name;
    private boolean active;
    private String nickname;              // stays null until someone sets it

    public Person(String name) { this.name = name; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getNickname() { return nickname; }   // may return null!

    public static Person anonymous() { return new Person("anonymous"); }

    public boolean is(String other) { return name.equals(other); }   // "is" is a Kotlin keyword
}
