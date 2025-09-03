package no.hvl.dat250.assignment2.model;

import java.time.Instant;

public class Vote {
    private Long id;
    private Long userId;
    private Long pollId;
    private Long voteOptionId;
    private Instant publishedAt;
    private Instant lastUpdatedAt;

    public Vote() {}

    public Vote(Long id, Long userId, Long pollId, Long voteOptionId, Instant publishedAt, Instant lastUpdatedAt) {
        this.id = id;
        this.userId = userId;
        this.pollId = pollId;
        this.voteOptionId = voteOptionId;
        this.publishedAt = publishedAt;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public Long getUserId() {return userId;}
    public void setUserId(Long userId) {this.userId = userId;}

    public Long getPollId() {return pollId;}
    public void setPollId(Long pollId) {this.pollId = pollId;}

    public Long getVoteOptionId() {return voteOptionId;}
    public void setVoteOptionId(Long voteOptionId) {this.voteOptionId = voteOptionId;}

    public Instant getPublishedAt() {return publishedAt;}
    public void setPublishedAt(Instant publishedAt) {this.publishedAt = publishedAt;}

    public Instant getLastUpdatedAt() {return lastUpdatedAt;}
    public void setLastUpdatedAt(Instant lastUpdatedAt) {this.lastUpdatedAt = lastUpdatedAt;}
}
