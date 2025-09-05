package no.hvl.dat250.assignment2.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Poll {
    private Long id;
    private String username;
    private String question;
    private Instant publishedAt;
    private Instant lastUpdatedAt;
    private Instant validUntil;
    private List<Vote> votes = new ArrayList<>();
    private List<VoteOption> options = new ArrayList<>();

    public Poll() {}

    public Poll(Long id, String username, String question, Instant publishedAt, Instant lastUpdatedAt, Instant validUntil) {
        this.id = id;
        this.username = username;
        this.question = question;
        this.publishedAt = publishedAt;
        this.lastUpdatedAt = lastUpdatedAt;
        this.validUntil = validUntil;
        
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

    public List<Vote> getVotes() {return votes;}
    public void setVotes(List<Vote> votes) {this.votes = votes;}

    public List<VoteOption> getOptions() {return options;}
    public void setOptions(List<VoteOption> options) {this.options = options;}
}
