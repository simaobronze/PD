package ServidorPrincipal;

import java.util.List;

public class Group {
    private String name;
    private String creator;
    private List<String> members;

    public Group(String name, String creator, List<String> members) {
        this.name = name;
        this.creator = creator;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void getCreator(String creator) {
        this.creator = creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public void addMember(String member) {
        this.members.add(member);
    }

    public void removeMember(String member) {
        this.members.remove(member);
    }
}
