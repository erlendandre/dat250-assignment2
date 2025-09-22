package no.hvl.dat250.assignment2.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "votes")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    @JsonIgnore
    private Poll poll;

    @ManyToOne
    private VoteOption votesOn;

    private Instant publishedAt;
    private Instant lastUpdatedAt;

    public Vote() {}

    public Vote(User user, Poll poll, VoteOption votesOn, Instant publishedAt, Instant lastUpdatedAt) {
        this.user = user;
        this.poll = poll;
        this.votesOn = votesOn;
        this.publishedAt = publishedAt;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}

    public Poll getPoll() {return poll;}
    public void setPoll(Poll poll) {this.poll = poll;}

    public VoteOption getVotesOn() {return votesOn;}
    public void setVotesOn(VoteOption votesOn) {this.votesOn = votesOn;}

    public Instant getPublishedAt() {return publishedAt;}
    public void setPublishedAt(Instant publishedAt) {this.publishedAt = publishedAt;}

    public Instant getLastUpdatedAt() {return lastUpdatedAt;}
    public void setLastUpdatedAt(Instant lastUpdatedAt) {this.lastUpdatedAt = lastUpdatedAt;}
}
