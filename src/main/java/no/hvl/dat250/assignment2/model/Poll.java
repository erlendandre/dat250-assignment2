package no.hvl.dat250.assignment2.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Poll {
    private Long id;
    private String username;

    @NotBlank(message = "Poll question cannot be empty")
    private String question;
    private Instant publishedAt;
    private Instant lastUpdatedAt;
    private Instant validUntil;
    private boolean isPublic;
    private Set<Long> invitedUserIds = new HashSet<>();
    private List<Vote> votes = new ArrayList<>();
    
    @Size(min = 2, message = "Poll must have at least 2 options")
    private List<VoteOption> options = new ArrayList<>();

    public Poll() {}

    public Poll(Long id, String username, String question, Instant publishedAt, Instant lastUpdatedAt, Instant validUntil, boolean isPublic) {
        this.id = id;
        this.username = username;
        this.question = question;
        this.publishedAt = publishedAt;
        this.lastUpdatedAt = lastUpdatedAt;
        this.validUntil = validUntil;
        this.isPublic = isPublic;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getQuestion() {return question;}
    public void setQuestion(String question) {this.question = question;}

    public Instant getPublishedAt() {return publishedAt;}
    public void setPublishedAt(Instant publishedAt) {this.publishedAt = publishedAt;}

    public Instant getLastUpdatedAt() {return lastUpdatedAt;}
    public void setLastUpdatedAt(Instant lastUpdatedAt) {this.lastUpdatedAt = lastUpdatedAt;}

    public Instant getValidUntil() {return validUntil;}
    public void setValidUntil(Instant validUntil) {this.validUntil = validUntil;}

    public boolean isPublic() {return isPublic;}
    public void setPublic(boolean isPublic) {this.isPublic = isPublic;}

    public Set<Long> getInvitedUserIds() {return invitedUserIds;}
    public void setInvitedUserIds(Set<Long> invitedUserIds) {this.invitedUserIds = invitedUserIds;}

    public List<Vote> getVotes() {return votes;}
    public void setVotes(List<Vote> votes) {this.votes = votes;}

    public List<VoteOption> getOptions() {return options;}
    public void setOptions(List<VoteOption> options) {this.options = options;}
}
